## Order Management System — Beginner Plan (Spring Boot + Java 21)

Purpose
-------
This plan breaks the project into small, teachable milestones you can complete one at a time. Each milestone has concrete tasks, simple acceptance criteria, and a tiny test plan so you learn while building.

High-level approach
-------------------
- Use Spring Boot (3.2.x+) with Java 21.
- Use H2 for local dev and Flyway for migrations.
- Implement entities with Spring Data JPA, services with @Transactional, and REST controllers under /api/v1.

Checklist (what I'll deliver if you ask me to scaffold)
- Project skeleton (Gradle or Maven) configured for Java 21
- Entities and JPA repositories (Customer, Product, Order, OrderItem)
- Flyway migrations and seed data for products/customers
- Controllers for basic CRUD and place/cancel endpoints
- Unit and integration tests (JUnit 5, Spring Boot test) for milestone flows

Milestone 1 — Project skeleton, entities, migrations (1–2 days)
-----------------------------------------------------------------
Tasks
- Create a Spring Boot project (Maven or Gradle) targeting Java 21.
- Add dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-validation, flyway-core, spring-boot-starter-test.
- Add Flyway and initial migration SQL to create tables and seed a couple of products/customers.
- Implement entity classes: Customer, Product, Order, OrderItem (basic fields only).

Acceptance criteria
- Project builds with Java 21.
- H2 starts and migrations run automatically.
- A small seed list of products and customers exists.

Quick tests
- Run the app and check H2 console or query tables via a test that asserts seed data exists.

Milestone 2 — Repositories, services, basic CRUD APIs (1–2 days)
-----------------------------------------------------------------
Tasks
- Implement Spring Data JPA repositories for each entity.
- Implement service layer for basic operations and simple business rules.
- Implement controllers for GET/POST/PUT/PATCH for products and orders (draft state allowed).

Acceptance criteria
- Can create/read products and orders via HTTP.
- Validation errors return 422 and missing resources return 404.

Quick tests
- Unit tests for service methods; controller tests with MockMvc for happy path and validation errors.

Milestone 3 — Place order endpoint with transactions and stock updates (1–2 days)
-----------------------------------------------------------------
Tasks
- Implement POST /orders/{id}/place in a service method annotated with @Transactional.
- Check stock, decrement product.stockQuantity, compute order.totalCents, set order.status=PLACED.
- Handle insufficient stock with a 409 Conflict.
- Add optimistic locking (@Version) to Product or use a PESSIMISTIC_WRITE lock as an alternative.

Acceptance criteria
- Placing an order decreases product stock and marks order as PLACED.
- Concurrent place attempts either succeed for one and fail for the other with 409, or are retried (choose one approach and document it).

Quick tests
- Integration test: create order then call place and assert stock decreased.
- Simulated concurrent place attempts to demonstrate optimistic/pessimistic lock behavior.

Milestone 4 — Payments, paid transition, and cancel behavior (1–2 days)
-----------------------------------------------------------------
Tasks
- Add Payment entity and simple flow to record payments.
- Transition order -> PAID when payment recorded and confirmed.
- Implement POST /orders/{id}/cancel: only allowed when not SHIPPED; restores stock in a transaction.

Acceptance criteria
- Payments recorded and order status becomes PAID.
- Cancelling restores stock and sets status= CANCELLED (when allowed).

Quick tests
- Integration tests that place, pay, and cancel orders and validate stock and status transitions.

Milestone 5 — Tests, README, CI (1–2 days)
-------------------------------------------------
Tasks
- Complete unit and integration test coverage for core flows.
- Add a README.md with run/test instructions.
- Add a simple GitHub Actions workflow to run the test suite on push.

Acceptance criteria
- Tests run locally and in CI; build passes on GitHub Actions.

Developer tips and commands
--------------------------
- Choose build tool: Maven or Gradle. Gradle example: use the Gradle Wrapper so contributors run with ./gradlew.

Start app (Gradle):

```bash
./gradlew bootRun
```

Build and test (Gradle):

```bash
./gradlew clean test build
```

Start app (Maven):

```bash
mvn spring-boot:run
```

Build and test (Maven):

```bash
mvn -DskipTests=false clean test package
```

Testing strategies (beginner-friendly)
-----------------------------------
- Unit tests: isolate service methods and use Mockito to mock repositories. Keep tests small and deterministic.
- Integration tests: use @SpringBootTest with H2 for quick feedback. Use Testcontainers (Postgres) later if you want parity with production.
- Controller tests: MockMvc + @WebMvcTest for fast controller validation.

Quality gates
------------
- Build: passes without errors.
- Lint/Formatting: consistent style (use google-java-format or your preferred formatter).
- Tests: happy path unit and at least 3 integration tests (place order, cancel order, insufficient stock).

Estimated total time
--------------------
About 1–2 weeks for a single developer working part time, or 3–5 days of focused work to reach a minimal working prototype (milestones 1–3).

Notes and assumptions
---------------------
- This plan assumes basic familiarity with Java, but aims to teach Spring Boot idioms and data modeling as you work.
- Security (auth) and shipping integration are out of scope for the first pass.

