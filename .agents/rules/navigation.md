# Navigation (Navigation 3)

Navigation 3 treats the back stack as **state we own** (a list of `NavKey`s). Product-level navigation (tabs, headers, pet switching, flows) is specified in `docs/product/app-navigation.md` — this file is how we implement it.

## 1. Model: one root stack above a tab host

```
Root back stack (NavBackStack<AppRoute>)
 ├─ Splash                      start; decides Login vs Main from the session
 ├─ Login / Onboarding
 ├─ Main                        tab host: TabBar + current tab root (Home, Health, Documents, Care, Pet)
 ├─ PetProfile(petId) …         detail screens are pushed HERE, above Main
 └─ AddVaccine(petId) …         full-screen modals are pushed HERE too
```

- **Tab roots are not back-stack entries.** `Main` renders the selected `MainTab` (enum, `rememberSaveable`) and keeps each tab's scroll/UI state with a `SaveableStateHolder`.
- **Details and modals are pushed on the root stack.** That hides the tab bar automatically (spec: tab bar is hidden on detail and modal screens) and returning pops straight back to the same tab with its state intact. We therefore don't need per-tab back stacks.
- Re-tapping the active tab scrolls its root to top.
- Long-press on the pet tab opens the pet switcher sheet owned by `Main` (+ haptic). The sheet writes `SelectedPetRepository`; tabs react.

## 2. Routes (`core:navigation`)

All routes live in one sealed hierarchy so any feature can navigate anywhere without depending on another feature.

```kotlin
@Serializable sealed interface AppRoute : NavKey

/** Full-screen modal: slides up, closes with ×, primary action pinned at the bottom. */
sealed interface ModalRoute : AppRoute

@Serializable data object Splash : AppRoute
@Serializable data object Login : AppRoute
@Serializable data object Main : AppRoute

@Serializable data class PetProfile(val petId: PetId) : AppRoute
@Serializable data class Passport(val petId: PetId) : AppRoute
@Serializable data class EmergencyQr(val petId: PetId) : AppRoute
@Serializable data class VaccineDetail(val vaccinationId: VaccinationId) : AppRoute
@Serializable data class LostAlertDetail(val alertId: LostAlertId) : AppRoute
@Serializable data object OwnerProfile : AppRoute

@Serializable data object AddPet : ModalRoute
@Serializable data class AddVaccine(val petId: PetId, val mode: VaccineEntryMode) : ModalRoute
@Serializable data class EditVaccine(val vaccinationId: VaccinationId) : ModalRoute
@Serializable data class AddDocument(val petId: PetId) : ModalRoute
@Serializable data class AddCare(val petId: PetId) : ModalRoute
@Serializable data class ReportLost(val petId: PetId) : ModalRoute
```

- Route params are **ids and small enums only** — never whole objects (they're serialized into saved state and must survive process death).
- Id types in routes are `@Serializable` value classes from `core:model`.
- Adding a route = add it here + register it in `appRouteSerializersModule` (AppRouteSerialization.kt) + an `entry<…>` in the owning feature (§4).

## 3. Back stack & saved state (`shared`)

```kotlin
// core:navigation — every AppRoute registered explicitly (iOS has no reflection); NavigationTest guards it.
val appRouteSavedStateConfiguration = SavedStateConfiguration { serializersModule = appRouteSerializersModule }

// shared — rememberAppNavigator() = rememberNavBackStack(appRouteSavedStateConfiguration, Splash) + saved tab
@Composable
fun AppNavHost(navigator: AppNavigator) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = navigator::back,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),   // a ViewModel per entry, cleared on pop
        ),
        entryProvider = entryProvider {
            authEntries(navigator)
            mainEntry(navigator)            // tab host
            homeEntries(navigator)
            healthEntries(navigator)
            documentsEntries(navigator)
            careEntries(navigator)
            petEntries(navigator)
            lostPetEntries(navigator)
            accountEntries(navigator)
        },
    )
}
```

- `rememberViewModelStoreNavEntryDecorator()` is mandatory — without it ViewModels leak across entries.
- Modal transitions: entries for `ModalRoute`s are registered with metadata for a slide-up transition; detail entries use the default push.

## 4. Feature entries

Each feature registers its routes in `navigation/<Feature>Entries.kt` and maps Route lambdas to the `Navigator`. Screens and ViewModels never see the navigator.

```kotlin
// feature/health/navigation/HealthEntries.kt
fun EntryProviderScope<NavKey>.healthEntries(navigator: Navigator) {
    entry<VaccineDetail> { route ->
        VaccineDetailRoute(
            viewModel = koinViewModel { parametersOf(route.vaccinationId) },
            onBack = navigator::back,
            onEdit = { id -> navigator.navigate(EditVaccine(id)) },
        )
    }
    entry<AddVaccine>(metadata = ModalTransition) { route ->
        AddVaccineRoute(
            viewModel = koinViewModel { parametersOf(route.petId, route.mode) },
            onClose = navigator::back,
        )
    }
}
```

Tab root composables (`HomeTabRoot`, `HealthTabRoot`, …) are exposed by each feature as plain `@Composable` functions that `Main` calls; they receive the same `Navigator`.

## 5. Navigator (`core:navigation`)

```kotlin
interface Navigator {
    fun navigate(route: AppRoute)
    fun back()
    fun replaceAll(route: AppRoute)            // e.g. Splash → Main, logout → Login
    fun switchTab(tab: MainTab)                // pops to Main, selects tab
}
```

`AppNavigator` (in `shared`) implements it over the `NavBackStack`. Guard against double pushes (same route already on top → ignore).

## 6. Auth gate, deep links and push

- `Splash` observes the session: `LoggedIn` → `replaceAll(Main)`, `LoggedOut` → `replaceAll(Login)`. A global session-expired signal (refresh failed) → `replaceAll(Login)`.
- Push / universal links → `DeepLinkParser` (`core:navigation`) turns the payload into `DeepLink(targetPetId: PetId?, routes: List<AppRoute>, tab: MainTab?)`.
- `DeepLinkHandler` (`shared`): if `targetPetId` differs from the selected pet, **switch the selected pet first** (spec: "Bildirimden gelinirse ilgili pete otomatik geçilir"), then `switchTab` / `navigate`. If the user isn't logged in, keep the deep link pending until login completes.
- Lost-pet alert push opens `LostAlertDetail(alertId)`.

## 7. Results between screens

- **Don't** return results through navigation. After a successful mutation the repository notifies `DataChangeNotifier`; the screen underneath refreshes itself (`architecture.md §2`).
- Pickers (vet, breed, allergy search, "Gördüm" details) are **bottom sheets owned by the screen**, not routes.

## 8. Back handling

- System back (Android) and edge swipe (iOS) are handled by `NavDisplay`.
- A modal with unsaved changes intercepts back with the Compose Multiplatform back handler (`NavigationBackHandler` / `BackHandler`, whichever the pinned version provides) and shows the discard `PurrDialog`. The ViewModel decides (`hasUnsavedChanges` in state); the handler only forwards an `Event`.
- Never call `navigator.back()` from a ViewModel; send an `Effect`, the Route calls the lambda.
