--Purpose    : Patch for config param for Marketing Information in Warranty Registration
--Author     : lavin.hawes	
--Created On : 29-March-2010

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Is marketing information by product applicable', 'Is marketing information by product applicable','isMarketInfoApplicable','boolean',TO_DATE('02/02/2010 10:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'TSA-Configuration', TO_DATE('02/02/2010 10:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
NULL, 'INVENTORY', 'radio', 1, 'INVENTORY_DR_ETR', 1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='isMarketInfoApplicable'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='isMarketInfoApplicable'),
(select id from config_param_option where value='false'))
/
COMMIT
/