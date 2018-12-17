--Purpose : new config param to consider the date against which the extended plan has to be valid
--Author  : raghuram.d
--Date    : 08/Mar/10

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, 
D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, 
PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Date to be considered to check the availability of an Extended Plan on EWP', 
    'Extended Warranty Purchase Logic Driven by',
    'dateConsideredForExtendedPlanAvailability','java.lang.String', 
    SYSDATE, 'Configuration - TSESA-221', SYSDATE, 
    (select id from org_user WHERE login = 'system'), 
    systimestamp, systimestamp, 'INVENTORY', 'select', 1, 'INVENTORY_SEARCH', 
    1, 1,1)
/
INSERT  INTO CONFIG_PARAM_OPTION (ID,VALUE,DISPLAY_VALUE) VALUES (CONFIG_PARAM_OPTION_SEQ.NEXTVAL,'ewpFiledOnDate','EWP Filing Date')
/
INSERT  INTO CONFIG_PARAM_OPTION (ID,VALUE,DISPLAY_VALUE) VALUES (CONFIG_PARAM_OPTION_SEQ.NEXTVAL,'ewpPurchaseDate','EWP Purchase Date')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='dateConsideredForExtendedPlanAvailability'),
(select id from config_param_option where value='ewpFiledOnDate'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='dateConsideredForExtendedPlanAvailability'),
(select id from config_param_option where value='ewpPurchaseDate'))
/
INSERT INTO CONFIG_VALUE
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, 
D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'dateConsideredForExtendedPlanAvailability'),
SYSDATE, NULL, SYSDATE, (select id from org_user WHERE login like 'system'), 'Thermo King TSA', 
SYSTIMESTAMP, SYSTIMESTAMP,(select id from config_param_option where value='ewpPurchaseDate'),1)
/
COMMIT
/