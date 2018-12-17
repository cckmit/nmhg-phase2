--PURPOSE    : PATCH FOR ADDING CONFIG PARAM TO CHECK RECOVERY FLAG BASED ON ACCOUNTABILITY CODE
--AUTHOR     : JITESH JAIN
--CREATED ON : 14-MAR-09

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Check Recovery Flag based on Accountability Code', 'Check Recovery Flag based on Accountability Code',
    'checkRecoveryFlag','boolean', 
    TO_DATE('03/14/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'Configuration', TO_DATE('03/14/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 56, NULL, 
    NULL, NULL, 'radio', NULL, NULL, 
    NULL, NULL,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='checkRecoveryFlag'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='checkRecoveryFlag'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'checkRecoveryFlag'),
TO_DATE('03/14/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('03/14/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 56, 'Transport Solutions ESA', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'checkRecoveryFlag'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 56, 'Clubcar ESA', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'checkRecoveryFlag'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 56, 'Hussmann', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'checkRecoveryFlag'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 56, 'TFM', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'checkRecoveryFlag'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 56, 'AIR', NULL, NULL,(select id from config_param_option where value='false'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'checkRecoveryFlag'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 56, 'Club Car', NULL, NULL,(select id from config_param_option where value='true'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'checkRecoveryFlag'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 56, 'TK', NULL, NULL,(select id from config_param_option where value='false'),1)
/
COMMIT
/