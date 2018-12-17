-- PATCH_FOR_UPDATING_DATECODE_TO_COMPONENT_DATECODE
-- Author:  pallavi
-- Created On : 18-04-2013

 
update config_param set description='This configuration species whether Component Date Code applicable or not',display_name='Is Component Date Code applicable for Claim submission' where description='This configuration species whether Date Code applicable or not'
/
COMMIT
/