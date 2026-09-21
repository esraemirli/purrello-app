package com.purrello.core.navigation

import androidx.navigation3.runtime.NavKey
import com.purrello.core.model.LostAlertId
import com.purrello.core.model.PetId
import com.purrello.core.model.VaccinationId
import kotlinx.serialization.Serializable

/**
 * Every destination of the root back stack (navigation.md §2). Tab roots are NOT routes — see [MainTab].
 * Args are ids / small enums only. Adding a route → also register it in [appRouteSerializersModule].
 */
@Serializable
sealed interface AppRoute : NavKey

/** Full-screen modal: slides up, closes with ×, primary action pinned at the bottom. */
@Serializable
sealed interface ModalRoute : AppRoute

@Serializable data object Splash : AppRoute
@Serializable data object Login : AppRoute
@Serializable data object Main : AppRoute

@Serializable data object OwnerProfile : AppRoute
@Serializable data class PetProfile(val petId: PetId) : AppRoute
@Serializable data class Passport(val petId: PetId) : AppRoute
@Serializable data class EmergencyQr(val petId: PetId) : AppRoute
@Serializable data class VaccineDetail(val vaccinationId: VaccinationId) : AppRoute
@Serializable data class LostAlertDetail(val alertId: LostAlertId) : AppRoute

@Serializable data object AddPet : ModalRoute
@Serializable data class AddVaccine(val petId: PetId, val mode: VaccineEntryMode) : ModalRoute
@Serializable data class EditVaccine(val vaccinationId: VaccinationId) : ModalRoute
@Serializable data class AddDocument(val petId: PetId) : ModalRoute
@Serializable data class AddCare(val petId: PetId) : ModalRoute
@Serializable data class ReportLost(val petId: PetId) : ModalRoute

/** "Yapıldı / Planla" in the add-vaccine flow. */
@Serializable
enum class VaccineEntryMode { DONE, PLANNED }

/** Tabs of the Main host, in tab-bar order. */
@Serializable
enum class MainTab { HOME, HEALTH, DOCUMENTS, CARE, PET }
