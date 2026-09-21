package com.purrello.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.designsystem.component.PurrAppHeader
import com.purrello.core.designsystem.component.PurrIconButton
import com.purrello.core.designsystem.component.PurrEmptyState
import com.purrello.core.designsystem.icon.PurrIcons
import com.purrello.core.designsystem.theme.PurrelloTheme
import com.purrello.core.ui.resources.Res
import com.purrello.core.ui.resources.a11y_add
import com.purrello.core.ui.resources.a11y_open_profile
import com.purrello.core.ui.resources.common_coming_soon_body
import com.purrello.core.ui.resources.common_coming_soon_title
import org.jetbrains.compose.resources.stringResource

/**
 * Standard tab-root layout: warm header (title, subtitle, + button, owner avatar → profile) above the content.
 * Tab bar is drawn by the Main host, not here.
 */
@Composable
fun TabRootScaffold(
    title: String,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onAddClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().background(PurrelloTheme.colors.bgPage)) {
        PurrAppHeader(
            title = title,
            subtitle = subtitle,
            onAddClick = onAddClick,
            addContentDescription = stringResource(Res.string.a11y_add),
            trailing = {
                // TODO(ds): owner avatar (PurrAvatar) instead of the generic icon.
                PurrIconButton(
                    icon = PurrIcons.Pet,
                    contentDescription = stringResource(Res.string.a11y_open_profile),
                    onClick = onOpenProfile,
                    tint = PurrelloTheme.colors.heroText1,
                )
            },
        )
        content()
    }
}

/** Placeholder body for tabs whose screens aren't built yet. */
@Composable
fun ComingSoonContent(modifier: Modifier = Modifier) {
    PurrEmptyState(
        title = stringResource(Res.string.common_coming_soon_title),
        body = stringResource(Res.string.common_coming_soon_body),
        modifier = modifier,
    )
}
