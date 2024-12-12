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

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.0"
    `maven-publish`
}

group = "io.github.xpenatan"
version = "0.10"

repositories {
    mavenCentral()
}

dependencies {
}

gradlePlugin {
    website.set("https://github.com/xpenatan/multi-module-aar")
    vcsUrl.set("https://github.com/xpenatan/multi-module-aar.git")
    val aarModule by plugins.creating {
        id = "io.github.xpenatan.multi-module-aar"
        displayName = "Improve android build time"
        description = "A plugin that help reducing build times by switching between android project modules and AAR libs"
        implementationClass = "AARModulePlugin"
        tags.set(listOf("aar", "android", "fast", "build"))
    }
}