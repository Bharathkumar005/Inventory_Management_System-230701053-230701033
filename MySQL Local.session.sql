
USE inventory_db;

CREATE TABLE products(
    product_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100),
    quantity INT,
    price DOUBLE
);
SHOW GRANTS FOR 'root'@'localhost';
GRANT ALL PRIVILEGES ON inventory_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
