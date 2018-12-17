--Purpose    : BU Config to say if supplier is needed for cost fetch or not, changes made as a part of 4.3 upgrade 
--Created On : 09-Jan-2010
--Created By : Rahul Katariya
--Impact     : Cost Price Fetch


INSERT INTO CONFIG_PARAM 
values (config_param_seq.nextval,'Supplier required for cost fetch','Is supplier required by ERP for cost fetch','isSupplierRequiredForCostFetch','boolean',sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'radio',null,1,null,1,1)
/
Insert Into Config_Param_Options_Mapping 
Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='isSupplierRequiredForCostFetch'), 
(select id from CONFIG_PARAM_OPTION where value='true'))
/
Insert Into Config_Param_Options_Mapping 
Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, 
(select id from CONFIG_PARAM where NAME='isSupplierRequiredForCostFetch'), (select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM 
where NAME='isSupplierRequiredForCostFetch'),sysdate,'4.3 Upgrade',
sysdate,56,sysdate,sysdate,1,'Transport Solutions ESA',(select id from CONFIG_PARAM_OPTION where value='true'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM 
where NAME='isSupplierRequiredForCostFetch'),sysdate,'4.3 Upgrade',
sysdate,56,sysdate,sysdate,1,'Hussmann',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM 
where NAME='isSupplierRequiredForCostFetch'),sysdate,'4.3 Upgrade',
sysdate,56,sysdate,sysdate,1,'AIR',(select id from CONFIG_PARAM_OPTION where value='true'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM 
where NAME='isSupplierRequiredForCostFetch'),sysdate,'4.3 Upgrade',
sysdate,56,sysdate,sysdate,1,'TFM',(select id from CONFIG_PARAM_OPTION where value='true'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM 
where NAME='isSupplierRequiredForCostFetch'),sysdate,'4.3 Upgrade',
sysdate,56,sysdate,sysdate,1,'Clubcar ESA',(select id from CONFIG_PARAM_OPTION where value='true'))
/
Commit
/