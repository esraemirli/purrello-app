// Base for every KMP module: Android (AGP 9 KMP library) + iOS targets, serialization, coroutines, tests.
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    androidLibrary {
        namespace = purrelloNamespace()
        compileSdk = intVersion("android-compileSdk")
        minSdk = intVersion("android-minSdk")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        withHostTest {}
    }

    iosArm64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            // api: both types show up in public APIs (StateFlow on repositories, ImmutableMap/List on
            // AppError and UI state), so consumers need them on their compile classpath.
            api(lib("kotlinx-coroutines-core"))
            api(lib("kotlinx-collections-immutable"))
            implementation(lib("kotlinx-serialization-json"))
        }
        commonTest.dependencies {
            implementation(lib("kotlin-test"))
            implementation(lib("kotlinx-coroutines-test"))
            implementation(lib("turbine"))
            implementation(lib("assertk"))
        }
    }
}
