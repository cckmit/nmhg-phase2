--Purpose    : Patch for adding system configured rules
--Author     : vaibhav.fouzdar	
--Created On : 26-mar-2009
ALTER TABLE DOMAIN_PREDICATE ADD (SYSTEM_DEFINED_CONDITION_NAME VARCHAR2(255 BYTE))
/
Insert into DOMAIN_PREDICATE (id, CONTEXT, NAME, VERSION, D_UPDATED_ON, D_CREATED_TIME, D_UPDATED_TIME, BUSINESS_UNIT_INFO, D_ACTIVE,SYSTEM_DEFINED_CONDITION_NAME)
Values(domain_predicate_seq.nextval,'ClaimRules', 'Claim parameters do not match Campaign setup parameters', 1, current_timestamp, current_timestamp, current_timestamp, 'TK',1, 'campaignCostCategoryValidator')
/
Insert into DOMAIN_PREDICATE (id, CONTEXT, NAME, VERSION, D_UPDATED_ON, D_CREATED_TIME, D_UPDATED_TIME, BUSINESS_UNIT_INFO, D_ACTIVE,SYSTEM_DEFINED_CONDITION_NAME)
Values(domain_predicate_seq.nextval,'ClaimRules', 'Claim parameters do not match Campaign setup parameters', 1, current_timestamp, current_timestamp, current_timestamp, 'Club Car',1, 'campaignCostCategoryValidator')
/
Insert into DOMAIN_PREDICATE (id, CONTEXT, NAME, VERSION, D_UPDATED_ON, D_CREATED_TIME, D_UPDATED_TIME, BUSINESS_UNIT_INFO, D_ACTIVE,SYSTEM_DEFINED_CONDITION_NAME)
Values(domain_predicate_seq.nextval,'ClaimRules', 'Claim parameters do not match Campaign setup parameters', 1, current_timestamp, current_timestamp, current_timestamp, 'Transport Solutions ESA',1, 'campaignCostCategoryValidator')
/
Insert into DOMAIN_PREDICATE (id, CONTEXT, NAME, VERSION, D_UPDATED_ON, D_CREATED_TIME, D_UPDATED_TIME, BUSINESS_UNIT_INFO, D_ACTIVE,SYSTEM_DEFINED_CONDITION_NAME)
Values(domain_predicate_seq.nextval,'ClaimRules', 'Claim parameters do not match Campaign setup parameters', 1, current_timestamp, current_timestamp, current_timestamp, 'AIR',1, 'campaignCostCategoryValidator')
/
Insert into DOMAIN_PREDICATE (id, CONTEXT, NAME, VERSION, D_UPDATED_ON, D_CREATED_TIME, D_UPDATED_TIME, BUSINESS_UNIT_INFO, D_ACTIVE,SYSTEM_DEFINED_CONDITION_NAME)
Values(domain_predicate_seq.nextval,'ClaimRules', 'Claim parameters do not match Campaign setup parameters', 1, current_timestamp, current_timestamp, current_timestamp, 'TFM',1, 'campaignCostCategoryValidator')
/
Insert into DOMAIN_PREDICATE (id, CONTEXT, NAME, VERSION, D_UPDATED_ON, D_CREATED_TIME, D_UPDATED_TIME, BUSINESS_UNIT_INFO, D_ACTIVE,SYSTEM_DEFINED_CONDITION_NAME)
Values(domain_predicate_seq.nextval,'ClaimRules', 'Claim parameters do not match Campaign setup parameters', 1, current_timestamp, current_timestamp, current_timestamp, 'Hussmann',1, 'campaignCostCategoryValidator')
/
Insert into DOMAIN_PREDICATE (id, CONTEXT, NAME, VERSION, D_UPDATED_ON, D_CREATED_TIME, D_UPDATED_TIME, BUSINESS_UNIT_INFO, D_ACTIVE,SYSTEM_DEFINED_CONDITION_NAME)
Values(domain_predicate_seq.nextval,'ClaimRules', 'Claim parameters do not match Campaign setup parameters', 1, current_timestamp, current_timestamp, current_timestamp, 'Clubcar ESA',1, 'campaignCostCategoryValidator')
/
COMMIT
/