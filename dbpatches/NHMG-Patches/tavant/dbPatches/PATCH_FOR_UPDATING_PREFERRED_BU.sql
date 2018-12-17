-- Patch to update preferred business unit
-- Author: PARTHASARATHY R
-- Created On : 20-NOV-2012

update org_user set preferred_bu = 'NMHG US' where preferred_bu = 'ITS'
/
update org_user set preferred_bu = 'NMHG EMEA' where preferred_bu = 'Thermo King TSA'
/
COMMIT
/