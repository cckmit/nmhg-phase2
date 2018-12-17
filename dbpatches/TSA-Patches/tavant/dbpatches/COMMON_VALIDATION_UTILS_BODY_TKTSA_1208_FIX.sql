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