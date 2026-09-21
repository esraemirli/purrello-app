// Umbrella module: root composable, root navigation, Koin graph. Exports the single iOS framework "Shared".
plugins {
    id("purrello.kmp.compose")
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.data)
            implementation(projects.core.designsystem)
            implementation(projects.core.ui)
            implementation(projects.core.navigation)

            implementation(projects.feature.auth)
            implementation(projects.feature.home)
            implementation(projects.feature.health)
            implementation(projects.feature.documents)
            implementation(projects.feature.care)
            implementation(projects.feature.pet)
            implementation(projects.feature.lostpet)
            implementation(projects.feature.account)

            implementation(libs.navigation3.ui)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.viewmodel.navigation3)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
