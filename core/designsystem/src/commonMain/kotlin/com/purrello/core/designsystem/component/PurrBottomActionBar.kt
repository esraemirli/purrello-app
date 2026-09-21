package com.purrello.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.designsystem.theme.PurrelloTheme

/** Sticky bottom bar for the screen's primary CTA ("Kaydet", "Devam et"). Never put CTAs in scrolling content. */
@Composable
fun PurrBottomActionBar(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(color = PurrelloTheme.colors.bgSurface, modifier = modifier.fillMaxWidth()) {
        Column {
            HorizontalDivider(color = PurrelloTheme.colors.border)
            Column(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = PurrelloTheme.spacing.screenEdge, vertical = PurrelloTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(PurrelloTheme.spacing.sm),
                content = content,
            )
        }
    }
}
