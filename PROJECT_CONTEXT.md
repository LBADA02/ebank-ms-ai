# eBank MS AI — Project Context & Vision

> Backup of my project context before formatting my PC (2026-11-07).
> Sources: 4 consolidated ChatGPT conversation summaries, my local `docs/transfer-notes.md`
> (test results of 2026-11-02, gitignored), and the actual code of this repo.
> **Rule for reading this file:** section 3 is what the code *really* is today. Everything
> under "Vision" / "Roadmap" is the plan, not proof that it's implemented.

---

## 1. What this project is (my vision)

A **mini digital-banking platform** built with Spring Boot microservices, as a learning and
portfolio project. The point is **not** CRUD — it is to practise how a professional backend
engineer designs, implements, tests and secures *real* banking workflows:

- Money transfers between accounts (debit/credit, transaction history).
- Payments to providers / bills.
- Beneficiaries (saved recipients).
- Idempotency and protection against duplicate operations (double-click, retries).
- Balance consistency and concurrency safety.
- Scheduled / recurring transfers.
- Notifications through asynchronous events (Kafka).
- Authentication, authorization, customer data isolation.
- Testing as a normal developer responsibility (unit, integration, API) — I want to be a
  developer who tests his own features, not a QA specialist.
- An **AI banking assistant** (`ebank-bot`) that talks to the services through **MCP**.

**Core philosophy:** build a small bank with real-world engineering discipline. Get the core
transfer logic *correct and tested* first, then add architecture (Kafka, Batch, more services)
only when the use case justifies it.

### Guiding principles
1. Each microservice owns its data and business rules — no cross-service DB access, no JPA
   relationships to entities of another service (store the remote ID only).
2. Money: always `BigDecimal`, never `double`/`float`.
3. Financial commands are idempotent (`Idempotency-Key` header + DB unique constraint).
4. Balance updates must be concurrency-safe.
5. Transfer + balance changes + transaction rows are atomic while they share one DB.
6. Customer identity comes from the security context/JWT — never trust client-supplied IDs.
7. Explicit status enums, persisted with `@Enumerated(EnumType.STRING)`.
8. Use DTOs across boundaries, never expose JPA entities.
9. Avoid N+1 DB and network calls: joins/entity graphs locally, batch endpoints remotely.
10. Custom exceptions for business failures + a global `@RestControllerAdvice` for HTTP mapping.
11. Test failure / rollback / concurrency paths, not only the happy path.
12. The AI bot never bypasses backend validation, authorization or financial workflows.

### My preferences (how I like to work)
- **Manual mapper** for `TransferDto` (I explicitly chose this over MapStruct automatic mapping,
  because DTOs are enriched with remote customer data). MapStruct is still in the POM.
- Incremental implementation consistent with my actual code; explanations that teach the
  senior-level reasoning behind a decision.
- Clearly distinguish what is implemented vs. what is an idea.

---

## 2. Target architecture (vision)

```
                     ┌──────────────┐
                     │  ebank-bot   │  Gemini + Spring AI, MCP client, Discord
                     └──────┬───────┘
                            │ MCP (streamable HTTP, /mcp)
            ┌───────────────┼────────────────┐
            ▼                                ▼
   ┌─────────────────┐   OpenFeign    ┌─────────────────────┐
   │ customer-service│◄───────────────│   ebank-service     │
   │ customers       │  /customers/   │ accounts, transfers │
   └─────────────────┘   batch        │ transactions,       │
                                      │ payments, benefic.  │
                                      └─────────┬───────────┘
                                                │ Kafka events (planned)
                    ┌───────────────────────────┼────────────────────────┐
                    ▼                           ▼                        ▼
           billing-service          notification-service     scheduled-transfer-service
           (providers, invoices)    (email/in-app)           (Spring Batch + Kafka)
                    — all three are PLANNED, not created yet —

   Infra: discovery-service (Eureka) + gateway-service (Spring Cloud Gateway)
```

| Service | Responsibility | Status |
|---|---|---|
| `discovery-service` | Eureka server | Exists |
| `gateway-service` | Spring Cloud Gateway (discovery locator) | Exists |
| `customer-service` | Customer data; MCP server; batch lookup | Exists |
| `ebank-service` | Bank accounts + transfers + transactions (+ payment/beneficiary entities) ; MCP server | Exists, main active service |
| `ebank-bot` | AI assistant (Gemini, MCP client, Discord bot) | Exists |
| `billing-service` | Providers / invoices | Planned |
| `notification-service` | Notifications via Kafka | Planned (deferred) |
| `scheduled-transfer-service` | Future/recurring transfers via Spring Batch + Kafka; must call the normal transfer workflow, never touch balances directly | Planned |

> ⚠️ **Naming note:** the GPT chats talk about an "`account-service`" on port 8057. In the real
> code **there is no account-service** — accounts live in `ebank-service` (port 8057), and the
> bot's MCP connection named `account-service` actually points to `ebank-service`.
> Because `BankAccount`, `Transfer` and `Transaction` share the same DB in `ebank-service`,
> local JPA relationships between them are valid and one `@Transactional` makes them atomic.
> If accounts are ever split into their own service, that atomicity is lost → Saga /
> reservation + outbox would be needed.

---

## 3. Actual state of the code (verified 2026-11-07)

### Stack (from the POMs)
| | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.9 (discovery-service: 4.1.2) |
| Spring Cloud | 2025.1.4 |
| Spring AI | 2.0.1 (BOM, in ebank-bot; MCP server starter in customer/ebank services) |
| springdoc | 3.2.0 |
| MapStruct | in ebank-service POM (+ lombok-mapstruct-binding, processor) — not used by the transfer mapper |
| Resilience4j | `resilience4j-spring-boot4` |
| DB | H2 in-memory per service (`customer-db`, `ebank-db`), seeded at startup |
| Build | Maven per service (a leftover root Gradle project also exists: `build.gradle`, `src/`) |

### Ports & config
| Service | Port | Notes |
|---|---|---|
| discovery-service | 8761 | Eureka |
| gateway-service | 9999 | Calls like `http://localhost:9999/EBANK-SERVICE/...` |
| customer-service | 8056 | MCP server `protocol: streamable` |
| ebank-service | 8057 | MCP server `protocol: streamable` |
| ebank-bot | 8058 | Gemini `gemini-3.1-flash-lite`, MCP client → 8056/mcp and 8057/mcp |

Spring Cloud Config is disabled everywhere (`spring.cloud.config.enabled: false`).

### Environment variables needed (after reinstalling)
- `GEMINI_API_KEY` — Google GenAI key for ebank-bot
- `DISCORD_BOT_TOKEN` — Discord bot token for ebank-bot

Never commit them. GitHub push protection (GH013) already blocked a push once because of
secrets → any secret that was ever pushed must be **rotated**.

### Git branches
| Branch | Content |
|---|---|
| `main` | initial setup |
| `feature/openfeign-circuit-breaker` | OpenFeign client + Resilience4j circuit breaker |
| `feat/mcp-integration` | MCP servers on customer/ebank services + bot MCP client |
| `feat/bank-transfers` | transfers, transactions, idempotency, filters, customer batch lookup |
| `fix/transfer-account-mapping` | follow-up fixes (see below) + this file |

### What is implemented in `ebank-service`
- `BankAccount` (String UUID id, `BigDecimal balance`, type, `customerId`, `@Transient Customer`),
  domain methods `validationSufficientBalance`, `debit`, `credit`. Exposed as MCP tools
  (`getAllBankAccounts`, `getBankAccountById`, `saveBankAccount`).
- `Transfer` entity: `@ManyToOne(LAZY)` `sourceAccount` / `destinationAccount` (writable FK
  columns `source_account_id`, `destination_account_id`), `beneficiaryId`, `amount`,
  `description`, `status` (`StatusFather`: PENDING/COMPLETED/FAILED), `idempotencyKey`
  (unique constraint `uk_transfer_idempotency`), `createdAt`.
- `Transaction` entity: one DEBIT + one CREDIT row per transfer (`TransactionTypes`,
  `OperationType.TRANSFER`).
- `Payment`, `Beneficiary` entities + `PaymentType` enum — **entities only, no workflow yet**.
- `POST /transfer` with `Idempotency-Key` header → `TransferServiceImpl.makeTransfer`
  (`@Transactional`): idempotency lookup → load accounts → same-account check → balance check →
  debit/credit → save transfer → record transactions → enrich with customers → `TransferDto`.
- `GET /transfer` — paginated history with `TransferFilter` + `TransferSpecification`
  (destination, beneficiary, status, date range, amount range). `customerId` is hardcoded `2L`.
- `TransferRepository` uses `@EntityGraph({"sourceAccount","destinationAccount"})` on
  `findById` / `findAll(spec, pageable)` so accounts come in the same query.
- Customer enrichment: collect distinct `customerId`s from the joined accounts → **one**
  OpenFeign call `POST /customers/batch` (body `[1,2,...]`) → `Map<Long, Customer>` → manual
  `TransferMapper.toDto(transfer, sourceCustomer, destinationCustomer)`.
- `GlobalExceptionHandler` + `ErrorResponse(status, code, message, timestamp)`;
  `AccountNotFoundException` → 404, `InsufficientBalanceException` → 422, missing header → 400.

### What is implemented in `customer-service`
- Customer CRUD-ish endpoints, MCP tools, and `POST /customers/batch` (`findAllById`).

### `ebank-bot`
- `EbankAiAgent`: Spring AI `ChatClient` with chat memory + MCP `ToolCallbackProvider`
  (`defaultTools(tools)` — `defaultToolCallbacks` is deprecated in 2.0.1). French system prompt
  ("answer about customers and bank accounts, say JE NE SAIS PAS if no context").
- REST chat controller + Discord bot integration.

### Last commit on `fix/transfer-account-mapping`
- `Transfer` FK columns are now written by the relationships (removed
  `insertable=false, updatable=false`, which prevented the account IDs from being persisted).
- `TransactionServiceImpl` uses `transfer.getSourceAccount().getId()` /
  `getDestinationAccount().getId()`.
- `BankAccountRepository.findByIdIn` returns `List<BankAccount>` (was `Arrays` — wrong type).
- `/customers/batch` is `POST` on both sides (GET with body caused a 405 via Feign).
- `.gitignore`: `docs/*.md` (local notes).

---

## 4. Transfer feature — manual test results (2026-11-02)

Get test data: `GET http://localhost:9999/EBANK-SERVICE/accounts` (IDs change on every restart,
H2 in-memory). Make a transfer:

```http
POST http://localhost:9999/EBANK-SERVICE/transfer
Content-Type: application/json
Idempotency-Key: test-001

{ "sourceAccountId": "<id>", "destinationAccountId": "<id>",
  "amount": 100, "currency": "MAD", "description": "Test transfer" }
```
(`currency` is required.)

| # | Test | Expected | Result |
|---|---|---|---|
| 1 | Normal transfer | 201 + both customers | Pass |
| 2 | Balances updated | −100 / +100 | Pass |
| 3 | `POST :8056/customers/batch` `[1,2]` | 200, 2 customers | Pass |
| 4 | Same idempotency key | same transfer, balances unchanged | Pass |
| 5 | Missing header | 400 `MISSING_HEADER` | Pass |
| 6/7 | Unknown source / destination | 404 `ACCOUNT_NOT_FOUND` | Pass |
| 8 | Same source & destination | 400 `INVALID_REQUEST` | Pass |
| 9 | Insufficient balance | 422 `INSUFFICIENT_BALANCE` | Pass |
| 10/11 | Invalid amount / missing currency | 400 | Pass (inconsistent body) |
| 12 | Beneficiary only | transfer to beneficiary account | **Fail** (null id) |
| 13 | No destination at all | clear 400 | Partial |
| 14 | Same key, different body | 409/422 | **Fail** (201 old transfer) |
| 15 | Same key in parallel ×4 | 1 transfer, no 500 | Partial (one 500) |
| 16 | Different keys in parallel ×6 of 10 | balances move by 60 | **Fail** (moved by 50 — lost update) |
| 17 | `GET /transfer` | 200 page | **Fail** (400) |
| 18 | History filters | filtered page | blocked by 17 |
| 19 | customer-service down | transfer still succeeds | Not run (code would roll back) |

### Pending changes (my TODO list, in priority order)

**High**
1. Fix `GET /transfer`: `TransferSpecification` still uses `sourceAccountId` /
   `destinationAccountId` → use `root.get("sourceAccount").get("id")` and
   `root.get("destinationAccount").get("id")`.
2. Lost balance updates: add `@Version` on `BankAccount` (handle conflict) or pessimistic
   lock both accounts in a consistent order (avoid deadlocks).
3. Customer lookup must not cancel a transfer: it runs inside `@Transactional` after
   debit/credit. Commit first, then enrich outside the transaction.
4. Add `@CircuitBreaker` + fallback (placeholder "No Name" customers) on
   `CustomerGatewayImpl.getCustomersByIds`.
5. Beneficiary-only transfers: resolve the destination from `beneficiaryId`; 400 if neither
   is set (exactly one of `beneficiaryId` / `destinationAccountId`).
6. Same key + different body → 409/422 (store and compare a request fingerprint/hash).
7. Concurrent same-key requests → catch `DataIntegrityViolationException`, return the existing
   transfer.
8. Authentication: real customer from JWT instead of hardcoded `2L`; check source ownership.

**Medium**
9. Handler for `MethodArgumentNotValidException` returning `ErrorResponse`.
10. Don't leak internal messages from `IllegalArgumentException`; dedicated business exception,
    generic 500 for unexpected errors.
11. Customers missing from batch response → placeholder or explicit error.
12. `currency`: store it or remove it.
13. Fill `Transaction.balanceAfter`.
14. `@Enumerated(EnumType.STRING)` on `Transfer.status`.
15. Finish or remove `getTransferStatus` (returns null) / `getTransfer` (no endpoint, wrong
    error message).

**Low**
16. `GlobalExceptionHandler` should not `extends RuntimeException`.
17. Update the JSON example comment in `TransferRestController` (add `currency`).
18. Clean `BankAccountRepository` unused imports / `findByIdIn`.
19. Automate this checklist (only `contextLoads` exists). Tests 16 and 17 first.

---

## 5. Design decisions already taken

| Topic | Decision |
|---|---|
| Transfer vs Transaction | Transfer = business operation; Transactions = DEBIT/CREDIT account movements linked to it |
| Transfer ↔ account | `@ManyToOne(LAZY)` relationships own the FK columns; fetched with `@EntityGraph` |
| Customer data | stays in customer-service; never a JPA relation; one batch Feign call per page |
| DTO mapping | manual `TransferMapper` (no remote calls inside mappers) |
| History | `JpaSpecificationExecutor` + `Specification` + `Pageable` (sort `createdAt` DESC, size 10) |
| Idempotency | `Idempotency-Key` **header**, persisted, DB unique constraint; replay returns original result; key format (UUID) not enforced for now; frontend will generate it later, Postman manually for now |
| Cooldown | optional, configurable, disableable; account-level preferred (`cooldownEnabled`, `cooldownDurationSeconds`, `lastTransactionAt`); **not** the same as idempotency |
| Beneficiary | lives in ebank-service; convenience alias; transfer always stores the real `destinationAccountId`; check beneficiary ownership |
| Status codes | 404 not found, 400 validation, 401/403 auth, 409 idempotency conflict, 422 insufficient balance, 500 only for real defects |
| Billing | billing-service owns providers/invoices; ebank-service executes the payment; `PaymentCompleted` event → billing marks invoice paid, notification notifies |
| Events | publish only **after commit**; transactional outbox + idempotent consumers + reconciliation |
| Scheduled transfers | separate service owns schedules; execution goes through the normal transfer workflow with an idempotency key per occurrence |
| AI bot | read-only tools first; money-moving tools only with auth, explicit confirmation, server-side validation; bot must never invent balances/results |

---

## 6. Roadmap

1. **Stabilise transfers** — pending changes 1–8 above, tests for success/failure/rollback/concurrency.
2. **Transfer history** — sent vs received semantics (`source IN ... OR destination IN ...`),
   filters validation (`fromDate <= toDate`, `min <= max`), empty account list → `cb.disjunction()`.
3. **Beneficiaries** — CRUD scoped to authenticated customer, used in transfers.
4. **Security** — Spring Security + JWT (Keycloak considered), ownership checks everywhere.
5. **Payments + billing-service** — payment lifecycle, idempotency, invoice validation.
6. **Kafka + notification-service** — event contracts, outbox, idempotent consumers, DLQ.
7. **scheduled-transfer-service** — Spring Batch, recurrence, pause/resume/cancel, history.
8. **AI assistant hardening** — more MCP tools (transfers history, balances), auth, confirmation,
   prompt-injection tests.
9. **Production-minded** — Docker Compose, PostgreSQL, observability (logs/metrics/tracing),
   CI, Testcontainers, API docs.

### Definition of done for a banking feature
API contract + validation · authn/authz · rules in the domain/service layer · DB constraints for
invariants · failure/retry behaviour defined · unit + integration + API tests · useful logs without
secrets · docs updated · events versioned and consumers idempotent.

---

## 7. Open decisions
- Transfer status lifecycle (immediate COMPLETED vs PENDING → COMPLETED/FAILED).
- History: sent only, or sent + received?
- Fees and currency rules (fees included in debit? multi-currency?).
- Idempotency key scope (customer + operation + key?), retention, in-progress handling.
- Cooldown: account-wide vs "similar transfer" policy; do failed operations count?
- ID types: `String` vs `UUID` everywhere.
- Will accounts stay in ebank-service, or become a separate account-service?
- When to move from H2 to PostgreSQL; Redis role (undecided).
- Remove the leftover root Gradle project?

---

## 8. Prompt to paste into a new AI session

> I'm building an eBank microservices learning project (repo `LBADA02/ebank-ms-ai`): Java 21,
> Spring Boot 4.0.9, Spring Cloud 2025.1.4, Maven, Spring Data JPA, H2, Lombok, OpenFeign,
> Resilience4j, Eureka (8761), Gateway (9999), Spring AI 2.0.1 with Gemini
> `gemini-3.1-flash-lite` and MCP. Services: customer-service (8056), ebank-service (8057, owns
> accounts + transfers + transactions), ebank-bot (8058, MCP client + Discord). Planned:
> billing, notification (Kafka), scheduled-transfer (Spring Batch + Kafka).
> Transfers: `POST /transfer` with `Idempotency-Key` header; `Transfer` has `@ManyToOne`
> source/destination `BankAccount` fetched with `@EntityGraph`; each transfer writes a DEBIT and
> a CREDIT `Transaction`; responses are enriched with one `POST /customers/batch` Feign call and a
> **manual** mapper. Read `PROJECT_CONTEXT.md` section 4 for the pending fixes (spec attribute
> names, lost updates/locking, customer lookup inside the transaction, beneficiary-only
> transfers, idempotency payload mismatch, auth). Teach me the senior-level reasoning, keep
> changes incremental and consistent with my code, and distinguish implemented vs. planned.
