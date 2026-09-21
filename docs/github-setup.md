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

## 4. Jira ↔ GitHub

Site `esraemirli97.atlassian.net`, project **Purrello** (`KAN`), statuses To Do → In Progress → In Review → Done.
Client tickets are created with **Team = Mobile**.

Everything below is **free on both sides** (the Atlassian "GitHub for Jira" app is listed as Free on the
GitHub Marketplace and on the Atlassian Marketplace).

### 4.1 Link the repo (free, ~5 minutes)

Jira → Apps → *GitHub for Jira* → Connect GitHub organization → pick `esraemirli/purrello-app`.
From then on, anything whose **branch name, commit message or PR title contains `KAN-4`** shows up in that
issue's **Development** panel: branch, commits, PR with its status and link. This alone covers "Jira'da PR linki
olsun" and costs nothing — no automation runs, no API calls.

Our branch/PR conventions already carry the key (`feature/KAN-12-…`, `feat(health): KAN-12 …`), so linking is automatic.

### 4.2 Status transitions — two free options

**a) Smart commits** (no automation quota at all). The commit message carries the command:
`KAN-12 #in-progress`, `KAN-12 #done`. Requires the GitHub and Jira accounts to share the same email and
"Keep my email addresses private" to be off in GitHub. Note this fights our one-line commit rule a bit — the
command sits in the subject line.

**b) Jira automation rules** (Free plan: **100 rule runs per month, single-project rules only** — ours are
single-project, so they are allowed). Project settings → Automation:

| Trigger | Action |
|---|---|
| Branch created (`KAN-*`) | Transition to **In Progress** |
| Pull request created | Transition to **In Review** |
| Pull request merged | Transition to **Done** |

That is 3 runs per ticket → roughly **33 tickets/month** inside the free quota; when it runs out, rules pause
until the next cycle. Claude can also do the transitions through the Atlassian connector, which costs nothing.

## 5. Free-plan limits worth knowing

| Thing | Free tier | Notes |
|---|---|---|
| GitHub Actions | **2.000 dk/ay** (private repo), storage 500 MB | Public repo → unlimited. This CI is ~3–6 min/PR on Linux; macOS minutes cost ~10× |
| Branch protection / rulesets | **Not available on private repos with GitHub Free** | Public repo → free. Otherwise GitHub Pro (~$4/user/mo) |
| Auto-delete merged branches, squash-only, auto-merge | Free, any repo | Settings → General |
| ~~Claude PR review bot~~ | Would bill **Anthropic API usage separately** | Not set up on purpose — use `/review-pr` locally instead |
| Jira | 10 users, 2 GB storage, 1 site | |
| Jira Automation | **100 rule runs/month, single-project rules only** | 3 rules per ticket ≈ 33 tickets/month. (Atlassian is moving to a per-step model — recheck before relying on it) |
| GitHub ↔ Jira app | Free | Dev panel links + smart commits |

## 6. Not enabled yet (deliberately)

- **iOS build/test in CI** — needs a macOS runner (~10× the minutes). Add a second job running
  `./gradlew iosSimulatorArm64Test` when the iOS surface grows.
- **detekt / ktlint** — add as a fourth static check once the code base is past the scaffold stage.
- **PR template & CODEOWNERS** — worth adding when more than one person reviews.
