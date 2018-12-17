--Purpose    : BU Config for displaying alarm codes section visible.changes made as a part of 4.3 upgrade
--Created On : 24-Mar-2010
--Created By : Naveen Kumar Jadav


INSERT INTO CONFIG_PARAM 
values (config_param_seq.nextval,'Is alarm codes section visible','Is alarm codes section visible','isAlarmCodesSectionVisible','boolean',
sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'radio','CLAIMS',1,'CLAIM_INPUT_PARAMETERS',1,1)
/
Insert Into Config_Param_Options_Mapping 
Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'), (
select id from CONFIG_PARAM_OPTION where value='true'))
/
Insert Into Config_Param_Options_Mapping Values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, 
(select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'), (select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'),
sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'Transport Solutions ESA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'),
sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'Hussmann',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'),
sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'AIR',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'),
sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'TFM',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='isAlarmCodesSectionVisible'),
sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'Clubcar ESA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
Commit
/