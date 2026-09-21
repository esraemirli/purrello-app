package com.purrello.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.purrello.core.designsystem.theme.PurrelloTheme

/**
 * The paw motif (`assets/Motifs/paw.svg`), drawn rather than bundled so it takes any color and size.
 * Authored in a 24 × 26 box; the ellipses below are that box's coordinates divided by it, so one paw
 * scales from a tab-bar glyph to a full-screen pattern without new numbers.
 */
// centerX, centerY, radiusX, radiusY — all as a fraction of the motif box.
private val PawEllipses = listOf(
    Ellipse(0.500f, 0.730f, 0.2917f, 0.2308f),   // pad
    Ellipse(0.188f, 0.385f, 0.1083f, 0.1269f),   // outer left toe
    Ellipse(0.392f, 0.192f, 0.1083f, 0.1269f),   // inner left toe
    Ellipse(0.608f, 0.192f, 0.1083f, 0.1269f),   // inner right toe
    Ellipse(0.813f, 0.385f, 0.1083f, 0.1269f),   // outer right toe
)

@Immutable
private data class Ellipse(val cx: Float, val cy: Float, val rx: Float, val ry: Float)

/** Motif aspect ratio (24 × 26): a paw's height is its width times this. */
internal const val PAW_ASPECT: Float = 26f / 24f

/** Draws one paw whose bounding box is [size], centered on [center] and turned [rotationDegrees]. */
internal fun DrawScope.drawPaw(
    center: Offset,
    size: Size,
    color: Color,
    rotationDegrees: Float = 0f,
    alpha: Float = 1f,
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        val left = center.x - size.width / 2f
        val top = center.y - size.height / 2f
        PawEllipses.forEach { ellipse ->
            val width = ellipse.rx * 2f * size.width
            val height = ellipse.ry * 2f * size.height
            drawOval(
                color = color,
                topLeft = Offset(
                    x = left + ellipse.cx * size.width - width / 2f,
                    y = top + ellipse.cy * size.height - height / 2f,
                ),
                size = Size(width, height),
                alpha = alpha,
            )
        }
    }
}

/** A single paw glyph — empty states, list bullets, the brand mark. [width] drives the height. */
@Composable
fun PurrPaw(
    modifier: Modifier = Modifier,
    color: Color = PurrelloTheme.colors.bgBrand,
    width: Dp = 24.dp,
) {
    Canvas(modifier = modifier.size(width = width, height = width * PAW_ASPECT)) {
        drawPaw(center = Offset(size.width / 2f, size.height / 2f), size = size, color = color)
    }
}
