pluginManagement {
    //Uncomment to use github repo
    includeBuild("plugin")
}

plugins {
    id("io.github.xpenatan.multi-module-aar") version "0.3"
}

include(":demo:app")
include(":demo:feature:base")
include(":demo:feature:splash")
include(":demo:feature:login:start")
include(":demo:feature:login:login")
include(":demo:feature:dashboard:start")
include(":demo:feature:dashboard:dashboard")

include(":demo:lib:components")

include(":demo:standaloneAARlib")
include(":demo:standalonelib")