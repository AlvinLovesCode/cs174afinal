CREATE TABLE Inventory (
    stock_number VARCHAR2(7) PRIMARY KEY,
    manufacturer VARCHAR2(100) NOT NULL,
    model_number VARCHAR2(100) NOT NULL,
    quantity INTEGER NOT NULL,
    min_stock_level INTEGER NOT NULL,
    max_stock_level INTEGER NOT NULL,
    location VARCHAR2(25) NOT NULL,
    replenishment INTEGER,

    CONSTRAINT unique_manufacturer_model 
        UNIQUE (manufacturer, model_number),

    CONSTRAINT check_inventory_quantity
        CHECK (quantity >= 0 AND quantity < max_stock_level),

    CONSTRAINT check_min_stock
        CHECK (min_stock_level >= 0),

    CONSTRAINT check_max_stock
        CHECK (max_stock_level > min_stock_level),

    CONSTRAINT check_replenishment
        CHECK (replenishment >= 0),

    CONSTRAINT check_stock_number
        CHECK (REGEXP_LIKE(stock_number, '^[A-Z]{2}[0-9]{5}$')),
    
    CONSTRAINT ck_location          
        CHECK (REGEXP_LIKE(location, '^[A-Za-z][1-9][0-9]*$'))
);

CREATE TABLE ShippingNotice (
    notice_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shipping_company VARCHAR2(100) NOT NULL
);

CREATE TABLE ShippingNoticeItem (
    notice_id INTEGER NOT NULL,
    manufacturer VARCHAR2(100) NOT NULL,
    model_number VARCHAR2(100) NOT NULL,
    quantity INTEGER NOT NULL,

    PRIMARY KEY (notice_id, manufacturer, model_number),
    FOREIGN KEY (notice_id) REFERENCES ShippingNotice(notice_id),
    CONSTRAINT check_shipping_quantity  
        CHECK (quantity > 0)
);

CREATE TABLE ReplenishmentOrder (
    replenishment_order_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    manufacturer VARCHAR2(100) NOT NULL
);

CREATE TABLE ReplenishmentOrderItem (
    replenishment_order_id INTEGER NOT NULL,
    stock_number VARCHAR2(7) NOT NULL,
    quantity_requested INTEGER NOT NULL,

    PRIMARY KEY (replenishment_order_id, stock_number),
    FOREIGN KEY (replenishment_order_id) REFERENCES ReplenishmentOrder(replenishment_order_id),
    FOREIGN KEY (stock_number) REFERENCES Inventory(stock_number),
    CONSTRAINT check_replenishment_quantity CHECK (quantity_requested > 0)
);