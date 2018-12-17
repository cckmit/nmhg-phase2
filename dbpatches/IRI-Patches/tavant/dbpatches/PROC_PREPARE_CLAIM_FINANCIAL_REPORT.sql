--Purpose    : Used to prepare the financial report based on the user input
--Author     : Jhulfikar Ali. A
--Created On : 16-Jan-08

create or replace
PROCEDURE PREPARE_CLAIM_FINANCIAL_REPORT(p_dealer_number IN VARCHAR2, 
p_from_date IN VARCHAR2, p_to_date IN VARCHAR2,
p_delimiter IN VARCHAR2, p_maximum_download IN NUMBER, reportclob OUT NOCOPY CLOB ) AS

CURSOR MODELS_ALL_REC(p_dealer_number IN VARCHAR2, p_from_date IN VARCHAR2, p_to_date IN VARCHAR2)
IS 
  select distinct model.id as "MODEL"
  from 
    claim cl, claimed_item ci, payment pay, 
    item it, item_group model, dealership dealer
  where 
  cl.payment = pay.id and 
  upper(cl.state) NOT IN ('DRAFT ', 'DRAFT_DELETED', 'DELETED') and 
  ci.claim =cl.id and ci.item_ref_unszed_item (+) = it.id and it.model = model.id and dealer.id = cl.for_dealer 
  and (to_date(cl.filed_on_date, 'DD-MM-YYYY') between to_date(p_from_date, 'DD-MM-YYYY') and to_date(p_to_date, 'DD-MM-YYYY'))
  and dealer.dealer_number in (p_dealer_number);
  
CURSOR REPORT_ALL_REC (p_model_id NUMBER, p_maximum_download NUMBER)
IS
  select model.id as "MODEL", cl.state as "STATE", 
  count(distinct (cl.id)) as "QUANTITY", 
  pay.previous_paid__amount_curr as "CURRENCY", 
  sum(pay.total_amount_amt) as "AMOUNT", 
  sum(pay.previous_PAID_AMOUNT_AMT) as "AMOUNT_PAID"
  from 
    claim cl, claimed_item ci, payment pay, 
    item it, item_group model, dealership dealer
  where 
  cl.payment = pay.id and upper(cl.state) NOT IN ('DRAFT ', 'DRAFT_DELETED', 'DELETED') and 
  ci.claim =cl.id and ci.item_ref_unszed_item (+) = it.id and it.model = model.id and dealer.id = cl.for_dealer 
  and (to_date(cl.filed_on_date, 'DD-MM-YYYY') between to_date(p_from_date, 'DD-MM-YYYY') and to_date(p_to_date, 'DD-MM-YYYY'))and 
  dealer.dealer_number in (p_dealer_number) 
  and model.id = p_model_id and rownum < p_maximum_download
  group by model.id, cl.state, pay.previous_paid__amount_curr;
  
  v_financial_array	DBMS_UTILITY.UNCL_ARRAY;
  v_model_name VARCHAR2(255) := 0;
  v_financial_array_iter NUMBER := 0;
  v_amount NUMBER := 0;
  v_quantity NUMBER := 0;
  v_amt_paid NUMBER := 0;
  v_amt_rejected NUMBER := 0;
  v_temp_amt_rejected NUMBER := 0;
  v_amt_pending NUMBER := 0;
  v_clms_pending NUMBER := 0;
  v_clms_rejected NUMBER := 0;
  v_clms_temp_rejected NUMBER := 0;
  
  v_financial_report CLOB := EMPTY_CLOB;
  v_curr VARCHAR2(20) := '';
  v_temp_lob CLOB := EMPTY_CLOB;
  v_temp_financial_report CLOB := EMPTY_CLOB;
  v_str_financial_report VARCHAR2(4000) := '';
  v_dealer_number VARCHAR2(4000) := '';
  
BEGIN
  reportclob := EMPTY_CLOB;
  -- Preparing the Header (v_financial_report - Header and Values)
  DBMS_LOB.createtemporary(v_financial_report, TRUE);
  DBMS_LOB.open(v_financial_report, DBMS_LOB.lob_readwrite);
  v_str_financial_report := 'Model' || p_delimiter || 'Qty Claimed' || p_delimiter || 'Amount Claimed' || p_delimiter || 
                        'Amount Paid' || p_delimiter || 'Amount Pending' || p_delimiter || 'Total Claims Pending' || 
                        p_delimiter || 'Total Qty Rejected' || p_delimiter || 'Total Amount Rejected' || p_delimiter || 
                        'Total Qty Temporarily Rejected' || p_delimiter || 'Total Amount Temporarily Rejected' || 
                        p_delimiter || 'Currency';
  DBMS_LOB.WRITEAPPEND (v_financial_report, length(v_str_financial_report), v_str_financial_report);
                        
  FOR EACH_REC IN MODELS_ALL_REC(p_dealer_number, p_from_date, p_to_date)
  LOOP
    BEGIN
    -- Setting the model id for future
    v_financial_array_iter := 0;
    select igmodel.description 
    INTO v_model_name
    from item_group igmodel where igmodel.id = EACH_REC.MODEL;
    
    v_financial_array(v_financial_array_iter) := v_model_name;

    v_quantity := 0; -- 2nd Item in the Array 
    v_amount := 0; -- 3rd Item in the Array
    v_amt_paid := 0; -- 4th Item in the Array
    v_amt_pending := 0; -- 5th Item in the Array
    v_clms_pending := 0; -- 6th Item in the Array
    v_clms_rejected := 0; -- 7th Item in the Array 
    v_amt_rejected := 0; -- 8th Item in the Array
    v_clms_temp_rejected := 0; -- 9th Item in the Array
    v_temp_amt_rejected := 0; -- 10th Item in the Array
    v_curr := ''; -- 11th Item in the Array

      FOR EACH_MODEL IN REPORT_ALL_REC (EACH_REC.MODEL, p_maximum_download)
      LOOP
       BEGIN
        --- Value assignments
        v_amount := v_amount + EACH_MODEL.AMOUNT;
        v_quantity := v_quantity + EACH_MODEL.QUANTITY;
        v_amt_paid := v_amt_paid + EACH_MODEL.AMOUNT_PAID;
        v_curr := EACH_MODEL.CURRENCY;
        
        IF (EACH_MODEL.STATE = 'Denied' OR EACH_MODEL.STATE = 'DENIED_AND_CLOSED')
        THEN
          v_amt_rejected := v_amt_rejected + EACH_MODEL.AMOUNT;
          v_clms_rejected := v_clms_rejected + 1;
        ELSIF (EACH_MODEL.STATE = 'Forwarded')
        THEN
          v_temp_amt_rejected := v_temp_amt_rejected + EACH_MODEL.AMOUNT;
          v_clms_temp_rejected := v_clms_temp_rejected + 1;
        ELSE 
          v_amt_pending := v_amt_pending + EACH_MODEL.AMOUNT;
          v_clms_pending := v_clms_pending + 1;
        END IF;
        
       END;
      END LOOP;
      
      DBMS_LOB.createtemporary(v_temp_lob, TRUE); -- v_temp_lob - to hold the data
      DBMS_LOB.open(v_temp_lob, DBMS_LOB.lob_readwrite);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_model_name || p_delimiter), v_model_name || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_quantity || p_delimiter ), v_quantity || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_amount || p_delimiter), v_amount || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_amt_paid || p_delimiter), v_amt_paid || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_amt_pending || p_delimiter), v_amt_pending || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_clms_pending || p_delimiter), v_clms_pending || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_clms_rejected || p_delimiter), v_clms_rejected || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_amt_rejected || p_delimiter), v_amt_rejected || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_clms_temp_rejected || p_delimiter), v_clms_temp_rejected || p_delimiter);
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_temp_amt_rejected || p_delimiter ), v_temp_amt_rejected || p_delimiter );
      DBMS_LOB.WRITEAPPEND (v_temp_lob, length(v_curr), v_curr);
      
      DBMS_LOB.createtemporary(v_temp_financial_report, TRUE); -- v_temp_financial_report - to hold 
      DBMS_LOB.open(v_temp_financial_report, DBMS_LOB.lob_readwrite);
      DBMS_LOB.WRITEAPPEND (v_temp_financial_report, length(v_temp_lob), v_temp_lob);
      
      DBMS_LOB.close(v_temp_lob);
      DBMS_LOB.close(v_temp_financial_report);
      DBMS_LOB.WRITEAPPEND (v_financial_report, length('\n'||v_temp_financial_report), '\n'||v_temp_financial_report);
    END;
  END LOOP;
  
  DBMS_LOB.createtemporary(reportclob, TRUE); -- reportclob - to hold return data
  DBMS_LOB.open(reportclob, DBMS_LOB.lob_readwrite);
  DBMS_LOB.COPY (reportclob, v_financial_report, length(v_financial_report), 1,1);
  DBMS_LOB.close(v_financial_report);
END PREPARE_CLAIM_FINANCIAL_REPORT;
/