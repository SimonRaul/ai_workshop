UPDATE orders SET country = 'N/A' WHERE country IS NULL;
UPDATE orders SET city = 'N/A' WHERE city IS NULL;
UPDATE orders SET county = 'N/A' WHERE county IS NULL;
UPDATE orders SET street_address = 'N/A' WHERE street_address IS NULL;

ALTER TABLE orders
    ALTER COLUMN country SET NOT NULL,
    ALTER COLUMN city SET NOT NULL,
    ALTER COLUMN county SET NOT NULL,
    ALTER COLUMN street_address SET NOT NULL;
