# Kotlin Multiplatform Conventions

Module layout, source sets, DI, platform entry points and libraries. Read with `architecture.md`.

Core rule: **shared code is the default.** ViewModels, domain, data, Compose UI and the DS all live in `commonMain`. Only genuinely platform-specific APIs go to `androidMain` / `iosMain`.

## 1. Module map

```
androidApp/                 Android entry: Application, MainActivity, FCM service, BuildConfig → AppConfig
iosApp/                     Xcode project (Swift): AppDelegate/App, push registration, Google Sign-In SDK bridge
shared/                     Umbrella KMP module: App() root composable, root navigation, Koin graph assembly,
                            MainViewController() for iOS. Exports the single iOS framework "Shared".
build-logic/convention/     Gradle convention plugins (see §3)

core/
  common/                   AppResult, AppError, DispatcherProvider, Clock, logging facade      (no Compose)
  model/                    Cross-feature domain models & ids: PetId, PetSummary, Species, …    (no Compose)
  network/                  Ktor HttpClient, auth/refresh, safeApiCall, error-body mapping, Json config
  data/                     Session, SelectedPetRepository, DataChangeNotifier, DataStore, SecureStore
  database/                 Room KMP — only when offline cache is needed (passport, emergency QR)
  designsystem/             PurrelloTheme, tokens, Purr* components, icons, fonts, DS strings
  ui/                       MviViewModel, Async + AsyncContent, ObserveEffects, AppErrorState/AppErrorDialog,
                            TabRootScaffold, PetNameFormatter,
                            common strings (common_*, error_*), preview helpers
  navigation/               AppRoute (all NavKeys), Navigator interface, DeepLinkParser
  testing/                  Fakes (FakeSelectedPetRepository, FakeDataChangeNotifier, …), MainDispatcher
                            helper, TestDispatcherProvider                                        (test-only)

feature/
  auth/                     Splash, Login (Google), onboarding
  home/                     Ana sayfa
  health/                   Sağlık: vaccines, allergies, chronic, medications, weight
  documents/                Belgeler: vault, upload, "Veterinerden iste"
  care/                     Bakım: grooming & routine care
  pet/                      Pet profile, add/edit pet, passport, emergency QR, pet switcher
  lostpet/                  Report lost, active alert, nearby alert detail, "Gördüm"
  account/                  Owner profile, notification preferences, settings
```

### Dependency rules (enforced by Gradle — don't work around them)

| Module | May depend on |
|---|---|
| `feature:*` | `core:*` only. **Never another feature.** Cross-feature navigation goes through routes in `core:navigation`. |
| `core:designsystem` | Compose only. No domain, no network. |
| `core:ui` | `core:designsystem`, `core:common`, `core:model` |
| `core:network` | `core:common` |
| `core:data` | `core:common`, `core:model`, `core:network` |
| `core:navigation` | `core:model` (for id types) |
| `shared` | everything; exports only what iOS needs |
| `androidApp` | `shared` |

Two features needing the same thing → move it to a `core` module (or create a new `core:x`). Don't copy code between features.

### Feature module internal layout

```
feature/health/src/commonMain/kotlin/com/purrello/feature/health/
  data/
    remote/HealthApi.kt              interface
    remote/KtorHealthApi.kt          real implementation
    remote/dto/…                     @Serializable DTOs (from docs/api contract)
    fake/FakeHealthApi.kt            returns the contract example JSON verbatim
    mapper/…                         DTO → domain
    HealthRepositoryImpl.kt          internal
  domain/
    model/…  HealthRepository.kt  usecase/…  validation/…
  presentation/
    vaccines/  VaccineListContract.kt   (UiState + Event + Effect)
               VaccineListViewModel.kt  VaccineListRoute.kt  VaccineListScreen.kt  components/
  navigation/HealthEntries.kt        EntryProviderScope extension registering this feature's routes
  di/HealthModule.kt
feature/health/src/commonMain/composeResources/values/strings.xml
feature/health/src/commonTest/kotlin/…
```

- Base package: `com.purrello.<module path>` (e.g. `com.purrello.feature.health`, `com.purrello.core.network`).
- Data implementations are `internal`; only the domain interface and DI module are public.

## 2. Source sets

| Source set | Contains |
|---|---|
| `commonMain` | Everything by default |
| `androidMain` | Android-only `actual`s (Context-backed storage, Credential Manager, Google Maps, share intents), Ktor OkHttp engine |
| `iosMain` | iOS-only `actual`s (Keychain, MapKit via `UIKitView`, `UIActivityViewController`), Ktor Darwin engine, `MainViewController()` |
| `commonTest` | ViewModel, domain, data tests (see `testing.md`) |
| `androidHostTest` | Only tests that truly need Android APIs |

Targets: `androidLibrary { }` (AGP 9 `com.android.kotlin.multiplatform.library`), `iosArm64()`, `iosSimulatorArm64()`.

## 3. Build logic

Convention plugins in `build-logic/convention`, versions only in `gradle/libs.versions.toml`:

| Plugin id | Does |
|---|---|
| `purrello.kmp.library` | KMP targets, JVM 17, namespace from path, common test deps |
| `purrello.kmp.compose` | + Compose Multiplatform, compose compiler, resources config (`packageOfResClass = "com.purrello.<module>.resources"`) |
| `purrello.kmp.feature` | library + compose + Koin + ViewModel + Navigation 3 + `core:{common,model,ui,designsystem,navigation,data}` |

A feature's `build.gradle.kts` should be a few lines. Don't copy dependency blocks between modules.

## 4. expect/actual and platform bridges

- Prefer **an interface in common + implementations per platform, bound in Koin** (`GoogleAuthProvider`, `SecureStore`, `ShareLauncher`, `MapPreview`). Easy to fake in tests.
- Use `expect/actual` for thin primitives only (`AppConfig` defaults, `platformLocale()`, `@Composable expect fun PurrMapPreview(...)`).
- Never let `Context`, `UIViewController`, `NSUserDefaults` etc. appear in `commonMain` signatures.
- iOS-native SDKs (Google Sign-In, Firebase Messaging) are called from Swift and handed to Kotlin through a small Kotlin interface implemented in Swift and registered at startup.

## 5. Dependency injection (Koin)

- One Koin module per core/feature module (`healthModule`, `networkModule`, …). `shared` assembles them in `initKoin(appConfig, platformModule)`, called from `Application.onCreate` (Android) and the iOS `App` init.
- ViewModels: `viewModelOf(::FooViewModel)`; obtained in the Route with `koinViewModel()`; route args via `koinViewModel { parametersOf(route.petId) }`. A ViewModel may take `SavedStateHandle` as a constructor parameter.
- Repositories/APIs: `single`. Use cases: `factory`.
- Bind implementations to interfaces (`singleOf(::HealthRepositoryImpl) { bind<HealthRepository>() }`).
- No `get()` positional chains for more than trivial cases; prefer `singleOf` / `viewModelOf` constructor references.

## 6. Platform entry points

```kotlin
// androidApp — MainActivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }                    // from :shared
    }
}

// shared/iosMain
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
```

`App()` = `PurrelloTheme { AppNavHost() }`. Push/deep-link intents are forwarded to `DeepLinkHandler` in `shared` (see `navigation.md §6`).

## 7. Configuration & environments

`AppConfig(environment, baseUrl, useFakeApi, isDebug)` in `core:common`, provided by the platform entry (Android `BuildConfig` flavors / iOS `.xcconfig`). Feature code reads `AppConfig` from DI; never `if (BuildConfig.DEBUG)` in shared code.

## 8. Storage

| Need | Choice |
|---|---|
| Small preferences (selected pet, dismissed coach marks) | DataStore Preferences (KMP) in `core:data` |
| Tokens / secrets | `SecureStore` interface → Android Keystore-encrypted storage / iOS Keychain |
| Offline data (passport, emergency QR, cached screens) | Room KMP in `core:database` — add when the feature needs it |
| Files (document pages before upload) | App cache dir via platform bridge; deleted after upload |

## 9. Serialization

- `kotlinx.serialization` everywhere: DTOs, routes (`NavKey`), `SavedStateHandle` slices, DataStore JSON.
- **No** `Parcelable` / `@Parcelize`.
- One shared `Json` in `core:network`: `ignoreUnknownKeys = true`, `explicitNulls = false`, `coerceInputValues = true`.
- Every enum crossing the network has an `UNKNOWN` fallback entry.

## 10. Threading

- `viewModelScope` (Main.immediate) for UI orchestration; Ktor suspends off the main thread on its own.
- CPU-heavy work (image downscale, PDF assembly) uses `dispatchers.default` from the injected `DispatcherProvider`.
- Routes collect `StateFlow` with **`collectAsStateWithLifecycle()`** (`lifecycle-runtime-compose`, multiplatform) so collection pauses while the app is in the background on both platforms. Stateless Screens never collect anything — they receive `state`.

## 11. Library checklist

Pin exact versions when the Gradle project is created; update deliberately.

| Need | Library |
|---|---|
| UI | Compose Multiplatform (`org.jetbrains.compose`); Material 3 only **inside** `core:designsystem` |
| Resources / strings | `compose.components.resources` |
| ViewModel / lifecycle | `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`, `lifecycle-viewmodel-navigation3`, `lifecycle-viewmodel-savedstate` |
| Navigation | `org.jetbrains.androidx.navigation3:navigation3-ui` |
| DI | Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`) |
| Network | Ktor client (`core`, `content-negotiation`, `serialization-kotlinx-json`, `auth`, `logging`; OkHttp / Darwin engines) |
| Serialization / time / collections | kotlinx.serialization, kotlinx-datetime, kotlinx-collections-immutable |
| Local storage | DataStore Preferences (KMP), Room KMP (when needed) |
| Images | Coil 3 (`coil-compose`, `coil-network-ktor3`) |
| File & camera picking | FileKit |
| QR rendering | qrose (candidate — confirm at setup) |
| Logging | Kermit |
| Tests | kotlin-test, kotlinx-coroutines-test, Turbine, assertk, Ktor `MockEngine`; Mokkery only when a fake is impractical |
| Static analysis | detekt + compose rules, ktlint |
