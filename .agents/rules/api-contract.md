# API Contract Design (client → Go backend)

We don't write the backend. For every screen and every user action **we decide the endpoint, request, response and errors**, document it in `docs/api/`, and the backend team implements it. The contract doc is the single source of truth for both sides; our DTOs, fake API and test fixtures are derived from it.

Style (ADR-0001 §2): **screen-shaped BFF, raw data**. One read endpoint per screen, sections shaped for our DS components; mutations are resource-style.

## 1. Workflow

1. Design & navigation are known for the screen (`docs/product/`).
2. List what the screen **shows** (per DS component) and what the user can **do** (actions).
3. Write/extend `docs/api/<feature>/<screen>.md` from `docs/api/_template.md` → status **Draft**. Use `/define-api-contract`.
4. Implement DTOs + `FakeXApi` returning the doc's example JSON **verbatim**; build the screen against it.
5. Review with backend → status **Agreed**. After this, changes need backend sign-off.
6. Backend ships → set `useFakeApi = false` for that API in dev, verify → status **Live**.

Add the contract to the index in `docs/api/README.md`.

## 2. Read endpoints — one per screen

```
GET /v1/screens/{screen}[?petId=…]      → {Screen}ScreenResponse
```

- The response contains **everything the screen needs for its first paint** — no follow-up calls to fill sections.
- Group fields by **section**, and name sections after what they are (`upcoming`, `alerts`, `passport`), not after the component.
- For every section, the contract doc states which **DS component** renders it and which field feeds which slot (§8). This is how "response models match DS components": the shape fits the component; the content stays data.
- Lists that can grow unbounded (documents, sightings, history) are **paginated** (§5) and may have their own list endpoint; the screen response then returns the first page.
- Detail screens: `GET /v1/screens/vaccination-detail?vaccinationId=…` (same pattern).
- Reusable reads used by pickers/sheets are resource endpoints: `GET /v1/pets`, `GET /v1/catalog/vaccines?species=CAT`, `GET /v1/catalog/breeds?species=DOG&query=gol`.

## 3. Data, not copy

The backend returns **data**; the client owns **copy** (see `localization.md`).

| Backend sends | Example | Client does |
|---|---|---|
| Ids (strings) | `"petId": "pet_01J8…"` | Wraps in value class |
| Enums | `"status": "OVERDUE"` | Maps to enum → localized label/icon/color |
| Numbers in canonical units | `"weightGrams": 4200`, `"amountMinor": 125000, "currency": "TRY"` | Formats per locale ("4,2 kg") |
| Dates | `"dueDate": "2026-10-02"` (local date), `"createdAt": "2026-09-20T09:00:00Z"` (instant, UTC) | Formats per locale, relative ("3 gün kaldı") |
| Message parameters | `{"type": "VACCINE_OVERDUE", "vaccineName": "Kuduz", "daysOverdue": 12}` | Picks a string template by `type`, fills params |
| User content | pet name, notes, clinic name | Shows as-is |
| Catalog / reference names | vaccine, breed, allergy names | Shows as-is — **backend localizes these by `Accept-Language`** |

**Never** in a response: UI sentences ("Aşın 12 gün gecikti"), button titles, colors, icons, layout hints. If you feel the need to add one, add an enum or a parameter instead.

Every request sends `Accept-Language` (current app locale) so catalog names come back localized.

## 4. Actions — resource-style mutations

```
POST   /v1/pets                                  create pet
PATCH  /v1/pets/{petId}                          partial update (only sent fields change)
DELETE /v1/pets/{petId}
POST   /v1/pets/{petId}/vaccinations             create
POST   /v1/vaccinations/{id}:mark-done           non-CRUD action → ":verb" suffix
POST   /v1/pets/{petId}/lost-reports             publish lost report
POST   /v1/lost-alerts/{alertId}/sightings       "Gördüm"
```

- Request bodies are `…Request` objects with only the fields the form edits.
- Responses return the created/updated resource's **id and server-computed fields the client must show immediately** (e.g. `nextDoseDate`), not a full screen. The client then refreshes affected screens via `DataChangeNotifier`.
- `POST` that creates something takes an `Idempotency-Key` header (UUID generated when the form opens, kept in `SavedStateHandle`) so a retried submit never creates duplicates.
- Validation errors return `422` with `fieldErrors` (§6); field names match request field names exactly.

## 5. JSON conventions

| Topic | Rule |
|---|---|
| Casing | `camelCase` keys (confirm with backend — see open items in `docs/api/README.md`) |
| Envelope | None for success — the body **is** the payload |
| Ids | Opaque strings, never numbers |
| Dates | `YYYY-MM-DD` for calendar dates (birthdays, vaccine dates); ISO-8601 UTC instants (`…Z`) for timestamps |
| Durations | Explicit unit in the name: `validForSeconds` |
| Units | Metric, integer minor units in the name: `weightGrams`, `amountMinor` + `currency` |
| Enums | `SCREAMING_SNAKE_CASE` strings. The client **must** tolerate unknown values (`UNKNOWN` fallback). Adding an enum value is non-breaking only because of this rule. |
| Nullability | Missing ≡ `null`. Lists are never `null` — send `[]`. Booleans are never `null`. |
| Pagination | Cursor: `{"items": [...], "nextCursor": "abc" }`; request `?cursor=abc&limit=20`. `nextCursor: null` = end. |
| Images / files | `{"url": "...", "expiresAt": "..."}` for signed URLs; don't cache past `expiresAt`. Thumbnails as separate `thumbnailUrl`. |
| Versioning | `/v1/…` path. Additive changes (new optional field, new enum value) are allowed within v1; removing/renaming/retyping a field is breaking → new field name or `/v2`. |

## 6. Errors

HTTP status + a single error body:

```json
{
  "error": {
    "code": "PET_NOT_FOUND",
    "message": "pet pet_01J8… not found",
    "traceId": "4bf92f3577b34da6",
    "fieldErrors": [
      { "field": "weightGrams", "code": "OUT_OF_RANGE" }
    ]
  }
}
```

| Status | Meaning | Client `AppError` |
|---|---|---|
| 400 | Malformed request (client bug) | `Server` |
| 401 | Access token invalid/expired → refresh once, retry; refresh fails → logout | `Unauthorized` |
| 403 | Not allowed (not owner, expired vet link) | `Server(code)` |
| 404 | Not found | `Server(code)` |
| 409 | Conflict (already marked done, duplicate) | `Server(code)` |
| 422 | Validation — `fieldErrors` present | `Validation` |
| 429 | Rate limited | `Server(code)` |
| 5xx | Server error | `Server` |
| no response | offline / DNS / TLS | `NoConnection` / `Timeout` |

- `message` is for logs only — **never shown to users**. Users see a localized string chosen by `code` (fallback: generic error), plus `code`/`traceId` in the "Destekle iletişime geç" dialog.
- Every error `code` a screen can hit is listed in its contract doc, and has a string in the feature's resources (`error_<code_lowercase>`), or is explicitly mapped to the generic message.

## 7. Cross-cutting contracts (defined once, in `docs/api/common/`)

- **Auth** (`auth.md`): `POST /v1/auth/google {idToken}` → `{accessToken, refreshToken, expiresInSeconds, user}`; `POST /v1/auth/refresh {refreshToken}` → same shape; `POST /v1/auth/logout`. `Authorization: Bearer <accessToken>` on every other call. Ktor `Auth` plugin refreshes once on 401.
- **Uploads** (`uploads.md`) — signed URLs, bytes never go through our API:
  1. `POST /v1/uploads {purpose, fileName, contentType, sizeBytes}` → `{uploadId, uploadUrl, method, headers, expiresAt}`
  2. Client `PUT`s bytes straight to `uploadUrl` with `headers` (progress shown per page).
  3. The domain call references `uploadIds` (e.g. `POST /v1/pets/{petId}/documents {uploadIds, category, title, date, …}`).
  Allowed types/sizes per `purpose` are listed in the doc; the client validates before step 1.
- **Push** (`push.md`): `PUT /v1/devices/{deviceId} {pushToken, platform, locale}`; payload schema `{type, petId?, targetId?}` → parsed by `DeepLinkParser`. No display text is required in the data payload; visible notification text is produced by the backend in the user's locale.
- **Headers on every call**: `Accept-Language`, `X-App-Version`, `X-Platform` (`android`/`ios`), `X-Request-Id`.

## 8. Worked example — Home (abridged)

`GET /v1/screens/home?petId=pet_01J8Z…`

```json
{
  "pet": {
    "id": "pet_01J8Z",
    "name": "Boncuk",
    "species": "CAT",
    "photo": { "url": "https://…", "expiresAt": "2026-09-20T10:00:00Z" }
  },
  "stats": { "ageMonths": 38, "weightGrams": 4200, "isNeutered": true },
  "alerts": [
    { "type": "VACCINE_OVERDUE", "vaccinationId": "vac_91", "vaccineName": "Kuduz", "daysOverdue": 12 }
  ],
  "upcoming": [
    { "kind": "VACCINE", "id": "vac_92", "title": "Karma aşı", "dueDate": "2026-10-02" },
    { "kind": "CARE", "id": "care_7", "careTypes": ["BATH", "NAIL_TRIM"], "dueDate": "2026-10-10" }
  ],
  "hasOtherPets": true
}
```

| Section | DS component | Field → slot |
|---|---|---|
| `pet` | `PurrAppHeader` (expanded) + `PurrPetSwitcher` | `name` → title, `photo.url` → avatar, `hasOtherPets` → show ▾ |
| `stats` | 3 × `PurrStatTile` | `ageMonths` → "Yaş" tile (client formats "3 yaş 2 ay"), `weightGrams` → "Kilo", `isNeutered` → "Kısır" |
| `alerts[]` | `PurrBanner` (warning/danger) | `type` → string template + tone, params fill template, tap → `VaccineDetail(vaccinationId)` |
| `upcoming[]` | `PurrListRow` + `PurrBadge` | `title` or `careTypes` → title, `dueDate` → subtitle + badge ("3 gün kaldı"), `kind` → icon & route |
| Quick actions | `PurrButton` grid | **Not in the response** — static client UI |

Note what is **not** there: section titles, "Yaş" labels, badge text, colors — all client-side.

## 9. Client-side implementation rules

- DTOs mirror the doc exactly (`@Serializable`, property names = JSON keys unless the project settles on `@SerialName` + snake_case). DTO enums have `@SerialName` per entry + `UNKNOWN`.
- One `XApi` interface per feature with `KtorXApi` and `FakeXApi`. Every call goes through `safeApiCall { }` and returns `AppResult<Dto>`.
- `FakeXApi` returns the contract example JSON (parsed with the same `Json`), adds a small delay, and can be switched to failure modes for manual testing.
- Tests decode the contract example JSON (`testing.md`) — if the doc changes and the DTO doesn't, a test fails.
- When the real backend deviates from an **Agreed** contract, don't silently adapt the client: raise it, then update the doc first.
