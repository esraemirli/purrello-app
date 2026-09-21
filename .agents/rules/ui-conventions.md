# UI Conventions

Compose Multiplatform UI over ViewModels (`architecture.md`). Visual decisions come from the Purrello design system — `docs/product/design-system.md` (tokens, components, live DS artifact) and `docs/product/app-navigation.md` (headers, tab bar, flows).

## 1. Design system (`core:designsystem`)

- Theme: `PurrelloTheme { }` provides `PurrelloTheme.colors`, `.typography`, `.spacing`, `.shapes`, `.sizes`. Light **and** dark are both required; dark uses warm neutrals.
- Components are prefixed **`Purr`** and are the only building blocks features use. **Never use Material 3 components directly in features** — M3 may only be used inside DS implementations.
- **No raw values in features:** no hex colors, no `dp`/`sp` literals for spacing/sizes/type, no `RoundedCornerShape(12.dp)`. Use tokens (`spacing.screenEdge` = 24, 4 px grid; `shapes.card` = 16; `sizes.button` = 52, `sizes.minTouch` = 44, `sizes.chip` = 36). Missing token → add it to the DS, don't inline it.
- Typography: Poppins scale (`wordmark`, `display`, `title1…3`, `bodyLg`, `body`, `bodySm`, `button`, `label`, `caption`, `micro`, `amount`).

| Group | Components |
|---|---|
| Brand | `PurrBrandHero` |
| Navigation | `PurrAppHeader` (expanded + compact), `PurrTabBar`, `PurrDetailBar` |
| Actions | `PurrButton`, `PurrBottomActionBar` |
| Inputs | `PurrTextField`, `PurrDropdown`, `PurrFilterChip`, `PurrFileUpload`, `PurrSwitch`, `PurrSegmentedControl`, `PurrChoiceCards`, `PurrStepIndicator`, `PurrPetSwitcher` |
| Feedback | `PurrDialog`, `PurrBanner`, `PurrCoachMark`, `PurrBadge`, `PurrErrorState`, `PurrEmptyState`, `PurrLoadingState` (+ `AppErrorState` / `AppErrorDialog` in `core:ui`, which map `AppError` to copy) |
| Containers | `PurrListRow`, `PurrBottomSheet`, `PurrCard` |
| Data display | `PurrStatTile`, `PurrKeyValueRow`, `PurrPassportCard`, `PurrQrCard`, `PurrMapPreview` |
| Media | `PurrAsyncImage` (Coil), `PurrAvatar` |

`PurrErrorState`, `PurrEmptyState`, `PurrLoadingState`, `PurrCard`, `PurrAsyncImage` and `PurrAvatar` are implementation-level additions not yet drawn in the DS artifact — design them there when first built.

A new component goes into the DS first (with previews in both themes), then into the feature.

## 2. Screen structure

- **Route / Screen split** (`architecture.md §4.4`). Screen signature:

```kotlin
@Composable
fun FooScreen(
    state: FooUiState,
    onEvent: (FooEvent) -> Unit,
    modifier: Modifier = Modifier,
)
```

- Screens are dumb: they render `state` (wrapping each `Async` slice in `AsyncContent`) and send `FooEvent`s. No business rules, no repository access, no navigation, no `koinInject`, no `ViewModel` references (the Route holds those). Local UI-only state (scroll, focus, sheet expansion, animation) is fine with `remember` / `rememberSaveable`.
- Split large screens into `components/` composables in the same package; each non-trivial one gets its own preview.
- Lists: `LazyColumn` with stable `key = { it.id.value }` and `contentType`.

### Headers (from the navigation spec)

| Screen kind | Header | Tab bar |
|---|---|---|
| Tab root (Ana sayfa, Sağlık, Belgeler, Bakım, Pet) | `PurrAppHeader` expanded "sıcak başlık" (bg-hero, bottom corners 28, paw prints, 48 px filled + button) collapsing to **compact** 56 px on scroll (bottom corners 20 + soft shadow; filter chips pinned inside). Owner avatar top-right → `OwnerProfile`. | Visible |
| Detail (pushed) | `PurrDetailBar`: back + centered title, transparent → `bg-surface` + divider on scroll | Hidden |
| Full-screen modal (add/edit flows) | × close (top-left), title, **primary action pinned in `PurrBottomActionBar`** ("Kaydet") | Hidden |
| Welcome moments (splash, login, onboarding, empty states) | `PurrBrandHero` — coral + paw prints | Hidden |

- Health / Documents / Care headers show the selected pet's mini avatar + name in the subtitle; no pet dropdown there. Home keeps the pet switcher dropdown.
- Primary CTAs are **never** at the end of scrolling content — always in `PurrBottomActionBar`.
- Insets: the screen scaffold owns edge-to-edge insets. Don't sprinkle `systemBarsPadding()` in screen bodies.

## 3. Feedback — no toasts, no snackbars

| Situation | Surface |
|---|---|
| Initial load failed, nothing to show (`Async.Failure(previous = null)`) | Full-screen `AppErrorState` (title, body, "Tekrar dene"); header stays so the user can go back |
| System error while content is visible — refresh (`Async.Failure(previous)`) or an action (save/upload) | `AppErrorDialog` (alertdialog). "Tekrar dene" **inside** the dialog. No connection: Tekrar dene / Kapat. Account/server: Tekrar dene / Destekle iletişime geç / Kapat + error code |
| Form field invalid | Inline error under the field (`PurrTextField(error = …)`), never a dialog |
| User cancelled Google sign-in | Nothing — back on Login |
| Success | The result itself is the feedback: modal closes, list updates, status changes. No "Kaydedildi" toast |
| Destructive confirmation (delete pet, discard changes, publish lost report) | `PurrDialog` with danger action |
| Persistent status (overdue vaccine, active lost report, expiring certificate) | `PurrBanner` in the content |

Dialogs are driven by **State** (`errorDialog`, `confirmDialog`) and dismissed via an `Event` — never by an Effect.

## 4. Visual & accessibility rules

- No white text on coral (contrast 2.3:1) — the logo is the only exception.
- Status colors (success / warning / danger) always come with an **icon and text**, never color alone.
- Minimum touch target 44 × 44; chips/small buttons 36 high but padded to 44 touch.
- Every icon-only control has a `contentDescription` from resources; decorative images use `null`.
- Custom accessibility actions where the spec asks (pet tab → "Pet değiştir").
- Support font scaling: use typography tokens (sp), let rows grow; never fix text container heights.
- QR card is white in both themes.
- Icons: Phosphor Regular via the DS icon set (`PurrIcons.X`), not ad-hoc vectors.

## 5. Composable parameter order

```kotlin
@Composable
fun PurrListRow(
    // 1. required values
    title: String,
    // 2. required callbacks
    onClick: () -> Unit,
    // 3. modifier
    modifier: Modifier = Modifier,
    // 4. optional values
    subtitle: String? = null,
    badge: BadgeModel? = null,
    enabled: Boolean = true,
    // 5. optional callbacks
    onLongClick: (() -> Unit)? = null,
    // 6. trailing content slot
    trailing: (@Composable () -> Unit)? = null,
)
```

DS components take **already-resolved strings** (the screen resolves resources); they never receive enums from feature domains.

## 6. Previews

- `@PurrPreviews` = light + dark + large font. Every Screen and every non-trivial component has one.
- Screen previews pass a sample `FooUiState` (Loading, Content, Error, Empty where relevant) — no ViewModel, no Koin.
- Put previews in `FooScreenPreviews.kt` next to the screen; sample data in `FooPreviewData.kt`. Sample *data* may be literals (pet "Boncuk"); UI *copy* never is.
