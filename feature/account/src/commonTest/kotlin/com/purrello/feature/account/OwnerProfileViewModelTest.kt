package com.purrello.feature.account

import app.cash.turbine.testIn
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.testing.FakeSessionRepository
import com.purrello.core.testing.MainDispatcherTest
import com.purrello.feature.account.presentation.profile.OwnerProfileEffect
import com.purrello.feature.account.presentation.profile.OwnerProfileEvent
import com.purrello.feature.account.presentation.profile.OwnerProfileViewModel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class OwnerProfileViewModelTest : MainDispatcherTest() {

    private val session = FakeSessionRepository(loggedIn = true)

    @Test
    fun `GIVEN logged in WHEN sign out clicked THEN session ends and signed-out effect`() = runTest(testDispatcher) {
        val vm = OwnerProfileViewModel(session)
        val effects = vm.effects.testIn(backgroundScope)

        vm.onEvent(OwnerProfileEvent.SignOutClicked)

        assertThat(effects.awaitItem()).isEqualTo(OwnerProfileEffect.SignedOut)
        assertThat(session.endSessionCalls).isEqualTo(1)
        effects.cancel()
    }

    @Test
    fun `GIVEN profile WHEN back clicked THEN navigate-back effect`() = runTest(testDispatcher) {
        val vm = OwnerProfileViewModel(session)
        val effects = vm.effects.testIn(backgroundScope)

        vm.onEvent(OwnerProfileEvent.BackClicked)

        assertThat(effects.awaitItem()).isEqualTo(OwnerProfileEffect.NavigateBack)
        effects.cancel()
    }
}
