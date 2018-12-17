--patch for insertion of business unit configuration to decide whether to show labor type or not to show.
INSERT INTO CONFIG_PARAM (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, PARAM_DISPLAY_TYPE) VALUES ((SELECT MAX(ID)+1 FROM CONFIG_PARAM), 'This configuration decides whether Labor Type Split should be enabled and shown for current business or not', 'Enable Over Time Labor Split','enableLaborSplit','boolean','radio')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID)  VALUES ((select max(id)+1 from config_param_options_mapping),(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableLaborSplit'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  (ID, PARAM_ID, OPTION_ID) VALUES ((select max(id)+1 from config_param_options_mapping),(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableLaborSplit'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/