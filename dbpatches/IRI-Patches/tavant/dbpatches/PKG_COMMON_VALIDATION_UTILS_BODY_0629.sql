--Purpose    : Fixes for JobCode and InstallBase uploads
--Author     : raghuram.d
--Created On : 15-Jun-09

create or replace PACKAGE BODY COMMON_VALIDATION_UTILS AS 

  FUNCTION getValidBusinessUnitName(p_business_unit_name VARCHAR2)
  RETURN VARCHAR2
  IS
  v_business_unit_name VARCHAR2(255) := NULL;
  BEGIN
    -- Check whether the item number is exist at the System or not
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
    and lower(business_unit_info) = lower(p_business_unit_name)
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
    and lower(m.business_unit_info) = lower(p_business_unit_name)
    and lower(p.business_unit_info) = lower(p_business_unit_name)
    and m.is_part_of=p.id and p.d_active = 1
    and m.item_group_type = 'MODEL' and m.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidModelForProduct;

 --TO CHECK WHETHER THE GIVEN MODEL Code AND PRODUCT IS VALID IN THE SYSTEM OR NOT
  FUNCTION isValidModelCodeForProduct(p_model_code VARCHAR2, p_product VARCHAR2, p_business_unit_name VARCHAR2)
  RETURN BOOLEAN
  IS
  v_model VARCHAR2(255) := NULL;
  BEGIN
    -- Check whether the item number is exist at the System or not
    SELECT m.NAME 
    INTO v_model
    FROM item_group m,item_group p
    WHERE 
    lower(m.group_code) = lower(ltrim(rtrim(p_model_code))) 
    and lower(p.name) = lower(ltrim(rtrim(p_product))) 
    and lower(m.business_unit_info) = lower(p_business_unit_name)
    and lower(p.business_unit_info) = lower(p_business_unit_name)
    and m.is_part_of=p.id and p.d_active = 1
    and m.item_group_type = 'MODEL' and m.d_active = 1 AND ROWNUM = 1;
    RETURN TRUE;
  EXCEPTION 
  WHEN OTHERS THEN
    RETURN FALSE;
  END isValidModelCodeForProduct;

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
    IF p_code = '0000' THEN
        RETURN FALSE;
    END IF;

    SELECT id INTO v_assembly_id
    FROM assembly_definition 
    WHERE lower(code) = lower(p_code) AND assembly_level = p_level;
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
    -- Check whether the supplier is exist at the System or not
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
    -- Check whether the supplier is exist at the System or not
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
        FAILURE_TYPE_DEFINITION ftd
    where 
        ft.definition_id = ftd.id 
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
        v.business_unit_info='Transport Solutions ESA' AND
        i.business_unit_info='Transport Solutions ESA' AND
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


FUNCTION isValidMiscPart(p_item_number VARCHAR2, p_service_provider NUMBER, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
  v_id         NUMBER := NULL;
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

FUNCTION isValidCampaignCode(p_campaign_code VARCHAR2, p_dealer NUMBER, p_business_unit VARCHAR2)
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
        AND SYSDATE >= c.from_date AND SYSDATE <= c.till_date
        AND UPPER(c.code) = UPPER(p_campaign_code)
        AND ROWNUM = 1;

    RETURN TRUE;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
END isValidCampaignCode;

FUNCTION isValidInventoryForFieldMod(p_serial_number VARCHAR2, p_campaign_code VARCHAR2, p_dealer NUMBER, p_business_unit VARCHAR2)
RETURN BOOLEAN
IS
v_id    NUMBER;
BEGIN
    SELECT i.id INTO v_id 
    FROM campaign_notification n,inventory_item i, campaign c
    WHERE UPPER(i.business_unit_info) = UPPER(p_business_unit) 
        AND n.item = i.id
        AND n.campaign = c.id 
        AND n.dealership = p_dealer
        AND n.notification_status = 'PENDING'
        AND sysdate >= c.from_date and sysdate <= c.till_date
        AND UPPER(i.serial_number) = UPPER(p_serial_number)
        AND UPPER(c.code) = UPPER(p_campaign_code)
        AND ROWNUM = 1;

    RETURN TRUE;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
END isValidInventoryForFieldMod;


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

END COMMON_VALIDATION_UTILS;
/
COMMIT
/