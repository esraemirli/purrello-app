package com.purrello.core.navigation

import com.purrello.core.model.LostAlertId
import com.purrello.core.model.PetId
import com.purrello.core.model.VaccinationId

/** Parsed push / link target. The handler switches [targetPetId] first, then opens [tab] and [routes]. */
data class DeepLink(
    val targetPetId: PetId?,
    val tab: MainTab?,
    val routes: List<AppRoute>,
)

/**
 * Push payload → [DeepLink]. Payload keys follow docs/api/common/push.md: `type`, `petId`, `targetId`.
 * Unknown types return null (open the app normally).
 */
object DeepLinkParser {

    fun parse(payload: Map<String, String>): DeepLink? {
        val petId = payload["petId"]?.takeIf { it.isNotBlank() }?.let(::PetId)
        val targetId = payload["targetId"]?.takeIf { it.isNotBlank() }
        return when (payload["type"]) {
            "VACCINE_DUE", "VACCINE_OVERDUE" -> DeepLink(
                targetPetId = petId,
                tab = MainTab.HEALTH,
                routes = listOfNotNull(targetId?.let { VaccineDetail(VaccinationId(it)) }),
            )
            "DOCUMENT_UPLOADED" -> DeepLink(targetPetId = petId, tab = MainTab.DOCUMENTS, routes = emptyList())
            "CARE_DUE" -> DeepLink(targetPetId = petId, tab = MainTab.CARE, routes = emptyList())
            "LOST_PET_NEARBY" -> targetId?.let {
                DeepLink(targetPetId = null, tab = null, routes = listOf(LostAlertDetail(LostAlertId(it))))
            }
            else -> null
        }
    }
}
