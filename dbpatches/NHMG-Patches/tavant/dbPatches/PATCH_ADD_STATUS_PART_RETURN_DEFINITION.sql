--Purpose    : Patch for adding the satus column to Part Return Definition table.
--Author     : Kuldeep Patil	
--Created On : 10-Aug-2012

ALTER TABLE PART_RETURN_DEFINITION ADD STATUS VARCHAR2(20)
/
UPDATE PART_RETURN_DEFINITION SET STATUS = 'ACTIVE' WHERE STATUS IS NULL AND D_ACTIVE = 1
/
COMMIT
/