plugins {
    kotlin("jvm") version "1.2.61"
}

dependencies {
    compile(kotlin("stdlib"))
}

repositories {
    jcenter()
}

tasks.withType<Wrapper> {
    gradleVersion = "4.10"
    distributionType = Wrapper.DistributionType.ALL
}
