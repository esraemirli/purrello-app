package com.purrello.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale from `tokens.json` (spacing.tokens); 4 px grid. */
@Immutable
data class PurrSpacing(
    /** Icon-to-text in tight spots, badge padding. */
    val space1: Dp = 4.dp,
    /** Gap between chips, icon-to-label in buttons, label-to-field gap. */
    val space2: Dp = 8.dp,
    /** Inner gap in inputs (icon ↔ text), list item vertical padding. */
    val space3: Dp = 12.dp,
    /** Card padding, input horizontal padding, stack gap between form fields. */
    val space4: Dp = 16.dp,
    /** Chip row side inset when scrolled. */
    val space5: Dp = 20.dp,
    /** Screen side gutter, primary button horizontal padding, section gap. */
    val space6: Dp = 24.dp,
    /** Gap between screen sections. */
    val space8: Dp = 32.dp,
    /** Top of content under a hero. */
    val space10: Dp = 40.dp,
    /** Empty-state vertical padding. */
    val space12: Dp = 48.dp,
) {
    /** Screen side gutter (= [space6]). */
    val screenEdge: Dp get() = space6
}

/** Corner radii from `tokens.json` (radius.tokens), plus the two header shapes from the navigation spec. */
@Immutable
data class PurrShapes(
    /** Buttons, chips, avatars, progress bars. */
    val pill: Shape = RoundedCornerShape(percent = 50),
    /** File-type tiles, menu items, small badges. */
    val sm: Shape = RoundedCornerShape(8.dp),
    /** Text fields, dropdown triggers, menus. */
    val input: Shape = RoundedCornerShape(12.dp),
    /** Cards, upload area, file rows. */
    val card: Shape = RoundedCornerShape(16.dp),
    /** Bottom sheet top corners, hero cards. */
    val sheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    /** Warm tab-root header, expanded. */
    val heroHeader: Shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
    /** Warm tab-root header, collapsed on scroll. */
    val compactHeader: Shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
)

/** Heights, touch targets and icon sizes from `tokens.json` (size.tokens). px = iOS pt = Android dp. */
@Immutable
data class PurrSizes(
    /** Primary/secondary buttons, text fields, dropdown triggers. */
    val controlLg: Dp = 52.dp,
    /** Medium buttons, menu items — the minimum touch target. */
    val controlMd: Dp = 44.dp,
    /** Small buttons and chips (hit area padded to 44). */
    val controlSm: Dp = 36.dp,
    /** Default icon size. */
    val iconMd: Dp = 24.dp,
    /** Icons inside inputs, chips and buttons. */
    val iconSm: Dp = 20.dp,

    // Layout sizes from the navigation spec (docs/product/app-navigation.md).
    val compactHeader: Dp = 56.dp,
    val heroAddButton: Dp = 48.dp,
    val tabIndicatorWidth: Dp = 56.dp,
    val tabIndicatorHeight: Dp = 32.dp,
    val avatarSm: Dp = 24.dp,
    val avatarMd: Dp = 40.dp,
) {
    /** Minimum touch target (= [controlMd]). */
    val minTouch: Dp get() = controlMd
}

/** Opacity tokens from `tokens.json`. Disabled *text* uses `textDisabled`, not opacity. */
@Immutable
data class PurrOpacity(
    /** Disabled images and illustrations. */
    val disabled: Float = 0.48f,
)
