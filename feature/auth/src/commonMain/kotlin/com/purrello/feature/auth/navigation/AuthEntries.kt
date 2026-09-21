package com.purrello.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.purrello.core.navigation.Login
import com.purrello.core.navigation.Main
import com.purrello.core.navigation.Navigator
import com.purrello.core.navigation.Splash
import com.purrello.feature.auth.presentation.login.LoginRoute
import com.purrello.feature.auth.presentation.splash.SplashRoute

fun EntryProviderScope<NavKey>.authEntries(navigator: Navigator) {
    entry<Splash> {
        SplashRoute(
            onLoggedIn = { navigator.replaceAll(Main) },
            onLoggedOut = { navigator.replaceAll(Login) },
        )
    }
    entry<Login> {
        // TODO(product): isNewUser → onboarding / "Pet ekle" once designed.
        LoginRoute(onSignedIn = { navigator.replaceAll(Main) })
    }
}
