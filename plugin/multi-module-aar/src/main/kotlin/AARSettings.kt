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

import org.gradle.api.invocation.Gradle
import org.gradle.initialization.DefaultSettings
import java.util.ArrayList

class AARSettings {
    companion object {
        const val ENABLE_DEBUG_LOG = "aarEnableLog"
        const val ENABLE_MAVEN = "aarEnableMaven"
        const val ENABLE_MULTI_MODULE = "aarEnableMultiModule"
        const val KEEP_MODULES = "aarKeepModules"
        const val USE_SETTINGS_MODULES = "arrUseSettingsModules"
        const val USE_TEST_MODE = "arrTestMode"
        const val REPOSITORY_NAME = "arrRepositoryName"
        const val SHOW_DEPENDENCY = "arrShowDependency"
    }

    var aarEnableLog = false
    var aarEnableMaven = false
    var aarEnableMultiModule = false
    var aarShowDependency = false
    var aarKeepModules = ArrayList<String>()
    var arrModulesMode = ArrModulesMode.USE_SETTINGS
    var arrTestMode = false
    var mavenRepositoryName = "localAARMavenRepository"

    fun loadProperties(gradle: Gradle, settings: DefaultSettings) {
        aarKeepModules.clear()
        val propertiesList = PropertiesUtil.findAllGradleProperties(gradle, settings)
        propertiesList.forEach { properties ->
            if (!aarEnableMultiModule) {
                aarEnableMultiModule = properties.getProperty(ENABLE_MULTI_MODULE, "false").toBoolean()
                if (aarEnableMultiModule) {
                    val aarKeepModulesStr = properties.getOrDefault(KEEP_MODULES, "").toString().trim()
                    if (aarKeepModulesStr.isNotEmpty()) {
                        val array = aarKeepModulesStr.split(" ")
                        aarKeepModules.addAll(array)
                    }
                }
            }
            if (!aarEnableMaven) {
                aarEnableMaven = properties.getProperty(ENABLE_MAVEN, "false").toBoolean()
            }

            if (!aarEnableLog) {
                aarEnableLog = properties.getProperty(ENABLE_DEBUG_LOG, "false").toBoolean()
            }
            if (arrModulesMode == ArrModulesMode.USE_SETTINGS) {
                val value = properties.getProperty(USE_SETTINGS_MODULES, "0").toInt()
                when(value) {
                    0 -> arrModulesMode = ArrModulesMode.USE_SETTINGS
                    1 -> arrModulesMode = ArrModulesMode.USE_PROPERTIES
                    2 -> arrModulesMode = ArrModulesMode.USE_PROPERTIES_AND_VISIBILITY
                }
            }
            if (!arrTestMode) {
                arrTestMode = properties.getProperty(USE_TEST_MODE, "false").toBoolean()
            }
            if (!aarShowDependency) {
                aarShowDependency = properties.getProperty(SHOW_DEPENDENCY, "false").toBoolean()
            }
            mavenRepositoryName = properties.getProperty(REPOSITORY_NAME, mavenRepositoryName).toString()
        }
    }

    enum class ArrModulesMode {
        USE_SETTINGS, // Settings control module visibility
        USE_PROPERTIES, // Properties control to use aar or not
        USE_PROPERTIES_AND_VISIBILITY // Properties control to use aar or not and enable/disable modules visibility
    }
}