plugins {
    id("purrello.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.designsystem)
            implementation(projects.core.model)
            // MviViewModel extends androidx ViewModel and features subclass it → api, not implementation.
            api(libs.lifecycle.viewmodel)
        }
    }
}

// App-wide strings (common_*, error_*) are shared with every feature.
compose.resources {
    publicResClass = true
}
