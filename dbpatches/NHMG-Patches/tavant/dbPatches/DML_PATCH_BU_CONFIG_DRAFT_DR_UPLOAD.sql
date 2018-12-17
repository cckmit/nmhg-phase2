--PURPOSE    : WR/DR UPLOAD
--AUTHOR     : LAVIN HAWES
--CREATED ON : 23-may-13

insert into config_param values (CONFIG_PARAM_SEQ.nextVal,'Save DR as Draft on DR upload','Save DR as Draft on DR upload','saveDRAsDraftOnUpload','boolean','',sysdate,null,null,null,null,1,'radio','INVENTORY',1,'INVENTORY_DR',1,1)
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name = 'saveDRAsDraftOnUpload'),sysdate,'',sysdate,'',null,null,1,'EMEA',(select id from config_param_option where display_value = 'Yes'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name = 'saveDRAsDraftOnUpload'),sysdate,'',sysdate,'',null,null,1,'AMER',(select id from config_param_option where display_value = 'No'))
/
insert into config_param_options_mapping values(CONFIG_PARAM_OPTION_SEQ.nextVal,(select id from config_param where name = 'saveDRAsDraftOnUpload'),(select id from config_param_option where display_value = 'Yes'))
/
insert into config_param_options_mapping values(CONFIG_PARAM_OPTION_SEQ.nextVal,(select id from config_param where name = 'saveDRAsDraftOnUpload'),(select id from config_param_option where display_value = 'No'))
/
commit
/
