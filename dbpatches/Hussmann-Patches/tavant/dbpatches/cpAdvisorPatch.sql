 INSERT INTO USER_SCHEME (ID, NAME, VERSION, BUSINESS_UNIT_INFO)
 VALUES (USER_SCHEME_SEQ.NEXTVAL, 'CP Advisor  Assignment', 0, 'Hussmann')
/
INSERT INTO user_cluster (id,description,version,name,scheme,business_unit_info)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'CP Advisor Review','0','CP Advisor Review',(SELECT ID FROM user_scheme WHERE NAME='CP Advisor  Assignment'),'Hussmann')
/
INSERT INTO role (id,name,version,d_internal_comments)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'cpAdvisor','0','Role created for Commercial Policy advisor')
/
INSERT INTO purpose (id,name,version,business_unit_info)
VALUES (PURPOSE_SEQ.NEXTVAL,'CP Advisor Assignment','0','Hussmann')
/

--patch for insertion of business unit configuration to decide whether to show CP Advisor functionality or not
INSERT INTO CONFIG_PARAM (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, PARAM_DISPLAY_TYPE) VALUES (config_param_seq.nextval, 'This configuration decides whether CP Advisor should be enabled and shown for current business or not', 'Enable CP Advisor','enableCPAdvisor','boolean','radio')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID)  VALUES (CONFIG_PARAM_OPTION_SEQ.nextval,(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableCPAdvisor'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  (ID, PARAM_ID, OPTION_ID) VALUES (CONFIG_PARAM_OPTION_SEQ.nextval,(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableCPAdvisor'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/



