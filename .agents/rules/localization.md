# Localization & Strings

**Every user-visible string lives in string resources.** No literals in Kotlin UI code — not for labels, buttons, placeholders, dialog titles, empty states, `contentDescription`s, accessibility action labels, share texts or client-built notification channel names. Turkish is the first language; the app must be translatable later without touching Kotlin code.

## 1. Where strings live

Compose Multiplatform resources (`org.jetbrains.compose.resources`), one `Res` per module:

```
feature/health/src/commonMain/composeResources/
  values/strings.xml          ← Turkish (default / fallback)
  values-en/strings.xml       ← added when English ships
```

| Strings used by | Live in |
|---|---|
| One feature | That feature module |
| DS components themselves (e.g. dialog close label, "Kopyala") | `core:designsystem` |
| App-wide: common actions (Kaydet, Tekrar dene, Kapat, İptal), generic errors, units, relative time | `core:ui` (`publicResClass = true`) |

Each module sets `compose.resources { packageOfResClass = "com.purrello.<module>.resources" }` so `Res` classes never collide. Don't duplicate a common string into a feature — use the `core:ui` one.

`values/` is **Turkish** on purpose: the default folder is the fallback for any device language we don't support yet.

## 2. Keys

`snake_case`, `<scope>_<element>_<meaning>`:

| Scope | Examples |
|---|---|
| `common_` | `common_action_save`, `common_action_retry`, `common_action_close` |
| `error_` | `error_no_connection_title`, `error_generic_body`, `error_pet_not_found` (backend code, lower-cased) |
| `<feature>_<screen>_` | `health_vaccine_list_title`, `health_vaccine_list_empty_body`, `home_quick_action_add_vaccine` |
| `<feature>_enum_` | `health_enum_vaccine_status_overdue` |
| `a11y_` | `a11y_pet_tab_switch_pet` ("Pet değiştir") |

Key names describe meaning, not wording (`…_cta_save`, not `…_kaydet`). Never reuse a key for a different meaning just because the Turkish text happens to match.

## 3. Formatting

- **Placeholders are positional:** `%1$s`, `%2$d`. Never build sentences by concatenating strings or appending to a resource.
- **Plurals** use `<plurals>` + `pluralStringResource(Res.plurals.x, count, count)`, even though Turkish rarely inflects — English will.
- **Turkish suffixes on names** (possessive/genitive): "Boncuk'un", "Mia'nın", "Zeytin'in" depend on vowel harmony — a template like `%1$s'un` is wrong for most names. Either phrase copy to avoid the suffix ("Aşı ve sağlık takibi · Boncuk"), or use `PetNameFormatter.possessive(name)` from `core:ui` (locale-aware: Turkish vowel harmony + apostrophe; English `'s`). Put the formatted name into a `%1$s` template.
- **Dates, numbers, units, money** are formatted in the UI layer with locale-aware formatters in `core:ui` (`rememberDateFormatter()`, `formatWeight(grams)`, `formatRelativeDue(date)`); ViewModels keep raw `LocalDate` / `Instant` / `Int`. Turkish uses a decimal comma ("4,2 kg").
- Don't hardcode separators, date patterns (`"dd.MM.yyyy"`) or unit symbols in feature code; call the formatter.

## 4. Where strings are resolved

- **Only composables resolve strings** (`stringResource`, `pluralStringResource`). ViewModels, use cases and repositories stay resource-free: they expose enums, sealed types, `AppError` or ids.
- Map typed state to text with small, named `@Composable` functions next to the screen:

```kotlin
@Composable
fun VaccineStatus.label(): String = when (this) {
    VaccineStatus.OVERDUE -> stringResource(Res.string.health_enum_vaccine_status_overdue)
    VaccineStatus.UPCOMING -> stringResource(Res.string.health_enum_vaccine_status_upcoming)
    VaccineStatus.DONE -> stringResource(Res.string.health_enum_vaccine_status_done)
    VaccineStatus.UNKNOWN -> stringResource(CoreRes.string.common_unknown)
}
```

- `AppError` → title/body/actions via `core:ui`'s `errorText(error)` / `AppErrorDialog` / `AppErrorState`. Screen-specific backend codes are mapped by the feature (`error_<code_lowercase>` in its own resources) and passed as `bodyOverride`; everything else falls back to the generic message.
- Non-composable contexts that must produce text (platform notifications built on-device, share sheets) use the suspend `getString(Res.string.x)` in the platform bridge — still from resources.

## 5. What comes from the backend

- User content (pet names, notes, clinic names): shown as-is, never translated.
- Catalog/reference names (vaccines, breeds, allergies, diseases, medications): localized **by the backend** using the `Accept-Language` header we send with every request.
- Backend error `message` fields are never shown; we map `code` to our strings.
- Push notification text shown by the OS is produced by the backend in the device's registered locale.

## 6. Platform setup

- **iOS:** list supported languages in `iosApp/Info.plist` → `CFBundleLocalizations` (`tr`, later `en`) so iOS resolves the app locale correctly; keep it in sync with the `values-*` folders.
- **Android:** declare supported locales (`localeFilters` / `locales_config.xml`) when a second language ships, enabling per-app language in system settings.
- In-app language switching is **not** in MVP; don't build a custom locale override until it's a product decision.

## 7. Enforcement

- `scripts/check-hardcoded-strings.sh` fails on string literals passed to `Text(`, `text =`, `title =`, `label =`, `placeholder =`, `contentDescription =` in `src/*Main/` (previews and tests excluded). Run it before every commit; it runs in CI.
- Code review rejects any new user-visible literal. Previews may use literals for **sample data** (a pet named "Boncuk"), never for UI copy.
- Brand wordmark "PURRELLO" is rendered from a resource too (`common_brand_wordmark`), so it goes through the same pipeline.
