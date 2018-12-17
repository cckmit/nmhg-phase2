--Purpose : Modified the ID column to make it unique
--Author : raghuram.d
--Date : 20/Jul/09

  CREATE OR REPLACE FORCE VIEW "VENDOR_RECOVERY_EXTRACT" ("BUSSINESS_UNIT_INFO", "ID", "CLAIM_NUMBER", "RECOVERY_CLAIM_STATE", "REC_CLAIM_CREATED_ON", "REC_CLAIM_UPDATED_ON", "DEALER_NUMBER", "DEALER_NAME", "CAUSAL_PART_NUMBER", "REPLACED_PART_NUMBER", "REPLACED_PART_DESC", "BASE_UOM", "MAPPED_UOM", "REPLACED_PART_QTY", "REPLACED_PART_COST", "REPLACED_PART_COST_CURR", "IS_CAUSAL_PART", "IS_IR_PART", "SUPPLIER_NUMBER", "SUPPLIER_NAME", "SERIAL_NUMBER", "MODEL_DESC", "CLAIM_TYPE", "BUILD_DATE", "DATE_OF_INVOICE", "DATE_OF_DELIVERY", "FAILURE_DATE", "REPAIR_DATE", "JOB_CODE", "TOTAL_LABOR_HOURS", "LABOR_COST_TOTAL_USD", "CURRENCY", "HOURS_IN_SERVICE", "FAULT_FOUND", "CAUSED_BY", "DEALER_COMMENTS", "PROCESSOR_COMMENTS", "MATERIAL_COST_TOTAL", "NON_TK_PARTS_TOTAL", "MATERIAL_PARTS_TOTAL", "MISC_COST_TOTAL", "LABOR_COST_TOTAL", "TOTAL_CONTRACT_AMT", "SUPPLIER_CURRENCY", "MISC_COST_TOTAL_USD", "TK_PARTS_COST_TOTAL_USD", "TOTAL_CONTRACT_AMT_USD") AS 
  select  
	r.BUSINESS_UNIT_INFO BUSSINESS_UNIT_INFO, COALESCE(r.id||opr.ITEM_REF_ITEM,r.id||'') as id,claim_number, r.RECOVERY_CLAIM_STATE,
    r.d_created_on rec_claim_created_on,r.d_updated_on rec_claim_updated_on,
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
	null as serial_number, null as model_desc,
	c.type claim_type, null as build_date, null as date_of_invoice, null as date_of_delivery,c.failure_date, c.repair_date, 
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

 
