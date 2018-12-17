-- Patch for updating value 'Machine' to 'Unit' in config_param_option
-- Author: PARTHASARATHY R
-- Created On : 22-OCT-2012

UPDATE CONFIG_PARAM_OPTION SET VALUE='Unit' WHERE VALUE='Machine'
/
COMMIT
/