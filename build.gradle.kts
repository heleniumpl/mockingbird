import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.github.ben-manes.versions") version "0.20.0"
}

subprojects {
    repositories {
        jcenter()

        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/spekframework/spek-dev")
    }
}

tasks {
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
