# {Screen name} — API contract

- **Feature module:** `feature:{feature}`
- **Status:** Draft | Agreed | Live
- **Design:** {link to design artifact / Figma node}
- **Last change:** {YYYY-MM-DD} — {what changed}

## 1. Screen summary

What the user sees and does, in 3–5 lines. States: loading, empty, error.

## 2. Read — `GET /v1/screens/{screen}`

**Query:** `petId` (required) …

**Response 200 — example** (used verbatim by `Fake{Feature}Api` and test fixtures):

```json
{
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `pet.id` | string | yes | |
| `alerts[].type` | enum | yes | `VACCINE_OVERDUE`, `VACCINE_DUE_SOON`, … (client falls back to `UNKNOWN`) |

## 3. DS mapping

| Section | DS component | Field → slot | Client-side static |
|---|---|---|---|
| | | | |

## 4. Actions

### 4.1 {Action name} — `POST /v1/...`

**Headers:** `Idempotency-Key` (creates)

**Request — example**

```json
{
}
```

**Response 201 — example**

```json
{
}
```

**Validation (422) fields:** `field` → codes …
**After success:** notifies `DataChange.{Topic}(petId)`; screens that refresh: …

## 5. Errors handled by this screen

| Code | Status | Surface | String key |
|---|---|---|---|
| `PET_NOT_FOUND` | 404 | full-screen error | `error_pet_not_found` |

## 6. Open questions for backend

1. …
