# Delivery Address Order-Creation Fix Plan

## Problem and proposed approach

Orders can currently be created without a delivery address because the cart UI submits only item data, `CreateOrderDto`/`OrderRequestDto` do not contain an address, and the backend allows `Order.address` to remain `null`. The fix will make the existing four-field address contract (`country`, `city`, `county`, `streetAddress`) required at checkout, carry it through the frontend request DTO and backend request mapper, validate it at both boundaries, and remove nullable-address behavior so persisted orders always contain the submitted address.

The implementation must preserve the existing feature-based Angular structure, standalone/signal patterns, DTO/entity separation, Spring service/controller boundaries, Flyway ownership, mock mode, and current order response shape. The Angular CLI MCP is available in `.mcp.json`; use it when implementing Angular 21 signal/form syntax or validating generated framework APIs.

## Current order-creation workflow

1. `CartOverviewPageComponent.onCheckout()` in `onlineshopui/src/app/features/cart/components/pages/cart-overview-page/cart-overview-page.component.ts` calls `toCreateOrderDto(this.cartItems())`.
2. `toCreateOrderDto()` in `onlineshopui/src/app/features/cart/utils/cart.utils.ts` creates a payload containing only `items`.
3. `OrdersService.create()` posts that payload to `/orders`.
4. `OrderController.create()` receives `OrderRequestDto`, maps it with `OrderMapper.toEntity()`, and passes the entity to `OrderService.createOrder()`.
5. `OrderMapper.toEntity()` creates order details but does not set an address.
6. `OrderService.createOrder()` resolves the authenticated user, allocates stock, saves the order, builds details, deducts stock, and saves again.
7. `OrderMapper.toDto()` conditionally maps the address, allowing a `null` response.
8. The mock order handler independently creates a hard-coded Seattle address, so mock mode hides the missing frontend/backend address submission.

The database already has nullable address columns in `orders` (`country`, `city`, `county`, `street_address`), so the primary contract change is application-level. If the implementation makes those columns non-null, it must add a new Flyway migration and update local mock migration data per the backend rules.

## Scope and affected files

### 1. Define the request contract and validation

- Update `onlineshopapi/src/main/java/msg/onlineshopapi/dto/OrderRequestDto.java` to include an address DTO and validation constraints for all four fields, while retaining item validation conventions.
- Reuse or extend `AddressDto` only if that is consistent with the project’s request/response DTO conventions; otherwise introduce a clearly named request DTO rather than exposing the entity.
- Update `OrderController.create()` to trigger request validation using the repository’s established Spring validation/error-handler conventions.
- Update `OrderMapper.toEntity()` to map the submitted address through a pure mapper and set it on the `Order`.
- Decide whether `AddressMapper` needs a request-to-entity method or a separate pure request mapper; keep mapper dependencies limited to models/DTOs/mappers.

**Verification after step:** compile the backend and add/update controller tests that submit a complete address, assert the mapper receives the request, and assert missing/blank each address field returns the repository-standard 400 response without invoking order creation.

### 2. Enforce the domain invariant and remove the workaround

- Update `OrderService.createOrder()`/related domain handling so a missing address is rejected explicitly rather than tolerated.
- Remove any hard-coded/default-address behavior if found during implementation; do not retain the mock-only hard-coded address as a substitute for request data.
- Make `Order.address` non-null at the application boundary and, if appropriate, make the database address columns non-null through a Flyway migration after checking existing data and migration/test profiles.
- Preserve the transaction and stock-allocation sequence so invalid addresses fail before persistence or stock mutation.

**Verification after step:** add unit and integration coverage proving a complete address is persisted and returned, missing address data is rejected before repository/stock writes, and existing insufficient-stock behavior still leaves stock and order state unchanged. Run the focused backend tests, then `mvn test`.

### 3. Add the delivery-address form to the cart checkout

- Update `CartOverviewPageComponent` to own a typed reactive form or signal-backed form state for the four required fields, following the existing Angular 21 conventions. Prefer the form API that best matches nearby code after checking the Angular CLI MCP.
- Render the fields in the cart checkout UI, likely in the cart page or the cart summary boundary depending on ownership; keep business submission logic in the page component and presentation-only rendering in the view component.
- Add labels, required/trimmed validation, accessible error messages, invalid-state styling, and disable checkout while invalid/submitting.
- Pass the address as an explicit output/input only if the summary view owns the form; otherwise keep the form in the smart cart page and pass the valid value to checkout.

**Verification after step:** update `cart-overview-page.component.spec.ts` (and add a summary spec only if the summary gains logic) to verify all controls render, required errors prevent submission, valid values enable checkout, and the component sends the exact entered values. Run the targeted Angular test and build/lint checks for the changed frontend files.

### 4. Carry address through frontend DTOs, service calls, and mock mode

- Extend `CreateOrderDto` in `onlineshopui/src/app/core/types/dtos/order.dto.ts` with the address shape used by the backend.
- Update `toCreateOrderDto()` to accept the validated address and include it in the request payload.
- Update the cart checkout call to pass the form value and verify no request is sent for empty/invalid address data.
- Update `onlineshopui/src/app/core/mocks/interceptors/handlers/orders-handler.mock.ts` so created orders use `body.address`, not a hard-coded address; reject malformed payloads consistently with the real API.
- Update frontend service/component tests and any request fixtures that construct `CreateOrderDto`.

**Verification after step:** inspect the outgoing POST body in a service/component test and confirm it contains the exact four submitted address fields plus items; run mock-mode tests/build and verify a created mock order displays the submitted address in the order detail view.

### 5. Align persistence/data fixtures and documentation

- If database nullability changes, add a versioned migration under `onlineshopapi/src/main/resources/db/migration/` and update `src/main/resources/db/migration/local/` seed data as required by the backend rules.
- Update backend controller/service tests, frontend mocks, and any API documentation/OpenAPI descriptions affected by the new required request field.
- Check all order creation call sites and fixtures so no stale items-only payload remains.

**Verification after step:** search for every `OrderRequestDto`, `CreateOrderDto`, `/orders` POST, and hard-coded order address; ensure all call sites provide an address. Run `mvn test`, `npx ng test --watch=false`, `npm run build`, and `npm run lint` from their respective project directories.

## Validation and acceptance criteria

- A customer cannot place an order while any address field is empty or whitespace-only.
- A valid checkout POST contains the entered `country`, `city`, `county`, `streetAddress`, and cart items.
- The backend validates the request and maps the submitted address to the persisted `Order`.
- A created order response and order-detail page show the submitted delivery address.
- Missing/invalid address requests return a client error using the existing global error handling, with no order or stock mutation.
- Mock and real API modes have equivalent address behavior; no hard-coded fallback address remains.
- Existing authentication, cart clearing on success, stock strategy behavior, and order navigation remain unchanged.

## Rules and implementation notes

- Backend rules: DTOs at controller boundaries, entities only in services, pure mappers, Flyway for model/schema changes, controller test updates, and `mvn test`.
- Frontend rules: DTOs in `core/types/dtos`, cart behavior in the cart feature, page components own business logic, subscriptions cleaned with `take(1)`/existing patterns, and `npx ng test --watch=false`.
- Use Windows paths and repository formatting conventions.
- Do not change unrelated order display behavior.
- No implementation is included in this planning phase.
