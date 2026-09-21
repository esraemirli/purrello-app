package com.purrello.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily

private val LocalPurrColors = staticCompositionLocalOf { LightPurrColors }
private val LocalPurrTypography = staticCompositionLocalOf { purrTypography(FontFamily.Default) }
private val LocalPurrSpacing = staticCompositionLocalOf { PurrSpacing() }
private val LocalPurrShapes = staticCompositionLocalOf { PurrShapes() }
private val LocalPurrSizes = staticCompositionLocalOf { PurrSizes() }
private val LocalPurrOpacity = staticCompositionLocalOf { PurrOpacity() }
private val LocalPurrElevation = staticCompositionLocalOf { LightPurrElevation }

@Composable
fun PurrelloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkPurrColors else LightPurrColors
    val elevation = if (darkTheme) DarkPurrElevation else LightPurrElevation
    // Font() loads its bytes asynchronously: the first composition gets a placeholder family and a new
    // one arrives once Poppins is loaded, so the scale must be keyed on `family` — otherwise the
    // placeholder is cached forever and the app never switches to Poppins. Font() remembers its own
    // result, so an unchanged family is equal across recompositions and the scale is rebuilt only once.
    val family = poppinsFamily()
    val typography = remember(family) { purrTypography(family) }

    // Material 3 scheme only so the M3 internals used inside DS components pick matching colors.
    val materialScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.actionPrimary, onPrimary = colors.onActionPrimary,
            background = colors.bgPage, onBackground = colors.textPrimary,
            surface = colors.bgSurface, onSurface = colors.textPrimary,
            surfaceVariant = colors.bgSubtle, onSurfaceVariant = colors.textSecondary,
            error = colors.danger, onError = colors.onActionPrimary, outline = colors.borderControl,
            outlineVariant = colors.borderSubtle, scrim = colors.scrim,
        )
    } else {
        lightColorScheme(
            primary = colors.actionPrimary, onPrimary = colors.onActionPrimary,
            background = colors.bgPage, onBackground = colors.textPrimary,
            surface = colors.bgSurface, onSurface = colors.textPrimary,
            surfaceVariant = colors.bgSubtle, onSurfaceVariant = colors.textSecondary,
            error = colors.danger, onError = colors.onActionPrimary, outline = colors.borderControl,
            outlineVariant = colors.borderSubtle, scrim = colors.scrim,
        )
    }

    CompositionLocalProvider(
        LocalPurrColors provides colors,
        LocalPurrTypography provides typography,
        LocalPurrSpacing provides PurrSpacing(),
        LocalPurrShapes provides PurrShapes(),
        LocalPurrSizes provides PurrSizes(),
        LocalPurrOpacity provides PurrOpacity(),
        LocalPurrElevation provides elevation,
    ) {
        MaterialTheme(colorScheme = materialScheme, content = content)
    }
}

object PurrelloTheme {
    val colors: PurrColors @Composable @ReadOnlyComposable get() = LocalPurrColors.current
    val typography: PurrTypography @Composable @ReadOnlyComposable get() = LocalPurrTypography.current
    val spacing: PurrSpacing @Composable @ReadOnlyComposable get() = LocalPurrSpacing.current
    val shapes: PurrShapes @Composable @ReadOnlyComposable get() = LocalPurrShapes.current
    val sizes: PurrSizes @Composable @ReadOnlyComposable get() = LocalPurrSizes.current
    val opacity: PurrOpacity @Composable @ReadOnlyComposable get() = LocalPurrOpacity.current
    val elevation: PurrElevation @Composable @ReadOnlyComposable get() = LocalPurrElevation.current
}
