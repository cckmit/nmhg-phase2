--Purpose    : Changes Made for validation, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None


create or replace
PACKAGE                 "COMMON_VALIDATION_UTILS" AS

 FUNCTION getValidBusinessUnitName(p_business_unit_name VARCHAR2)
 RETURN VARCHAR2;

 
 FUNCTION isValidBusinessUnitName(p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isUserBelongsToBU(p_business_unit_name VARCHAR2, p_user_login VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidModel(p_model VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidProductCode(p_product_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidModelForProduct(p_model VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidModelCodeForProduct(p_model_code VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidSupplier(p_supplier_name VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidItemNumber(p_item_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
  FUNCTION isValidPartSerialNumber(p_serial_number VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN;

 
 FUNCTION isValidJobCode(p_job_code VARCHAR2, p_model VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidUser(p_user_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidDealer(p_dealer_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidDealerByNumber(p_dealer_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidInventory(p_serial_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidInventoryWithConNum (p_container_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 FUNCTION isValidInventoryForFieldModWCN(p_container_number VARCHAR2, p_campaign_code VARCHAR2, p_dealer NUMBER, p_business_unit VARCHAR2)
 RETURN BOOLEAN;
 
 FUNCTION isValidFaultCode(p_model VARCHAR2, p_fault_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidFaultFound(p_model VARCHAR2, p_fault_found VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidCausedBy(p_model VARCHAR2, p_fault_found VARCHAR2, p_caused_by VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidRootCause(p_model VARCHAR2, p_fault_found VARCHAR2, p_root_cause VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidTechnician(p_technician VARCHAR2, p_dealer VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidDate (p_date VARCHAR2, p_format VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidCurrency (p_currency VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isConfigParamSet (p_config_param VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 
 FUNCTION isValidAssemblyDefinitionCode (p_code VARCHAR2, p_level NUMBER)
 RETURN BOOLEAN;

 FUNCTION isValidActionName (p_action VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION getValidSMRReasonId (p_lov VARCHAR2, p_locale VARCHAR2, p_business_unit_info VARCHAR2)
 RETURN VARCHAR2;
 
 FUNCTION getValidCompetitorModelId (p_lov VARCHAR2, p_locale VARCHAR2, p_business_unit_info VARCHAR2)
 RETURN VARCHAR2;

 FUNCTION isValidSMRReason (p_lov VARCHAR2, p_locale VARCHAR2, p_business_unit_info VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidFaultCodeForModelId(p_model NUMBER, p_fault_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidJobCodeForModelId(p_model NUMBER, p_job_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidFaultFoundForModelId(p_model NUMBER, p_fault_found VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidCausedByForModelId(p_model NUMBER, p_fault_found VARCHAR2, p_caused_by VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidRootCauseForModelId(p_model NUMBER, p_fault_found VARCHAR2, p_root_cause VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isAllowedCostCategory(p_cost_cat_code VARCHAR2,p_product VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidDelimitedValue(p_value VARCHAR2, p_delimiter VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidReplacedIRPart(p_item_number VARCHAR2, p_business_unit VARCHAR2)
 RETURN BOOLEAN;
 
 FUNCTION isValidReplacedIRPart(p_serial_number VARCHAR2, p_item_number VARCHAR2, p_business_unit VARCHAR2)
 RETURN BOOLEAN;
 
 FUNCTION isValidInstalledIRPart(p_item_number VARCHAR2, p_business_unit VARCHAR2)
 RETURN BOOLEAN;
 
 FUNCTION isValidAlarmCode(p_alarm_code VARCHAR2,p_product VARCHAR2, p_business_unit VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidMiscPart(p_item_number VARCHAR2, p_service_provider NUMBER, p_business_unit VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidCountry(p_country_code VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidState(p_state VARCHAR2, p_country_code VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidCity(p_city VARCHAR2, p_state VARCHAR2, p_country_code VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidZipcode(p_zipcode VARCHAR2, p_city VARCHAR2, p_state VARCHAR2, p_country_code VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidCampaignCode(p_campaign_code VARCHAR2, p_dealer NUMBER, p_business_unit VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidInventoryForFieldMod(p_serial_number VARCHAR2, p_campaign_code VARCHAR2, p_dealer NUMBER, p_business_unit VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isClaimTypeAllowed(p_type VARCHAR2, p_business_unit VARCHAR2)
 RETURN BOOLEAN;
 
 FUNCTION hasDuplicateSerializedPart(p_value_serialNo     VARCHAR2,p_value_partNo     VARCHAR2,p_delimiter VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION getSerialNoPartNo(p_value_serialNo     VARCHAR2,p_value_partNo     VARCHAR2,p_delimiter VARCHAR2,p_index     NUMBER)
 RETURN VARCHAR2;

END COMMON_VALIDATION_UTILS;
/
COMMIT
/