package com.purrello.feature.care.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.ui.component.ComingSoonContent
import com.purrello.core.ui.component.TabRootScaffold
import com.purrello.feature.care.resources.Res
import com.purrello.feature.care.resources.care_tab_subtitle
import com.purrello.feature.care.resources.care_tab_title
import org.jetbrains.compose.resources.stringResource

/**
 * Root of the "Bakım" tab, rendered by the Main host (navigation.md §1).
 * TODO: contract (docs/api/care/…) → CareViewModel + CareScreen via /create-screen.
 */
@Composable
fun CareTabRoot(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRootScaffold(
        title = stringResource(Res.string.care_tab_title),
        subtitle = stringResource(Res.string.care_tab_subtitle),
        onOpenProfile = onOpenProfile,
        modifier = modifier,
    ) {
        ComingSoonContent()
    }
}
