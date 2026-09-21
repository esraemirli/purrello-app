// KMP module with Compose Multiplatform UI + string/drawable resources.
plugins {
    id("purrello.kmp.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    androidLibrary {
        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(lib("compose-runtime"))
            implementation(lib("compose-foundation"))
            implementation(lib("compose-ui"))
            implementation(lib("compose-components-resources"))
            implementation(lib("compose-uiToolingPreview"))
        }
    }
}

// One Res class per module, in the module's own package, so accessors never collide.
compose.resources {
    packageOfResClass = "${purrelloNamespace()}.resources"
}

dependencies {
    "androidRuntimeClasspath"(lib("compose-uiTooling"))
}
