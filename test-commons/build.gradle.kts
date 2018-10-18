plugins {
    kotlin("jvm") version Versions.kotlin
}

dependencies {
    implementation(project(":core"))

    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = Versions.fuel)
}
