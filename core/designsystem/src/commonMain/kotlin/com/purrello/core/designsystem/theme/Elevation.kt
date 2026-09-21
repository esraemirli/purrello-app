package com.purrello.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shadow tokens from `tokens.json` (shadow.tokens). CSS shadows don't translate 1:1 to Compose, so each
 * token keeps the values Compose needs: blur radius as elevation plus the ambient/spot color to tint it.
 */
@Immutable
data class PurrShadow(
    val elevation: Dp,
    val color: Color,
)

@Immutable
data class PurrElevation(
    val sm: PurrShadow,
    val md: PurrShadow,
    val lg: PurrShadow,
)

internal val LightPurrElevation = PurrElevation(
    sm = PurrShadow(elevation = 3.dp, color = Color(0x142F3B4C)),   // rgba(47,59,76,0.08)
    md = PurrShadow(elevation = 12.dp, color = Color(0x1F2F3B4C)),  // rgba(47,59,76,0.12)
    lg = PurrShadow(elevation = 16.dp, color = Color(0x292F3B4C)),  // rgba(47,59,76,0.16)
)

internal val DarkPurrElevation = PurrElevation(
    sm = PurrShadow(elevation = 3.dp, color = Color(0x66000000)),
    md = PurrShadow(elevation = 12.dp, color = Color(0x80000000)),
    lg = PurrShadow(elevation = 16.dp, color = Color(0x99000000)),
)
