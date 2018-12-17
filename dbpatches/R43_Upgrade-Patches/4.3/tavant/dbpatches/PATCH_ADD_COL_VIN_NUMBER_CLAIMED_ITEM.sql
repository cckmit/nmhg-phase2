--Purpose    : vin_number is  added to CLAIMED_ITEM table, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 11-Oct-2010

ALTER TABLE CLAIMED_ITEM ADD vin_number VARCHAR2(255 CHAR)
/
COMMIT
/