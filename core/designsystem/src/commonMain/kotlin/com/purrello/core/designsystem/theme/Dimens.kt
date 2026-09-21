package com.purrello.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 4 px grid; screen edge 24. */
@Immutable
data class PurrSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val screenEdge: Dp = 24.dp,
)

@Immutable
data class PurrShapes(
    val pill: Shape = RoundedCornerShape(percent = 50),
    val input: Shape = RoundedCornerShape(12.dp),
    val card: Shape = RoundedCornerShape(16.dp),
    val sheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val heroHeader: Shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
    val compactHeader: Shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
)

@Immutable
data class PurrSizes(
    val button: Dp = 52.dp,
    val input: Dp = 52.dp,
    val minTouch: Dp = 44.dp,
    val chip: Dp = 36.dp,
    val compactHeader: Dp = 56.dp,
    val heroAddButton: Dp = 48.dp,
    val tabIndicatorWidth: Dp = 56.dp,
    val tabIndicatorHeight: Dp = 32.dp,
    val icon: Dp = 24.dp,
    val avatarSm: Dp = 24.dp,
    val avatarMd: Dp = 40.dp,
)
