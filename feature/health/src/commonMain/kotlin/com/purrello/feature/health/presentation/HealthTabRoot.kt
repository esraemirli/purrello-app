package com.purrello.feature.health.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.ui.component.ComingSoonContent
import com.purrello.core.ui.component.TabRootScaffold
import com.purrello.feature.health.resources.Res
import com.purrello.feature.health.resources.health_tab_subtitle
import com.purrello.feature.health.resources.health_tab_title
import org.jetbrains.compose.resources.stringResource

/**
 * Root of the "Sağlık" tab, rendered by the Main host (navigation.md §1).
 * TODO: contract (docs/api/health/…) → HealthViewModel + HealthScreen via /create-screen.
 */
@Composable
fun HealthTabRoot(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRootScaffold(
        title = stringResource(Res.string.health_tab_title),
        subtitle = stringResource(Res.string.health_tab_subtitle),
        onOpenProfile = onOpenProfile,
        modifier = modifier,
    ) {
        ComingSoonContent()
    }
}
