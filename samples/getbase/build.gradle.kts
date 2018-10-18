plugins {
    kotlin("jvm") version Versions.kotlin
}

apply(from = "$rootDir/gradle/test.gradle.kts")

dependencies {
    implementation(project(":core"))
}
