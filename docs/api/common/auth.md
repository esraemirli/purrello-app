# Auth — API contract

- **Feature module:** `feature:auth` (+ token refresh in `core:network`)
- **Status:** Draft
- **Design:** Login — "Hoş geldin", "Google ile devam et" (docs/product/app-navigation.md → Hata gösterimi / Login)
- **Last change:** 2026-09-20 — first draft

## 1. Summary

Google-only sign-in. The app obtains a Google **ID token** on the device (Credential Manager on Android, Google Sign-In SDK on iOS) and exchanges it for Purrello tokens. All other endpoints use `Authorization: Bearer <accessToken>`. On `401` the client refreshes once; if refresh fails the session ends and the user returns to Login.

## 2. Actions

### 2.1 Sign in with Google — `POST /v1/auth/google`

No bearer token.

**Request — example**

```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij…"
}
```

**Response 200 — example** (used verbatim by `FakeAuthApi` and `AuthResponseDecodingTest`)

```json
{
  "accessToken": "access-token-123",
  "refreshToken": "refresh-token-456",
  "expiresInSeconds": 3600,
  "user": {
    "id": "usr_01J8Z6",
    "displayName": "Esra Emirli",
    "email": "esra@example.com",
    "avatarUrl": null,
    "isNewUser": true
  }
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `accessToken` | string | yes | Short-lived bearer token |
| `refreshToken` | string | yes | Rotated on every refresh |
| `expiresInSeconds` | int | yes | Access token lifetime |
| `user.id` | string | yes | |
| `user.displayName` | string | no | From Google profile |
| `user.email` | string | no | |
| `user.avatarUrl` | string | no | |
| `user.isNewUser` | bool | yes | `true` on first sign-in → client may route to onboarding / "Pet ekle" |

**Errors**

| Code | Status | Surface |
|---|---|---|
| `INVALID_GOOGLE_TOKEN` | 401 | Dialog: generic + code, Tekrar dene / Destekle iletişime geç / Kapat |
| `ACCOUNT_DISABLED` | 403 | Same dialog, body from `auth_error_account_disabled` |
| — (no response) | — | Dialog: Bağlantı yok — Tekrar dene / Kapat |

User cancelling the Google sheet never reaches the backend — nothing is shown.

### 2.2 Refresh — `POST /v1/auth/refresh`

No bearer token. Called only by the Ktor `Auth` plugin.

```json
{ "refreshToken": "refresh-token-456" }
```

**Response 200:** `{ "accessToken": "…", "refreshToken": "…", "expiresInSeconds": 3600 }`
**Errors:** `401 REFRESH_TOKEN_INVALID` → client clears tokens, session ends.

### 2.3 Logout — `POST /v1/auth/logout`

Bearer token. Body: `{ "refreshToken": "…" }` → `204`. Client clears local tokens regardless of the result.

## 3. Open questions for backend

1. Access / refresh lifetimes and whether refresh tokens rotate.
2. Should `POST /v1/auth/google` also accept Apple sign-in later (`/v1/auth/apple`) — App Store requires it if Google login is offered on iOS.
3. Account deletion endpoint (store requirement).
