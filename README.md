# Purrello

Personal pet health & care tracker — **Kotlin Multiplatform + Compose Multiplatform** client for Android and iOS (UI and logic shared). The backend is a separate Go project; this repo designs the API contracts (`docs/api/`) and builds the client.

Start with **[AGENTS.md](AGENTS.md)** (rules, workflow) and **[ADR-0001](docs/adr/0001-client-architecture.md)** (why this architecture).

## Requirements

- **JDK 17+ (21 recommended) on your shell PATH.** Android Studio's bundled JBR covers the IDE, but Gradle
  from the terminal — and Xcode's "Compile Kotlin Framework" phase — use the shell's JDK. With JDK 11 you get
  `Gradle requires JVM 17 or later`. Fix once:

  ```bash
  brew install --cask temurin@21
  echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc && source ~/.zshrc
  java -version   # expect 21
  ```

  The Xcode build phase resolves a JDK on its own (`/usr/libexec/java_home`, falling back to Android Studio's
  JBR), so iOS builds work even from a GUI launch that never reads `~/.zshrc`.
- Android Studio (latest stable)
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
