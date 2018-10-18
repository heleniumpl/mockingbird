plugins {
    kotlin("jvm") version "1.3.0-rc-190"
}

object Versions {
    const val logback = "1.2.3"
    const val kotlinLogging = "1.6.10"
    const val spark = "2.8.0"
    const val jackson = "2.9.7"

    // test
    const val spek2 = "2.0.0-rc.1"
    const val kotlinTest = "3.1.10"
    const val fuel = "1.16.0"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect")

    implementation(group = "io.github.microutils", name = "kotlin-logging", version = Versions.kotlinLogging)
    implementation(group = "com.sparkjava", name = "spark-core", version = Versions.spark)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = Versions.jackson)

    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = Versions.logback)

    testImplementation(group = "org.spekframework.spek2", name = "spek-dsl-jvm", version = Versions.spek2)
    testImplementation(group = "io.kotlintest", name = "kotlintest-assertions", version = Versions.kotlinTest)
    testImplementation(group = "com.github.kittinunf.fuel", name = "fuel", version = Versions.fuel)

    testRuntimeOnly(group = "org.spekframework.spek2", name = "spek-runner-junit5", version = Versions.spek2)
}

tasks {
    withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}
