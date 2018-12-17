--Purpose    : BU Config to say if part coverage needs to be considered for parts claim
--Created On : 26-Feb-2010
--Created By : Rahul Katariya
--Impact     : Parts Claims


INSERT INTO CONFIG_PARAM values (config_param_seq.nextval,'Consider warranty coverage on parts claim','Should part warranty be considered on parts claim','considerWarrantyCoverageForPartsClaim','boolean',sysdate,'TSA-Migration',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'radio',null,1,null,1,1)
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'), (select id from CONFIG_PARAM_OPTION where value='true'))
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'), (select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'),sysdate,'TSA-Migration',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from CONFIG_PARAM_OPTION where value='true'))
/
Commit
/