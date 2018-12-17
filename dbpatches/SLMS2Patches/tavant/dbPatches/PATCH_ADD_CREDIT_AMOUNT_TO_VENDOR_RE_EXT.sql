CREATE OR REPLACE FORCE VIEW  "VENDOR_RECOVERY_EXTRACT" ("BUSINESS_UNIT_INFO", "ID", "CLAIM_NUMBER", "FILED_ON_DATE", "CLAIM_TYPE", "RECOVERY_CLAIM_STATE", "REC_CLAIM_CREATED_DATE", "REC_CLAIM_UPDATED_DATE", "REC_CLAIM_UPDATED_BY", "REC_CLAIM_MODIFIED_DATE", "FAILURE_DATE", "REPAIR_DATE", "DEALER_NUMBER", "DEALER_NAME", "CAUSAL_PART_NUMBER", "REPLACED_PART_NUMBER", "SUPPLIER_NUMBER", "SUPPLIER_NAME", "SERIAL_NUMBER", "MODEL_DESC", "BUILD_DATE", "INVOICE_DATE", "DELIVERY_DATE", "JOB_CODE", "HOURS_IN_SERVICE", "FAULT_FOUND", "CAUSED_BY", "DEALER_COMMENTS", "PROCESSOR_COMMENTS", "MATERIAL_COST_TOTAL", "NON_TK_PARTS_TOTAL", "MATERIAL_PARTS_TOTAL", "TOTAL_LABOR_HOURS", "LABOR_COST_TOTAL", "MISC_COST_TOTAL", "TOTAL_ACTUAL_AMT", "TOTAL_CONTRACT_AMT", "TOTAL_WARRANTY_AMT", "DEALER_CURRENCY", "SUPPLIER_CURRENCY", "CREDIT_MEMO_DATE", "CREDIT_MEMO_NUMBER", "CREDIT_MEMO_AMOUNT", "CREDIT_NOTE_ACCEPTED_CURR", "RECOVERY_COMMENTS", "CONTRACT_NAME", "REC_CLAIM_ACCEPTANCE_REASON","REC_CLAIM_REJECTION_REASON")
AS
  SELECT r.BUSINESS_UNIT_INFO BUSINESS_UNIT_INFO,
    r.id AS id,
    c.claim_number,
    c.filed_on_date,
    c.type claim_type,
    r.RECOVERY_CLAIM_STATE,
    r.d_created_on rec_claim_created_date,
    r.d_updated_on rec_claim_updated_date,
    (SELECT login FROM org_user WHERE id=r.d_last_updated_by
    ) rec_claim_updated_by,
    r.updated_date rec_claim_modified_date,
    claimAudit.failure_date,
    claimAudit.repair_date,
    (SELECT service_provider_number
    FROM service_provider
    WHERE id = c.for_dealer
    ) dealer_number,
    (SELECT name FROM party WHERE id = c.for_dealer
    ) dealer_name,
    (SELECT item_number FROM item WHERE id = s.CAUSAL_PART
    ) causal_part_number,
    GET_REPLACED_PARTS(s.service_detail) replaced_part_number,
    (SELECT supplier_number
    FROM supplier
    WHERE id =
      (SELECT supplier FROM contract WHERE id = r.CONTRACT
      )
    ) supplier_number,
    (SELECT name
    FROM party
    WHERE id =
      (SELECT supplier FROM contract WHERE id = r.CONTRACT
      )
    ) supplier_name,
    GET_SERIAL_NUMBER(c.id) AS serial_number,
    GET_MODEL_DESC(c.id)    AS model_desc,
    (SELECT MIN(BUILT_ON)
    FROM INVENTORY_ITEM
    WHERE ID IN
      (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = c.id
      )
    ) AS build_date,
    (SELECT MIN(INVOICE_DATE)
    FROM INVENTORY_TRANSACTION
    WHERE TRANSACTED_ITEM IN
      (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = c.id
      )
    AND INV_TRANSACTION_TYPE = 1
    ) AS invoice_date,
    (SELECT MIN(delivery_date)
    FROM INVENTORY_ITEM
    WHERE ID IN
      (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = c.id
      )
    ) AS delivery_date,
    GET_JOB_CODE_DESC(c.id) job_code,
    (SELECT SUM(hours_in_service) FROM claimed_item WHERE claim = c.id
    ) hours_in_service,
    (SELECT name FROM failure_type_definition WHERE id = s.FAULT_FOUND
    ) fault_found,
    (SELECT name FROM failure_cause_definition WHERE id = s.CAUSED_BY
    ) caused_by,
    to_clob(NVL(c.condition_found,''))
    ||to_clob(' ## ')
    ||to_clob(NVL(claimAudit.work_performed,''))
    ||to_clob(' ## ')
    ||to_clob(NVL(claimAudit.other_comments,'')) dealer_comments,
    (SELECT internal_comments
    FROM claim_audit
    WHERE id=
      (SELECT MAX(id)
      FROM claim_audit
      WHERE for_claim   =c.id
      AND previous_state='ACCEPTED'
      )
    ) processor_comments,
    (SELECT li.recovered_cost_amt
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name            ='Oem Parts'
    ) AS MATERIAL_COST_TOTAL,
    (SELECT li.recovered_cost_amt
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name            ='Non Oem Parts'
    ) AS NON_TK_PARTS_TOTAL,
    (SELECT li.recovered_cost_amt
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name            ='Miscellaneous Parts'
    )                           AS MATERIAL_PARTS_TOTAL,
    GET_TOTAL_LABOR_HOURS(c.id) AS total_labor_hours,
    (SELECT li.recovered_cost_amt
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name            ='Labor'
    ) AS LABOR_COST_TOTAL,
    (SELECT SUM(li.recovered_cost_amt)
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name NOT       IN ('Oem Parts','Non Oem Parts','Labor','Miscellaneous Parts')
    ) AS MISC_COST_TOTAL,
    (SELECT SUM(li.recovered_cost_amt)
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name           != 'Claim Amount'
    ) AS TOTAL_ACTUAL_AMT,
    (SELECT SUM(li.contract_cost_amt)
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name           != 'Claim Amount'
    ) AS TOTAL_CONTRACT_AMT,
    (SELECT SUM(li.cost_amt)
    FROM cost_line_item li,
      rec_clm_cost_line_items rli,
      section s
    WHERE li.id           =rli.cost_line_items
    AND rli.recovery_claim=r.id
    AND li.section        =s.id
    AND s.name           != 'Claim Amount'
    ) AS TOTAL_WARRANTY_AMT,
    (SELECT preferred_currency FROM organization WHERE id = c.for_dealer
    ) AS DEALER_CURRENCY,
    (SELECT preferred_currency
    FROM organization
    WHERE id =
      (SELECT supplier FROM contract WHERE id = r.CONTRACT
      )
    ) AS supplier_currency,
    (SELECT m.credit_memo_date
    FROM recovery_payment p,
      credit_memo m
    WHERE p.for_recovery_claim=r.id
    AND p.active_credit_memo  =m.id
    ) credit_memo_date,
    (SELECT m.credit_memo_number
    FROM recovery_payment p,
      credit_memo m
    WHERE p.for_recovery_claim=r.id
    AND p.active_credit_memo  =m.id
    ) credit_memo_number,
    (SELECT m.credit_amount_amt
    FROM recovery_payment p,
      credit_memo m
    WHERE p.for_recovery_claim=r.id
    AND p.active_credit_memo  =m.id
    ) AS CREDIT_MEMO_AMOUNT,
    (SELECT m.credit_amount_curr
    FROM recovery_payment p,
      credit_memo m
    WHERE p.for_recovery_claim=r.id
    AND p.active_credit_memo  =m.id
    ) AS CREDIT_NOTE_ACCEPTED_CURR,
    (SELECT comments
    FROM rec_claim_audit
    WHERE for_recovery_claim=r.id
    AND list_index          =
      (SELECT MAX(list_index) FROM rec_claim_audit WHERE for_recovery_claim=r.id
      )
    ) recovery_comments,
    (SELECT name FROM contract WHERE id=r.contract
    ) contract_name,
    (SELECT t.description
    FROM list_of_values lov,
      i18nlov_text t
    WHERE lov.id=r.rec_clm_accpt_reason
    AND lov.id  =t.list_of_i18n_values
    AND t.locale='en_US'
    AND UPPER(r.recovery_claim_state) LIKE '%CLOSED%'
    AND r.recovery_claim_state != 'CLOSED_UNRECOVERED'
    ) rec_claim_acceptance_reason,
    (SELECT t.description
    FROM list_of_values lov,
      i18nlov_text t
    WHERE lov.id=r.rec_clm_reject_reason
    AND lov.id  =t.list_of_i18n_values
    AND t.locale='en_US'
    AND UPPER(r.recovery_claim_state) LIKE '%CLOSED%'
    AND r.recovery_claim_state != 'CLOSED_UNRECOVERED'
    ) rec_claim_rejection_reason
  FROM claim c,
    claim_audit claimAudit,
    recovery_claim r,
    service_information s
  WHERE c.id                         = r.claim
  AND c.active_claim_audit           =claimAudit.id
  AND claimAudit.service_information = s.id
/