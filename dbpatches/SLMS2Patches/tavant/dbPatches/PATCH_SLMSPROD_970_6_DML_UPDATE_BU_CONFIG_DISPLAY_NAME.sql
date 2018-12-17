--  PURPOSE		: To rename the display name, description and name of a BU config 'DisplayNcrAndBt30DayNcrOnClaimPage'
--  REFERENCE   : SLMSPOD-970 
--	AUTHOR		: Ravi K Sinha

UPDATE config_param 
SET description = 'Display NCR and 30Day NCR(checkboxes) on Claim page 1',
display_name='Display NCR and 30Day NCR(checkboxes) on Claim page 1',
name='DisplayNcrAnd30DayNcrOnClaimPage'
WHERE name='DisplayNcrAndBt30DayNcrOnClaimPage';
/

COMMIT
/