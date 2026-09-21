package com.purrello.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.purrello.core.designsystem.theme.PurrelloTheme

/**
 * The only text primitive features use — Material 3 stays inside the design system (ui-conventions.md §1).
 * Defaults to body style in the primary ink color.
 */
@Composable
fun PurrText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = PurrelloTheme.typography.body,
    color: Color = PurrelloTheme.colors.textPrimary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
    )
}
