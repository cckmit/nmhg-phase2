-- PURPOSE    : PATCH_TO_CREATE_SERVICE_PROCEDURE_DEFINITION_AND_CLAIM_AUDIT
-- AUTHOR     : Sumesh Kumar.R
-- CREATED ON : 25-AUGUST-2014
create index claim_audit_assign_to_idx on claim_audit(assign_to_user)
/
CREATE INDEX SERVICEPROCDEF_ACTIONCODE_IX ON Service_Procedure_Definition(Code)
/
commit
/