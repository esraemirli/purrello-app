package com.purrello.core.network.di

import com.purrello.core.network.auth.SessionEvents
import com.purrello.core.network.createHttpClient
import org.koin.dsl.module

/** Needs `AppConfig` (platform entry) and `TokenStorage` (core:data) in the graph. */
val networkModule = module {
    single { SessionEvents() }
    single { createHttpClient(config = get(), tokenStorage = get(), sessionEvents = get()) }
}
