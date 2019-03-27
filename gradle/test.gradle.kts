val testImplementation by configurations
val testRuntimeOnly by configurations

dependencies {
    testImplementation(project(":test-commons"))
    testImplementation(group = "org.spekframework.spek2", name = "spek-dsl-jvm", version = Versions.spek2)
    testImplementation(group = "io.kotlintest", name = "kotlintest-assertions", version = Versions.kotlinTest)
    testImplementation(group = "io.mockk", name = "mockk", version = Versions.mockk)
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
