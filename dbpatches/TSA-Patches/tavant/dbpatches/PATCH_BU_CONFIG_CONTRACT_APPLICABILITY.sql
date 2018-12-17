--Purpose    : Patch for config param for Contract Applicablity 
--Author     : lavin.hawes	
--Created On : 03-September-2010

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'This BU Config is used to check coverage applicability of contracts defined by Date of Manufacture', 'Do serialized parts have shipment date','canSerializedPartsHaveShipmentDate','boolean',TO_DATE('03/09/2010 10:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'TSA-Configuration', TO_DATE('03/09/2010 10:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
NULL, 'SUPPLIER_RECOVERY', 'radio', 1,'null', 1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canSerializedPartsHaveShipmentDate'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canSerializedPartsHaveShipmentDate'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='canSerializedPartsHaveShipmentDate'),sysdate,'TSA-Migration | Added to boothstrap from DBPatch PATCH_BU_CONFIG_CONTRACT_APPLICABILITY.sql',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
COMMIT
/
