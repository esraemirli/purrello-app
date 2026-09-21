package com.purrello.feature.pet.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.ui.component.ComingSoonContent
import com.purrello.core.ui.component.TabRootScaffold
import com.purrello.feature.pet.resources.Res
import com.purrello.feature.pet.resources.pet_tab_subtitle
import com.purrello.feature.pet.resources.pet_tab_title
import org.jetbrains.compose.resources.stringResource

/**
 * Root of the "Pet profili" tab, rendered by the Main host (navigation.md §1).
 * TODO: contract (docs/api/pet/…) → PetViewModel + PetScreen via /create-screen.
 */
@Composable
fun PetTabRoot(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRootScaffold(
        title = stringResource(Res.string.pet_tab_title),
        subtitle = stringResource(Res.string.pet_tab_subtitle),
        onOpenProfile = onOpenProfile,
        modifier = modifier,
    ) {
        ComingSoonContent()
    }
}
