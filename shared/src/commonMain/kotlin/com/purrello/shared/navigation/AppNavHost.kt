package com.purrello.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.purrello.core.navigation.Main
import com.purrello.feature.account.navigation.accountEntries
import com.purrello.feature.auth.navigation.authEntries
import com.purrello.feature.care.navigation.careEntries
import com.purrello.feature.documents.navigation.documentsEntries
import com.purrello.feature.health.navigation.healthEntries
import com.purrello.feature.home.navigation.homeEntries
import com.purrello.feature.lostpet.navigation.lostPetEntries
import com.purrello.feature.pet.navigation.petEntries
import com.purrello.shared.main.MainScreen

@Composable
fun AppNavHost(
    navigator: AppNavigator,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = navigator.backStack,
        modifier = modifier,
        onBack = { navigator.back() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),   // one ViewModelStore per entry, cleared on pop
        ),
        entryProvider = entryProvider<NavKey> {
            authEntries(navigator)
            entry<Main> { MainScreen(navigator = navigator) }
            homeEntries(navigator)
            healthEntries(navigator)
            documentsEntries(navigator)
            careEntries(navigator)
            petEntries(navigator)
            lostPetEntries(navigator)
            accountEntries(navigator)
        },
    )
}
