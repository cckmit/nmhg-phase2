--PURPOSE    : PATCH TO ADD BRAND_TYPE,OPPOSITE_SERIES,COMPANY_TYPE,BUILD_PLANT
--AUTHOR     : KALYANI
--CREATED ON : 20-JAN-13

ALTER TABLE ITEM_GROUP ADD (BRAND_TYPE VARCHAR2(100 BYTE),OPPOSITE_SERIES NUMBER(19,0),COMPANY_TYPE VARCHAR2(255 BYTE),BUILD_PLANT VARCHAR2(255 BYTE))
/