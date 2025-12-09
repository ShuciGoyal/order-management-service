# OMS — Order Management Service

Small Spring Boot microservice that implements a minimal Order Management System used for learning and demonstration.

High-level context
------------------
- Purpose: manage customers, products, orders and simple payments with transactional place/cancel flows and optimistic locking for product stock.
- Built as a simple single-service example for testing transactional behavior, REST controllers, Flyway migrations and integration testing with H2.
- Not a production payment system — payments are recorded locally and set an order to PAID for demo/testing purposes.

Tech stack
----------
- Java 21+ (tests ran in the environment on Java 23)
- Spring Boot 4.x, Spring Data JPA, Hibernate 7.x
- Flyway for schema migrations
- H2 in-memory database for tests
- JUnit 5, Mockito, MockMvc for tests

Repository layout (important files)
----------------------------------
- `src/main/java/com/example/oms/model` — JPA entities (Customer, Product, Order, OrderItem, Payment)
- `src/main/java/com/example/oms/repository` — Spring Data repositories
- `src/main/java/com/example/oms/service` — service interfaces & implementations (Order, Product, Customer, Payment)
- `src/main/java/com/example/oms/web` — REST controllers (OrderController, PaymentController, ProductController, CustomerController)
- `src/main/resources/db/migration` — Flyway migrations (V001__initial_schema.sql, V002__seed_data.sql)

Quick architecture notes
------------------------
- Controllers accept DTO requests and return DTO responses via mappers (no entity leakage in API responses).
- `OrderService.placeOrder` is transactional and calls `ProductService.adjustStock` for each item.
- `Product` uses optimistic locking (`@Version`) and `ProductServiceImpl.adjustStock` performs a small retry loop on optimistic lock failures.
- Payments are simple local records (`Payment` entity) and `PaymentService.recordPayment(...)` sets the linked order to PAID inside a transaction.
- Cancelling an order (`OrderService.cancelOrder`) restores stock (if order was PLACED or PAID) and sets status to CANCELLED; cancelling a SHIPPED order is rejected.

How to build and run tests locally
----------------------------------
Prerequisites: Java 21+ and Maven installed.

Run the full unit + integration test suite:

```bash
# OMS — Order Management Service (ecomm_v3 - oms)

A small Spring Boot microservice demonstrating a minimal Order Management System for learning and testing transactional behavior, optimistic locking, and simple payment/cancel flows.

## Project context

- Purpose: manage customers, products, orders and payments in a single-service demo. Designed for education, tests, and local experimentation.
- Scope: CRUD for customers/products/orders, transactional order placement that decrements stock, optimistic-lock retry for concurrent stock updates, payment recording (local), and cancel semantics that restore stock when appropriate.
- Not production-ready: payments are simulated and no external payment gateway or security features (auth, rate-limiting) are included.

## Tech stack

- Java 21+ (development environment used Java 23 here)
- Spring Boot 4.x, Spring Data JPA, Hibernate 7.x
- Flyway for DB migrations
- H2 in-memory DB used for tests and local dev
- JUnit 5, Mockito, MockMvc for tests

## Repository layout

- `src/main/java/com/example/oms/model` — JPA entities (Customer, Product, Order, OrderItem, Payment)
- `src/main/java/com/example/oms/repository` — Spring Data repositories
- `src/main/java/com/example/oms/service` — service interfaces & implementations
- `src/main/java/com/example/oms/web` — REST controllers
- `src/main/resources/db/migration` — Flyway SQL migrations

## Key behaviors

- `OrderService.placeOrder`: transactional; for each item it calls `ProductService.adjustStock(...)` to decrement stock.
- `Product` entity includes a `@Version` field; `ProductServiceImpl.adjustStock` retries on optimistic lock failures.
- `PaymentService.recordPayment(...)` records a `Payment` row and marks the order as `PAID`.
- `OrderService.cancelOrder`: restores stock when cancelling a `PLACED` or `PAID` order, sets the status to `CANCELLED`. Cancelling a `SHIPPED` order is rejected.

## Getting started (developer)

Requirements:
- Java 21+ (or compatible JDK)
- Maven 3.8+

Clone and run:

```bash
git clone <repo-url>
cd ecomm_v3/oms
mvn spring-boot:run
```

The application runs on `http://localhost:8080` by default.

### H2 console (dev)

When running locally the project starts an H2 web console available at:

```
http://localhost:8082
```

Default JDBC URL (test/dev): `jdbc:h2:mem:testdb`, user `SA`, empty password.

## Database migrations

Flyway migrations are in `src/main/resources/db/migration`:
- `V001__initial_schema.sql` — schema
- `V002__seed_data.sql` — seed data

Migrations run automatically at application startup and in tests.

## API reference (quick)

Base path: `/api/v1`

- POST /api/v1/customers — create customer
- POST /api/v1/products — create product
- POST /api/v1/orders — create order
- GET /api/v1/orders/{id} — get order
- POST /api/v1/orders/{id}/place — place order (decrements stock)
- POST /api/v1/orders/{id}/pay — record payment, sets order to PAID
- POST /api/v1/orders/{id}/cancel — cancel order (restores stock when applicable)

### Example: create customer

```bash
curl -s -X POST http://localhost:8080/api/v1/customers \
	-H 'Content-Type: application/json' \
	-d '{"name":"Alice","email":"alice@example.com"}'
```

### Example: create product

```bash
curl -s -X POST http://localhost:8080/api/v1/products \
	-H 'Content-Type: application/json' \
	-d '{"sku":"SKU-1","name":"Widget","priceCents":1500,"stockQuantity":10}'
```

### Example: order lifecycle (place, pay, cancel)

```bash
# place
curl -X POST http://localhost:8080/api/v1/orders/<orderId>/place

# pay
curl -X POST http://localhost:8080/api/v1/orders/<orderId>/pay \
	-H 'Content-Type: application/json' \
	-d '{"amountCents":1500, "method":"card"}'

# cancel
curl -X POST http://localhost:8080/api/v1/orders/<orderId>/cancel
```

## Testing

Run full test-suite (unit + integration):

```bash
mvn -DskipTests=false test
```

Run a single test class:

```bash
mvn -Dtest=com.example.oms.web.OrderPlacementIntegrationTest test
```

Quiet CI-style run:

```bash
mvn -DskipTests=false -q test
```

Notes:
- Tests use H2 and Flyway. They are designed to be self-contained and fast.
- Integration tests use Spring's test context and MockMvc.

## Development notes & next steps

- Add GitHub Actions to run tests on push/PR (can be scaffolded).
- Improve integration tests to assert DB state after payment/cancel flows (verify payment rows and final stock quantity).
- Consider adding OpenAPI/Swagger for API docs if this becomes a public API.



