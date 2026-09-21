package com.purrello.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.purrello.core.designsystem.theme.PurrelloTheme

/**
 * The tone-on-tone paw trail that fills a brand panel — splash, login, onboarding, big empty states
 * (design system → BrandHero, `assets/Motifs/paw.svg`).
 *
 * Positions come from the Splash artboard and are stored as fractions of its 390 × 844 frame, so the
 * trail keeps its composition on any screen. Paw size stays in dp: the motif should read the same on a
 * small phone and a tablet. Purely decorative: a Canvas emits no semantics, so screen readers skip it.
 *
 * @param count how many paws to draw. The trail is ordered so any prefix stays spread over the frame.
 */
@Composable
fun PurrPawPattern(
    modifier: Modifier = Modifier,
    color: Color = PurrelloTheme.colors.pawPrint,
    count: Int = PawTrail.size,
    pawWidth: Dp = 30.dp,
) {
    val paws = PawTrail.take(count.coerceIn(0, PawTrail.size))
    Canvas(modifier = modifier) {
        val width = pawWidth.toPx()
        val pawSize = Size(width, width * PAW_ASPECT)
        paws.forEach { paw ->
            drawPaw(
                center = Offset(x = paw.x * size.width, y = paw.y * size.height),
                size = pawSize,
                color = color,
                rotationDegrees = paw.rotation,
            )
        }
    }
}

@Immutable
private data class PawSpot(val x: Float, val y: Float, val rotation: Float)

/** Splash artboard positions (390 × 844), interleaved top/bottom so a shorter trail stays balanced. */
private val PawTrail = listOf(
    PawSpot(x = 46f / 390f, y = 90f / 844f, rotation = -20f),
    PawSpot(x = 300f / 390f, y = 596f / 844f, rotation = 22f),
    PawSpot(x = 150f / 390f, y = 58f / 844f, rotation = 10f),
    PawSpot(x = 120f / 390f, y = 740f / 844f, rotation = -14f),
    PawSpot(x = 268f / 390f, y = 96f / 844f, rotation = 15f),
    PawSpot(x = 62f / 390f, y = 610f / 844f, rotation = 15f),
    PawSpot(x = 330f / 390f, y = 190f / 844f, rotation = -10f),
    PawSpot(x = 250f / 390f, y = 730f / 844f, rotation = 8f),
    PawSpot(x = 96f / 390f, y = 196f / 844f, rotation = 25f),
    PawSpot(x = 336f / 390f, y = 706f / 844f, rotation = -22f),
    PawSpot(x = 214f / 390f, y = 232f / 844f, rotation = -5f),
    PawSpot(x = 176f / 390f, y = 646f / 844f, rotation = -18f),
)
