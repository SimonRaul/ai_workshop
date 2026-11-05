# Plan: Require a delivery address during order creation

## Problem and proposed approach

Order creation currently accepts only item data. The Angular cart page has no address controls, `CreateOrderDto`/`OrderRequestDto` omit the address, the mapper therefore creates an `Order` with a null address, and the mock API fabricates a default address. The `orders` table and `Order` entity already have address columns, so the fix should extend the existing order contract rather than add persistence structure.

Use the existing four address fields (`country`, `city`, `county`, `streetAddress`) as required checkout input. Add the address to the shared frontend DTO and backend request DTO, map it into the entity through the existing mapper boundary, validate blank/missing input at the HTTP boundary, remove the mock default, and preserve the existing service/strategy transaction flow. Also assess existing persisted rows and enforce the invariant in the database with a Flyway migration when the data is safe to constrain, using a migration/backfill sequence if null legacy rows exist.

## Todos

1. Verify and document the current order flow and contract boundaries.
2. Extend and validate the backend order request contract.
3. Add address state and required checkout UI to the Angular cart feature.
4. Align mock API data and frontend/backend contract tests.
5. Run targeted and full verification for the complete order creation flow.

## Implementation and verification steps

### 1. Verify and document the current order flow and contract boundaries

- Confirm the flow remains `CartOverviewPageComponent` -> `OrdersService` -> `POST /orders` -> `OrderController` -> `OrderMapper` -> `OrderService` -> repositories/strategy.
- Confirm address persistence is already represented by `Order.address` and the existing Flyway `orders` columns.
- Check whether any existing database/profile/test data can contain null address columns. Decide whether one Flyway migration can add `NOT NULL` directly or whether a versioned backfill migration must precede the constraint migration; do not silently invent fallback addresses.
- Record the current baseline: targeted Angular cart/order tests pass; backend controller tests pass; integration execution requires Docker/Testcontainers.
- **Validation after step:** use the existing source paths and baseline test results to ensure the plan targets the actual runtime path and does not change unrelated cart or stock strategy behavior.

### 2. Extend and validate the backend order request contract

- Add an `AddressDto` field to `OrderRequestDto`.
- Apply required non-blank validation to all four address fields, add the required validation dependency if needed, and bind validation at the controller request boundary.
- Extend `OrderMapper.toEntity` to map the request address into the embedded `Address`; keep DTO/entity separation and mapper purity.
- Route validation failures through the existing global exception handling convention with a clear client error response; remove any workaround/default behavior if found during implementation.
- If step 1 finds nullable legacy data that must be retained, add the required Flyway migration(s) under `onlineshopapi/src/main/resources/db/migration/` to backfill only through an explicitly approved data rule and then make `country`, `city`, `county`, and `street_address` non-null. Update local seed data under `onlineshopapi/src/main/resources/db/migration/local/` only if the schema/data change requires it.
- Update controller tests for a valid address, missing address, blank fields, and preservation of the existing stock failure response.
- **Validation after step:** run `mvn -q '-Dtest=OrderControllerTest' test`, migration/schema tests as applicable, and verify valid requests contain the mapped address while invalid requests are rejected before service invocation.

### 3. Add address state and required checkout UI to the Angular cart feature

- Add the address shape to `CreateOrderDto` using the existing shared `AddressDto` contract in `core/types/dtos/`.
- Add a typed reactive form or equivalent page-owned state in `CartOverviewPageComponent`; keep checkout orchestration in the smart page component and keep `CartSummaryComponent` presentational.
- Render required controls for country, city, county, and street address in the cart checkout area, show field-level validation, disable submission until the cart and address are valid, and preserve loading/submission/error behavior.
- Update `toCreateOrderDto` and the checkout call so the entered address is included in the POST payload.
- Use the Angular MCP configured in `.mcp.json` if needed for Angular-specific implementation or verification; follow the existing standalone, OnPush, signals, and subscription conventions.
- Add/update the cart page tests for empty/invalid address prevention, valid address payload forwarding, and successful checkout behavior.
- **Validation after step:** run the targeted Angular cart/order tests and `npm run build`; verify no order request is made with missing/blank address values and that the exact four-field payload is emitted for valid input.

### 4. Align mock API data and frontend/backend contract tests

- Update the MSW order handler to consume the request address instead of hard-coding the Seattle/Pine default.
- Keep response fixtures and order display DTOs consistent with the existing address shape.
- Update order service tests and any mock request expectations to include the address while retaining existing response behavior.
- **Validation after step:** run targeted frontend tests and exercise the mock POST path to confirm the created order echoes the submitted address.

### 5. Run targeted and full verification for the complete order creation flow

- Run backend unit/controller tests and the integration order tests with Docker available.
- Run `mvn test` from `onlineshopapi`.
- Run `npx ng test --watch=false`, `npm run lint`, and `npm run build` from `onlineshopui`.
- If runtime services are available, manually verify cart entry -> POST payload -> persisted order address -> order detail display; otherwise document the environment limitation.
- **Validation after step:** confirm all automated checks pass, invalid requests cannot create orders, valid requests persist the submitted address, and no stock/order strategy behavior regresses.

## Notes and constraints

- The user confirmed the existing four fields are the required delivery-address contract.
- A database migration is required if the implementation changes the entity/schema or if the address invariant must be enforced at persistence level. Prefer making the four order address columns non-null after checking/backfilling existing data; do not use a runtime workaround to satisfy the constraint.
- Backend rules: keep HTTP translation and validation in controllers/DTOs, business orchestration in services, persistence in repositories, and entity/DTO conversion in pure mappers; update controller tests for controller changes and use domain/global error handling rather than broad catches.
- Frontend rules: keep DTOs in `core/types/dtos/`, checkout orchestration in the cart page, reusable UI feature-agnostic, subscriptions cleaned up, and page/service tests updated; preserve standalone components, OnPush, signals, and existing Angular test commands.
- Do not move order business logic into the controller or shared UI library.
- Preserve the existing order strategy selection, transaction boundary, authentication, and cart-clearing-on-success behavior.
- Existing unrelated worktree changes must not be reverted or folded into the implementation.
