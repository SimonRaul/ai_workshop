INSERT INTO suppliers (id, name, location, description, contact_email, phone)
VALUES ('5099ec00-0000-0000-0000-000000000002', 'TechSource Electronics', 'Shenzhen, China',
        'Manufacturer and distributor of consumer electronics.', 'contact@techsource.example', '+86-755-1234567'),
       ('5099ec00-0000-0000-0000-000000000003', 'Northline Apparel Co.', 'Cluj-Napoca, Romania',
        'Textile and apparel supplier for clothing lines.', 'sales@northlineapparel.example', '+40-264-112233'),
       ('5099ec00-0000-0000-0000-000000000004', 'GreenYard Supplies', 'Bucharest, Romania',
        'Home and garden equipment supplier.', 'info@greenyardsupplies.example', '+40-21-9988776');

UPDATE products SET supplier_id = '5099ec00-0000-0000-0000-000000000002'
WHERE id IN ('fade0001-0000-0000-0000-000000000001', 'fade0002-0000-0000-0000-000000000002', 'fade0003-0000-0000-0000-000000000003');

UPDATE products SET supplier_id = '5099ec00-0000-0000-0000-000000000003'
WHERE id IN ('fade0004-0000-0000-0000-000000000004', 'fade0005-0000-0000-0000-000000000005');

UPDATE products SET supplier_id = '5099ec00-0000-0000-0000-000000000004'
WHERE id IN ('fade0006-0000-0000-0000-000000000006', 'fade0007-0000-0000-0000-000000000007',
             'fade0008-0000-0000-0000-000000000008', 'fade0009-0000-0000-0000-000000000009');
-- 'fade000a-0000-0000-0000-00000000000a' is intentionally left on the Legacy Supplier from V2.
