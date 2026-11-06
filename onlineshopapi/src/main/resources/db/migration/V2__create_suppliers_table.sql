CREATE TABLE suppliers
(
    id             UUID         PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    location       VARCHAR(255) NOT NULL,
    description    VARCHAR(1000),
    contact_email  VARCHAR(255),
    phone          VARCHAR(50)
);

ALTER TABLE products ADD COLUMN supplier_id UUID;

INSERT INTO suppliers (id, name, location, description, contact_email, phone)
VALUES ('5099ec00-0000-0000-0000-000000000001', 'Legacy Supplier', 'Unknown',
        'Placeholder supplier assigned to products that existed before supplier tracking was introduced.',
        NULL, NULL);

UPDATE products SET supplier_id = '5099ec00-0000-0000-0000-000000000001' WHERE supplier_id IS NULL;

ALTER TABLE products
    ALTER COLUMN supplier_id SET NOT NULL,
    ADD CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id);

CREATE INDEX idx_products_supplier_id ON products (supplier_id);
