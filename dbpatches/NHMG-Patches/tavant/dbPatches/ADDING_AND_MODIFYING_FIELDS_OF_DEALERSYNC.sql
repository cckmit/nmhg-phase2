--Purpose    : Patch for adding the column in address and address_book_address_mapping tables.
--Author     : Suneetha Nagaboyina
--Created On : 11-FEB-2013

alter table ADDRESS add ("SIC_CODE" VARCHAR2(255))
/
alter table ADDRESS_BOOK_ADDRESS_MAPPING add ("IS_END_CUSTOMER" NUMBER(1,0))
/
alter table DEALERSHIP add ("SALES_TERRITORY_NAME" VARCHAR2(255)) 
/
alter table service_provider drop column HRS_OPERATION_WEEKDAY
/
alter table service_provider drop column HRS_OPERATION_SATURDAY
/
alter table service_provider drop column HRS_OPERATION_SUNDAY
/
alter table DEALERSHIP drop column mark_region
/
alter table service_provider drop column customer_category
/
alter table DEALERSHIP add (fleet_business_area VARCHAR2(255))
/
