--PURPOSE    : PATCH FOR ADDING NEW CONFIG PARAM IF EXTERNAL USER CAN PURCHASE EXTENDED WARRANTY
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 14-JUL-09

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Can external user purchase extended warranty', 'Can external user purchase extended warranty',
    'canExternalUserPurchaseExtendedWarranty','boolean', 
    TO_DATE('11/01/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'Configuration', TO_DATE('12/24/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), (select id from org_user WHERE login like 'system'), NULL, 
    NULL, 'INVENTORY', 'radio', 1, 'INVENTORY_SEARCH', 
    1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canExternalUserPurchaseExtendedWarranty'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canExternalUserPurchaseExtendedWarranty'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canExternalUserPurchaseExtendedWarranty'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), (select id from org_user WHERE login like 'system'), 'AIR', NULL, NULL,(select id from config_param_option where value='true'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canExternalUserPurchaseExtendedWarranty'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), (select id from org_user WHERE login like 'system'), 'TFM', NULL, NULL,(select id from config_param_option where value='true'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canExternalUserPurchaseExtendedWarranty'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), (select id from org_user WHERE login like 'system'), 'Clubcar ESA', NULL, NULL,(select id from config_param_option where value='true'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canExternalUserPurchaseExtendedWarranty'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), (select id from org_user WHERE login like 'system'), 'Hussmann', NULL, NULL,(select id from config_param_option where value='true'),1)
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canExternalUserPurchaseExtendedWarranty'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), (select id from org_user WHERE login like 'system'), 'Transport Solutions ESA', NULL, NULL,(select id from config_param_option where value='true'),1)
/
COMMIT
/