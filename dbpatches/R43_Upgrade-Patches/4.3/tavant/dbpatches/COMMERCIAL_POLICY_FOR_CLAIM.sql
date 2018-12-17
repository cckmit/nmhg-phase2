--PURPOSE    : Patch for adding new column to claim table, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 11-Oct-2010

ALTER TABLE CLAIM ADD COMMERCIAL_POLICY NUMBER(1,0)
/
update claim set commercial_policy = 0
/
COMMIT
/
