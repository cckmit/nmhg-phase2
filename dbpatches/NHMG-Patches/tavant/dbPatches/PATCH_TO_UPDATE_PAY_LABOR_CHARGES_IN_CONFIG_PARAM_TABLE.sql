-- Patch for updating diplay_name 'Pay Labor Charges' to 'Pay Labor Charges for SMR Machine and part claim' in config_param
-- Author: PRACHER 
-- Created On : 10-NOV-2012


UPDATE CONFIG_PARAM SET DISPLAY_NAME='Pay Labor Charges for SMR Machine and part claim' WHERE DISPLAY_NAME='Pay Labor Charges'
/
COMMIT
/