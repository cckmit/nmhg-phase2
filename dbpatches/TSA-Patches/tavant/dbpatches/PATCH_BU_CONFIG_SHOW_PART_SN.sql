--Purpose    : Patch for config param for hide part serial number on parts installed/remove section,changes made as a part of 4.3 upgrade 
--Created On : 24-Nov-2010
--Created By : Manish kumar

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME,D_ACTIVE,PARAM_DISPLAY_TYPE,LOGICAL_GROUP, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER)
VALUES
(config_param_seq.NEXTVAL, 'Show Part Serial Number on installed and remove section', 'Display Part Serial Number field on Parts Installed/Removed section','showPartSerialNumber','boolean',SYSDATE, '4.3 Upgrade', SYSDATE, 56, sysdate, sysdate,1,'radio','CLAIMS',1,'CLAIM_INPUT_PARAMETERS',1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='showPartSerialNumber'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='showPartSerialNumber'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',
(select id from CONFIG_PARAM where NAME='showPartSerialNumber'),sysdate,'4.3 Upgrade', sysdate,56,SYSDATE,SYSDATE,1,'Thermo King TSA',
(select id from CONFIG_PARAM_OPTION where value='true'))
/
commit
/