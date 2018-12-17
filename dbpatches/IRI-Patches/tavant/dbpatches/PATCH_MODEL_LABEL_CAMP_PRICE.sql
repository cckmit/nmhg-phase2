--PURPOSE    :Add labels to model and create unit price for campaign non oem parts
--AUTHOR     : PRADYOT.ROUT
--CREATED ON : 05-MAY-09


create table item_group_labels (
item_group number(19,0) not null,
Labels varchar(255) not null)
/
ALTER TABLE item_group_labels ADD CONSTRAINT item_group_labels_item_grp_FK FOREIGN KEY (item_group) 
REFERENCES item_group
/
ALTER TABLE item_group_labels ADD CONSTRAINT item_group_labels_label_FK FOREIGN KEY (Labels) 
REFERENCES label
/
CREATE SEQUENCE CAMPAIGN_NON_OEM_PRICE_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE TABLE Campaign_Non_Oem_Price
(
  ID                   NUMBER(19)               NOT NULL,
  price_per_unit_amt   NUMBER(19,2),
  price_per_unit_curr  VARCHAR2(255 CHAR),
  NON_OEM_PART           NUMBER(19),
  CONSTRAINT Campaign_Non_Oem_Price_PK PRIMARY KEY (ID)
)
/
ALTER TABLE Campaign_Non_Oem_Price ADD CONSTRAINT NON_OEM_PART_CAMPAIGN_FK FOREIGN KEY (NON_OEM_PART) 
REFERENCES non_oem_part_to_replace
/
commit
/