--Purpose    : Patch for BuConfig Additional Information Details
--Author     : saya.sudha	
--Created On : 20-jan-2009
INSERT INTO config_param(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME,PARAM_DISPLAY_TYPE,LOGICAL_GROUP,LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER)
VALUES(config_param_seq.nextval, 'Additional information applicable','is Additional information details applicable','additionalInformationDetailsApplicable','boolean', sysdate,NULL,sysdate,NULL,NULL,sysdate,'radio',NULL,NULL,NULL,NULL,NULL)
/
INSERT INTO config_value(ID,ACTIVE,VALUE,CONFIG_PARAM,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION)
VALUES(config_value_seq.nextval,1,NULL,(select id from config_param where name='additionalInformationDetailsApplicable'),sysdate,NULL,sysdate,NULL,NULL, NULL,'Club Car',(select id from config_param_option where value='false'))
/
insert into config_param_options_mapping values((select max(id) from config_param_options_mapping) +20,
(select id from config_param where name='additionalInformationDetailsApplicable'),
(select id from config_param_option where value='false'))
/
insert into config_param_options_mapping values((select max(id) from config_param_options_mapping) +20,
(select id from config_param where name='additionalInformationDetailsApplicable'),
(select id from config_param_option where value='true'))
