package com.purrello.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.purrello.core.designsystem.resources.Res
import com.purrello.core.designsystem.resources.poppins_light
import com.purrello.core.designsystem.resources.poppins_medium
import com.purrello.core.designsystem.resources.poppins_regular
import com.purrello.core.designsystem.resources.poppins_semibold
import org.jetbrains.compose.resources.Font

/**
 * Type scale, generated from `core/designsystem/tokens.json` (type.groups).
 * Poppins, bundled under `composeResources/font` (SIL Open Font License, see font/OFL.txt).
 */
@Immutable
data class PurrTypography(
    /** Logotype on bg-brand only. Uppercase, Light 300, tracking 0.3em. */
    val wordmark: TextStyle,
    /** One per screen: onboarding/login greeting, big empty states. */
    val display: TextStyle,
    /** Screen title (large app bar). */
    val title1: TextStyle,
    /** Section headers inside a screen, bottom sheet titles. */
    val title2: TextStyle,
    /** Card titles, list item primary line, dialog titles. */
    val title3: TextStyle,
    /** Default reading text, input values (16px stops iOS zoom). */
    val bodyLg: TextStyle,
    /** Dense lists, card descriptions, menu items. */
    val body: TextStyle,
    /** Secondary lines in list items, metadata. */
    val bodySm: TextStyle,
    /** Buttons lg/md. Sentence case, never uppercase. */
    val button: TextStyle,
    /** Form field labels, chip text, tabs, small buttons. */
    val label: TextStyle,
    /** Helper/error text, timestamps, file meta. Minimum size for readable text. */
    val caption: TextStyle,
    /** Bottom tab bar labels and badge counts only. */
    val micro: TextStyle,
    /** Weight, key stats, expense totals. Turkish formatting: 12.400,50 ₺. */
    val amount: TextStyle,
)

@Composable
internal fun poppinsFamily(): FontFamily = FontFamily(
    Font(Res.font.poppins_light, FontWeight.Light),
    Font(Res.font.poppins_regular, FontWeight.Normal),
    Font(Res.font.poppins_medium, FontWeight.Medium),
    Font(Res.font.poppins_semibold, FontWeight.SemiBold),
)

internal fun purrTypography(family: FontFamily): PurrTypography {
    fun style(
        size: Int,
        lineHeight: Int,
        weight: FontWeight = FontWeight.Normal,
        letterSpacing: Double = 0.0,
    ) = TextStyle(
        fontFamily = family,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
        letterSpacing = letterSpacing.em,
    )

    return PurrTypography(
        wordmark = style(40, 48, FontWeight.Light, 0.3),
        display = style(32, 40, FontWeight.Medium, -0.01),
        title1 = style(24, 32, FontWeight.SemiBold),
        title2 = style(20, 28, FontWeight.SemiBold),
        title3 = style(17, 24, FontWeight.SemiBold),
        bodyLg = style(16, 24),
        body = style(15, 22),
        bodySm = style(14, 20),
        button = style(16, 24, FontWeight.Medium),
        label = style(14, 20, FontWeight.Medium),
        caption = style(12, 16),
        micro = style(11, 14, FontWeight.Medium, 0.01),
        amount = style(28, 36, FontWeight.SemiBold, -0.01),
    )
}
