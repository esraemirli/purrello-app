Write unit tests for $ARGUMENTS following `.agents/rules/testing.md` (the full conventions — this is the short version).

## Scope first

1. `git diff origin/main...HEAD` — test only what changed or was added on the branch.
2. Brand-new class → cover all public behavior. Existing class → update tests only where the change broke them.

## Setup

- `commonTest`, kotlin.test + assertk + Turbine + kotlinx-coroutines-test (+ Ktor `MockEngine` for data).
- ViewModel tests extend `MainDispatcherTest` (`core:testing`); build the ViewModel directly with **fakes** and `SavedStateHandle()`. No Koin.
- Mokkery only when a fake is impractical. No MockK / Robolectric in `commonTest`.

## ViewModel pattern

- `val states = vm.state.testIn(backgroundScope)`, `val events = vm.events.testIn(backgroundScope)` — before the action; never nested `test {}`.
- First `awaitItem()` is the current state — assert it. Then every emission in order as `initial.copy(...)`.
- Navigation / one-shot outcomes: assert on `events`.
- `expectNoEvents()` for no-op branches. Cancel turbines at the end; verifications after.

## Must cover

Success · full-screen error (no content) · error dialog (content present) · inline `Validation` errors (forms) · retry · empty · **load cancellation** (second load wins, first result never shows) · **pet switch** (tab screens) · **SavedStateHandle restoration** (forms/filters) · no-op branches.

## Data / contract

- Fixture = contract example JSON copied verbatim from `docs/api/...` into `fixtures/<Screen>Fixtures.kt` (comment with the doc link).
- Decode with the production `Json`, map to domain, assert the full expected object (hardcoded).
- Add: unknown enum value → `UNKNOWN`; missing optional field → default; error bodies → the right `AppError`.

## Style

- `` `GIVEN … WHEN … THEN …` `` names; one blank line before each `@Test`; grouped setup; blank line before assertions.
- `assertThat(actual).isEqualTo(expected)` on full values. No `isInstanceOf`, no computed expectations.
