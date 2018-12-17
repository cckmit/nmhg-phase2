--Purpose    : BU Config for Currency to be used for ERP Interactions, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None


INSERT INTO CONFIG_PARAM values (config_param_seq.nextval,'Currency to be used for ERP interactions','Currency to be used for ERP interactions','erpCurrency','java.lang.String',sysdate,'4.3 Upgrade',sysdate,56,SYSDATE,SYSDATE,1,'select',null,1,null,1,1)
/
Insert Into Config_Param_Option Values (Config_Param_Option_seq.nextval,'Dealers Currency','dealersCurrency')
/
Insert Into Config_Param_Option Values (Config_Param_Option_seq.nextval,'USD','usd')
/
Insert Into Config_Param_Option Values (Config_Param_Option_seq.nextval,'EUR','eur')
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='erpCurrency'), (select id from CONFIG_PARAM_OPTION where value='dealersCurrency'))
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='erpCurrency'), (select id from CONFIG_PARAM_OPTION where value='usd'))
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='erpCurrency'), (select id from CONFIG_PARAM_OPTION where value='eur'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='erpCurrency'),SYSDATE,'4.3 Upgrade',SYSDATE,56,SYSDATE,SYSDATE,1,'Transport Solutions ESA',(select id from CONFIG_PARAM_OPTION where value='dealersCurrency'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='erpCurrency'),SYSDATE,'4.3 Upgrade',SYSDATE,56,SYSDATE,SYSDATE,1,'Hussmann',(select id from CONFIG_PARAM_OPTION where value='dealersCurrency'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='erpCurrency'),SYSDATE,'4.3 Upgrade',SYSDATE,56,SYSDATE,SYSDATE,1,'AIR',(select id from CONFIG_PARAM_OPTION where value='dealersCurrency'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='erpCurrency'),SYSDATE,'4.3 Upgrade',SYSDATE,56,SYSDATE,SYSDATE,1,'Clubcar ESA',(select id from CONFIG_PARAM_OPTION where value='dealersCurrency'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='erpCurrency'),SYSDATE,'4.3 Upgrade',SYSDATE,56,SYSDATE,SYSDATE,1,'TFM',(select id from CONFIG_PARAM_OPTION where value='dealersCurrency'))
/
Commit
/