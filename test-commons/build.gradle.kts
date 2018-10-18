plugins {
    kotlin("jvm") version "1.3.0-rc-190"
}

private object Versions {
    const val fuel = "1.16.0"
}

dependencies {
    implementation(project(":core"))

    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = Versions.fuel)
}
