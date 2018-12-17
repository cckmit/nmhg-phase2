--patch for insertion of business unit configuration to decide whether to show labor type or not to show.
INSERT INTO CONFIG_PARAM (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, PARAM_DISPLAY_TYPE) VALUES ((SELECT MAX(ID)+1 FROM CONFIG_PARAM), 'This configuration decides whether Labor Type Split should be always inclusive, always exclusive or optional for dealer; as far as distribution is concerned for labor hours', 'Over Time Labor Split Distribution','laborSplitDistribution','java.lang.String','select')
/
INSERT INTO  CONFIG_PARAM_OPTION (ID, VALUE, DISPLAY_VALUE)  VALUES ((SELECT MAX(ID)+1 FROM CONFIG_PARAM_OPTION),'ALWAYSINCLUSIVE','Always Inclusive')
/
INSERT INTO  CONFIG_PARAM_OPTION  (ID, VALUE, DISPLAY_VALUE) VALUES ((SELECT MAX(ID)+1 FROM CONFIG_PARAM_OPTION),'ALWAYSEXCLUSIVE','Always Exclusive')
/
INSERT INTO  CONFIG_PARAM_OPTION  (ID, VALUE, DISPLAY_VALUE) VALUES ((SELECT MAX(ID)+1 FROM CONFIG_PARAM_OPTION),'OPTIONAL','Optional')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID)  VALUES ((select max(id)+1 from config_param_options_mapping),(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'laborSplitDistribution'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'ALWAYSINCLUSIVE'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  (ID, PARAM_ID, OPTION_ID) VALUES ((select max(id)+1 from config_param_options_mapping),(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'laborSplitDistribution'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'ALWAYSEXCLUSIVE'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  (ID, PARAM_ID, OPTION_ID) VALUES ((select max(id)+1 from config_param_options_mapping),(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'laborSplitDistribution'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'OPTIONAL'))
/