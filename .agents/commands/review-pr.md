Review the current branch / an open PR against this repo's rules, locally. Target (branch, PR number or nothing = current branch): $ARGUMENTS

This replaces a paid CI review bot: it runs in your own Claude session, so it costs nothing beyond the
subscription. Run it **before pushing** (or before asking for a human review).

**Where to run it**
- **Claude Code** (terminal, inside the repo): type `/review-pr` — it is registered through
  `.claude/commands/review-pr.md`.
- **This chat / Cowork session**: say "Purrello'da /review-pr çalıştır" — Claude reads this file and follows it.
- **Cursor or another agent**: point it at `.agents/commands/review-pr.md`.

It is a checklist for the agent, not a shell script — the shell commands in step 2 are the parts you can also
run by hand.

## Steps

1. Get the diff against `main`: `git fetch origin main && git diff origin/main...HEAD`. Review **only changed lines**.
2. Run the same checks CI runs and report failures first:
   `scripts/check-secrets.sh`, `scripts/check-hardcoded-strings.sh`, `scripts/check-architecture.sh`,
   `./gradlew :androidApp:assembleDebug allTests`.
3. Read `AGENTS.md` and the rule files that apply to the diff, then review in this order:
   1. **Leaked secrets** — API key, token, password, private key, signing config, service-account JSON or any
      credential in plain text, even in a comment, fixture or TODO. Always 🔴, always say "rotate it and move it
      to a secret store".
   2. **Correctness & crashes** — nullability, cancellation, racing loads, unhandled error branches,
      unreachable or unrecoverable state.
   3. **Rule violations**, each with rule file + section:
      - MVI: screen ViewModels extend `MviViewModel<State, Event, Effect>`; public surface is only
        `state` / `effects` / `onEvent`; navigation leaves as an Effect; anything visible is State.
      - `Async<T>` for loaded data; `previous` kept on refresh failure; no `isLoading` + `data` + `error` triples.
      - Screens stateless `(state, onEvent, modifier)`; `koinViewModel` / `collectAsStateWithLifecycle` /
        `ObserveEffects` only in the Route.
      - No user-visible literals; no string concatenation for Turkish suffixes.
      - No Material 3, raw dp or hex in features — `Purr*` + `PurrelloTheme` tokens.
      - No feature → feature imports; DTOs stay in the data layer.
      - `safeApiCall` for network; no `try/catch` in ViewModels; `AppResult` / `AppError` end to end.
      - Data-driven UI matches a contract in `docs/api/`; flag DTO fields no contract documents.
      - New/changed ViewModels and mappers have `commonTest` coverage (success, error, retry, cancellation).
   4. **Test gaps and missing previews.**
4. Output: a list of findings, each prefixed `🔴 blocker` / `🟠 should fix` / `🟡 nit`, with `file:line` and a short
   fix sketch. Then one line on what a human still has to check by hand (design fidelity, product behavior).
   If nothing is wrong, say so in one line.
5. Only post to GitHub if the user asks. Default output is the chat.
