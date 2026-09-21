# Anti-Patterns

Known mistakes. Presentation is **ViewModel + Navigation 3 with unidirectional data flow** (`architecture.md`).

## Architecture

### Don't put navigation or UI types in a ViewModel
No `Navigator`, `NavBackStack`, `NavKey` pushes, `Composable`, `Res.*`, `Painter`, `Color` or platform types in a ViewModel. Navigation leaves as a `FooEffect`; the Route maps it to a lambda supplied by the feature's entry.

### Don't emit effects through `SharedFlow`
`SharedFlow` drops emissions with no active collector. Effects go through `MviViewModel`'s buffered `Channel`, collected once with `ObserveEffects` (Main.immediate).

### Don't hand-roll loading state
No `isLoading: Boolean` + `data: T?` + `error: AppError?` triples, and no `screenState` + nullable `content` pair — they allow states that can't exist and force every load to update several fields. One `Async<T>` field per asynchronously loaded slice; `isRefreshing` is derived, not stored.

### Don't drop the data behind a failed refresh
`toFailure(error)` / `toLoading()` keep `previous` on purpose: that is what makes a refresh failure a dialog over the existing content instead of a full-screen error. Don't overwrite the field with a bare `Async.Failure(error)` when data is on screen.

### Don't model visible UI as an Effect
Dialogs, inline errors, banners, loading and "refreshing" flags are **State**. If a rotation or process death would lose it, it's wrong as an Effect.

### Don't let a Screen depend on the ViewModel
`FooScreen(state, onEvent)` only. `koinViewModel()`, `collectAsStateWithLifecycle()` and `ObserveEffects` live in `FooRoute`. A Screen that needs a ViewModel can't be previewed or UI-tested.

### Don't lose unsubmitted input on process death
Form fields, search text, filters, idempotency keys → `SavedStateHandle` (`saved { }`, `@Serializable`). Don't persist fetched content.

### Don't add public functions next to `onEvent`
A ViewModel's public surface is `state`, `effects` and `onEvent`. A `fun refresh()` or `fun onRetry()` called from the UI breaks the single-intent channel — add an `Event` instead. Keep `MutableStateFlow`/`Channel` private to `MviViewModel`.

### Don't fire overlapping loads
Retry, pull-to-refresh, pet switch, filter and search must cancel the in-flight job (`loadJob?.cancel()`) or use `flatMapLatest`. Out-of-order completions must never show stale data — especially after a pet switch.

### Don't keep a private "current pet" copy
Read it from `SelectedPetRepository`. A tab that caches its own pet id shows the wrong pet after a switch from another tab or a push notification.

### Don't pass results back through navigation
After a mutation, notify `DataChangeNotifier`; the previous screen refreshes. Don't invent result keys on the back stack.

### Don't inject one repository into another
Extract the shared lower-level API/DAO/cache and inject that into both, or orchestrate in a use case.

### Don't make feature modules depend on each other
`feature:health` must not import from `feature:pet`. Shared models → `core:model`; shared routes → `core:navigation`; shared UI → `core:ui` / `core:designsystem`.

### Don't create a domain layer for its own sake
No pass-through use cases (`GetPetUseCase` that only calls `repository.getPet`). Add one when it combines, is reused, or holds a client rule (`architecture.md §6`).

## API contract

### Don't consume an endpoint that has no contract doc
Write/extend `docs/api/…` first. "I'll ask backend what it returns" leads to screens shaped by accident.

### Don't put UI copy in contracts
No sentences, button titles, colors or icons in responses. Send enums + parameters; the client owns copy.

### Don't let DTOs escape the data layer
ViewModels and domain never see `…Dto`. Map in `data/mapper`.

### Don't crash on unknown enum values or missing optionals
Every network enum has `UNKNOWN`; `Json { ignoreUnknownKeys = true; coerceInputValues = true; explicitNulls = false }`.

### Don't show backend `error.message` to users
Map `error.code` to a string resource; fall back to the generic message.

### Don't reimplement backend business rules
Next-dose dates, reminder schedules, lost-alert radius, storage quota: the backend computes them; if the UI needs them, they belong in the response.

## Strings

### Don't hardcode user-visible text
Not in `Text("…")`, not in `contentDescription`, not in dialog titles, not "just for now". `scripts/check-hardcoded-strings.sh` must pass.

### Don't concatenate strings or build Turkish suffixes by hand
`name + "'un aşıları"` is wrong for most names. Use positional placeholders and `PetNameFormatter.possessive(...)`.

### Don't resolve strings or format dates in the ViewModel
Expose `LocalDate`, `Int` grams, enums, `AppError`; format and resolve in composables.

## Errors

### Don't `try/catch` outside `safeApiCall` and platform bridges
Data returns `AppResult`; ViewModels use `onSuccess` / `onFailure` / `fold`. Never swallow `CancellationException`.

### Don't mix error styles in one flow
No `runCatching`, `Result`, nullable-as-error and `AppResult` side by side. `AppResult` end to end.

### Don't show a toast or snackbar
The product has none. Full-screen error, error dialog, inline field error or nothing (`ui-conventions.md §3`).

### Don't silently drop failures
`getOrNull()` only where failure is truly irrelevant; otherwise log with Kermit and handle.

## Compose

### Don't use Material 3 or raw values in features
Use `Purr*` components and theme tokens. No hex colors, no `12.dp` literals, no `RoundedCornerShape` in features.

### Don't forget stability
`@Immutable` on UI state and content classes; `ImmutableList` / `ImmutableMap` instead of `List` / `Map`.

### `CircleShape` clip + border artifact
`.clip(CircleShape).border(…, CircleShape)` leaves a blurry 1 px edge. Use `background(borderColor, CircleShape)` + `padding(borderWidth)` + `clip(CircleShape)` (applies to the pet-tab avatar ring).

### Don't put primary CTAs inside scrolling content
They belong in `PurrBottomActionBar`.

## Coroutines & platform

### Don't hardcode dispatchers in testable classes
Inject `DispatcherProvider`.

### Don't leak platform types into `commonMain`
No `Context`, `UIViewController`, `NSUserDefaults` in shared signatures — interface + platform implementation bound in Koin.

### Don't use `Parcelable`
kotlinx.serialization for routes, saved state and DTOs.

### Don't call `viewModel()` without an initializer / Koin in common code
There's no reflection on iOS. Use `koinViewModel()` (Koin provides the factory) — never the no-arg `viewModel()`.
