package com.purrello.core.ui.error

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.purrello.core.common.AppError
import com.purrello.core.designsystem.component.PurrButtonStyle
import com.purrello.core.designsystem.component.PurrDialog
import com.purrello.core.designsystem.component.PurrDialogAction
import com.purrello.core.designsystem.component.PurrErrorState
import com.purrello.core.ui.resources.Res
import com.purrello.core.ui.resources.common_action_close
import com.purrello.core.ui.resources.common_action_contact_support
import com.purrello.core.ui.resources.common_action_retry
import com.purrello.core.ui.resources.error_code_footnote
import com.purrello.core.ui.resources.error_generic_body
import com.purrello.core.ui.resources.error_generic_title
import com.purrello.core.ui.resources.error_no_connection_body
import com.purrello.core.ui.resources.error_no_connection_title
import com.purrello.core.ui.resources.error_timeout_body
import com.purrello.core.ui.resources.error_timeout_title
import org.jetbrains.compose.resources.stringResource

@Immutable
data class ErrorText(val title: String, val body: String, val footnote: String?)

/**
 * Localized copy for an [AppError]. Features that know specific backend codes pass [bodyOverride]
 * (e.g. `error_pet_not_found` from their own resources); backend `message` is never shown.
 */
@Composable
fun errorText(error: AppError, bodyOverride: String? = null): ErrorText = when (error) {
    AppError.NoConnection -> ErrorText(
        title = stringResource(Res.string.error_no_connection_title),
        body = bodyOverride ?: stringResource(Res.string.error_no_connection_body),
        footnote = null,
    )
    AppError.Timeout -> ErrorText(
        title = stringResource(Res.string.error_timeout_title),
        body = bodyOverride ?: stringResource(Res.string.error_timeout_body),
        footnote = null,
    )
    is AppError.Server -> ErrorText(
        title = stringResource(Res.string.error_generic_title),
        body = bodyOverride ?: stringResource(Res.string.error_generic_body),
        footnote = supportReference(error.code, error.traceId)?.let { stringResource(Res.string.error_code_footnote, it) },
    )
    is AppError.Validation, AppError.Unauthorized, is AppError.Unknown -> ErrorText(
        title = stringResource(Res.string.error_generic_title),
        body = bodyOverride ?: stringResource(Res.string.error_generic_body),
        footnote = null,
    )
}

private fun supportReference(code: String?, traceId: String?): String? =
    listOfNotNull(code, traceId).takeIf { it.isNotEmpty() }?.joinToString(" · ")

/** Full-screen error for a failed load with nothing on screen (`Async.Failure(previous = null)`). */
@Composable
fun AppErrorState(
    error: AppError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    bodyOverride: String? = null,
) {
    val text = errorText(error, bodyOverride)
    PurrErrorState(
        title = text.title,
        body = text.body,
        retryText = stringResource(Res.string.common_action_retry),
        onRetry = onRetry,
        modifier = modifier,
    )
}

/**
 * System-error popup while content is visible (ui-conventions.md §3): "Tekrar dene" inside the dialog.
 * Connection errors: Tekrar dene / Kapat. Server errors: Tekrar dene / Destekle iletişime geç / Kapat + code.
 * [AppError.Unauthorized] renders nothing — the session layer handles it globally.
 */
@Composable
fun AppErrorDialog(
    error: AppError,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    bodyOverride: String? = null,
    onContactSupport: (() -> Unit)? = null,
) {
    if (error == AppError.Unauthorized) return
    val text = errorText(error, bodyOverride)
    val retry = PurrDialogAction(stringResource(Res.string.common_action_retry), onRetry)
    val close = PurrDialogAction(stringResource(Res.string.common_action_close), onDismiss, PurrButtonStyle.TEXT)
    val support = onContactSupport
        ?.takeIf { error is AppError.Server || error is AppError.Unknown }
        ?.let { PurrDialogAction(stringResource(Res.string.common_action_contact_support), it, PurrButtonStyle.SECONDARY) }
    PurrDialog(
        title = text.title,
        message = text.body,
        primary = retry,
        secondary = support ?: close,
        tertiary = if (support != null) close else null,
        footnote = text.footnote,
        onDismissRequest = onDismiss,
    )
}
