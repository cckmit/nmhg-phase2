--Purpose : vendor recovery extract modified as per TSESA-232
--Author  : raguram.d
--Date    : 25/Jan/09

  CREATE OR REPLACE FORCE VIEW "VENDOR_RECOVERY_EXTRACT" (
  "BUSINESS_UNIT_INFO", "ID", "CLAIM_NUMBER", "CLAIM_TYPE", "RECOVERY_CLAIM_STATE", "REC_CLAIM_CREATED_DATE", 
  "REC_CLAIM_UPDATED_DATE", "REC_CLAIM_UPDATED_BY", "REC_CLAIM_MODIFIED_DATE",
  "FAILURE_DATE", "REPAIR_DATE", "DEALER_NUMBER", "DEALER_NAME", "CAUSAL_PART_NUMBER", 
  "REPLACED_PART_NUMBER", "SUPPLIER_NUMBER", "SUPPLIER_NAME", "SERIAL_NUMBER", "MODEL_DESC", "BUILD_DATE", "INVOICE_DATE", 
  "DELIVERY_DATE", "JOB_CODE", "HOURS_IN_SERVICE", "FAULT_FOUND", "CAUSED_BY", "DEALER_COMMENTS", "PROCESSOR_COMMENTS", 
  "MATERIAL_COST_TOTAL", "NON_TK_PARTS_TOTAL", "MATERIAL_PARTS_TOTAL", "TOTAL_LABOR_HOURS", "LABOR_COST_TOTAL", "MISC_COST_TOTAL", 
  "TOTAL_ACTUAL_AMT", "TOTAL_CONTRACT_AMT", "TOTAL_WARRANTY_AMT", "SUPPLIER_CURRENCY",
  "CREDIT_MEMO_DATE","CREDIT_MEMO_NUMBER", "RECOVERY_COMMENTS", "CONTRACT_NAME",
  "REC_CLAIM_ACCEPTANCE_REASON", "REC_CLAIM_REJECTION_REASON") AS 
  select
r.BUSINESS_UNIT_INFO BUSINESS_UNIT_INFO,
r.id as id,
c.claim_number, 
c.type claim_type,
r.RECOVERY_CLAIM_STATE,
r.d_created_on rec_claim_created_date,
r.d_updated_on rec_claim_updated_date,
(select login from org_user where id=r.d_last_updated_by) rec_claim_updated_by,
r.updated_date rec_claim_modified_date,
c.failure_date,
c.repair_date,
(select service_provider_number from service_provider where id = c.for_dealer) dealer_number,
(select name from party where id = c.for_dealer) dealer_name,
(select item_number from item where id = s.CAUSAL_PART) causal_part_number,
GET_REPLACED_PARTS(s.service_detail) replaced_part_number,
(select supplier_number from supplier where id = (select supplier from contract where id = r.CONTRACT)) supplier_number,
(select name from party where id = (select supplier from contract where id = r.CONTRACT)) supplier_name,
GET_SERIAL_NUMBER(c.id) as serial_number,
GET_MODEL_DESC(c.id) as model_desc,
(SELECT min(BUILT_ON) FROM INVENTORY_ITEM 
    WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = c.id))
    as build_date,
(SELECT min(INVOICE_DATE) FROM INVENTORY_TRANSACTION
    WHERE TRANSACTED_ITEM IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = c.id) AND INV_TRANSACTION_TYPE = 1)
    as invoice_date,
(SELECT min(delivery_date) FROM INVENTORY_ITEM 
    WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = c.id))
    as delivery_date,
GET_JOB_CODE_DESC(c.id) job_code,
(select sum(hours_in_service) from claimed_item where claim = c.id) hours_in_service,
(select name from failure_type_definition where id = s.FAULT_FOUND) fault_found,
(select name from failure_cause_definition where id = s.CAUSED_BY) caused_by,
NVL(c.condition_found,'') || ' ## ' || NVL(c.work_performed,'') || ' ## ' || NVL(c.other_comments,'')
    dealer_comments,
(select internal_comments from claim_audit where id=(SELECT MAX(id) 
    FROM claim_audit WHERE for_claim =c.id AND previous_state='ACCEPTED'))
    processor_comments,
(select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id and s.name='Club Car Parts')
    as MATERIAL_COST_TOTAL,
(select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id and s.name='Non Club Car Parts')
    as NON_TK_PARTS_TOTAL,
(select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id and s.name='MiscellaneousParts')
    as MATERIAL_PARTS_TOTAL,
GET_TOTAL_LABOR_HOURS(c.id) as total_labor_hours,
(select li.recovered_cost_amt from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id and s.name='Labor')
    as LABOR_COST_TOTAL,
(select sum(li.recovered_cost_amt) from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id 
    and s.name not in ('Club Car Parts','Non Club Car Parts','Labor','MiscellaneousParts'))
    as MISC_COST_TOTAL,
(select sum(li.recovered_cost_amt) from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id and s.name != 'Claim Amount')
    as TOTAL_ACTUAL_AMT,
(select sum(li.contract_cost_amt) from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id and s.name != 'Claim Amount')
    as TOTAL_CONTRACT_AMT,
(select sum(li.supplier_cost_amt) from cost_line_item li,rec_clm_cost_line_items rli, section s
    where li.id=rli.cost_line_items and rli.recovery_claim=r.id and li.section=s.id and s.name != 'Claim Amount')
    as TOTAL_WARRANTY_AMT,
(select preferred_currency from organization where id =(select supplier from contract where id = r.CONTRACT))
    as supplier_currency,
(select m.credit_memo_date from recovery_payment p,credit_memo m 
    where p.for_recovery_claim=r.id and p.active_credit_memo=m.id) credit_memo_date,
(select m.credit_memo_number from recovery_payment p,credit_memo m 
    where p.for_recovery_claim=r.id and p.active_credit_memo=m.id) credit_memo_number,
(select comments from rec_claim_audit where for_recovery_claim=r.id and
    list_index=(select max(list_index) from rec_claim_audit where for_recovery_claim=r.id)) recovery_comments,
(select name from contract where id=r.contract) contract_name,
(select t.description from list_of_values lov,i18nlov_text t 
  where lov.id=r.rec_clm_accpt_reason and lov.id=t.list_of_i18n_values and t.locale='en_US'
  and UPPER(r.recovery_claim_state) like '%CLOSED%' and r.recovery_claim_state != 'CLOSED_UNRECOVERED') rec_claim_acceptance_reason,
(select t.description from list_of_values lov,i18nlov_text t 
  where lov.id=r.rec_clm_reject_reason and lov.id=t.list_of_i18n_values and t.locale='en_US'
  and UPPER(r.recovery_claim_state) like '%CLOSED%' and r.recovery_claim_state != 'CLOSED_UNRECOVERED') rec_claim_rejection_reason
from claim c, recovery_claim r, service_information s
where c.id = r.claim
and c.service_information = s.id
/
