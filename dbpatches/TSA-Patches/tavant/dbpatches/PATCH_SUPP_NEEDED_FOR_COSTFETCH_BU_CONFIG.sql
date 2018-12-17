--Purpose    : BU Config to say if supplier is needed for cost fetch or not
--Created On : 09-Jan-2010
--Created By : Rahul Katariya
--Impact     : Cost Price Fetch


INSERT INTO CONFIG_PARAM values (config_param_seq.nextval,'Supplier required for cost fetch','Is supplier required by ERP for cost fetch','isSupplierRequiredForCostFetch','boolean',sysdate,'TSA-Migration | Added to boothstrap from DBPatch PATCH_SUPP_NEEDED_FOR_COSTFETCH_BU_CONFIG.sql',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'radio',null,1,null,1,1)
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='isSupplierRequiredForCostFetch'), (select id from CONFIG_PARAM_OPTION where value='true'))
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='isSupplierRequiredForCostFetch'), (select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='isSupplierRequiredForCostFetch'),sysdate,'TSA-Migration | Added to boothstrap from DBPatch PATCH_SUPP_NEEDED_FOR_COSTFETCH_BU_CONFIG.sql.sql',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
Commit
/