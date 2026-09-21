package com.purrello.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.purrello.core.designsystem.theme.PurrelloTheme
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PurrTabItem(
    val label: String,
    val icon: ImageVector,
)

/**
 * 5-tab bar. Active tab: 56×32 bg-brand-soft pill behind the icon, label in action color.
 * TODO(ds): pet tab avatar with 2 px action ring, long-press + "Pet değiştir" accessibility action.
 */
@Composable
fun PurrTabBar(
    items: ImmutableList<PurrTabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = PurrelloTheme.colors
    Surface(color = colors.bgSurface, modifier = modifier.fillMaxWidth()) {
        Column {
            HorizontalDivider(color = colors.border)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(vertical = PurrelloTheme.spacing.sm),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(selected = selected, role = Role.Tab, onClick = { onSelect(index) }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(PurrelloTheme.sizes.tabIndicatorWidth, PurrelloTheme.sizes.tabIndicatorHeight)
                                .background(
                                    color = if (selected) colors.bgBrandSoft else colors.bgSurface,
                                    shape = PurrelloTheme.shapes.pill,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,   // the label below is the accessible name
                                tint = if (selected) colors.actionPrimary else colors.textSecondary,
                            )
                        }
                        Text(
                            text = item.label,
                            style = PurrelloTheme.typography.micro,
                            color = if (selected) colors.actionPrimary else colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
