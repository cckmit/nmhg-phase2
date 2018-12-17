create or replace
PACKAGE  COMMON_VALIDATION_UTILS AS

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

 FUNCTION getModelForModelCodeAndProduct(p_model_code VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN NUMBER;

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

FUNCTION isValidInventoryWithConNum (p_container_number VARCHAR2,
	p_business_unit_name VARCHAR2, p_error_code OUT VARCHAR2)
RETURN NUMBER;

FUNCTION isValidInventoryForFieldModWCN(p_container_number VARCHAR2, 
	p_campaign_code VARCHAR2, p_dealer NUMBER, 
	p_business_unit VARCHAR2, p_rep_date DATE, p_error_code OUT VARCHAR2)
RETURN NUMBER;

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

 FUNCTION isValidActionCode (p_action VARCHAR2)
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

 FUNCTION isAllowedCostCategory(p_cost_cat_code VARCHAR2,p_product VARCHAR2,p_business_unit_info VARCHAR2)
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

 FUNCTION isValidCampaignCode(p_campaign_code VARCHAR2, p_dealer NUMBER, p_business_unit VARCHAR2,p_date DATE,v_service_provider_number VARCHAR2,p_serial_number VARCHAR2)
 RETURN BOOLEAN;

FUNCTION isValidInventoryForFieldMod(p_serial_number VARCHAR2, 
	p_campaign_code VARCHAR2,p_dealer NUMBER,
	p_business_unit VARCHAR2,p_rep_date DATE,p_error_code OUT VARCHAR2)
RETURN NUMBER;

 FUNCTION isClaimTypeAllowed(p_type VARCHAR2, p_business_unit VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION hasDuplicateSerializedPart(p_value_serialNo     VARCHAR2,p_value_partNo     VARCHAR2,p_delimiter VARCHAR2)
 RETURN BOOLEAN;

 FUNCTION getSerialNoPartNo(p_value_serialNo     VARCHAR2,p_value_partNo     VARCHAR2,p_delimiter VARCHAR2,p_index     NUMBER)
 RETURN VARCHAR2;

 FUNCTION getValidCausalPart(p_item_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN VARCHAR2;

 FUNCTION getReplacedIRPartNumber(p_item_number VARCHAR2, p_business_unit VARCHAR2)
 RETURN VARCHAR2;

 FUNCTION getInstalledIRPart(p_item_number VARCHAR2, p_business_unit VARCHAR2)
 RETURN VARCHAR2;

FUNCTION getOwnershipForInventory(p_inv NUMBER)
RETURN NUMBER;

FUNCTION getServiceProviderType(p_service_provider NUMBER)
RETURN VARCHAR2;

FUNCTION isAllowedCustomerTypeForSearch(p_cust_type VARCHAR2,p_bu VARCHAR2)
RETURN BOOLEAN;

 FUNCTION isValidMachineSerialNumber(p_serial_number VARCHAR2,
	p_business_unit_name VARCHAR2, p_model VARCHAR2,
	p_dealer_id NUMBER, p_error_code OUT VARCHAR2)
RETURN NUMBER;

FUNCTION getValidPartSerialNumber(
	p_serial_number VARCHAR2, 
	p_business_unit_name VARCHAR2)
RETURN NUMBER;

FUNCTION isValidItemForNonSerialized (
	p_item_number VARCHAR2,
	p_bu VARCHAR2,
	p_error_code OUT VARCHAR2)
RETURN NUMBER;

FUNCTION isValidModelForNonSerialized (
	p_model_name VARCHAR2,
	p_bu VARCHAR2,
	p_error_code OUT VARCHAR2)
RETURN NUMBER;

FUNCTION isValidItemForPartsClaim(
	p_item_number VARCHAR2,
	p_bu VARCHAR2,
	p_error_code OUT VARCHAR2)
RETURN NUMBER;

end COMMON_VALIDATION_UTILS;
/
create or replace
PACKAGE BODY COMMON_VALIDATION_UTILS AS 

  FUNCTION getValidBusinessUnitName(p_business_unit_name VARCHAR2)
  RETURN VARCHAR2
  IS
  v_business_unit_name VARCHAR2(255) := NULL;
  BEGIN

    SELECT NAME 
    INTO v_business_unit_name
    FROM business_unit
    WHERE 
    lower(name) = lower(p_business_unit_name) AND ROWNUM = 1;
    RETURN v_business_unit_name;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN NULL;
  END getValidBusinessUnitName;


  FUNCTION isValidBusinessUnitName(p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_business_unit_name VARCHAR2(255) := NULL;
  BEGIN

    SELECT NAME 
    INTO v_business_unit_name
    FROM business_unit
    WHERE 
    lower(name) = lower(p_business_unit_name) AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidBusinessUnitName;

  FUNCTION isUserBelongsToBU(p_business_unit_name VARCHAR2, p_user_login VARCHAR2)
  RETURN BOOLEAN
  IS
  v_business_unit_name VARCHAR2(255) := NULL;
  BEGIN
    SELECT bum.bu
    INTO v_business_unit_name
    FROM org_user u, bu_user_mapping bum
    WHERE 
	lower(bum.bu) = lower(p_business_unit_name)
	AND u.login=p_user_login 
	AND u.id=bum.org_user 
	AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isUserBelongsToBU;


  FUNCTION isValidModel(p_model VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_model VARCHAR2(255) := NULL;
  BEGIN

    SELECT NAME 
    INTO v_model
    FROM item_group
    WHERE 
    lower(name) = lower(ltrim(rtrim(p_model))) 
    and lower(business_unit_info) = lower(p_business_unit_name)
    and item_group_type = 'MODEL' and d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidModel;


 FUNCTION isValidProductCode(p_product_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_product_code NUMBER := NULL;
  BEGIN

    SELECT id
    INTO v_product_code
    FROM item_group
    WHERE 
    lower(name) = lower(ltrim(rtrim(p_product_code))) 
    and lower(business_unit_info) = lower(p_business_unit_name)
    and item_group_type = 'PRODUCT' and d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidProductCode;


  FUNCTION isValidModelForProduct(p_model VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_model VARCHAR2(255) := NULL;
  BEGIN

    SELECT m.NAME 
    INTO v_model
    FROM item_group m,item_group p1,item_group p2
    WHERE 
    lower(m.name) = lower(ltrim(rtrim(p_model))) 
    and (lower(p1.name) = lower(ltrim(rtrim(p_product))) and p1.item_group_type='PRODUCT'
      or lower(p2.name) = lower(ltrim(rtrim(p_product))) and p1.item_group_type='PRODUCT')
    and lower(m.business_unit_info) = lower(p_business_unit_name)
    and lower(p1.business_unit_info) = lower(p_business_unit_name)
    and lower(p2.business_unit_info) = lower(p_business_unit_name)
    and m.is_part_of=p1.id and p1.d_active = 1
    and p1.is_part_of=p2.id and p2.d_active = 1
    and m.item_group_type = 'MODEL' and m.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidModelForProduct;


  FUNCTION isValidModelCodeForProduct(p_model_code VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_model VARCHAR2(255) := NULL;
  BEGIN

    SELECT m.NAME 
    INTO v_model
    FROM item_group m,item_group p1,item_group p2
    WHERE 
    lower(m.group_code) = lower(ltrim(rtrim(p_model_code))) 
    and (lower(p1.name) = lower(ltrim(rtrim(p_product))) and p1.item_group_type='PRODUCT'
      or lower(p2.name) = lower(ltrim(rtrim(p_product))) and p2.item_group_type='PRODUCT')
    and lower(m.business_unit_info) = lower(p_business_unit_name)
    and lower(p1.business_unit_info) = lower(p_business_unit_name)
    and lower(p2.business_unit_info) = lower(p_business_unit_name)
    and m.is_part_of=p1.id and p1.d_active = 1
    and p1.is_part_of=p2.id and p2.d_active = 1
    and m.item_group_type = 'MODEL' and m.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidModelCodeForProduct;

  FUNCTION getModelForModelCodeAndProduct(p_model_code VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN NUMBER
  IS
  v_model NUMBER := NULL;
  v_scheme NUMBER := NULL;
  BEGIN
	SELECT id INTO v_scheme
	FROM item_scheme WHERE UPPER(name)='PROD STRUCT SCHEME'
		AND business_unit_info=p_business_unit_name;

	SELECT A.id INTO v_model
	FROM
	(SELECT IG.group_code,
	  IG.item_group_type,
	  IG.business_unit_info,
	  IG.id
	FROM item_group IG
	  CONNECT BY PRIOR IG.ID = IG.IS_PART_OF
	  START WITH
	  (
		UPPER(IG.NAME)           = UPPER(LTRIM(RTRIM(p_product)))
	  AND IG.BUSINESS_UNIT_INFO = p_business_unit_name
	  AND IG.ITEM_GROUP_TYPE    = 'PRODUCT'
	  AND IG.D_ACTIVE           = 1
	  AND IG.SCHEME             = v_scheme
	  )
	) A
	WHERE A.ITEM_GROUP_TYPE  = 'MODEL'
	AND UPPER(A.group_code)               = UPPER(LTRIM(RTRIM(p_model_code)))
	AND A.BUSINESS_UNIT_INFO = p_business_unit_name
	AND ROWNUM               = 1;

    RETURN v_model;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN NULL;
  END getModelForModelCodeAndProduct;

  FUNCTION isValidItemNumber(p_item_number VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_item_id NUMBER := NULL;
  v_item_number_index NUMBER := -1;
  v_item_number VARCHAR2(255) := NULL;
  BEGIN


    SELECT i.ID 
    INTO v_item_id
    FROM ITEM i, PARTY p 
    WHERE ( UPPER(i.alternate_item_number) = UPPER(ltrim(rtrim(p_item_number)))  OR 
	UPPER(i.item_number) = UPPER(ltrim(rtrim(p_item_number))) )
    AND lower(i.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name)))
    AND i.owned_by = p.ID
    AND p.NAME = common_utils.constant_oem_name and i.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidItemNumber;



  FUNCTION isValidPartSerialNumber(p_serial_number VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_item_id NUMBER := NULL;  
  BEGIN


    SELECT i.ID 
    INTO v_item_id
    FROM INVENTORY_ITEM i, PARTY p 
    WHERE lower(i.serial_number) = lower(ltrim(rtrim(p_serial_number))) 
    AND lower(i.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name)))
    and i.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidPartSerialNumber;


  FUNCTION isValidSupplier(p_supplier_name VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_supplier_id NUMBER := NULL;
  BEGIN

    SELECT s.ID 
    INTO v_supplier_id
    FROM SUPPLIER s, bu_org_mapping m
    WHERE UPPER(supplier_number) = UPPER(p_supplier_name)
        AND s.id = m.org AND UPPER(m.bu) = UPPER(p_business_unit_name)
        AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidSupplier;


 FUNCTION isValidJobCode(p_job_code VARCHAR2, p_model VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_job_code VARCHAR2(256) := NULL;
  BEGIN

    select to_char(spd.code)
    INTO v_job_code
    FROM 
      action_node an,
      service_procedure sp,
      service_procedure_definition spd,
      (select id from assembly connect by prior id = is_part_of_assembly start with id in 
          ( select a.id 
            from 
            failure_structure fs,
            failure_structure_assemblies fsa,
            assembly a,
            item_group ig
            where
              a.active = 1 and
              fs.for_item_group = ig.id and
              fs.id = fsa.failure_structure and
              fsa.assemblies = a.id and
              upper(ig.name) = upper(p_model))) adata
    WHERE
      adata.id = an.defined_for and
      an.id = sp.defined_for and
      an.active = 1 and
      sp.definition = spd.id and
      lower(spd.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and
      lower(spd.code) = lower(ltrim(rtrim(p_job_code))) and spd.d_active = 1 AND ROWNUM = 1;

    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidJobCode;


 FUNCTION isValidUser(p_user_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_user_login VARCHAR2(256) := NULL;
  BEGIN

    SELECT OU.LOGIN  
    INTO v_user_login
    FROM ORG_USER OU, BU_USER_MAPPING BUM 
    WHERE lower(OU.login) = lower(ltrim(rtrim(p_user_login))) AND 
    OU.ID = BUM.ORG_USER AND 
    lower(bum.bu) = lower(ltrim(rtrim(p_business_unit_name))) AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidUser;


 FUNCTION isValidDealer(p_dealer_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_dealer_login VARCHAR2(256) := NULL;
  BEGIN

    SELECT OU.LOGIN  
    INTO v_dealer_login
    FROM ORG_USER OU, DEALERSHIP dealer, BU_USER_MAPPING BUM 
    WHERE lower(OU.login) = lower(ltrim(rtrim(p_dealer_login))) AND 
    OU.ID = BUM.ORG_USER AND dealer.id = OU.belongs_to_organization AND 
    lower(bum.bu) = lower(ltrim(rtrim(p_business_unit_name))) AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidDealer;


 FUNCTION isValidDealerByNumber(p_dealer_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_dealer_number VARCHAR2(256) := NULL;
  BEGIN

    SELECT dealer.dealer_number  
    INTO v_dealer_number
    from bu_org_mapping bom, dealership dealer 
    where lower(dealer.dealer_number) = lower(ltrim(rtrim(p_dealer_number))) and dealer.id = bom.org and 
    lower(bom.bu) = lower(ltrim(rtrim(p_business_unit_name))) AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidDealerByNumber;


 FUNCTION isValidInventory (p_serial_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_inventory VARCHAR2(256) := NULL;
  BEGIN

    SELECT ID   
    INTO v_inventory
    FROM INVENTORY_ITEM 
    WHERE lower(serial_number) = lower(ltrim(rtrim(p_serial_number))) AND 
    lower(business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and d_active=1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidInventory;


FUNCTION isValidInventoryWithConNum (
	p_container_number VARCHAR2, 
	p_business_unit_name VARCHAR2,
	p_error_code OUT VARCHAR2)
RETURN NUMBER
IS
v_inventory NUMBER := NULL;
BEGIN
	p_error_code := NULL;
	SELECT ID INTO v_inventory
	FROM INVENTORY_ITEM 
	WHERE lower(vin_number) = lower(ltrim(rtrim(p_container_number)))
		AND business_unit_info=p_business_unit_name 
		AND d_active=1 AND serialized_part = 0;
	RETURN v_inventory;
EXCEPTION 
	WHEN NO_DATA_FOUND THEN
		p_error_code := 'CN_INV';
		RETURN NULL;
	WHEN TOO_MANY_ROWS THEN
		p_error_code := 'CN_DUP';
		RETURN NULL;
END isValidInventoryWithConNum;

 FUNCTION isValidFaultCode(p_model VARCHAR2, p_fault_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_fault_code            VARCHAR2(256) := NULL;
  v_failure_structure     NUMBER := NULL;
  v_complete_fault_code   VARCHAR2(4000) := NULL;
  BEGIN

    SELECT fcd.code 
    INTO v_fault_code
    FROM 
      fault_code_definition fcd,
      fault_code fc,
        (select fault_code from assembly where fault_code is not null connect by prior id = 
            is_part_of_assembly start with id in 
            (select a.id 
            from 
              failure_structure fs,
              failure_structure_assemblies fsa,
              assembly a,
              item_group ig
            where
              a.active = 1 and
              fs.for_item_group = ig.id and
              fs.id = fsa.failure_structure and
              fsa.assemblies = a.id and
              upper(ig.name) = upper(p_model))) adata
    WHERE
      adata.fault_code = fc.id and
      fc.definition = fcd.id and
      lower(fcd.code) = lower(ltrim(rtrim(p_fault_code))) and 
      lower(fcd.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and fcd.d_active=1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidFaultCode;


 FUNCTION isValidFaultFound(p_model VARCHAR2, p_fault_found VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
 IS
  v_fault_found NUMBER := 0;
 BEGIN
    SELECT ft.id 
    INTO v_fault_found
    from FAILURE_TYPE ft, 
    FAILURE_TYPE_DEFINITION ftd, 
    ITEM_GROUP model
    where 
    ft.definition_id = ftd.id and 
    lower(ftd.name) = lower(ltrim(rtrim(p_fault_found))) and ft.for_item_group_id = model.id and 
    lower(model.name) = lower(ltrim(rtrim(p_model))) and model.d_active = 1 and ft.d_active = 1 and 
    lower(model.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and 
	lower(model.item_group_type) = 'model' AND ROWNUM = 1;

    RETURN TRUE;
 EXCEPTION
 WHEN OTHERS THEN
    RETURN FALSE;
 END isValidFaultFound;


 FUNCTION isValidCausedBy(p_model VARCHAR2, p_fault_found VARCHAR2, p_caused_by VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_caused_by VARCHAR2(256) := NULL;
  BEGIN
    select fc.id 
    INTO v_caused_by
    from FAILURE_CAUSE fc , FAILURE_CAUSE_DEFINITION fcd , FAILURE_TYPE ft, 
    FAILURE_TYPE_DEFINITION ftd, ITEM_GROUP model
    where fc.definition_id = fcd.id and lower(fcd.name) = lower(ltrim(rtrim(p_caused_by))) and 
    fc.failure_type_id = ft.id and ft.definition_id = ftd.id and 
    lower(ftd.name) = lower(ltrim(rtrim(p_fault_found))) and ft.for_item_group_id = model.id and 
    lower(model.name) = lower(ltrim(rtrim(p_model))) and model.d_active = 1 and fc.d_active = 1 and 
    lower(model.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and 
	lower(model.item_group_type) = 'model' AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
 END isValidCausedBy;


 FUNCTION isValidRootCause(p_model VARCHAR2, p_fault_found VARCHAR2, p_root_cause VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_root_cause VARCHAR2(256) := NULL;
  BEGIN
    select frc.id 
    INTO v_root_cause
    from FAILURE_ROOT_CAUSE frc , FAILURE_ROOT_CAUSE_DEFINITION frcd , FAILURE_TYPE ft, 
    FAILURE_TYPE_DEFINITION ftd, ITEM_GROUP model
    where frc.definition_id = frcd.id and lower(frcd.name) = lower(ltrim(rtrim(p_root_cause))) and 
    frc.failure_type_id = ft.id and ft.definition_id = ftd.id and 
    lower(ftd.name) = lower(ltrim(rtrim(p_fault_found))) and ft.for_item_group_id = model.id and 
    lower(model.name) = lower(ltrim(rtrim(p_model))) and model.d_active = 1 and frc.d_active = 1 and 
    lower(model.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and 
	lower(model.item_group_type) = 'model' AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidRootCause;


FUNCTION isValidTechnician(p_technician VARCHAR2, p_dealer VARCHAR2, p_business_unit_name VARCHAR2)
RETURN BOOLEAN
IS
    v_technician VARCHAR2(256) := NULL;
BEGIN

    SELECT OU.LOGIN  
    INTO v_technician
    FROM ORG_USER TECHNICIAN, BU_USER_MAPPING BUM, USER_ROLES ur, ROLE role, 
        org_user_belongs_to_orgs torgs, service_provider dealer,
        ORG_USER OU, org_user_belongs_to_orgs orgs
    WHERE 
        lower(TECHNICIAN.login) = lower(ltrim(rtrim(p_technician))) 
        AND TECHNICIAN.d_active = 1 
        AND technician.ID = BUM.ORG_USER 
        AND lower(bum.bu) = lower(ltrim(rtrim(p_business_unit_name))) 
        AND ur.org_user = TECHNICIAN.id 
        AND ur.roles = role.id 
        AND lower(role.name) = 'technician' 
        AND technician.id = torgs.org_user
        AND torgs.belongs_to_organizations = dealer.id
        AND lower(OU.login) = lower(ltrim(rtrim(p_dealer))) 
        AND ou.id = orgs.org_user
        AND orgs.belongs_to_organizations = dealer.id 
        AND ROWNUM = 1;
RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidTechnician;


 FUNCTION isValidDate (p_date VARCHAR2, p_format VARCHAR2)
 RETURN BOOLEAN
 IS
  v_date DATE := NULL;
  BEGIN
   SELECT TO_DATE(p_date,p_format)
   INTO   v_date
   FROM   DUAL;
   RETURN TRUE;
  EXCEPTION
  WHEN OTHERS THEN
   RETURN FALSE;
  END isValidDate;


 FUNCTION isValidCurrency (p_currency VARCHAR2)
 RETURN BOOLEAN
 IS
  v_currency VARCHAR2(256) := NULL;
  BEGIN
   SELECT from_currency
   INTO   v_currency
   FROM   CURRENCY_EXCHANGE_RATE
   WHERE upper(from_currency) = upper(ltrim(rtrim(p_currency))) AND ROWNUM = 1;
   RETURN TRUE;
  EXCEPTION
  WHEN OTHERS THEN
   RETURN FALSE;
  END isValidCurrency;


 FUNCTION isConfigParamSet (p_config_param VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
 IS
  v_config_value VARCHAR2(10) := NULL;
  BEGIN
    SELECT cpo.value INTO v_config_value
    FROM config_param cp, config_value cv, config_param_option cpo
    WHERE cp.id=cv.config_param AND cp.type='boolean'
	AND upper(cp.name) = upper(p_config_param) 
	AND cp.d_active=1 AND cv.d_active=1
    AND cv.business_unit_info = p_business_unit_name
	AND cv.config_param_option=cpo.id;
	IF lower(v_config_value) = 'true' THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;
  EXCEPTION
  WHEN OTHERS THEN
   RETURN FALSE;
  END isConfigParamSet;


FUNCTION isValidAssemblyDefinitionCode (p_code VARCHAR2, p_level NUMBER)
RETURN BOOLEAN
IS
    v_assembly_id       NUMBER;
BEGIN
    IF p_code = '0000' THEN
        RETURN FALSE;
    END IF;

    SELECT id INTO v_assembly_id
    FROM assembly_definition 
    WHERE lower(code) = lower(p_code) AND assembly_level = p_level
		AND d_active=1;
	RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidAssemblyDefinitionCode;

FUNCTION isValidActionName (p_action VARCHAR2)
RETURN BOOLEAN
IS
    v_id       NUMBER;
BEGIN
    SELECT id INTO v_id FROM action_definition 
    WHERE LOWER(name) = LOWER(p_action) AND ROWNUM=1;
    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidActionName;

FUNCTION isValidActionCode (p_action VARCHAR2)
RETURN BOOLEAN
IS
    v_id       NUMBER;
BEGIN
    SELECT id INTO v_id FROM action_definition 
    WHERE LOWER(code) = LOWER(p_action) AND d_active=1;
    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidActionCode;

FUNCTION getValidSMRReasonId (p_lov VARCHAR2, p_locale VARCHAR2, p_business_unit_info VARCHAR2)
RETURN VARCHAR2
IS
    v_lov_id VARCHAR2(255);
BEGIN
    SELECT l.id INTO v_lov_id
    FROM list_of_values l , i18nlov_text t 
    WHERE 
	l.id=t.list_of_i18n_values 
	AND (t.locale = p_locale OR t.locale='en_US') 
	AND t.description = p_lov
        AND UPPER(l.business_unit_info) = UPPER(p_business_unit_info)
        AND l.type = 'SMRREASON'
        AND l.d_active = 1
        AND l.state='active'
	AND ROWNUM=1;

    RETURN v_lov_id;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END getValidSMRReasonId;

FUNCTION getValidCompetitorModelId (p_lov VARCHAR2, p_locale VARCHAR2, p_business_unit_info VARCHAR2)
RETURN VARCHAR2
IS
    v_lov_id VARCHAR2(255);
BEGIN
    SELECT l.id INTO v_lov_id
    FROM list_of_values l , i18nlov_text t 
    WHERE 
	l.id=t.list_of_i18n_values 
	AND (t.locale = p_locale OR t.locale='en_US') 
	AND UPPER(t.description) = UPPER(p_lov)
        AND UPPER(l.business_unit_info) = UPPER(p_business_unit_info)
        AND l.type = 'CLAIMCOMPETITORMODEL'
        AND l.d_active = 1
        AND l.state='active'
	AND ROWNUM=1;

    RETURN v_lov_id;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END getValidCompetitorModelId;

FUNCTION isValidSMRReason (p_lov VARCHAR2, p_locale VARCHAR2, p_business_unit_info VARCHAR2)
RETURN BOOLEAN
IS
    v_lov_id       NUMBER;
BEGIN

    SELECT l.id INTO v_lov_id
    FROM list_of_values l , i18nlov_text t 
    WHERE 
	l.id=t.list_of_i18n_values 
	AND (t.locale = p_locale OR t.locale='en_US') 
	AND t.description = p_lov
        AND UPPER(l.business_unit_info) = UPPER(p_business_unit_info)
        AND l.type = 'SMRREASON'
        AND l.d_active = 1
        AND l.state='active'
	AND ROWNUM=1;

    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidSMRReason;


FUNCTION isValidFaultCodeForModelId(p_model NUMBER, p_fault_code VARCHAR2, p_business_unit_name VARCHAR2)
RETURN BOOLEAN
IS
    v_fault_code            VARCHAR2(256) := NULL;
BEGIN

    SELECT fcd.code 
    INTO v_fault_code
    FROM fault_code_definition fcd, fault_code fc,
        (select fault_code from assembly where fault_code is not null 
            connect by prior id = is_part_of_assembly start with id in 
            (select a.id 
            from failure_structure fs, failure_structure_assemblies fsa, assembly a
            where a.active = 1 and
                fs.for_item_group = p_model and
                fs.id = fsa.failure_structure and
                fsa.assemblies = a.id )
        ) adata
    WHERE
        adata.fault_code = fc.id and
        fc.definition = fcd.id and
        lower(fcd.code) = lower(ltrim(rtrim(p_fault_code))) and 
        lower(fcd.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and 
        fcd.d_active=1 AND ROWNUM = 1;

    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidFaultCodeForModelId;


FUNCTION isValidJobCodeForModelId(p_model NUMBER, p_job_code VARCHAR2, p_business_unit_name VARCHAR2)
RETURN BOOLEAN
IS
    v_job_code VARCHAR2(256) := NULL;
BEGIN

    select to_char(spd.code)
    INTO v_job_code
    FROM action_node an,
        service_procedure sp,
        service_procedure_definition spd,
        (select id from assembly connect by prior id = is_part_of_assembly start with id in 
            ( select a.id 
            from 
                failure_structure fs,
                failure_structure_assemblies fsa,
                assembly a
            where
                a.active = 1 and
                fs.for_item_group = p_model and
                fs.id = fsa.failure_structure and
                fsa.assemblies = a.id)
        ) adata
    WHERE
        adata.id = an.defined_for and
        an.id = sp.defined_for and
        an.active = 1 and
        sp.definition = spd.id and
        lower(spd.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name))) and
        lower(spd.code) = lower(ltrim(rtrim(p_job_code))) and spd.d_active = 1 AND ROWNUM = 1;

    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidJobCodeForModelId;


FUNCTION isValidFaultFoundForModelId(p_model NUMBER, p_fault_found VARCHAR2, p_business_unit_name VARCHAR2)
RETURN BOOLEAN
IS
    v_fault_found NUMBER := 0;
BEGIN
    SELECT ft.id 
    INTO v_fault_found
    from FAILURE_TYPE ft, 
        I18NFAILURE_TYPE_DEFINITION ftd
    where 
        ft.definition_id = ftd.failure_type_definition
        AND lower(ftd.name) = lower(ltrim(rtrim(p_fault_found))) 
        AND ft.for_item_group_id = p_model 
        AND ft.d_active = 1
        AND ROWNUM = 1;

    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidFaultFoundForModelId;


FUNCTION isValidCausedByForModelId(p_model NUMBER, p_fault_found VARCHAR2, p_caused_by VARCHAR2, p_business_unit_name VARCHAR2)
RETURN BOOLEAN
IS
    v_caused_by VARCHAR2(256) := NULL;
BEGIN
    select fc.id 
    INTO v_caused_by
    from FAILURE_CAUSE fc , FAILURE_CAUSE_DEFINITION fcd , 
        FAILURE_TYPE ft, FAILURE_TYPE_DEFINITION ftd
    where fc.definition_id = fcd.id 
        and lower(fcd.name) = lower(ltrim(rtrim(p_caused_by))) 
        and fc.failure_type_id = ft.id and ft.definition_id = ftd.id 
        and lower(ftd.name) = lower(ltrim(rtrim(p_fault_found))) 
        and ft.for_item_group_id = p_model 
        and fc.d_active = 1
        AND ROWNUM = 1;
    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidCausedByForModelId;

FUNCTION isValidRootCauseForModelId(p_model NUMBER, p_fault_found VARCHAR2, p_root_cause VARCHAR2, p_business_unit_name VARCHAR2)
RETURN BOOLEAN
IS
    v_root_cause VARCHAR2(256) := NULL;
BEGIN
    select frc.id 
    INTO v_root_cause
    from FAILURE_ROOT_CAUSE frc , FAILURE_ROOT_CAUSE_DEFINITION frcd ,
        FAILURE_TYPE ft, FAILURE_TYPE_DEFINITION ftd
    where frc.definition_id = frcd.id 
        and lower(frcd.name) = lower(ltrim(rtrim(p_root_cause))) 
        and frc.failure_type_id = ft.id 
        and ft.definition_id = ftd.id 
        and lower(ftd.name) = lower(ltrim(rtrim(p_fault_found))) 
        and ft.for_item_group_id = p_model 
        and frc.d_active = 1 
        and ROWNUM = 1;
    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidRootCauseForModelId;


FUNCTION isValidDelimitedValue(p_value VARCHAR2, p_delimiter VARCHAR2)
RETURN BOOLEAN
IS
  v_index         NUMBER := 1;
  v_cur_idx       NUMBER := 1;
  v_delim_length  NUMBER;
  v_value_length  NUMBER;
BEGIN
  v_delim_length := LENGTH(p_delimiter);
  v_value_length := LENGTH(p_value);

  IF v_value_length <= v_delim_length THEN
    RETURN TRUE;
  END IF;

  WHILE v_index != 0 AND v_cur_idx <= v_value_length LOOP
    v_index := INSTR(p_value, p_delimiter, v_cur_idx);
    IF v_index = v_cur_idx THEN
      RETURN FALSE;
    ELSIF v_index = v_value_length - v_delim_length + 1 THEN
      RETURN FALSE;
    ELSIF v_index > v_cur_idx THEN
      v_cur_idx := v_index + v_delim_length;
    END IF;
  END LOOP;

  RETURN TRUE;
END isValidDelimitedValue;

FUNCTION isValidReplacedIRPart(p_item_number VARCHAR2, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
  v_id         NUMBER := NULL;
BEGIN
    SELECT i.id INTO v_id
    FROM item i, item_group ig,
        config_param p,config_value v,config_param_option o
    WHERE p.name='replacedItemsOnClaimConfiguration' AND
        v.config_param=p.id AND v.config_param_option=o.id AND
        UPPER(ig.name)=UPPER(o.value) AND
        i.model=ig.id AND 
        v.business_unit_info=p_business_unit AND
        i.business_unit_info=p_business_unit AND
        p.d_active=1 AND i.d_active=1 AND
        i.owned_by in (SELECT org.id FROM party pty,organization org 
            WHERE pty.id=org.id AND pty.name='OEM') AND
        (i.item_number=p_item_number OR i.alternate_item_number=p_item_number)
        AND ROWNUM=1;

    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidReplacedIRPart;

FUNCTION isValidReplacedIRPart(p_serial_number VARCHAR2, p_item_number VARCHAR2, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
  v_id         NUMBER := NULL;
BEGIN
    SELECT i.id INTO v_id
    FROM item i, item_group ig,inventory_item ii,
        config_param p,config_value v,config_param_option o
    WHERE p.name='replacedItemsOnClaimConfiguration' AND
        v.config_param=p.id AND v.config_param_option=o.id AND
        UPPER(ig.name)=UPPER(o.value) AND
        i.model=ig.id AND 
        ii.serial_number=p_serial_number AND
        ii.of_type=i.id AND
        v.business_unit_info=p_business_unit AND
        i.business_unit_info=p_business_unit AND
        p.d_active=1 AND i.d_active=1 AND
        i.owned_by in (SELECT org.id FROM party pty,organization org 
            WHERE pty.id=org.id AND pty.name='OEM') AND
        (i.item_number=p_item_number OR i.alternate_item_number=p_item_number)
        AND ROWNUM=1;

    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidReplacedIRPart;

FUNCTION isValidInstalledIRPart(p_item_number VARCHAR2, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
  v_id         NUMBER := NULL;
Begin
    SELECT i.id INTO v_id
      FROM item i, item_group ig, item_group pg 
      WHERE
        I.Business_Unit_Info = P_Business_Unit And I.D_Active = 1 
        AND (i.item_number =p_item_number or i.alternate_item_number =p_item_number) 
        AND i.owned_by = 1 AND i.model = ig.ID 
        AND i.service_part = 1  AND (UPPER(pg.name) in ('PART'))  
        AND ig.tree_id = pg.tree_id  
        AND ig.lft >= pg.lft  
        And Ig.Rgt <= Pg.Rgt  
        AND rownum =1; 


    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidInstalledIRPart;

FUNCTION isValidAlarmCode(p_alarm_code VARCHAR2,p_product VARCHAR2, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
  v_id         NUMBER := NULL;
BEGIN       
      select COUNT(1) into  v_id from item_groups_alarm_code where alarm_code in (select id from alarm_code where  code = p_alarm_code);
      IF v_id=0 then
          RETURN TRUE;
      ELSE    
        SELECT COUNT(1) into v_id FROM DUAL WHERE p_product IN (
        select ITEM_GROUP from item_groups_alarm_code where alarm_code in (select id from alarm_code where  code = p_alarm_code));

         IF v_id >0 then     
          RETURN TRUE;
          else
            RETURN FALSE;
         END IF;
       END IF;   
    EXCEPTION 
        WHEN OTHERS THEN
        RETURN FALSE;
END isValidAlarmCode;

FUNCTION isAllowedCostCategory(p_cost_cat_code VARCHAR2,p_product VARCHAR2,p_business_unit_info VARCHAR2)
RETURN BOOLEAN
IS
  v_id         NUMBER := NULL;
BEGIN
    select COUNT(1) into  v_id from COSTCAT_APPL_PRODUCTS cp, COST_CATEGORY c,item_group g where cp.cost_category = c.id and c.code = p_cost_cat_code
      and g.id =cp.item_group  and g.business_unit_info = p_business_unit_info;
      IF v_id=0 then
          RETURN TRUE;
      ELSE    
        SELECT COUNT(1) into v_id from COSTCAT_APPL_PRODUCTS cp, COST_CATEGORY c where cp.cost_category = c.id 
        and c.code = p_cost_cat_code and cp.item_group = p_product;
         IF v_id >0 then
            RETURN TRUE;
         else
            RETURN FALSE;
         END IF;
       END IF;
    EXCEPTION 
        WHEN OTHERS THEN
        RETURN FALSE;
END isAllowedCostCategory;


FUNCTION isValidMiscPart(p_item_number VARCHAR2, p_service_provider NUMBER, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
  v_id         NUMBER := NULL;
BEGIN
   select
        distinct miscellane6_.id INTO v_id
    from
        misc_item_criteria miscellane0_ 
    inner join
        misc_item_config itemconfig1_ 
            on miscellane0_.id=itemconfig1_.for_criteria 
    inner join
        misc_item miscellane6_ 
            on itemconfig1_.miscellaneous_item=miscellane6_.id 
    left outer join
        dealer_group dealergrou2_ 
            on miscellane0_.dealer_group=dealergrou2_.id cross 
    join
        dealer_group dealergrou3_ 
    inner join
        dealers_in_group includedde4_ 
            on dealergrou3_.id=includedde4_.dealer_group 
    inner join
        service_provider servicepro5_ 
            on includedde4_.dealer=servicepro5_.id 
    inner join
        organization servicepro5_1_ 
            on servicepro5_.id=servicepro5_1_.id 
    inner join
        party servicepro5_2_ 
            on servicepro5_.id=servicepro5_2_.id cross 
    join
        misc_item miscellane7_ 
    where
        miscellane0_.business_unit_info in (
            p_business_unit
        ) 
        and miscellane0_.d_active = 1 
        and dealergrou3_.business_unit_info in (
           p_business_unit
        ) 
        and dealergrou3_.d_active = 1 
        and itemconfig1_.miscellaneous_item=miscellane7_.id 
        and (
            miscellane0_.service_provider is null 
            or miscellane0_.service_provider=p_service_provider
        ) 
        and (
            dealergrou2_.id is null 
            or dealergrou2_.tree_id=dealergrou3_.tree_id 
            and dealergrou2_.lft<=dealergrou3_.lft 
            and dealergrou3_.rgt<=dealergrou2_.rgt 
            and p_service_provider=servicepro5_.id
        ) 
        and (
            upper(miscellane7_.part_number) =upper(p_item_number)
        ) 
        and miscellane0_.active=1 ;
    RETURN TRUE;
EXCEPTION 
WHEN OTHERS THEN
BEGIN 
   SELECT i.id INTO v_id
    FROM misc_item_criteria cr, misc_item_config cfg, misc_item i
    WHERE cr.active=1 AND cr.service_provider=p_service_provider AND
        cfg.for_criteria=cr.id AND cfg.active=1 AND
        cfg.miscellaneous_item=i.id AND
        i.part_number=p_item_number AND
        i.business_unit_info=p_business_unit AND ROWNUM=1;
           RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN

    BEGIN 
        SELECT i.id INTO v_id
        FROM dealers_in_group dig,dealer_group grp,dealer_scheme_purposes sp, 
            purpose pp,misc_item_criteria cr, misc_item_config cfg, misc_item i
        WHERE 
            dig.dealer=p_service_provider AND
            dig.dealer_group = grp.id AND
            grp.scheme = sp.dealer_scheme AND
            sp.purposes = pp.id AND
            pp.name='Dealer Rates' AND
            cr.dealer_group = grp.id AND
            cr.active=1 and cfg.active=1 AND
            cfg.for_criteria=cr.id AND
            cfg.miscellaneous_item=i.id AND 
            i.part_number=p_item_number AND 
            i.business_unit_info=p_business_unit AND rownum=1;
        RETURN TRUE;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN FALSE;
     END;
     END;
END isValidMiscPart;


FUNCTION isValidCountry(p_country_code VARCHAR2)
RETURN BOOLEAN
IS
    v_id    NUMBER;
BEGIN
    SELECT id INTO v_id
    FROM country WHERE UPPER(code) = UPPER(p_country_code);
    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidCountry;

FUNCTION isValidState(p_state VARCHAR2, p_country_code VARCHAR2)
RETURN BOOLEAN
IS
    v_id    NUMBER;
BEGIN
    SELECT id INTO v_id
    FROM msa 
    WHERE UPPER(country) = UPPER(p_country_code) 
        AND UPPER(st) = UPPER(p_state) AND ROWNUM=1;
    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidState;

FUNCTION isValidCity(p_city VARCHAR2, p_state VARCHAR2, p_country_code VARCHAR2)
RETURN BOOLEAN
IS
    v_id    NUMBER;
BEGIN
    SELECT id INTO v_id
    FROM msa 
    WHERE UPPER(country) = UPPER(p_country_code) 
        AND UPPER(st) = UPPER(p_state)
        AND UPPER(city) = UPPER(p_city) AND ROWNUM=1;
    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidCity;

FUNCTION isValidZipcode(p_zipcode VARCHAR2, p_city VARCHAR2, p_state VARCHAR2, p_country_code VARCHAR2)
RETURN BOOLEAN
IS
    v_id    NUMBER;
BEGIN
    SELECT id INTO v_id
    FROM msa 
    WHERE UPPER(country) = UPPER(p_country_code) 
        AND UPPER(st) = UPPER(p_state)
        AND UPPER(city) = UPPER(p_city)
        AND UPPER(zip2) = UPPER(p_zipcode) AND ROWNUM=1;
    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidZipcode;

FUNCTION isValidCampaignCode(p_campaign_code VARCHAR2, p_dealer NUMBER, p_business_unit VARCHAR2,p_date DATE,v_service_provider_number VARCHAR2,p_serial_number VARCHAR2)
RETURN BOOLEAN
IS
    v_id    NUMBER;     
BEGIN

    SELECT c.id INTO v_id 
    FROM campaign_notification n, campaign c
    WHERE UPPER(c.business_unit_info) = UPPER(p_business_unit) 
        AND n.campaign = c.id 
        AND n.dealership = p_dealer
        AND n.notification_status = 'PENDING'
        AND p_date >= c.from_date AND p_date <= c.till_date
        AND UPPER(c.code) = UPPER(p_campaign_code)
        AND ROWNUM = 1;

    RETURN TRUE;
EXCEPTION  WHEN others THEN 
   BEGIN    
    select   distinct inventoryi0_.id as id86_ into v_id

        from
            inventory_item inventoryi0_ cross 
        join
            campaign_notification campaignno1_ cross 
        join
            service_provider servicepro2_ 
        inner join
            organization servicepro2_1_ 
                on servicepro2_.id=servicepro2_1_.id 
        inner join
            party servicepro2_2_ 
                on servicepro2_.id=servicepro2_2_.id cross 
        join
            campaign campaign3_ 
        where
            inventoryi0_.business_unit_info in (p_business_unit) 
            and inventoryi0_.d_active = 1 
            and campaignno1_.d_active = 1 
            and servicepro2_2_.d_active = 1 
            and campaignno1_.campaign=campaign3_.id 
            and campaignno1_.item=inventoryi0_.id 
            and campaignno1_.dealership=servicepro2_.id 
            and (
                servicepro2_.service_provider_number=p_dealer
                and inventoryi0_.type='STOCK' 
                or inventoryi0_.type='RETAIL'
            ) 
            and (
                campaignno1_.claim is null
            ) 
            and campaignno1_.notification_status='PENDING' 
            and campaign3_.from_date<=sysdate 
            and campaign3_.code=p_campaign_code
            and (
                upper(inventoryi0_.serial_number)=p_serial_number
            ) 
            and inventoryi0_.serialized_part=0        
          AND ROWNUM = 1;
    RETURN TRUE; 
EXCEPTION
    WHEN others THEN    
        RETURN FALSE;  
 END;
END isValidCampaignCode;

FUNCTION isValidInventoryForFieldMod(
	p_serial_number VARCHAR2, 
	p_campaign_code VARCHAR2, 
	p_dealer NUMBER, 
	p_business_unit VARCHAR2,
	p_rep_date DATE,
	p_error_code OUT VARCHAR2)
RETURN NUMBER
IS
	v_id NUMBER;
	v_date DATE;
BEGIN
	p_error_code := NULL;
	v_date := p_rep_date;
	IF v_date IS NULL THEN
		v_date := sysdate;
	END IF;
    SELECT i.id INTO v_id 
    FROM campaign_notification n,inventory_item i, campaign c
    WHERE UPPER(i.business_unit_info) = UPPER(p_business_unit) 
        AND n.item = i.id
        AND n.campaign = c.id 
        AND (n.dealership=p_dealer AND i.type='STOCK'
			OR i.type='RETAIL')
        AND n.notification_status = 'PENDING'
        AND v_date >= c.from_date and v_date <= c.till_date
        AND UPPER(i.serial_number) = UPPER(p_serial_number)
        AND UPPER(c.code) = UPPER(p_campaign_code)
		AND i.serialized_part=0
		AND i.d_active=1 AND c.d_active=1;

    RETURN v_id;  
EXCEPTION
    WHEN NO_DATA_FOUND THEN
		p_error_code := 'SN_INV';
		RETURN NULL;
	WHEN TOO_MANY_ROWS THEN
		p_error_code := 'SN_DUP';
		RETURN NULL;
END isValidInventoryForFieldMod;

FUNCTION isValidInventoryForFieldModWCN(
	p_container_number VARCHAR2, 
	p_campaign_code VARCHAR2, 
	p_dealer NUMBER, 
	p_business_unit VARCHAR2,
	p_rep_date DATE,
	p_error_code OUT VARCHAR2)
RETURN NUMBER
IS
	v_id    NUMBER;
	v_date DATE;
BEGIN
	p_error_code := NULL;
	v_date := p_rep_date;
	IF v_date IS NULL THEN
		v_date := sysdate;
	END IF;
    SELECT i.id INTO v_id 
    FROM campaign_notification n,inventory_item i, campaign c
    WHERE i.business_unit_info=p_business_unit
        AND n.item = i.id
        AND n.campaign = c.id 
        AND (n.dealership=p_dealer AND i.type='STOCK'
			OR i.type='RETAIL')
        AND n.notification_status = 'PENDING'
        AND v_date >= c.from_date and v_date <= c.till_date
        AND UPPER(i.vin_number) = UPPER(p_container_number)
        AND UPPER(c.code) = UPPER(p_campaign_code)
        AND i.serialized_part=0
		AND i.d_active=1 AND c.d_active=1;

    RETURN v_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
		p_error_code := 'CN_INV';
        RETURN NULL;
	WHEN TOO_MANY_ROWS THEN
		p_error_code := 'CN_DUP';
		RETURN NULL;
END isValidInventoryForFieldModWCN;

FUNCTION isClaimTypeAllowed(p_type VARCHAR2, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
    v_id    NUMBER;
    v_type  VARCHAR2(255);
BEGIN

    IF UPPER(p_type) IN ('MACHINE SERIALIZED','MACHINE NON SERIALIZED') THEN
        v_type := 'Machine';
    ELSIF UPPER(p_type) IN ('PARTS WITH HOST','PARTS WITHOUT HOST') THEN
        v_type := 'Parts';
    ELSE
        v_type := 'Campaign';
    END IF;

    SELECT o.id INTO v_id
    FROM config_param p,config_value v,config_param_option o
    WHERE p.d_active=1 AND p.id=v.config_param 
        AND v.d_active=1 AND v.config_param_option=o.id
        AND p.name='claimType'
        AND o.value=v_type
        AND UPPER(v.business_unit_info)=UPPER(p_business_unit)
        AND ROWNUM = 1;

    IF UPPER(p_type) IN ('MACHINE SERIALIZED','PARTS WITH HOST','FIELDMODIFICATION') THEN
        RETURN TRUE;
    ELSIF UPPER(p_type) IN ('MACHINE NON SERIALIZED','PARTS WITHOUT HOST') THEN
        IF UPPER(p_type) = 'MACHINE NON SERIALIZED' THEN
            v_type := 'nonSerializedClaimAllowed';
        ELSE
            v_type := 'partsClaimWithoutHostAllowed';
        END IF;

        SELECT o.id INTO v_id
        FROM config_param p,config_value v,config_param_option o
        WHERE p.d_active=1 AND p.id=v.config_param 
            AND v.d_active=1 AND v.config_param_option=o.id
            AND p.name=v_type
            AND o.value='true'
            AND UPPER(v.business_unit_info)=UPPER(p_business_unit)
            AND ROWNUM = 1;

        RETURN TRUE;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END isClaimTypeAllowed;

FUNCTION hasDuplicateSerializedPart
  (
    p_value_serialNo     VARCHAR2,
    p_value_partNo     VARCHAR2,
    p_delimiter VARCHAR2)
  RETURN BOOLEAN
IS
       v_serial_count                NUMBER         := 0;
       v_part_count                NUMBER         := 0;
       p_serialized_part     VARCHAR2(4000) := NULL;
       v_count                NUMBER         := 0;
BEGIN
  v_serial_count := Common_Utils.count_delimited_values(p_value_serialNo, p_delimiter);
  v_part_count := Common_Utils.count_delimited_values(p_value_partNo, p_delimiter);
   FOR i IN 1 .. v_serial_count LOOP
      p_serialized_part := getSerialNoPartNo( p_value_serialNo,p_value_partNo,p_delimiter,i);

            v_count := 0;
            FOR j IN 1 .. v_serial_count LOOP  

               IF upper(p_serialized_part)= upper(getSerialNoPartNo( p_value_serialNo,p_value_partNo,p_delimiter,j)) THEN                              
                  v_count := v_count+1;                  
                  IF v_count > 1 THEN
                    RETURN TRUE; 
                  END IF;                

               END IF;               
            END LOOP;
    END LOOP;
  RETURN FALSE;
END hasDuplicateSerializedPart;



FUNCTION getSerialNoPartNo
  (
    p_value_serialNo     VARCHAR2,
    p_value_partNo     VARCHAR2,
    p_delimiter VARCHAR2,
    p_index     NUMBER)
  RETURN VARCHAR2
IS
   p_serial_no     VARCHAR2(4000) := NULL;
   p_part_no VARCHAR2(4000) := NULL;
BEGIN
  p_serial_no := common_utils.get_delimited_value(p_value_serialNo, p_delimiter, p_index); 
  p_part_no := common_utils.get_delimited_value(p_value_partNo, p_delimiter, p_index);   
  RETURN CONCAT(p_serial_no, p_part_no);
END getSerialNoPartNo;

FUNCTION getValidCausalPart(p_item_number VARCHAR2, p_business_unit_name VARCHAR2)
RETURN VARCHAR2
IS
    v_item_id NUMBER := NULL;
    v_item_number_index NUMBER := -1;
    v_item_number VARCHAR2(255) := NULL;
BEGIN
SELECT i.item_number 
    INTO v_item_number
    FROM ITEM i, PARTY p, item_group ig, item_group pg
    WHERE ( UPPER(i.alternate_item_number) = UPPER(ltrim(rtrim(p_item_number)))  OR 
    UPPER(i.item_number) = UPPER(ltrim(rtrim(p_item_number))) )
    AND (i.business_unit_info) = (ltrim(rtrim(p_business_unit_name)))
    AND i.owned_by = p.ID
    AND p.NAME = common_utils.constant_oem_name and i.d_active = 1 
    AND i.model=ig.id 
    AND UPPER(pg.name) in (SELECT UPPER(co.value)
        FROM config_param cp, config_value cv, config_param_option co
        WHERE cp.name='causalItemsOnClaimConfiguration' 
		AND cp.id=cv.config_param
            AND cv.d_active=1
			AND cv.config_param_option=co.id
            AND (cv.business_unit_info) = (ltrim(rtrim(p_business_unit_name))))
    AND ig.tree_id=pg.tree_id
    AND ig.lft >= pg.lft
    AND ig.rgt <= pg.rgt 
    AND rownum  < 2; 
    RETURN v_item_number;
EXCEPTION 
WHEN OTHERS THEN
    RETURN NULL;
END getValidCausalPart;

FUNCTION getReplacedIRPartNumber(p_item_number VARCHAR2, p_business_unit VARCHAR2)
RETURN VARCHAR2
IS
  v_item_number         VARCHAR2(255) := NULL;
BEGIN
    SELECT i.item_number
    INTO v_item_number
    FROM item i,
      item_group ig,
      item_group pg,
      config_param p,
      config_value v,
      config_param_option o
    WHERE p.name             ='replacedItemsOnClaimConfiguration'
    AND v.config_param       =p.id
    AND v.config_param_option=o.id
    AND UPPER(ig.name)       =UPPER(o.value)
    AND i.model              =pg.id
    AND v.business_unit_info = p_business_unit
    AND i.business_unit_info =p_business_unit
    AND p.d_active           =1
    AND i.d_active           =1
    AND pg.tree_id           = ig.tree_id
    AND pg.lft              >= ig.lft
    AND pg.rgt              <= ig.rgt
    AND i.owned_by          IN
      (SELECT org.id
      FROM party pty,
        organization org
      WHERE pty.id=org.id
      AND pty.name='OEM'
      )
    AND (i.item_number        =p_item_number
    OR i.alternate_item_number=p_item_number)
    AND rownum                < 2;      
    RETURN v_item_number;
EXCEPTION 
    WHEN OTHERS THEN
        RETURN NULL;
END getReplacedIRPartNumber;

FUNCTION getInstalledIRPart(p_item_number VARCHAR2, p_business_unit VARCHAR2)
RETURN VARCHAR2
IS
  v_item_number         VARCHAR2(255) := NULL;
Begin
    SELECT i.item_number INTO v_item_number
      FROM item i, item_group ig, item_group pg 
      WHERE
        I.Business_Unit_Info = P_Business_Unit And I.D_Active = 1 
        AND (i.item_number =p_item_number or i.alternate_item_number =p_item_number) 
        AND i.owned_by = 1 AND i.model = ig.ID 
        AND i.service_part = 1  
        AND (UPPER(pg.name) in (SELECT t3.value 
FROM config_param t1 ,
  config_value t2 ,
  config_param_option t3
WHERE t1.name              = 'replacedItemsOnClaimConfiguration'
AND t2.config_param        = t1.id
AND t2.config_param_option = t3.id
AND t2.d_active            = 1
AND t2.active              = 1
AND t2.business_unit_info  = P_Business_Unit ))  
        AND ig.tree_id = pg.tree_id  
        AND ig.lft >= pg.lft  
        And Ig.Rgt <= Pg.Rgt 
        AND rownum  <2;        
    RETURN v_item_number;

EXCEPTION 
    WHEN OTHERS THEN
        RETURN NULL;
END getInstalledIRPart;

FUNCTION getOwnershipForInventory(p_inv NUMBER)
RETURN NUMBER
IS
	v_owner NUMBER;
BEGIN
	SELECT it.owner_ship INTO v_owner
	FROM inventory_transaction it
	WHERE d_active=1 AND transacted_item=p_inv
		AND transaction_order=(
			SELECT MAX(transaction_order) FROM inventory_transaction t 
			WHERE t.transacted_item=p_inv AND t.d_active=1);

	RETURN v_owner;
EXCEPTION
	WHEN OTHERS THEN
		RETURN NULL;
END getOwnershipForInventory;

FUNCTION getServiceProviderType(p_service_provider NUMBER)
RETURN VARCHAR2
IS
	v_cust_type VARCHAR2(20);
BEGIN
	SELECT CASE WHEN d.id IS NOT NULL THEN 'Dealer'
		WHEN dc.id IS NOT NULL THEN 'DirectCustomer'
		WHEN ic.id IS NOT NULL THEN 'InterCompany'
		WHEN na.id IS NOT NULL THEN 'NationalAccount'
		WHEN oem.id IS NOT NULL THEN 'OEM' END
	INTO v_cust_type
	FROM party sp
	LEFT OUTER JOIN dealership d ON d.id=sp.id
	LEFT OUTER JOIN direct_customer dc ON dc.id=sp.id
	LEFT OUTER JOIN inter_company ic ON ic.id=sp.id
	LEFT OUTER JOIN national_account na ON na.id=sp.id
	LEFT OUTER JOIN original_equip_manufacturer oem ON oem.id=sp.id
	WHERE sp.id=p_service_provider AND sp.d_active=1;

	RETURN v_cust_type;
EXCEPTION
	WHEN OTHERS THEN
		RETURN NULL;
END getServiceProviderType;

FUNCTION isAllowedCustomerTypeForSearch(
	p_cust_type VARCHAR2,
	p_bu VARCHAR2)
RETURN BOOLEAN
IS
	v_count NUMBER;
BEGIN
	SELECT COUNT(cp.id) INTO v_count
	FROM config_param cp, config_value cv, config_param_option po
	WHERE cp.name='wntyConfigCustomerTypesAllowedinSearchResult'
		AND cv.config_param=cp.id
		AND cv.business_unit_info=p_bu and cv.d_active=1
		AND po.id=cv.config_param_option
		AND UPPER(po.value)=UPPER(p_cust_type);
	IF v_count > 0 THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;
END isAllowedCustomerTypeForSearch;

FUNCTION isValidMachineSerialNumber (
	p_serial_number VARCHAR2, 
	p_business_unit_name VARCHAR2,
	p_model VARCHAR2,
	p_dealer_id NUMBER,
	p_error_code OUT VARCHAR2)
RETURN NUMBER
IS
	v_inventory NUMBER := NULL;
	v_type VARCHAR2(10);
	v_owner NUMBER;
	v_cust_type VARCHAR2(20);
BEGIN
p_error_code := NULL;
BEGIN
	SELECT ID,type INTO v_inventory, v_type
	FROM inventory_item 
	WHERE lower(serial_number) = lower(p_serial_number)
		AND business_unit_info = p_business_unit_name
		AND d_active=1 AND serialized_part = 0;
EXCEPTION 
	WHEN NO_DATA_FOUND THEN
		p_error_code:='SN_INV';
	WHEN TOO_MANY_ROWS THEN
		IF p_model IS NULL THEN
			p_error_code:='SN_DUP_M_EMP';
		ELSE
		BEGIN
			SELECT inv.id,inv.type INTO v_inventory,v_type
			FROM inventory_item inv, item i, item_group m
			WHERE LOWER(inv.serial_number) = LOWER(p_serial_number)
			AND inv.business_unit_info = p_business_unit_name
			AND inv.d_active=1 AND inv.serialized_part = 0
			AND inv.of_type = i.id AND i.model = m.id 
			AND m.item_group_type = 'MODEL'
			AND LOWER(m.name) = LOWER(p_model)
			AND i.d_active = 1 AND m.d_active = 1;
		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				p_error_code:='SN_DUP_M_INV';
			WHEN TOO_MANY_ROWS THEN
				p_error_code:='SN_M_DUP';
		END;
		END IF;
END;
IF v_type = 'STOCK' THEN
	v_owner := getOwnershipForInventory(v_inventory);
	IF v_owner != p_dealer_id THEN
		v_cust_type := getServiceProviderType(v_owner);
		IF NOT isAllowedCustomerTypeForSearch(v_cust_type, p_business_unit_name) THEN
			p_error_code:='SN_CUST_INV';
		END IF;
	END IF;
END IF;
RETURN v_inventory;
END isValidMachineSerialNumber;

FUNCTION getValidPartSerialNumber(
	p_serial_number VARCHAR2, 
	p_business_unit_name VARCHAR2)
RETURN NUMBER
IS
	v_item_id NUMBER := NULL;  
BEGIN
	SELECT i.ID INTO v_item_id
	FROM INVENTORY_ITEM i
	WHERE lower(i.serial_number) = lower(p_serial_number)
	AND i.business_unit_info = p_business_unit_name
	AND i.d_active = 1 AND i.serialized_part=1
	AND i.source='MAJORCOMPREGISTRATION';
	RETURN v_item_id;
EXCEPTION 
	WHEN OTHERS THEN
		RETURN NULL;
END getValidPartSerialNumber;

FUNCTION isValidItemForNonSerialized (
	p_item_number VARCHAR2,
	p_bu VARCHAR2,
	p_error_code OUT VARCHAR2)
RETURN NUMBER
IS
	v_item NUMBER;
BEGIN
	p_error_code := NULL;
	SELECT i.id INTO v_item
	FROM item i
	WHERE (UPPER(i.item_number)=UPPER(p_item_number)
			OR(UPPER(i.item_number)!=UPPER(p_item_number)
				AND UPPER(i.alternate_item_number)=UPPER(p_item_number)))
		AND i.d_active=1 AND i.business_unit_info=p_bu
		AND i.item_type='MACHINE' AND i.owned_by=1;
	RETURN v_item;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		p_error_code := 'IT_INV';
		RETURN NULL;
	WHEN TOO_MANY_ROWS THEN
		p_error_code := 'IT_DUP';
		RETURN NULL;
END isValidItemForNonSerialized;

FUNCTION isValidModelForNonSerialized (
	p_model_name VARCHAR2,
	p_bu VARCHAR2,
	p_error_code OUT VARCHAR2)
RETURN NUMBER
IS
	v_model NUMBER;
BEGIN
	p_error_code := NULL;
	SELECT m.id INTO v_model
	FROM item_group m,item_group p
	WHERE m.lft > p.lft AND m.rgt <= p.rgt
		AND UPPER(m.name)=UPPER(p_model_name)
		AND m.item_group_type='MODEL'
		AND p.item_group_type='PRODUCT TYPE'
		AND UPPER(p.name)='MACHINE'
		AND m.d_active=1 AND p.d_active=1
		AND m.business_unit_info=p_bu
		AND p.business_unit_info=p_bu;
	RETURN v_model;
EXCEPTION 
	WHEN NO_DATA_FOUND THEN
		p_error_code := 'M_INV';
		RETURN NULL;
	WHEN TOO_MANY_ROWS THEN
		p_error_code := 'M_DUP';
		RETURN NULL;
END isValidModelForNonSerialized;

FUNCTION isValidItemForPartsClaim(
	p_item_number VARCHAR2,
	p_bu VARCHAR2,
	p_error_code OUT VARCHAR2)
RETURN NUMBER
IS
	v_item NUMBER;
BEGIN
	p_error_code := NULL;
	SELECT i.id INTO v_item
	FROM item i, item_group ig, item_group pg,
	config_param cp, config_value cv, config_param_option po
	WHERE (UPPER(i.item_number)=UPPER(p_item_number)
			OR(UPPER(i.item_number)!=UPPER(p_item_number)
				AND UPPER(i.alternate_item_number)=UPPER(p_item_number)))
		AND i.d_active=1 AND i.business_unit_info=p_bu
		AND i.model=ig.id AND i.owned_by=1
		AND ig.tree_id=pg.tree_id 
		AND ig.lft>=pg.lft AND ig.rgt<=pg.rgt
		AND UPPER(pg.name)=UPPER(po.value)
		AND po.id=cv.config_param_option
		AND cv.business_unit_info=p_bu and cv.d_active=1
		AND cv.config_param=cp.id
		AND cp.name='itemTypeAllowedForPartsClaim';
	RETURN v_item;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		p_error_code := 'PRT_INV';
		RETURN NULL;
	WHEN TOO_MANY_ROWS THEN
		p_error_code := 'PRT_DUP';
		RETURN NULL;
END isValidItemForPartsClaim;

END COMMON_VALIDATION_UTILS;
/
commit
/
create or replace
PROCEDURE                 UPLOAD_DRAFT_CLAIM_VALIDATION AS
CURSOR ALL_REC IS
	SELECT * FROM STG_DRAFT_CLAIM
	WHERE NVL(ERROR_STATUS,'N') = 'N' -- AND

		 ORDER BY ID ASC;

    v_loop_count            NUMBER         := 0;
    v_success_count         NUMBER         := 0;
    v_error_count           NUMBER         := 0;
    v_count                 NUMBER         := 0;
    v_count2                NUMBER         := 0;
    v_file_upload_mgt_id    NUMBER         := 0;
    v_number_temp           NUMBER         := 0;
    isFaultFoundValid       BOOLEAN        := FALSE;
    v_error                 VARCHAR2(4000) := NULL;
    v_error_code            VARCHAR2(4000) := NULL;
    v_model                 NUMBER := NULL;
	v_causal_part           VARCHAR2(255) := NULL;
	v_replaced_parts        VARCHAR2(255) := NULL;
	v_installed_parts       VARCHAR2(255) := NULL;
    v_temp_part_number      VARCHAR2(255) := NULL;
    v_product               NUMBER := NULL;
    v_machine_serial_number VARCHAR2(4000) := NULL;
    v_replaced_part      NUMBER := NULL;
    v_flag                  BOOLEAN := FALSE;
    v_valid_bu              BOOLEAN;
    v_valid_fault_found     BOOLEAN := FALSE;
    v_valid_campaign_code   BOOLEAN := FALSE;
    v_user_locale           VARCHAR2(255) := NULL;
    v_dealer                VARCHAR2(255) := NULL;
    v_dealer_id             NUMBER := NULL;
    v_bu_name               VARCHAR2(255) := NULL;
    v_smr_reason_id         VARCHAR2(255) := NULL;
    v_service_provider      NUMBER := NULL;
    v_service_provider_number  VARCHAR2(255) := NULL;
    v_delimiter             VARCHAR2(10) := '#$#';
    v_fault_code            VARCHAR2(255);
    v_job_codes_delimited   VARCHAR2(255);
    v_job_code              VARCHAR2(255);
    v_fault_found           VARCHAR2(255);    
    v_item_number           VARCHAR2(255) := NULL;   
    v_competitor_model_id   NUMBER := NULL;
    v_ac_input      		    NUMBER         := 0;
    v_id                    NUMBER := NULL;   
    v_repairdate        DATE := NULL;
    v_installeddate      VARCHAR2(255) := NULL;
    v_failuredate       VARCHAR2(255) := NULL;
    v_serial_number       VARCHAR2(20) := NULL;
	v_serialized BOOLEAN;
	v_part_installed BOOLEAN;
	v_part_installed_on_tktsa BOOLEAN;
	v_bu_config_display_item BOOLEAN := FALSE;
	v_err VARCHAR2(20);
	v_inv NUMBER;
	v_item NUMBER;
	v_part_serial NUMBER;
	v_part NUMBER;
	v_model_id NUMBER;

BEGIN


    BEGIN
    SELECT u.locale, u.login, f.business_unit_info, o.id INTO v_user_locale, v_dealer, v_bu_name, v_dealer_id
    FROM org_user u,file_upload_mgt f,org_user_belongs_to_orgs orgu,organization o
    WHERE u.id = f.uploaded_by 
        and orgu.org_user=u.id and orgu.belongs_to_organizations=o.id AND f.id=(SELECT file_upload_mgt_id FROM stg_draft_claim WHERE rownum = 1);
       /* SELECT u.locale, u.login, f.business_unit_info, u.belongs_to_organization  INTO v_user_locale, v_dealer, v_bu_name, v_dealer_id
        FROM org_user u,file_upload_mgt f
        WHERE u.id = f.uploaded_by AND f.id = 
            (SELECT file_upload_mgt_id FROM stg_draft_claim WHERE rownum = 1);*/
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            v_user_locale := 'en_US';
    END;


    BEGIN
        SELECT id, service_provider_number 
		INTO v_service_provider,v_service_provider_number 
        FROM service_provider WHERE id=v_dealer_id;
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            NULL;
    END;
	
	IF common_validation_utils.isConfigParamSet('isItemNumberDisplayRequired', v_bu_name) THEN
		v_bu_config_display_item := TRUE;
	END IF;

  FOR EACH_REC IN ALL_REC
  LOOP

    v_error_code := '';
    v_model := NULL;
	v_causal_part := NULL;
	v_replaced_parts := NULL;
	v_installed_parts := NULL;
    v_product := NULL;
    v_valid_bu := FALSE;
    v_valid_fault_found := FALSE;
    v_job_codes_delimited := NULL;
    v_valid_campaign_code := FALSE;
    v_smr_reason_id := NULL;
    v_fault_code := NULL;
    v_job_code := NULL;
    v_fault_found := NULL;
	v_serial_number := ltrim(rtrim(each_rec.serial_number));
	v_err := NULL;
	v_inv := NULL;
	v_item := NULL;
	v_part_serial := NULL;
	v_part := NULL;
	v_model_id := NULL;
	v_machine_serial_number := NULL;
	v_item_number := NULL;
	v_competitor_model_id := NULL;
	v_serialized := TRUE;
	v_part_installed := FALSE;
	v_part_installed_on_tktsa := FALSE;
	v_repairdate := NULL;
	v_failuredate := NULL;
	v_installeddate := NULL;

	IF EACH_REC.REPAIR_DATE IS NOT NULL AND 
		COMMON_VALIDATION_UTILS.isValidDate(each_rec.REPAIR_DATE, 'YYYY-MM-DD') THEN
		v_repairdate := TO_DATE(each_rec.repair_date,'YYYY-MM-DD');
	ELSIF EACH_REC.REPAIR_DATE IS NOT NULL AND 
		COMMON_VALIDATION_UTILS.isValidDate(each_rec.REPAIR_DATE, 'YYYYMMDD') THEN
		v_repairdate := TO_DATE(each_rec.repair_date,'YYYYMMDD');
	ELSE
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC018');
	END IF;

	IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('FIELDMODIFICATION') THEN
		IF EACH_REC.FAILURE_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.FAILURE_DATE, 'YYYY-MM-DD') THEN
			v_failuredate := TO_DATE(each_rec.FAILURE_DATE,'YYYY-MM-DD');
		ELSIF EACH_REC.FAILURE_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.FAILURE_DATE, 'YYYYMMDD') THEN
			v_failuredate := TO_DATE(each_rec.FAILURE_DATE,'YYYYMMDD');
		ELSE
			v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC019');
		END IF;
	END IF;

	IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE NON SERIALIZED', 'PARTS WITH HOST') THEN
		IF EACH_REC.INSTALLATION_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.INSTALLATION_DATE, 'YYYY-MM-DD') THEN
			v_installeddate := TO_DATE(each_rec.INSTALLATION_DATE,'YYYY-MM-DD');
		ELSIF EACH_REC.INSTALLATION_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.INSTALLATION_DATE, 'YYYYMMDD') THEN
			v_installeddate := TO_DATE(each_rec.INSTALLATION_DATE,'YYYYMMDD');
		ELSE
			v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC020');
		END IF;
	END IF;


    BEGIN
        IF v_bu_name IS NULL OR lower(v_bu_name) != lower(each_rec.business_unit_name) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC001');
        ELSIF NOT (COMMON_VALIDATION_UTILS.isUserBelongsToBU(v_bu_name,v_dealer)) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC044');
        ELSE
            v_valid_bu := TRUE;
        END IF;
	END;

	IF EACH_REC.UNIQUE_IDENTIFIER IS NULL THEN
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC002');
	END IF;

	BEGIN
        IF each_rec.claim_type IS NULL OR UPPER(each_rec.claim_type) NOT IN 
            ('MACHINE SERIALIZED', 'MACHINE NON SERIALIZED', 'PARTS WITH HOST', 
            'PARTS WITHOUT HOST', 'FIELDMODIFICATION')
        THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC003');
        ELSIF v_valid_bu = TRUE AND NOT common_validation_utils.isClaimTypeAllowed(each_rec.claim_type, v_bu_name) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC066');
        END IF;
	END;


    IF UPPER(each_rec.claim_type) IN ('FIELDMODIFICATION') THEN
        IF each_rec.campaign_code IS NULL THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC026');
        ELSIF NOT common_validation_utils.isValidCampaignCode(each_rec.campaign_code, v_service_provider, v_bu_name,v_repairdate,v_service_provider_number,v_serial_number) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC065');
        ELSE
            v_valid_campaign_code := TRUE;
        END IF;
    END IF;

	IF UPPER(each_rec.is_serialized) = 'N' THEN
		v_serialized := FALSE;
	END IF;
	IF UPPER(each_rec.is_part_installed) = 'Y' THEN
		v_part_installed := TRUE;
	END IF;
	IF UPPER(each_rec.is_part_installed_on_tktsa) = 'Y' THEN
		v_part_installed_on_tktsa := TRUE;
	END IF;

	IF UPPER(each_rec.claim_type) = 'PARTS WITHOUT HOST' THEN
		v_serialized := NULL;
		v_part_installed_on_tktsa := NULL;
	ELSIF UPPER(each_rec.claim_type) in ('FIELDMODIFICATION',
		'MACHINE SERIALIZED','MACHINE NON SERIALIZED') THEN
		v_part_installed := NULL;
		v_part_installed_on_tktsa := NULL;
	ELSIF UPPER(each_rec.claim_type) = 'PARTS WITH HOST' AND v_serialized THEN
		v_part_installed_on_tktsa := NULL;
	END IF;

	IF NOT v_serialized AND UPPER(each_rec.claim_type) IN 
			('MACHINE SERIALIZED','FIELDMODIFICATION') THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC101');
	ELSIF v_serialized AND UPPER(each_rec.claim_type) IN 
			('MACHINE NON SERIALIZED') THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC107');
	END IF;
	IF UPPER(each_rec.claim_type) = 'PARTS WITH HOST' 
			AND NOT v_part_installed THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC102');
	END IF;
	IF UPPER(each_rec.claim_type) = 'PARTS WITHOUT HOST' 
			AND v_part_installed THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC103');
	END IF;

	IF UPPER(each_rec.claim_type) IN ('PARTS WITHOUT HOST','PARTS WITH HOST') THEN
		IF each_rec.part_serial_number IS NOT NULL THEN
			v_part_serial := common_validation_utils.getValidPartSerialNumber(
								each_rec.part_serial_number, v_bu_name);
			IF v_part_serial IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC073_PS');
			ELSE
			SELECT of_type INTO v_part 
			FROM inventory_item WHERE id=v_part_serial;
			BEGIN
				select id,serial_number into v_inv,v_machine_serial_number 
				from inventory_item where id in ( 
					select part_of from inventory_item_composition  
					where part = v_part_serial);
			EXCEPTION 
				WHEN NO_DATA_FOUND THEN
					NULL;
			END;
			END IF;
		ELSIF each_rec.part_item_number IS NOT NULL THEN
			v_part := common_validation_utils.isValidItemForPartsClaim(
								each_rec.PART_ITEM_NUMBER, v_bu_name,v_err);
			IF v_err IS NOT NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_'||v_err);
				v_err := NULL;
			END IF;
		ELSE
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC014');
		END IF;
	END IF;

	IF v_machine_serial_number IS NOT NULL THEN
		IF UPPER(each_rec.claim_type)='PARTS WITHOUT HOST' THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC104');
		ELSIF NOT v_serialized THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC105');
		ELSIF v_serial_number IS NULL THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC004');
		ELSIF UPPER(v_serial_number) != UPPER(v_machine_serial_number) THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_90');
		END IF;
	ELSIF v_serialized THEN
		IF v_serial_number IS NOT NULL THEN
			IF UPPER(each_rec.claim_type)='FIELDMODIFICATION' THEN
				v_inv := common_validation_utils.isValidInventoryForFieldMod(
							v_serial_number,each_rec.campaign_code,
							v_service_provider,v_bu_name,v_repairdate,v_err);
			ELSE
				v_inv := common_validation_utils.isValidMachineSerialNumber(
								v_serial_number, v_bu_name, each_rec.model_number,
								v_service_provider, v_err);
			END IF;
		ELSIF each_rec.container_number IS NOT NULL THEN
			IF UPPER(each_rec.claim_type)='FIELDMODIFICATION' THEN
				v_inv := common_validation_utils.isValidInventoryForFieldModWCN(
							each_rec.container_number,each_rec.campaign_code,
							v_service_provider,v_bu_name,v_repairdate,v_err);
			ELSE 
				v_inv := common_validation_utils.isValidInventoryWithConNum(
								each_rec.container_number, v_bu_name,v_err);
			END IF;
		ELSE
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC004');
		END IF;
		IF v_err IS NOT NULL THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_'||v_err);
			v_err := NULL;
			v_inv := NULL;
		END IF;
	ELSIF NOT v_serialized THEN
		IF v_part_installed AND NOT v_part_installed_on_tktsa THEN
			IF each_rec.competitor_model IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC106');
			ELSE
				v_competitor_model_id := common_validation_utils.getValidCompetitorModelId(
					each_rec.competitor_model, v_user_locale, v_bu_name);
				IF v_competitor_model_id IS NULL THEN
					v_error_code := common_utils.addErrorMessage(v_error_code,'DC071_CM');
				END IF;
			END IF;
		ELSIF (v_part_installed AND v_part_installed_on_tktsa)
				OR v_part_installed IS NULL THEN
			IF v_bu_config_display_item THEN
				IF each_rec.item_number IS NULL THEN
					v_error_code := common_utils.addErrorMessage(v_error_code,'DC011');
				ELSE 
					v_item := common_validation_utils.isValidItemForNonSerialized(
									each_rec.item_number,v_bu_name,v_err);
				END IF;
			ELSIF each_rec.model_number IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code,'DC009');
			ELSE
				v_model_id := common_validation_utils.isValidModelForNonSerialized(
									each_rec.model_number, v_bu_name, v_err);
			END IF;
			IF v_err IS NOT NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_'||v_err);
				v_err := NULL;
			END IF;
		END IF;
	END IF;

	IF v_model_id IS NOT NULL THEN
		v_model := v_model_id;
	ELSIF v_item IS NOT NULL THEN
		SELECT m.id INTO v_model
		FROM item i,item_group m
		WHERE i.id=v_item AND i.model=m.id;
	ELSIF v_inv IS NOT NULL THEN
		SELECT m.id,CASE WHEN p1.item_group_type='PRODUCT' THEN p1.id
			ELSE p2.id END INTO v_model,v_product
		FROM inventory_item ii,item i,item_group m,item_group p1,item_group p2
		WHERE ii.id=v_inv AND ii.of_type=i.id and i.model=m.id
			AND m.is_part_of=p1.id AND p1.is_part_of=p2.id;
	END IF;


     BEGIN
       IF EACH_REC.ALARM_CODES IS NOT NULL THEN          
            v_ac_input := Common_Utils.count_delimited_values(each_rec.ALARM_CODES, ',');
            FOR i IN 1 .. v_ac_input LOOP

                IF  v_product IS NOT NULL THEN       
                      IF NOT common_validation_utils.isValidAlarmCode(
                              common_utils.get_delimited_value(each_rec.ALARM_CODES, ',', i),v_product, v_bu_name)
                      THEN
                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC067_IAC');
                          EXIT;
                      END IF;
                ELSE 
                     select alarm_code.id into v_id from alarm_code where  lower(trim(code)) = lower(trim(common_utils.get_delimited_value(each_rec.ALARM_CODES, ',', i)));

                END IF;
            END LOOP;
        END IF; 
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC067_IAC');
     END;




  IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE SERIALIZED', 'PARTS WITH HOST', 'FIELDMODIFICATION')
  THEN
    IF EACH_REC.HOURS_IN_SERVICE IS NULL
    THEN
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC016');
    END IF;

    IF EACH_REC.HOURS_IN_SERVICE IS NOT NULL AND (EACH_REC.HOURS_IN_SERVICE < 0 OR EACH_REC.HOURS_IN_SERVICE > 999999)
    THEN
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC017');
    END IF;
  END IF;

	BEGIN
		 IF EACH_REC.WORK_ORDER_NUMBER IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC021');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.CONDITIONS_FOUND IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC022');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.WORK_PERFORMED IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC023');
		 END IF;
	END;




	BEGIN
    IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST', 'FIELDMODIFICATION') THEN
        IF (EACH_REC.CAUSAL_PART IS NOT NULL) THEN
            v_causal_part := COMMON_VALIDATION_UTILS.getValidCausalPart(EACH_REC.CAUSAL_PART, v_bu_name);
        END IF;
        IF EACH_REC.CAUSAL_PART IS NULL OR (EACH_REC.CAUSAL_PART IS NOT NULL AND v_causal_part IS NULL) THEN
		    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC025');
        ELSIF v_causal_part != each_rec.causal_part THEN
            UPDATE stg_draft_claim SET causal_part=v_causal_part WHERE id=each_rec.id;
		END IF;
    END IF;
	END;







    IF UPPER(each_rec.claim_type) NOT IN ('PARTS WITHOUT HOST') AND 
            each_rec.replaced_ir_parts IS NOT NULL THEN

         IF  each_rec.INSTALLED_IR_PARTS IS NULL THEN            
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045_ADD_INP');         
         END IF;    

        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_ir_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045');
            v_flag := FALSE;
        ELSE
            v_count := Common_Utils.count_delimited_values(each_rec.replaced_ir_parts, v_delimiter);
             IF  each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NOT NULL THEN            
                IF NOT common_validation_utils.isValidDelimitedValue(each_rec.REPLACED_IR_PARTS_SERIAL_NUM, v_delimiter) THEN
                  v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045_SE');
                  v_flag := FALSE;
                ELSE
                  v_count2 := Common_Utils.count_delimited_values(each_rec.REPLACED_IR_PARTS_SERIAL_NUM, v_delimiter); 
                END IF; 
             END IF;
            IF v_flag = TRUE THEN


            IF UPPER(each_rec.claim_type) IN ('PARTS WITH HOST') AND common_validation_utils.hasDuplicateSerializedPart(each_rec.REPLACED_IR_PARTS_SERIAL_NUM,each_rec.replaced_ir_parts, v_delimiter) THEN
                  v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_98');          
            END IF;

            FOR i IN 1 .. v_count LOOP
                IF i <= v_count2 THEN
                IF UPPER(each_rec.claim_type) IN ('PARTS WITHOUT HOST') THEN

                            IF UPPER(each_rec.PART_SERIAL_NUMBER) IS NOT NULL THEN
                               IF each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NULL THEN   
                                        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Removed part should be serialized replaced part');                                         
                               ELSE
                                                     IF each_rec.PART_SERIAL_NUMBER != each_rec.REPLACED_IR_PARTS_SERIAL_NUM THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced part serial number should be same as part serial number');

                                                     ELSIF common_utils.get_delimited_value(each_rec.replaced_ir_parts_quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Serialized replaced part quatity should be one');
                                                     END IF;

                               END IF;  
                            ELSIF UPPER(each_rec.Part_item_Number) IS NOT NULL THEN
                                 IF each_rec.Replaced_IR_Parts IS NOT NULL THEN
                                                  IF common_utils.get_delimited_value(each_rec.Replaced_IR_Parts, v_delimiter, i) != each_rec.Part_item_Number THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Please add correct replaced part');
                                                   END IF;
                                  END IF;                         
                                  IF UPPER(each_rec.REPLACED_IR_PARTS_QUANTITY) IS NOT NULL THEN
                                                  IF common_utils.get_delimited_value(each_rec.replaced_ir_parts_quantity, v_delimiter, i) !=1 THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced part quantity should be one only');   
                                                   END IF;
                                  END IF;                 
                             END IF;
                END IF; 
                        IF UPPER(each_rec.claim_type) IN ('PARTS WITH HOST') THEN

                            IF UPPER(each_rec.PART_SERIAL_NUMBER) IS NOT NULL THEN
                                   IF common_utils.get_delimited_value(each_rec.replaced_ir_parts_quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_79');
                                   END IF;

                                   IF each_rec.Is_Serialized in ('Y') THEN

                                          IF each_rec.Is_Part_Installed_on_TKTSA in ('Y') THEN                                         

                                              IF each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                    select count(*) into v_replaced_part from  Inventory_Item_Composition where part_of in (select id from inventory_item where serial_number = v_machine_serial_number ) and part in (select id from inventory_item where serial_number=common_utils.get_delimited_value(each_rec.REPLACED_IR_PARTS_SERIAL_NUM, v_delimiter, i) );   

                                                     IF v_replaced_part != 1 THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_78');                                                     
                                                     END IF;
                                               END IF;


                                               IF each_rec.Installed_IR_Parts_Serial_Num IS NOT NULL THEN
                                                    IF common_utils.get_delimited_value(each_rec.Installed_IR_Parts_Quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_80');
                                                     END IF;
                                               END IF;

                                           ELSE 

                                              IF each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NOT NULL AND common_utils.get_delimited_value(each_rec.REPLACED_IR_PARTS_SERIAL_NUM, v_delimiter, i) != each_rec.Part_Serial_Number THEN
                                                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_81');
                                              END IF;   

                                              IF each_rec.Installed_IR_Parts_Serial_Num IS NOT NULL AND common_utils.get_delimited_value(each_rec.Installed_IR_Parts_Quantity, v_delimiter, i) !=1 THEN

                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_80');

                                               END IF;
                                           END IF;

                                    ELSE
                                             IF each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NOT NULL AND common_utils.get_delimited_value(each_rec.REPLACED_IR_PARTS_SERIAL_NUM, v_delimiter, i) != each_rec.Part_Serial_Number THEN
                                                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_81');
                                              END IF;    

                                              IF each_rec.Installed_IR_Parts_Serial_Num IS NOT NULL AND common_utils.get_delimited_value(each_rec.Installed_IR_Parts_Quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_80');
                                               END IF;  
                                    END IF;


                            ELSIF UPPER(each_rec.Part_item_Number) IS NOT NULL THEN

                                      IF each_rec.Is_Serialized in ('Y') THEN    

                                            IF each_rec.Is_Part_Installed_on_TKTSA in ('Y') THEN
                                                  IF each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_82');
                                                   END IF;        

                                            ELSE
                                                  IF each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_82'); 
                                                  END IF;
                                                  IF each_rec.Replaced_IR_Parts IS NOT NULL AND UPPER(each_rec.Part_item_Number) != common_utils.get_delimited_value(each_rec.Replaced_IR_Parts, v_delimiter, 1) THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_85');
                                                   END IF;
                                                   IF Common_Utils.count_delimited_values(each_rec.Replaced_IR_Parts, v_delimiter)  !=1 THEN
                                                      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Should not add more than one part');
                                                   END IF;

                                                   IF each_rec.Installed_IR_Parts_Serial_Num is NOT NULL THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_87');
                                                   END IF;  
                                             END IF;

                                       ELSE

                                              IF each_rec.REPLACED_IR_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_84');
                                               END IF;

                                              IF each_rec.Replaced_IR_Parts IS NOT NULL AND UPPER(each_rec.Part_item_Number) != common_utils.get_delimited_value(each_rec.Replaced_IR_Parts, v_delimiter, i) THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_85');
                                              END IF;

                                              IF Common_Utils.count_delimited_values(each_rec.Replaced_IR_Parts, v_delimiter)  !=1 THEN
                                                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Should not add more than one part');
                                              END IF;

                                              IF each_rec.Installed_IR_Parts_Serial_Num is NOT NULL THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_87');
                                              END IF;  
                                       END IF;
                            END IF;   
                         END IF; 
                        IF NOT common_validation_utils.isValidReplacedIRPart(common_utils.get_delimited_value(each_rec.REPLACED_IR_PARTS_SERIAL_NUM, v_delimiter, i),
                              common_utils.get_delimited_value(each_rec.replaced_ir_parts, v_delimiter, i), v_bu_name)
                        THEN
                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC046');
                          EXIT;
                        END IF;

                ELSE          

                              IF UPPER(each_rec.Part_item_Number) IS NOT NULL THEN
                                   IF each_rec.Is_Part_Installed_on_TKTSA in ('N') OR each_rec.Is_Serialized in ('N') THEN                                         

                                        IF each_rec.Replaced_IR_Parts IS NOT NULL AND UPPER(each_rec.Part_item_Number) != common_utils.get_delimited_value(each_rec.Replaced_IR_Parts, v_delimiter, i) THEN
                                                                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_88');
                                        END IF;

                                        IF Common_Utils.count_delimited_values(each_rec.Replaced_IR_Parts, v_delimiter)  !=1 THEN
                                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Should not add more than one part');
                                        END IF;

                                        IF each_rec.Installed_IR_Parts_Serial_Num is NOT NULL THEN
                                                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_87');
                                        END IF;  
                                    END IF;
                              END IF;

                      v_temp_part_number := common_validation_utils.getReplacedIRPartNumber(
		                        common_utils.get_delimited_value(each_rec.replaced_ir_parts, v_delimiter, i), v_bu_name);
		                IF v_temp_part_number IS NULL
		                THEN
		                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC046');
		                    v_replaced_parts := NULL;
		                    EXIT;
		                END IF;
		                IF i > 1 THEN
		                    v_replaced_parts := v_replaced_parts || v_delimiter;
		                END IF;			  
						v_replaced_parts := v_replaced_parts || v_temp_part_number;
                END IF;     

            END LOOP;
			IF v_replaced_parts IS NOT NULL AND v_replaced_parts != each_rec.replaced_ir_parts THEN
                UPDATE stg_draft_claim SET replaced_ir_parts=v_replaced_parts WHERE id=each_rec.id;
            ENd IF;
            END IF;   
        END IF;

        IF each_rec.replaced_ir_parts_quantity IS NULL THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC027');
        ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_ir_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC047');
        ELSIF v_flag = TRUE AND v_count != Common_Utils.count_delimited_values(each_rec.replaced_ir_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC048');
        ELSIF v_flag = TRUE THEN
            FOR i IN 1 .. v_count LOOP
                IF NOT common_utils.isPositiveInteger(
                        common_utils.get_delimited_value(each_rec.replaced_ir_parts_quantity, v_delimiter, i)) 
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC054');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
    END IF; 




    IF UPPER(each_rec.claim_type) NOT IN ('PARTS WITHOUT HOST') AND 
            each_rec.INSTALLED_IR_PARTS IS NOT NULL THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.INSTALLED_IR_PARTS, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045_INP');
            v_flag := FALSE;
        ELSE            
              IF v_count != Common_Utils.count_delimited_values(each_rec.INSTALLED_IR_PARTS, v_delimiter) THEN
                 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC075');
              End If;
            v_count := Common_Utils.count_delimited_values(each_rec.INSTALLED_IR_PARTS, v_delimiter);
            For I In 1 .. V_Count Loop               
                v_temp_part_number := common_validation_utils.getInstalledIRPart(
                        common_utils.get_delimited_value(each_rec.INSTALLED_IR_PARTS, v_delimiter, i), v_bu_name);
                IF v_temp_part_number IS NULL
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC046_INP');
                    v_installed_parts := NULL;
                    EXIT;
                END IF;
                IF i > 1 THEN
                    v_installed_parts := v_installed_parts || v_delimiter;
                END IF;
                v_installed_parts := v_installed_parts || v_temp_part_number;
            END LOOP;
			IF v_installed_parts IS NOT NULL AND v_installed_parts != each_rec.INSTALLED_IR_PARTS THEN
                UPDATE stg_draft_claim SET INSTALLED_IR_PARTS=v_installed_parts WHERE id=each_rec.id;
            ENd IF;
        END IF;

        IF each_rec.INSTALLED_IR_PARTS_QUANTITY IS NULL THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC027_INP');
        ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.INSTALLED_IR_PARTS_QUANTITY, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC047_INP');
        ELSIF v_flag = TRUE AND v_count != Common_Utils.count_delimited_values(each_rec.INSTALLED_IR_PARTS_QUANTITY, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC048_INP');
        ELSIF v_flag = TRUE THEN
            FOR i IN 1 .. v_count LOOP
                IF NOT common_utils.isPositiveInteger(
                        common_utils.get_delimited_value(each_rec.INSTALLED_IR_PARTS_QUANTITY, v_delimiter, i)) 
                THEN
                      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC054_INP');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
    END IF;

    IF each_rec.miscellaneous_parts IS NOT NULL THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.miscellaneous_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC049');
            v_flag := FALSE;
        ELSE
            v_count := Common_Utils.count_delimited_values(each_rec.miscellaneous_parts, v_delimiter);
            FOR i IN 1 .. v_count LOOP
                IF NOT common_validation_utils.isValidMiscPart(
                        common_utils.get_delimited_value(each_rec.miscellaneous_parts, v_delimiter, i), v_service_provider, v_bu_name)
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC050');
                    EXIT;
                END IF;
            END LOOP;
        END IF;

        IF each_rec.misc_parts_quantity IS NULL THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC051');
        ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.misc_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC052');
        ELSIF v_flag = TRUE AND v_count != Common_Utils.count_delimited_values(each_rec.misc_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC053');
        ELSIF v_flag = TRUE THEN
            FOR i IN 1 .. v_count LOOP
                IF NOT common_utils.isPositiveInteger(
                        common_utils.get_delimited_value(each_rec.misc_parts_quantity, v_delimiter, i)) 
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC055');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
    END IF;

    IF UPPER(each_rec.claim_type) NOT IN ('PARTS WITHOUT HOST') AND 
            each_rec.replaced_non_ir_parts IS NOT NULL 
    THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC056');
            v_flag := FALSE;
        ELSE
            v_count := common_utils.count_delimited_values(each_rec.replaced_non_ir_parts, v_delimiter);
            IF each_rec.replaced_non_ir_parts_quantity IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC028');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts_quantity, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC057');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_ir_parts_quantity, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC058');
            ELSE
                FOR i IN 1 .. v_count LOOP
                    IF NOT common_utils.isPositiveInteger(
                            common_utils.get_delimited_value(each_rec.replaced_non_ir_parts_quantity, v_delimiter, i)) 
                    THEN
                        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC059');
                        EXIT;
                    END IF;
                END LOOP;
            END IF;

            IF each_rec.replaced_non_ir_parts_price IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC029');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts_price, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC060');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_ir_parts_price, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC061');
            END IF;

            IF each_rec.replaced_non_ir_parts_desc IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC030');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts_desc, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC062');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_ir_parts_desc, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC063');
            END IF;        
        END IF;
    END IF;




	BEGIN
		 IF EACH_REC.SMR_CLAIM IS NOT NULL AND EACH_REC.SMR_CLAIM NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC031');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.COMMERCIAL_POLICY IS NOT NULL AND EACH_REC.COMMERCIAL_POLICY NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC068_CP');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.IS_PART_INSTALLED IS NOT NULL AND EACH_REC.IS_PART_INSTALLED NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC069_IPI');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.IS_PART_INSTALLED_ON_TKTSA IS NOT NULL AND EACH_REC.IS_PART_INSTALLED_ON_TKTSA NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC070_PITKTSA');
		 END IF;
	END;



	BEGIN
		 IF EACH_REC.SMR_CLAIM IS NOT NULL AND each_rec.smr_claim = 'Y' THEN
            IF EACH_REC.REASON_FOR_SMR_CLAIM IS NULL THEN
			    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC032');
            ELSE
                v_smr_reason_id := common_validation_utils.getValidSMRReasonId(each_rec.reason_for_smr_claim, v_user_locale, v_bu_name);
                IF v_smr_reason_id IS NULL THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC033');
                END IF;
            END IF;
		 END IF;
	END;

	IF (UPPER(each_rec.claim_type) = 'MACHINE NON SERIALIZED' OR
			(UPPER(each_rec.claim_type) = 'PARTS WITH HOST' 
				AND NOT v_serialized AND v_part_installed_on_tktsa))
			AND COMMON_VALIDATION_UTILS.isConfigParamSet('invoiceNumberApplicable', v_bu_name) 
			AND EACH_REC.INVOICE_NUMBER IS NULL THEN
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC034');
	END IF;

	BEGIN
		 IF TO_NUMBER(EACH_REC.HOURS_ON_PARTS) IS NOT NULL  AND (TO_NUMBER(EACH_REC.HOURS_ON_PARTS)<0) 

		 THEN

			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC035');
		 END IF;
	END;





	BEGIN

		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
		 EACH_REC.LABOUR_HOURS IS NOT NULL  
		 THEN
        IF EACH_REC.REASON_FOR_EXTRA_LABOR_HOURS IS NULL THEN 
        	 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036');
        ELSE 
         select count(*) into v_id    from add_lbr_egl_service_providers where d_active = 1;          
           IF v_id =0 then
              null;               
            ELSE             
                select count(*) into v_id    from add_lbr_egl_service_providers where SERVICE_PROVIDERS = v_dealer_id and d_active = 1; 
                IF v_id = 0 then
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_ALH');
                END IF; 
            END IF; 
        END IF;  
		 END IF;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_ALH');

	END;

IF v_product IS NOT NULL THEN     




BEGIN
       IF UPPER(EACH_REC.LABOUR_HOURS)IS NOT NULL THEN 
               IF NOT common_validation_utils.isAllowedCostCategory('LABOR',v_product,v_bu_name) THEN
                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC072');                                
               END IF;
        END IF;

        EXCEPTION 
          WHEN OTHERS THEN
          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC072');
END;




BEGIN
       IF UPPER(EACH_REC.REPLACED_IR_PARTS)IS NOT NULL THEN 
               IF NOT common_validation_utils.isAllowedCostCategory('OEM_PARTS',v_product,v_bu_name) THEN
                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_RP');                                
              END IF;
        END IF;

    EXCEPTION 
        WHEN OTHERS THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, '123');
END;





BEGIN
       IF UPPER(EACH_REC.MISCELLANEOUS_PARTS)IS NOT NULL THEN 
            IF NOT common_validation_utils.isAllowedCostCategory('MISC_PARTS',v_product,'v_bu_name') THEN
                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_MP');                                
              END IF;
       END IF;
END;

END IF;

	IF v_repairdate IS NOT NULL AND v_failuredate IS NOT NULL 
			AND v_repairdate < v_failuredate THEN
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC037');
	END IF;

	BEGIN
        v_fault_code := common_utils.getValidFaultCode(EACH_REC.fault_location);
		 IF v_model IS NOT NULL  AND EACH_REC.fault_location IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidFaultCodeForModelId(v_model, v_fault_code, v_bu_name)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC038');
		 END IF;
	END;


	IF each_rec.job_code IS NOT NULL THEN
        v_count := Common_Utils.count_delimited_values_new(each_rec.job_code, ',');
        FOR i IN 1 .. v_count LOOP
            v_job_code := common_utils.getValidJobCode(common_utils.get_delimited_value(each_rec.job_code, ',', i));
            IF v_job_code IS NULL THEN
                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC039');
                EXIT;
            ELSIF v_model IS NOT NULL AND
                NOT common_validation_utils.isValidJobCodeForModelId(v_model, v_job_code, v_bu_name)
		    THEN
			    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC039');
                EXIT;
            ELSE
                IF v_job_codes_delimited IS NULL THEN
                    v_job_codes_delimited := '';
                ELSE
                    v_job_codes_delimited := v_job_codes_delimited || ',';
                END IF;
                v_job_codes_delimited := v_job_codes_delimited || v_job_code;
            END IF;

        END LOOP;
        IF each_rec.labour_hours IS NOT NULL AND Common_Utils.count_delimited_values(each_rec.labour_hours, ',') > 0 THEN
            IF Common_Utils.count_delimited_values_new(each_rec.labour_hours, ',') < v_count THEN
                v_count := Common_Utils.count_delimited_values_new(each_rec.labour_hours, ',');
            END IF;
            FOR i IN 1 .. v_count LOOP
                IF common_utils.get_delimited_value(each_rec.labour_hours, ',', i) IS NOT NULL AND 
                        common_utils.get_delimited_value(each_rec.labour_hours, ',', i) != '0' AND 
                        common_utils.get_delimited_value(each_rec.reason_for_extra_labor_hours, v_delimiter, i) IS NULL THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
	END IF;





	BEGIN
		 IF v_model IS NOT NULL  AND EACH_REC.FAULT_FOUND IS NOT NULL THEN
            IF NOT COMMON_VALIDATION_UTILS.isValidFaultFoundForModelId(v_model, EACH_REC.FAULT_FOUND, v_bu_name)
		    THEN
			    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC040');
            ELSE
                v_valid_fault_found := TRUE;
                SELECT ftd.name INTO v_fault_found
                FROM failure_type ft, 
                    i18nfailure_type_definition i18n_ftd,
                    failure_type_definition ftd
                where 
                    ft.definition_id = i18n_ftd.failure_type_definition
                    AND lower(i18n_ftd.name) = lower(ltrim(rtrim(each_rec.fault_found))) 
                    AND ft.for_item_group_id = v_model 
                    AND ft.d_active = 1
                    AND ftd.id = i18n_ftd.failure_type_definition
                    AND ROWNUM = 1;
            END IF;
		 END IF;
	END;


	BEGIN
		 IF v_valid_fault_found  AND EACH_REC.failure_detail IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidRootCauseForModelId(v_model, EACH_REC.FAULT_FOUND, EACH_REC.failure_detail, v_bu_name)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC042');
		 END IF;
	END;

    BEGIN
		 IF each_rec.technician_id IS NOT NULL AND 
            NOT COMMON_VALIDATION_UTILS.isValidTechnician(each_rec.technician_id, v_dealer, v_bu_name)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC043');
		 END IF;
	END;


	IF v_error_code IS NULL
	THEN

	   UPDATE STG_DRAFT_CLAIM
	   SET
		  ERROR_STATUS = 'Y',
			ERROR_CODE = NULL,
            business_unit_name = v_bu_name,
            reason_for_smr_claim = v_smr_reason_id,
            competitor_model_id = v_competitor_model_id,
            fault_location = v_fault_code,
            job_code = v_job_codes_delimited,
            fault_found = v_fault_found,
            part_number=  v_item_number,
            repair_date= CAST (each_rec.REPAIR_DATE AS number(8,0)),                      
            failure_date= CAST (each_rec.FAILURE_DATE AS number(8,0)) ,                    
            installation_date=CAST (each_rec.INSTALLATION_DATE AS number(8,0)),
			serial_number_id = v_inv,
			item_number_id = v_item,
			model_id = v_model_id,
			part_id = v_part,
			part_serial_id = v_part_serial

		WHERE
		  ID = EACH_REC.ID;
	ELSE

		UPDATE STG_DRAFT_CLAIM
	   SET
		  ERROR_STATUS = 'N',
		  ERROR_CODE = v_error_code
		WHERE
		  ID = EACH_REC.ID;
	END IF;

    v_loop_count := v_loop_count + 1;

    IF v_loop_count = 10 THEN

      COMMIT;
      v_loop_count := 0; -- Initialize the count size
    END IF;

  END LOOP;

    IF v_loop_count > 0 THEN
        COMMIT;
    END IF;

  BEGIN



    SELECT DISTINCT file_upload_mgt_id 
    INTO v_file_upload_mgt_id
    FROM STG_DRAFT_CLAIM 
    WHERE ROWNUM < 2;


    BEGIN
      SELECT count(*)
      INTO v_success_count
      FROM STG_DRAFT_CLAIM 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
    EXCEPTION
    WHEN OTHERS THEN
      v_success_count := 0;
    END;


    BEGIN
      SELECT count(*)
      INTO v_error_count
      FROM STG_DRAFT_CLAIM 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;


    SELECT count(*)
    INTO v_count
    FROM STG_DRAFT_CLAIM 
    where file_upload_mgt_id = v_file_upload_mgt_id ;

    UPDATE FILE_UPLOAD_MGT 
    SET 
      SUCCESS_RECORDS= v_success_count, 
      ERROR_RECORDS= v_error_count,
      TOTAL_RECORDS = v_count
    WHERE ID = v_file_upload_mgt_id;

  EXCEPTION
  WHEN OTHERS THEN

    v_error := SUBSTR(SQLERRM, 1, 4000);
    UPDATE FILE_UPLOAD_MGT 
    SET 
      ERROR_MESSAGE = v_error
    WHERE ID = v_file_upload_mgt_id;

  END;
  COMMIT; -- Final Commit for the procedure

END UPLOAD_DRAFT_CLAIM_VALIDATION;
/
commit
/