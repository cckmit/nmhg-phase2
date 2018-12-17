--Purpose    : Updating payment table to update amount of 'claimed_amount_amt' which is same as 'base_amt' column of line_item_group table
--Author     : Kuldeep Patil
--Created On : 17/06/2011

DECLARE
  CURSOR cur_sel
  IS
    SELECT SUM(LIG.BASE_AMT) AS BASE_AMT,
      CLM.PAYMENT,PMT.CLAIMED_AMOUNT_AMT
    FROM PAYMENT PMT, 
      line_item_groups ligs,
      line_item_group lig,
      CLAIM CLM
    WHERE clm.payment = pmt.id and ligs.line_item_groups = lig.id
    AND LIGS.FOR_PAYMENT        = pmt.id
    AND LIG.NAME               <> 'Claim Amount'
    GROUP BY  CLM.PAYMENT ,PMT.CLAIMED_AMOUNT_AMT
    having  SUM(lig.base_amt) <> PMT.CLAIMED_AMOUNT_AMT; 
  v_base_amt NUMBER(19,2) := 0.0;
BEGIN
  FOR i IN cur_sel
  LOOP
    UPDATE payment Pmt
    SET claimed_amount_amt = i.base_amt,
      d_internal_comments  = '4.3 Upgrade|QC-44-Fix'
    WHERE id               = i.payment;
  END LOOP;
  COMMIT;
END;
/
DECLARE
  payment_audit_id NUMBER:=0;
  claimed_amt      NUMBER:=0;
  list_indx        NUMBER:=0;
BEGIN
  FOR each_rec IN
  (SELECT t1.id, t1.payment FROM claim t1 , payment t2 WHERE t1.payment = t2.id
  )
  LOOP
    BEGIN
      SELECT MAX(list_index)
      INTO list_indx
      FROM claim_audit
      WHERE for_claim     = each_rec.id
      AND previous_state IN ('SUBMITTED','SERVICE_MANAGER_RESPONSE','SERVICE_MANAGER_REVIEW','EXTERNAL_REPLIES'); -- To Get the Latest Payment for dealer audit
      SELECT payment
      INTO payment_audit_id
      FROM claim_audit
      WHERE for_claim     = each_rec.id
      AND previous_state IN ('SUBMITTED','SERVICE_MANAGER_RESPONSE','SERVICE_MANAGER_REVIEW','EXTERNAL_REPLIES')
      AND list_index      = list_indx;
      SELECT total_amount_amt
      INTO claimed_amt
      FROM payment
      WHERE id = payment_audit_id;
      UPDATE payment
      SET claimed_amount_amt = claimed_amt,
        d_internal_comments  = d_internal_comments
        || '- QC-189'
      WHERE id = each_rec.payment;
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
      --dbms_output.put_line(each_rec.claim_number);
      NULL;
    END;
  END LOOP;
  commit;
END;
/
Commit
/