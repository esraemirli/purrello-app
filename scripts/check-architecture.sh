#!/usr/bin/env bash
# Mechanical architecture guards from .agents/rules (the ones a grep can prove).
# Everything subtler is the AI reviewer's and the human reviewer's job.
set -uo pipefail
cd "$(dirname "$0")/.."

fail=0
report() { echo "::error::$1"; fail=1; }

# 1. Features must not depend on each other (kmp-conventions.md §1).
for dir in feature/*/; do
  module=$(basename "$dir")
  if hits=$(grep -rn --include='*.kt' -E "^import com\.purrello\.feature\.(?!$module)" -P "$dir" 2>/dev/null); then
    while IFS= read -r hit; do
      [[ -n "$hit" ]] && report "feature:$module imports another feature — move the shared piece to core:*: $hit"
    done <<< "$hits"
  fi
done

# 2. Screen ViewModels are MVI hosts (architecture.md §4.2).
while IFS= read -r file; do
  [[ -z "$file" ]] && continue
  grep -q ": MviViewModel<" "$file" || report "$file: a feature ViewModel must extend MviViewModel<State, Event, Effect>"
done < <(find feature -name '*ViewModel.kt' -path '*/presentation/*' 2>/dev/null)

# 3. Material 3 is a design-system implementation detail (ui-conventions.md §1).
if hits=$(grep -rn --include='*.kt' '^import androidx\.compose\.material3\.' feature shared core/ui 2>/dev/null); then
  while IFS= read -r hit; do
    [[ -n "$hit" ]] && report "Use Purr* components instead of Material 3 directly: $hit"
  done <<< "$hits"
fi

# 4. No raw design values in features — use PurrelloTheme tokens (ui-conventions.md §1).
if hits=$(grep -rn --include='*.kt' -E '(^|[^A-Za-z0-9_])[0-9]+\.dp|Color\(0x' feature 2>/dev/null); then
  while IFS= read -r hit; do
    [[ -n "$hit" ]] && report "Hardcoded dp/color in a feature — add a DS token instead: $hit"
  done <<< "$hits"
fi

# 5. Exceptions are caught in core:network's safeApiCall (anti-patterns.md).
if hits=$(grep -rn --include='*.kt' -E '\btry[[:space:]]*\{' feature --include='*.kt' 2>/dev/null | grep -v '/commonTest/'); then
  while IFS= read -r hit; do
    [[ -n "$hit" ]] && report "try/catch outside core:network — data returns AppResult: $hit"
  done <<< "$hits"
fi

if [[ $fail -eq 0 ]]; then
  echo "Architecture checks passed."
fi
exit $fail
