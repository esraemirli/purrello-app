package com.purrello.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Semantic color tokens — docs/product/design-system.md. Features use these, never hex values. */
@Immutable
data class PurrColors(
    val bgPage: Color,
    val bgSurface: Color,
    val bgSubtle: Color,
    val bgBrand: Color,
    val bgBrandSoft: Color,
    val bgHero: Color,
    val heroText2: Color,
    val heroPaw: Color,
    val bgInverse: Color,
    val onInverse: Color,
    val actionPrimary: Color,
    val onActionPrimary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val pawPrint: Color,
    val border: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val isDark: Boolean,
)

internal val LightPurrColors = PurrColors(
    bgPage = Color(0xFFF7F9FB),
    bgSurface = Color(0xFFFFFFFF),
    bgSubtle = Color(0xFFEEF2F6),
    bgBrand = Color(0xFFF08F8A),
    bgBrandSoft = Color(0xFFFEF1F0),
    bgHero = Color(0xFFFCDFDC),
    heroText2 = Color(0xFF7B3F3A),
    heroPaw = Color(0xFFF8C2BE),
    bgInverse = Color(0xFF2F3B4C),
    onInverse = Color(0xFFF3EEEC),
    actionPrimary = Color(0xFFC94A43),
    onActionPrimary = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF2F3B4C),
    textSecondary = Color(0xFF5F6F84),
    pawPrint = Color(0xFFF5ABA7),
    // TODO(ds): border + status colors are not specified in the DS artifact yet — confirm values.
    border = Color(0xFFE1E6EC),
    success = Color(0xFF2E7D5B),
    warning = Color(0xFFB26A00),
    danger = Color(0xFFC0392B),
    isDark = false,
)

internal val DarkPurrColors = PurrColors(
    bgPage = Color(0xFF171213),
    bgSurface = Color(0xFF211B1B),
    bgSubtle = Color(0xFF2B2423),
    bgBrand = Color(0xFFA85550),
    bgBrandSoft = Color(0xFF3A2524),
    bgHero = Color(0xFF3A2524),
    heroText2 = Color(0xFFD9B5B1),
    heroPaw = Color(0xFF4A2E2C),
    bgInverse = Color(0xFFF3EEEC),
    onInverse = Color(0xFF2F3B4C),
    actionPrimary = Color(0xFFF4A29D),
    onActionPrimary = Color(0xFF171213),
    textPrimary = Color(0xFFF3EEEC),
    textSecondary = Color(0xFFB3A8A5),
    pawPrint = Color(0xFFB86A65),
    border = Color(0xFF3A3230),
    success = Color(0xFF7FD1A8),
    warning = Color(0xFFF2B866),
    danger = Color(0xFFF28B82),
    isDark = true,
)
