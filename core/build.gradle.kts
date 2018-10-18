plugins {
    kotlin("jvm") version "1.3.0-rc-190"
}

apply(from = "$rootDir/gradle/test.gradle.kts")

private object Versions {
    const val logback = "1.2.3"
    const val kotlinLogging = "1.6.10"
    const val spark = "2.8.0"
    const val jackson = "2.9.7"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect")

    implementation(group = "io.github.microutils", name = "kotlin-logging", version = Versions.kotlinLogging)
    implementation(group = "com.sparkjava", name = "spark-core", version = Versions.spark)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = Versions.jackson)

    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = Versions.logback)
}
