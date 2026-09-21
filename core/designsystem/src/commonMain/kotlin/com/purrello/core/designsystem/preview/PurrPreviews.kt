package com.purrello.core.designsystem.preview

import androidx.compose.ui.tooling.preview.Preview

/** Light + dark + large font. Use on every *Screen and non-trivial component (ui-conventions.md §6). */
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)            // Configuration.UI_MODE_NIGHT_YES
@Preview(name = "Large font", fontScale = 1.5f)
annotation class PurrPreviews
