--Purpose    : Patch for config param for type of inventories for DCAP, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME,D_ACTIVE,PARAM_DISPLAY_TYPE,LOGICAL_GROUP, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER)
VALUES
(config_param_seq.NEXTVAL, 'Specify type of inventories for DCAP', 'Type Of Inventories Allowed For DCAP','typeOfInventoriesAllowedForDCAP','java.lang.String',SYSDATE, '4.3 Upgrade', SYSDATE, 56, sysdate, sysdate,1,'select',NULL,1,NULL,1,1)
/
INSERT INTO config_param_option(ID, display_value, value)
VALUES(CONFIG_PARAM_OPTION_SEQ.NEXTVAL,'Only Retail Inventories','Only Retail Inventories')
/
INSERT INTO config_param_option(ID, display_value, value)
VALUES(CONFIG_PARAM_OPTION_SEQ.NEXTVAL,'Only Retail Inventories With PDI','Only Retail Inventories With PDI')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='typeOfInventoriesAllowedForDCAP'),
(select id from config_param_option where value='Only Retail Inventories'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='typeOfInventoriesAllowedForDCAP'),
(select id from config_param_option where value='Only Retail Inventories With PDI'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='typeOfInventoriesAllowedForDCAP'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Hussmann',
(select id from CONFIG_PARAM_OPTION where value='Only Retail Inventories'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='typeOfInventoriesAllowedForDCAP'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'TFM',
(select id from CONFIG_PARAM_OPTION where value='Only Retail Inventories'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='typeOfInventoriesAllowedForDCAP'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'AIR',
(select id from CONFIG_PARAM_OPTION where value='Only Retail Inventories'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='typeOfInventoriesAllowedForDCAP'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Transport Solutions ESA',
(select id from CONFIG_PARAM_OPTION where value='Only Retail Inventories'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='typeOfInventoriesAllowedForDCAP'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Clubcar ESA',
(select id from CONFIG_PARAM_OPTION where value='Only Retail Inventories'))
/
COMMIT
/