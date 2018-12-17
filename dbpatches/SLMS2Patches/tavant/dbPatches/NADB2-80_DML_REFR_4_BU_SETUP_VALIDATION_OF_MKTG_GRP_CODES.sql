--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-427 : BU Configuration
--Author     : Arpitha Nadig AR
--Created On : 26-FEB-2014 
insert into config_param (id,description,display_name,name,type,d_created_on,d_internal_comments,d_updated_on,d_last_updated_by,d_created_time,d_updated_time,d_active,param_display_type,logical_group,logical_group_order,sections,sections_order,param_order) 
values(CONFIG_PARAM_SEQ.nextval,'Validate Marketing Group Codes For Different Warranty Types','Validate Marketing Group Codes For Different Warranty Types',
'flagToValidateMarketingGroupCodes','boolean',sysdate,null,sysdate,56,current_timestamp,current_timestamp,1,'radio','CLAIMS','1','CLAIM_INPUT_PARAMETERS','1','1')
/
insert into config_param_options_mapping (id,param_id,option_id) values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,(select id from config_param where name='flagToValidateMarketingGroupCodes'),11)
/
insert into config_param_options_mapping (id,param_id,option_id) values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,(select id from config_param where name='flagToValidateMarketingGroupCodes'),12)
/
insert into config_value (id,active,value,config_param,d_created_on,d_internal_comments,d_updated_on,d_last_updated_by,d_created_time,d_updated_time,d_active,business_unit_info,config_param_option) 
values(CONFIG_VALUE_SEQ.nextval,1,null,(select id from config_param where name='flagToValidateMarketingGroupCodes'),null,null,null,56,null,null,1,'AMER',11)
/
insert into config_param (id,description,display_name,name,type,d_created_on,d_internal_comments,d_updated_on,d_last_updated_by,d_created_time,d_updated_time,d_active,param_display_type,logical_group,logical_group_order,sections,sections_order,param_order) values(CONFIG_PARAM_SEQ.nextval,'Please provide comma separated dealer marketing group codes','Allowed Dealer Marketing Group Codes For Filing Non-serialized Claims','allowedDealerMktgGroupCodes','java.lang.String',sysdate,null,sysdate,56,current_timestamp,current_timestamp,1,'textbox','CLAIMS','1','CLAIM_INPUT_PARAMETERS','1','1')
/
insert into config_value (id,active,value,config_param,d_created_on,d_internal_comments,d_updated_on,d_last_updated_by,d_created_time,d_updated_time,d_active,business_unit_info,config_param_option) 
values(CONFIG_VALUE_SEQ.nextval,1,'011,074,016,019',(select id from config_param where name='allowedDealerMktgGroupCodes'),null,null,null,56,null,null,1,'AMER',null)
/
commit
/