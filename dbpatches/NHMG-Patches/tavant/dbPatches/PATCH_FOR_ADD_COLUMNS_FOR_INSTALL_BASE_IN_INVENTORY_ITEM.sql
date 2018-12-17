--PURPOSE    : Patch for adding new columns to inventory_item table, as part of NMHG TWMS implementation
--AUTHOR     : Pracher Pancholi
--CREATED ON : 2-Nov-2012

ALTER TABLE inventory_item ADD (bill_to_purchase_order VARCHAR2(100),order_received_date DATE,actual_cts_date DATE,disc_authorization_number VARCHAR2(100),discount_percent NUMBER(19,2),order_type VARCHAR2(100),mde_capacity NUMBER(19,2),model_power NUMBER(19,2),mast_type VARCHAR2(100),tire_type VARCHAR2(100),brand_type VARCHAR2(100),ita_book_date DATE, ita_book_report_date DATE,ita_delivery_date DATE,ita_delivery_report_date DATE)
/