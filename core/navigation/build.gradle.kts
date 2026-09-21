plugins {
    id("purrello.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            api(libs.navigation3.ui)      // NavKey, NavBackStack
            api(libs.savedstate)          // SavedStateConfiguration for the back-stack serializers
        }
    }
}
