# Order Service

GraphQL service that manages orders. Part of `java-qa-testing-suite`, a demo application built as a target for API test automation.

## What it does

- Creates orders (user id, product name, quantity, unit price) and stores them in MySQL
- Calculates the total price of an order (`unitPrice * quantity`)
- Returns all orders or only the orders of one user
- Updates the status of an order
- Deletes an order
- Exposes health and metrics endpoints for monitoring

## Features

- **GraphQL API:** one endpoint `POST /graphql` with:
    - `query orders` (all orders or filtered by `userId`)
    - `mutation createOrder`
    - `mutation updateOrder` (changes `status`)
    - `mutation deleteOrder`
- **GraphiQL:** web page for manual queries at `http://localhost:8082/graphiql`
- **Database:** MySQL (schema `orders_db`), tables managed by Flyway migrations
- **Monitoring:** Spring Boot Actuator (`/actuator/health`, `/actuator/info`, `/actuator/metrics`)
- **Docker:** multi-stage Dockerfile, runs with Docker Compose together with MySQL

## Order status

- `status` is a free-form string
- It is set to `NEW` when an order is created
- `updateOrder` accepts any value

## Tech stack

Java 17, Maven, Spring Boot 4.1.1 (Web MVC, GraphQL, Data JPA, Validation, Actuator), MySQL 8.4, Flyway

## Run

From the repository root:

    docker compose up --build

Service: `http://localhost:8082`. Health check: `http://localhost:8082/actuator/health`.

## Known limitations

Intentional, used as test targets. The full list is in [`docs/known-bugs.md`](../../docs/known-bugs.md).

- No input validation: zero and negative `quantity` and `unitPrice` are accepted
- `createOrder` does not check that the user exists
- Errors for a non-existent order or an invalid `userId` return `INTERNAL_ERROR` instead of a clear message

## Note

Passwords and secrets in this repository are demo values for local use only.