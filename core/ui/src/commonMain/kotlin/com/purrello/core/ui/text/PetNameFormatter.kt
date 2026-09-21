package com.purrello.core.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale

/**
 * Possessive form of a pet name for copy like "%1$s aşı ve sağlık takibi" (localization.md §3).
 * Turkish follows vowel harmony with an apostrophe (Boncuk'un, Mia'nın, Zeytin'in, Köpük'ün);
 * English uses 's. Never build these suffixes by string concatenation in features.
 */
object PetNameFormatter {

    fun possessive(name: String, languageCode: String): String {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return trimmed
        return when (languageCode.lowercase().substringBefore('-')) {
            "tr" -> turkishGenitive(trimmed)
            else -> if (trimmed.endsWith("s", ignoreCase = true)) "$trimmed'" else "$trimmed's"
        }
    }

    private const val TURKISH_VOWELS = "aeıioöuü"

    private fun turkishGenitive(name: String): String {
        val lower = name.turkishLowercase()
        val lastVowel = lower.lastOrNull { it in TURKISH_VOWELS } ?: return "$name'in"
        val suffixVowel = when (lastVowel) {
            'a', 'ı' -> 'ı'
            'e', 'i' -> 'i'
            'o', 'u' -> 'u'
            'ö', 'ü' -> 'ü'
            else -> 'i'
        }
        val buffer = if (lower.last() in TURKISH_VOWELS) "n" else ""
        return "$name'$buffer${suffixVowel}n"
    }

    private fun String.turkishLowercase(): String = buildString(length) {
        for (c in this@turkishLowercase) {
            append(
                when (c) {
                    'I' -> 'ı'
                    'İ' -> 'i'
                    else -> c.lowercaseChar()
                },
            )
        }
    }
}

@Composable
fun possessivePetName(name: String): String = PetNameFormatter.possessive(name, Locale.current.language)
