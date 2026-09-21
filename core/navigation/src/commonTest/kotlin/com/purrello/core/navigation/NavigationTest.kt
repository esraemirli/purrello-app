package com.purrello.core.navigation

import androidx.navigation3.runtime.NavKey
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.model.PetId
import com.purrello.core.model.VaccinationId
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test

class NavigationTest {

    private val json = Json { serializersModule = appRouteSerializersModule }

    @Test
    fun `GIVEN a route WHEN round-tripped polymorphically THEN it is restored`() {
        val route: NavKey = AddVaccine(PetId("pet_1"), VaccineEntryMode.PLANNED)
        val serializer = PolymorphicSerializer(NavKey::class)

        val restored = json.decodeFromString(serializer, json.encodeToString(serializer, route))

        assertThat(restored).isEqualTo(route)
    }

    @Test
    fun `GIVEN vaccine push WHEN parsed THEN switches pet, opens health and the vaccine`() {
        val link = DeepLinkParser.parse(mapOf("type" to "VACCINE_OVERDUE", "petId" to "pet_1", "targetId" to "vac_9"))

        assertThat(link).isEqualTo(
            DeepLink(targetPetId = PetId("pet_1"), tab = MainTab.HEALTH, routes = listOf(VaccineDetail(VaccinationId("vac_9")))),
        )
    }

    @Test
    fun `GIVEN unknown push type WHEN parsed THEN null`() {
        assertThat(DeepLinkParser.parse(mapOf("type" to "SOMETHING_NEW"))).isEqualTo(null)
    }
}
