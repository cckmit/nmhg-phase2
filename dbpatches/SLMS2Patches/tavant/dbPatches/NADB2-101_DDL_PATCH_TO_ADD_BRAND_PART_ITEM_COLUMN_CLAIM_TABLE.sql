-- Patch to add brand item number in claim table

alter table claim add (BRAND_PART_ITEM NUMBER(19,0))
/
alter table claim add CONSTRAINT CLAIM_BRAPART_FK FOREIGN KEY (BRAND_PART_ITEM) REFERENCES BRAND_ITEM (ID) ENABLE
/