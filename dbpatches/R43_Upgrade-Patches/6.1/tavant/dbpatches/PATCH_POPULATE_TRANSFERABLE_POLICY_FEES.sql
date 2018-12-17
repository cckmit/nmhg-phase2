--PURPOSE    : PATCH FOR INSERTING RECORDS WITH TRANSFERABLE POLICY FEE
--AUTHOR     : ROHIT MEHROTRA
--CREATED ON : 27-APR-11
--Impact     : POLICY DEFINIITION

create or replace
PROCEDURE POLICY_FEES_ADD_TRANSFERABLE
AS
  CURSOR ALL_REC
  IS
    SELECT id FROM policy_definition;
  v_reg_amt_number NUMBER;
  v_REG_number      NUMBER;
  v_transferable_number  NUMBER;
  v_total_count      NUMBER:=0;
BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    v_reg_amt_number:= 0;
    v_REG_number     := 0;
    v_transferable_number :=0;
    SELECT COUNT(*)
    INTO v_reg_amt_number
    FROM policy_fees
    WHERE policy       =EACH_REC.id
    AND amount         =0
    AND is_transferable=0;
    SELECT COUNT(*)
    INTO v_REG_number
    FROM policy_fees
    WHERE policy         =EACH_REC.id
    AND is_transferable  =0;
    SELECT COUNT(*)
    INTO v_transferable_number
    FROM policy_fees
    WHERE policy         =EACH_REC.id
    AND is_transferable  =1;
    IF v_reg_amt_number =v_REG_number and v_transferable_number=0 THEN
      FOR REC           IN
      ( SELECT * FROM policy_fees WHERE policy=EACH_REC.id
      )
      LOOP
        BEGIN
        v_total_count:=v_total_count+1;
          INSERT
          INTO policy_fees VALUES
            (
              POLICY_FEES_SEQ.NEXTVAL,
              rec.amount,
              rec.currency,
              rec.policy,
              1
            );
        END;
       
      END LOOP;
    END IF;
  END LOOP;
COMMIT;
  DBMS_OUTPUT.PUT_LINE
  (
    'procedure succeded:'||v_total_count
  )
  ;
END POLICY_FEES_ADD_TRANSFERABLE;
/
begin
POLICY_FEES_ADD_TRANSFERABLE();
end;
/