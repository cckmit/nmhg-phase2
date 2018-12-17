--Purpose    : Patch for Minimum Labour Round Up for claim types, changes done as per 4.3 upgrade
--Author     : Kuldeep Patil
--Created On : 11-Oct-2010

ALTER TABLE MINIMUM_LABOR_ROUND_UP ADD (APPL_MACHINE_CLAIM NUMBER(1,0))
/
ALTER TABLE MINIMUM_LABOR_ROUND_UP ADD (APPL_PARTS_CLAIM NUMBER(1,0))
/
COMMIT
/