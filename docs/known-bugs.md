Known bugs (intentional test targets)

User Service

Token is not tied to the owner: any logged-in user can read, update and delete other users.
Token of a deleted user keeps working until it expires.
401 caused by a missing or invalid token returns an empty body, while other errors return JSON (inconsistent error formats).
name or email longer than 255 characters causes a database error instead of 400.

Order Service

createOrder does not check that the user exists.
quantity of 0 or a negative number is accepted.
Zero or negative unitPrice is accepted.
unitPrice: "abc" causes an internal error instead of a clear validation message.
Total price above 99999999.99 does not fit into DECIMAL(10, 2) and causes a database error.
productName can be empty or longer than 255 characters (longer values cause a database error).