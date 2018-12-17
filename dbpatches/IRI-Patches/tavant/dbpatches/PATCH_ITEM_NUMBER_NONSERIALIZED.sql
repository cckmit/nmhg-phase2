--Purpose: Creating Config Param for Item Number Display for Non serialized Claims
--Author: Gyanendra Biswanath Mishra
--Created On: Date 18 March 2009

insert into config_param 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE,PARAM_DISPLAY_TYPE,D_ACTIVE)
values (config_param_seq.nextval,'isItemNumberDisplayRequired','Display Item number for non serialized claims','isItemNumberDisplayRequired','boolean','radio',1)
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isItemNumberDisplayRequired'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'))
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isItemNumberDisplayRequired'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No')) 
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION,D_ACTIVE)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isItemNumberDisplayRequired'),'TK',
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'),1)
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION,D_ACTIVE)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isItemNumberDisplayRequired'),'Club Car',(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No'),1)
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION,D_ACTIVE)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isItemNumberDisplayRequired'),'Transport Solutions ESA',(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No'),1)
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION,D_ACTIVE)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isItemNumberDisplayRequired'),'AIR',(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'),1)
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION,D_ACTIVE)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isItemNumberDisplayRequired'),'TFM',(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No'),1)
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION,D_ACTIVE)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isItemNumberDisplayRequired'),'Hussmann',(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No'),1)
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION,D_ACTIVE)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='isItemNumberDisplayRequired'),'Clubcar ESA',(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No'),1)
/
COMMIT
/