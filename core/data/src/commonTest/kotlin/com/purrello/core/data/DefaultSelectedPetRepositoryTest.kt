package com.purrello.core.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.data.pet.DefaultSelectedPetRepository
import com.purrello.core.data.storage.InMemoryKeyValueStore
import com.purrello.core.model.PetId
import kotlin.test.Test

class DefaultSelectedPetRepositoryTest {

    @Test
    fun `GIVEN a stored pet WHEN repository is created THEN it restores the selection`() {
        val store = InMemoryKeyValueStore().apply { putString("selected_pet_id", "pet_1") }

        val repository = DefaultSelectedPetRepository(store)

        assertThat(repository.selectedPetId.value).isEqualTo(PetId("pet_1"))
    }

    @Test
    fun `GIVEN a selection WHEN cleared THEN selection and storage are empty`() {
        val store = InMemoryKeyValueStore()
        val repository = DefaultSelectedPetRepository(store)
        repository.select(PetId("pet_2"))

        repository.clear()

        assertThat(repository.selectedPetId.value).isEqualTo(null)
        assertThat(store.getString("selected_pet_id")).isEqualTo(null)
    }
}
