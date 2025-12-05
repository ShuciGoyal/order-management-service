## Order Management System — Detailed Task Breakdown (Beginner-Friendly)

Overview
--------
This document lists every task needed to complete the Order Management System. Each task has:
- Clear description (what to do and why)
- Effort estimate (how long it should take)
- Acceptance criteria (how you know it's done)
- Learning notes (concepts covered)

Use this to track progress and stay focused. Check items off as you complete them.

---

## Milestone 1: Project Skeleton, Entities, and Migrations

**Goal**: Set up a working Spring Boot project with the core domain model and an H2 database for local development.

**Duration**: 1–2 days

### Task 1.1: Create Spring Boot project
**What**: Use Spring Initializr (start.spring.io) or the Spring CLI to create a new project targeting Java 21.
**Why**: Establishes a working project structure and initial dependencies.
**Effort**: 30 min
**Acceptance Criteria**:
- Project directory contains `pom.xml` or `build.gradle` and compiles.
- `./gradlew build` or `mvn -DskipTests=false clean package` completes.

**Learning**: Spring Boot starters, selecting Java 21, build tools (Maven/Gradle).

---

### Task 1.2: Configure H2 and Flyway
**What**: Configure H2 for local dev and Flyway for schema migrations.
**Why**: Flyway provides repeatable schema management; H2 is fast for development and tests.
**Effort**: 30 min
**Acceptance Criteria**:
- `application.properties` or `application.yml` contains datasource settings for H2.
- `src/main/resources/db/migration` exists and Flyway runs on startup.

**Learning**: Spring datasource configuration, Flyway migration lifecycle.

---

### Task 1.3: Create entity classes
**What**: Implement `Customer`, `Product`, `Order`, `OrderItem`, and `Payment` as JPA entities.
**Why**: These classes define the database schema and domain model.
**Effort**: 1 hour
**Acceptance Criteria**:
- Entities compile and map to tables.
- Relationships (@OneToMany, @ManyToOne) are declared where appropriate.

**Learning**: JPA annotations, relationships, UUID primary keys.

---

### Task 1.4: Write Flyway migrations for tables
**What**: Add SQL migrations to create tables and constraints.
**Why**: Migrations ensure schema is reproducible and version-controlled.
**Effort**: 1 hour
**Acceptance Criteria**:
- Migrations run successfully and tables exist in H2.

**Learning**: SQL DDL and Flyway naming conventions.

---

### Task 1.5: Seed initial data
**What**: Create a migration to insert sample products and customers.
**Why**: Seed data speeds development and testing.
**Effort**: 30 min
**Acceptance Criteria**:
- Sample rows are present after app startup.

**Learning**: Managing seed data with migrations.

---

### Task 1.6: Verify milestone 1
**What**: Build and run the app; confirm migrations and seed data.
**Effort**: 15 min
**Acceptance Criteria**:
- App starts and H2 console shows schema + seed data.

---

## Milestone 2: Repositories, Services, and Basic CRUD APIs

**Goal**: Implement data access layer, basic business services, DTOs, and controllers.

**Duration**: 1–2 days

### Task 2.1: Create JPA repositories
**What**: Add Spring Data JPA repositories for all entities.
**Effort**: 30 min
**Acceptance Criteria**:
- Repositories compile and Spring auto-detects them.

---

### Task 2.2: Create service layer
**What**: Implement services for Product and Order operations to encapsulate business logic.
**Effort**: 1–1.5 hours
**Acceptance Criteria**:
- Service methods provide basic CRUD and simple validation.

---

### Task 2.3: Create DTOs
**What**: Create request/response DTOs (consider Java records for brevity).
**Effort**: 45 min
**Acceptance Criteria**:
- Controllers use DTOs instead of exposing entities directly.

---

### Task 2.4: Unit tests for services
**What**: Write unit tests with JUnit 5 and Mockito.
**Effort**: 1 hour
**Acceptance Criteria**:
- Core service behavior is covered by unit tests.

---

### Task 2.5: Create REST controllers
**What**: Implement controllers for products and orders (GET, POST, PATCH where needed).
**Effort**: 1.5 hours
**Acceptance Criteria**:
- Endpoints return expected JSON and status codes (200, 201, 404).

---

### Task 2.6: Add validation
**What**: Use Jakarta Bean Validation (@Valid, @NotNull, @Min, etc.) on DTOs.
**Effort**: 30 min
**Acceptance Criteria**:
- Invalid requests return 422 with a validation error response.

---

### Task 2.7: Integration tests for controllers
**What**: Use @SpringBootTest and MockMvc to validate API endpoints.
**Effort**: 1–1.5 hours
**Acceptance Criteria**:
- Endpoint flows tested end-to-end with H2 backing store.

---

### Task 2.8: Verify milestone 2
**What**: Run tests and manually exercise endpoints via curl/Postman.
**Effort**: 30 min
**Acceptance Criteria**:
- Test suite passes and API works for create/read operations.

---

## Milestone 3: Place Order with Transactions and Stock Decrement

**Goal**: Implement transactional place order flow that updates stock safely.

**Duration**: 1–2 days

### Task 3.1: Add OrderStatus enum
**Effort**: 15 min
**Acceptance Criteria**: Enum in entity with @Enumerated mapping.

---

### Task 3.2: Implement transactional placeOrder
**What**: Service method annotated with @Transactional to decrement stock and mark order PLACED.
**Effort**: 1 hour
**Acceptance Criteria**:
- Stock decreases and order status updates atomically.

---

### Task 3.3: Add optimistic locking to Product
**What**: Add @Version field (and migration to add column).
**Effort**: 30 min
**Acceptance Criteria**:
- Concurrent updates throw OptimisticLockException which you handle (409 or retry).

---

### Task 3.4: Handle insufficient stock errors
**What**: Throw a custom InsufficientStockException and map to 409 via @ControllerAdvice.
**Effort**: 45 min
**Acceptance Criteria**:
- Attempting to place with insufficient stock returns 409 with useful message.

---

### Task 3.5: Add place endpoint
**What**: POST /api/v1/orders/{id}/place
**Effort**: 30 min
**Acceptance Criteria**:
- Endpoint triggers placeOrder and returns updated order.

---

### Task 3.6: Integration tests for place flow
**Effort**: 1.5 hours
**Acceptance Criteria**:
- Happy path and insufficient stock scenarios covered.

---

### Task 3.7: Verify milestone 3
**Effort**: 30 min
**Acceptance Criteria**:
- Manual and automated tests validate transactional behavior.

---

## Milestone 4: Payments, Paid Transition, and Cancel

**Goal**: Add simple payment recording and cancel logic that restores stock.

**Duration**: 1–2 days

### Task 4.1: Payment service and endpoint
**Effort**: 1 hour
**Acceptance Criteria**:
- Recording a payment sets order status to PAID and a Payment row exists.

---

### Task 4.2: Cancel order service method
**Effort**: 1 hour
**Acceptance Criteria**:
- Cancelling restores stock and sets status to CANCELLED unless SHIPPED.

---

### Task 4.3: Cancel endpoint
**Effort**: 15 min
**Acceptance Criteria**:
- POST /api/v1/orders/{id}/cancel works as expected.

---

### Task 4.4: Tests for pay and cancel
**Effort**: 1 hour
**Acceptance Criteria**:
- Integration tests demonstrate correct transitions and stock restoration.

---

### Task 4.5: Verify milestone 4
**Effort**: 30 min

---

## Milestone 5: Tests, README, and CI

**Goal**: Complete tests, documentation, and set up automated CI.

**Duration**: 1–2 days

### Task 5.1: Unit test coverage
**Effort**: 1–1.5 hours

### Task 5.2: Edge case integration tests
**Effort**: 1 hour

### Task 5.3: Write README.md
**Effort**: 45 min

### Task 5.4: Add GitHub Actions workflow
**Effort**: 30 min

### Task 5.5: Final verification
**Effort**: 30 min

---

## Summary checklist

- [ ] 1.1 Create Spring Boot project
- [ ] 1.2 Configure H2 and Flyway
- [ ] 1.3 Create entity classes
- [ ] 1.4 Write Flyway migrations for tables
- [ ] 1.5 Seed initial data
- [ ] 1.6 Verify milestone 1
- [ ] 2.1 Create JPA repositories
- [ ] 2.2 Create service layer
- [ ] 2.3 Create DTOs
- [ ] 2.4 Write simple unit tests for services
- [ ] 2.5 Create REST controllers
- [ ] 2.6 Add validation with Jakarta Bean Validation
- [ ] 2.7 Write integration tests for controllers
- [ ] 2.8 Verify milestone 2
- [ ] 3.1 Add OrderStatus enum
- [ ] 3.2 Add @Transactional to place order service method
- [ ] 3.3 Add optimistic locking to Product
- [ ] 3.4 Handle insufficient stock exceptions
- [ ] 3.5 Add place order endpoint to controller
- [ ] 3.6 Write integration tests for place order
- [ ] 3.7 Verify milestone 3
- [ ] 4.1 Add Payment endpoints and service
- [ ] 4.2 Implement cancel order service method
- [ ] 4.3 Add cancel endpoint
- [ ] 4.4 Write tests for payment and cancel
- [ ] 4.5 Verify milestone 4
- [ ] 5.1 Complete unit test coverage
- [ ] 5.2 Add integration tests for edge cases
- [ ] 5.3 Write README.md
- [ ] 5.4 Add GitHub Actions workflow
- [ ] 5.5 Final verification

---

## Tips
- Work one small task at a time and run tests frequently.
- Commit after each milestone to keep changes small and reversible.
- Ask for help when blocked—I'm ready to scaffold code or tests on request.

Good luck — you can start by telling me whether you want Maven or Gradle and I will scaffold milestone #1.
