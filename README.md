# multi-module-aar

Gradle plugin that switch between project modules and aar libs.
This can improve build time if your project contains many modules by building only a few modules instead of the whole project.

The idea behind is that the generated aar libs are generated first and the developer choose which module will actually compile.
Skipping unused modules from building may decrease build time.

## Usage

```
// settings.gradle

plugins {
    id("io.github.xpenatan.multi-module-aar") version LIB_VERSION
}

// Create a local.properties file and add these properties:

aarCacheEnabled=false
aarEnableLog=false
aarEnableMaven=true
aarEnableMultiModule=true
aarShowDependency=false
aarTaskShouldRerun=false
arrUseSettingsModules=2
aarKeepModules=\
:demo:app \
:demo:standaloneAARlib \
:demo:standalonelib \
:demo:feature:login:login \
:demo:feature:login:start \
:demo:feature:base \
:demo:lib:components \
:demo:feature:splash \
:demo:feature:dashboard:start \
:demo:feature:dashboard:dashboard

To generate aar set aarEnableMaven to true and sync. 
Call "./gradlew publishLocalAARPublicationToMavenRepository"
aar files will be created in localAARMavenRepository folder

Use aarKeepModules property to select which modules will actually compile every build

```