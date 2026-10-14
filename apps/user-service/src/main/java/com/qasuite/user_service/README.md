# User Service

REST service that manages users and issues JWT tokens. Part of `java-qa-testing-suite`, a demo application built as a target for API test automation.

## What it does

- Registers users (name, email, password) and stores them in MySQL
- Logs users in and returns a JWT token (valid for 60 minutes)
- Lets authenticated users read, update and delete user records
- Exposes health and metrics endpoints for monitoring

## Features

- **REST API:** `POST /api/users`, `GET/PUT/DELETE /api/users/{id}`, `POST /api/auth/login`
- **Authentication:** JWT (Bearer token), passwords hashed with BCrypt
- **Validation:** request fields are validated, errors return a unified JSON format with status codes 400, 401, 404 and 409
- **Database:** MySQL, schema managed by Flyway migrations
- **Monitoring:** Spring Boot Actuator (`/actuator/health`, `/actuator/info`, `/actuator/metrics`)
- **Docker:** multi-stage Dockerfile, runs with Docker Compose together with MySQL

#