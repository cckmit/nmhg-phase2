-- Purpose    : Added Missing Incidental Cost Categories in the Campaign service detail
-- Author     : Jitesh Jain
-- Created On : 18-Jun-09

CREATE SEQUENCE CAMPAIGN_SECTION_PRICE_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE TABLE Campaign_section_Price
(
  ID                   NUMBER(19)               NOT NULL,
  price_per_unit_amt   NUMBER(19,2),
  price_per_unit_curr  VARCHAR2(255 CHAR),
  NON_OEM_PART           NUMBER(19),
  CONSTRAINT Campaign_section_Price_PK PRIMARY KEY (ID)
)
/
ALTER TABLE Campaign_section_Price ADD CONSTRAINT NON_OEMPART_CAMPAIGN_FK FOREIGN KEY (NON_OEM_PART) 
REFERENCES non_oem_part_to_replace
/
alter table CAMPAIGN add(contract NUMBER(19))
/
alter table CAMPAIGN add constraint campaign_contract_fk foreign key(CONTRACT) references CONTRACT(ID)
/
CREATE TABLE CAMPAIGN_MISC_PARTS (CAMPAIGN NUMBER(19, 0) NOT NULL,MISC_PARTS_TO_REPLACE NUMBER(19, 0) NOT NULL)
/
alter table "CAMPAIGN_MISC_PARTS" add constraint CAMPAIGNMISCPARTS_CMPGN_FK foreign key("CAMPAIGN") references "CAMPAIGN"("ID")
/
alter table "CAMPAIGN_MISC_PARTS" add constraint CAMPAIGNMISCPARTS_MISCREPL_FK foreign key("MISC_PARTS_TO_REPLACE") references "NON_OEM_PART_TO_REPLACE"("ID")
/
alter table NON_OEM_PART_TO_REPLACE add(MISC_ITEM_CONFIG NUMBER(19))
/
alter table NON_OEM_PART_TO_REPLACE add(MISC_ITEM NUMBER(19))
/
alter table "NON_OEM_PART_TO_REPLACE" add constraint NON_OEM_PN_RP_MISC_ITEM_CFG_FK foreign key("MISC_ITEM_CONFIG") references "MISC_ITEM_CONFIG"("ID")
/
alter table "NON_OEM_PART_TO_REPLACE" add constraint NON_OEM_PN_TO_REP_MISCITEM_FK foreign key("MISC_ITEM") references "MISC_ITEM"("ID")
/
alter table DOCUMENT add(Mandatory NUMBER(1))
/
ALTER TABLE CAMPAIGN_LABOR_DETAIL MODIFY ("SPECIFIED_LABOR_HOURS" NUMBER(19, 2))
/
ALTER TABLE LABOR_DETAIL MODIFY ("SPECIFIED_HOURS_IN_CAMPAIGN" NUMBER(19, 2))
/
alter table CAMPAIGN_SECTION_PRICE add(Section_Name VARCHAR2(255))
/
alter table CAMPAIGN_SECTION_PRICE add(CAMPAIGN_SERVICE_DETAIL NUMBER(19))
/
alter table CAMPAIGN_SECTION_PRICE add constraint CMPGN_PRICE_CMPGN_SERV_FK foreign key("CAMPAIGN_SERVICE_DETAIL") references "CAMPAIGN_SERVICE_DETAIL"("ID")
/
drop table CAMPAIGN_NON_OEM_PRICE cascade constraints
/
drop sequence CAMPAIGN_NON_OEM_PRICE_SEQ
/
commit
/