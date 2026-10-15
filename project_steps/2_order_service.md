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

POSTMAN - not available for export

Step 1: Query orders (all fields) works as GET
return all orders, each order has all fields

In Postman:

Click New → GraphQL to create a GraphQL request.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
query {
orders {
id
userId
productName
quantity
totalPrice
status
createdAt
}
}
Click Query (the blue button next to the URL).
Click Save. Postman offers to create a collection: 
name it order_service_graphql and save the request as orders - all fields.

Expected result: 200 OK and data.orders contains the Keyboard order.

Step 2: Same query, only two fields, works as GET
return all orders, each order has only productName and totalPrice
In Postman:

In the collection order_service_graphql, duplicate the request orders
- all fields (right-click → Duplicate) and rename the copy to orders
- - two fields.
Open the Query tab and replace the query with:
graphql
query {
orders {
productName
totalPrice
}
}
Click Query.
Click Save.

Expected result: 200 OK, and each order in data.orders contains only productName 
and totalPrice.

Difference from REST: in REST the server decides which fields to return 
(GET /api/users/1 always returns id, name and email). In GraphQL the client 
lists the needed fields in the query, so the response contains exactly those 
fields.

Step 3: Create an order (mutation) works as POST

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
createOrder(input: {userId: 3, productName: "Mouse", quantity: 3, unitPrice: "10.50"}) {
id
userId
productName
quantity
totalPrice
status
}
}
Click Query.
Click Save and name the request createOrder.

Expected result: 200 OK, data.createOrder has 
"id": "2", "totalPrice": "31.50", "status": "NEW".

Step 4: Create an order with missing fields works as POST

required fields: userId, productName, quantity и unitPrice

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
createOrder(input: {userId: 3, productName: "Keyboard"}) {
id
productName
totalPrice
status
}
}
Click Query.
Click Save and name the request createOrder - missing fields.

Expected result: 200 OK,
"message": "Validation error (WrongType@[createOrder]) : 
argument 'input' with value 'ObjectValue{objectFields=[ObjectField{name='userId', 
value=IntValue{value=3}}, ObjectField{name='productName', 
value=StringValue{value='Keyboard'}}]}' is missing required fields 
'[quantity, unitPrice]'",

Step 5: Query with an unknown field works as POST

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
query {
orders {
id
foo
}
}
Click Query.
Click Save and name the request orders - unknown field.

Expected result: 200 OK, in errors a message Validation error 
(FieldUndefined@[orders/foo]) : Field 'foo' in type 'Order' is undefined, 
with "classification": 
"ValidationError".

Step 6: Filter orders(userId: 3) works as GET
get all orders for 1 user by userId
In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
query {
orders(userId: 3) {
id
userId
productName
totalPrice
}
}
Click Query.
Click Save and name the request orders - filter by userId.

Expected result: 200 OK, data.orders contains the orders of user 3 
(Keyboard and Mouse), and every item has "userId": "3".

Step 7: Filter orders(userId: 999) works as GET

User 999 not exist
In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
query {
orders(userId: 999) {
id
userId
productName
totalPrice
}
}
Click Query.
Click Save and name the request orders - filter by unknown userId.

Expected result: 200 OK, "data": {"orders": []}, no errors field.

Step 8: Wrong data types

8.1 Text in quantity works as POST

Negative test for create an order
send a string instead of an Int in quantity, for example "two" instead of 2
The server should return a ValidationError.

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
createOrder(input: {userId: 3, productName: "Test", quantity: "two", unitPrice: "10.00"}) {
id
}
}
Click Query.
Click Save and name the request createOrder - wrong type quantity.

Expected result: 200 OK, a ValidationError in errors (the value "two" is 
not an Int), no data.

8.2 Letters in userId

Negative test for create an order
send a string instead of an Int in userId, for example "two" instead of 2
The server should return a ValidationError.

Repeat the same steps with this query and the name createOrder - 
wrong type userId:

graphql
mutation {
createOrder(input: {userId: "abc", productName: "Test", quantity: 1, unitPrice: "10.00"}) {
id
}
}

Expected result: 200 OK and an error in errors. 
The exact text is not known yet (userId is an ID, 
so the failure may come from the conversion to a number, 
not from schema validation). 

-----------------

add PUT and DELETE  

update apps/order_service/src/main/resources/graphql/schema.graphqls
add src/main/java/com/qasuite/order_service/exception/OrderNotFoundException.java
update src/main/java/com/qasuite/order_service/service/OrderService.java
update src/main/java/com/qasuite/order_service/graphql/OrderGraphQlController.java

Reload all Maven Projects
docker compose up --build

POSTMAN, part 2

9. Update an order status (updateOrder) works as PUT

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
updateOrder(id: 1, status: "PAID") {
id
productName
status
}
}
Click Query.
Click Save and name the request updateOrder.

Expected result: 200 OK, "id": "1", "productName": "Keyboard", "status": "PAID".

10. Delete an order (deleteOrder) works as DELETE

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
deleteOrder(id: 2)
}
Click Query.
Click Save and name the request deleteOrder.

Expected result: 200 OK, "data": {"deleteOrder": true}.

11. Delete a non-existent order

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
deleteOrder(id: 2)
}
Click Query.
Click Save and name the request deleteOrder - not found.

Expected result: 200 OK, "data": null, and in errors a message 
like INTERNAL_ERROR for ... with "classification": "INTERNAL_ERROR". 
The text Order not found: 2 does not reach the client.

12. Update a non-existent order

In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
updateOrder(id: 999, status: "PAID") {
id
status
}
}
Click Query.
Click Save and name the request updateOrder - not found.

Expected result: 200 OK, "data": null, and in errors 
a message like INTERNAL_ERROR 
for ... with "classification": "INTERNAL_ERROR".

13. Update with an invalid status
exist only one status: NEW
In Postman:

In the collection order_service_graphql, click New → GraphQL.
Enter the URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
updateOrder(id: 1, status: "abc") {
id
productName
status
}
}
Click Query.
Click Save and name the request updateOrder - invalid status.

Expected result: 200 OK, "id": "1", "status": "abc"

14. Create an order with a quantity: 0

In Postman:

In the collection order_service_graphql, click New → GraphQL.
URL: http://localhost:8082/graphql
Open the Query tab and paste:
graphql
mutation {
createOrder(input: {userId: 3, productName: "Zero", quantity: 0, 
unitPrice: "10.00"}) {
id
quantity
totalPrice
status
}
}
Click Query, then Save as createOrder - quantity zero.

Expected result: 200 OK, a new order with "quantity": 
0 and "totalPrice": "0.00".


# Order Service: more negative tests (ideas)

**createOrder: input values**

- Negative `unitPrice` (`"-5.00"`)
- Zero `unitPrice` (`"0.00"`)
- Non-numeric `unitPrice` (`"abc"`)
- Empty `unitPrice` (`""`)
- `unitPrice` with more than 2 decimal places (`"19.999"`)
- `unitPrice` with a comma instead of a dot (`"19,99"`)
- Total price above `99999999.99` (does not fit into `DECIMAL(10, 2)`)
- Very large `quantity` (`2147483647`, then `2147483648` as out of `Int` range)
- Empty `productName` (`""`)
- Whitespace-only `productName` (`"   "`)
- `productName` longer than 255 characters
- `productName` with special characters, emoji, SQL fragments (`'; DROP TABLE orders; --`), HTML or script tags
- Non-existent `userId` (`99999`)
- Zero or negative `userId` (`0`, `-1`)
- Very large `userId` (beyond `Long` range)
- Non-numeric `userId` (`"abc"`, already checked, returns `INTERNAL_ERROR`)

**createOrder: structure of the request**

- Missing required field (checked)
- Wrong type (checked for `quantity`)
- `null` for a required field
- Extra unknown field in `input`
- Missing `input` completely
- Duplicate orders: same data sent twice (no idempotency)

**updateOrder / deleteOrder**

- Non-existent `id` (checked, returns `INTERNAL_ERROR`)
- Non-numeric `id` (`"abc"`)
- Negative or zero `id`
- Very large `id`
- Empty `status` (`""`)
- `status` with 1000+ characters
- `status` with special characters
- Delete the same order twice (checked)
- Update an order after it was deleted

**Query orders**

- Unknown field (checked)
- `userId` as text (`"abc"`)
- Negative `userId`
- Empty selection (`orders { }`)
- Deeply nested or repeated fields
- Same field requested many times via aliases (basic load check)
- Syntax errors: missing brace, missing quote, empty query (checked)

**Protocol level**

- `GET /graphql` instead of `POST`
- Wrong or missing `Content-Type` header
- Malformed JSON body
- Empty body
- Very large request body
- Unknown operation name when several operations are sent

**Actuator**

- `GET /actuator/metrics/unknown.metric` (expect `404`)
- `POST` to `/actuator/health` (expect `405`)
- `/actuator/health` while MySQL is stopped (expect `503`, `DOWN`)

updated readme file and known_bugs.md
add postman collection for order service 