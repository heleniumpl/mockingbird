plugins {
    kotlin("jvm") version "1.2.70"
}

object Versions {
    const val logback = "1.2.3"
    const val kotlinLogging = "1.6.10"
    const val spark = "2.7.2"
    const val jackson = "2.9.6"

    // test
    const val spek2 = "2.0.0-alpha.2"
    const val kotlinTest = "3.1.9"
    const val fuel = "1.15.0"
}

repositories {
    jcenter()

    maven("https://dl.bintray.com/spekframework/spek-dev")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

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

    withType<Wrapper> {
        gradleVersion = "4.10.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}
