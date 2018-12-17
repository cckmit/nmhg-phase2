--Purpose    : BU Config to enable a dealer to fill out the report on an inventory which is held by another dealer.
--Created On : 28-April-2010
--Created By : Nishad.T

INSERT INTO CONFIG_PARAM values (config_param_seq.nextval,'Enable Custom Report Filing on inventory by any dealer','Enable Report Filing by Any Dealer',
'enableReportFilingAnyDealer','boolean',sysdate,null,
sysdate,null,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,null,null,1,null,1,1)
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
 (select id from CONFIG_PARAM where NAME='enableReportFilingAnyDealer'),
 (select id from CONFIG_PARAM_OPTION where value='false'))
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
 (select id from CONFIG_PARAM where NAME='enableReportFilingAnyDealer'),
 (select id from CONFIG_PARAM_OPTION where value='true'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enableReportFilingAnyDealer'),sysdate,
null,sysdate,null,CAST(sysdate AS TIMESTAMP),
CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from CONFIG_PARAM_OPTION where value='true'))
/
Commit
/