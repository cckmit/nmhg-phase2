-- Patch for allowing  Session Time Out
-- 22th November 2008
-- Author: Rakesh R
insert into config_param 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE,PARAM_DISPLAY_TYPE)
values (config_param_seq.nextval,'Allow Session Time Out','Allow Session Time Out','isSessionTimeOutAllowed','boolean','radio')
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isSessionTimeOutAllowed'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes')) 
/  
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isSessionTimeOutAllowed'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No')) 
/ 
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isSessionTimeOutAllowed'),'Club Car',
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'))
/
COMMIT
/