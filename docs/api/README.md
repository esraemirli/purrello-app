# Purrello API contracts

Client-designed contracts for the Go backend. Rules: `.agents/rules/api-contract.md`. New contract: copy `_template.md`, or run `/define-api-contract {Screen}`.

**Status flow:** Draft (client) → Agreed (client + backend) → Live (backend shipped, client verified against it).

## Index

| Area | Doc | Endpoints | Status |
|---|---|---|---|
| common | `common/auth.md` | `POST /v1/auth/google`, `/v1/auth/refresh`, `/v1/auth/logout` | Draft |
| common | `common/uploads.md` | `POST /v1/uploads` + signed PUT | To write |
| common | `common/push.md` | `PUT /v1/devices/{deviceId}`, push payload | To write |
| common | `common/errors.md` | error body, status mapping, global codes | To write |

## Open items for the backend team

1. JSON key casing: we propose **camelCase**. If the Go side prefers snake_case, decide once, before the first contract is Agreed.
2. Access/refresh token lifetimes and refresh-token rotation.
3. Global error code catalog (`common/errors.md`).
4. Signed URL lifetime for downloads/uploads and allowed file types/sizes per purpose.
5. Base URLs per environment (dev / staging / prod).
