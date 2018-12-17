--patch for insertion of business unit configuration to decide whether to show Factory Order Number or not to show.
INSERT INTO CONFIG_PARAM (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, PARAM_DISPLAY_TYPE) VALUES ((SELECT MAX(ID)+1 FROM CONFIG_PARAM), 'This configuration decides whether Factory Order Number should be enabled and shown for current business or not', 'Enable Factory Order Number','enableFactoryOrderNumber','boolean','radio')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID)  VALUES ((select max(id)+1 from config_param_options_mapping),(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableFactoryOrderNumber'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  (ID, PARAM_ID, OPTION_ID) VALUES ((select max(id)+1 from config_param_options_mapping),(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableFactoryOrderNumber'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/