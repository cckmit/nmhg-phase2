--PURPOSE    : PATCH TO CREATE ATTACHMENT CLAIM TYPE AS NEW WARRANTY CLAIM TYPE
--CREATED ON : 20-JULY-12

INSERT INTO config_param_options_mapping (id, param_id,option_id) 
VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE NAME='claimType'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Attachment'))
/