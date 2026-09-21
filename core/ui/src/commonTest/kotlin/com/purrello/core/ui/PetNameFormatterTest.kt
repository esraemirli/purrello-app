package com.purrello.core.ui

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.ui.text.PetNameFormatter
import kotlin.test.Test

class PetNameFormatterTest {

    @Test
    fun `GIVEN turkish names WHEN possessive THEN suffix follows vowel harmony`() {
        val actual = listOf("Boncuk", "Mia", "Zeytin", "Köpük", "Max", "Pamuk", "Tarçın", "Duman", "Leo", "Fındık")
            .map { PetNameFormatter.possessive(it, "tr") }

        assertThat(actual).isEqualTo(
            listOf("Boncuk'un", "Mia'nın", "Zeytin'in", "Köpük'ün", "Max'ın", "Pamuk'un", "Tarçın'ın", "Duman'ın", "Leo'nun", "Fındık'ın"),
        )
    }

    @Test
    fun `GIVEN dotted capital I WHEN possessive in turkish THEN uses front vowel`() {
        assertThat(PetNameFormatter.possessive("İZMİR", "tr-TR")).isEqualTo("İZMİR'in")
    }

    @Test
    fun `GIVEN english locale WHEN possessive THEN apostrophe s`() {
        assertThat(PetNameFormatter.possessive("Boncuk", "en")).isEqualTo("Boncuk's")
        assertThat(PetNameFormatter.possessive("Chris", "en-US")).isEqualTo("Chris'")
    }
}
