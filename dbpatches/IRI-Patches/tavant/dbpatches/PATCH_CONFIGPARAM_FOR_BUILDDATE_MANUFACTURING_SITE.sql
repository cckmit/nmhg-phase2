-- Patch for allowing Dealer to Search Based on Build Date and Manufacturing Site
-- 15th November 2008
-- Author: Rakesh R
insert into config_param 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE,PARAM_DISPLAY_TYPE)
values (config_param_seq.nextval,'Allow Dealer to Search Based On Build Date','Allow dealer to search based on Build Date','isBuildDateVisible','boolean','radio')
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isBuildDateVisible'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes')) 
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isBuildDateVisible'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No')) 
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isBuildDateVisible'),'Club Car',
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'))
/
insert into config_param 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE,PARAM_DISPLAY_TYPE)
values (config_param_seq.nextval,'Allow Dealer to Search Based On Manufacturing Site','Allow dealer to search based on Manufacturing Site','isManufacturingSiteVisible','boolean','radio')
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isManufacturingSiteVisible'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes')) 
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isManufacturingSiteVisible'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No')) 
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isManufacturingSiteVisible'),'Club Car',
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'))
/
COMMIT
/