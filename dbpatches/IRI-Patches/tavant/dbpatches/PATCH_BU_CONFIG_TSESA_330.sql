--Purpose : new config param to enable Inventory Full View at BU level
--Author  : raghuram.d
--Date    : 13/Jul/2010

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, 
D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, 
PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Display additional end user and claim info on EHP', 
    'Display additional end user and claim info on EHP',
    'enableInventoryFullView','boolean', 
    SYSDATE, 'InventoryFullView - TSESA-330', SYSDATE, 
    (select id from org_user WHERE login = 'system'), 
    systimestamp, systimestamp, 'INVENTORY', 'radio', 1, 'INVENTORY_SEARCH', 
    1, 1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableInventoryFullView'),
(select min(id) from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableInventoryFullView'),
(select min(id) from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, 
D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableInventoryFullView'),
SYSDATE, NULL, SYSDATE, (select id from org_user WHERE login like 'system'), 'Thermo King TSA', 
SYSTIMESTAMP, SYSTIMESTAMP,(select min(id) from config_param_option where value='true'),1)
/
COMMIT
/