package com.purrello.core.data.pet

import com.purrello.core.data.storage.KeyValueStore
import com.purrello.core.model.PetId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App-wide selected pet (tabs, headers, deep links). Tab ViewModels observe [selectedPetId];
 * they never keep their own "current pet" (anti-patterns.md).
 */
interface SelectedPetRepository {
    val selectedPetId: StateFlow<PetId?>
    fun select(petId: PetId)
    fun clear()
}

internal class DefaultSelectedPetRepository(private val store: KeyValueStore) : SelectedPetRepository {

    private val _selectedPetId = MutableStateFlow(store.getString(KEY)?.let(::PetId))
    override val selectedPetId: StateFlow<PetId?> = _selectedPetId.asStateFlow()

    override fun select(petId: PetId) {
        store.putString(KEY, petId.value)
        _selectedPetId.value = petId
    }

    override fun clear() {
        store.remove(KEY)
        _selectedPetId.value = null
    }

    private companion object {
        const val KEY = "selected_pet_id"
    }
}
