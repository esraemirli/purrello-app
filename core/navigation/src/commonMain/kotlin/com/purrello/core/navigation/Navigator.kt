package com.purrello.core.navigation

/**
 * The only way features navigate. Used in feature `navigation/*Entries.kt` to back Route lambdas —
 * never in ViewModels or Screens (navigation.md §4–5).
 */
interface Navigator {
    fun navigate(route: AppRoute)
    fun back()

    /** Clears the stack: Splash → Main, logout/session expiry → Login. */
    fun replaceAll(route: AppRoute)

    /** Pops to Main and selects [tab]. */
    fun switchTab(tab: MainTab)
}
