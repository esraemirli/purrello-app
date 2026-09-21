package com.purrello.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.purrello.core.designsystem.icon.PurrIcons
import com.purrello.core.designsystem.theme.PurrelloTheme

@Composable
fun PurrLoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PurrelloTheme.colors.actionPrimary)
    }
}

/** Empty or informational full-area state. TODO(ds): coral + paw illustration for brand moments. */
@Composable
fun PurrEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    icon: ImageVector = PurrIcons.Info,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    MessageState(title, body, icon, actionText, onAction, modifier)
}

/** Full-screen error for a failed initial load. The header stays so users can go back. */
@Composable
fun PurrErrorState(
    title: String,
    body: String,
    retryText: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessageState(title, body, PurrIcons.Warning, retryText, onRetry, modifier)
}

@Composable
private fun MessageState(
    title: String,
    body: String?,
    icon: ImageVector,
    actionText: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier,
) {
    val spacing = PurrelloTheme.spacing
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = spacing.screenEdge),
        verticalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PurrelloTheme.colors.actionPrimary,
            modifier = Modifier.size(PurrelloTheme.sizes.heroAddButton),
        )
        Text(
            text = title,
            style = PurrelloTheme.typography.title2,
            color = PurrelloTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        if (body != null) {
            Text(
                text = body,
                style = PurrelloTheme.typography.body,
                color = PurrelloTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        if (actionText != null && onAction != null) {
            PurrButton(text = actionText, onClick = onAction, modifier = Modifier.fillMaxWidth())
        }
    }
}
