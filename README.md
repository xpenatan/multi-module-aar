# multi-module-aar

Gradle plugin that switch between project modules and aar libs.
This can improve build time if your project contains many modules by building only a few modules instead of the whole project.

For example, a project that contains 100+ modules and takes 15+ minutes to build from scratch. 
This solution can decrease to about 2 minutes depending on your machine.

The idea behind is that you first generate aar from all modules that acts like a cache and use this plugin to configure
which module will use aar and which ones will use project modules.

## Usage

```
// Root build.gradle

plugins {
    id("io.github.xpenatan.multi-module-aar")
}

// To generate aar

./gradlew publishMultiModuleAARPublicationToMavenRepository -PaarEnableMaven=true -PaarEnableMultiModule=false

// local.properties

aarEnableMaven=true
aarEnableMultiModule=true
aarKeepModules=\
:demo:standalonelib
```