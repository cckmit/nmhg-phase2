-- Patch for updating value 'Machine' to 'Unit' in config param
-- Author: PARTHASARATHY R
-- Created On : 19-OCT-2012

UPDATE CONFIG_PARAM_OPTION SET DISPLAY_VALUE='Unit' WHERE VALUE='Machine'
/
UPDATE CONFIG_PARAM SET DISPLAY_NAME='Can external user perform Retail Unit Transfer' WHERE DESCRIPTION='Can external user perform Retail Machine Transfer'
/
COMMIT
/