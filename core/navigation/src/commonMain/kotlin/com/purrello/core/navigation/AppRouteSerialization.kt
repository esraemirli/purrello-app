package com.purrello.core.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Polymorphic registration of every [AppRoute] — required on iOS (no reflection) for back-stack
 * saving/restoring. Keep in sync with AppRoute.kt; the unit test fails if a route is missing.
 */
val appRouteSerializersModule: SerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Splash::class, Splash.serializer())
        subclass(Login::class, Login.serializer())
        subclass(Main::class, Main.serializer())
        subclass(OwnerProfile::class, OwnerProfile.serializer())
        subclass(PetProfile::class, PetProfile.serializer())
        subclass(Passport::class, Passport.serializer())
        subclass(EmergencyQr::class, EmergencyQr.serializer())
        subclass(VaccineDetail::class, VaccineDetail.serializer())
        subclass(LostAlertDetail::class, LostAlertDetail.serializer())
        subclass(AddPet::class, AddPet.serializer())
        subclass(AddVaccine::class, AddVaccine.serializer())
        subclass(EditVaccine::class, EditVaccine.serializer())
        subclass(AddDocument::class, AddDocument.serializer())
        subclass(AddCare::class, AddCare.serializer())
        subclass(ReportLost::class, ReportLost.serializer())
    }
}

val appRouteSavedStateConfiguration: SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = appRouteSerializersModule
}
