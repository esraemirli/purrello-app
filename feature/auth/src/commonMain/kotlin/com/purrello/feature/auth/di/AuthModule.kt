package com.purrello.feature.auth.di

import com.purrello.core.common.AppConfig
import com.purrello.feature.auth.data.AuthRepositoryImpl
import com.purrello.feature.auth.data.fake.FakeAuthApi
import com.purrello.feature.auth.data.fake.FakeGoogleIdTokenProvider
import com.purrello.feature.auth.data.remote.AuthApi
import com.purrello.feature.auth.data.remote.KtorAuthApi
import com.purrello.feature.auth.domain.AuthRepository
import com.purrello.feature.auth.domain.GoogleIdTokenProvider
import com.purrello.feature.auth.domain.NotConfiguredGoogleIdTokenProvider
import com.purrello.feature.auth.presentation.login.LoginViewModel
import com.purrello.feature.auth.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    single<AuthApi> { if (get<AppConfig>().useFakeApi) FakeAuthApi() else KtorAuthApi(get()) }
    // TODO(platform): bind Credential Manager (Android) / GoogleSignIn bridge (iOS) in the platform module.
    single<GoogleIdTokenProvider> {
        if (get<AppConfig>().useFakeApi) FakeGoogleIdTokenProvider() else NotConfiguredGoogleIdTokenProvider()
    }
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
}
