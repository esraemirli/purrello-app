plugins {
    id("purrello.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Material 3 is an implementation detail of the DS — features use Purr* components only.
            implementation(libs.compose.material3)
            api(libs.compose.materialIconsCore) // TODO(ds): replace with Phosphor Regular icon set
        }
    }
}
