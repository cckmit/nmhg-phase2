-- Patch for updating diplay_value 'Dealer Sales Person' to 'Dealer Representative'  in config_param_option 
-- Author: PRACHER 
-- Created On : 15-MARCH-2013


UPDATE CONFIG_PARAM_OPTION SET DISPLAY_VALUE='Dealer Representative' WHERE VALUE='salesPerson'
/
commit
/
