plugins {
    kotlin("jvm") version "1.8.22"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "7.5"
        distributionType = Wrapper.DistributionType.BIN
    }
}
