Step 1: Generate the Order Service
Open https://start.spring.io
Fill in the fields:
Project: Maven
Language: Java
Spring Boot: 4.1.1
Group: com.qasuite
Artifact: order-service
Name: order-service
Packaging: Jar
Java: 17
Click Add Dependencies and add these seven: Spring Web, Spring GraphQL, Spring Data JPA, MySQL Driver, Flyway Migration, Spring Boot Actuator, Validation.
Click Generate. The file order-service.zip downloads.
Unzip the archive (double-click in Finder). You get a folder order-service in Downloads.

Done when: the folder order-service is in Downloads and contains pom.xml.

Step 2: Move Order Service into apps/
In Finder, open Downloads and find the unzipped order-service folder.
Drag the whole order-service folder into java-qa-testing-suite/apps/, next to user-service.
Check the path: java-qa-testing-suite/apps/order-service/pom.xml must exist.
In IntelliJ, right-click apps/order-service/pom.xml → Add as Maven Project. Wait until the progress bar at the bottom finishes.

Step 3: Add code and dataBase
Open apps/order-service/src/main/java/com/qasuite/order_service/. This is the folder with the main class OrderServiceApplication.
Right-click order_service → New → Package and create four packages, one by one: model, repository, service, graphql.
Right-click the model package → New → Java Class → Order.
add src/main/java/com/qasuite/order_service/model/Order.java

add src/main/resources/db/migration/V1__create_orders_table.sql
add src/main/java/com/qasuite/order_service/repository/OrderRepository.java
add src/main/java/com/qasuite/order_service/service/OrderService.java
updated apps/order_service/src/main/resources/application.properties
Reload All Maven Projects.

add apps/order_service/src/main/resources/graphql/schema.graphqls
add apps/order_service/src/main/java/com/qasuite/order_service/graphql/CreateOrderInput.java
add apps/order_service/src/main/java/com/qasuite/order_service/graphql/OrderGraphQlController.java
Reload All Maven Projects.

add apps/order_service/Dockerfile
add apps/order_service/.dockerignore
add infra/mysql/init.sql

clear dataBase: docker compose down -v

updated docker-compose.yml
updated .env.example
Reload All Maven Projects.

docker compose up --build
run project 

add apps/order_service/README.md

open http://localhost:8082/graphiql in chrome

1. create order
   mutation {
   createOrder(input: {userId: 3, productName: "Keyboard", quantity: 2, unitPrice: "19.99"}) {
   id
   userId
   productName
   quantity
   totalPrice
   status
   createdAt
   }
   }
response:
   {
   "data": {
   "createOrder": {
   "id": "1",
   "userId": "3",
   "productName": "Keyboard",
   "quantity": 2,
   "totalPrice": "39.98",
   "status": "NEW",
   "createdAt": "2026-10-15T07:14:47.222599255Z"
   }
   }
   }
2. Check query orders
   query {
   orders {
   id
   userId
   productName
   quantity
   totalPrice
   status
   }
   }
response
   {
   "data": {
   "orders": [
   {
   "id": "1",
   "userId": "3",
   "productName": "Keyboard",
   "quantity": 2,
   "totalPrice": "39.98",
   "status": "NEW"
   }
   ]
   }
   }
3. Filter by userId
   query {
   orders(userId: 3) {
   id
   productName
   }
   }
response: {
   "data": {
   "orders": [
   {
   "id": "1",
   "productName": "Keyboard"
   }
   ]
   }
   }
with user id 99 -> response empty array
4. request unexisting field:
   query {
   orders {
   foo
   }
   }
response
   {
   "errors": [
   {
   "message": "Validation error (FieldUndefined@[orders/foo]) : Field 'foo' in type 'Order' is undefined",
   "locations": [
   {
   "line": 3,
   "column": 5
   }
   ],
   "extensions": {
   "classification": "ValidationError"
   }
   }
   ]
   }
5. close chrome
6. ## API

for negative tests:
productName can be empty and up to 255 characters
quantity can be empty and negative
unitPrice can be negative and 0, "abc" gives internal error
userId can be any, but only digits
limits for price DECIMAL(10, 2), max 99999999.99, min -99999999.99

Base URL: `http://localhost:8082`

GraphQL endpoint: `POST /graphql`, header `Content-Type: application/json`. Authorization is not required.

Request body format:

    {"query": "<GraphQL query or mutation>"}

GraphQL returns HTTP `200` even when the response contains errors. Check the `errors` field in the body.

### Query: orders

Get all orders, or only the orders of one user.

**Arguments**

| Name | Type | Required | Description |
|------|------|----------|-------------|
| `userId` | ID | no | If set, returns only the orders of this user |

**Request**

    query {
      orders(userId: 3) {
        id
        userId
        productName
        quantity
        totalPrice
        status
        createdAt
      }
    }

**Response**

    {"data": {"orders": [{"id": "1", "userId": "3", "productName": "Keyboard", "quantity": 2, "totalPrice": "39.98", "status": "NEW", "createdAt": "2026-10-15T07:14:47.222599255Z"}]}}

| Case | Result |
|------|--------|
| Orders found | `data.orders` with the list |
| No orders (unknown `userId` or empty table) | `data.orders` is `[]` |
| Unknown field in the selection | `errors[0].extensions.classification` is `ValidationError`, message `Validation error (FieldUndefined@[orders/foo]) : Field 'foo' in type 'Order' is undefined` |
| Empty or malformed query | `errors[0].extensions.classification` is `InvalidSyntax`, message `Invalid syntax with offending token '<EOF>' at line 1 column 1` |

### Mutation: createOrder

Create an order. The total price is calculated as `unitPrice * quantity`. The new order gets status `NEW`.

**Input fields (`CreateOrderInput`)**

| Name | Type | Required | Description |
|------|------|----------|-------------|
| `userId` | ID | yes | Id of the user who makes the order |
| `productName` | String | yes | Product name |
| `quantity` | Int | yes | Number of items |
| `unitPrice` | String | yes | Price of one item, for example `"19.99"` |

**Request**

    mutation {
      createOrder(input: {userId: 3, productName: "Keyboard", quantity: 2, unitPrice: "19.99"}) {
        id
        userId
        productName
        quantity
        totalPrice
        status
        createdAt
      }
    }

**Response**

    {"data": {"createOrder": {"id": "1", "userId": "3", "productName": "Keyboard", "quantity": 2, "totalPrice": "39.98", "status": "NEW", "createdAt": "2026-10-15T07:14:47.222599255Z"}}}

| Case | Result |
|------|--------|
| Success | `data.createOrder` with the created order |
| Missing required input field | `ValidationError` in `errors` |
| Wrong type (for example text in `quantity`) | `ValidationError` in `errors` |
| Invalid `unitPrice` string (for example `"abc"`) | `errors` with an internal error, `data.createOrder` is `null` |

### Types

`Order` fields: `id` (ID), `userId` (ID), `productName` (String), `quantity` (Int), `totalPrice` (String), `status` (String), `createdAt` (String, ISO-8601 UTC).

Money values are strings to avoid precision loss.

### GraphiQL

Web page for manual queries: `GET http://localhost:8082/graphiql`.

### Actuator

| Method | Endpoint | Description | Success | 
|--------|----------|-------------|---------|
| GET | `/actuator/health` | Service and database status | `200`, `{"groups": ["liveness", "readiness"], "status": "UP"}` |
| GET | `/actuator/info` | Application info (empty by default) | `200`, `{}` |
| GET | `/actuator/metrics` | List of available metric names | `200`, `{"names": [...]}` |
| GET | `/actuator/metrics/{name}` | Value of one metric, for example `jvm.memory.used` | `200`, or `404` if the metric does not exist |

If the database is down, `/actuator/health` returns `503` with `"status": "DOWN"`.

### Known limitations

- `createOrder` does not check that the user exists.
- `quantity` and `unitPrice` are not validated: zero and negative values are accepted.
- The service has no authentication.


add docs/known-bugs.md
updated README.md
