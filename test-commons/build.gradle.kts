plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))

    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = Versions.fuel)
}
