plugins {
    kotlin("jvm") version "1.8.22"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    }
}

tasks {
    wrapper {
        gradleVersion = "7.5"
        distributionType = Wrapper.DistributionType.BIN
    }
}
