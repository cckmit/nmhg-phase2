--Purpose    : PATCH FOR ADDING NEW CONFIG PARAM FOR PART RETURN VISIBILITY FOR DEALERS, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None


ALTER TABLE PART_RETURN
ADD BAR_CODE VARCHAR(255)
/
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Enable Dealers to enter Barcodes while returning parts', 'Enable Bar Code Feature','enableBarCodeFeature','boolean',SYSDATE, '4.3 Upgrade', SYSDATE, NULL, SYSDATE, 
    SYSDATE, 'CLAIMS', 'radio', 1, 'CLAIM_RETURN_PART', 
    1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableBarCodeFeature'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableBarCodeFeature'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enableBarCodeFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Transport Solutions ESA',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enableBarCodeFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Hussmann',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enableBarCodeFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'TFM',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enableBarCodeFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'AIR',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enableBarCodeFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Clubcar ESA',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Enable the Part Return receiver to correct the part number entered by dealer', 'Enable Part Off Feature','enablePartOffFeature','boolean',SYSDATE, '4.3 Upgrade', SYSDATE, NULL, SYSDATE, SYSDATE, 'CLAIMS', 'radio', 1, 'CLAIM_RETURN_PART', 1, 1, 1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enablePartOffFeature'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enablePartOffFeature'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enablePartOffFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Transport Solutions ESA',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enablePartOffFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Hussmann',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enablePartOffFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'TFM',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enablePartOffFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'AIR',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='enablePartOffFeature'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Clubcar ESA',
(select id from CONFIG_PARAM_OPTION where value='false'))
/
COMMIT
/
