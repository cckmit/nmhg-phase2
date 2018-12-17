--Purpose    : INVENTORY ITEM DATES UPLOAD PROCEDURE, CHANGES DONE AS PART OF  4.3 UPGRADE
--Author     : ROHIT MEHROTRA
--Created On : 11-APR-2011
--Impact     : RETAIL INVENTORY LISTING
CREATE OR REPLACE
PROCEDURE INVENTORY_DATES_UPDATE
AS
  -- GET ALL THE RECORDS IN CURSOR WHICH NEED TO BE PROCESSED
  CURSOR ALL_REC
  IS
    SELECT id
    FROM inventory_item
    WHERE wnty_start_date IS NULL
    AND type               = 'RETAIL'
    AND d_active           = 1 ;
  v_from_date DATE;
  v_till_date DATE;
  v_from_date_temp DATE;
  v_till_date_temp DATE;
  v_stmt_number NUMBER := 0;
  v_policy_id   NUMBER;
  v_inv_id      NUMBER;
BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    v_stmt_number    := 10;
    v_from_date_temp := NULL;
    v_till_date_temp := NULL;
    v_inv_id         :=EACH_REC.id;
    FOR i            IN
    (SELECT id
    FROM policy
    WHERE warranty IN
      (SELECT id
      FROM warranty
      WHERE for_transaction =
        (SELECT ID
        FROM INVENTORY_TRANSACTION
        WHERE TRANSACTED_ITEM = EACH_REC.id
        AND TRANSACTION_ORDER =
          (SELECT MAX(TRANSACTION_ORDER)
          FROM INVENTORY_TRANSACTION
          WHERE TRANSACTED_ITEM = EACH_REC.id
          )
        )
      )
    )
    LOOP
      BEGIN
        v_policy_id   := i.id;
        v_stmt_number := 20;
        SELECT from_date,
          till_date
        INTO v_from_date,
          v_till_date
        FROM policy_audit
        WHERE for_policy = i.id
        AND status       = 'Active'
        AND id           =
          (SELECT MAX(id)
          FROM policy_audit
          WHERE for_policy = i.id
          GROUP BY for_policy
          );
        v_stmt_number        := 30;
        IF v_from_date_temp  IS NOT NULL AND v_till_date_temp IS NOT NULL THEN
          IF v_from_date_temp > v_from_date THEN
            v_from_date_temp := v_from_date;
          END IF;
          IF v_till_date_temp < v_till_date THEN
            v_till_date_temp := v_till_date;
          END IF;
        ELSE
          v_stmt_number    := 40;
          v_from_date_temp := v_from_date;
          v_till_date_temp := v_till_date;
        END IF;
      EXCEPTION
      WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Exception Occured while processing policy id : ' || v_policy_id ||'Inv id : ' || v_inv_id);
      END;
    END LOOP;
    v_stmt_number := 50;
    UPDATE inventory_item
    SET wnty_start_date   = v_from_date_temp ,
      wnty_end_date       = v_till_date_temp,
      d_internal_comments = d_internal_comments
      || ' : Dates Updated'
    WHERE id=EACH_REC.id;
    COMMIT;
  END LOOP;
  v_stmt_number := 60;
  DBMS_OUTPUT.PUT_LINE('Procedure executed successfully');
EXCEPTION
WHEN OTHERS THEN
  ROLLBACK;
  DBMS_OUTPUT.PUT_LINE('Exception Occured @:' || v_stmt_number ||' .' || SUBSTR(SQLERRM,1,255));
END INVENTORY_DATES_UPDATE;
/
begin
INVENTORY_DATES_UPDATE();
end;
/