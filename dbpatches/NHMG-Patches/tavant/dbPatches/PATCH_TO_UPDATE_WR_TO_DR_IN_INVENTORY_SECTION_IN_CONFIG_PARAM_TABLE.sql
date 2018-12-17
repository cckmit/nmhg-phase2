-- Patch for updating diplay_name 'WR' to 'DR' and 'Warranty Registration' to 'Delivery Report' in config_param 
-- Author: PRACHER 
-- Created On : 12-DEC-2012


UPDATE CONFIG_PARAM SET DISPLAY_NAME='Perform Dealer to Dealer Transaction on Delivery Report',DESCRIPTION='Perform Dealer to Dealer Transaction on Delivery Report' WHERE NAME='performD2DOnWR'
/

UPDATE CONFIG_PARAM SET DISPLAY_NAME='Capture Installing Dealer/Installation Date on DR/ETR',DESCRIPTION='Capture Installing Dealer/Installation Date on DR/ETR' WHERE NAME='enableDealerAndInstallationDate'
/

COMMIT
/

