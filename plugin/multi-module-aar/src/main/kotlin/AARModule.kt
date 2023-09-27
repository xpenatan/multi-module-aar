/*******************************************************************************
 * Copyright 2023 Natan Barbosa - xpenatan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import org.gradle.api.Project
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.component.ProjectComponentSelector
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler
import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.api.internal.component.DefaultSoftwareComponentContainer
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.internal.metaobject.DynamicInvokeResult
import org.gradle.internal.metaobject.MethodAccess
import org.gradle.kotlin.dsl.repositories
import java.io.File
import java.util.*

class AARModule : AARDependencyHandler.InvokeMethod {

    private var aarSettings = AARSettings()

    private var aarMavenPath = ""
    private var aarVersion = "1.0"

    private lateinit var projectRoot: Project

    private var init = false
    private var projectCount = 0

    fun apply(settings: Settings) {
        val gradle = settings.gradle

        gradle.beforeProject {
            if (!init) {
                init = true
                aarSettings.loadProperties(gradle)
                projectRoot = rootProject
                val path = project.rootDir.path
                aarMavenPath = "$path/localAARMavenRepository"
            }
            if (aarSettings.aarEnableMaven) {
                configureMaven(this)
            }

            if (aarSettings.aarEnableMultiModule) {
                configureInvalidCaller(this)
            }
        }

        gradle.afterProject {
            if (configurations.size > 0) {
                if (aarSettings.aarEnableMultiModule) {
                    if (buildFile.exists()) {
                        projectCount++
                        configureDependencies(this)
                    }
                }
            }
        }

        gradle.projectsEvaluated {
            if (aarSettings.aarEnableMultiModule) {
                rootProject.logger.error("AARPlugin: Total Projects $projectCount")
            }
        }
    }

    private fun configureMaven(project: Project) {
        project.afterEvaluate {
            afterEvaluate {
                var haveEntry = false
                val artifactList = mutableListOf<PublishArtifact>()
                val c = project.components as DefaultSoftwareComponentContainer
                val entry = (c.asMap as TreeMap).ceilingEntry("debug")
                if (entry != null) {
                    haveEntry = !(entry.key.contains("aab") || entry.key.contains("apk"))
                } else {
                    if(aarSettings.arrTestMode) {
                        configurations.forEach {
                            val t = it as DefaultPublishArtifact
                            if(t.classifier == null) { t.classifier = t.name }
                            artifactList.addAll(it.artifacts)
                        }
                    }
                }
                val haveArtifact = (artifactList.size > 0)

                if (haveEntry || haveArtifact) {
                    project.pluginManager.apply(MavenPublishPlugin::class.java)
                    project.extensions.configure(PublishingExtension::class.java) {
                        let { publishing ->
                            publishing.repositories {
                                let { repositories ->
                                    repositories.mavenLocal()
                                    repositories.maven {
                                        url = File(aarMavenPath).toURI()
                                    }
                                }

                            }
                            publishing.publications {
                                if (aarSettings.aarEnableLog) {
                                    logger.error("AARPlugin: Adding maven to ${project.path}")
                                }
                                let { publications ->
                                    publications.create("LocalAAR", MavenPublication::class.java) {
                                        groupId = project.group.toString()
                                        artifactId = project.name
                                        version = aarVersion
                                        if (entry != null) {
                                            from(entry.value)
                                        } else {
                                            setArtifacts(artifactList)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun configureInvalidCaller(project: Project) {
        val dep = project.dependencies as DefaultDependencyHandler
        val newHandler = AARDependencyHandler(dep, this)
        val declaredField = DefaultProject::class.java.getDeclaredField("dependencyHandler")
        declaredField.isAccessible = true
        declaredField.set(project, newHandler)
    }

    override fun invoke(method: MethodAccess, name: String?, vararg arguments: Any?): DynamicInvokeResult {
        if (name == "project" && arguments.isNotEmpty()) {
            val any = arguments[0] as Array<*>
            val projectPath = any[0].toString()
            val findProject = projectRoot.findProject(projectPath)
            if (findProject == null) {
                val pair = getModuleMap(projectPath)
                val groupName = pair.first
                val moduleName = pair.second
                val versionName = aarVersion
                val arrName = "${groupName}:${moduleName}:${versionName}"
                any[0] = arrName as Nothing
                return method.tryInvokeMethod("implementation", any)
            }
        }
        return method.tryInvokeMethod(name, arguments)
    }

    private fun configureDependencies(project: Project) {
        project.repositories {
            let { repositories ->
                repositories.mavenLocal()
                repositories.maven {
                    url = File(aarMavenPath).toURI()
                }
            }
        }

        project.configurations.configureEach {
            let { config ->
                resolutionStrategy.dependencySubstitution.all {
                    let { dependency ->
                        val componentSelector = dependency.requested
                        if (componentSelector is ModuleComponentSelector) {
                            var mod = componentSelector.module
                            var groupStr = componentSelector.group
                            groupStr = groupStr.replace("${projectRoot.name}", "")
                            groupStr = groupStr.replace(".", ":")
                            mod = "${groupStr}:${mod}"
                            val targetProject = projectRoot.findProject(mod)
                            if (targetProject != null) {
                                // Convert pom dependency to project module
                                var useTargetProject = false
                                if(aarSettings.arrUseSettingsModules) {
                                    useTargetProject = projectRoot.findProject(mod) != null
                                }
                                else {
                                    useTargetProject = aarSettings.aarKeepModules.contains(mod)
                                }
                                if (useTargetProject) {
                                    if (aarSettings.aarEnableLog) {
                                        project.logger.error("AARPlugin: ${project.path} - KeepModule: $mod - Config: ${config.name}")
                                    }
                                    dependency.useTarget(targetProject)
                                } else {
                                    if (project.path == mod) {
                                        dependency.useTarget(targetProject)
                                    }
                                }
                            }
                        } else if (componentSelector is ProjectComponentSelector) {
                            val module = componentSelector.projectPath
                            if (project.path != module) {
                                var isDevAARModule = false
                                if(aarSettings.arrUseSettingsModules) {
                                    isDevAARModule = projectRoot.findProject(module) != null
                                }
                                else {
                                    isDevAARModule = aarSettings.aarKeepModules.contains(module)
                                }
                                if (!isDevAARModule) {
                                    val pair = getModuleMap(module)
                                    val groupName = pair.first
                                    val moduleName = pair.second
                                    val versionName = aarVersion
                                    val arrName = "${groupName}:${moduleName}:${versionName}"
                                    dependency.useTarget(arrName)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getModuleMap(moduleName: String): Pair<String, String> {
        var moduleStr = moduleName
        if (moduleStr.startsWith(":")) {
            // Remove the first double dots
            moduleStr = moduleStr.replaceFirst(":", "")
        }

        val moduleSplit = moduleStr.split(":")
        val module = moduleSplit[moduleSplit.size - 1]

        var groupName = ""
        if (moduleSplit.size == 1) {
            groupName = projectRoot.name
        } else {
            var groupStr = moduleStr.replace(":", ".")
            // Replace last
            val toReplace = "." + module
            val pos = groupStr.lastIndexOf(toReplace)
            if (pos > -1) {
                groupStr = groupStr.substring(0, pos) + groupStr.substring(pos + toReplace.length);
            }
            groupName = projectRoot.name + "." + groupStr
        }
        return Pair(groupName, module)
    }
}