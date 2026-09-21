package com.purrello.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens, generated from `core/designsystem/tokens.json` (version 4), the in-repo copy of
 * the design system artifact's `project/tokens.json` — which is also where each token's intended use and
 * verified contrast ratio are written down.
 *
 * Features use these names only: never a palette step, never a raw hex (`ui-conventions.md §1`).
 */
@Immutable
data class PurrColors(
    val bgPage: Color,
    val bgSurface: Color,
    val bgSubtle: Color,
    /** Brand hero: splash, login, onboarding. Only the wordmark may sit on it — 2.3:1 for anything else. */
    val bgBrand: Color,
    val bgBrandSoft: Color,
    val scrim: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    /** Disabled labels only — never for information the user has to read. */
    val textDisabled: Color,
    val textLink: Color,
    val textOnBrand: Color,
    val textOnBrandSoft: Color,

    val pawPrint: Color,
    /** Warm tab-root header, passport card, lost-alert photo. Dark is the splash's coral-dusk. */
    val bgHero: Color,
    val heroPaw: Color,
    val heroText1: Color,
    /** Second line on [bgHero]. Hierarchy comes from size and weight, never from opacity. */
    val heroText2: Color,

    /** Coach mark (one-time tip) bubble. */
    val bgInverse: Color,
    val textOnInverse: Color,
    val linkOnInverse: Color,

    val actionPrimary: Color,
    val actionPrimaryPressed: Color,
    val onActionPrimary: Color,

    val borderSubtle: Color,
    val borderControl: Color,
    /** 2px outline with a 2px offset, on every interactive element. */
    val focusRing: Color,

    // Status colors always travel with an icon: color alone never carries the meaning.
    val success: Color,
    val successSoft: Color,
    val warning: Color,
    val warningSoft: Color,
    val danger: Color,
    val dangerSoft: Color,
)

// --- Palette steps (tokens.json → color.tokens). Only PurrColors and the DS itself use these. ---

private val Coral50 = Color(0xFFFEF1F0)
private val Coral100 = Color(0xFFFCDFDC)
private val Coral200 = Color(0xFFF8C2BE)
private val Coral300 = Color(0xFFF4A29D)
private val Coral400 = Color(0xFFF08F8A)   // THE brand coral
private val Coral600 = Color(0xFFC94A43)
private val Coral700 = Color(0xFFB53F39)
private val Coral800 = Color(0xFF8E2F2A)
private val CoralDusk = Color(0xFFA85550)  // dark-theme brand coral

private val Slate0 = Color(0xFFFFFFFF)
private val Slate25 = Color(0xFFF7F9FB)
private val Slate50 = Color(0xFFEEF2F6)
private val Slate100 = Color(0xFFE2E7EE)
private val Slate300 = Color(0xFFA6B0BE)
private val Slate400 = Color(0xFF8591A3)
private val Slate500 = Color(0xFF5F6F84)
private val Slate800 = Color(0xFF2F3B4C)

private val Night900 = Color(0xFF171213)
private val Night800 = Color(0xFF211B1B)
private val Night700 = Color(0xFF2B2423)
private val Night600 = Color(0xFF3A3130)

internal val LightPurrColors = PurrColors(
    bgPage = Slate25,
    bgSurface = Slate0,
    bgSubtle = Slate50,
    bgBrand = Coral400,
    bgBrandSoft = Coral50,
    scrim = Color(0x7A14171C),                 // rgba(20,23,28,0.48)
    textPrimary = Slate800,
    textSecondary = Slate500,
    textDisabled = Slate300,
    textLink = Coral700,
    textOnBrand = Color(0xFFFFFFFF),
    textOnBrandSoft = Coral800,
    pawPrint = Color(0xFFF5ABA7),
    bgHero = Coral100,
    heroPaw = Coral200,
    heroText1 = Slate800,
    heroText2 = Color(0xFF7B3F3A),
    bgInverse = Slate800,
    textOnInverse = Color(0xFFFFFFFF),
    linkOnInverse = Coral200,
    actionPrimary = Coral600,
    actionPrimaryPressed = Coral700,
    onActionPrimary = Color(0xFFFFFFFF),
    borderSubtle = Slate100,
    borderControl = Slate400,
    focusRing = Coral700,
    success = Color(0xFF1E7A55),
    successSoft = Color(0xFFE8F5EE),
    warning = Color(0xFF9A5B00),
    warningSoft = Color(0xFFFFF4E0),
    danger = Color(0xFFC0262D),
    dangerSoft = Color(0xFFFDECEC),
)

internal val DarkPurrColors = PurrColors(
    bgPage = Night900,
    bgSurface = Night800,
    bgSubtle = Night700,
    bgBrand = CoralDusk,
    bgBrandSoft = Color(0xFF8E4541),          // coral-dusk one step deeper: selection reads as coral, not brown
    scrim = Color(0xA3000000),                 // rgba(0,0,0,0.64)
    textPrimary = Color(0xFFF3EEEC),
    textSecondary = Color(0xFFB3A8A5),
    textDisabled = Color(0xFF625856),
    textLink = Coral300,
    textOnBrand = Color(0xFFFFFFFF),
    textOnBrandSoft = Color(0xFFFFFFFF),
    pawPrint = Color(0xFFB86A65),
    bgHero = CoralDusk,                       // same hero coral as splash/login
    heroPaw = Color(0xFFC07A75),
    heroText1 = Color(0xFFFFFFFF),
    heroText2 = Color(0xFFFFFFFF),
    bgInverse = Color(0xFFF3EEEC),
    textOnInverse = Night900,
    linkOnInverse = Coral800,
    actionPrimary = Coral300,
    actionPrimaryPressed = Coral200,
    onActionPrimary = Color(0xFF2A1614),
    borderSubtle = Night600,
    borderControl = Color(0xFF7A6F6C),
    focusRing = Coral300,
    success = Color(0xFF5FD19C),
    successSoft = Color(0xFF183A2C),
    warning = Color(0xFFF6B85A),
    warningSoft = Color(0xFF3A2E17),
    danger = Color(0xFFFF8A80),
    dangerSoft = Color(0xFF3A2224),
)
