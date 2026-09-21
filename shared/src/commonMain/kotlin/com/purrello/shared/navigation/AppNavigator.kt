package com.purrello.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.purrello.core.navigation.AppRoute
import com.purrello.core.navigation.Main
import com.purrello.core.navigation.MainTab
import com.purrello.core.navigation.Navigator
import com.purrello.core.navigation.Splash
import com.purrello.core.navigation.appRouteSavedStateConfiguration

/**
 * Owns the root back stack and the selected Main tab (navigation.md §1, §5).
 * Details/modals are pushed above [Main], which hides the tab bar and preserves tab state on return.
 */
@Stable
class AppNavigator(
    val backStack: NavBackStack<NavKey>,
    private val selectedTabName: MutableState<String>,
) : Navigator {

    val selectedTab: MainTab
        get() = MainTab.entries.firstOrNull { it.name == selectedTabName.value } ?: MainTab.HOME

    override fun navigate(route: AppRoute) {
        if (backStack.lastOrNull() == route) return          // guard double taps
        backStack.add(route)
    }

    override fun back() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    override fun replaceAll(route: AppRoute) {
        backStack.add(route)
        while (backStack.size > 1) backStack.removeAt(0)
    }

    override fun switchTab(tab: MainTab) {
        val mainIndex = backStack.indexOfLast { it == Main }
        if (mainIndex == -1) {
            replaceAll(Main)
        } else {
            while (backStack.size > mainIndex + 1) backStack.removeAt(backStack.lastIndex)
        }
        selectTab(tab)
    }

    /** Tab bar taps (stays on Main). */
    fun selectTab(tab: MainTab) {
        selectedTabName.value = tab.name
    }

    val isInMain: Boolean get() = backStack.contains(Main)
}

@Composable
fun rememberAppNavigator(): AppNavigator {
    val backStack = rememberNavBackStack(appRouteSavedStateConfiguration, Splash)
    val selectedTab = rememberSaveable { mutableStateOf(MainTab.HOME.name) }
    return remember(backStack, selectedTab) { AppNavigator(backStack, selectedTab) }
}
