plugins {
    id("purrello.android.application")
}

android {
    namespace = "com.purrello.app"

    defaultConfig {
        applicationId = "com.purrello.app"   // TODO(release): confirm final application id
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            buildConfigField("String", "ENVIRONMENT", "\"DEV\"")
            buildConfigField("String", "BASE_URL", "\"https://api.dev.purrello.app/\"")   // TODO(backend): real dev URL
            buildConfigField("boolean", "USE_FAKE_API", "true")
        }
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "ENVIRONMENT", "\"PROD\"")
            buildConfigField("String", "BASE_URL", "\"https://api.purrello.app/\"")       // TODO(backend): real prod URL
            buildConfigField("boolean", "USE_FAKE_API", "false")
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}
