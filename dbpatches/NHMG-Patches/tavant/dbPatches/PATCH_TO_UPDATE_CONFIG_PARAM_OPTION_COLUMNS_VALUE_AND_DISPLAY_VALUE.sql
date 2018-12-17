-- Patch for updating value 'Machine' to 'Unit' in config_param_option
-- Author: PARTHASARATHY R
-- Created On : 23-OCT-2012

UPDATE CONFIG_PARAM_OPTION SET VALUE='Machine' WHERE VALUE='Unit'
/
UPDATE CONFIG_PARAM_OPTION SET DISPLAY_VALUE='Machine' WHERE DISPLAY_VALUE='Unit'
/
COMMIT
/