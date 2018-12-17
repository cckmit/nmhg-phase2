--Purpose    : Patch for config param for Installing Dealer and Installation Date in Warranty Registration
--Author     : lavin.hawes	
--Created On : 05-May2010

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Capture Installing Dealer/Installation Date on WR/ETR', 'Capture Installing Dealer/Installation Date on WR/ETR','enableDealerAndInstallationDate','boolean',sysdate, 'TSA-Configuration',sysdate, NULL, NULL, 
NULL, 'INVENTORY', 'radio', 1, 'INVENTORY_DR_ETR', 1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableDealerAndInstallationDate'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableDealerAndInstallationDate'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Perform Dealer to Dealer Transaction on Warranty Registration', 'Perform Dealer to Dealer Transaction on Warranty Registration','performD2DOnWR','boolean',sysdate, 'TSA-Configuration',sysdate, NULL, NULL, 
NULL, 'INVENTORY', 'radio', 1, 'INVENTORY_DR_ETR', 1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='performD2DOnWR'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='performD2DOnWR'),
(select id from config_param_option where value='false'))
/
COMMIT
/