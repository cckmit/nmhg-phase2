-- Patch for showing miscellaneous items secion
-- 17th December 2008
-- Author: Prashanth Konda
insert into config_param 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE,PARAM_DISPLAY_TYPE)
values (config_param_seq.nextval,'Display miscellaneous items section','Display miscellaneous items section','isMiscPartsSectionVisible','boolean','radio')
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isMiscPartsSectionVisible'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes')) 
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='isMiscPartsSectionVisible'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No')) 
/
COMMIT
/
 