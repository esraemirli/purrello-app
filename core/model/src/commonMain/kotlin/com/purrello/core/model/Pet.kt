package com.purrello.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class Species { CAT, DOG, BIRD, RABBIT, OTHER, UNKNOWN }

/** Minimal pet data shared across features (headers, pet switcher, tab label). */
data class PetSummary(
    val id: PetId,
    val name: String,
    val species: Species,
    val photoUrl: String?,
)
