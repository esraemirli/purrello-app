# Git Conventions

## Branch Naming

| Prefix | Format |
|---|---|
| `epic/` | `epic/description` |
| `feature/` | `feature/description` |
| `bugfix/` | `bugfix/description` |
| `crash/` | `crash/description` |
| `techdebt/` | `techdebt/description` |
| `modularization/` | `modularization/description` |
| `devops/` | `devops/description` |
| `release/` | `release/vx.x.x` |

Description must be **lowercase**, words separated by `-` (e.g. `feature/booking-detail-screen`).

## Commit Messages

| Type | Purpose |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code restructure (no behavior change) |
| `chore` | Infra, config, dependency updates |
| `docs` | Documentation updates |
| `style` | Code style (formatting, whitespace) |
| `test` | Adding/fixing tests |
| `perf` | Performance improvement |
| `build` | Build system / dependency changes |
| `ci` | CI/CD config changes |
| `revert` | Reverting a previous commit |

Optional scope in parentheses: `feat(health): add cancel confirmation sheet`

Breaking changes use `!`: `feat!: remove deprecated login API`

## One line, no body

A commit message is **`<type>: <one sentence>`** and nothing else. What changed is already in the diff; a bullet list under the subject goes stale and nobody reads it.

```
refactor: model async UI state with Async<T> instead of screenState + content
feat(auth): sign in with Google against the auth contract
build: scaffold KMP project with core/feature modules
```

- Imperative mood, lower case after the colon, no trailing period, aim for ≤ 72 characters.
- No bullet list, no "- what changed" block, no file list under the subject.
- Nothing below the subject: no trailers, no `Co-Authored-By:`, no AI-assistant or session links. A commit made with tooling help is still the developer's commit.
- If one sentence genuinely can't cover it, the commit is doing too much — split it.
- Same rule for PR titles; the PR **description** is where context belongs.

## Authorship

All commits are authored as **`Esra Emirli <esraemirli97@gmail.com>`** (set `user.name` / `user.email` in the repo, don't pass `-c` per commit).

## GitHub Enforcement

Commit messages are validated by this regex:

```
^((docs|test|BREAKING CHANGE|fix|feat|chore|perf|refactor|ci|build|revert|style)(\(.+\))?!?:.+)|(Merge .*)|(Revert .*)
```
