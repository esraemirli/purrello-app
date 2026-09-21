package com.purrello.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.purrello.core.data.pet.SelectedPetRepository
import com.purrello.core.data.session.SessionRepository
import com.purrello.core.designsystem.theme.PurrelloTheme
import com.purrello.core.navigation.DeepLink
import com.purrello.core.navigation.Login
import com.purrello.core.navigation.MainTab
import com.purrello.core.ui.mvi.ObserveEffects
import com.purrello.shared.deeplink.DeepLinkDispatcher
import com.purrello.shared.navigation.AppNavHost
import com.purrello.shared.navigation.AppNavigator
import com.purrello.shared.navigation.rememberAppNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.viewmodel.koinViewModel

/** App-level concerns that outlive screens: session expiry and pending deep links. */
class RootViewModel(
    session: SessionRepository,
    private val deepLinks: DeepLinkDispatcher,
    private val selectedPet: SelectedPetRepository,
) : ViewModel() {
    val sessionExpired: Flow<Unit> = session.sessionExpired
    val pendingDeepLink: StateFlow<DeepLink?> = deepLinks.pending

    /** Switches the selected pet first (spec: "Bildirimden gelinirse ilgili pete otomatik geçilir"). */
    fun onDeepLinkApplied(link: DeepLink) {
        link.targetPetId?.let(selectedPet::select)
        deepLinks.consume()
    }
}

@Composable
fun App() {
    PurrelloTheme {
        val navigator = rememberAppNavigator()
        val root: RootViewModel = koinViewModel()

        ObserveEffects(root.sessionExpired) { navigator.replaceAll(Login) }

        val pending by root.pendingDeepLink.collectAsStateWithLifecycle()
        LaunchedEffect(pending, navigator.isInMain) {
            val link = pending ?: return@LaunchedEffect
            if (!navigator.isInMain) return@LaunchedEffect      // wait until logged in
            root.onDeepLinkApplied(link)
            applyDeepLink(navigator, link)
        }

        AppNavHost(navigator = navigator)
    }
}

private fun applyDeepLink(navigator: AppNavigator, link: DeepLink) {
    navigator.switchTab(link.tab ?: MainTab.HOME)
    link.routes.forEach(navigator::navigate)
}
