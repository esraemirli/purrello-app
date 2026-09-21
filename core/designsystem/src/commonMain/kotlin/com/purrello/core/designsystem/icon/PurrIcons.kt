package com.purrello.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

/** Single entry point for icons. TODO(ds): swap to Phosphor Regular without touching call sites. */
object PurrIcons {
    val Home: ImageVector get() = Icons.Outlined.Home
    val Health: ImageVector get() = Icons.Outlined.Favorite
    val Documents: ImageVector get() = Icons.Outlined.DateRange
    val Care: ImageVector get() = Icons.Outlined.Build
    val Pet: ImageVector get() = Icons.Outlined.Person
    val Add: ImageVector get() = Icons.Outlined.Add
    val Back: ImageVector get() = Icons.AutoMirrored.Outlined.ArrowBack
    val Close: ImageVector get() = Icons.Outlined.Close
    val Info: ImageVector get() = Icons.Outlined.Info
    val Warning: ImageVector get() = Icons.Outlined.Warning
}
