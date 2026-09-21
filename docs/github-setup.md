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

## 3. Claude review bot

1. Install the **Claude GitHub App** on the repository (github.com/apps/claude → Install).
2. Settings → Secrets and variables → Actions → New repository secret: **`ANTHROPIC_API_KEY`**.
3. That's it — `.github/workflows/claude-review.yml` runs on every PR open/update and comments inline.

The review prompt is part of the workflow file, so tightening the review = editing that prompt in a PR.
It reads `AGENTS.md` + `.agents/rules/` at review time, so rule changes take effect immediately.

## 4. Jira

- Project key placeholder in the rules is **`PURR`** — replace it everywhere once the real key exists.
- Branch and PR title carry the key (`feature/PURR-12-vaccine-list`, `feat(health): PURR-12 …`).
- **Automatic transitions**: install the *GitHub for Jira* app and connect the repo. Then, in Jira
  (Project settings → Automation), add:
  - PR created for `PURR-x` → transition to **In Review**
  - PR merged to `main` → transition to **Test** (or Done if there is no test column)
  - Branch created from `PURR-x` → transition to **In Progress**
- Claude (this session) can create tickets and move them through the workflow directly once the
  **Atlassian MCP** connector is connected in claude.ai → Connectors.

## 5. Not enabled yet (deliberately)

- **iOS build/test in CI** — needs a macOS runner (~10× the minutes). Add a second job running
  `./gradlew iosSimulatorArm64Test` when the iOS surface grows.
- **detekt / ktlint** — add as a fourth static check once the code base is past the scaffold stage.
- **PR template & CODEOWNERS** — worth adding when more than one person reviews.
