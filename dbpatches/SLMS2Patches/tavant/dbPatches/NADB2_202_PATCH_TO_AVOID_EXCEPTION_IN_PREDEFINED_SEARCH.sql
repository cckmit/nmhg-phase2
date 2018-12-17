-- PURPOSE    : Patch to avoid exception while doing predefined search
-- AUTHOR     : Sumesh Kumar.R
-- CREATED ON : 26-AUGUST-2014
update inbox_view set field_names=REPLACE(field_names, 'state', 'activeClaimAudit.state') where field_names like '%enum:ClaimState:state%' and type = 'ClaimSearches'
/
update inbox_view set field_names=REPLACE(field_names, 'claim.state', 'claim.activeClaimAudit.state') where field_names like '%enum:ClaimState:claim.state%' and type = 'ClaimSearches'
/
commit
/