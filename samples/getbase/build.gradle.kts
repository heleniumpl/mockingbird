plugins {
    kotlin("jvm") version "1.3.0-rc-190"
}

apply(from = "$rootDir/gradle/test.gradle.kts")

dependencies {
    implementation(project(":core"))
}
