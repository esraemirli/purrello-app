package com.purrello.feature.documents.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.ui.component.ComingSoonContent
import com.purrello.core.ui.component.TabRootScaffold
import com.purrello.feature.documents.resources.Res
import com.purrello.feature.documents.resources.documents_tab_subtitle
import com.purrello.feature.documents.resources.documents_tab_title
import org.jetbrains.compose.resources.stringResource

/**
 * Root of the "Belgeler" tab, rendered by the Main host (navigation.md §1).
 * TODO: contract (docs/api/documents/…) → DocumentsViewModel + DocumentsScreen via /create-screen.
 */
@Composable
fun DocumentsTabRoot(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRootScaffold(
        title = stringResource(Res.string.documents_tab_title),
        subtitle = stringResource(Res.string.documents_tab_subtitle),
        onOpenProfile = onOpenProfile,
        modifier = modifier,
    ) {
        ComingSoonContent()
    }
}
