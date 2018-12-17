CREATE OR REPLACE FUNCTION GET_JOB_CODE_STRING (p_claimId NUMBER) RETURN VARCHAR2 IS
 CURSOR ALL_JOB_CODES IS
  select code from service_procedure_definition where id in (
	select definition from service_procedure where id in ( 
	select service_procedure from labor_detail where id in(
	select labor_performed from service_labor_performed where service 
	in (select service_detail from service_information where id in 
	(select service_information from claim where id = p_claimId)))));

v_return_str	VARCHAR2(4000);
v_each_job_code VARCHAR2(255);
	

BEGIN

OPEN ALL_JOB_CODES;

fetch ALL_JOB_CODES into v_each_job_code;

IF length(v_return_str) > 0 THEN
   v_return_str := v_return_str || ',' || v_each_job_code;
ELSE   
   v_return_str := v_each_job_code;
END IF;   	    

CLOSE ALL_JOB_CODES;

RETURN v_return_str;

END;
/

CREATE OR REPLACE FUNCTION GET_TOTAL_LABOR_HOURS (p_claimId NUMBER) RETURN NUMBER IS
 CURSOR ALL_STD_HOURS IS
  	select SUM(SUGGESTED_LABOUR_HOURS) from service_procedure where id in ( 
	select service_procedure from labor_detail where id in(
	select labor_performed from service_labor_performed where service 
	in (select service_detail from service_information where id in 
	(select service_information from claim where id = p_claimId))));

 CURSOR ALL_ADD_HOURS IS
	select SUM(ADDITIONAL_LABOR_HOURS) from labor_detail where id in(
	select labor_performed from service_labor_performed where service 
	in (select service_detail from service_information where id in 
	(select service_information from claim where id = p_claimId)));
	 	
v_total_labor_hrs	NUMBER(19,2);
v_std_hours NUMBER(19,2);
v_add_hours NUMBER(19,2);

BEGIN

OPEN ALL_STD_HOURS;
OPEN ALL_ADD_HOURS;

fetch ALL_STD_HOURS into v_std_hours;
fetch ALL_ADD_HOURS into v_add_hours;

v_total_labor_hrs := NVL(v_std_hours,0) + NVL(v_add_hours,0);

CLOSE ALL_STD_HOURS;
CLOSE ALL_ADD_HOURS;

RETURN v_total_labor_hrs;

END;
/

CREATE OR REPLACE FUNCTION GET_INVENTORY_INFO (P_CLAIMID NUMBER, DATE_TYPE VARCHAR2) RETURN DATE IS

V_RETURN_VAL	DATE;
V_TEMP 			VARCHAR2(255);

BEGIN
	 
	 SELECT TYPE INTO V_TEMP FROM CLAIM WHERE ID = P_CLAIMID;
	 
	 IF (V_TEMP <> 'PARTS') THEN
	    IF (DATE_TYPE = 'BUILD_DATE') THEN
		 	SELECT BUILT_ON INTO V_RETURN_VAL FROM INVENTORY_ITEM 
			WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM IN (P_CLAIMID) AND ROWNUM = 1);
		END IF;
			
		IF	(DATE_TYPE = 'DELIVERY_DATE') THEN
			 SELECT DELIVERY_DATE INTO V_RETURN_VAL FROM INVENTORY_ITEM 
			WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM IN (P_CLAIMID) AND ROWNUM = 1);
		END IF;
		
		IF (DATE_TYPE = 'INVOICE_DATE') THEN
			 SELECT INVOICE_DATE INTO V_RETURN_VAL FROM INVENTORY_TRANSACTION
			 WHERE TRANSACTED_ITEM IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM IN (P_CLAIMID) AND ROWNUM = 1)
			 AND INV_TRANSACTION_TYPE = 1;
		END IF;
	 END IF;
	 

RETURN V_RETURN_VAL;

END;
/

CREATE OR REPLACE FUNCTION GET_MODEL_DESC (P_CLAIMID NUMBER) RETURN VARCHAR2 IS

V_RETURN_STR	VARCHAR2(255);
V_TEMP 			VARCHAR2(255);

BEGIN
	 
	 SELECT NAME INTO V_RETURN_STR FROM ITEM_GROUP WHERE ID IN (
	 SELECT MODEL FROM ITEM WHERE ID IN (SELECT OF_TYPE FROM INVENTORY_ITEM WHERE ID 
	 IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM IN (P_CLAIMID) AND ROWNUM = 1)));
	 
	 IF V_RETURN_STR IS NULL THEN
	 	SELECT NAME INTO V_RETURN_STR FROM ITEM_GROUP WHERE ID IN 
		   (SELECT MODEL_REF_FOR_UNSZED FROM CLAIMED_ITEM WHERE CLAIM IN (P_CLAIMID) AND ROWNUM = 1);
	 END IF;

RETURN V_RETURN_STR;

END;
/

CREATE OR REPLACE FUNCTION GET_SERIAL_NUMBER (P_CLAIMID NUMBER) RETURN VARCHAR2 IS

V_RETURN_STR	VARCHAR2(255);
V_CLM_TYPE	VARCHAR2(255);
V_TEMP 			VARCHAR2(255);

BEGIN

	 SELECT SERIAL_NUMBER INTO V_RETURN_STR FROM INVENTORY_ITEM WHERE ID 
	 IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM IN (P_CLAIMID) AND ROWNUM = 1);
	 
	 IF V_RETURN_STR IS NULL THEN
	 	SELECT ITEM_REFERENCE_UNSZD_SL_NO INTO V_RETURN_STR FROM CLAIMED_ITEM WHERE CLAIM IN (P_CLAIMID) AND ROWNUM = 1;
	 END IF;

RETURN V_RETURN_STR;

END;
/

create or replace view vendor_recovery_extract as
select  
	r.BUSINESS_UNIT_INFO BUSSINESS_UNIT_INFO,opr.id,claim_number, r.RECOVERY_CLAIM_STATE,r.d_created_on rec_claim_created_on,
	(select dealer_number from dealership where id = c.for_dealer) dealer_number,
	(select name from party where id = c.for_dealer) dealer_name,
	(select item_number from item where id = s.CAUSAL_PART) causal_part_number,
	(select item_number from item where id = opr.ITEM_REF_ITEM) replaced_part_number,
	(select description from item where id = opr.ITEM_REF_ITEM) replaced_part_desc,
	(select base_uom from uom_mappings where id = opr.uom_mapping) base_uom,
	(select mapped_uom from uom_mappings where id = opr.uom_mapping) mapped_uom,
	(select number_of_units from item where id = opr.ITEM_REF_ITEM) replaced_part_qty,
	(select COST_PRICE_PER_UNIT_AMT from item where id = opr.ITEM_REF_ITEM) replaced_part_cost,
	(select COST_PRICE_PER_UNIT_CURR from item where id = opr.ITEM_REF_ITEM) replaced_part_cost_curr,
	decode(opr.item_ref_item, s.CAUSAL_PART, 'YES','NO') is_causal_part,
	decode((select owned_by from item where id = opr.item_ref_item), (select id from party where name = 'OEM'), 'YES','NO') is_ir_part,
	(select supplier_number from supplier where id = (select supplier from contract where id = r.CONTRACT)) supplier_number,
	(select name from party where id = (select supplier from contract where id = r.CONTRACT)) supplier_name,
	GET_SERIAL_NUMBER(r.claim)  as serial_number, 
	GET_MODEL_DESC(r.claim) as model_desc,
	c.type claim_type, 
    GET_INVENTORY_INFO(r.claim, 'BUILD_DATE') as build_date, 
    GET_INVENTORY_INFO(r.claim, 'INVOICE_DATE') as date_of_invoice, 
    GET_INVENTORY_INFO(r.claim, 'DELIVERY_DATE') as date_of_delivery,
	c.failure_date, c.repair_date, 
	GET_JOB_CODE_STRING(c.id) job_code, 
	GET_TOTAL_LABOR_HOURS(c.id) total_labor_hours,
	(select cost_amt from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section in (select id from section where name = 'Labor')) labor_cost_total_USD,
	(select cost_curr from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section in (select id from section where name = 'Labor')) currency,
	(select sum(hours_in_service) from claimed_item where claim = c.id) hours_in_service,
	(select name from failure_type_definition where id = s.FAULT_FOUND) fault_found, 
	(select name from failure_cause_definition where id = s.CAUSED_BY) caused_by,
	c.external_comment dealer_comments, c.internal_comment processor_comments,
	(select CONTRACT_COST_AMT from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section in (select id from section where name = 'Club Car Parts')) MATERIAL_COST_TOTAL,
	(select CONTRACT_COST_AMT from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section in (select id from section where name = 'Non Club Car Parts')) NON_TK_PARTS_TOTAL,
	(select CONTRACT_COST_AMT from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section in (select id from section where name = 'MiscellaneousParts')) MATERIAL_PARTS_TOTAL,
	(select sum(CONTRACT_COST_AMT) from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section not in (select id from section where name in ('Club Car Parts','Non Club Car Parts','Labor','MiscellaneousParts'))) MISC_COST_TOTAL,
	(select CONTRACT_COST_AMT from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section in (select id from section where name = 'Labor')) LABOR_COST_TOTAL,
	(select sum(CONTRACT_COST_AMT) from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section not in (select id from section where name = 'Claim Amount')) TOTAL_CONTRACT_AMT,
	(select preferred_currency from organization where id =(select supplier from contract where id = r.CONTRACT)) supplier_currency,
	(select sum(COST_AMT) from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section not in (select id from section where name in ('Club Car Parts','Non Club Car Parts','Labor','MiscellaneousParts'))) MISC_COST_TOTAL_USD,
	(select COST_AMT from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section in (select id from section where name in ('Club Car Parts'))) TK_PARTS_COST_TOTAL_USD,
	(select sum(COST_AMT) from cost_line_item where id in (select cost_line_items from rec_clm_cost_line_items where RECOVERY_CLAIM =r.id)
	and section not in (select id from section where name = 'Claim Amount')) TOTAL_CONTRACT_AMT_USD
from claim c, recovery_claim r, service_information s, 
service_oemparts_replaced sor, oem_part_replaced opr
where c.id = r.claim
and c.service_information = s.id
and s.SERVICE_DETAIL = sor.service(+)
and opr.id(+) = sor.oemparts_replaced
/