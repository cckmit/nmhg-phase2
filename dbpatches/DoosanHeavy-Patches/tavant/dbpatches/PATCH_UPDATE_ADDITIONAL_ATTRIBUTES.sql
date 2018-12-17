--PURPOSE    : PATCH TO UPDATE attribute_purpose
--AUTHOR     : Sushma
--CREATED ON : 26-June -2012

update additional_attributes set attribute_purpose = 'JOB_CODE_PURPOSE' where attribute_purpose = 'CLAIMED_INVENTORY_PUPOSE'
/
COMMIT
/