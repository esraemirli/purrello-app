package com.purrello.feature.account.di

import com.purrello.feature.account.presentation.profile.OwnerProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val accountModule = module {
    viewModelOf(::OwnerProfileViewModel)
}
