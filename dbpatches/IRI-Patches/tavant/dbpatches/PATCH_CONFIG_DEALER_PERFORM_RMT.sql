--PURPOSE    : PATCH FOR ADDING NEW CONFIG PARAM IF EXTERNAL USER CAN FILE RMT
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 23-FEB-10

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Can external user perform Retail Machine Transfer', 'Can external user perform Retail Machine Transfer',
    'canDealerPerformRMT','boolean',sysdate, 'Configuration', sysdate, (select id from org_user WHERE login like 'system'), NULL, 
    NULL, 'INVENTORY', 'radio', 1, 'INVENTORY_SEARCH', 
    1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canDealerPerformRMT'),
(select max(id) from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canDealerPerformRMT'),
(select max(id) from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'canDealerPerformRMT'),
sysdate, NULL, sysdate, (select id from org_user WHERE login like 'system'), 'Thermo King TSA', NULL, NULL,(select id from config_param_option where value='false'),1)
/
COMMIT
/