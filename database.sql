drop DATABASE shopping_cart;
CREATE DATABASE shopping_cart;
USE shopping_cart;

-- USERS
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(50),
    mobile VARCHAR(15)
);

-- ADMIN
CREATE TABLE admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(50)
);


-- PRODUCTS
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    price DOUBLE
   
);
ALTER TABLE products 
ADD brand VARCHAR(50),
ADD quantity INT;
-- CART
CREATE TABLE cart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    quantity INT
);

-- ORDERS
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    total_amount DOUBLE,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ORDER ITEMS
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT,
    price DOUBLE
);

-- BILL
CREATE TABLE bill (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    total_amount DOUBLE,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);






-- ACCOUNTS
CREATE TABLE accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    balance DOUBLE
);


-- TRANSACTIONS
CREATE TABLE transactions (
    txn_id INT PRIMARY KEY AUTO_INCREMENT,
    from_acc INT,
    to_acc INT,
    amount DOUBLE,
    txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);






-- SAMPLE DATA
INSERT INTO users(username,password,mobile) VALUES ('sowmya','123','9999999999');
INSERT INTO admin(username,password) VALUES ('admin','admin');

INSERT INTO products(name, brand, price, quantity) VALUES
('Yoga Mat Pro','FitLife',1200,15),
('Stainless Steel Bottle','Milton',600,25),
('LED Study Lamp','Philips',1500,12),
('Ergonomic Office Chair','GreenSoul',8500,5),
('Wireless Phone Stand','Portronics',900,18),
('Digital Alarm Clock','Casio',1100,10),
('Mini Air Cooler','Bajaj',4500,6),
('Electric Kettle','Prestige',1800,14),
('Vacuum Cleaner','Eureka Forbes',7000,7),
('Induction Cooktop','Pigeon',2200,11),
('Smart LED Bulb','Wipro',700,20),
('Wall Mounted Bookshelf','HomeTown',3500,4),
('Portable Blender','NutriMix',2500,9),
('Laptop Backpack','Skybags',2000,13),
('Noise Cancelling Earplugs','Loop',1500,17),
('Handheld Garment Steamer','Philips',3200,8),
('Fitness Resistance Bands','Boldfit',800,22),
('Tabletop Tripod Stand','Digitek',1200,16),
('Room Air Purifier','Mi',9000,5),
('Cordless Screwdriver Kit','Bosch',4000,6);

SET SQL_SAFE_UPDATES = 0;
UPDATE products SET brand='Generic', quantity=10 WHERE brand IS NULL;
SELECT * FROM users;
SELECT * FROM products;
SELECT * FROM cart;
SELECT * FROM orders;
SELECT * FROM transaction_history ;
SELECT * FROM bill_history ;

CREATE TABLE bill_history (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    total_amount DOUBLE,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE transaction_history (
    txn_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    amount DOUBLE,
    txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);