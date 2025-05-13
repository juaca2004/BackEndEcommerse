CREATE DATABASE IF NOT EXISTS simple_shop;
USE simple_shop;

-- Tabla de usuarios básica
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       password VARCHAR(50) NOT NULL
);

-- Tabla de productos CON DESCRIPCIÓN
CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          description TEXT NOT NULL,  -- Columna añadida para descripción
                          price DECIMAL(10,2) NOT NULL,
                          stock INT NOT NULL
);

-- Carrito (1 por usuario)
CREATE TABLE carts (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       user_id INT NOT NULL UNIQUE,
                       FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Items del carrito
CREATE TABLE cart_items (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            cart_id INT NOT NULL,
                            product_id INT NOT NULL,
                            quantity INT NOT NULL DEFAULT 1,
                            FOREIGN KEY (cart_id) REFERENCES carts(id),
                            FOREIGN KEY (product_id) REFERENCES products(id)
);


-- Tabla de órdenes simplificada
CREATE TABLE orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        total DECIMAL(10,2) NOT NULL,
                        city TEXT NOT NULL,
                        address TEXT NOT NULL,
                        fullName TEXT NOT NULL,
                        typePayment TEXT NOT NULL,
                        status VARCHAR(20) DEFAULT 'pending',
                        FOREIGN KEY (user_id) REFERENCES users(id)
);
