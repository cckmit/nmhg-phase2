-- Patch for updating TYPE Authorization number
-- Author: PALLAVI 
-- Created On : 15-APRIL-2013


update config_param set description='Show Authorization Number',display_name='Show Authorization Number' where description='Show Authorisation Number'
/
update config_param set description='Show Authorization Received',display_name='Show Authorization Received' where description='Show Authorisation Received'
/
commit
/