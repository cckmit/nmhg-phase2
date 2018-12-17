--Purpose    : DDL for NMHGSLMS-425 -Mapping table constraints for policy and products
--Author     : Arpitha Nadig AR
--Created On : 27-JAN-2013
alter table POLICY_PRODUCTS add constraint POLICY_DEFN_FK foreign key (POLICY_DEFINITION) references POLICY_DEFINITION(ID)
/
alter table POLICY_PRODUCTS add constraint POLICY_PROD_MAPPING_FK foreign key (POLICY_PRODUCT_MAPPING) references POLICY_PRODUCT_MAPPING(ID)
/
alter table policy_product_mapping drop column POLICY_DEFN
/