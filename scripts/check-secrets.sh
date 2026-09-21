#!/usr/bin/env bash
# Blocks obvious secrets from entering the repo: API keys, private keys, signing material,
# and credential-looking assignments in source. Test fixtures / fakes / previews are skipped.
#
# Portable on purpose: BSD grep (macOS) has no -P, and a swallowed grep error would make this
# check pass without running. Every grep result is checked explicitly: rc 0 = hits, 1 = clean,
# anything else = the check itself failed and we exit non-zero.
# See .agents/rules/git-conventions.md → CI checks.
set -uo pipefail
cd "$(dirname "$0")/.."

fail=0
report() { echo "::error::$1"; fail=1; }

# 1. Files that must never be committed (checked against tracked files).
while IFS= read -r file; do
  [[ -z "$file" ]] && continue
  case "$file" in
    *google-services.json|*GoogleService-Info.plist|*.jks|*.keystore|*.p8|*.p12|*.mobileprovision|*keystore.properties|*local.properties|*.env)
      report "Secret-bearing file is tracked: $file (add it to .gitignore and rotate the credential)"
      ;;
  esac
done < <(git ls-files)

# 2. Secret-shaped strings in source. POSIX ERE only — no \d, no lookarounds.
patterns=(
  'AIza[0-9A-Za-z_-]{35}'                      # Google API key
  'sk-ant-[A-Za-z0-9_-]{20,}'                  # Anthropic key
  'gh[pousr]_[A-Za-z0-9]{30,}'                 # GitHub token
  'AKIA[0-9A-Z]{16}'                           # AWS access key id
  '-----BEGIN [A-Z ]*PRIVATE KEY-----'         # private key block
  'xox[baprs]-[0-9A-Za-z-]{10,}'               # Slack token
  '(api[_-]?key|client[_-]?secret|password|secret|access[_-]?token|refresh[_-]?token)[[:space:]]*[:=][[:space:]]*"[^"]{12,}"'
)
# Only real fixtures/fakes are exempt — docs and rules are scanned too: a key pasted into a
# markdown file is just as leaked.
excludes=(':!**/commonTest/**' ':!**/androidHostTest/**' ':!**/*Fixtures.kt' ':!**/fake/**' ':!**/*Preview*.kt' ':!scripts/check-secrets.sh')

for pattern in "${patterns[@]}"; do
  hits=$(git grep -n -I -E -i -e "$pattern" -- . "${excludes[@]}")
  rc=$?
  if [[ $rc -gt 1 ]]; then
    echo "::error::git grep failed (rc=$rc) for pattern: $pattern — the secret scan did not run"
    exit 2
  fi
  if [[ $rc -eq 0 ]]; then
    while IFS= read -r hit; do
      [[ -n "$hit" ]] && report "Possible secret: $hit"
    done <<< "$hits"
  fi
done

if [[ $fail -eq 0 ]]; then
  echo "No secrets found."
fi
exit $fail
