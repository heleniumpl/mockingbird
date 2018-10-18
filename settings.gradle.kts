rootProject.name = "mockingbird"

pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

include("core")
