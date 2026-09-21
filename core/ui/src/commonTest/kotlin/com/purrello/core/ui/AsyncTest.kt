package com.purrello.core.ui

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.common.AppError
import com.purrello.core.common.AppResult
import com.purrello.core.ui.mvi.Async
import com.purrello.core.ui.mvi.dismissFailure
import com.purrello.core.ui.mvi.isRefreshing
import com.purrello.core.ui.mvi.toAsync
import com.purrello.core.ui.mvi.toFailure
import com.purrello.core.ui.mvi.toLoading
import com.purrello.core.ui.mvi.valueOrNull
import kotlin.test.Test

class AsyncTest {

    @Test
    fun `GIVEN first load WHEN loading THEN nothing to show and not refreshing`() {
        val state: Async<String> = Async.Uninitialized.toLoading()

        assertThat(state).isEqualTo(Async.Loading<String>(previous = null))
        assertThat(state.valueOrNull).isEqualTo(null)
        assertThat(state.isRefreshing).isEqualTo(false)
    }

    @Test
    fun `GIVEN content WHEN reloading THEN content stays and state is refreshing`() {
        val state = Async.Success("data").toLoading()

        assertThat(state).isEqualTo(Async.Loading(previous = "data"))
        assertThat(state.valueOrNull).isEqualTo("data")
        assertThat(state.isRefreshing).isEqualTo(true)
    }

    @Test
    fun `GIVEN first load WHEN it fails THEN full-screen error - no previous data`() {
        val state: Async<String> = AppResult.Failure(AppError.NoConnection).toAsync(Async.Uninitialized)

        assertThat(state).isEqualTo(Async.Failure<String>(AppError.NoConnection, previous = null))
    }

    @Test
    fun `GIVEN content WHEN refresh fails THEN error keeps previous data for the dialog`() {
        val state = Async.Success("data").toFailure(AppError.Timeout)

        assertThat(state).isEqualTo(Async.Failure(AppError.Timeout, previous = "data"))
    }

    @Test
    fun `GIVEN failed refresh WHEN dialog dismissed THEN previous data is shown again`() {
        val state = Async.Success("data").toFailure(AppError.Timeout).dismissFailure()

        assertThat(state).isEqualTo(Async.Success("data"))
    }

    @Test
    fun `GIVEN full-screen error WHEN dismissed THEN it stays - there is nothing to fall back to`() {
        val state: Async<String> = Async.Failure(AppError.NoConnection)

        assertThat(state.dismissFailure()).isEqualTo(Async.Failure<String>(AppError.NoConnection))
    }

    @Test
    fun `GIVEN success WHEN converted from AppResult THEN success`() {
        val state = AppResult.Success("data").toAsync(Async.Uninitialized)

        assertThat(state).isEqualTo(Async.Success("data"))
    }
}
