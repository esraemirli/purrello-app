package com.purrello.shared.di

import com.purrello.core.common.AppConfig
import com.purrello.core.common.DefaultDispatcherProvider
import com.purrello.core.common.DispatcherProvider
import com.purrello.core.data.di.dataModule
import com.purrello.core.network.di.networkModule
import com.purrello.feature.account.di.accountModule
import com.purrello.feature.auth.di.authModule
import com.purrello.feature.care.di.careModule
import com.purrello.feature.documents.di.documentsModule
import com.purrello.feature.health.di.healthModule
import com.purrello.feature.home.di.homeModule
import com.purrello.feature.lostpet.di.lostPetModule
import com.purrello.feature.pet.di.petModule
import com.purrello.shared.RootViewModel
import com.purrello.shared.deeplink.DeepLinkDispatcher
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

private val sharedModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single { DeepLinkDispatcher() }
    viewModelOf(::RootViewModel)
}

private val appModules: List<Module> = listOf(
    sharedModule,
    networkModule,
    dataModule,
    authModule,
    homeModule,
    healthModule,
    documentsModule,
    careModule,
    petModule,
    lostPetModule,
    accountModule,
)

/**
 * Called once from the platform entry. [platformModule] must provide `KeyValueStore` and `SecureStore`,
 * and may override platform bridges (e.g. `GoogleIdTokenProvider`) — it is loaded last on purpose.
 */
fun initKoin(config: AppConfig, platformModule: Module): KoinApplication = startKoin {
    modules(listOf(module { single { config } }) + appModules + platformModule)
}

/** Push/link payload from the platform → pending deep link (applied once the user is in Main). */
fun handleDeepLinkPayload(payload: Map<String, String>) {
    KoinPlatform.getKoin().get<DeepLinkDispatcher>().dispatch(payload)
}
