plugins {
    kotlin("jvm") version Versions.kotlin
}

apply(from = "$rootDir/gradle/test.gradle.kts")

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect")

    implementation(group = "io.github.microutils", name = "kotlin-logging", version = Versions.kotlinLogging)
    implementation(group = "com.sparkjava", name = "spark-core", version = Versions.spark)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = Versions.jackson)

    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = Versions.logback)
}
