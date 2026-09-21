plugins {
    id("purrello.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.model)
            api(projects.core.network)
            implementation(libs.koin.core)
        }
    }
}
