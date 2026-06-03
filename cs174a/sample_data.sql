-- eMart & eDepot Sample Data

-- 1. Insert Products (eMart schema)
INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00101', 'Laptop', 'HP', 'A6111', 'Processor speed: 3.33Ghz
Ram size: 512 Mb
Hard disk size: 100Gb
Display Size: 17"', 12, 1630.00);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00201', 'Desktop', 'Dell', 'B420', 'Processor speed: 2.53Ghz
Ram size: 256 Mb
Hard disk size: 80Gb
OS: none', 12, 239.00);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00202', 'Desktop', 'eMachines', 'C3958', 'Processor speed: 2.9Ghz
Ram size: 512 Mb
Hard disk size: 80Gb', 12, 369.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00301', 'Monitor', 'Envision', 'D720', 'Size: 17"
Weight: 25 lb.', 36, 69.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00302', 'Monitor', 'Samsung', 'E712', 'Size: 17"
Weight: 9.6 lb.', 36, 279.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00401', 'Software', 'Symantec', 'F2005', 'Required disk size: 128 MB
Required RAM size: 64 MB', 60, 19.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00402', 'Software', 'McAfee', 'G2005', 'Required disk size: 128 MB
Required RAM size: 64 MB', 60, 19.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00403', 'Software', 'Oracle', 'H26', 'Required disk size: 1 GB
Required RAM size: 128 MB', 12, 29.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00501', 'Printer', 'HP', 'J1320', 'Resolution: 1200 dpi
Sheet capacity: 500
Weight: .4 lb', 12, 299.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00601', 'Camera', 'HP', 'K435', 'Resolution: 3.1 Mp
Max zoom: 5 times
Weight: 24.7 lb', 3, 119.99);

INSERT INTO Products (stock_number, category, manufacturer, model_number, description, warranty_months, price) VALUES ('AA00602', 'Camera', 'Canon', 'L738', 'Resolution: 3.1 Mp
Max zoom: 5 times
Weight: 24.7 lb', 1, 329.99);

-- 2. Insert Product Compatibility (eMart schema)
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00301', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00301', 'AA00202');

INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00302', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00302', 'AA00202');

INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00401', 'AA00101');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00401', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00401', 'AA00202');

INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00402', 'AA00101');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00402', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00402', 'AA00202');

INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00403', 'AA00101');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00403', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00403', 'AA00202');

INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00501', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00501', 'AA00202');

INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00601', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00601', 'AA00202');

INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00602', 'AA00201');
INSERT INTO ProductCompatibility (stock_number, compatible_stock_number) VALUES ('AA00602', 'AA00202');

-- 3. Insert Inventory (eDepot schema)
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00101', 'HP', 'A6111', 2, 1, 2, 'A9', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00201', 'Dell', 'B420', 3, 2, 5, 'A7', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00202', 'eMachines', 'C3958', 4, 2, 5, 'B52', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00301', 'Envision', 'D720', 4, 3, 6, 'C27', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00302', 'Samsung', 'E712', 5, 3, 6, 'C13', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00401', 'Symantec', 'F2005', 7, 5, 9, 'D27', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00402', 'McAfee', 'G2005', 7, 5, 9, 'D15', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00403', 'Oracle', 'H26', 7, 5, 9, 'D3', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00501', 'HP', 'J1320', 3, 2, 4, 'E7', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00601', 'HP', 'K435', 3, 2, 5, 'F9', 0);
INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, min_stock_level, max_stock_level, location, replenishment) VALUES ('AA00602', 'Canon', 'L738', 3, 2, 5, 'F3', 0);

-- 4. Insert Customers
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Lkim', 'Lkim', 'Linda Kim', 'lkim@cs', '45 Oak Ave, Santa Barbara, CA 93101', 'Gold');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Djones', 'Djones', 'Derek Jones', 'djones@cs', '88 Pine St, Goleta, CA 93117', 'Silver');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Mramirez', 'Mramirez', 'Maria Ramirez', 'mramirez@cs', '12 Maple Rd, Carpinteria, CA 93013', 'New');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Tpatel', 'Tpatel', 'Tariq Patel', 'tpatel@ce', '305 Elm Blvd, Ventura, CA 93001', 'New');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Swong', 'Swong', 'Sarah Wong', 'swong@ce', '77 Cedar Lane, Ojai, CA 93023', 'Green');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Bford', 'Bford', 'Blake Ford', 'bford@ce', '200 Spruce Ct, Oxnard, CA 93030', 'Green');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Tcodd', 'Tcodd', 'Ted Codd', 'tcodd@db', '123 Database St, Data, CA 93116', 'Gold');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Pchen', 'Pchen', 'Peter Chen', 'pchen@db', '456 Database Wy, Datum, CA 93117', 'Silver');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Jgray', 'Jgray', 'Jim Gray', 'jgray@db', '789 Database Rd, Datas, CA 93118', 'Green');
INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES ('Dknuth', 'Dknuth', 'Donald Knuth', 'dknuth@cs', '101 Compsci Ln, Comp, CA 94305', 'Gold');

-- 5. Insert Managers
INSERT INTO Managers (manager_id, password) VALUES ('Swong', 'Swong');
INSERT INTO Managers (manager_id, password) VALUES ('Tcodd', 'Tcodd');

COMMIT;
