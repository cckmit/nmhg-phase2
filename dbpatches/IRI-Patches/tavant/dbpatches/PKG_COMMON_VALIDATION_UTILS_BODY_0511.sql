--Purpose    : Fixed the job codes upload  - TWMS4.1-2705
--Author     : Raghu
--Created On : 11-May-09

create or replace PACKAGE BODY COMMON_VALIDATION_UTILS AS

 --TO CHECK WHETHER THE GIVEN ITEM NUMBER IS VALID IN THE SYSTEM OR NOT
  FUNCTION isValidBusinessUnitName(p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_business_unit_name VARCHAR2(255) := NULL;
  BEGIN
    -- Check whether the item number is exist at the System or not
    SELECT NAME 
    INTO v_business_unit_name
    FROM business_unit
    WHERE 
    lower(name) = lower(ltrim(rtrim(p_business_unit_name))) AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidBusinessUnitName;
  
 --TO CHECK WHETHER THE GIVEN MODEL IS VALID IN THE SYSTEM OR NOT
  FUNCTION isValidModel(p_model VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_model VARCHAR2(255) := NULL;
  BEGIN
    -- Check whether the item number is exist at the System or not
    SELECT NAME 
    INTO v_model
    FROM item_group
    WHERE 
    lower(name) = lower(ltrim(rtrim(p_model))) 
    and business_unit_info = p_business_unit_name
    and item_group_type = 'MODEL' and d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidModel;
  
 --TO CHECK WHETHER THE GIVEN PRODUCT CODE IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidProductCode(p_product_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_product_code NUMBER := NULL;
  BEGIN
    -- Check whether the item number is exist at the System or not
    SELECT NAME 
    INTO v_product_code
    FROM item_group
    WHERE 
    lower(name) = lower(ltrim(rtrim(p_product_code))) 
    and business_unit_info = p_business_unit_name
    and item_group_type = 'PRODUCT' and d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidProductCode;
 
 --TO CHECK WHETHER THE GIVEN MODEL AND PRODUCT IS VALID IN THE SYSTEM OR NOT
  FUNCTION isValidModelForProduct(p_model VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_model VARCHAR2(255) := NULL;
  BEGIN
    -- Check whether the item number is exist at the System or not
    SELECT m.NAME 
    INTO v_model
    FROM item_group m,item_group p
    WHERE 
    lower(m.name) = lower(ltrim(rtrim(p_model))) 
    and lower(p.name) = lower(ltrim(rtrim(p_product))) 
    and m.business_unit_info = p_business_unit_name
    and p.business_unit_info = p_business_unit_name
    and m.is_part_of=p.id and p.d_active = 1
    and m.item_group_type = 'MODEL' and m.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidModelForProduct;

 --TO CHECK WHETHER THE GIVEN ITEM NUMBER IS VALID IN THE SYSTEM OR NOT
  FUNCTION isValidItemNumber(p_item_number VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_item_id NUMBER := NULL;
  v_item_number_index NUMBER := -1;
  v_item_number VARCHAR2(255) := NULL;
  BEGIN
    -- Check whether the item number is exist at the System or not
    -- Check for Proper Item number with the given Business Unit with OEM manufactured
    SELECT i.ID 
    INTO v_item_id
    FROM ITEM i, PARTY p 
    WHERE ( lower(i.alternate_item_number) = lower(ltrim(rtrim(p_item_number)))  OR 
	lower(i.item_number) = lower(ltrim(rtrim(p_item_number))) )
    AND lower(i.business_unit_info) = lower(ltrim(rtrim(p_business_unit_name)))
    AND i.owned_by = p.ID
    AND p.NAME = common_utils.constant_oem_name and i.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidItemNumber;
  
 --TO CHECK WHETHER THE GIVEN SUPPLIER IS VALID IN THE SYSTEM OR NOT
  FUNCTION isValidSupplier(p_supplier_name VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_supplier_id NUMBER := NULL;
  BEGIN
    -- Check whether the supplier is exist at the System or not
    SELECT ID 
    INTO v_supplier_id
    FROM SUPPLIER 
    WHERE lower(supplier_number) = lower(ltrim(rtrim(p_supplier_name)))
    AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidSupplier;

 --TO CHECK WHETHER THE GIVEN JOB CODE IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidJobCode(p_job_code VARCHAR2, p_model VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_job_code VARCHAR2(256) := NULL;
  BEGIN
    -- Check whether the supplier is exist at the System or not
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

 --TO CHECK WHETHER THE GIVEN USER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidUser(p_user_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_user_login VARCHAR2(256) := NULL;
  BEGIN
    -- Check whether the supplier is exist at the System or not
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

 --TO CHECK WHETHER THE GIVEN DEALER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidDealer(p_dealer_login VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_dealer_login VARCHAR2(256) := NULL;
  BEGIN
    -- Check whether the dealer is exist at the System or not
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

 --TO CHECK WHETHER THE GIVEN DEALER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidDealerByNumber(p_dealer_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_dealer_number VARCHAR2(256) := NULL;
  BEGIN
    -- Check whether the dealer is exist at the System or not
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

 --TO CHECK WHETHER THE GIVEN DEALER IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidInventory (p_serial_number VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_inventory VARCHAR2(256) := NULL;
  BEGIN
    -- Check whether the supplier is exist at the System or not
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

 --TO CHECK WHETHER THE GIVEN FAULT CODE IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidFaultCode(p_model VARCHAR2, p_fault_code VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_fault_code            VARCHAR2(256) := NULL;
  v_failure_structure     NUMBER := NULL;
  v_complete_fault_code   VARCHAR2(4000) := NULL;
  BEGIN
    -- Check whether the supplier is exist at the System or not
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

 --TO CHECK WHETHER THE GIVEN FAULT FOUND IS VALID IN THE SYSTEM OR NOT
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
 
 --TO CHECK WHETHER THE GIVEN CAUSED BY IS VALID IN THE SYSTEM OR NOT
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

 --TO CHECK WHETHER THE GIVEN ROOT CAUSE IS VALID IN THE SYSTEM OR NOT
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

 --TO CHECK WHETHER THE GIVEN TECHNICIAN IS VALID IN THE SYSTEM OR NOT
 FUNCTION isValidTechnician(p_technician VARCHAR2, p_dealer VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
  IS
  v_technician VARCHAR2(256) := NULL;
  BEGIN
    -- Check whether the technician is exist at the System or not
    SELECT OU.LOGIN  
    INTO v_technician
    FROM ORG_USER TECHNICIAN, ORG_USER OU, DEALERSHIP dealer, BU_USER_MAPPING BUM, USER_ROLES ur, ROLE role 
    WHERE lower(TECHNICIAN.login) = lower(ltrim(rtrim(p_technician))) AND 
    TECHNICIAN.belongs_to_organization = dealer.id AND 
    lower(OU.login) = lower(ltrim(rtrim(p_dealer))) AND 
    OU.ID = BUM.ORG_USER AND TECHNICIAN.d_active = 1 AND 
    ur.org_user = TECHNICIAN.id AND ur.roles = role.id AND 
    lower(role.name) = 'technician' AND 
    lower(bum.bu) = lower(ltrim(rtrim(p_business_unit_name))) AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidTechnician;

 --TO CHECK IF PASSED DATE IS  A VALID DATE. FORMAT OF DATE SHOULD BE YYYYMMDD
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

 --TO CHECK IF THE CURRENCY IS VALID AT TWMS SYSTEM
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

 --TO CHECK IF THE CONFIG PARAM IS SET OR NOT
 FUNCTION isConfigParamSet (p_config_param VARCHAR2, p_business_unit_name VARCHAR2)
 RETURN BOOLEAN
 IS
  v_config_value VARCHAR2(1) := NULL;
  BEGIN
    SELECT NVL(cv.value, 'N') 
    INTO v_config_value
    FROM 
    config_param cp, config_value cv 
    WHERE upper(cp.name) = upper(ltrim(rtrim(p_config_param))) and 
    upper(cv.business_unit_info) = upper(ltrim(rtrim(p_business_unit_name)));
    IF v_config_value = 'Y'
    THEN
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  EXCEPTION
  WHEN OTHERS THEN
   RETURN FALSE;
  END isConfigParamSet;

--TO CHECK IF ASSEMBLY DEFINITION CODE IS VALID
FUNCTION isValidAssemblyDefinitionCode (p_code VARCHAR2, p_level NUMBER)
RETURN BOOLEAN
IS
    v_assembly_id       NUMBER;
BEGIN
    SELECT id INTO v_assembly_id
    FROM assembly_definition 
    WHERE lower(code) = lower(p_code) AND assembly_level = p_level;
    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END isValidAssemblyDefinitionCode;

END COMMON_VALIDATION_UTILS;
/
COMMIT
/