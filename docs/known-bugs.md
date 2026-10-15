# Known bugs (intentional test targets)

Before adding a bug to a report, reproduce it manually and write down the actual response.

## User Service

1. Token is not tied to the owner: any logged-in user can read, update and delete other users.
2. Token of a deleted user keeps working until it expires.
3. `401` caused by a missing or invalid token returns an empty body, while other errors return JSON (inconsistent error formats).
4. `name` or `email` longer than 255 characters causes a database error instead of `400`.

## Order Service

5. `createOrder` does not check that the user exists.
6. `quantity` of `0` or a negative number is accepted (the total price becomes `0.00` or negative).
7. Zero or negative `unitPrice` is accepted.
8. `unitPrice: "abc"` causes an internal error instead of a clear validation message.
9. Total price above `99999999.99` does not fit into `DECIMAL(10, 2)` and causes a database error.
10. `productName` can be empty or longer than 255 characters (longer values cause a database error).
11. `createOrder` with a non-numeric `userId` (for example `"abc"`) returns `INTERNAL_ERROR` with an unhelpful message instead of a clear validation error.
12. `updateOrder` accepts any string as `status` (for example `"abc"` or an empty string). Expected: only allowed values (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`).
13. `updateOrder` and `deleteOrder` for a non-existent order return `INTERNAL_ERROR` instead of a clear "Order not found" message.