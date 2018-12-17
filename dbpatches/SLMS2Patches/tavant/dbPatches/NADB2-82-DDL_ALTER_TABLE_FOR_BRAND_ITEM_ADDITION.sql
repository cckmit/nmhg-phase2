-- Patch to add brand item number in different tables

alter table service_information add (CAUSAL_BRAND_PART NUMBER(19,0))
/
alter table service_information add CONSTRAINT SERINFO_CAUSALBRPART_FK FOREIGN KEY (CAUSAL_BRAND_PART) REFERENCES BRAND_ITEM (ID) ENABLE
/
alter table installed_parts add (BRAND_ITEM NUMBER(19,0))
/
alter table installed_parts add CONSTRAINT INST_PRT_BRITEM_FK FOREIGN KEY (BRAND_ITEM) REFERENCES BRAND_ITEM (ID) ENABLE
/
alter table oem_part_replaced add (BRAND_ITEM NUMBER(19,0))
/
alter table oem_part_replaced add CONSTRAINT REPLCED_PRT_BRITEM_FK FOREIGN KEY (BRAND_ITEM) REFERENCES BRAND_ITEM (ID) ENABLE
/