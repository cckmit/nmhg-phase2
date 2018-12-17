--Purpose    : Alter UOM MAPPING table increase BUSINESS_UNIT_INFO Column size
--Author     : Jitesh Jain
--Created On : 26-Oct-08

ALTER TABLE UOM_MAPPINGS MODIFY ("BUSINESS_UNIT_INFO" VARCHAR2(50 BYTE))
/
COMMIT
/