package com.purrello.core.navigation

interface Navigator {
    fun navigate(route: AppRoute)
    fun back()

    /** Clears the stack: Splash → Main, logout/session expiry → Login. */
    fun replaceAll(route: AppRoute)

    /** Pops to Main and selects [tab]. */
    fun switchTab(tab: MainTab)
}
