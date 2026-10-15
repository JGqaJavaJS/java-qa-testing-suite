# Order Service

GraphQL service that manages orders. Part of `java-qa-testing-suite`, a demo application built as a target for API test automation.

## What it does

- Creates orders (user id, product name, quantity, unit price) and stores them in MySQL
- Calculates the total price of an order (`unitPrice * quantity`)
- Returns all orders or only the orders of one user
- Exposes health and metrics endpoints for monitoring

## Features

- **GraphQL API:** one endpoint `POST /graphql` with `query orders` and `mutation createOrder`
- **GraphiQL:** web page for manual queries at `http://localhost:8082/graphiql`
- **Database:** MySQL (schema `orders_db`), tables managed by Flyway migrations
- **Monitoring:** Spring Boot Actuator (`/actuator/health`, `/actuator/info`, `/actuator/metrics`)
- **Docker:** multi-stage Dockerfile, runs with Docker Compose together with MySQL

## Tech stack

Java 17, Maven, Spring Boot 4.1.1 (Web MVC, GraphQL, Data JPA, Validation, Actuator), MySQL 8.4, Flyway

## Run

From the repository root:

    docker compose up --build

Service: `http://localhost:8082`. Health check: `http://localhost:8082/actuator/health`.

## Note

Passwords and secrets in this repository are demo values for local use only.