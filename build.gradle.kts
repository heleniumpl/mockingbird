import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version Versions.GradlePlugins.versions

    kotlin("jvm") version Versions.kotlin apply false
}

subprojects {
    repositories {
        jcenter()
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    configurations.all {
        resolutionStrategy {
            eachDependency {
                if(requested.group == "org.jetbrains.kotlin") {
                    useVersion(Versions.kotlin)
                }
            }
        }
    }
}

tasks {
    withType<Wrapper> {
        gradleVersion = Versions.gradle
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<DependencyUpdatesTask> {
        resolutionStrategy {
            componentSelection {
                all {
                    listOf("alpha", "beta", "rc", "cr", "m", "eap")
                        .asSequence()
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                        .any { it.matches(candidate.version) }
                        .let { if (it) reject("Release candidate") }
                }
            }
        }
    }
}
