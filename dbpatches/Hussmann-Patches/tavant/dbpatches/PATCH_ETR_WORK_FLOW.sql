--Purpose    : Patch for ETR Work Flow
--Author     : saya.sudha	
--Created On : 22-dec-2008
insert into config_param values(config_param_seq.nextval, 'manualApprovalFlowForETR',
'manualApprovalFlowForETR','manualApprovalFlowForETR','boolean',NULL,NULL,NULL,NULL,NULL,NULL,NULL,
'radio',NULL,NULL,NULL,NULL)
/
insert into config_value values(config_value_seq.nextval,1,NULL,(select id from config_param where 
name='manualApprovalFlowForETR'),sysdate,NULL,sysdate,NULL,sysdate,sysdate,'Club Car',
(select id from config_param_option where value='true'))
/
insert into config_param_options_mapping values((select max(id) from config_param_options_mapping) +20,
(select id from config_param where name='manualApprovalFlowForETR'),(select id from config_param_option 
where value='false'))
/
insert into config_param_options_mapping values((select max(id) from config_param_options_mapping) +20,
(select id from config_param where name='manualApprovalFlowForETR'),(select id from config_param_option 
where value='true'))
/


