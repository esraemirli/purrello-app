rootProject.name = "Purrello"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":shared")

include(":core:common")
include(":core:model")
include(":core:network")
include(":core:data")
include(":core:designsystem")
include(":core:ui")
include(":core:navigation")
include(":core:testing")

include(":feature:auth")
include(":feature:home")
include(":feature:health")
include(":feature:documents")
include(":feature:care")
include(":feature:pet")
include(":feature:lostpet")
include(":feature:account")
