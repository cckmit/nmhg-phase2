--ALTER TABLE LIMIT_OF_AUTHORITY_LEVEL MODIFY (LOA_SCHEME NUMBER(19,0))
--/
--ALTER TABLE LIMIT_OF_AUTHORITY_LEVEL MODIFY (LOA_USER NUMBER(19,0))
--/
--Kuldeep - Moved above part to PATCH_LOA_SCHEME.sql under table creation script, so this patch is not required

--ALTER TABLE LOA_AMOUNT MODIFY (LOA_LEVEL NUMBER(19,0))
--/
--Kuldeep - Moved above part to PATCH_CREATE_LOA_AMOUNT.sql under table creation script, so this patch is not required
COMMIT
/