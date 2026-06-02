CREATE TABLE Inventory (
    stock_number VARCHAR2(50) PRIMARY KEY,
    manufacturer VARCHAR2(100) NOT NULL,
    model_number VARCHAR2(100) NOT NULL,
    quantity INTEGER NOT NULL,
    min_stock_level INTEGER NOT NULL,
    max_stock_level INTEGER NOT NULL,
    location VARCHAR2(25) NOT NULL,
    replenishment INTEGER,

    CONSTRAINT check_quantity
        CHECK (quantity > 0 AND quantity < max_stock_level),

    CONSTRAINT check_min_stock
        CHECK (min_stock_level > 0),

    CONSTRAINT check_max_stock
        CHECK (max_stock_level > 0 AND max_stock_level > min_stock_level),

    CONSTRAINT check_replenishment
        CHECK (replenishment > 0)
);
