#!/usr/bin/env bash
# Mechanical architecture guards from .agents/rules (the ones a grep can prove).
# Everything subtler is the reviewer's job (/review-pr, human).
#
# Portable on purpose (see check-secrets.sh): POSIX ERE only, and a grep that fails for any
# reason other than "no match" aborts the script instead of silently passing.
set -uo pipefail
cd "$(dirname "$0")/.."

fail=0
report() { echo "::error::$1"; fail=1; }

# Runs grep, returns its output, and dies if grep itself failed.
scan() { # scan <label> <pattern> <path...>
  local label="$1" pattern="$2"; shift 2
  local out rc
  out=$(grep -rn --include='*.kt' -E "$pattern" "$@")
  rc=$?
  if [[ $rc -gt 1 ]]; then
    echo "::error::grep failed (rc=$rc) while checking: $label"
    exit 2
  fi
  printf '%s' "$out"
}

# 1. Features must not depend on each other (kmp-conventions.md §1).
for dir in feature/*/; do
  module=$(basename "$dir")
  while IFS= read -r hit; do
    [[ -z "$hit" ]] && continue
    # a feature importing its own package is fine; anything else is a cross-feature dependency
    [[ "$hit" == *"com.purrello.feature.$module."* ]] && continue
    report "feature:$module imports another feature — move the shared piece to core:*: $hit"
  done <<< "$(scan "cross-feature imports" '^import com\.purrello\.feature\.' "$dir")"
done

# 2. Screen ViewModels are MVI hosts (architecture.md §4.2).
while IFS= read -r file; do
  [[ -z "$file" ]] && continue
  grep -q ": MviViewModel<" "$file" || report "$file: a feature ViewModel must extend MviViewModel<State, Event, Effect>"
done < <(find feature -name '*ViewModel.kt' -path '*/presentation/*' 2>/dev/null)

# 3. Material 3 is a design-system implementation detail (ui-conventions.md §1).
while IFS= read -r hit; do
  [[ -n "$hit" ]] && report "Use Purr* components instead of Material 3 directly: $hit"
done <<< "$(scan "material3 outside the DS" '^import androidx\.compose\.material3\.' feature shared core/ui)"

# 4. No raw design values in features — use PurrelloTheme tokens (ui-conventions.md §1).
while IFS= read -r hit; do
  [[ -n "$hit" ]] && report "Hardcoded dp/color in a feature — add a DS token instead: $hit"
done <<< "$(scan "raw dp/colors in features" '(^|[^A-Za-z0-9_])[0-9]+\.dp|Color\(0x' feature)"

# 5. Exceptions are caught in core:network's safeApiCall (anti-patterns.md).
while IFS= read -r hit; do
  [[ -z "$hit" ]] && continue
  [[ "$hit" == */commonTest/* ]] && continue
  report "try/catch outside core:network — data returns AppResult: $hit"
done <<< "$(scan "try/catch in features" '\btry[[:space:]]*\{' feature)"

if [[ $fail -eq 0 ]]; then
  echo "Architecture checks passed."
fi
exit $fail
