--Purpose    : Patch for adding new config param if a dealer can search for other dealers retail inventory
--Author     : raghuram.d
--Created on : 19-AUG-09

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, 
    NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, 
    D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, 
    SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Can dealer search for other dealers retail inventory', 'Can dealer search for other dealers retail inventory',
    'canDealerSearchOtherDealersRetail','boolean', SYSDATE, 'Configuration', SYSDATE, (select id from org_user WHERE login = 'system'), 
    NULL, NULL, 'CLAIMS', 'radio', 1, 'CLAIM_INPUT_PARAMETERS', 
    1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canDealerSearchOtherDealersRetail'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canDealerSearchOtherDealersRetail'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canDealerSearchOtherDealersRetail'),
SYSDATE, NULL, SYSDATE, (select id from org_user WHERE login = 'system'), 'AIR', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canDealerSearchOtherDealersRetail'),
SYSDATE, NULL, SYSDATE, (select id from org_user WHERE login = 'system'), 'TFM', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canDealerSearchOtherDealersRetail'),
SYSDATE, NULL, SYSDATE, (select id from org_user WHERE login = 'system'), 'Clubcar ESA', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canDealerSearchOtherDealersRetail'),
SYSDATE, NULL, SYSDATE, (select id from org_user WHERE login = 'system'), 'Hussmann', NULL, NULL,(select id from config_param_option where value='true'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canDealerSearchOtherDealersRetail'),
SYSDATE, NULL, SYSDATE, (select id from org_user WHERE login = 'system'), 'Transport Solutions ESA', NULL, NULL,(select id from config_param_option where value='false'),1)
/
COMMIT
/