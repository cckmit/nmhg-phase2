--PURPOSE    : PATCH TO CREATE BU CONFIG FOR WARRANTY ORDER FOR CLAIM
--CREATED ON : 18-JUNE-13


Insert into config_param (ID,DESCRIPTION,DISPLAY_NAME,NAME,TYPE,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,PARAM_DISPLAY_TYPE,LOGICAL_GROUP,LOGICAL_GROUP_ORDER,SECTIONS,SECTIONS_ORDER,PARAM_ORDER) 
values (CONFIG_PARAM_SEQ.nextval,'Enable Warranty Order Claim','Enable Warranty Order Claim','enableWarrantyOrderClaim','boolean',sysdate,'Nacco Configuration',sysdate,null,sysdate,sysdate,1,'radio','CLAIMS',1,'CLAIM_INPUT_PARAMETERS',1,1)
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) 
VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='enableWarrantyOrderClaim'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Yes'))
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) 
VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='enableWarrantyOrderClaim'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='No'))
/