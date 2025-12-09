-- V002__seed_data.sql
-- Idempotent seed data for development: customers, products, an order, items, and a payment

-- Customers
INSERT INTO customers (id, name, email, created_at)
SELECT '11111111-1111-1111-1111-111111111111', 'Alice Example', 'alice@example.com', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'alice@example.com');

INSERT INTO customers (id, name, email, created_at)
SELECT '22222222-2222-2222-2222-222222222222', 'Bob Example', 'bob@example.com', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'bob@example.com');

-- Products
INSERT INTO products (id, sku, name, price_cents, stock_quantity, version)
SELECT 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'SKU-RED-001', 'Red T-Shirt', 1999, 100, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SKU-RED-001');

INSERT INTO products (id, sku, name, price_cents, stock_quantity, version)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'SKU-BLU-002', 'Blue Jeans', 4599, 50, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SKU-BLU-002');

INSERT INTO products (id, sku, name, price_cents, stock_quantity, version)
SELECT 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'SKU-GRN-003', 'Green Hat', 1299, 200, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SKU-GRN-003');

-- One sample order for Alice
INSERT INTO orders (id, customer_id, total_cents, status, created_at, updated_at)
SELECT 'dddddddd-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', 1999+1299, 'PLACED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE id = 'dddddddd-dddd-dddd-dddd-dddddddddddd');

-- Order items (link to products)
INSERT INTO order_items (id, order_id, product_id, unit_price_cents, quantity)
SELECT 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1999, 1
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE id = 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee');

INSERT INTO order_items (id, order_id, product_id, unit_price_cents, quantity)
SELECT 'ffffffff-ffff-ffff-ffff-ffffffffffff', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 1299, 1
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE id = 'ffffffff-ffff-ffff-ffff-ffffffffffff');

-- Payment for the order
INSERT INTO payments (id, order_id, amount_cents, method, status, created_at)
SELECT '99999999-9999-9999-9999-999999999999', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 1999+1299, 'CARD', 'COMPLETED', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM payments WHERE id = '99999999-9999-9999-9999-999999999999');
