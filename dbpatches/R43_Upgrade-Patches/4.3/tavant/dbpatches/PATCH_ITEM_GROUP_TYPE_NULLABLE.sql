--Purpose    : Change made to remove item group type not null constraint, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 11-Oct-2010

ALTER TABLE ITEM_GROUP MODIFY (ITEM_GROUP_TYPE NULL)
/
UPDATE ITEM_GROUP 
SET ITEM_GROUP_TYPE = NULL 
where id in (
SELECT IG.id FROM ITEM_GROUP IG, ITEM_SCHEME ISC
WHERE IG.SCHEME = ISC.ID
AND ISC.NAME <> 'Prod Struct Scheme')
/
commit
/