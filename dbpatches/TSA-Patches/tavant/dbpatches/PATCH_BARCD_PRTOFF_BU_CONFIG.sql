--PURPOSE    : PATCH FOR ADDING NEW CONFIG PARAM FOR PART RETURN VISIBILITY FOR DEALERS
--AUTHOR     : Amritha Krishnamoorthy
--CREATED ON : 21-DEC-09

ALTER TABLE PART_RETURN
ADD BAR_CODE VARCHAR(255)
/
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Enable Dealers to enter Barcodes while returning parts', 'Enable Bar Code Feature','enableBarCodeFeature','boolean',TO_DATE('12/24/2009 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'TSA-Configuration', TO_DATE('12/24/2009 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
    NULL, 'CLAIMS', 'radio', 1, 'CLAIM_RETURN_PART', 
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
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Enable Part Return receiver to correct the part number entered by dealer', 'Enable Part Off Feature','enablePartOffFeature','boolean',TO_DATE('12/24/2009 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'TSA-Configuration', TO_DATE('12/24/2009 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
    NULL, 'CLAIMS', 'radio', 1, 'CLAIM_RETURN_PART', 
    1,1,1)
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
COMMIT
/
