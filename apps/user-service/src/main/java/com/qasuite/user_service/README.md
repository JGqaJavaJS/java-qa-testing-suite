# User Service

REST service for managing users. Part of `java-qa-testing-suite`: 
a test target application for practicing API test automation.

## Tech stack

- Java 17, Maven
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation, Actuator)
- MySQL + Flyway migrations

## Planned REST API

| Method | Path               | Description                |
|--------|--------------------|----------------------------|
| POST   | `/api/users`       | Create a user              |
| GET    | `/api/users/{id}`  | Get a user by id           |
| PUT    | `/api/users/{id}`  | Update a user (full)       |
| DELETE | `/api/users/{id}`  | Delete a user              |

User fields: `id`, `name`, `email` (email is unique).

## Status

- [x] Project generated, builds with Maven
- [x] `User` entity
- [x] Repository, service, controller (create, get, update, delete)
- [x] Flyway migration (`users` table)
- [x] Dockerfile and Docker Compose
- [ ] Kafka event `UserCreated`

## Build

```bash
cd apps/user-service
mvn clean package -DskipTests
```

## Run

From the repository root:

```bash
docker compose up --build
```

Service: `http://localhost:8081`, 
health check: `http://localhost:8081/actuator/health`.