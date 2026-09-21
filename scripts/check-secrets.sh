#!/usr/bin/env bash
# Blocks obvious secrets from entering the repo: API keys, private keys, signing material,
# and credential-looking assignments in source. Test fixtures / fakes / previews are skipped.
# See .agents/rules/git-conventions.md → CI checks.
set -uo pipefail
cd "$(dirname "$0")/.."

fail=0
report() { echo "::error::$1"; fail=1; }

# 1. Files that must never be committed (checked against tracked files).
tracked=$(git ls-files 2>/dev/null || true)
while IFS= read -r file; do
  [[ -z "$file" ]] && continue
  case "$file" in
    *google-services.json|*GoogleService-Info.plist|*.jks|*.keystore|*.p8|*.p12|*.mobileprovision|*keystore.properties|*local.properties|*.env)
      report "Secret-bearing file is tracked: $file (add it to .gitignore and rotate the credential)"
      ;;
  esac
done <<< "$tracked"

# 2. Secret-shaped strings in source.
patterns=(
  'AIza[0-9A-Za-z_-]{35}'                      # Google API key
  'sk-ant-[A-Za-z0-9_-]{20,}'                  # Anthropic key
  'gh[pousr]_[A-Za-z0-9]{30,}'                 # GitHub token
  'AKIA[0-9A-Z]{16}'                           # AWS access key id
  '-----BEGIN [A-Z ]*PRIVATE KEY-----'         # private key block
  'xox[baprs]-[0-9A-Za-z-]{10,}'               # Slack token
  '(?i)(api[_-]?key|client[_-]?secret|password|secret|access[_-]?token|refresh[_-]?token)[[:space:]]*[:=][[:space:]]*"[^"]{12,}"'
)
excludes=(':!**/commonTest/**' ':!**/androidHostTest/**' ':!**/*Fixtures.kt' ':!**/fake/**' ':!**/*Preview*.kt' ':!scripts/check-secrets.sh' ':!docs/**' ':!.agents/**')

for pattern in "${patterns[@]}"; do
  if hits=$(git grep -n -P -I -e "$pattern" -- . "${excludes[@]}" 2>/dev/null); then
    while IFS= read -r hit; do
      [[ -n "$hit" ]] && report "Possible secret: $hit"
    done <<< "$hits"
  fi
done

if [[ $fail -eq 0 ]]; then
  echo "No secrets found."
fi
exit $fail
