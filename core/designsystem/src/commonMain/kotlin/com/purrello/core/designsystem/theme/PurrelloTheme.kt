package com.purrello.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalPurrColors = staticCompositionLocalOf { LightPurrColors }
private val LocalPurrTypography = staticCompositionLocalOf { DefaultPurrTypography }
private val LocalPurrSpacing = staticCompositionLocalOf { PurrSpacing() }
private val LocalPurrShapes = staticCompositionLocalOf { PurrShapes() }
private val LocalPurrSizes = staticCompositionLocalOf { PurrSizes() }

@Composable
fun PurrelloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkPurrColors else LightPurrColors
    // Material scheme only so M3 internals used by DS components pick matching colors.
    val materialScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.actionPrimary, onPrimary = colors.onActionPrimary,
            background = colors.bgPage, onBackground = colors.textPrimary,
            surface = colors.bgSurface, onSurface = colors.textPrimary,
            surfaceVariant = colors.bgSubtle, onSurfaceVariant = colors.textSecondary,
            error = colors.danger, outline = colors.border,
        )
    } else {
        lightColorScheme(
            primary = colors.actionPrimary, onPrimary = colors.onActionPrimary,
            background = colors.bgPage, onBackground = colors.textPrimary,
            surface = colors.bgSurface, onSurface = colors.textPrimary,
            surfaceVariant = colors.bgSubtle, onSurfaceVariant = colors.textSecondary,
            error = colors.danger, outline = colors.border,
        )
    }
    CompositionLocalProvider(
        LocalPurrColors provides colors,
        LocalPurrTypography provides DefaultPurrTypography,
        LocalPurrSpacing provides PurrSpacing(),
        LocalPurrShapes provides PurrShapes(),
        LocalPurrSizes provides PurrSizes(),
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
}
