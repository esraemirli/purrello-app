package com.purrello.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale from `tokens.json` (spacing.tokens); 4 px grid. */
@Immutable
data class PurrSpacing(
    val space1: Dp = 4.dp,
    val space2: Dp = 8.dp,
    val space3: Dp = 12.dp,
    val space4: Dp = 16.dp,
    val space5: Dp = 20.dp,
    val space6: Dp = 24.dp,
    val space8: Dp = 32.dp,
    val space10: Dp = 40.dp,
    val space12: Dp = 48.dp,
) {
    val screenEdge: Dp get() = space6
}

/** Corner radii from `tokens.json` (radius.tokens), plus the two header shapes from the navigation spec. */
@Immutable
data class PurrShapes(
    val pill: Shape = RoundedCornerShape(percent = 50),
    val sm: Shape = RoundedCornerShape(8.dp),
    val input: Shape = RoundedCornerShape(12.dp),
    val card: Shape = RoundedCornerShape(16.dp),
    val sheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val heroHeader: Shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
    val compactHeader: Shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
)

/** Heights, touch targets and icon sizes from `tokens.json` (size.tokens). px = iOS pt = Android dp. */
@Immutable
data class PurrSizes(
    val controlLg: Dp = 52.dp,
    val controlMd: Dp = 44.dp,
    val controlSm: Dp = 36.dp,
    val iconMd: Dp = 24.dp,
    val iconSm: Dp = 20.dp,

    // Layout sizes from the navigation spec (docs/product/app-navigation.md).
    val compactHeader: Dp = 56.dp,
    val heroAddButton: Dp = 48.dp,
    val tabIndicatorWidth: Dp = 56.dp,
    val tabIndicatorHeight: Dp = 32.dp,
    val avatarSm: Dp = 24.dp,
    val avatarMd: Dp = 40.dp,
) {
    val minTouch: Dp get() = controlMd
}

/** Opacity tokens from `tokens.json`. Disabled *text* uses `textDisabled`, not opacity. */
@Immutable
data class PurrOpacity(
    val disabled: Float = 0.48f,
)
