package com.purrello.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.purrello.core.designsystem.icon.PurrIcons
import com.purrello.core.designsystem.theme.PurrelloTheme

/**
 * Tab-root "sıcak başlık" (expanded). bg-hero, bottom corners 28, title + subtitle, optional 48 px add button,
 * trailing slot for the owner avatar.
 * TODO(ds): paw-print decoration and the collapsing compact (56 px) state on scroll.
 */
@Composable
fun PurrAppHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    addContentDescription: String? = null,
    onAddClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = PurrelloTheme.colors
    val spacing = PurrelloTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgHero, PurrelloTheme.shapes.heroHeader)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = spacing.screenEdge, vertical = spacing.lg),
    ) {
        if (trailing != null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { trailing() }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = PurrelloTheme.typography.title1, color = colors.textPrimary)
                if (subtitle != null) {
                    Text(text = subtitle, style = PurrelloTheme.typography.bodySm, color = colors.heroText2)
                }
            }
            if (onAddClick != null) {
                FilledIconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(PurrelloTheme.sizes.heroAddButton),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = colors.actionPrimary,
                        contentColor = colors.onActionPrimary,
                    ),
                ) {
                    Icon(imageVector = PurrIcons.Add, contentDescription = addContentDescription)
                }
            }
        }
    }
}

/** Pushed detail screen: back + centered title. TODO(ds): transparent → bg-surface + divider on scroll. */
@Composable
fun PurrDetailBar(
    title: String,
    backContentDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BarRow(modifier = modifier) {
        IconButton(onClick = onBack) {
            Icon(imageVector = PurrIcons.Back, contentDescription = backContentDescription, tint = PurrelloTheme.colors.textPrimary)
        }
        BarTitle(title = title, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.size(PurrelloTheme.sizes.minTouch))
    }
}

/** Full-screen modal (add/edit flows): × on the left, title; primary action lives in PurrBottomActionBar. */
@Composable
fun PurrModalBar(
    title: String,
    closeContentDescription: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BarRow(modifier = modifier) {
        IconButton(onClick = onClose) {
            Icon(imageVector = PurrIcons.Close, contentDescription = closeContentDescription, tint = PurrelloTheme.colors.textPrimary)
        }
        BarTitle(title = title, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.size(PurrelloTheme.sizes.minTouch))
    }
}

@Composable
private fun BarRow(modifier: Modifier, content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(PurrelloTheme.sizes.compactHeader)
            .padding(horizontal = PurrelloTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun BarTitle(title: String, modifier: Modifier) {
    Text(
        text = title,
        modifier = modifier,
        style = PurrelloTheme.typography.title3,
        color = PurrelloTheme.colors.textPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
