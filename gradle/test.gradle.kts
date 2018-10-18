val testImplementation by configurations
val testRuntimeOnly by configurations

private object Versions {
    const val spek2 = "2.0.0-rc.1"
    const val kotlinTest = "3.1.10"
}

dependencies {
    testImplementation(project(":test-commons"))
    testImplementation(group = "org.spekframework.spek2", name = "spek-dsl-jvm", version = Versions.spek2)
    testImplementation(group = "io.kotlintest", name = "kotlintest-assertions", version = Versions.kotlinTest)

    testRuntimeOnly(group = "org.spekframework.spek2", name = "spek-runner-junit5", version = Versions.spek2)
}

tasks {
    withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}
