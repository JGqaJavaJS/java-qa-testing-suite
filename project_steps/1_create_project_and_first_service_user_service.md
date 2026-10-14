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
- in idea in terminal: docker compose up --build
- in chrome open: http://localhost:8081/actuator/health (check the port number in terminal)
- open second terminal in IDEA
curl -i -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Anna","email":"anna@example.com"}'
  response: {"id":1,"name":"Anna","email":"anna@example.com"}% 
  - curl -i http://localhost:8081/api/users/1
  response: {"id":1,"name":"Anna","email":"anna@example.com"}% & HTTP/1.1 200                           
  
  All requests of User Service (base URL http://localhost:8081)

1. Create user

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

5. Health check (Actuator)

GET /actuator/health → 200, {"status":"UP"}

6. Info (Actuator)

GET /actuator/info → 200

7. Metrics (Actuator)

GET /actuator/metrics → 200, list of metric names
GET /actuator/metrics/{name}, for example /actuator/metrics/jvm.memory.used

Error response format (same for 400, 404, 409):

json
{"timestamp":"...","status":404,"error":"Not Found","message":"User not found: 99"}                                                             






