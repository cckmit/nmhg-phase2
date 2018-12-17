--Purpose    : Patch for config param for Warranty Coverage for Part Claim, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Consider Warranty Coverage for Part Claim', 'Consider Warranty Coverage for Part Claim','considerWarrantyCoverageForPartsClaim','boolean',SYSDATE, '4.3 Upgrade', SYSDATE, 56, sysdate, sysdate, 'CLAIMS', '', 1, 'CLAIM_PROCESS', 1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='considerWarrantyCoverageForPartsClaim'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='considerWarrantyCoverageForPartsClaim'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'),sysdate,'4.3 Upgrade',sysdate,56,SYSDATE,SYSDATE,1,'Transport Solutions ESA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'),sysdate,'4.3 Upgrade',sysdate,56,SYSDATE,SYSDATE,1,'Hussmann',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'),sysdate,'4.3 Upgrade',sysdate,56,SYSDATE,SYSDATE,1,'AIR',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'),sysdate,'4.3 Upgrade',sysdate,56,SYSDATE,SYSDATE,1,'TFM',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='considerWarrantyCoverageForPartsClaim'),sysdate,'4.3 Upgrade',sysdate,56,SYSDATE,SYSDATE,1,'Clubcar ESA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
COMMIT
/