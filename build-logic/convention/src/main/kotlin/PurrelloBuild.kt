import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.lib(alias: String): Provider<MinimalExternalModuleDependency> =
    libs.findLibrary(alias).orElseThrow { IllegalArgumentException("Missing library '$alias' in libs.versions.toml") }

internal fun Project.intVersion(alias: String): Int =
    libs.findVersion(alias).get().requiredVersion.toInt()

/** `:feature:health` → `com.purrello.feature.health`, `:core:designsystem` → `com.purrello.core.designsystem`. */
internal fun Project.purrelloNamespace(): String =
    "com.purrello." + path.removePrefix(":").replace(":", ".").replace("-", "")
