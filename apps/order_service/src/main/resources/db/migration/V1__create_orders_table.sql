CREATE TABLE orders (
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    user_id      BIGINT         NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    quantity     INT            NOT NULL,
    total_price  DECIMAL(10, 2) NOT NULL,
    status       VARCHAR(50)    NOT NULL,
    created_at   TIMESTAMP(6)   NOT NULL,
    PRIMARY KEY (id)
);