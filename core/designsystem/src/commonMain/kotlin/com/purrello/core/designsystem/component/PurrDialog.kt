package com.purrello.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.purrello.core.designsystem.theme.PurrelloTheme

@Immutable
data class PurrDialogAction(
    val text: String,
    val onClick: () -> Unit,
    val style: PurrButtonStyle = PurrButtonStyle.PRIMARY,
)

/**
 * Alert dialog (role alertdialog). Actions are stacked full width, primary first.
 * Used for system errors ("Tekrar dene" inside), destructive confirmations and discard prompts — never toasts.
 */
@Composable
fun PurrDialog(
    title: String,
    message: String,
    primary: PurrDialogAction,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    secondary: PurrDialogAction? = null,
    tertiary: PurrDialogAction? = null,
    footnote: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        containerColor = PurrelloTheme.colors.bgSurface,
        title = { Text(text = title, style = PurrelloTheme.typography.title2, color = PurrelloTheme.colors.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(PurrelloTheme.spacing.sm)) {
                Text(text = message, style = PurrelloTheme.typography.body, color = PurrelloTheme.colors.textSecondary)
                if (footnote != null) {
                    Text(text = footnote, style = PurrelloTheme.typography.caption, color = PurrelloTheme.colors.textSecondary)
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PurrelloTheme.spacing.sm),
            ) {
                listOfNotNull(primary, secondary, tertiary).forEach { action ->
                    PurrButton(
                        text = action.text,
                        onClick = action.onClick,
                        style = action.style,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
    )
}
