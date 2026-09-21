package com.purrello.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.purrello.core.designsystem.theme.PurrelloTheme

enum class PurrButtonStyle { PRIMARY, SECONDARY, TEXT, DANGER }

@Composable
fun PurrButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PurrButtonStyle = PurrButtonStyle.PRIMARY,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val colors = PurrelloTheme.colors
    val shape = PurrelloTheme.shapes.pill
    val height = PurrelloTheme.sizes.button
    val sized = modifier.defaultMinSize(minHeight = height)
    val padding = PaddingValues(horizontal = PurrelloTheme.spacing.xl)
    val content: @Composable () -> Unit = {
        Box(contentAlignment = Alignment.Center) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(PurrelloTheme.sizes.icon), strokeWidth = 2.dp)
            } else {
                Text(text = text, style = PurrelloTheme.typography.button)
            }
        }
    }
    val isEnabled = enabled && !loading
    when (style) {
        PurrButtonStyle.PRIMARY, PurrButtonStyle.DANGER -> Button(
            onClick = onClick,
            modifier = sized,
            enabled = isEnabled,
            shape = shape,
            contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (style == PurrButtonStyle.DANGER) colors.danger else colors.actionPrimary,
                contentColor = colors.onActionPrimary,
            ),
        ) { content() }

        PurrButtonStyle.SECONDARY -> OutlinedButton(
            onClick = onClick,
            modifier = sized,
            enabled = isEnabled,
            shape = shape,
            contentPadding = padding,
            border = BorderStroke(1.dp, colors.actionPrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.actionPrimary),
        ) { content() }

        PurrButtonStyle.TEXT -> TextButton(
            onClick = onClick,
            modifier = sized,
            enabled = isEnabled,
            shape = shape,
            contentPadding = padding,
            colors = ButtonDefaults.textButtonColors(contentColor = colors.actionPrimary),
        ) { content() }
    }
}
