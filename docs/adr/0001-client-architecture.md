# ADR-0001 — Client architecture

- **Status:** Accepted
- **Date:** 2026-09-20
- **Scope:** Purrello client (Android + iOS). Backend (Go) is out of scope.

## Context

- One team builds Android and iOS. Both **UI and logic** are shared via Kotlin Multiplatform + Compose Multiplatform.
- The backend is a separate Go project. The client team **designs the API contract** per screen/action; the backend implements it.
- The app must be localizable later (Turkish first) — no hardcoded strings.
- SweatIn (our previous KMP app) used Pure Decompose + a single `shared` module + ad-hoc strings. It worked, but we reviewed each choice again instead of copying it.

## Decisions

### 1. Presentation & navigation: androidx ViewModel + Navigation 3 (not Decompose)

- Navigation 3 is available in Compose Multiplatform since 1.10 and is the JetBrains/Google-backed direction; the back stack is plain state we own (`NavBackStack`), which fits a tab host + root stack model.
- Multiplatform `ViewModel` (`org.jetbrains.androidx.lifecycle`) is scoped per back-stack entry with `lifecycle-viewmodel-navigation3`, and Koin has first-class support (`viewModelOf`, `koinViewModel`).
- Larger ecosystem and lower onboarding cost than Decompose; Decompose's last stable line (3.5, March 2025) is maintained by one author with v4 still in progress.
- **Kept from SweatIn:** unidirectional data flow, single immutable UI state, dumb/stateless screens, Route ↔ Screen split, cancelling in-flight loads, surviving process death, no Android types in shared code.
- **Changed:** `Component` → `ViewModel`; parent callbacks → `Event`s collected in the Route composable (a ViewModel outlives composition, so it can't hold navigation lambdas); function-per-intent → sealed `Action` + `onAction` (the stateless Screen then takes one `onAction` lambda instead of a dozen).
- **Trade-off accepted:** tab-level multiple back stacks aren't built in. We avoid needing them: details and modals are pushed on the **root** stack above the tab host (which also hides the tab bar as the navigation spec requires).

### 2. API contract: screen-shaped BFF with raw data

- One `GET /v1/screens/{screen}` per screen, sections shaped to map 1:1 onto DS components.
- Payloads carry **data** (ids, enums, numbers, ISO dates, user content, localized catalog names) — **not** UI copy. Static copy is resolved from client string resources, keeping localization in one place.
- Mutations are resource-style (`POST /v1/pets/{petId}/vaccinations`, …).
- Rejected: server-driven UI (UI knowledge and translations leak into the backend); plain resource REST (client stitches 3–5 calls per screen, more round trips and more client logic).

### 3. Modules: `core:*` + `feature:*` + `build-logic`

- Follows the Kotlin docs' recommended structure (separate platform entry modules; required with AGP 9) plus feature modularization.
- Compiler-enforced boundaries (features can't depend on each other), faster incremental builds, per-module string resources.
- `shared` is the umbrella: assembles DI + navigation and exports the single iOS framework.

### 4. Strings: Compose Multiplatform resources from day one

- `values/` = Turkish (default), more locales added as `values-xx/`. ViewModels expose typed state; only composables resolve strings.

### 5. Errors: typed `AppResult<T>` / `AppError`

- SweatIn used `Either<Throwable, T>`. We need **typed** errors (backend error codes, field errors, "no connection" vs "server") to pick the right feedback surface (dialog / inline / full-screen). Exceptions are caught in exactly one place (`core:network` `safeApiCall`).

## Consequences

- Every screen has: contract doc → DTOs + fake API → repository → ViewModel → Route/Screen → strings → tests.
- Build setup cost up-front (convention plugins) — paid once.
- Revisit if Navigation 3 on iOS shows gesture/lifecycle issues we can't fix at the host level.

## Sources

- Kotlin docs — Recommended KMP project structure: https://kotlinlang.org/docs/multiplatform/multiplatform-project-recommended-structure.html
- Kotlin docs — Navigation 3 in Compose Multiplatform: https://kotlinlang.org/docs/multiplatform/compose-navigation-3.html
- Kotlin docs — Multiplatform ViewModel: https://kotlinlang.org/docs/multiplatform/compose-viewmodel.html
- Kotlin docs — Localizing strings: https://kotlinlang.org/docs/multiplatform/compose-localize-strings.html
- Decompose releases: https://github.com/arkivanov/Decompose/releases
- Navigation comparison (2026): https://mvpfactory.io/blog/compose-multiplatform-navigation-in-2026-decompose-vs-voyage/
