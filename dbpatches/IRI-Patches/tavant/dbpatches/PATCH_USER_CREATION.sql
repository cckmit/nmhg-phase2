insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'Dealer Administration','dealerAdministrator')
/
insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'Dealer Sales Administration','dealerSalesAdministration')
/
insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'Dealer Warranty Administration','dealerWarrantyAdmin')
/
insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'Dealer Sales Person','salesPerson')
/
insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'Dealer Technician','technician')
/
insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'DCAP Dealer','dcapDealer')
/
insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'Third Party Privilege','thirdPartyPrivilege')
/
insert into config_param_option values (CONFIG_PARAM_OPTION_SEQ.nextVal,'View FOC Claims','viewFOCClaims')
/
insert into config_param values (CONFIG_PARAM_SEQ.nextVal,'Roles To Be Displayed','Roles To Be Displayed','rolesToBeDisplayed','java.lang.String',sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),1,'multiselect',null,null,null,null,null)
/
insert into config_param_options_mapping values ((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='dealerAdministrator'))
/
insert into config_param_options_mapping values((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='dealerSalesAdministration'))
/
insert into config_param_options_mapping values((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='dealerWarrantyAdmin'))
/
insert into config_param_options_mapping values((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='salesPerson'))
/
insert into config_param_options_mapping values((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='technician'))
/
insert into config_param_options_mapping values((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='dcapDealer'))
/
insert into config_param_options_mapping values((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='thirdPartyPrivilege'))
/
insert into config_param_options_mapping values((select max(id)+20 from config_param_options_mapping),(select id from config_param where name='rolesToBeDisplayed'),(select id from config_param_option 
where value='viewFOCClaims'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from config_param_option where value='dealerAdministrator'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from config_param_option where value='dealerSalesAdministration'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from config_param_option where value='dealerWarrantyAdmin'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from config_param_option where value='salesPerson'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from config_param_option where value='technician'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from config_param_option where value='dcapDealer'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),0,'Thermo King TSA',(select id from config_param_option where value='thirdPartyPrivilege'))
/
insert into config_value values (CONFIG_VALUE_SEQ.nextVal,1,null,(select id from config_param where name='rolesToBeDisplayed'),sysdate,null,sysdate,null,CAST( sysdate AS TIMESTAMP),
CAST( sysdate AS TIMESTAMP),0,'Thermo King TSA',(select id from config_param_option where value='viewFOCClaims'))
/
COMMIT
/