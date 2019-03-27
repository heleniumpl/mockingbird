plugins {
    kotlin("jvm")
}

apply(from = "$rootDir/gradle/test.gradle.kts")

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = Versions.jackson)

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect")

    implementation(group = "io.github.microutils", name = "kotlin-logging", version = Versions.kotlinLogging)
    implementation(group = "com.sparkjava", name = "spark-core", version = Versions.spark)

    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = Versions.logback)
}
