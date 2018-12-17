--Purpose    : Patch for config param to Allow warranty registration across other dealers stock inventory as a part of 4.3 upgrade 
--Author     : Kuldeep.patil	
--Created On : 15-June-2010

INSERT INTO CONFIG_PARAM
(ID,DESCRIPTION,DISPLAY_NAME,NAME, TYPE,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON, D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE, PARAM_DISPLAY_TYPE,LOGICAL_GROUP,LOGICAL_GROUP_ORDER,SECTIONS,SECTIONS_ORDER,PARAM_ORDER) 
VALUES 
(CONFIG_PARAM_SEQ.NEXTVAL, 'Specify if dealers should be allowed to register units in other dealers stock', 'Allow warranty registration across other dealers stock inventory', 'allowWntyRegOnOthersStock', 'boolean', SYSDATE, '4.3 Upgrade', SYSDATE, NULL, SYSDATE, SYSDATE, 
1, 'radio', 'INVENTORY', 1,'INVENTORY_DR', 1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING 
(ID, PARAM_ID, OPTION_ID) 
VALUES
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, 
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'allowWntyRegOnOthersStock'), 
(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
VALUES
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, 
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'allowWntyRegOnOthersStock'), 
(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/
INSERT INTO CONFIG_VALUE(ID, ACTIVE,VALUE,CONFIG_PARAM,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION) 
VALUES
(CONFIG_VALUE_SEQ.NEXTVAL,1,'',
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'allowWntyRegOnOthersStock'), SYSDATE, '4.3 Upgrade',SYSDATE, NULL,SYSDATE,SYSDATE,1, 'Transport Solutions ESA', 
(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/
INSERT INTO CONFIG_VALUE(ID, ACTIVE,VALUE,CONFIG_PARAM,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION) 
VALUES
(CONFIG_VALUE_SEQ.NEXTVAL,1,'',
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'allowWntyRegOnOthersStock'), SYSDATE, '4.3 Upgrade',SYSDATE, NULL,SYSDATE,SYSDATE,1, 'Hussmann', 
(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/
INSERT INTO CONFIG_VALUE(ID, ACTIVE,VALUE,CONFIG_PARAM,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION) 
VALUES
(CONFIG_VALUE_SEQ.NEXTVAL,1,'',
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'allowWntyRegOnOthersStock'), SYSDATE, '4.3 Upgrade',SYSDATE, NULL,SYSDATE,SYSDATE,1, 'AIR', 
(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/
INSERT INTO CONFIG_VALUE(ID, ACTIVE,VALUE,CONFIG_PARAM,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION) 
VALUES
(CONFIG_VALUE_SEQ.NEXTVAL,1,'',
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'allowWntyRegOnOthersStock'), SYSDATE, '4.3 Upgrade',SYSDATE, NULL,SYSDATE,SYSDATE,1, 'TFM', 
(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/
INSERT INTO CONFIG_VALUE(ID, ACTIVE,VALUE,CONFIG_PARAM,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION) 
VALUES
(CONFIG_VALUE_SEQ.NEXTVAL,1,'',
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'allowWntyRegOnOthersStock'), SYSDATE, '4.3 Upgrade',SYSDATE, NULL,SYSDATE,SYSDATE,1, 'Clubcar ESA', 
(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/
COMMIT
/