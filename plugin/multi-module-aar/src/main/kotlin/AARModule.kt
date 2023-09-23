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
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.component.ProjectComponentSelector
import org.gradle.api.internal.component.DefaultSoftwareComponentContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.repositories
import java.io.File
import java.util.*

class AARModule {

    private var init = false

    private var aarSettings = AARSettings()

    private var aarMavenPath = ""
    private var aarVersion = "1.0"

    private lateinit var rootProject: Project

    fun apply(project: Project) {
        if (!init) {
            rootProject = project.rootProject

            aarSettings.loadProperties(project.gradle)

            if (aarSettings.aarEnableMaven || aarSettings.aarEnableMultiModule) {
                val path = project.rootDir.path
                aarMavenPath = "$path/localAARMavenRepository"
                project.configure(project.subprojects) {
                    if (buildFile.exists()) {
                        configureSubProject(this)
                    }
                }
            }
        }
    }

    private fun configureSubProject(subproject: Project) {
        if (aarSettings.aarEnableMaven) {
            configureMaven(subproject)
        }
        if (aarSettings.aarEnableMultiModule) {
            configureDependencies(subproject)
        }
    }

    private fun configureMaven(project: Project) {
        project.afterEvaluate {
            afterEvaluate {
                val c = project.components as DefaultSoftwareComponentContainer
                val entry = (c.asMap as TreeMap).ceilingEntry("debug")
                if (entry != null) {
                    val includeDependencies = !(entry.key.contains("aab") || entry.key.contains("apk"))
                    if (includeDependencies) {
                        project.pluginManager.apply(MavenPublishPlugin::class.java)
                        project.extensions.configure(PublishingExtension::class.java) {
                            let { publishing ->
                                publishing.repositories {
                                    let { repositories ->
                                        repositories.maven {
                                            url = File(aarMavenPath).toURI()
                                        }
                                    }

                                }
                                publishing.publications {
                                    let { publications ->
                                        publications.create("multiModuleAAR", MavenPublication::class.java) {
                                            groupId = project.group.toString()
                                            artifactId = project.name
                                            version = aarVersion
                                            from(entry.value)
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
                            groupStr = groupStr.replace("${rootProject.name}", "")
                            groupStr = groupStr.replace(".", ":")
                            mod = "${groupStr}:${mod}"

                            val targetProject = project.findProject(mod)
                            if (targetProject != null) {
                                // Convert pom dependency to project module
                                val useTargetProject = aarSettings.aarKeepModules.contains(mod)
                                if (useTargetProject) {
                                    if (aarSettings.aarDebugLog) {
                                        println("AARPlugin: ${project.path} - KeepModule: $mod - Config: ${config.name}")
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
                                val isDevAARModule = aarSettings.aarKeepModules.contains(module)
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
            groupName = rootProject.name
        } else {
            var groupStr = moduleStr.replace(":", ".")
            // Replace last
            val toReplace = "." + module
            val pos = groupStr.lastIndexOf(toReplace)
            if (pos > -1) {
                groupStr = groupStr.substring(0, pos) + groupStr.substring(pos + toReplace.length);
            }
            groupName = rootProject.name + "." + groupStr
        }
        return Pair(groupName, module)
    }

    class CustomDepsPluginConvention {
        // here you can return anything which configuration methods in the
        // `dependencies` block would accept, e.g. a string or a map
        fun customDependency(): Map<String, String> {
            return mapOf(
                "group" to "com.whatever",
                "name" to "whatever",
                "version" to "1.2.3"
            )
        }
    }
}