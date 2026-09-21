package com.purrello.core.data.di

import com.purrello.core.data.change.DataChangeNotifier
import com.purrello.core.data.change.DefaultDataChangeNotifier
import com.purrello.core.data.pet.DefaultSelectedPetRepository
import com.purrello.core.data.pet.SelectedPetRepository
import com.purrello.core.data.session.DefaultSessionRepository
import com.purrello.core.data.session.SecureTokenStorage
import com.purrello.core.data.session.SessionRepository
import com.purrello.core.network.auth.TokenStorage
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** Needs `KeyValueStore` and `SecureStore` from the platform module. */
val dataModule = module {
    single<TokenStorage> { SecureTokenStorage(get()) }
    singleOf(::DefaultSelectedPetRepository) { bind<SelectedPetRepository>() }
    singleOf(::DefaultSessionRepository) { bind<SessionRepository>() }
    singleOf(::DefaultDataChangeNotifier) { bind<DataChangeNotifier>() }
}
