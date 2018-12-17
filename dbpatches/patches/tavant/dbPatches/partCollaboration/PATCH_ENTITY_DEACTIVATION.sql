--Purpose    : Patch For Implementing De-Activation Of Entities 
--Author     : Vikas Sasidharan
--Created On : 08-Dec-08

alter table ACCEPTANCE_REASON add (d_active number(1, 0) default 1)
/
alter table ACCOUNTABILITY_CODE add (d_active number(1, 0) default 1)
/
alter table ACTION_DEFINITION add (d_active number(1, 0) default 1)
/
alter table ACTION_LEVEL add (d_active number(1, 0) default 1)
/
alter table ACTION_NODE add (d_active number(1, 0) default 1)
/
alter table ADDITIONAL_ATTRIBUTES add (d_active number(1, 0) default 1)
/
alter table ADDRESS add (d_active number(1, 0) default 1)
/
alter table ADDRESS_BOOK add (d_active number(1, 0) default 1)
/
alter table ADDRESS_BOOK_ADDRESS_MAPPING add (d_active number(1, 0) default 1)
/
alter table ADDRESS_FOR_TRANSFER add (d_active number(1, 0) default 1)
/
alter table APPLICABLE_POLICY add (d_active number(1, 0) default 1)
/
alter table ASSEMBLY add (d_active number(1, 0) default 1)
/
alter table ASSEMBLY_DEFINITION add (d_active number(1, 0) default 1)
/
alter table ASSEMBLY_LEVEL add (d_active number(1, 0) default 1)
/
alter table ATTRIBUTE add (d_active number(1, 0) default 1)
/
alter table ATTRIBUTE_ASSOCIATION add (d_active number(1, 0) default 1)
/
alter table ATTR_VALUE add (d_active number(1, 0) default 1)
/
alter table BASE_PART_RETURN add (d_active number(1, 0) default 1)
/
alter table BATTERY_INFO add (d_active number(1, 0) default 1)
/
alter table BATTERY_TEST_SHEET add (d_active number(1, 0) default 1)
/
alter table BATTERY_VOLTAGE_INFO add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_CLASS add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_COVERAGE add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_LABOR_DETAIL add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_NOTIFICATION add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_SERIAL_NUMBERS add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_SERIAL_RANGE add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_SERVICE_DETAIL add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_STATUS add (d_active number(1, 0) default 1)
/
alter table CAMPAIGN_TRAVEL_DETAIL add (d_active number(1, 0) default 1)
/
alter table CARRIER add (d_active number(1, 0) default 1)
/
alter table CATEGORY add (d_active number(1, 0) default 1)
/
alter table CLAIM_ATTRIBUTES add (d_active number(1, 0) default 1)
/
alter table CLAIMED_ITEM add (d_active number(1, 0) default 1)
/
alter table CLAIM_NUMBER_PATTERN add (d_active number(1, 0) default 1)
/
alter table COMPENSATION_TERM add (d_active number(1, 0) default 1)
/
alter table COMPETITION_TYPE add (d_active number(1, 0) default 1)
/
alter table COMPETITOR_MAKE add (d_active number(1, 0) default 1)
/
alter table COMPETITOR_MODEL add (d_active number(1, 0) default 1)
/
alter table COMPLAINT add (d_active number(1, 0) default 1)
/
alter table CONFIG_PARAM add (d_active number(1, 0) default 1)
/
alter table CONFIG_VALUE add (d_active number(1, 0) default 1)
/
alter table CONSUMER add (d_active number(1, 0) default 1)
/
alter table CONTRACT add (d_active number(1, 0) default 1)
/
alter table COST_CATEGORY add (d_active number(1, 0) default 1)
/
alter table COST_LINE_ITEM add (d_active number(1, 0) default 1)
/
alter table COUNTRY add (d_active number(1, 0) default 1)
/
alter table COUNTRY_STATE add (d_active number(1, 0) default 1)
/
alter table COVERAGE_CONDITION add (d_active number(1, 0) default 1)
/
alter table CREDIT_MEMO add (d_active number(1, 0) default 1)
/
alter table CRITERIA_EVALUATION_PRECEDENCE add (d_active number(1, 0) default 1)
/
alter table DEALER_GROUP add (d_active number(1, 0) default 1)
/
alter table DEALER_SCHEME add (d_active number(1, 0) default 1)
/
alter table DISCHARGE add (d_active number(1, 0) default 1)
/
alter table DISCHARGE_TEST_TEMP_RANGE add (d_active number(1, 0) default 1)
/
alter table DOCUMENT add (d_active number(1, 0) default 1)
/
alter table DOMAIN_PREDICATE add (d_active number(1, 0) default 1)
/
alter table DOMAIN_RULE add (d_active number(1, 0) default 1)
/
alter table DOMAIN_RULE_ACTION add (d_active number(1, 0) default 1)
/
alter table DOMAIN_RULE_AUDIT add (d_active number(1, 0) default 1)
/
alter table FAILURE_CAUSE add (d_active number(1, 0) default 1)
/
alter table FAILURE_CAUSE_DEFINITION add (d_active number(1, 0) default 1)
/
alter table FAILURE_REASON add (d_active number(1, 0) default 1)
/
alter table FAILURE_STRUCTURE add (d_active number(1, 0) default 1)
/
alter table FAILURE_TYPE add (d_active number(1, 0) default 1)
/
alter table FAILURE_TYPE_DEFINITION add (d_active number(1, 0) default 1)
/
alter table FAULT_CODE add (d_active number(1, 0) default 1)
/
alter table FAULT_CODE_DEFINITION add (d_active number(1, 0) default 1)
/
alter table I18N_TEXT add (d_active number(1, 0) default 1)
/
alter table INSPECTION_RESULT add (d_active number(1, 0) default 1)
/
alter table INVENTORY_ITEM add (d_active number(1, 0) default 1)
/
alter table INVENTORY_ITEM_COMPOSITION add (d_active number(1, 0) default 1)
/
alter table INVENTORY_ITEM_CONDITION add (d_active number(1, 0) default 1)
/
alter table INVENTORY_TRANSACTION add (d_active number(1, 0) default 1)
/
alter table INVENTORY_TRANSACTION_TYPE add (d_active number(1, 0) default 1)
/
alter table INVENTORY_TYPE add (d_active number(1, 0) default 1)
/
alter table ITEM add (d_active number(1, 0) default 1)
/
alter table ITEM_COMPOSITION add (d_active number(1, 0) default 1)
/
alter table ITEM_GROUP add (d_active number(1, 0) default 1)
/
alter table ITEM_MAPPING add (d_active number(1, 0) default 1)
/
alter table ITEM_SCHEME add (d_active number(1, 0) default 1)
/
alter table JOB add (d_active number(1, 0) default 1)
/
alter table JOB_DEFINITION add (d_active number(1, 0) default 1)
/
alter table LABEL add (d_active number(1, 0) default 1)
/
alter table LABOR_DETAIL add (d_active number(1, 0) default 1)
/
alter table LINE_ITEM add (d_active number(1, 0) default 1)
/
alter table LINE_ITEM_GROUP add (d_active number(1, 0) default 1)
/
alter table LOCALIZED_MESSAGES add (d_active number(1, 0) default 1)
/
alter table LOCATION add (d_active number(1, 0) default 1)
/
alter table MARKETING_INFORMATION add (d_active number(1, 0) default 1)
/
alter table MARKET_TYPE add (d_active number(1, 0) default 1)
/
alter table MATCH_READ_INFO add (d_active number(1, 0) default 1)
/
alter table MODEL_PROCESS add (d_active number(1, 0) default 1)
/
alter table MODEL_PROJECT_ENTITY add (d_active number(1, 0) default 1)
/
alter table MODEL_RULE add (d_active number(1, 0) default 1)
/
alter table MSA add (d_active number(1, 0) default 1)
/
alter table ORG_USER add (d_active number(1, 0) default 1)
/
alter table OWNERSHIP_STATE add (d_active number(1, 0) default 1)
/
alter table PART_ACCEPTANCE_REASON add (d_active number(1, 0) default 1)
/
alter table PART_INVENTORY add (d_active number(1, 0) default 1)
/
alter table PART_PAYMENT_INFO add (d_active number(1, 0) default 1)
/
alter table PART_RETURN_ACTION add (d_active number(1, 0) default 1)
/
alter table PART_RETURN_AUDIT add (d_active number(1, 0) default 1)
/
alter table PART_RETURN_DEFINITION add (d_active number(1, 0) default 1)
/
alter table PARTY add (d_active number(1, 0) default 1)
/
alter table PAYMENT add (d_active number(1, 0) default 1)
/
alter table PAYMENT_COMPONENT add (d_active number(1, 0) default 1)
/
alter table PAYMENT_CONDITION add (d_active number(1, 0) default 1)
/
alter table PAYMENT_DEFINITION add (d_active number(1, 0) default 1)
/
alter table PAYMENT_SECTION add (d_active number(1, 0) default 1)
/
alter table PAYMENT_VARIABLE add (d_active number(1, 0) default 1)
/
alter table PAYMENT_VARIABLE_LEVEL add (d_active number(1, 0) default 1)
/
alter table POLICY add (d_active number(1, 0) default 1)
/
alter table POLICY_AUDIT add (d_active number(1, 0) default 1)
/
alter table POLICY_DEFINITION add (d_active number(1, 0) default 1)
/
alter table PURPOSE add (d_active number(1, 0) default 1)
/
alter table REC_CLAIM_AUDIT add (d_active number(1, 0) default 1)
/
alter table REC_CLM_ACCPT_REASON add (d_active number(1, 0) default 1)
/
alter table REC_CLM_REJECT_REASON add (d_active number(1, 0) default 1)
/
alter table RECOVERY_CLAIM add (d_active number(1, 0) default 1)
/
alter table RECOVERY_FORMULA add (d_active number(1, 0) default 1)
/
alter table RECOVERY_PAYMENT add (d_active number(1, 0) default 1)
/
alter table REJECTION_REASON add (d_active number(1, 0) default 1)
/
alter table ROLE add (d_active number(1, 0) default 1)
/
alter table RULE_FAILURE add (d_active number(1, 0) default 1)
/
alter table SAVED_QUERY add (d_active number(1, 0) default 1)
/
alter table SECTION add (d_active number(1, 0) default 1)
/
alter table SERIALIZED_ITEM_REPLACEMENT add (d_active number(1, 0) default 1)
/
alter table SERVICE add (d_active number(1, 0) default 1)
/
alter table SERVICE_INFORMATION add (d_active number(1, 0) default 1)
/
alter table SERVICE_PROCEDURE add (d_active number(1, 0) default 1)
/
alter table SERVICE_PROCEDURE_DEFINITION add (d_active number(1, 0) default 1)
/
alter table SHIPMENT add (d_active number(1, 0) default 1)
/
alter table SMR_REASON add (d_active number(1, 0) default 1)
/
alter table SPECIFIC_GRAVITY_INFO add (d_active number(1, 0) default 1)
/
alter table SUP_REC_COST_CATEGORY add (d_active number(1, 0) default 1)
/
alter table SYNC_STATUS add (d_active number(1, 0) default 1)
/
alter table TEXT add (d_active number(1, 0) default 1)
/
alter table TRANSACTION_TYPE add (d_active number(1, 0) default 1)
/
alter table TRAVEL_DETAIL add (d_active number(1, 0) default 1)
/
alter table TREAD_BUCKET add (d_active number(1, 0) default 1)
/
alter table UPLOAD_HISTORY add (d_active number(1, 0) default 1)
/
alter table USER_CLUSTER add (d_active number(1, 0) default 1)
/
alter table USER_COMMENT add (d_active number(1, 0) default 1)
/
alter table USER_GROUP add (d_active number(1, 0) default 1)
/
alter table USER_SCHEME add (d_active number(1, 0) default 1)
/
alter table WAREHOUSE add (d_active number(1, 0) default 1)
/
alter table WARRANTY add (d_active number(1, 0) default 1)
/
alter table WARRANTY_TYPE add (d_active number(1, 0) default 1)
/
alter table WATCHED_DEALERSHIP add (d_active number(1, 0) default 1)
/
alter table WATCHED_PART add (d_active number(1, 0) default 1)
/
alter table OEM_PART_REPLACED add (d_active number(1, 0) default 1)
/
alter table non_OEM_PART_REPLACED add (d_active number(1, 0) default 1)
/
alter table LINE_ITEM_GROUP_AUDIT add (d_active number(1, 0) default 1)
/