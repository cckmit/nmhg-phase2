--Purpose    : Added scripts for dropping dealer family code from dealership as a part of 4.3 upgrade.
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

UPDATE SERVICE_PROVIDER SP SET DEALER_FAMILY_CODE = (SELECT DEALER_FAMILY_CODE FROM DEALERSHIP DS WHERE DS.ID = SP.ID)
/
ALTER TABLE DEALERSHIP DROP COLUMN DEALER_FAMILY_CODE
/
COMMIT
/