--PURPOSE    : NMHGSLMS-175
--AUTHOR     : LAVIN HAWES
--CREATED ON : 15-may-13

insert into config_param values (CONFIG_PARAM_SEQ.nextVal,'Show WPRA Number in Shipment/Location view','Show WPRA Number in Shipment/Location view','showWPRANumberInLocationView','boolean','',sysdate,null,null,null,null,1,'radio','CLAIMS',1,'CLAIM_RETURN_PART',1,1)
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name = 'showWPRANumberInLocationView'),sysdate,'',sysdate,'',null,null,1,'EMEA',(select id from config_param_option where display_value = 'Yes'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name = 'showWPRANumberInLocationView'),sysdate,'',sysdate,'',null,null,1,'AMER',(select id from config_param_option where display_value = 'No'))
/
insert into config_param_options_mapping values(CONFIG_PARAM_OPTION_SEQ.nextVal,(select id from config_param where name = 'showWPRANumberInLocationView'),(select id from config_param_option where display_value = 'Yes'))
/
insert into config_param_options_mapping values(CONFIG_PARAM_OPTION_SEQ.nextVal,(select id from config_param where name = 'showWPRANumberInLocationView'),(select id from config_param_option where display_value = 'No'))
/
commit
/