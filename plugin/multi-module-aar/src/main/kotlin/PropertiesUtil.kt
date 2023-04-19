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
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class PropertiesUtil {
    companion object {
        private const val PROPERTY_GRADLE_LOCAL = "local.properties"
        private const val PROPERTY_GRADLE = "gradle.properties"

        fun findAllGradleProperties(gradle: Gradle): List<Properties> {
            val projectDir: File = gradle.rootProject.projectDir
            val usersGradleProperties: Properties? = findUsersGradleProperties(gradle)
            val localProperties: Properties? = findLocalProperties(projectDir)
            val gradleProperties: Properties? = findGradleProperties(projectDir)
            val list: ArrayList<Properties> = ArrayList()
            if (usersGradleProperties != null) list.add(usersGradleProperties)
            if (localProperties != null) list.add(localProperties)
            if (gradleProperties != null) list.add(gradleProperties)
            return list
        }

        private fun findLocalProperties(projectDirectory: File): Properties? {
            val localProperties = File(projectDirectory, PROPERTY_GRADLE_LOCAL)
            return if (localProperties.exists()) readProperties(localProperties) else null
        }

        private fun findUsersGradleProperties(gradle: Gradle): Properties? {
            val gradleProperties = File(gradle.gradleHomeDir, PROPERTY_GRADLE)
            return if (gradleProperties.exists()) {
                readProperties(gradleProperties)
            } else null
        }

        private fun findGradleProperties(projectDirectory: File): Properties? {
            val localProperties = File(projectDirectory, PROPERTY_GRADLE)
            return if (localProperties.exists()) {
                readProperties(localProperties)
            } else null
        }

        private fun readProperties(file: File): Properties {
            val prof = Properties()
            if (file.exists()) {
                try {
                    prof.load(FileInputStream(file))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return prof
        }
    }
}