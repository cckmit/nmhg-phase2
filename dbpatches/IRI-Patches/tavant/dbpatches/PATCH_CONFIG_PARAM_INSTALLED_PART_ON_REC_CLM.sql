--PURPOSE    : PATCH FOR ADDING NEW CONFIG PARAM IF RECOVERY CLAIM HAS TO USE INSTALLED PARTS
--AUTHOR     : RAHUL KATARIYA
--CREATED ON : 12-AUG-09

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Configure if Installed Parts should be used to calculate recovery claims instead or removed parts(Not applicable if Parts Replaced / Installed Section is disabled)', 'Use Installed Parts for calculations on Recovery Claims',
    'useInstalledPartsForRecoveryClaim','boolean', 
    sysdate, 'Configuration', sysdate, (select id from org_user WHERE login like 'system'), NULL, 
    NULL, 'SUPPLIER_RECOVERY', 'radio', 1, null, 
    1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='useInstalledPartsForRecoveryClaim'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='useInstalledPartsForRecoveryClaim'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, 'false', (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'useInstalledPartsForRecoveryClaim'),
sysdate, NULL, sysdate, (select id from org_user WHERE login like 'system'), 'AIR', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, 'false', (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'useInstalledPartsForRecoveryClaim'),
sysdate, NULL, sysdate, (select id from org_user WHERE login like 'system'), 'TFM', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, 'false', (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'useInstalledPartsForRecoveryClaim'),
sysdate, NULL, sysdate, (select id from org_user WHERE login like 'system'), 'Clubcar ESA', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, 'true', (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'useInstalledPartsForRecoveryClaim'),
sysdate, NULL, sysdate, (select id from org_user WHERE login like 'system'), 'Hussmann', NULL, NULL,(select id from config_param_option where value='true'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, 'false', (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'useInstalledPartsForRecoveryClaim'),
sysdate, NULL, sysdate, (select id from org_user WHERE login like 'system'), 'Transport Solutions ESA', NULL, NULL,(select id from config_param_option where value='false'),1)
/
COMMIT
/