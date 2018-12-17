create or replace
PROCEDURE POPULATE_PAYMENT_MODIFIERS
AS
  CURSOR all_line_item_groups_audits
  IS
    SELECT LIGA.FOR_LINE_ITEM_GRP,
      liga.ACCEPTED_AMT,
      liga.ACCEPTED_CURR,
      liga.PERCENTAGE_ACCEPTANCE,
      liga.ACCEPTED_CP_AMT,
      LIGA.ACCEPTED_CP_CURR,
      liga.D_INTERNAL_COMMENTS
      ||'4.3 Upgrade',
      liga.BASE_AMT,
      liga.BASE_CURR,
      liga.GROUPTOTAL_AMT,
      liga.GROUPTOTAL_CURR,
      DECODE(liga.DISBURSED_AMT, NULL, liga.TOTAL_CREDIT_AMOUNT, -1*liga.DISBURSED_AMT) DISBURSED_AMT,
      NVL(liga.DISBURSED_CURR, liga.TOTAL_CREDIT_CURR) TOTAL_CREDIT_CURR,
      liga.RATE_AMOUNT,
      liga.RATE_CURR,
      liga.id exist_liga_id
    FROM line_item_group_Audit liga,
      line_item_group lig
    WHERE liga.list_index =
      (SELECT MAX(liga1.list_index)
      FROM line_item_group_Audit liga1
      WHERE liga1.for_line_item_grp = lig.id
      )
  AND LIG.id = LIGA.FOR_LINE_ITEM_GRP;
  v_start_time TIMESTAMP;
  V_COUNT       NUMBER := 0;
BEGIN
  SELECT CURRENT_TIMESTAMP INTO v_start_time FROM dual;
  dbms_output.put_line('Start time '||v_start_time);
  FOR each_LIGA IN all_line_item_groups_audits
  LOOP
    BEGIN
      UPDATE line_item_group
      SET ACCEPTED_AMT        = each_LIGA.ACCEPTED_AMT,
        ACCEPTED_CURR         = each_LIGA.ACCEPTED_CURR,
        PERCENTAGE_ACCEPTANCE = each_LIGA.PERCENTAGE_ACCEPTANCE,
        ACCEPTED_CP_AMT       = each_LIGA.ACCEPTED_CP_AMT,
        ACCEPTED_CP_CURR      = each_LIGA.ACCEPTED_CP_CURR,
        D_INTERNAL_COMMENTS   = D_INTERNAL_COMMENTS
        || '4.3 Upgrade',
        D_UPDATED_ON                = sysdate,
        D_UPDATED_TIME              = sysdate,
        BASE_AMT                    = each_LIGA.BASE_AMT,
        BASE_CURR                   = each_LIGA.BASE_CURR,
        GROUPTOTAL_AMT              = each_LIGA.GROUPTOTAL_AMT,
        GROUPTOTAL_CURR             = each_LIGA.GROUPTOTAL_CURR,
        TOTAL_CREDIT_AMT            = each_LIGA.DISBURSED_AMT,
        TOTAL_CREDIT_CURR           = each_LIGA.TOTAL_CREDIT_CURR,
        RATE                        = each_LIGA.RATE_AMOUNT,
        RATE_CURR                   = each_LIGA.RATE_CURR,
        exist_line_item_group_audit = null,
        prev_line_item_group_audit  = each_LIGA.exist_liga_id
      WHERE ID                      = each_LIGA.FOR_LINE_ITEM_GRP;
      IF (V_COUNT                   = 1000) THEN
        COMMIT;
        V_COUNT := 0;
      ELSE
        V_COUNT := V_COUNT + 1;
      END IF;
    END;
  END LOOP;
  
  INSERT
  INTO LINE_ITEM
    (
      ID,
      LINE_ITEM_LEVEL,
      MODIFIER_PERCENTAGE,
      NAME,
      AMT,
      CURR,
      VERSION,
      CP_AMT,
      CP_CURR,
      CLAIMED_AMT,
      CLAIMED_CURR,
      DLR_CLAIMED_AMT,
      DLR_CLAIMED_CURR,
      DISPLAY_AMT,
      DISPLAY_CURR,
      D_CREATED_ON,
      D_CREATED_TIME,
      D_INTERNAL_COMMENTS,
      D_UPDATED_ON,
      D_UPDATED_TIME,
      D_LAST_UPDATED_BY,
      D_ACTIVE,
      PAYMENT_VARIABLE,
      IS_FLAT_RATE,
      line_item_group_audit,
      line_item_group
    )
    (SELECT SEQ_LINEITEM.NEXTVAL,
        LI.LINE_ITEM_LEVEL,
        LI.MODIFIER_PERCENTAGE,
        LI.NAME,
        LI.AMT,
        LI.CURR,
        LI.VERSION,
        LI.CP_AMT,
        LI.CP_CURR,
        LI.CLAIMED_AMT,
        LI.CLAIMED_CURR,
        LI.DLR_CLAIMED_AMT,
        LI.DLR_CLAIMED_CURR,
        LI.DISPLAY_AMT,
        LI.DISPLAY_CURR,
        sysdate,
        systimestamp,
        LI.D_INTERNAL_COMMENTS
        ||'4.3 Upgrade',
        sysdate,
        systimestamp,
        LI.D_LAST_UPDATED_BY,
        LI.D_ACTIVE,
        LI.PAYMENT_VARIABLE,
        LI.IS_FLAT_RATE,
        M.LINE_ITEM_GROUP_AUDIT,
        lig.id
      FROM MODIFIERS M,
        LINE_ITEM LI,
        LINE_ITEM_GROUP LIG
      WHERE M.LINE_ITEM_GROUP_AUDIT = LIG.PREV_LINE_ITEM_GROUP_AUDIT
      AND M.MODIFIERS               = LI.ID
    );

  commit;

  INSERT
  INTO MODIFIERS_STAGE
    (
      MODIFIERS,
      LINE_ITEM_GROUP
    )
    (SELECT DISTINCT M.MODIFIERS modifiers,
      LIG.ID
      FROM MODIFIERS M,
      LINE_ITEM_GROUP LIG 
      WHERE M.LINE_ITEM_GROUP_AUDIT = LIG.EXIST_LINE_ITEM_GROUP_AUDIT
      UNION
      SELECT distinct LI.ID,
        LIG.ID
      FROM LINE_ITEM LI,
        LINE_ITEM_GROUP LIG
      WHERE LI.LINE_ITEM_GROUP_AUDIT = LIG.PREV_LINE_ITEM_GROUP_AUDIT and li.line_item_group = lig.id
    );

  commit;

  INSERT
  INTO LABOR_SPLIT_DETAIL_AUDIT
    (
      ID,
      LABOR_TYPE,
      LABOR_HRS,
      NAME,
      LABOR_RATE_AMT,
      LABOR_RATE_CURR,
      MULTIPLICATION_VALUE,
      VERSION,
      D_CREATED_ON,
      D_UPDATED_ON,
      D_INTERNAL_COMMENTS,
      D_LAST_UPDATED_BY,
      D_CREATED_TIME,
      D_UPDATED_TIME,
      LIST_INDEX,
      line_item_group_audit,
      line_item_group
    )
    (SELECT LABOR_SPLIT_DETAIL_AUDIT_SEQ.NEXTVAL,
        LA.LABOR_TYPE,
        LA.LABOR_HRS,
        LA.NAME,
        LA.LABOR_RATE_AMT,
        LA.LABOR_RATE_CURR,
        LA.MULTIPLICATION_VALUE,
        LA.VERSION,
        sysdate,
        sysdate,
        LA.D_INTERNAL_COMMENTS
        ||'4.3 Upgrade',
        LA.D_LAST_UPDATED_BY,
        systimestamp,
        systimestamp,
        LA.LIST_INDEX,
        L.LINE_ITM_GRP_AUDIT,
        lig.id
      FROM LABOR_SPLIT_DETAILS L,
        LABOR_SPLIT_DETAIL_AUDIT la,
        line_item_group lig
      WHERE L.LINE_ITM_GRP_AUDIT     = lig.prev_line_item_group_audit
      AND L.LABOR_SPLIT_DETAIL_AUDIT = LA.ID
    );
  --insert into labor split details
  commit;

  INSERT
  INTO LABOR_SPLIT_DETAILS_STAGE
    (
      LABOR_SPLIT_DETAIL_AUDIT,
      LINE_ITEM_GROUP
    )
    (SELECT L.LABOR_SPLIT_DETAIL_AUDIT,
        LIG.ID
      FROM LABOR_SPLIT_DETAILS L,
        LINE_ITEM_GROUP LIG
      WHERE L.LINE_ITM_GRP_AUDIT = LIG.EXIST_LINE_ITEM_GROUP_AUDIT
      UNION
      SELECT LSA.ID,
        LIG.ID
      FROM LABOR_SPLIT_DETAIL_AUDIT LSA,
        LINE_ITEM_GROUP LIG
      WHERE LSA.LINE_ITEM_GROUP_AUDIT = LIG.prev_line_item_group_audit and lig.id = lsa.line_item_group
    );
  COMMIT;
  dbms_output.put_line
  (
    'End Time '||CURRENT_TIMESTAMP
  )
  ;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line
  (
    'End Error '||CURRENT_TIMESTAMP
  )
  ;
  dbms_output.put_line
  (
    'Error occurred '||SQLERRM
  )
  ;
  ROLLBACK;
END;