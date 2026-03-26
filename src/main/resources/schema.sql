CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT chk_users_role
        CHECK (role IN ('MANAGER', 'ADMIN', 'STAFF'))
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    threshold INT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS inventory_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    date DATETIME NOT NULL,
    remarks VARCHAR(500),
    CONSTRAINT chk_inventory_transactions_type
        CHECK (type IN ('ADD', 'REMOVE')),
    CONSTRAINT fk_inventory_transactions_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS stock_alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    current_quantity INT NOT NULL,
    threshold INT NOT NULL,
    alert_message VARCHAR(500) NOT NULL,
    created_at DATETIME NOT NULL,
    status VARCHAR(10) NOT NULL,
    CONSTRAINT chk_stock_alerts_status
        CHECK (status IN ('ACTIVE', 'RESOLVED')),
    CONSTRAINT fk_stock_alerts_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);
