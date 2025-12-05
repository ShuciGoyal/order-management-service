## Order Management System — Specification and Learning Guide

Goal
-----
Create a small, clear Order Management System (OMS) suitable for a beginner developer to implement and learn core backend concepts: data modeling, REST APIs, transactions, validation, testing, and simple deployment.

Learning objectives
-------------------
- Understand domain modeling (Order, Customer, Product, OrderItem, Payment, Shipment).
- Design simple RESTful APIs and request/response contracts.
- Handle basic data integrity and concurrency (transactions, optimistic locking).
- Write unit and integration tests for business logic and endpoints.
- Implement a small linear development plan with milestones.

Tiny contract (inputs / outputs / success criteria)
-----------------------------------------------
- Input: API requests to create/read/update orders (JSON bodies). Sample create order payload shown below.
- Output: JSON responses (order objects, status codes), and persisted rows in a database (SQL; use H2 for local development with Spring Boot).
- Success: Orders can be created, listed, updated (status), and cancelled; inventory decrements on confirmed orders; basic validation and error handling present.

Core domain model (simple relational view)
-----------------------------------------
- Customer
  - id (uuid)
  - name, email
  - created_at

- Product
  - id (uuid)
  - sku, name, price_cents (integer), stock_quantity (integer)

- Order
  - id (uuid)
  - customer_id -> Customer
  - total_cents (integer)
  - status (enum: draft, placed, paid, shipped, cancelled)
  - created_at, updated_at

- OrderItem
  - id (uuid)
  - order_id -> Order
  - product_id -> Product
  - unit_price_cents, quantity

- Payment (optional simple record)
  - id, order_id, amount_cents, method, status

Example SQL table snippets (for learning; with Spring Boot use H2 for dev)
---------------------------------------------------------------------
-- products
-- orders
-- order_items

API surface (REST, minimal)
---------------------------
Base path: /api/v1

- POST /orders
  - Create a new order (initially in status `draft` or directly `placed`).
  - Request body (example):
    {
      "customer_id": "uuid",
      "items": [ { "product_id": "uuid", "quantity": 2 } ]
    }
  - Validations: product exists, quantity > 0, sufficient stock if placing immediately.

- GET /orders
  - List orders (pagination, filters by status/customer)

- GET /orders/:id
  - Get single order with items and customer info

- POST /orders/:id/place
  - Mark order as `placed` (decrement inventory inside a transaction)

- POST /orders/:id/cancel
  - Cancel order (if not shipped) and restore inventory

- PATCH /orders/:id
  - Update order metadata or items while `draft`

Sample create order response (success)
-------------------------------------
{
  "id": "uuid",
  "customer_id": "uuid",
  "status": "placed",
  "total_cents": 3499,
  "items": [ { "product_id": "uuid", "quantity": 1, "unit_price_cents": 3499 } ]
}

Workflows and important behaviors
---------------------------------
- Place order: validate items -> open DB transaction -> check stock and decrement -> compute totals -> set status=placed -> commit.
- Cancel order: only allowed if status != shipped; in transaction, set status=cancelled and restore product stock.
- Payments: keep simple — record a Payment row and set order to `paid` when payment confirmed.

Concurrency and data integrity (beginner-friendly)
------------------------------------------------
- Use database transactions for operations that modify stock and order state.
- For concurrent updates to product stock, apply one of:
  - SELECT ... FOR UPDATE (if DB supports) inside a transaction, or
  - optimistic locking: add `version` or `updated_at` and fail on mismatch.
- Keep retry logic simple: detect contention error, return 409 Conflict or retry a small number of times.

Validation and error handling
-----------------------------
- Validate request shapes strictly. Return 422 Unprocessable Entity for validation errors.
- Return 404 for unknown resources.
- Return 409 Conflict for concurrency or business-rule violations (e.g., insufficient stock).

Edge cases to learn and cover
----------------------------
- Ordering more items than in stock.
- Cancelling an already shipped order.
- Multiple simultaneous place attempts for the same order or product.
- Partial failures: payment succeeds but shipping fails (how to reconcile?).
- Orders with price changes for products after order creation — store unit_price in OrderItem to keep history.

Tests to write (start small)
---------------------------
- Unit tests
  - Order total calculation from items
  - Validation rules for create order payload

- Integration tests (use SQLite or an in-memory DB)
  - Create + place order and assert stock decreased
  - Cancel placed order and assert stock restored
  - Concurrent place attempts (simulate two separate workers)

Implementation milestones (suggested)
----------------------------------
1) Data model + migrations + simple seed data (customers, products)
2) CRUD endpoints for orders and products (no stock enforcement yet)
3) Implement place endpoint with transactional stock decrement
4) Add payment record and `paid` transition
5) Add tests and CI (run tests locally)
6) Small frontend or curl examples to exercise API

Roadmap for learning (resources)
--------------------------------
- SQL fundamentals: SELECT, JOIN, TRANSACTION
- REST API design: status codes, idempotency
- Testing: unit vs integration tests
- Concurrency basics: locking vs optimistic concurrency

Spring Boot + Java 21 — implementation notes
-------------------------------------------
If you choose Spring Boot with Java 21, follow these concrete recommendations and patterns so the spec maps directly to common Spring idioms.

- Project and Java
  - Use Java 21 (LTS) and Spring Boot 3.2.x or later (these support Java 21).
  - Pick Maven or Gradle as your build tool (Gradle is common in newer Spring Boot projects; Maven is fine too).

- Database and migrations
  - Use H2 for local development and tests (in-memory or file mode). Use PostgreSQL for staging/production.
  - Manage schema migrations with Flyway (recommended) or Liquibase. Keep migration scripts under src/main/resources/db/migration.

- Persistence
  - Use Spring Data JPA with Hibernate for ORM. Model domain objects as @Entity classes and map relationships (Order -> OrderItem -> Product).
  - Use UUIDs for IDs (java.util.UUID). Hibernate supports UUID mapping; choose column type uuid (Postgres) or varchar for H2 if necessary.
  - Store snapshot pricing in OrderItem (unitPriceCents) to preserve history.

- Concurrency and transactions
  - Use @Transactional on service methods that modify orders and stock.
  - For pessimistic locking, use @Lock(LockModeType.PESSIMISTIC_WRITE) with a JpaRepository or EntityManager.lock(). For optimistic locking, add a @Version field to Product (or Order) and handle OptimisticLockException.
  - Return 409 Conflict on business concurrency failures.

- Validation and DTOs
  - Use Jakarta Bean Validation (jakarta.validation / Hibernate Validator) with @Valid on controller DTOs and constraint annotations (@NotNull, @Min).
  - Keep domain entities separate from API DTOs. Use simple mappers or MapStruct for larger projects; record DTOs are a clean concise option in Java 21.

- API and controllers
  - Implement REST controllers with @RestController and map to /api/v1.
  - Use ResponseEntity<> to control status codes (201 Created, 422 Unprocessable Entity, 404, 409).

- Testing
  - Unit tests: JUnit 5 + Mockito for service layer logic.
  - Integration tests: @SpringBootTest with Testcontainers (Postgres) or with H2 for faster in-memory tests. Use MockMvc for controller tests.

- Useful libraries and starters
  - spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-validation, spring-boot-starter-test.
  - Flyway Core, optionally MapStruct or Lombok (Lombok is optional and can hide Java for beginners — use it sparingly).

- Java 21 notes (practical, beginner-friendly)
  - You can use Java records for immutable DTOs to reduce boilerplate (e.g., request/response payloads).
  - Avoid advanced preview features (like virtual threads) until comfortable; focus on JPA, transactions, and testing first.

- Development flow suggestions
  - Start with entities, Flyway migrations, and a few seed SQL migration scripts for customers and products.
  - Implement repositories and services, then controllers for CRUD, then the `place` endpoint with @Transactional stock decrement.
  - Add optimistic locking (@Version) to Product if you want to teach JPA optimistic concurrency.

- Assumptions
  - Authentication/authorization is out of scope for first pass — assume internal API.

If you'd like, I can scaffold a Spring Boot project (Gradle or Maven) targeting Java 21 with basic entities, Flyway migrations, seed data, and tests for milestone #1. Tell me which build tool you prefer (Maven or Gradle).

Try-it examples (curl)
---------------------
Create order (example payload):

    curl -X POST -H "Content-Type: application/json" \
      -d '{"customer_id":"<uuid>","items":[{"product_id":"<uuid>","quantity":1}]}' \
      http://localhost:8000/api/v1/orders

Quality gates & tests
----------------------
- Run unit tests (local command depends on stack). Aim for a small test suite that covers the happy path plus 1-2 edge cases.
