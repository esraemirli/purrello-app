package com.purrello.feature.account.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.purrello.core.navigation.Login
import com.purrello.core.navigation.Navigator
import com.purrello.core.navigation.OwnerProfile
import com.purrello.feature.account.presentation.profile.OwnerProfileRoute

fun EntryProviderScope<NavKey>.accountEntries(navigator: Navigator) {
    entry<OwnerProfile> {
        OwnerProfileRoute(
            onBack = navigator::back,
            onSignedOut = { navigator.replaceAll(Login) },
        )
    }
}
