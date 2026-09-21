package com.purrello.shared.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.purrello.core.designsystem.component.PurrTabBar
import com.purrello.core.designsystem.component.PurrTabItem
import com.purrello.core.designsystem.icon.PurrIcons
import com.purrello.core.designsystem.theme.PurrelloTheme
import com.purrello.core.navigation.MainTab
import com.purrello.core.navigation.OwnerProfile
import com.purrello.feature.care.presentation.CareTabRoot
import com.purrello.feature.documents.presentation.DocumentsTabRoot
import com.purrello.feature.health.presentation.HealthTabRoot
import com.purrello.feature.home.presentation.HomeTabRoot
import com.purrello.feature.pet.presentation.PetTabRoot
import com.purrello.shared.navigation.AppNavigator
import com.purrello.shared.resources.Res
import com.purrello.shared.resources.main_tab_care
import com.purrello.shared.resources.main_tab_documents
import com.purrello.shared.resources.main_tab_health
import com.purrello.shared.resources.main_tab_home
import com.purrello.shared.resources.main_tab_pet_fallback
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * Tab host. Each tab root keeps its UI state (scroll etc.) in a SaveableStateHolder keyed by tab.
 * TODO(nav): pet tab = selected pet's name + avatar, long-press → pet switcher sheet (+ haptic),
 * re-tap active tab → scroll to top.
 */
@Composable
fun MainScreen(
    navigator: AppNavigator,
    modifier: Modifier = Modifier,
) {
    val stateHolder = rememberSaveableStateHolder()
    val selected = navigator.selectedTab
    val openProfile = { navigator.navigate(OwnerProfile) }
    val tabs = persistentListOf(
        PurrTabItem(stringResource(Res.string.main_tab_home), PurrIcons.Home),
        PurrTabItem(stringResource(Res.string.main_tab_health), PurrIcons.Health),
        PurrTabItem(stringResource(Res.string.main_tab_documents), PurrIcons.Documents),
        PurrTabItem(stringResource(Res.string.main_tab_care), PurrIcons.Care),
        PurrTabItem(stringResource(Res.string.main_tab_pet_fallback), PurrIcons.Pet),
    )

    Column(modifier = modifier.fillMaxSize().background(PurrelloTheme.colors.bgPage)) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            stateHolder.SaveableStateProvider(selected.name) {
                when (selected) {
                    MainTab.HOME -> HomeTabRoot(onOpenProfile = openProfile)
                    MainTab.HEALTH -> HealthTabRoot(onOpenProfile = openProfile)
                    MainTab.DOCUMENTS -> DocumentsTabRoot(onOpenProfile = openProfile)
                    MainTab.CARE -> CareTabRoot(onOpenProfile = openProfile)
                    MainTab.PET -> PetTabRoot(onOpenProfile = openProfile)
                }
            }
        }
        PurrTabBar(
            items = tabs,
            selectedIndex = selected.ordinal,
            onSelect = { index -> navigator.selectTab(MainTab.entries[index]) },
        )
    }
}
