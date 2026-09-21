# Purrello

Personal pet health & care tracker — **Kotlin Multiplatform + Compose Multiplatform** client for Android and iOS (UI and logic shared). The backend is a separate Go project; this repo designs the API contracts (`docs/api/`) and builds the client.

Start with **[AGENTS.md](AGENTS.md)** (rules, workflow) and **[ADR-0001](docs/adr/0001-client-architecture.md)** (why this architecture).

## Requirements

- Android Studio (latest stable) with JDK 17+ (bundled JBR is fine)
- Xcode 16+ for iOS
- No backend needed for development: debug builds use fake APIs that return the contract example JSON (`AppConfig.useFakeApi`).

## Run

```bash
./gradlew :androidApp:installDebug          # Android (debug = DEV + fake API)
open iosApp/iosApp.xcodeproj                # iOS — the "Compile Kotlin Framework" phase builds :shared
./gradlew allTests                          # all commonTest suites (Android host + iOS simulator)
scripts/check-hardcoded-strings.sh          # no user-visible literals (localization.md)
```

## Modules

```
androidApp/   Android entry (Application, MainActivity, BuildConfig → AppConfig)
iosApp/       Xcode project (SwiftUI host, AppDelegate → startPurrelloIos)
shared/       App(), root navigation (Navigation 3), Koin graph, iOS framework "Shared"
build-logic/  convention plugins: purrello.kmp.library / .kmp.compose / .kmp.feature / .android.application
core/         common · model · network · data · designsystem · ui · navigation · testing
feature/      auth · home · health · documents · care · pet · lostpet · account
docs/         product (design system, navigation, handoff) · api (contracts) · adr
```

Details and dependency rules: `.agents/rules/kmp-conventions.md`.
