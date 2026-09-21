# Architecture

Purrello is **Kotlin Multiplatform + Compose Multiplatform** (Android + iOS) with **UI and logic both shared**. Presentation is **MVI on androidx `ViewModel` (multiplatform) + Navigation 3**: one immutable **State** out, sealed **Event**s (user intents) in, one-shot **Effect**s (navigation and other parent-owned outcomes) out. Every screen's ViewModel extends **`MviViewModel<State, Event, Effect>`** (`core:ui`). Rationale: `docs/adr/0001-client-architecture.md`.

Read with: `kmp-conventions.md` (modules, DI), `navigation.md`, `api-contract.md`, `localization.md`, `ui-conventions.md`, `testing.md`, `anti-patterns.md`.

When unsure, **open a similar screen in the same feature module and match it**.

## 1. Layers

```
UI (Compose)     FooRoute ──► FooScreen(state, onEvent)         feature/*/presentation
                   ▲ state        │ events (user intents)
ViewModel        FooViewModel : MviViewModel<State, Event, Effect>  — state, effects, onEvent()
                   │
Domain           models, repository interfaces, use cases,       feature/*/domain, core:model
(pure Kotlin)    validators
                   ▲ implements
Data             repository impl, remote API (Ktor) + DTOs,     feature/*/data, core:network, core:data
                 local (DataStore / Room), mappers, fake API
```

- **Dependency rule:** UI → ViewModel → domain ← data. Domain has **no** Ktor, Compose, Koin, platform or DTO types.
- **DTOs never leave `data`.** Repositories return domain models.
- **Domain layer is thin on purpose.** The backend owns business rules; with screen-shaped (BFF) endpoints most screens are: ViewModel → repository. Add a use case only when (a) two or more ViewModels need the same logic, (b) several repositories must be combined, or (c) there is a client-side rule worth testing on its own (form validation, reminder-time suggestion, pet-switch policy).
- **Don't duplicate backend rules** (next-dose date, reminder schedule, lost-alert radius). If the screen needs the result, it goes into the contract.

## 2. Principles

| Topic | Rule |
|---|---|
| **ViewModel** | `class FooViewModel(...) : MviViewModel<FooUiState, FooEvent, FooEffect>(FooUiState())`. Thin host: `onEvent` → repositories/use cases → `updateState { }` / `sendEffect(...)`. No UI types, no `Res.*`, no navigation types. Only `onEvent`, `state` and `effects` are public API. |
| **UI state** | One `@Immutable data class FooUiState` per screen; `ImmutableList` for lists; defaults for every field. Asynchronous data is an **`Async<T>`** field (`core:ui`), never `isLoading` + `data` + `error` triples. |
| **Events** | `sealed interface FooEvent` = user intents, one entry per interaction. The ViewModel implements a single `override fun onEvent(event: FooEvent)` with an exhaustive `when`; the stateless Screen takes one `onEvent: (FooEvent) -> Unit`. No other public functions on the ViewModel. |
| **Effects** | `sealed interface FooEffect` for **one-shot, parent-owned** outcomes only: navigate, close modal, open system share/dialer/maps. Buffered `Channel` inside `MviViewModel`, collected once in the Route with `ObserveEffects` (§4.5). |
| **Everything visible is State** | Dialogs, inline field errors, banners, loading indicators, selected tab/filter are **State**, never Effects: an Effect that is missed while the screen is recreated can never be recovered. |
| **Route / Screen split** | `FooRoute` (stateful: gets the ViewModel, collects state + effects, receives navigation lambdas) and `FooScreen` (stateless: `state` + `onEvent`, previewable). The Screen never sees the ViewModel. |
| **Initial load** | Runtime args (ids) arrive via the route → Koin `parametersOf`. Load starts in `init`. No `ScreenStarted` event. |
| **Concurrent loads** | Every re-triggerable load (retry, pull-to-refresh, pet switch, filter) **cancels the previous run**: hold `private var loadJob: Job?` and `loadJob?.cancel()`, or drive it from a `StateFlow` with `flatMapLatest`. |
| **Process death** | Unsubmitted user input (form fields, search text, selected filter) survives via `SavedStateHandle` (`saved { }` delegate, `@Serializable`). Only that slice — fetched content reloads. |
| **Selected pet** | App-wide `SelectedPetRepository` (`core:data`) exposes `StateFlow<PetId?>`, persisted. Tab ViewModels react to it (`flatMapLatest`/`collectLatest`); they never keep their own copy of "current pet". |
| **Refresh after mutations** | A successful mutation calls `DataChangeNotifier.notify(DataChange.X(petId))` (`core:data`). ViewModels showing affected data observe the topic and refresh silently. Don't pass results back through navigation. |
| **Errors** | Data layer returns `AppResult<T>` (`Success` / `Failure(AppError)`). No `try/catch` outside `core:network`'s `safeApiCall` and platform bridges. The ViewModel maps `AppError` to a surface (§5). |
| **Formatting** | ViewModel holds raw values (`LocalDate`, `Instant`, `Int` grams, enums). Formatting to text happens in the UI with locale-aware formatters (`localization.md`). |
| **Dispatchers** | Inject `DispatcherProvider` (`core:common`); never hardcode dispatchers in classes that need tests. `viewModelScope` is Main.immediate. |
| **Flows** | Big `combine` / `flatMapLatest` graphs across repositories go into a use case that exposes **one** `Flow`. The ViewModel collects it. |

## 3. Models per layer

| Layer | Type | Example | Lives in |
|---|---|---|---|
| Network | `…Dto`, `…Request`, `…Response` (`@Serializable`) | `HealthScreenResponse`, `VaccinationDto` | `feature/x/data/remote/dto` |
| Domain | Plain Kotlin data classes, value classes for ids | `Vaccination`, `PetId` | `feature/x/domain/model`, shared ones in `core:model` |
| UI | `FooUiState`, `FooContent`, `FooEvent`, `FooEffect` | `VaccineRowUi` | `feature/x/presentation/foo` |

- Mapping DTO → domain: named mappers in `data/mapper` (`fun VaccinationDto.toDomain(): Vaccination`). Unknown enum values map to `UNKNOWN` — never crash on a new backend value.
- Mapping domain → UI: in the ViewModel for trivial cases, in a named `FooUiMapper` when it grows. Not generic extension soup.
- Ids are value classes: `@JvmInline value class PetId(val value: String)` — never raw `String` across layers.

## 4. Code samples (Health → vaccine list)

### 4.1 Contract: State, Event, Effect (`VaccineListContract.kt`)

```kotlin
// core:ui — one field per asynchronously loaded slice
sealed interface Async<out T> {
    data object Uninitialized : Async<Nothing>
    data class Loading<out T>(val previous: T? = null) : Async<T>       // previous = data still on screen
    data class Success<out T>(val value: T) : Async<T>
    data class Failure<out T>(val error: AppError, val previous: T? = null) : Async<T>
}

@Immutable
data class VaccineListUiState(
    val vaccines: Async<VaccineListContent> = Async.Uninitialized,
    val filter: VaccineFilter = VaccineFilter.ALL,
    val saveError: AppError? = null,             // action (not fetch) failures, e.g. "mark as done"
)

@Immutable
data class VaccineListContent(
    val pet: PetSummary,
    val overdue: ImmutableList<Vaccination>,
    val upcoming: ImmutableList<Vaccination>,
    val done: ImmutableList<Vaccination>,
)

/** User intents — the only thing the Screen sends. */
sealed interface VaccineListEvent {
    data object RetryClicked : VaccineListEvent
    data object Refreshed : VaccineListEvent
    data class FilterSelected(val filter: VaccineFilter) : VaccineListEvent
    data class VaccineClicked(val id: VaccinationId) : VaccineListEvent
    data object AddClicked : VaccineListEvent
    data object ErrorDismissed : VaccineListEvent
}

/** One-shot outcomes the parent owns. */
sealed interface VaccineListEffect {
    data class OpenVaccine(val id: VaccinationId) : VaccineListEffect
    data class OpenAddVaccine(val petId: PetId) : VaccineListEffect
}
```

**Why `Async<T>` and not `screenState` + `content`:** two fields let you express states that can't exist (Content with `content == null`) and force every load to update both. With `Async`, `Loading`/`Failure` carry the data still on screen (`previous`), which is exactly what decides the error surface: `previous == null` → full-screen error, otherwise content + retry dialog. `isRefreshing` is derived (`Loading(previous != null)`), not stored.

Helpers (`core:ui`): `valueOrNull`, `isLoading`, `isRefreshing`, `errorOrNull`, `toLoading()`, `toFailure(error)`, `dismissFailure()`, `map()`, `AppResult.toAsync(previous)`.

**Multiple independent sections** (e.g. passport + scanned pages + QR, each from its own call) get **one `Async` field each** — that is the case `Async` is really for. With a screen-shaped BFF endpoint most screens need only one.

### 4.2 ViewModel

```kotlin
class VaccineListViewModel(
    private val healthRepository: HealthRepository,
    private val selectedPet: SelectedPetRepository,
    private val changes: DataChangeNotifier,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<VaccineListUiState, VaccineListEvent, VaccineListEffect>(VaccineListUiState()) {

    // Transient input that must survive process death.
    private var savedFilter by savedStateHandle.saved { VaccineFilter.ALL }

    private var loadJob: Job? = null
    private var petId: PetId? = null

    init {
        updateState { copy(filter = savedFilter) }

        selectedPet.selectedPetId
            .filterNotNull()
            .onEach { id -> petId = id; load() }
            .launchIn(viewModelScope)

        changes.observe<DataChange.Health>()
            .filter { it.petId == petId }
            .onEach { load() }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: VaccineListEvent) {
        when (event) {
            VaccineListEvent.RetryClicked, VaccineListEvent.Refreshed -> load()
            is VaccineListEvent.FilterSelected -> {
                savedFilter = event.filter
                updateState { copy(filter = event.filter) }
            }
            is VaccineListEvent.VaccineClicked -> sendEffect(VaccineListEffect.OpenVaccine(event.id))
            VaccineListEvent.AddClicked -> petId?.let { sendEffect(VaccineListEffect.OpenAddVaccine(it)) }
            VaccineListEvent.ErrorDismissed -> updateState { copy(vaccines = vaccines.dismissFailure()) }
        }
    }

    private fun load() {
        val id = petId ?: return
        loadJob?.cancel()                                          // no racing loads
        loadJob = launch {
            updateState { copy(vaccines = vaccines.toLoading()) }  // keeps data on screen while refreshing
            val result = healthRepository.getVaccineList(id)
            updateState { copy(vaccines = result.toAsync(vaccines)) }
        }
    }
}
```

One load = two `updateState` calls, no `screenState`/`content`/`isRefreshing` bookkeeping, and first load vs refresh is the same code path.

### 4.3 Repository (domain contract + data impl)

```kotlin
// feature/health/domain/HealthRepository.kt — pure Kotlin
interface HealthRepository {
    suspend fun getVaccineList(petId: PetId): AppResult<VaccineListContent>
    suspend fun addVaccination(petId: PetId, draft: VaccinationDraft): AppResult<VaccinationId>
}

// feature/health/data/HealthRepositoryImpl.kt
internal class HealthRepositoryImpl(
    private val api: HealthApi,                  // interface: KtorHealthApi or FakeHealthApi
    private val changes: DataChangeNotifier,
) : HealthRepository {

    override suspend fun getVaccineList(petId: PetId) =
        api.getHealthScreen(petId.value).map { it.toVaccineListContent() }

    override suspend fun addVaccination(petId: PetId, draft: VaccinationDraft) =
        api.createVaccination(petId.value, draft.toRequest())
            .map { VaccinationId(it.id) }
            .onSuccess { changes.notify(DataChange.Health(petId)) }
}
```

A repository never injects another repository. If two repositories share I/O, extract the lower-level piece (API, DAO, cache) and inject that into both.

### 4.4 Route + Screen

```kotlin
// feature/health/presentation/vaccines/VaccineListRoute.kt
@Composable
fun VaccineListRoute(
    onOpenVaccine: (VaccinationId) -> Unit,
    onOpenAddVaccine: (PetId) -> Unit,
    viewModel: VaccineListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            is VaccineListEffect.OpenVaccine -> onOpenVaccine(effect.id)
            is VaccineListEffect.OpenAddVaccine -> onOpenAddVaccine(effect.petId)
        }
    }
    VaccineListScreen(state = state, onEvent = viewModel::onEvent)
}

// feature/health/presentation/vaccines/VaccineListScreen.kt — stateless, previewable
@Composable
fun VaccineListScreen(
    state: VaccineListUiState,
    onEvent: (VaccineListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // AsyncContent applies the product rules: spinner / content / full-screen error / content + retry dialog.
    AsyncContent(
        state = state.vaccines,
        onRetry = { onEvent(VaccineListEvent.RetryClicked) },
        onErrorDismissed = { onEvent(VaccineListEvent.ErrorDismissed) },
        modifier = modifier,
    ) { content ->
        VaccineListBody(
            content = content,
            filter = state.filter,
            isRefreshing = state.vaccines.isRefreshing,
            onEvent = onEvent,
        )
    }
}
```

### 4.5 `ObserveEffects` (in `core:ui`)

```kotlin
@Composable
fun <T> ObserveEffects(effects: Flow<T>, key: Any? = null, onEffect: (T) -> Unit) {
    val currentOnEffect by rememberUpdatedState(onEffect)
    LaunchedEffect(effects, key) {
        withContext(Dispatchers.Main.immediate) {   // never lose an effect between recompositions
            effects.collect { currentOnEffect(it) }
        }
    }
}
```

A `Channel` buffers effects until the single collector reads them — unlike `SharedFlow`, nothing is dropped while the Route is off-screen.

### 4.6 DI

```kotlin
// feature/health/di/HealthModule.kt
val healthModule = module {
    single<HealthApi> { if (get<AppConfig>().useFakeApi) FakeHealthApi() else KtorHealthApi(get()) }
    singleOf(::HealthRepositoryImpl) { bind<HealthRepository>() }
    viewModelOf(::VaccineListViewModel)
    viewModelOf(::VaccineDetailViewModel)   // takes VaccinationId via parametersOf(...)
}
```

## 5. Error model

```kotlin
// core:common
sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}
// + map, flatMap, onSuccess, onFailure, getOrNull, fold

sealed interface AppError {
    data object NoConnection : AppError
    data object Timeout : AppError
    data object Unauthorized : AppError                       // refresh failed → session ends
    data class Server(val status: Int, val code: String?, val traceId: String?) : AppError
    data class Validation(val fields: ImmutableMap<String, String>, val traceId: String?) : AppError // field → error code
    data class Unknown(val cause: Throwable?) : AppError
}
```

- `core:network`'s `safeApiCall { }` is the **only** place that catches exceptions (and it always rethrows `CancellationException`). It maps HTTP status + error body (`api-contract.md §6`) to `AppError`.
- The ViewModel picks the surface (matches the product rule "no toasts"):

| Situation | Surface |
|---|---|
| Initial load failed, nothing to show | `Async.Failure(previous = null)` → full-screen `AppErrorState` (core:ui → `PurrErrorState`) with "Tekrar dene" |
| Refresh failed while content is visible | `Async.Failure(previous)` → content + `AppErrorDialog` ("Tekrar dene" inside; dismiss → `dismissFailure()` keeps the old data) |
| A non-fetch action failed (save, delete, upload) | Its own `AppError?` field (e.g. `saveError`) → `AppErrorDialog`; `Server` also shows the error code and "Destekle iletişime geç" |
| `Validation` | Inline under each field (`fieldErrors` state), **not** a dialog |
| `Unauthorized` | Handled globally by the session layer (back to Login); screens don't render it |
| User cancelled Google sign-in | Nothing — stay on Login |

## 6. Where logic lives

| Case | Pattern |
|---|---|
| Trivial (one or two branches) | Directly in `onEvent` |
| Client-only rule (validation, draft → request shaping, eligibility for a UI option) | Pure function / use case in `domain`, unit-tested |
| Combining several repositories or reused in ≥ 2 ViewModels | Use case in `domain/usecase` exposing one `suspend` function or one `Flow` |
| Business rule the backend owns | Contract field — don't reimplement |

Use cases: `class GetFooBar(private val repo: FooRepository) { suspend operator fun invoke(params): AppResult<Bar> }`. No base `Interactor` class.

## 7. Analytics

Per-feature helper (`HealthAnalytics`) injected into the ViewModel when dedupe/gating is needed; event names are constants in that helper, never inline strings in the ViewModel.
