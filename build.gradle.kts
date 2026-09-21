// Plugins and their versions come from build-logic (convention plugins, see build-logic/convention).
// Modules apply `purrello.*` plugins only — no plugin versions or shared config here.

/**
 * CI entry point for tests: every JVM/Android unit-test task in the build.
 *
 * Kotlin/Native (iOS) test tasks are NOT `Test` tasks, so they are excluded by construction — they need a
 * macOS host and would break a Linux runner. A module with no test task simply contributes nothing:
 * "no tests" is a pass, "a failing test" is a failure.
 */
tasks.register("hostTests") {
    group = "verification"
    description = "Runs the JVM/Android unit tests of every module (Apple targets require a macOS host)."
    dependsOn(subprojects.map { project -> project.tasks.withType(Test::class.java) })
}
