-- PURPOSE    : Patch to update labels in PDI report
-- AUTHOR     : Sumesh kumar.R
-- CREATED ON : 10-SEP-2014

update delivery_check_list set delivery_check_list='OPERATOR RESTRAINT SYSTEM' where delivery_check_list='OPERATOR RESTRIANT SYSTEM'
/
update delivery_check_list set delivery_check_list='OPERATION AND MAINTENANCE OF ATTACHMENT' where delivery_check_list='OPERATION AND MAINTENANCE OF ATTACHEMENT'
/