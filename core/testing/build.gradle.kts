// Test helpers & fakes shared by all modules' commonTest. Never a dependency of production code.
plugins {
    id("purrello.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.data)
            api(libs.kotlin.test)
            api(libs.kotlinx.coroutines.test)
            api(libs.turbine)
            api(libs.assertk)
        }
        androidMain.dependencies {
            api(libs.kotlin.testJunit)   // kotlin-test needs a concrete runner on the JVM
        }
    }
}
