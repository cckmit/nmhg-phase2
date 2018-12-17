--Purpose    : Data migration for existing params
--Author     : prashanth konda
--Created On : 29-sep-08

CREATE SEQUENCE CFG_PARAM_OPTNS_MAPPING_SEQ   START WITH 2200   INCREMENT BY 20   MAXVALUE 999999999999999999999999999   MINVALUE 1   NOCYCLE CACHE 20 NOORDER
/
ALTER TABLE CONFIG_PARAM_OPTIONS_MAPPING ADD CONSTRAINT CONFIG_PARAM_OPTIONS_MAPPI_U01  UNIQUE (PARAM_ID, OPTION_ID)  ENABLE VALIDATE
/
ALTER TABLE CONFIG_PARAM DROP COLUMN INTERNAL_COMMENTS
/
ALTER TABLE CONFIG_PARAM DROP COLUMN INTERNAL_STATUS
/
ALTER TABLE CONFIG_PARAM_OPTION DROP COLUMN CONFIG_PARAM_ID
/
CREATE SEQUENCE CONFIG_PARAM_OPTION_SEQ
  START WITH 1000
  INCREMENT BY 20
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'DirectCustomer', 'DirectCustomer')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'InterCompany', 'InterCompany')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'NationalAccount', 'NationalAccount')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'Dealer', 'Dealer')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'EndCustomer', 'EndCustomer')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL,'Machine','Machine')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'Campaign', 'Campaign')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'Parts', 'Parts')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'false', 'No')
/
Insert into CONFIG_PARAM_OPTION    (ID, VALUE, DISPLAY_VALUE)  Values    (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'true', 'Yes')
/
INSERT INTO CONFIG_PARAM(ID,   description,   display_name,   NAME,   TYPE,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time,   LOGICAL_GROUP)
VALUES(config_param_seq.NEXTVAL,   'List the customer types displayed in the drop down menu on warranty plan setup page',   'Customer Types Displayed In Policy',   'customersForPolicy',   'java.lang.String',   SYSDATE,   'Configuration',   SYSDATE,   NULL,   NULL,   NULL,   NULL)
/
INSERT INTO config_param(id,   description,   display_name,   name,   type,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time,   logical_group) 
VALUES(config_param_seq.nextval,   'Base Rates & Modifiers can be setup only for the selected Customer Types.',   'Customer Types for Base Rates & Modifiers',   'customersFilingClaim',   'java.lang.String',   sysdate,   'Configuration',   sysdate,   NULL,   NULL,   NULL,   NULL)
/
INSERT INTO CONFIG_PARAM(ID,   description,   display_name,   NAME,   TYPE,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time,   LOGICAL_GROUP)
VALUES(config_param_seq.NEXTVAL,   'List the customer types displayed in the drop down menu on DR page.',   'Customer Types Displayed In DR',   'customersFilingDR',   'java.lang.String',   SYSDATE,   'Configuration',   SYSDATE,   NULL,   NULL,   NULL,   NULL)
/
INSERT
INTO CONFIG_PARAM(ID,   description,   display_name,   NAME,   TYPE,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time,   LOGICAL_GROUP)
VALUES(config_param_seq.NEXTVAL,   'List the customer types displayed in the drop down menu on ETR page.',   'Customer Types Displayed In ETR',   'customersFilingETR',   'java.lang.String',   SYSDATE,   'Configuration',   SYSDATE,   NULL,   NULL,   NULL,   NULL)
/
UPDATE CONFIG_PARAM SET PARAM_DISPLAY_TYPE = 'radio' where  type = 'boolean'
/
UPDATE CONFIG_PARAM SET PARAM_DISPLAY_TYPE = 'multiselect' where name = 'claimType'
/
UPDATE CONFIG_PARAM SET PARAM_DISPLAY_TYPE = 'multiselect' where name in ('customersFilingClaim','wntyConfigCustomerTypesAllowedForETR','customersFilingDR','customersFilingETR','customersForPolicy')
/
UPDATE CONFIG_PARAM SET PARAM_DISPLAY_TYPE = 'textbox' where type = 'number'
/
UPDATE CONFIG_PARAM SET PARAM_DISPLAY_TYPE = 'textbox' where  type = 'java.lang.String' and type is null
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingClaim' and cpo.value = 'DirectCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingDR' and cpo.value = 'DirectCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingETR' and cpo.value = 'DirectCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersForPolicy' and cpo.value = 'DirectCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingClaim' and cpo.value = 'EndCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingDR' and cpo.value = 'EndCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingETR' and cpo.value = 'EndCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersForPolicy' and cpo.value = 'EndCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingClaim' and cpo.value = 'Dealer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingDR' and cpo.value = 'Dealer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingETR' and cpo.value = 'Dealer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersForPolicy' and cpo.value = 'Dealer')
/ 
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingClaim' and cpo.value = 'InterCompany')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingDR' and cpo.value = 'InterCompany')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingETR' and cpo.value = 'InterCompany')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersForPolicy' and cpo.value = 'InterCompany')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingClaim' and cpo.value = 'NationalAccount')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingDR' and cpo.value = 'NationalAccount')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersFilingETR' and cpo.value = 'NationalAccount')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customersForPolicy' and cpo.value = 'NationalAccount')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'claimType' and cpo.value = 'Parts')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'claimType' and cpo.value = 'Campaign')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'claimType' and cpo.value = 'Machine')
/
update config_value set config_param_option = (select id from config_param_option where value='Machine') where value ='Machine'
/
update config_value set config_param_option = (select id from config_param_option where value='Parts') where value ='Parts'
/
update config_value set config_param_option = (select id from config_param_option where value='Campaign') where value ='Campaign'
/
update config_value set config_param_option = (select id from config_param_option where value='true') where config_param =   (select cp.id from config_param cp where cp.name = 'matchReadApplicable')
/  
update config_value set config_param_option = (select id from config_param_option where value='true') where config_param =   (select cp.id from config_param cp where cp.name = 'multipleJobCodeAllowed')
/  
update config_value set config_param_option = (select id from config_param_option where value='true') where config_param =   (select cp.id from config_param cp where cp.name = 'canDealerEditFwdedClaims')
/  
update config_value set config_param_option = (select id from config_param_option where value='true') where config_param =   (select cp.id from config_param cp where cp.name = 'canProcessorEditClaims')
/  
update config_value set config_param_option = (select id from config_param_option where value='true') where config_param =   (select cp.id from config_param cp where cp.name = 'manualApprovalFlowForDR')
/
commit
/