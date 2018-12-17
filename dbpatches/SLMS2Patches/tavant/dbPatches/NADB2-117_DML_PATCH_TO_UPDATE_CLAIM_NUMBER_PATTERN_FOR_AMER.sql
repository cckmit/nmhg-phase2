-- PURPOSE    : PATCH TO Update Claim Number Pattern for AMER
-- AUTHOR     : ParthaSarathy R
-- CREATED ON : 27-Mar-2013

update claim_number_pattern set sequence_name = 'AMER_CLAIM_NUMBER_SEQ' where business_unit_info = 'AMER'
/
commit
/