--Purpose    : Fixes for JobCode and InstallBase uploads
--Author     : raghuram.d
--Created On : 08-Jun-09

create or replace PACKAGE COMMON_VALIDATION_UTILS AS

 FUNCTION getValidBusinessUnitName(p_business_unit_name VARCHAR2)
 RETURN VARCHAR2;

 --TO CHECK WHETHER THE GIVEN BUSINESS UNIT NAME IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidBusinessUnitName(p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 FUNCTION isUserBelongsToBU(p_business_unit_name VARCHAR2, p_user_login VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN MODEL IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidModel(p_model VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 --TO CHECK WHETHER THE GIVEN PRODUCT CODE IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidProductCode(p_product_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 --TO CHECK WHETHER THE GIVEN MODEL AND PRODUCT IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidModelForProduct(p_model VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidModelCodeForProduct(p_model_code VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN SUPPLIER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidSupplier(p_supplier_name VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 --TO CHECK WHETHER THE GIVEN ITEM NUMBER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidItemNumber(p_item_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN JOB CODE IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidJobCode(p_job_code VARCHAR2, p_model VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN USER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidUser(p_user_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 --TO CHECK WHETHER THE GIVEN DEALER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidDealer(p_dealer_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN DEALER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidDealerByNumber(p_dealer_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN DEALER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidInventory(p_serial_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN FAULT CODE IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidFaultCode(p_model VARCHAR2, p_fault_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 --TO CHECK WHETHER THE GIVEN FAULT FOUND IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidFaultFound(p_model VARCHAR2, p_fault_found VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 --TO CHECK WHETHER THE GIVEN CAUSED BY IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidCausedBy(p_model VARCHAR2, p_fault_found VARCHAR2, p_caused_by VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN ROOT CAUSE IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidRootCause(p_model VARCHAR2, p_fault_found VARCHAR2, p_root_cause VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK WHETHER THE GIVEN TECHNICIAN IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidTechnician(p_technician VARCHAR2, p_dealer VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;
 
 --TO CHECK IF PASSED DATE IS  A VALID DATE. FORMAT OF DATE SHOULD BE YYYYMMDD
 FUNCTION isValidDate (p_date VARCHAR2, p_format VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE CURRENCY IS VALID AT TWMS SYSTEM
 FUNCTION isValidCurrency (p_currency VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF THE CONFIG PARAM IS SET OR NOT
 FUNCTION isConfigParamSet (p_config_param VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN;

 --TO CHECK IF ASSEMBLY DEFINITION CODE IS VALID
 FUNCTION isValidAssemblyDefinitionCode (p_code VARCHAR2, p_level NUMBER)
 RETURN BOOLEAN;

 FUNCTION isValidActionName (p_action VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION getValidSMRReasonId (p_lov VARCHAR2, p_locale VARCHAR2, p_business_unit_info VARCHAR2)
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

 FUNCTION isValidDelimitedValue(p_value VARCHAR2, p_delimiter VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION isValidReplacedIRPart(p_item_number VARCHAR2, p_business_unit VARCHAR2)
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

END COMMON_VALIDATION_UTILS;
/
COMMIT
/