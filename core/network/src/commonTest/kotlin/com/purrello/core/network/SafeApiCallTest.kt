package com.purrello.core.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.purrello.core.common.AppError
import com.purrello.core.common.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test

class SafeApiCallTest {

    @Serializable
    data class PingDto(val status: String, val kind: Kind = Kind.UNKNOWN) {
        @Serializable
        enum class Kind { A, B, UNKNOWN }
    }

    private fun client(status: HttpStatusCode, body: String) = HttpClient(
        MockEngine { respond(body, status, headersOf(HttpHeaders.ContentType, "application/json")) },
    ) {
        install(ContentNegotiation) { json(PurrelloJson) }
    }

    @Test
    fun `GIVEN 200 with unknown enum and extra field WHEN called THEN decodes with UNKNOWN fallback`() = runTest {
        val client = client(HttpStatusCode.OK, """{"status":"ok","kind":"NEW_VALUE","extra":1}""")

        val result = safeApiCall<PingDto> { client.get("ping") }

        assertThat(result).isEqualTo(AppResult.Success(PingDto(status = "ok", kind = PingDto.Kind.UNKNOWN)))
    }

    @Test
    fun `GIVEN 422 with field errors WHEN called THEN validation error`() = runTest {
        val client = client(
            HttpStatusCode.UnprocessableEntity,
            """{"error":{"code":"VALIDATION_FAILED","traceId":"t1","fieldErrors":[{"field":"weightGrams","code":"OUT_OF_RANGE"}]}}""",
        )

        val result = safeApiCall<PingDto> { client.get("ping") }

        assertThat(result).isEqualTo(
            AppResult.Failure(AppError.Validation(persistentMapOf("weightGrams" to "OUT_OF_RANGE"), "t1")),
        )
    }

    @Test
    fun `GIVEN 404 with error body WHEN called THEN server error with code and trace id`() = runTest {
        val client = client(HttpStatusCode.NotFound, """{"error":{"code":"PET_NOT_FOUND","message":"x","traceId":"t2"}}""")

        val result = safeApiCall<PingDto> { client.get("ping") }

        assertThat(result).isEqualTo(AppResult.Failure(AppError.Server(status = 404, code = "PET_NOT_FOUND", traceId = "t2")))
    }

    @Test
    fun `GIVEN 500 without body WHEN called THEN server error without code`() = runTest {
        val client = client(HttpStatusCode.InternalServerError, "")

        val result = safeApiCall<PingDto> { client.get("ping") }

        assertThat(result).isEqualTo(AppResult.Failure(AppError.Server(status = 500, code = null, traceId = null)))
    }

    @Test
    fun `GIVEN 401 WHEN called THEN unauthorized`() = runTest {
        val client = client(HttpStatusCode.Unauthorized, "")

        val result = safeApiCall<PingDto> { client.get("ping") }

        assertThat(result).isEqualTo(AppResult.Failure(AppError.Unauthorized))
    }

    @Test
    fun `GIVEN transport failure WHEN called THEN no connection`() = runTest {
        val client = HttpClient(MockEngine { throw IllegalStateException("offline") })

        val result = safeApiCall<PingDto> { client.get("ping") }

        assertThat(result).isEqualTo(AppResult.Failure(AppError.NoConnection))
    }
}
