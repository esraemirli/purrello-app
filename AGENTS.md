# Purrello — Agent Guide

Purrello is a **personal pet health & care tracker**: vaccines, allergies / chronic conditions / medications, health documents (vault), grooming & care, pet passport, emergency QR and lost-pet alerts. It is **not** a social network. Product background: `docs/product/handoff.md`.

This repository is **client only**: one **Kotlin Multiplatform** codebase for **Android + iOS**, with **both UI and logic shared** (Compose Multiplatform). The backend is a separate **Go** project owned by the backend team — we never write backend code here.

## Our job vs. the backend's job

- We build screens, flows and all client logic.
- For every screen and every user action we **design the API contract** (endpoint, request, response, errors), shaped around the Purrello design-system (DS) components, and write it under **`docs/api/`**. The backend team implements it. → **`.agents/rules/api-contract.md`**
- Until an endpoint is live we develop against a **fake API** that returns the contract's example JSON verbatim, so switching to the real backend is a DI flag, not a rewrite.

## Stack at a glance (why: `docs/adr/0001-client-architecture.md`)

| Concern | Choice |
|---|---|
| UI | Compose Multiplatform, Purrello DS (`Purr*` components) in `core:designsystem` |
| Presentation | **MVI**: androidx **ViewModel** (KMP) via `MviViewModel<State, Event, Effect>` — `StateFlow` state out, sealed `Event`s in, one-shot `Effect`s out |
| Navigation | **Navigation 3** (KMP) — back stack is plain state we own |
| DI | Koin (`viewModelOf`, `koinViewModel`) |
| Network | Ktor client + kotlinx.serialization, screen-shaped BFF contract |
| Strings | Compose Multiplatform string resources — **no hardcoded user-visible text** |
| Modules | `androidApp`, `iosApp`, `shared` (umbrella), `core:*`, `feature:*`, `build-logic` |

## Before writing code

1. Read the rules that apply (index below). If rules and existing code disagree, the rules win — fix the code or raise it.
2. Check the design: `docs/product/design-system.md` and `docs/product/app-navigation.md` (they link to the live design artifacts).
3. Find the closest existing screen/module and match its structure — don't rely on memory.
4. New screen → **contract first** (`/define-api-contract`), then `/create-screen`.
5. Every user-visible string goes to string resources (`localization.md`).
6. Before pushing, run what CI runs: `scripts/check-secrets.sh`, `scripts/check-hardcoded-strings.sh`,
   `scripts/check-architecture.sh`, `./gradlew :androidApp:assembleDebug allTests`. PR/branch/CI conventions
   are in `git-conventions.md`; one-time GitHub & Jira setup is in `docs/github-setup.md`.
7. If a product or design decision is missing, **ask** — don't invent product behavior or backend fields silently.

## Anything that costs money

Never enable, install or configure something that bills money — a paid GitHub plan, a marketplace app, an
API-billed bot (e.g. `anthropics/claude-code-action`), a paid CI runner, a paid Atlassian plan or app, a cloud
service — **without asking first**. Explain what it costs, what it buys and what the free alternative is, then let
the owner decide. Free tiers with a hard cap (GitHub Actions minutes, Jira automation steps) are fine, but say
where the cap is when you set them up.

Review happens through the local `/review-pr` command (covered by the Claude subscription), not a paid CI bot.

## Rule index (`.agents/rules/`)

| File | Covers |
|---|---|
| `architecture.md` | Layers, ViewModel/State/Action/Event pattern, data flow, error model, where logic lives |
| `kmp-conventions.md` | Module map & dependency rules, source sets, expect/actual, DI, entry points, library checklist |
| `navigation.md` | Navigation 3 setup, routes, tabs, modals, deep links / push, back handling |
| `api-contract.md` | How we design backend contracts (BFF screens, actions, JSON, errors, uploads, auth) |
| `localization.md` | String resources, keys, formatting, plurals, Turkish suffixes, dates/numbers |
| `ui-conventions.md` | DS usage, headers, feedback (no toasts), accessibility, previews |
| `testing.md` | ViewModel / domain / data tests in `commonTest` |
| `anti-patterns.md` | Known mistakes — don't do these |
| `git-conventions.md` | Branches, one-line commit messages |

## Commands (`.agents/commands/`)

| Command | Use |
|---|---|
| `/define-api-contract {Screen}` | Design + document the endpoint(s) a screen/action needs |
| `/create-screen {Screen} {feature}` | Scaffold a screen (State, Action, Event, ViewModel, Route, Screen, entry, DI, strings) |
| `/implement-screen-from-design {Screen} {link}` | Build a screen from a Figma node or design-artifact screen |
| `/write-unit-tests {Class}` | Tests following `testing.md` |
| `/review-pr [branch\|PR]` | Local review against the rules before pushing (free; replaces a paid PR bot) |

## Lessons

`.agents/lessons.md` collects mistakes found in review. Read it; append to it when a review uncovers a new recurring mistake.
