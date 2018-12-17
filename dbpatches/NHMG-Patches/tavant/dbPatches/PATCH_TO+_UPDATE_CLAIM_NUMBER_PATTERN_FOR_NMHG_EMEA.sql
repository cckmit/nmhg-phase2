--Purpose    : PATCH_TO _UPDATE_CLAIM_NUMBER_PATTERN_FOR_NMHG_EMEA
--Author     : P Raghavendra Raju	
--Created On : 20-Mar-2013

update claim_number_pattern set template='T-YY-NNN' where business_unit_info='EMEA'
/
commit
/