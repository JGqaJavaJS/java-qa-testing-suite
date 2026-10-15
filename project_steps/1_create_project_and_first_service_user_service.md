Step 1, Part 1: Repository skeleton and User Service

1.1 Install the tools

Check that these work in your terminal:

bash
java -version      # should print 17.x
mvn -version       # any recent 3.9.x
docker --version    #any recent 29.2.x
docker compose version #any recent 5.x.x
git --version
node -v	#any recent 20.x.x
npm -v		#any recent 10.x.x

If something is missing, install it first.

1.2. Create the project

IntelliJ: File → Open → select java-qa-testing-suite → Trust Project.
VCS → Create Git Repository… → select the project folder → OK.
In IntelliJ’s Project panel, right-click the project root java-qa-testing-suite → New → Directory.
Create these six folders, one by one: apps, qa, infra, postman, scripts, docs.
update .gitignore

1.3. Generate the User Service

Open https://start.spring.io in your browser.
Set these fields:
Project: Maven
Language: Java
Spring Boot: the latest stable version (no SNAPSHOT, M1 or RC)
Group: com.qasuite
Artifact: user-service
Name: user-service
Packaging: Jar
Java: 17
Click Add Dependencies and add these six:
Spring Web
Spring Data JPA
MySQL Driver
Flyway Migration
Spring Boot Actuator
Validation
Click Generate. A zip file downloads.
Unzip it. and put into ..../myjavaproject/java-qa-testing-suite/apps/user-service
check ..../myjavaproject/java-qa-testing-suite/apps/user-service/pom.xml

Connect Maven and build
In IntelliJ, right-click apps/user-service/pom.xml → Add as Maven Project. Wait until the progress bar at the bottom finishes.
Open the terminal in IntelliJ (bottom panel) and run:
bash
cd apps/user-service
mvn clean package -DskipTests

Done when: the terminal prints BUILD SUCCESS and the file apps/user-service/target/user-service-0.0.1-SNAPSHOT.jar exists.

1.4. Share on Github
create commit witj message Initial repository skeleton and user-service generated from Spring Initializr and share on GitHub without files fro,.idea


Part 2, Step 2.1: Package structure

Inside apps/user-service/src/main/java/com/qasuite/userservice/ create four packages (right-click the userservice package → New → Package):

controller
service
repository
model
exception
dto

add with AI generation:
src/main/java/com/qasuite/user_service/README.md
src/main/java/com/qasuite/user_service/model/User.java
src/main/java/com/qasuite/user_service/repository/UserRepository.java
src/main/java/com/qasuite/user_service/exception/UserNotFoundException.java
src/main/java/com/qasuite/user_service/exception/EmailAlreadyExistsException.java
src/main/java/com/qasuite/user_service/service/UserService.java
src/main/java/com/qasuite/user_service/dto/UserRequest.java
src/main/java/com/qasuite/user_service/dto/UserResponse.java
src/main/java/com/qasuite/user_service/controller/UserController.java
src/main/java/com/qasuite/user_service/exception/GlobalExceptionHandler.java

Step 2.2: Flyway migration
In apps/user-service/src/main/resources/, create the folders db and then migration inside it (right-click → New → Directory, type db/migration). The final path is src/main/resources/db/migration.
In migration, create a file named exactly V1__create_users_table.sql (two underscores after V1). Paste:
add src/main/resources/db/migration/V1__create_users_table.sql
update: src/main/resources/application.properties

add:
apps/user-service/.dockerignore
apps/user-service/Dockerfile
docker-compose.yml
.env.example


Part 3 run app

- open docker 
- in idea in terminal: switch to folder: java-qa-testing-suite with cd ../.. if applicable
docker compose up --build
to stop: docker compose down
to stop and to delete data: docker compose down -v
to check running containers in docker: docker ps
- in chrome open: http://localhost:8081/actuator/health (check the port number in terminal)
- open second terminal in IDEA
curl -i -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Anna","email":"anna@example.com"}'
  response: {"id":1,"name":"Anna","email":"anna@example.com"}% 
  - curl -i http://localhost:8081/api/users/1
  response: {"id":1,"name":"Anna","email":"anna@example.com"}% & HTTP/1.1 200                           
  
  All requests of User Service (base URL http://localhost:8081)

1. for negative tests:
   name not empty
   email valid format and not empty
   password not empty and at least 6 characters

2. Create user

POST /api/users
Body (JSON): {"name":"Anna","email":"anna@example.com"}
Success: 201 Created, header Location: /api/users/1, body {"id":1,"name":"Anna","email":"anna@example.com"}
Errors: 400 (empty name or invalid email), 409 (email already exists)

2. Get user

GET /api/users/1
Success: 200 OK, body with the user
Errors: 404 (user not found)

3. Update user (full)

PUT /api/users/1
Body (JSON): {"name":"Anna Smith","email":"anna.smith@example.com"}
Success: 200 OK, body with the updated user
Errors: 400 (invalid input), 404 (user not found), 409 (email taken by another user)

4. Delete user

DELETE /api/users/1
Success: 204 No Content, empty body
Errors: 404 (user not found)

Actuator is a Spring Boot module that adds built-in monitoring endpoints to the service.
Why it matters for you as a tester: it is a ready-made target for health checks, smoke tests and monitoring tests, and it is exactly what real QA teams use to check that an environment is up before running tests.

5. Health check (Actuator)

GET /actuator/health → 200, {"status":"UP"}

6. Info (Actuator)

GET /actuator/info → 200

7. Metrics (Actuator)

GET /actuator/metrics → 200, list of metric names
GET /actuator/metrics/{name}, for example /actuator/metrics/application.ready.time

Error response format (same for 400, 404, 409):

json
{"timestamp":"...","status":404,"error":"Not Found","message":"User not found: 99"}                

TESTS in Postman
- download deckstop posman
- create collection

Part 4. add password and JWT

apps/user-service/pom.xml add dependencies
new file src/main/resources/db/migration/V2__add_password_hash_to_users.sql
updated src/main/java/com/qasuite/user_service/model/User.java
updated src/main/java/com/qasuite/user_service/dto/UserRequest.java
updated src/main/java/com/qasuite/user_service/service/UserService.java
updated src/main/java/com/qasuite/user_service/controller/UserController.java
add src/main/java/com/qasuite/user_service/security/JwtService.java
updated src/main/resources/application.properties
add src/main/java/com/qasuite/user_service/security/JwtAuthFilter.java
add src/main/java/com/qasuite/user_service/security/SecurityConfig.java
add src/main/java/com/qasuite/user_service/dto/LoginRequest.java
add src/main/java/com/qasuite/user_service/dto/LoginResponse.java
add src/main/java/com/qasuite/user_service/controller/AuthController.java
add src/main/java/com/qasuite/user_service/exception/InvalidCredentialsException.java
updated src/main/java/com/qasuite/user_service/exception/GlobalExceptionHandler.java
moved apps/user-service/README.md

User Service API

Base URL: http://localhost:8081

Protected endpoints require the header Authorization: Bearer <token>. Get the token from POST /api/auth/login. It is valid for 60 minutes.

Error response format

All errors handled by the service use the same JSON body:

json
{
  "timestamp": "2026-10-14T18:00:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 99"
}

Exception: 401 caused by a missing, invalid or expired token returns an empty body (no JSON). 401 caused by wrong login credentials returns the JSON above.

POST /api/users

Create a user (registration). No authorization required.

Request

json
{"name": "Carol", "email": "carol@example.com", "password": "secret123"}

Validation rules: name must not be blank; email must be a valid, unique email; password must not be blank and must be at least 6 characters.

Code	Meaning	Message
201	User created. Header Location: /api/users/{id}. Body: id, name, email (the password is never returned).	n/a
400	Validation failed	name: name must not be blank, email: email must not be blank, email: email must be a valid email address, password: password must not be blank, password: password must be at least 6 characters
409	Email already registered	Email already exists: carol@example.com

If several fields are invalid, the messages are joined with ; and sorted alphabetically, for example: email: email must be a valid email address; password: password must be at least 6 characters.

POST /api/auth/login

Log in and get a token. No authorization required.

Request

json
{"email": "carol@example.com", "password": "secret123"}
Code	Meaning	Message
200	Success. Body: token, tokenType (Bearer), expiresInMinutes (60).	n/a
400	Validation failed	email: email must not be blank, password: password must not be blank
401	Wrong email or password	Invalid email or password

The same message is returned for an unknown email and for a wrong password, so existing emails cannot be guessed.

GET /api/users/{id}

Get a user by id. Requires a token. (bearer token in authorisation)

Example: GET /api/users/3, Authorization: Bearer Token.

Code	Meaning	Message
200	Success. Body: id, name, email.	n/a
401	Token missing, invalid or expired	empty body
404	User not found	User not found: 3
PUT /api/users/{id}

Update a user (full update). Requires a token. All three fields are required, and the password is overwritten.

Example: PUT /api/users/3, Authorization: Bearer Token.

Request

json
{"name": "Carol Smith", "email": "carol.smith@example.com", "password": "newsecret123"}
Code	Meaning	Message
200	Success. Body: the updated user.	n/a
400	Validation failed (same rules as POST /api/users)	for example password: password must be at least 6 characters
401	Token missing, invalid or expired	empty body
404	User not found	User not found: 3
409	Email is already used by another user	Email already exists: carol.smith@example.com
DELETE /api/users/{id}

Delete a user. Requires a token. No request body.

Example: DELETE /api/users/3, Authorization: Bearer Token (or {{token}} in Postman).

Code	Meaning	Message
204	User deleted. Empty body.	n/a
401	Token missing, invalid or expired	empty body
404	User not found	User not found: 3

                                             
add Postman collection: project_steps/1_my_user_service.postman_collection.json


# User Service: negative tests (ideas)

**POST /api/users (registration)**

- Empty `name` (`""`), whitespace-only `name` (`"   "`), missing `name`, `null` name
- Empty, missing or `null` `email`
- Invalid `email` formats (`abc`, `abc@`, `@example.com`, `a b@example.com`, double `@@`)
- Duplicate `email` (expect `409`), same email in different case (`ANNA@example.com` vs `anna@example.com`)
- Empty, missing or `null` `password`
- `password` shorter than 6 characters (5 chars fails, 6 chars passes: boundary)
- Whitespace-only `password` (`"      "`)
- `name` or `email` longer than 255 characters (bug 4: database error instead of `400`)
- Very long `password` (BCrypt only uses the first 72 bytes, so two long passwords with the same start behave the same)
- `name` with special characters, emoji, SQL fragments (`'; DROP TABLE users; --`), HTML or script tags
- Wrong types (`"name": 123`, `"email": true`)
- Extra unknown field in the body (for example `"role": "ADMIN"`)
- Malformed JSON, empty body, wrong or missing `Content-Type`
- Send the same request twice quickly (race on unique email)
- Send `id` in the body (it must be ignored)

**POST /api/auth/login**

- Wrong password for an existing email (expect `401`)
- Unknown email (expect `401`, the same message, so emails cannot be guessed)
- Empty or missing `email` or `password` (expect `400`)
- Email in a different case
- Login for users 1 and 2 (created before passwords, empty hash): login must fail
- Login after the user was deleted (expect `401`)
- Login after the password was changed: the old password must fail, the new one must pass
- SQL fragments in `email` (`' OR '1'='1`)
- Very long `email` or `password`
- Many failed attempts in a row (no rate limiting, no lockout)

**Token (all protected endpoints)**

- No `Authorization` header (expect `401`, empty body: bug 3)
- Header without `Bearer ` prefix, or with another scheme (`Basic ...`)
- Empty token (`Bearer `)
- Random string instead of a token
- Token with a changed character (broken signature)
- Token with a modified payload (for example another `sub`) and the old signature
- Token with `alg: none`
- Expired token (set `JWT_EXPIRATION_MINUTES=1` and wait)
- Token of a deleted user (bug 2: keeps working)
- Token of user A used on user B's record (bug 1: allowed)
- Token sent twice in the header, or in a query parameter instead of the header

**GET /api/users/{id}**

- Non-existent `id` (`999`, expect `404`)
- `id` as text (`abc`), negative (`-1`), zero (`0`), decimal (`1.5`)
- `id` beyond `Long` range (`99999999999999999999`)
- Another user's `id` with a valid token (bug 1)
- Deleted user's `id` (expect `404`)
- Trailing slash (`/api/users/3/`) and `GET /api/users` without id (expect `405`)

**PUT /api/users/{id}**

- All validation cases from registration (empty, invalid, short, too long)
- Missing `password` (expect `400`, the update is full)
- Email already used by another user (expect `409`)
- Same email as the current one (must pass)
- Non-existent `id` (expect `404`)
- Update another user's record with a valid token (bug 1)
- `id` in the body different from the `id` in the URL
- Check the old password stops working after the update
- Concurrent updates of the same user

**DELETE /api/users/{id}**

- Non-existent `id` (expect `404`)
- Delete the same user twice (second call `404`)
- Delete another user's record with a valid token (bug 1)
- Delete yourself, then use the same token (bug 2)
- `id` as text, negative, zero
- Body sent with `DELETE` (must be ignored)

**Protocol level**

- Wrong HTTP method on each path (`PATCH /api/users/3`, `GET /api/auth/login`: expect `405`)
- Wrong `Content-Type` (`text/plain`, `application/xml`: expect `415`)
- Very large request body
- Unknown path (`/api/unknown`: expect `401` without a token, `404` with a token)

**Actuator**

- `GET /actuator/metrics/unknown.metric` (expect `404`)
- `POST` to `/actuator/health` (expect `405`)
- `/actuator/health` while MySQL is stopped (expect `503`, `DOWN`)
- Endpoints not exposed (`/actuator/env`, `/actuator/beans`: expect `404`)

**Data checks (database)**

- Password is stored as a BCrypt hash (`$2a$...`), never as plain text
- The same password for two users gives two different hashes
- Response never contains `password` or `passwordHash`


