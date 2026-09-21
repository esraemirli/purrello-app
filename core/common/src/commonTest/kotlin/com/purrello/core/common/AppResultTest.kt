package com.purrello.core.common

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class AppResultTest {

    @Test
    fun `GIVEN success WHEN map THEN value is transformed`() {
        val result: AppResult<Int> = AppResult.Success(2)

        assertThat(result.map { it * 21 }).isEqualTo(AppResult.Success(42))
    }

    @Test
    fun `GIVEN failure WHEN flatMap THEN failure is kept and transform is not called`() {
        val result: AppResult<Int> = AppResult.Failure(AppError.NoConnection)
        var called = false

        val mapped = result.flatMap { called = true; AppResult.Success(it.toString()) }

        assertThat(mapped).isEqualTo(AppResult.Failure(AppError.NoConnection))
        assertThat(called).isEqualTo(false)
    }

    @Test
    fun `GIVEN failure WHEN fold THEN onFailure branch is returned`() {
        val result: AppResult<Int> = AppResult.Failure(AppError.Timeout)

        val folded = result.fold(onSuccess = { "ok" }, onFailure = { it.toString() })

        assertThat(folded).isEqualTo("Timeout")
    }
}
