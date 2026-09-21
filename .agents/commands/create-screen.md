Create a new screen following the Purrello architecture (KMP + Compose Multiplatform, **ViewModel + Navigation 3**, unidirectional data flow). Screen name and feature module: $ARGUMENTS

## Before you start

1. Read `.agents/rules/architecture.md`, `navigation.md`, `api-contract.md`, `localization.md`, `ui-conventions.md`.
2. The screen's contract must exist in `docs/api/<feature>/…` (status Draft or later). If it doesn't, run `/define-api-contract` first.
3. Open the closest existing screen in the same feature module and match its structure.

## Files (all `commonMain` of `feature/<feature>`)

Package `com.purrello.feature.<feature>`; `<screen>` = lower-case package for the screen.

**Data**
- `data/remote/dto/<Screen>ScreenResponse.kt` (+ nested DTOs, `…Request`s) — mirror the contract exactly; enums with `@SerialName` + `UNKNOWN`.
- `data/remote/<Feature>Api.kt` — add methods to the interface; implement in `Ktor<Feature>Api` (via `safeApiCall`) and `Fake<Feature>Api` (returns the contract example JSON verbatim).
- `data/mapper/<Screen>Mapper.kt` — DTO → domain.
- `data/<Feature>RepositoryImpl.kt` — add methods; mutations notify `DataChangeNotifier`.

**Domain**
- `domain/model/…` — domain models, value-class ids (shared ones in `core:model`).
- `domain/<Feature>Repository.kt` — add methods returning `AppResult<…>`.
- `domain/usecase/…` / `domain/validation/…` — only if `architecture.md §6` calls for it.

**Presentation** (`presentation/<screen>/`)
- `<Screen>Contract.kt` — the MVI triple: `@Immutable <Screen>UiState` (defaults for every field, fetched data as `Async<…>`, one `Async` per independently loading section, `ImmutableList` for lists, plus action-error / field-error state), `sealed interface <Screen>Event` (one entry per user intent, including `RetryClicked` / `ErrorDismissed`) and `sealed interface <Screen>Effect` (parent-owned one-shot outcomes only; omit if none).
- `<Screen>ViewModel.kt` — `class <Screen>ViewModel(...) : MviViewModel<<Screen>UiState, <Screen>Event, <Screen>Effect>(<Screen>UiState())`: `override fun onEvent` with an exhaustive `when`, load in `init`, `loadJob` cancellation, `SavedStateHandle` for unsubmitted input, `AppError` → surface mapping. No public functions besides `onEvent`.
- `<Screen>Route.kt` — `koinViewModel`, `collectAsStateWithLifecycle`, `ObserveEffects`, navigation lambdas.
- `<Screen>Screen.kt` — stateless `(state, onEvent, modifier)`, `AsyncContent` for each `Async` slice, `Purr*` components only, strings via `stringResource`.
- `<Screen>ScreenPreviews.kt` + `<Screen>PreviewData.kt` — `@PurrPreviews` for Loading / Content / Error / Empty.
- `components/…` — extracted sub-composables.

**Wiring**
- Route: add the `@Serializable` `NavKey` to `AppRoute` in `core:navigation` (`ModalRoute` for full-screen add/edit flows). Args = ids / small enums only.
- Entry: register `entry<Route>` in `navigation/<Feature>Entries.kt`, mapping Route lambdas to `Navigator`.
- DI: `viewModelOf(::<Screen>ViewModel)` (+ API/repository bindings) in `di/<Feature>Module.kt`.
- Strings: every text in `composeResources/values/strings.xml` of the feature (keys per `localization.md §2`); common ones from `core:ui`.

**Tests** (`commonTest`) — run `/write-unit-tests` for the ViewModel, the DTO decoding (contract fixture) and any use case/validator.

## Done when

- [ ] `scripts/check-hardcoded-strings.sh` passes.
- [ ] Light + dark previews render; large font doesn't clip.
- [ ] Loading, content, empty, full-screen error (`Async.Failure(previous = null)`), refresh-error dialog (`Async.Failure(previous)`) and inline field errors (if a form) are all reachable from state.
- [ ] Works against `Fake<Feature>Api`; switching `useFakeApi` needs no code change.
- [ ] Pet switch and process death behave (state reloads for the new pet; unsubmitted input survives).
- [ ] Tests pass on Android and iOS targets (`./gradlew :feature:<feature>:allTests`).
