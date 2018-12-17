alter table inventory_item add SHIP_FROM_ORG_CODE varchar2(255)
/
alter table inventory_transaction add INVOICE_COMMENTS varchar2(4000)
/
alter table inventory_item add OPERATING_UNIT varchar2(255)--Not Run 
/
alter table inventory_item add waiver number(19,0)--Not Run
/
alter table option_info add mast_type varchar2(255)
/
alter table option_info add tire_Type varchar2(255)
/
alter table part_group add part_group number(19,0)
/
CREATE TABLE I18NWaiver_Text
   (	
    ID NUMBER(19,0) NOT NULL ENABLE, 
	LOCALE VARCHAR2(255 CHAR), 
	DESCRIPTION VARCHAR2(4000 CHAR), 
	DIESAL_TIER_WAIVER NUMBER(19,0) NOT NULL ENABLE, 
	CONSTRAINT I18NWaiver_Text_PK PRIMARY KEY (ID) ENABLE, 
	CONSTRAINT I18NWaiver_Text_WAIVER_FK FOREIGN KEY (DIESAL_TIER_WAIVER)
	  REFERENCES diesel_tier_waiver (ID) ENABLE
   ) 
/
CREATE SEQUENCE  I18n_Waiver_Text_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1000 CACHE 20 NOORDER  NOCYCLE
/