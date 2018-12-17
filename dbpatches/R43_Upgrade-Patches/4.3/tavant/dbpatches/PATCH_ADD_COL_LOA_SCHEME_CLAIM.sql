--Purpose    : LOA_SCHEME Added to claim, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 12-Oct-2010

alter table CLAIM add LOA_SCHEME NUMBER(19,0)
/
COMMIT
/


