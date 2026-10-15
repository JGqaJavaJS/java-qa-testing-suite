# java-qa-testing-suite

A demo application built as a target for manual and automated testing practice.

## Why this project exists

- Gives testers something real to connect to: REST, GraphQL, a database, authentication
- Can be tested manually (Postman, GraphiQL, browser) and automatically
- Contains intentional bugs, so there is always something to find

## How it was built

- Generated with AI, under the control of an experienced SDET
- Built step by step, every step is documented in [`project_steps/`](project_steps/)

## Services

- **[User Service](apps/user-service/README.md)** (port 8081): REST API, JWT authentication, MySQL, Flyway
- **[Order Service](apps/order_service/README.md)** (port 8082): GraphQL API, MySQL, Flyway

## Tools already added

- Java 17, Maven, Spring Boot 4.1.1
- REST API, GraphQL API, GraphiQL
- JWT authentication, BCrypt
- MySQL 8.4, Flyway migrations
- Docker, Docker Compose (multi-stage Dockerfiles)
- Spring Boot Actuator (health, info, metrics)
- Postman collections

## Intentional bugs

- The application contains deliberate defects for testers to find
- See the list in [`docs/known-bugs.md`](docs/known-bugs.md)

## Documentation

- [`project_steps/`](project_steps/): step-by-step build log (what to click, what to open, which class was added), API documentation and exported Postman collections
- API reference: in each service README
- [Known bugs](docs/known-bugs.md)

## Quick start

Requirements: Docker Desktop, 4+ GB of memory for Docker.

    docker compose up --build

| Component | URL / port |
|-----------|------------|
| User Service | http://localhost:8081 |
| Order Service | http://localhost:8082 |
| GraphiQL | http://localhost:8082/graphiql |
| MySQL | localhost:3306 |

Health checks: `/actuator/health` on each service.

Stop: `docker compose down`. Stop and wipe the database: `docker compose down -v`.

## Plans

The project has more plans for extension: new services, tools and test suites will be added step by step.

## Note

Passwords in this repository are demo values for local use only.