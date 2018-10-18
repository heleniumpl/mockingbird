plugins {
    kotlin("jvm")
}

apply(from = "$rootDir/gradle/test.gradle.kts")

dependencies {
    implementation(project(":core"))
}
