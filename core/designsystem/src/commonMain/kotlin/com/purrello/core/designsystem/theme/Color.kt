package com.purrello.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens, generated from `core/designsystem/tokens.json` (version 4), the in-repo copy of
 * the design system artifact's `project/tokens.json`.
 *
 * Features use these names only — never a palette step and never a raw hex (`ui-conventions.md §1`).
 * Each token's doc carries the intended use and the contrast ratio the design system verified.
 */
@Immutable
data class PurrColors(
    /** Screen background. */
    val bgPage: Color,
    /** Cards, inputs, secondary buttons, menus, sheets. */
    val bgSurface: Color,
    /** Disabled controls, skeletons, progress track, file-type tiles. */
    val bgSubtle: Color,
    /** Brand hero: splash, login, onboarding, empty states. Never behind body text. */
    val bgBrand: Color,
    /** Selected chip, tonal button, active tab pill, icon circles, active drop zone. */
    val bgBrandSoft: Color,
    /** Overlay behind bottom sheets and dialogs. */
    val scrim: Color,

    /** Headings, body, input values (10.8:1 light / 16.1:1 dark). */
    val textPrimary: Color,
    /** Supporting copy, helper text, placeholders, icons (4.6:1 / 7.2:1). */
    val textSecondary: Color,
    /** Disabled labels only — never for readable information. */
    val textDisabled: Color,
    /** Inline links, ghost buttons, active icons (5.6:1 / 8.1:1). */
    val textLink: Color,
    /** The PURRELLO wordmark on [bgBrand] — logotype only (2.3:1 light), never body text. */
    val textOnBrand: Color,
    /** Text and icons on [bgBrandSoft]: selected chips, tonal buttons, active tab icon (7.4:1 / 6.8:1). */
    val textOnBrandSoft: Color,

    /** Paw pattern on [bgBrand] — a lighter tone of the same coral. Decoration only. */
    val pawPrint: Color,
    /**
     * Warm tab-root header (AppHeader), passport card, lost-alert photo area.
     * Dark is the same coral-dusk as the splash/login hero, so the brand color carries into the app
     * instead of turning brown.
     */
    val bgHero: Color,
    /** Decorative paw prints inside [bgHero] headers and the passport card. */
    val heroPaw: Color,
    /** Title and icons on [bgHero] (9.0:1 light / 5.2:1 dark). */
    val heroText1: Color,
    /** Second line on [bgHero] (6.4:1 / 5.2:1). Hierarchy comes from size and weight, never opacity. */
    val heroText2: Color,

    /** Coach mark (one-time tip) bubble. */
    val bgInverse: Color,
    /** Text on [bgInverse] (11.3:1 / 16.1:1). */
    val textOnInverse: Color,
    /** Action link on [bgInverse], e.g. "Anladım" (7.1:1 / 7.0:1). */
    val linkOnInverse: Color,

    /** Primary button fill, selected radio/checkbox, progress fill, selected chip border. */
    val actionPrimary: Color,
    /** Primary button pressed / hover. */
    val actionPrimaryPressed: Color,
    /** Label and icon on [actionPrimary] (4.6:1 / 8.6:1). */
    val onActionPrimary: Color,

    /** Dividers, card outlines, menu outline. Decorative only. */
    val borderSubtle: Color,
    /** Input, dropdown, chip and secondary button borders (≥3:1). */
    val borderControl: Color,
    /** 2px focus outline with 2px offset on every interactive element. */
    val focusRing: Color,

    /** Done vaccinations, completed uploads, healthy status. Always with an icon, never color alone. */
    val success: Color,
    /** Success badge / banner background. */
    val successSoft: Color,
    /** Upcoming vaccination, expiring certificate, near-full storage. */
    val warning: Color,
    /** Warning badge / banner background. */
    val warningSoft: Color,
    /** Overdue vaccination, field errors, failed upload, destructive actions. Always with an icon. */
    val danger: Color,
    /** Danger badge, destructive (tonal) button, error banner background. */
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
