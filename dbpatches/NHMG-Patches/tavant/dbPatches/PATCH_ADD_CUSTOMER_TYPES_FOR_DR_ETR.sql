-- new customer types
INSERT INTO config_param_option (id, DISPLAY_VALUE,VALUE) VALUES(config_param_option_seq.nextval,'Federal Government' ,'Federal Government' )
/
INSERT INTO config_param_option (id, DISPLAY_VALUE,VALUE) VALUES(config_param_option_seq.nextval,'State Government' ,'State Government' )
/
INSERT INTO config_param_option (id, DISPLAY_VALUE,VALUE) VALUES(config_param_option_seq.nextval,'County Government' ,'County Government' )
/
INSERT INTO config_param_option (id, DISPLAY_VALUE,VALUE) VALUES(config_param_option_seq.nextval,'City/Town/Village Government' ,'City/Town/Village Government' )
/
INSERT INTO config_param_option (id, DISPLAY_VALUE,VALUE) VALUES(config_param_option_seq.nextval,'Homeowners' ,'Homeowners' )
/
INSERT INTO config_param_option (id, DISPLAY_VALUE,VALUE) VALUES(config_param_option_seq.nextval,'Business' ,'Business' )
/
INSERT INTO config_param_option (id, DISPLAY_VALUE,VALUE) VALUES(config_param_option_seq.nextval,'Regional Account' ,'Regional Account' )
/
commit
/


-- mapping customer types with config params for Delivery Report
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='customersFilingDR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Federal Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='customersFilingDR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='State Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='customersFilingDR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='County Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='customersFilingDR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='City/Town/Village Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='customersFilingDR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Homeowners'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='customersFilingDR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Business'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='customersFilingDR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Regional Account'))
/
commit
/

-- mapping customer types with config params for ETR
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME = 'customersFilingETR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Federal Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME = 'customersFilingETR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='State Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME = 'customersFilingETR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='County Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME = 'customersFilingETR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='City/Town/Village Government'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME = 'customersFilingETR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Homeowners'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME = 'customersFilingETR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Business'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME = 'customersFilingETR'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Regional Account'))
/
commit
/