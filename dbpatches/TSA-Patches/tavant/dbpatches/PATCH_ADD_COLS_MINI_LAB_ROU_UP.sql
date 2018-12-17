--Purpose    : Patch for Minimum Labour Round Up for claim types
--Author     : Saya Sudha
--Created On : 19-APR-10

ALTER TABLE MINIMUM_LABOR_ROUND_UP ADD (APPL_MACHINE_CLAIM NUMBER(1,0))
/
ALTER TABLE MINIMUM_LABOR_ROUND_UP ADD (APPL_PARTS_CLAIM NUMBER(1,0))
/
COMMIT
/