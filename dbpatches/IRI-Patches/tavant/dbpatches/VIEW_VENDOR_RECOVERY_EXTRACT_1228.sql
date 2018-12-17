--Purpose : replaced parts for vendor recovery extract
--Author  : raguram.d
--Date    : 28/Dec/09

CREATE OR REPLACE FORCE VIEW vendor_recovery_extract_helper
as
select r.id recovery_claim,
min(case when t.pr=s.causal_part then 0 else t.prid end) primary_part_replaced
from claim c,recovery_claim r,service_information s,
  (select sopr.service,opr.id prid, opr.item_ref_item pr
    from service_oemparts_replaced sopr,oem_part_replaced opr 
    where sopr.oemparts_replaced=opr.id
    union all
    select snopr.service,nopr.id prid, null as pr
    from service_nonoemparts_replaced snopr,non_oem_part_replaced nopr 
    where snopr.nonoemparts_replaced=nopr.id
    union all
    select smpr.service,nopr.id prid, null as pr
    from service_misc_parts_replaced smpr,non_oem_part_replaced nopr 
    where smpr.misc_parts_replaced=nopr.id) t
where c.id=r.claim and c.service_information=s.id
  and s.service_detail=t.service
group by r.id

UNION ALL

select r.id recovery_claim,null primary_part_replaced
FROM claim c,recovery_claim r,service_information s
where c.id=r.claim and c.service_information=s.id
  and (select count(*) from service_oemparts_replaced where service= s.service_detail)=0
  and (select count(*) from service_nonoemparts_replaced where service= s.service_detail)=0
  and (select count(*) from service_misc_parts_replaced where service= s.service_detail)=0
/
CREATE OR REPLACE FORCE VIEW vendor_recovery_extract
as
select
    is_primary primary_line,
	business_unit_info bussiness_unit_info,
    recovery_id,
	(recovery_id || '-' || replaced_part) id,
	claim_number,
	clm_type_name claim_type,
	recovery_claim_state,
	d_created_on rec_claim_created_on,
	d_updated_on rec_claim_updated_on,
	failure_date,
	repair_date,
	(select service_provider_number from service_provider where id = for_dealer) dealer_number,
	(select name from party where id = for_dealer) dealer_name,
	(select supplier_number from supplier where id = (
		select supplier from contract where id = contract_id)) supplier_number,
	(select name from party where id = (
		select supplier from contract where id = contract_id)) supplier_name,
	(select item_number from item where id = causal_part) causal_part_number,
	is_causalpart is_causal_part,
	is_oempart is_ir_part,
	replaced_part replaced_part_number,
	part_desc replaced_part_desc,
	number_of_units replaced_part_qty,
	uom,
    case when part_unit_cost is not null then
	    cast(convert_to_currency(claim_number,part_unit_cost,unit_cost_curr,supplier_currency) as NUMBER(19,2))
        else null end replaced_part_cost,
	supplier_currency replaced_part_cost_curr,
    GET_SERIAL_NUMBER(claim_id) serial_number,
    GET_MODEL_DESC(claim_id) model_desc,
    (SELECT min(BUILT_ON) FROM INVENTORY_ITEM 
		WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = claim_id))
		build_date,
    (SELECT min(INVOICE_DATE) FROM INVENTORY_TRANSACTION
		WHERE TRANSACTED_ITEM IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM 
		WHERE CLAIM = claim_id) AND INV_TRANSACTION_TYPE = 1)
		invoice_date,
    (SELECT min(delivery_date) FROM INVENTORY_ITEM 
		WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = claim_id))
		delivery_date,
    GET_JOB_CODE_DESC(claim_id) job_code,
    (select sum(hours_in_service) 
		from claimed_item where claim = claim_id) 
		hours_in_service,
    (select name from failure_type_definition where id = fault_found) 
		fault_found,
    (select name from failure_cause_definition where id = caused_by) 
		caused_by,
    (select external_comments from claim_audit where id=accepted_audit) dealer_comments,
    (select internal_comments from claim_audit where id=accepted_audit) processor_comments,
	case when is_primary=1 then (select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section sec
		where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id and li.section=sec.id and sec.name='Club Car Parts')
		else null end MATERIAL_COST_TOTAL,
	case when is_primary=1 then (select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section sec
		where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id and li.section=sec.id and sec.name='Non Club Car Parts')
		else null end NON_TK_PARTS_TOTAL,
	case when is_primary=1 then (select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section sec
		where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id and li.section=sec.id and sec.name='MiscellaneousParts')
		else null end MATERIAL_PARTS_TOTAL,
	case when is_primary=1 then GET_TOTAL_LABOR_HOURS(claim_id) else null end total_labor_hours,
	case when is_primary=1 then (select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section sec
		where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id and li.section=sec.id and sec.name='Labor')
		else null end LABOR_COST_TOTAL,
	case when is_primary=1 then (select sum(li.recovered_cost_amt) from cost_line_item li,rec_clm_cost_line_items rli, section sec
		where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id and li.section=sec.id 
		and sec.name not in ('Club Car Parts','Non Club Car Parts','Labor','MiscellaneousParts'))
		else null end MISC_COST_TOTAL,
	case when is_primary=1 then (select sum(li.recovered_cost_amt) from cost_line_item li,rec_clm_cost_line_items rli, section sec
		where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id and li.section=sec.id and sec.name != 'Claim Amount')
		else null end TOTAL_CONTRACT_AMT,
	case when is_primary=1 then supplier_currency
		else null end supplier_currency
from (
--OEM Parts Replaced
select 
  r.id recovery_id,
  r.recovery_claim_state,
  r.d_created_on,
  r.d_updated_on,
  r.CONTRACT contract_id,
  r.BUSINESS_UNIT_INFO,
  c.id claim_id,
  c.claim_number,
  c.clm_type_name,
  c.failure_date,
  c.repair_date,
  c.for_dealer,
  c.external_comment,
  c.internal_comment,
  s.causal_part,
  s.service_detail,
  s.fault_found,
  s.CAUSED_BY,
  case when (h.primary_part_replaced=0 and opr.item_ref_item=s.causal_part) 
    or h.primary_part_replaced=opr.id then 1 else 0 end is_primary,
  'Y' is_oempart,
  case when opr.item_ref_item=s.causal_part then 'Y' else 'N' end is_causalpart,
  i.item_number replaced_part,
  i.description part_desc,
  opr.number_of_units,
  case when um.id is null then i.uom else um.mapped_uom end uom,
  case when um.id is null then opr.material_cost_amt 
    else opr.material_cost_amt/um.mapping_fraction end part_unit_cost,
  opr.material_cost_curr unit_cost_curr,
  (select preferred_currency from organization 
    where id =(select supplier from contract where id = r.contract)) supplier_currency,
  (select max(id) from claim_audit where for_claim=c.id and previous_state='ACCEPTED') accepted_audit
from claim c,recovery_claim r,service_information s,
  service_oemparts_replaced sopr, oem_part_replaced opr,
  uom_mappings um, item i, vendor_recovery_extract_helper h
where c.id=r.claim and c.service_information=s.id
  and s.service_detail= sopr.service and sopr.oemparts_replaced=opr.id
  and opr.uom_mapping=um.id(+) and opr.item_ref_item=i.id
  and r.id=h.recovery_claim
  
UNION ALL

--Non OEM Parts Replaced
select 
  r.id recovery_id,
  r.RECOVERY_CLAIM_STATE,
  r.d_created_on,
  r.d_updated_on,
  r.CONTRACT contract_id,
  r.BUSINESS_UNIT_INFO,
  c.id claim_id,
  c.claim_number,
  c.clm_type_name,
  c.failure_date,
  c.repair_date,
  c.for_dealer,
  c.external_comment,
  c.internal_comment,
  s.CAUSAL_PART,
  s.service_detail,
  s.FAULT_FOUND,
  s.CAUSED_BY,
  case when h.primary_part_replaced=opr.id then 1 else 0 end is_primary,
  'N' is_oempart,
  'N' is_causalpart,
  null replaced_part,
  opr.description part_desc,
  opr.number_of_units,
  'EACH' uom,
  opr.price_per_unit_amt part_unit_cost,
  opr.price_per_unit_curr unit_cost_curr,
  (select preferred_currency from organization 
    where id =(select supplier from contract where id = r.contract)) supplier_currency,
  (select max(id) from claim_audit where for_claim=c.id and previous_state='ACCEPTED') accepted_audit
from claim c,recovery_claim r,service_information s,
  service_nonoemparts_replaced sopr, non_oem_part_replaced opr,
  vendor_recovery_extract_helper h
where c.id=r.claim and c.service_information=s.id
  and s.service_detail= sopr.service and sopr.nonoemparts_replaced=opr.id
  and r.id=h.recovery_claim  

UNION ALL

--Miscellaneous Parts Replaced
select 
  r.id recovery_id,
  r.RECOVERY_CLAIM_STATE,
  r.d_created_on,
  r.d_updated_on,
  r.CONTRACT contract_id,
  r.BUSINESS_UNIT_INFO,
  c.id claim_id,
  c.claim_number,
  c.clm_type_name,
  c.failure_date,
  c.repair_date,
  c.for_dealer,
  c.external_comment,
  c.internal_comment,
  s.CAUSAL_PART,
  s.service_detail,
  s.FAULT_FOUND,
  s.CAUSED_BY,
  case when h.primary_part_replaced=opr.id then 1 else 0 end is_primary,
  'N' is_oempart,
  'N' is_causalpart,
  mi.part_number replaced_part,
  mi.description part_desc,
  opr.number_of_units,
  'EACH' uom,
  opr.price_per_unit_amt part_unit_cost,
  opr.price_per_unit_curr unit_cost_curr,
  (select preferred_currency from organization 
    where id =(select supplier from contract where id = r.contract)) supplier_currency,
  (select max(id) from claim_audit where for_claim=c.id and previous_state='ACCEPTED') accepted_audit
from claim c,recovery_claim r,service_information s,
  service_misc_parts_replaced sopr, non_oem_part_replaced opr,
  misc_item_config mic, misc_item mi,
  vendor_recovery_extract_helper h
where c.id=r.claim and c.service_information=s.id
  and s.service_detail= sopr.service and sopr.misc_parts_replaced=opr.id
  and opr.misc_item_config=mic.id and mic.miscellaneous_item=mi.id
  and r.id=h.recovery_claim  

UNION ALL

--No Replaced Parts
select 
  r.id recovery_id,
  r.RECOVERY_CLAIM_STATE,
  r.d_created_on,
  r.d_updated_on,
  r.CONTRACT contract_id,
  r.BUSINESS_UNIT_INFO,
  c.id claim_id,
  c.claim_number,
  c.clm_type_name,
  c.failure_date,
  c.repair_date,
  c.for_dealer,
  c.external_comment,
  c.internal_comment,
  s.CAUSAL_PART,
  s.service_detail,
  s.FAULT_FOUND,
  s.CAUSED_BY,
  1 is_primary,
  'N' is_oempart,
  'N' is_causalpart,
  null replaced_part,
  null part_desc,
  0 number_of_units,
  null uom,
  null part_unit_cost,
  null unit_cost_curr,
  (select preferred_currency from organization 
    where id =(select supplier from contract where id = r.contract)) supplier_currency,
  (select max(id) from claim_audit where for_claim=c.id and previous_state='ACCEPTED') accepted_audit
from claim c,recovery_claim r,service_information s,
  vendor_recovery_extract_helper h
where c.id=r.claim and c.service_information=s.id
  and r.id=h.recovery_claim and h.primary_part_replaced is null
) t
/