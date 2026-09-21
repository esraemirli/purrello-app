package com.purrello.core.designsystem.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purrello.core.designsystem.component.PurrAppHeader
import com.purrello.core.designsystem.component.PurrButton
import com.purrello.core.designsystem.component.PurrButtonStyle
import com.purrello.core.designsystem.component.PurrErrorState
import com.purrello.core.designsystem.theme.PurrelloTheme

// Preview-only sample copy lives in *Previews.kt files (excluded from the hardcoded-string check).

@PurrPreviews
@Composable
private fun PurrButtonPreview() {
    PurrelloTheme {
        Column(
            modifier = Modifier.padding(PurrelloTheme.spacing.space4),
            verticalArrangement = Arrangement.spacedBy(PurrelloTheme.spacing.space2),
        ) {
            PurrButton(text = "Kaydet", onClick = {})
            PurrButton(text = "Vazgeç", onClick = {}, style = PurrButtonStyle.SECONDARY)
            PurrButton(text = "Peti sil", onClick = {}, style = PurrButtonStyle.DANGER)
            PurrButton(text = "Kaydet", onClick = {}, loading = true)
        }
    }
}

@PurrPreviews
@Composable
private fun PurrAppHeaderPreview() {
    PurrelloTheme {
        PurrAppHeader(title = "Sağlık", subtitle = "Boncuk", onAddClick = {})
    }
}

@PurrPreviews
@Composable
private fun PurrErrorStatePreview() {
    PurrelloTheme {
        PurrErrorState(title = "Bağlantı yok", body = "İnternet bağlantını kontrol et.", retryText = "Tekrar dene", onRetry = {})
    }
}
