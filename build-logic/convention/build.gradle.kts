plugins {
    `kotlin-dsl`
}

// Plugins on this classpath are what `purrello.*` precompiled script plugins apply.
// Their versions live in gradle/libs.versions.toml — modules never declare plugin versions.
dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.serialization.gradlePlugin)
    implementation(libs.compose.compiler.gradlePlugin)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.android.gradlePlugin)
}
