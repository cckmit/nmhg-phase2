--Purpose    : Patch for config param for type of inventories for DCAP
--Author     : saya.sudha	
--Created On : 08-jan-2010

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME,D_ACTIVE,PARAM_DISPLAY_TYPE,LOGICAL_GROUP, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER)
VALUES
(config_param_seq.NEXTVAL, 'Specify type of inventories for DCAP', 'Type Of Inventories Allowed For DCAP','typeOfInventoriesAllowedForDCAP','java.lang.String',TO_DATE('12/24/2009 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'TSA-Configuration', TO_DATE('12/24/2009 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
 NULL,1,'select',NULL,1,NULL,1,1)
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
COMMIT
/