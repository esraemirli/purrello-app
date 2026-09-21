Design the API contract for the screen or action: $ARGUMENTS

We are the client team; the Go backend team implements what we specify. Follow `.agents/rules/api-contract.md` strictly.

## Steps

1. **Understand the screen.** Read `docs/product/app-navigation.md`, `docs/product/design-system.md` and the design (Figma node or design-artifact screen if a link is given). List:
   - every visible section and the **DS component** that renders it (`PurrStatTile`, `PurrListRow`, `PurrBanner`, …) with each slot it needs (title, subtitle, badge, avatar, value…),
   - every user action (buttons, toggles, swipe, form submit) and what changes after it,
   - empty / loading / error states and which backend error codes can occur.
2. **Check existing contracts** in `docs/api/` (index in `docs/api/README.md`). Reuse `common/` contracts (auth, uploads, push, pagination) and existing resource endpoints (`GET /v1/pets`, catalogs). Don't define the same data twice with different shapes.
3. **Read endpoint:** one `GET /v1/screens/{screen}` with everything needed for first paint, grouped by section. Only data: ids, enums, canonical numbers, ISO dates, user content, catalog names. No UI copy, colors, icons or layout.
4. **Action endpoints:** resource-style mutations, `…Request` bodies with only edited fields, minimal responses (id + server-computed fields shown immediately), `Idempotency-Key` for creates, `422 fieldErrors` for validation.
5. **Write the doc** at `docs/api/<feature>/<screen>.md` from `docs/api/_template.md`:
   - realistic example JSON for every response (these become the fake API + test fixtures — make them valid and representative, including one item per enum case you rely on),
   - field table (type, required, notes, enum values),
   - **DS mapping table** (section → component → field → slot, and what is client-side static),
   - error codes the screen handles and how (dialog / inline / full-screen),
   - open questions for backend.
   Status: **Draft**. Add a row to the index in `docs/api/README.md`.
6. **Self-review checklist** before handing it over:
   - [ ] Screen renders from one read call; no N+1 follow-ups.
   - [ ] No display strings; every enum has a documented value set and the client has an `UNKNOWN` path.
   - [ ] Units and dates follow §5 (grams, minor units, `YYYY-MM-DD` vs UTC instant).
   - [ ] Lists that can grow are paginated.
   - [ ] Every action lists its success response, error codes and which screens refresh afterwards (`DataChange` topic).
   - [ ] Nothing re-implements a backend rule on the client.
   - [ ] Privacy: no data the viewer shouldn't see (e.g. lost-pet alert: owner surname initial only, no address/chip no).
7. **Summarize** for the user: endpoints, notable decisions, open questions for the backend team.

Don't write Kotlin in this command unless asked; `/create-screen` turns the contract into DTOs, fake API and repository.
