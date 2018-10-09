import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "1.3.0-rc-146"

    id("com.github.ben-manes.versions") version "0.20.0"
}

object Versions {
    const val logback = "1.2.3"
    const val kotlinLogging = "1.6.10"
    const val spark = "2.8.0"
    const val jackson = "2.9.7"

    // test
    const val spek2 = "2.0.0-rc.1"
    const val kotlinTest = "3.1.10"
    const val fuel = "1.15.0"
}

repositories {
    jcenter()

    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://dl.bintray.com/spekframework/spek-dev")
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

    withType<Wrapper> {
        gradleVersion = "4.10.2"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<DependencyUpdatesTask> {
        resolutionStrategy {
            componentSelection {
                all {
                    if (candidate.group in setOf(
                            "org.spekframework.spek2",
                            "org.jetbrains.kotlin",
                            "org.jetbrains.kotlin.jvm"
                        )
                    ) return@all


                    listOf("alpha", "beta", "rc", "cr", "m")
                        .asSequence()
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                        .any { it.matches(candidate.version) }
                        .let { if (it) reject("Release candidate") }
                }
            }
        }
    }
}
