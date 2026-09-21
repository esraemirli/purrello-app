# Implement Screen from Design

```
/implement-screen-from-design {ScreenName} {figmaLink | design-artifact link + screen name}
```

## Core principle

Find the closest existing screen in the codebase first and follow its exact pattern. This command adds the **design-reading** part on top of `/create-screen` — it doesn't replace it.

## 1. Read the design

**Figma link** (Figma MCP tools):
1. Node id from the URL (`node-id=762-35238` → `762:35238`).
2. `get_design_context` → structure; `get_screenshot` → visual; `get_variable_defs` → tokens.

**Design artifact** (the Purrello canvas / DS artifacts linked in `docs/product/`): read the artifact, locate the named screen, and extract the same information.

**Checklist**
- Colors → `PurrelloTheme.colors` token names (no hex in features).
- Typography → `PurrelloTheme.typography` scale.
- Spacing / radius / heights → `spacing`, `shapes`, `sizes` tokens. A value with no token → propose a DS token, don't inline it.
- Header kind (tab root warm header / compact / detail bar / modal ×) and whether the tab bar shows (`ui-conventions.md §2`).
- Components → map every element to a `Purr*` component. Missing component → build it in `core:designsystem` first.
- States: loading, empty, error, disabled, selected, pressed, validation.
- Every text on the design → a string resource key (`localization.md`). Dynamic parts → placeholders; names with Turkish suffixes → `PetNameFormatter`.
- Interactions: taps, long-press, sheets, dialogs → one `Event` each; what navigates where → an `Effect` (`navigation.md`).

## 2. Contract

Make sure `docs/api/<feature>/<screen>.md` covers every data-driven element you found. If not, update it (`/define-api-contract`) **before** writing Kotlin.

## 3. Build

Run the `/create-screen` steps. Don't recreate files that already exist — extend them.

## 4. Verify against the design

- Compare previews (light + dark) with the design screenshot side by side; fix spacing/typography drift using tokens.
- Large font scale doesn't clip; touch targets ≥ 44.
- `scripts/check-hardcoded-strings.sh` passes; lint has zero errors.
