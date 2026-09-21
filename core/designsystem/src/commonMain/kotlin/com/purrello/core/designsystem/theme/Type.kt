package com.purrello.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/** Poppins type scale (size/line height) — docs/product/design-system.md. */
@Immutable
data class PurrTypography(
    val wordmark: TextStyle,
    val display: TextStyle,
    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val bodyLg: TextStyle,
    val body: TextStyle,
    val bodySm: TextStyle,
    val button: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
    val micro: TextStyle,
    val amount: TextStyle,
)

// TODO(ds): add Poppins (300/400/500/600) under composeResources/font and build the family with Font(Res.font.…).
internal val PurrFontFamily: FontFamily = FontFamily.Default

private fun style(size: Int, lineHeight: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = PurrFontFamily,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = weight,
)

internal val DefaultPurrTypography = PurrTypography(
    wordmark = style(40, 48, FontWeight.Light).copy(letterSpacing = 0.3.em),
    display = style(32, 40, FontWeight.SemiBold),
    title1 = style(24, 32, FontWeight.SemiBold),
    title2 = style(20, 28, FontWeight.SemiBold),
    title3 = style(17, 24, FontWeight.Medium),
    bodyLg = style(16, 24),
    body = style(15, 22),
    bodySm = style(14, 20),
    button = style(16, 24, FontWeight.Medium),
    label = style(14, 20, FontWeight.Medium),
    caption = style(12, 16),
    micro = style(11, 14),
    amount = style(28, 36, FontWeight.SemiBold),
)
