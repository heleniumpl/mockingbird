plugins {
    kotlin("jvm") version "1.2.61"
}

object Versions {
    const val logback = "1.2.3"
    const val kotlinLogging = "1.6.10"
    const val undertow = "2.0.13.Final"

    const val spek2 = "2.0.0-alpha.1"
    const val kotlinTest = "3.1.9"
}

repositories {
    jcenter()

    maven("https://dl.bintray.com/spekframework/spek-dev")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(group = "io.github.microutils", name = "kotlin-logging", version = Versions.kotlinLogging)
    implementation(group = "io.undertow", name = "undertow-core", version = Versions.undertow)

    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = Versions.logback)

    testImplementation(group = "org.spekframework.spek2", name = "spek-dsl-jvm", version = Versions.spek2)
    testImplementation(group = "io.kotlintest", name = "kotlintest-assertions", version = Versions.kotlinTest)

    testRuntimeOnly(group = "org.spekframework.spek2", name = "spek-runner-junit5", version = Versions.spek2)
    testRuntimeOnly(group = "org.jetbrains.kotlin", name = "kotlin-reflect")
}


tasks {
    withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    withType<Wrapper> {
        gradleVersion = "4.10"
        distributionType = Wrapper.DistributionType.ALL
    }
}
