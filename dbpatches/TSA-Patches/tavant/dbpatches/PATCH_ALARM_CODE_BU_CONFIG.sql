--Purpose    : BU Config for displaying alarm codes section visible.
--Created On : 24-Mar-2010
--Created By : Naveen Kumar Jadav


INSERT INTO CONFIG_PARAM values (config_param_seq.nextval,'Is alarm codes section visible','Is alarm codes section visible','isAlarmCodesSectionVisible','boolean',sysdate,'TSA-Migration | Added to boothstrap from DBPatch PATCH_ALARM_CODE_BU_CONFIG.sql',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,null,null,1,null,1,1)
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'), (select id from CONFIG_PARAM_OPTION where value='true'))
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'), (select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'),sysdate,'TSA-Migration | Added to boothstrap from DBPatch PATCH_ALARM_CODE_BU_CONFIG.sql',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
Commit
/