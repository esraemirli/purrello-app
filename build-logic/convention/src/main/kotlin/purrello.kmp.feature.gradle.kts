// Feature module: Compose + ViewModel + Navigation 3 + Koin + the core modules every feature uses.
// Features must never depend on other features (see .agents/rules/kmp-conventions.md).
plugins {
    id("purrello.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:model"))
            implementation(project(":core:network"))
            implementation(project(":core:data"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:ui"))
            implementation(project(":core:navigation"))

            implementation(lib("lifecycle-viewmodel-compose"))
            implementation(lib("lifecycle-viewmodel-savedstate"))
            implementation(lib("lifecycle-runtime-compose"))
            implementation(lib("navigation3-ui"))
            implementation(lib("koin-core"))
            implementation(lib("koin-compose"))
            implementation(lib("koin-compose-viewmodel"))
            implementation(lib("ktor-client-core"))
            implementation(lib("kotlinx-datetime"))
        }
        commonTest.dependencies {
            implementation(project(":core:testing"))
            implementation(lib("ktor-client-mock"))
        }
    }
}
