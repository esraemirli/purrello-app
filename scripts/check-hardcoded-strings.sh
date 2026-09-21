#!/usr/bin/env bash
# Fails when a user-visible string literal is passed to common Compose text slots in production source sets.
# Allowed: preview files (FooScreenPreviews.kt), test source sets, empty strings.
# See .agents/rules/localization.md §7.
set -euo pipefail
cd "$(dirname "$0")/.."

pattern='(Text\(\s*"[^"]+"|\b(text|title|label|placeholder|contentDescription|subtitle|message)\s*=\s*"[^"]+")'

dirs=$(find . -type d \( -path '*/src/commonMain' -o -path '*/src/androidMain' -o -path '*/src/iosMain' \) -not -path '*/build/*' 2>/dev/null || true)
if [[ -z "$dirs" ]]; then
  echo "No source sets yet — nothing to check."
  exit 0
fi

# shellcheck disable=SC2086
matches=$(grep -rnE "$pattern" --include='*.kt' $dirs 2>/dev/null | grep -vE 'Previews?\.kt:' || true)

if [[ -n "$matches" ]]; then
  echo "Hardcoded user-visible strings found — move them to composeResources/values/strings.xml:"
  echo "$matches"
  exit 1
fi
echo "No hardcoded user-visible strings found."
