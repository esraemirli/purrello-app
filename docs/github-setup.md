# GitHub & Jira setup

One-time settings that live outside the repo. Rules that live *in* the repo: `.agents/rules/git-conventions.md`.

## 1. Repository settings (Settings → General)

- **Pull Requests**: allow **squash merge** only (merge commits and rebase off), "Default to PR title for squash
  merge commit" on, **Automatically delete head branches** ✅ — merged branches disappear from the remote.
- **Allow auto-merge** ✅ so a PR merges itself once CI and the approval land.

## 2. Branch protection (Settings → Rules → Rulesets → new branch ruleset for `main`)

- Require a pull request before merging — **1 approval**, dismiss stale approvals on new commits.
- Require status checks to pass, and pick these (exact names from `.github/workflows/ci.yml`):
  - `Static checks`
  - `Build & test (Android + common)`
- Require branches to be up to date before merging.
- Block force pushes and deletions.
- Do **not** require the `Claude review` job: it is advisory by design (`git-conventions.md` → Pull requests).

With `gh` installed, the same thing in one call:

```bash
gh api -X PUT repos/esraemirli/purrello-app --field delete_branch_on_merge=true \
  --field allow_squash_merge=true --field allow_merge_commit=false --field allow_rebase_merge=false
gh api -X PUT repos/esraemirli/purrello-app/branches/main/protection \
  --input .github/branch-protection.json    # see the ruleset UI for the exact payload you want
```

## 3. Code review

Review is **local and free**: run `/review-pr` in a Claude session before pushing. It reads `AGENTS.md` +
`.agents/rules/`, runs the same scripts CI runs, and reports findings as 🔴 / 🟠 / 🟡 — including plain-text
secrets, which are always blockers.

A GitHub-side bot (`anthropics/claude-code-action`) would review every PR automatically, but it bills
**Anthropic API usage separately from any Claude subscription**, so it is deliberately not set up. Don't add
it without an explicit cost decision (see "Anything that costs money" in `AGENTS.md`).

## 4. Jira

- Site: `esraemirli97.atlassian.net` · project **Purrello**, key **`KAN`** (team-managed).
  Workflow statuses: To Do → In Progress → In Review → Done (no Test column).
- Branch and PR title carry the key (`feature/KAN-12-vaccine-list`, `feat(health): KAN-12 …`).
- **Automatic transitions**: install the *GitHub for Jira* app and connect the repo. Then, in Jira
  (Project settings → Automation), add:
  - Branch created for `KAN-x` → transition to **In Progress**
  - PR created → transition to **In Review**
  - PR merged to `main` → transition to **Done**

  On the Free plan the whole site shares **150 automation steps per month** (every trigger, condition and
  action counts as a step), so keep it to these three small rules — or do the transitions from Claude instead.
- Claude (this session) can create tickets and move them through the workflow directly once the
  **Atlassian MCP** connector is connected in claude.ai → Connectors.

## 5. Free-plan limits worth knowing

| Thing | Free tier | Notes |
|---|---|---|
| GitHub Actions | **2.000 dk/ay** (private repo), storage 500 MB | Public repo → unlimited. This CI is ~3–6 min/PR on Linux; macOS minutes cost ~10× |
| Branch protection / rulesets | **Not available on private repos with GitHub Free** | Public repo → free. Otherwise GitHub Pro (~$4/user/mo) |
| Auto-delete merged branches, squash-only, auto-merge | Free, any repo | Settings → General |
| ~~Claude PR review bot~~ | Would bill **Anthropic API usage separately** | Not set up on purpose — use `/review-pr` locally instead |
| Jira | 10 users, 2 GB storage, 1 site | |
| Jira Automation | **150 steps/month for the whole site** | Every trigger/condition/action = 1 step |

## 6. Not enabled yet (deliberately)

- **iOS build/test in CI** — needs a macOS runner (~10× the minutes). Add a second job running
  `./gradlew iosSimulatorArm64Test` when the iOS surface grows.
- **detekt / ktlint** — add as a fourth static check once the code base is past the scaffold stage.
- **PR template & CODEOWNERS** — worth adding when more than one person reviews.
