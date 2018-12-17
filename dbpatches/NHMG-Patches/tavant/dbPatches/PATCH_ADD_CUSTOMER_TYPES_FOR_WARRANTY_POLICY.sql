-- mapping customer types with config params to show in policy configuration
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Federal Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='State Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='County Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='City/Town/Village Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Homeowners'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Business'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Regional Account'))
/
commit
/