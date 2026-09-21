# Testing Conventions

Tests are written **once in `commonTest`** so they run on Android and iOS. Drop to `androidHostTest` only when a test truly needs Android APIs.

## 1. What we test

| Target | Required | How |
|---|---|---|
| ViewModels | Yes, every new/changed one | Fakes + Turbine on `state` and `effects` |
| Domain (use cases, validators, formatters' logic) | Yes | Plain unit tests |
| Data: DTO decoding | Yes, per contract | Decode the **contract example JSON** and assert the DTO / mapped domain model |
| Data: repositories / APIs | Yes for non-trivial mapping & error handling | Ktor `MockEngine` returning contract JSON / error bodies |
| `safeApiCall` error mapping | Yes (in `core:network`) | `MockEngine` per status code |
| Composables | Optional, for complex interaction | `runComposeUiTest` in `commonTest` |

Coverage target for new ViewModels and domain classes: **> 80 %**. Cover success, first-load failure (`Async.Failure(previous = null)`), refresh failure that keeps data (`Async.Failure(previous)`), dismiss → `dismissFailure()`, retry, empty, **load cancellation / race**, `SavedStateHandle` restoration, and no-op branches.

## 2. Naming & layout

- Class: `{ClassName}Test` — `VaccineListViewModelTest`, `HealthScreenResponseDecodingTest`.
- Method: backticked `GIVEN … WHEN … THEN …`, human-readable.
- One empty line before each `@Test`; setup lines grouped; one empty line between setup and assertions.

## 3. Tools

- **kotlin.test** (`@Test`, `@BeforeTest`, `@AfterTest`), **assertk** (`assertThat(x).isEqualTo(y)`), **Turbine**, **kotlinx-coroutines-test**, Ktor **MockEngine**.
- **Prefer hand-written fakes** (`core:testing` + per-feature `FakeHealthRepository`). Use **Mokkery** only when a fake is impractical. No MockK/Robolectric in `commonTest`.

## 4. ViewModel tests

- Main dispatcher: `Dispatchers.setMain(testDispatcher)` in `@BeforeTest`, `Dispatchers.resetMain()` in `@AfterTest` — via the `MainDispatcherTest` base class in `core:testing`. Run tests with `runTest(testDispatcher)` so the test body, `backgroundScope` collectors and `viewModelScope` share one scheduler.
- Fakes that stand in for I/O should `delay(…)` briefly: real calls suspend, and without a suspension point `StateFlow` conflates intermediate states (e.g. `isLoading = true`) so they can't be asserted.
- Build the ViewModel directly with fakes and `SavedStateHandle()` (or `SavedStateHandle(mapOf(...))` to test restoration). No Koin in tests.
- Collect `state` and `effects` with `testIn(backgroundScope)`; start collectors **before** sending the event; don't nest `test {}` blocks.
- Assert **every** state emission in order, expressed as `initial.copy(...)`. Navigation/one-shot outcomes are asserted on the `effects` turbine.
- `expectNoEvents()` for no-op branches. Cancel turbines at the end.

```kotlin
class VaccineListViewModelTest : MainDispatcherTest() {

    private val repository = FakeHealthRepository()
    private val selectedPet = FakeSelectedPetRepository(initial = PetId("pet_1"))
    private val changes = FakeDataChangeNotifier()

    private fun viewModel(handle: SavedStateHandle = SavedStateHandle()) =
        VaccineListViewModel(repository, selectedPet, changes, handle)

    @Test
    fun `GIVEN list loads WHEN add clicked THEN emits open-add-vaccine effect`() = runTest(testDispatcher) {
        repository.vaccineListResult = AppResult.Success(sampleContent)
        val vm = viewModel()
        advanceUntilIdle()                                   // let init pick up the selected pet
        val effects = vm.effects.testIn(backgroundScope)

        vm.onEvent(VaccineListEvent.AddClicked)

        assertThat(effects.awaitItem()).isEqualTo(VaccineListEffect.OpenAddVaccine(PetId("pet_1")))
        effects.cancel()
    }

    @Test
    fun `GIVEN content shown WHEN refresh fails with no connection THEN error keeps the content for the dialog`() = runTest(testDispatcher) {
        repository.vaccineListResult = AppResult.Success(sampleContent)
        val vm = viewModel()
        advanceUntilIdle()
        repository.vaccineListResult = AppResult.Failure(AppError.NoConnection)
        val states = vm.state.testIn(backgroundScope)
        val loaded = VaccineListUiState(vaccines = Async.Success(sampleContent))

        vm.onEvent(VaccineListEvent.Refreshed)

        assertThat(states.awaitItem()).isEqualTo(loaded)
        assertThat(states.awaitItem()).isEqualTo(loaded.copy(vaccines = Async.Loading(sampleContent)))
        assertThat(states.awaitItem()).isEqualTo(
            loaded.copy(vaccines = Async.Failure(AppError.NoConnection, previous = sampleContent)),
        )
        states.cancel()
    }
}
```

## 5. Contract tests (data)

- The contract example JSON from `docs/api/<feature>/<screen>.md` is copied **verbatim** into `commonTest/.../fixtures/<Screen>Fixtures.kt` as a raw string, with a comment linking the doc section. The same JSON backs `FakeXApi`.
- Test: decode with the production `Json` → map to domain → assert the full domain object with hardcoded expected values.
- Add a case with an **unknown enum value** and a **missing optional field** to prove forward compatibility.
- Error mapping: `MockEngine` returning `422` + `fieldErrors` → `AppError.Validation`; `500` → `AppError.Server(code, traceId)`; thrown IO exception → `NoConnection`.

## 6. Assertions

- Equality on full values: `assertThat(actual).isEqualTo(expected)`. No `isInstanceOf`, no `assertTrue(x is …)`.
- Hardcode expected values; never compute them in the test.
- Prefer checking fakes' recorded calls (`repository.addVaccinationCalls`) over mock verification. With Mokkery: `verifySuspend(VerifyMode.exactly(n)) { … }`.

## 7. Coroutines

- `runTest` everywhere (no `runBlocking` — not available on all targets).
- Inject `TestDispatcherProvider` wherever a class takes `DispatcherProvider`.
- No unstructured `launch` in tests.

## 8. Scope

Before writing tests on a branch, check `git diff origin/main...HEAD` and test what changed or was added; update existing tests only when the change broke them.
