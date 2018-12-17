--Purpose    : Patch for adding 'Central Logistic Location'  Configuration parameter
--Author     : Sumesh kumar.R
--Created On : 04-Mar-2014
insert into config_param(id,description,display_name,name,type,d_created_on,d_internal_comments,d_active,param_display_type,logical_group,logical_group_order,sections_order,param_order) values(110000000019280,'Central Logistic Location','Central Logistic Location','centralLogisticLocation','java.lang.String','05-03-14','Nacco Configuration','1','textbox','SUPPLIER_RECOVERY','1','1','1')
/
insert into config_value(id,active,value,config_param,d_active,business_unit_info) values('110000000111322','1','Logistic Location','110000000019280','1','AMER')
/
insert into config_value(id,active,value,config_param,d_active,business_unit_info) values('110000000111342','1','Logistic Location','110000000019280','1','EMEA')
/
commit
/