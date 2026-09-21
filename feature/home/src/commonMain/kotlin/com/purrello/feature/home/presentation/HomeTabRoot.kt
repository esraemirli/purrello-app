package com.purrello.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.ui.component.ComingSoonContent
import com.purrello.core.ui.component.TabRootScaffold
import com.purrello.feature.home.resources.Res
import com.purrello.feature.home.resources.home_tab_subtitle
import com.purrello.feature.home.resources.home_tab_title
import org.jetbrains.compose.resources.stringResource

/**
 * Root of the "Ana sayfa" tab, rendered by the Main host (navigation.md §1).
 * TODO: contract (docs/api/home/…) → HomeViewModel + HomeScreen via /create-screen.
 */
@Composable
fun HomeTabRoot(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRootScaffold(
        title = stringResource(Res.string.home_tab_title),
        subtitle = stringResource(Res.string.home_tab_subtitle),
        onOpenProfile = onOpenProfile,
        modifier = modifier,
    ) {
        ComingSoonContent()
    }
}
