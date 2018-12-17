--Purpose    : Added scripts for Payments as a part of 4.3 upgrade.
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None
--
CREATE TABLE ADDITIONAL_PAYMENT_INFO
(
  ID NUMBER(19, 0) NOT NULL,
  TYPE VARCHAR2(255 CHAR),
  ADDITIONAL_AMT NUMBER(19, 2),
  ADDITIONAL_CURR VARCHAR2(255 CHAR),
  PERCENTAGE_ACCEPTANCE NUMBER(19, 2), 
  CONSTRAINT ADDITIONAL_PAYMENT_INFO_PK PRIMARY KEY (ID)
)
/
CREATE SEQUENCE ADDITIONAL_PAYMENT_INFO_SEQ INCREMENT BY 20 START WITH 1000
/
CREATE TABLE ADD_PAYMENT_INFO
(
  ADDITIONAL_PAYMENT_INFO NUMBER(19, 0),
  LINE_ITEM_GROUP NUMBER(19, 0)
)
/
ALTER TABLE ADD_PAYMENT_INFO ADD CONSTRAINT ADD_PYM_INFO_LINE_ITEM_GRP_FK FOREIGN KEY 
("LINE_ITEM_GROUP") REFERENCES LINE_ITEM_GROUP ("ID") 
/
ALTER TABLE ADD_PAYMENT_INFO 
ADD CONSTRAINT ADDITIONAL_PAYMENT_INFO_FK FOREIGN KEY 
("ADDITIONAL_PAYMENT_INFO") REFERENCES ADDITIONAL_PAYMENT_INFO ("ID") 
/
CREATE TABLE LINE_ITEM_GROUP_BACKUP
AS (SELECT * FROM LINE_ITEM_GROUP)
/
CREATE TABLE PAYMENT_BACKUP
AS (SELECT * FROM PAYMENT)
/
CREATE TABLE PMT_PREV_CREDIT_MEMOS_BACKUP
AS (SELECT * FROM PAYMENT_PREVIOUS_CREDIT_MEMOS)
/
CREATE TABLE PREVIOUS_PART_INFO_BACKUP
AS (SELECT * FROM PREVIOUS_PART_INFO)
/
CREATE TABLE LINE_ITEM_GROUP_AUDIT_BACKUP
AS (SELECT * FROM LINE_ITEM_GROUP_AUDIT)
/
CREATE TABLE PAYMENT_COMPONENT_BACKUP
AS (SELECT * FROM PAYMENT_COMPONENT)
/
CREATE TABLE PAYMENT_AUDIT_BACKUP
AS (SELECT * FROM PAYMENT_AUDIT)
/
CREATE TABLE MODIFIERS_BACKUP
AS (SELECT * FROM MODIFIERS)
/
Create Table LIG_AUDIT_SLT_DTL_BACKUP 
as (Select * From LINE_ITM_GRP_AUDIT_SLT_DTL)
/
create table line_item_backup 
as (select * from line_item)
/
create table line_item_groups_backup 
as (select * from line_item_groups)
/
COMMIT
/
ALTER TABLE MODIFIERS ADD ("LINE_ITEM_GROUP" NUMBER(19))
/
ALTER TABLE MODIFIERS ADD CONSTRAINT MODIFIERS_LINE_ITEM_GROUP_FK FOREIGN KEY 
("LINE_ITEM_GROUP") REFERENCES LINE_ITEM_GROUP ("ID") 
/

ALTER TABLE LINE_ITM_GRP_AUDIT_SLT_DTL ADD  ("LINE_ITEM_GROUP" NUMBER(19))
/
ALTER TABLE LINE_ITM_GRP_AUDIT_SLT_DTL ADD  ("LABOR_SPLIT_DETAIL_AUDIT" NUMBER(19))
/
ALTER TABLE LINE_ITM_GRP_AUDIT_SLT_DTL ADD CONSTRAINT LABOR_SPLIT_LINE_ITEM_GROUP_FK FOREIGN KEY 
("LINE_ITEM_GROUP") REFERENCES LINE_ITEM_GROUP ("ID") 
/
ALTER TABLE LINE_ITEM_GROUP ADD ("BASE_AMT" NUMBER(19, 2))
/
ALTER TABLE LINE_ITEM_GROUP ADD ("BASE_CURR" VARCHAR2(255 CHAR))
/
ALTER TABLE LINE_ITEM_GROUP ADD ("GROUPTOTAL_AMT" NUMBER(19, 2))
/
ALTER TABLE LINE_ITEM_GROUP ADD ("GROUPTOTAL_CURR" VARCHAR2(255 CHAR))
/
ALTER TABLE LINE_ITEM_GROUP ADD ("TOTAL_CREDIT_AMT" NUMBER(19, 2))
/
ALTER TABLE LINE_ITEM_GROUP ADD ("TOTAL_CREDIT_CURR" VARCHAR2(255 CHAR))
/
ALTER TABLE LINE_ITEM_GROUP ADD ("RATE" NUMBER(19, 2))
/
ALTER TABLE LINE_ITEM_GROUP ADD ("RATE_CURR" VARCHAR2(255 CHAR))
/
ALTER TABLE CLAIM_AUDIT ADD ("PAYMENT" NUMBER(19, 0))
/
ALTER TABLE CLAIM_AUDIT ADD CONSTRAINT CLAIM_AUDIT_PAYMENT_FK1 FOREIGN KEY 
("PAYMENT") REFERENCES PAYMENT ("ID")
/
ALTER TABLE LINE_ITM_GRP_AUDIT_SLT_DTL RENAME TO LABOR_SPLIT_DETAILS
/
UPDATE LABOR_SPLIT_DETAILS
SET LABOR_SPLIT_DETAIL_AUDIT = SPLIT_DTL_AUDIT
/
declare
cursor CONS
is
select constraint_name from USER_CONS_COLUMNS 
where table_name = 'MODIFIERS' and column_name = 'LINE_ITEM_GROUP_AUDIT';
BEGIN
FOR CON in CONS LOOP
	EXECUTE IMMEDIATE 'ALTER table MODIFIERS drop constraint ' || CON.CONSTRAINT_NAME;
END LOOP;
END;
/
ALTER TABLE LABOR_SPLIT_DETAILS DROP CONSTRAINT "GRP_AUDIT_SLT_DTL_FK"
/
commit
/
--All below changes are proc specific and not the actual DB patch changes
alter table line_item_group add prev_line_item_group_audit NUMBER(19)
/
alter table line_item_group add exist_line_item_group_audit NUMBER(19)
/
alter table line_item add line_item_group_audit NUMBER(19)
/
alter table line_item add line_item_group NUMBER(19)
/
alter table LABOR_SPLIT_DETAIL_AUDIT add line_item_group_audit NUMBER(19)
/
alter table LABOR_SPLIT_DETAIL_AUDIT add line_item_group NUMBER(19)
/
create index line_item_group_indx1 on LABOR_SPLIT_DETAIL_AUDIT(line_item_group)
/
create index line_item_group_audit_indx4 on LABOR_SPLIT_DETAIL_AUDIT(line_item_group_audit)
/
create index line_item_group_indx2 on line_item(line_item_group)
/
create index line_item_group_audit_indx3 on line_item(line_item_group_audit)
/
create index line_item_group_audit_indx1 on line_item_group(prev_line_item_group_audit)
/
create index line_item_group_audit_indx2 on line_item_group(exist_line_item_group_audit)
/
CREATE TABLE MODIFIERS_STAGE
(
	LINE_ITEM_GROUP NUMBER(19,0) NOT NULL,
	MODIFIERS       NUMBER(19,0) NOT NULL
)
/
commit
/
CREATE TABLE LABOR_SPLIT_DETAILS_STAGE
  (
    LINE_ITEM_GROUP          NUMBER(19,0),
    LABOR_SPLIT_DETAIL_AUDIT NUMBER(19,0)
  )
/
CREATE UNIQUE INDEX SYS_C004762 ON MODIFIERS_STAGE (MODIFIERS)
/
commit
/
create table update_payment_log (
start_time TIMESTAMP,
end_time TIMESTAMP,
rec_processed number,
rec_remaining number,
rec_notappl number,
error_log varchar(4000)
)
/
create or replace
PROCEDURE UPDATE_CLAIM_AUDIT_PAYMENT_NEW
AS
  --Anonymous pl/sql block to populate payment and lineitemgroup.
  CURSOR ALL_CLAIMS
  IS
    SELECT DISTINCT C.id CLAIM_ID,
      C.CLAIM_NUMBER,
      C.PAYMENT,
      CA.id CLAIM_AUDIT_ID,
      CA.LIST_INDEX CLAIM_LIST_INDEX,
      CA.PREVIOUS_STATE CLAIM_STATE,
      lig.id AS lig_id,
      lig.name,
      LIGA.*
    FROM CLAIM C,
      claim_audit ca,
      LINE_ITEM_GROUP_AUDIT LIGA,
      LINE_ITEM_GROUP lig
    WHERE ca.for_claim          = c.id
    AND CA.PAYMENT             IS NULL
    AND LIGA.FOR_CLAIM_AUDIT (+)= CA.id
    AND lig.id(+)               = LIGA.FOR_LINE_ITEM_GRP
    AND c.state NOT            IN ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND C.CLAIM_NUMBER NOT     IN ('10218241', '10212907')
    ORDER BY C.id,
      ca.list_index;
  CURSOR ALL_LIGA (p_for_claim_audit NUMBER)
  IS
    SELECT lig.name,
      LIG.ID AS LIG_ID,
      liga.*
    FROM LINE_ITEM_GROUP_AUDIT liga,
      LINE_ITEM_GROUP lig
    WHERE liga.FOR_CLAIM_AUDIT = p_for_claim_audit
    AND LIGA.FOR_LINE_ITEM_GRP = LIG.ID;
	V_CLAIMED_AMT        NUMBER(17,2)  := 0.00;
	V_ACCEPTED_AMT       NUMBER(17,2)  := 0.00;
	V_PAYMENT_ID         NUMBER(19)    := 0;
	V_ADD_PMT_INFO_ID    NUMBER(19)    := 0;
	V_ORDER              NUMBER(19)    := 0;
	V_MEMO_ID            NUMBER(19)    := 0;
	V_DISBURSED_AMT      NUMBER(17,2)  := 0.00;
	V_TOT_CR_AMT         NUMBER(17,2)  := 0.00;
	V_LINE_ITEM_GROUP_ID NUMBER(19)    := 0;
	V_CURR               VARCHAR(30)   := '';
	V_PREVIOUS_STATE     VARCHAR(50)   := '';
	V_CLM_NUMBER         VARCHAR(50)   := '';
	TYPE T_LIG_IDS IS      VARRAY(100) OF NUMBER ;
	V_LIG_IDS T_LIG_IDS;
	V_LIGA_COUNT        NUMBER     := 1;
	V_MAX_AUDIT_ORDER   NUMBER     :=0;
	V_LIGA_FOR_CA       NUMBER(19) := 0;
	V_MAX_LIG_ID        NUMBER(19) := 0;
	V_LIGA_AUDIT_EXISTS NUMBER     :=0;
	V_MODIFIER_ID       NUMBER(19) := 0;
	V_LBR_SPT_DTL_ID    NUMBER(19) := 0;
	V_START_TIME TIMESTAMP;
	V_REC_PROCESSED         NUMBER;
	V_REC_REMAINING         NUMBER;
	V_REC_NOTAPPL           NUMBER;
	V_ERROR_LOG             VARCHAR(4000) := '';
	V_COUNT                 NUMBER        := 0;
	V_TEMP_CLAIM_ID         NUMBER(19)    :=0;
	V_TEMP_LIST_INDEX       NUMBER        :=0;
	V_TEMP_CLM_AUDIT_ID     NUMBER(19)    := 0;
	V_CLAIM_ADT_INDEX       NUMBER(19)    :=0;
	V_CLAIM_STATE           VARCHAR2(50)  :='';
	V_CLAIM_PAYMENT         NUMBER(19)    :=0;
	V_PREV_LINE_ITM_GRP_ADT NUMBER(19)    :=0;
	V_EXST_LINE_ITM_GRP_ADT NUMBER(19)    :=0;

	--logging variables
	v_program_name varchar2(255) :=null;
	v_migration_date timestamp := systimestamp;
	v_job_seq_id number(19):=0;
	v_xstatus number(19):=0;

--BEGIN
BEGIN
  SELECT CURRENT_TIMESTAMP INTO V_START_TIME FROM dual;
  DBMS_OUTPUT.PUT_LINE('Start: ' || V_START_TIME);
  v_program_name := 'UPDATE_CLAIM_AUDIT_PAYMENT_NEW';
  v_migration_date := SYSTIMESTAMP;
  v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);
              
  FOR each_CLAIM IN ALL_CLAIMS
  LOOP
    BEGIN
      --if claim audit changes then initialize the below values to
      IF V_TEMP_CLM_AUDIT_ID <> EACH_CLAIM.CLAIM_AUDIT_ID THEN
        -- FIRST CREATE A PAYMENT AND THEN REINITIALIZE
        IF V_CLAIM_ADT_INDEX > 0 AND V_LIGA_FOR_CA > 0 THEN
          IF (V_CLAIM_STATE  = 'ACCEPTED_AND_CLOSED' OR V_CLAIM_STATE = 'DENIED_AND_CLOSED') THEN
            BEGIN
              IF ((V_PREVIOUS_STATE IS NULL AND V_CLAIM_STATE = 'DENIED_AND_CLOSED') OR (V_DISBURSED_AMT = 0 AND LENGTH(V_PREVIOUS_STATE) > 1) OR (V_TOT_CR_AMT = 0)) THEN
                BEGIN
                  --DO NOTHING
                  NULL;
                END;
              ELSE
                BEGIN
                  V_PREVIOUS_STATE := V_CLAIM_STATE;
                  SELECT CM.ID
                  INTO V_MEMO_ID
                  FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM,
                    CREDIT_MEMO CM
                  WHERE PPCM.PAYMENT             = V_CLAIM_PAYMENT
                  AND PPCM.PREVIOUS_CREDIT_MEMOS = CM.ID
                  AND V_ORDER                    =
                    (SELECT COUNT(*)
                    FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM1,
                      CREDIT_MEMO CM1
                    WHERE PPCM1.PAYMENT             = PPCM.PAYMENT
                    AND PPCM1.PREVIOUS_CREDIT_MEMOS = CM1.ID
                    AND CM1.id                      < CM.id
                    );
                  V_ORDER := V_ORDER + 1;
                END;
              END IF;
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
          --    DBMS_OUTPUT.PUT_LINE('###EXCEPTION OCCURED HERE FOR CLAIM '||V_CLM_NUMBER);
             -- DBMS_OUTPUT.PUT_LINE('V_ORDER '||V_ORDER||' :: claim '||V_CLM_NUMBER||' :: claim Audit '||EACH_CLAIM.CLAIM_AUDIT_ID||' :: temp claim audit '||V_TEMP_CLM_AUDIT_ID);
              TAV_GIM_INITIAL_SETUP.proc_insert_error_record
               (GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
                G_ISSUE_ID               =>    '1.0'                        ,                       --2
                G_TABLE_NAME             =>    'CREDIT_MEMO'               ,                       --3
                G_ISSUE_COL_NAME         =>    'CREDIT_MEMO'                  ,                       --4
                G_ISSUE_COL_VALUE        =>    'CREDIT_MEMO'                 ,                       --5
                G_ISSUE_TYPE             =>    'Issue while looking up credit memo id ##'  ,    --6
                G_KEY_COL_NAME_1         =>    'CLAIM_STATE'    ,                                 --7
                G_KEY_COL_VALUE_1        =>    V_CLAIM_STATE    ,                                 --8
                G_KEY_COL_NAME_2         =>    'V_ORDER'    ,                                 --9
                G_KEY_COL_VALUE_2        =>    V_ORDER    ,                                 --10
                G_KEY_COL_NAME_3         =>    'V_CLAIM_PAYMENT'    ,                                 --11
                g_key_col_value_3        =>    V_CLAIM_PAYMENT    ,                                 --12
                g_ora_error_message      =>      SQLERRM             ,                                 --13
                G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
                G_BLOCK_NAME             =>   'Block 1'                                       --15
               );
--              ROLLBACK;   
--              raise;
            END;
          END IF;
          -- DBMS_OUTPUT.PUT_LINE('12');
          --        IF (V_MAX_AUDIT_ORDER <> V_TEMP_LIST_INDEX) THEN
          SELECT SEQ_Payment.NEXTVAL
          INTO V_PAYMENT_ID
          FROM DUAL;
          INSERT
          INTO PAYMENT
            (
              ID,
              CLAIMED_AMOUNT_AMT,
              CLAIMED_AMOUNT_CURR,
              TOTAL_AMOUNT_AMT,
              TOTAL_AMOUNT_CURR,
              VERSION,
              ACTIVE_CREDIT_MEMO,
              D_CREATED_ON,
              D_CREATED_TIME,
              D_INTERNAL_COMMENTS,
              D_UPDATED_ON,
              D_UPDATED_TIME,
              D_LAST_UPDATED_BY,
              D_ACTIVE
            )
            VALUES
            (
              V_PAYMENT_ID,
              V_CLAIMED_AMT,
              V_CURR,
              V_ACCEPTED_AMT,
              V_CURR,
              1,
              V_MEMO_ID,
              SYSDATE,
              SYSDATE,
              '4.3 Upgrade',
              SYSDATE,
              SYSDATE,
              56,
              1
            );
          --             DBMS_OUTPUT.PUT_LINE('13 : : '||V_TEMP_CLM_AUDIT_ID);
          FOR i IN V_LIG_IDS.first..V_LIG_IDS.last
          LOOP
            --Insert record into line item groups table for payment line item group mapping
            INSERT
            INTO LINE_ITEM_GROUPS
              (
                FOR_PAYMENT,
                LINE_ITEM_GROUPS
              )
              VALUES
              (
                V_PAYMENT_ID,
                V_LIG_IDS(i)
              );
          END LOOP;
          V_LIG_IDS.delete;
          --          end if;
          --           DBMS_OUTPUT.PUT_LINE('14');
          UPDATE CLAIM_AUDIT
          SET PAYMENT           = V_PAYMENT_ID
          WHERE ID              = V_TEMP_CLM_AUDIT_ID;
          IF (V_MAX_AUDIT_ORDER = V_TEMP_LIST_INDEX) THEN
            BEGIN
              UPDATE PAYMENT
              SET CLAIMED_AMOUNT_AMT = V_CLAIMED_AMT,
                CLAIMED_AMOUNT_CURR  = V_CURR,
                TOTAL_AMOUNT_AMT     = V_ACCEPTED_AMT,
                TOTAL_AMOUNT_CURR    = V_CURR,
                D_INTERNAL_COMMENTS  = D_INTERNAL_COMMENTS
                || '4.3 Upgrade',
                D_UPDATED_ON   = sysdate ,
                D_UPDATED_TIME = sysdate
              WHERE id         = V_CLAIM_PAYMENT;
            END;
          END IF;
        END IF;
        V_CLAIMED_AMT       := 0.00;
        V_ACCEPTED_AMT      := 0.00;
        V_MEMO_ID           := NULL;
        V_DISBURSED_AMT     := 0.00;
        V_TOT_CR_AMT        := 0.00;
        V_LIG_IDS           := T_LIG_IDS();
        V_LIGA_COUNT        := 1;
        V_LIGA_AUDIT_EXISTS := 0;
        V_CLAIM_ADT_INDEX   := 0;
        V_CLAIM_STATE       := EACH_CLAIM.CLAIM_STATE;
        V_TEMP_LIST_INDEX   := EACH_CLAIM.CLAIM_LIST_INDEX;
      END IF;
      IF V_TEMP_CLAIM_ID        <> each_CLAIM.CLAIM_ID THEN
        V_PREVIOUS_STATE        := '';
        V_ORDER                 := 0;
        V_LIGA_FOR_CA           := 0;
        V_CLM_NUMBER            := EACH_CLAIM.CLAIM_NUMBER;
        V_TEMP_LIST_INDEX       :=0;
        V_CLAIM_PAYMENT         := EACH_CLAIM.PAYMENT;
        V_PREV_LINE_ITM_GRP_ADT :=0;
        V_EXST_LINE_ITM_GRP_ADT :=0;
        BEGIN
          SELECT MAX(ca.list_index)
          INTO V_MAX_AUDIT_ORDER
          FROM CLAIM_AUDIT CA
          WHERE CA.FOR_CLAIM = EACH_CLAIM.CLAIM_ID;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          --DBMS_OUTPUT.PUT_LINE('$$$EXCEPTION OCCURED HERE FOR CLAIM '||V_CLM_NUMBER);
           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
               (GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
                G_ISSUE_ID               =>    '1.1'                        ,                       --2
                G_TABLE_NAME             =>    'CLAIM_AUDIT'               ,                       --3
                G_ISSUE_COL_NAME         =>    'list_index'                  ,                       --4
                G_ISSUE_COL_VALUE        =>    V_MAX_AUDIT_ORDER                 ,                       --5
                G_ISSUE_TYPE             =>    'Issue while looking up max list index for the claim $$'  ,    --6
                G_KEY_COL_NAME_1         =>    'CLAIM_ID'    ,                                 --7
                G_KEY_COL_VALUE_1        =>    EACH_CLAIM.CLAIM_ID    ,                                 --8
                G_KEY_COL_NAME_2         =>    ''    ,                                 --9
                G_KEY_COL_VALUE_2        =>    ''    ,                                 --10
                G_KEY_COL_NAME_3         =>    ''    ,                                 --11
                g_key_col_value_3        =>    ''    ,                                 --12
                g_ora_error_message      =>      SQLERRM             ,                                 --13
                G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
                G_BLOCK_NAME             =>   'Block 2'                                       --15
               );
          ROLLBACK;
          RAISE;
        END;
      END IF;
      SELECT NVL(EACH_CLAIM.FOR_CLAIM_AUDIT, V_LIGA_FOR_CA)
      INTO V_LIGA_FOR_CA
      FROM DUAL;
      SELECT DECODE(EACH_CLAIM.FOR_CLAIM_AUDIT, NULL, 0, 1)
      INTO V_LIGA_AUDIT_EXISTS
      FROM DUAL;
      IF V_liga_audit_exists    <> 1 THEN
        V_PREV_LINE_ITM_GRP_ADT := V_EXST_LINE_ITM_GRP_ADT;
        V_EXST_LINE_ITM_GRP_ADT := NULL;
      ELSE
        V_PREV_LINE_ITM_GRP_ADT := NULL;
        V_EXST_LINE_ITM_GRP_ADT := each_claim.id;
      END IF;
      IF (V_LIGA_FOR_CA > 0) THEN
        BEGIN
          IF V_LIGA_AUDIT_EXISTS = 1 THEN
            V_CURR              := EACH_CLAIM.BASE_CURR;
            V_MAX_LIG_ID        := 0;
            V_MODIFIER_ID       := 0;
            V_LBR_SPT_DTL_ID    := 0;
            IF EACH_CLAIM.NAME   = 'Claim Amount' THEN
              V_ACCEPTED_AMT    := EACH_CLAIM.ACCEPTED_AMT;
              SELECT NVL(EACH_CLAIM.TOTAL_CREDIT_AMOUNT, 0) INTO V_TOT_CR_AMT FROM DUAL;
              SELECT NVL(EACH_CLAIM.DISBURSED_AMT, 0) INTO V_DISBURSED_AMT FROM DUAL;
            ELSE
              V_CLAIMED_AMT := V_CLAIMED_AMT + EACH_CLAIM.BASE_AMT;
            END IF;
            --            IF (each_claim.CLAIM_LIST_INDEX < V_MAX_AUDIT_ORDER) THEN
            SELECT SEQ_LineItemGroup.NEXTVAL
            INTO V_LINE_ITEM_GROUP_ID
            FROM DUAL;
            V_LIG_IDS.EXTEND(1);
            V_LIG_IDS(V_LIGA_COUNT) := V_LINE_ITEM_GROUP_ID;
            V_LIGA_COUNT            := V_LIGA_COUNT + 1;
            INSERT
            INTO LINE_ITEM_GROUP
              (
                id,
                NAME,
                ACCEPTED_AMT,
                ACCEPTED_CURR,
                TOTAL_CREDIT_AMT,
                TOTAL_CREDIT_CURR,
                ACCEPTED_CP_AMT,
                ACCEPTED_CP_CURR,
                GROUPTOTAL_AMT,
                GROUPTOTAL_CURR,
                BASE_AMT,
                BASE_CURR,
                RATE,
                RATE_CURR,
                PERCENTAGE_ACCEPTANCE,
                D_CREATED_ON,
                D_CREATED_TIME,
                D_INTERNAL_COMMENTS,
                D_UPDATED_ON,
                D_UPDATED_TIME,
                D_LAST_UPDATED_BY,
                D_ACTIVE,
                VERSION,
                PREV_LINE_ITEM_GROUP_AUDIT,
                exist_LINE_ITEM_GROUP_AUDIT
              )
              VALUES
              (
                V_LINE_ITEM_GROUP_ID,
                each_CLAIM.NAME,
                each_CLAIM.ACCEPTED_AMT,
                each_CLAIM.ACCEPTED_CURR,
                DECODE(each_CLAIM.DISBURSED_AMT, NULL, each_CLAIM.TOTAL_CREDIT_AMOUNT, -1*each_CLAIM.DISBURSED_AMT),
                NVL(each_CLAIM.DISBURSED_CURR, each_CLAIM.TOTAL_CREDIT_CURR),
                each_CLAIM.ACCEPTED_CP_AMT,
                each_CLAIM.ACCEPTED_CP_CURR,
                each_CLAIM.GROUPTOTAL_AMT,
                each_CLAIM.GROUPTOTAL_CURR,
                each_CLAIM.BASE_AMT,
                each_CLAIM.BASE_CURR,
                each_CLAIM.RATE_AMOUNT,
                each_CLAIM.RATE_CURR,
                each_CLAIM.PERCENTAGE_ACCEPTANCE,
                SYSDATE,
                SYSDATE,
                '4.3 Upgrade',
                SYSDATE,
                SYSDATE,
                56,
                1,
                1,
                V_prev_line_itm_grp_adt,
                V_exst_line_itm_grp_adt
              );
            IF EACH_CLAIM.ACCEPTED_AMT_FOR_CP > 0 THEN
              SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
              INSERT
              INTO ADDITIONAL_PAYMENT_INFO
                (
                  ID,
                  TYPE,
                  ADDITIONAL_AMT,
                  ADDITIONAL_CURR,
                  PERCENTAGE_ACCEPTANCE
                )
                VALUES
                (
                  V_ADD_PMT_INFO_ID,
                  'ACCEPTED_FOR_CP',
                  EACH_CLAIM.ACCEPTED_AMT_FOR_CP,
                  EACH_CLAIM.ACCEPTED_CURR_FOR_CP,
                  DECODE(EACH_CLAIM.BASE_AMT, 0, 0, ROUND((EACH_CLAIM.ACCEPTED_AMT_FOR_CP/EACH_CLAIM.BASE_AMT)*100, 2))
                );
              INSERT
              INTO ADD_PAYMENT_INFO
                (
                  ADDITIONAL_PAYMENT_INFO,
                  LINE_ITEM_GROUP
                )
                VALUES
                (
                  V_ADD_PMT_INFO_ID,
                  V_LINE_ITEM_GROUP_ID
                );
            END IF;
            SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
            INSERT
            INTO ADDITIONAL_PAYMENT_INFO
              (
                ID,
                TYPE,
                ADDITIONAL_AMT,
                ADDITIONAL_CURR,
                PERCENTAGE_ACCEPTANCE
              )
              VALUES
              (
                V_ADD_PMT_INFO_ID,
                'ACCEPTED_FOR_WNTY',
                EACH_CLAIM.ACCEPTED_AMT_FOR_WNTY,
                EACH_CLAIM.ACCEPTED_CURR_FOR_WNTY,
                DECODE(EACH_CLAIM.BASE_AMT, 0, 0, ROUND((EACH_CLAIM.ACCEPTED_AMT_FOR_WNTY/EACH_CLAIM.BASE_AMT)*100, 2))
              );
            INSERT
            INTO ADD_PAYMENT_INFO
              (
                ADDITIONAL_PAYMENT_INFO,
                LINE_ITEM_GROUP
              )
              VALUES
              (
                V_ADD_PMT_INFO_ID,
                V_LINE_ITEM_GROUP_ID
              );
            --            END IF; --IF (each_claim.CLAIM_LIST_INDEX < V_MAX_AUDIT_ORDER) THEN  ENDS HERE
            IF
              (
                each_claim.CLAIM_LIST_INDEX = V_MAX_AUDIT_ORDER
              )
              THEN
              BEGIN
                IF each_claim.ACCEPTED_AMT_FOR_CP > 0 THEN
                  SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
                  INSERT
                  INTO ADDITIONAL_PAYMENT_INFO
                    (
                      ID,
                      TYPE,
                      ADDITIONAL_AMT,
                      ADDITIONAL_CURR,
                      PERCENTAGE_ACCEPTANCE
                    )
                    VALUES
                    (
                      V_ADD_PMT_INFO_ID,
                      'ACCEPTED_FOR_CP',
                      each_claim.ACCEPTED_AMT_FOR_CP,
                      each_claim.ACCEPTED_CURR_FOR_CP,
                      DECODE(each_claim.BASE_AMT, 0, 0, ROUND((each_claim.ACCEPTED_AMT_FOR_CP/each_claim.BASE_AMT)*100, 2))
                    );
                  INSERT
                  INTO ADD_PAYMENT_INFO
                    (
                      ADDITIONAL_PAYMENT_INFO,
                      LINE_ITEM_GROUP
                    )
                    VALUES
                    (
                      V_ADD_PMT_INFO_ID,
                      each_claim.lig_id
                    );
                END IF;
                SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
                INSERT
                INTO ADDITIONAL_PAYMENT_INFO
                  (
                    ID,
                    TYPE,
                    ADDITIONAL_AMT,
                    ADDITIONAL_CURR,
                    PERCENTAGE_ACCEPTANCE
                  )
                  VALUES
                  (
                    V_ADD_PMT_INFO_ID,
                    'ACCEPTED_FOR_WNTY',
                    each_claim.ACCEPTED_AMT_FOR_WNTY,
                    each_claim.ACCEPTED_CURR_FOR_WNTY,
                    DECODE(each_claim.BASE_AMT, 0, 0, ROUND((each_claim.ACCEPTED_AMT_FOR_WNTY/each_claim.BASE_AMT)*100, 2))
                  );
                INSERT
                INTO ADD_PAYMENT_INFO
                  (
                    ADDITIONAL_PAYMENT_INFO,
                    LINE_ITEM_GROUP
                  )
                  VALUES
                  (
                    V_ADD_PMT_INFO_ID,
                    each_claim.lig_id
                  );
              END;
            END IF;
          ELSIF V_LIGA_AUDIT_EXISTS <> 1 THEN
            BEGIN
              FOR EACH_REC IN ALL_LIGA
              (
                V_LIGA_FOR_CA
              )
              LOOP
                BEGIN
                  V_CURR           := EACH_REC.BASE_CURR;
                  V_MAX_LIG_ID     := 0;
                  V_MODIFIER_ID    := 0;
                  V_LBR_SPT_DTL_ID := 0;
                  IF EACH_REC.NAME  = 'Claim Amount' THEN
                    V_ACCEPTED_AMT := EACH_REC.ACCEPTED_AMT;
                    SELECT NVL(EACH_REC.TOTAL_CREDIT_AMOUNT, 0) INTO V_TOT_CR_AMT FROM DUAL;
                    SELECT NVL(EACH_REC.DISBURSED_AMT, 0) INTO V_DISBURSED_AMT FROM DUAL;
                  ELSE
                    V_CLAIMED_AMT := V_CLAIMED_AMT + EACH_REC.BASE_AMT;
                  END IF;
                  -- IF(each_claim.CLAIM_LIST_INDEX <> V_MAX_AUDIT_ORDER) THEN
                  SELECT SEQ_LineItemGroup.NEXTVAL
                  INTO V_LINE_ITEM_GROUP_ID
                  FROM DUAL;
                  V_LIG_IDS.EXTEND(1);
                  V_LIG_IDS(V_LIGA_COUNT) := V_LINE_ITEM_GROUP_ID;
                  V_LIGA_COUNT            := V_LIGA_COUNT + 1;
                  INSERT
                  INTO LINE_ITEM_GROUP
                    (
                      ID,
                      NAME,
                      ACCEPTED_AMT,
                      ACCEPTED_CURR,
                      TOTAL_CREDIT_AMT,
                      TOTAL_CREDIT_CURR,
                      ACCEPTED_CP_AMT,
                      ACCEPTED_CP_CURR,
                      GROUPTOTAL_AMT,
                      GROUPTOTAL_CURR,
                      BASE_AMT,
                      BASE_CURR,
                      RATE,
                      RATE_CURR,
                      PERCENTAGE_ACCEPTANCE,
                      D_CREATED_ON,
                      D_CREATED_TIME,
                      D_INTERNAL_COMMENTS,
                      D_UPDATED_ON,
                      D_UPDATED_TIME,
                      D_LAST_UPDATED_BY,
                      D_ACTIVE,
                      VERSION,
                      EXIST_LINE_ITEM_GROUP_AUDIT,
                      PREV_LINE_ITEM_GROUP_AUDIT
                    )
                    VALUES
                    (
                      V_LINE_ITEM_GROUP_ID,
                      EACH_REC.NAME,
                      EACH_REC.ACCEPTED_AMT,
                      EACH_REC.ACCEPTED_CURR,
                      DECODE(EACH_REC.DISBURSED_AMT, NULL, EACH_REC.TOTAL_CREDIT_AMOUNT, -1*EACH_REC.DISBURSED_AMT),
                      NVL(EACH_REC.DISBURSED_CURR, EACH_REC.TOTAL_CREDIT_CURR),
                      EACH_REC.ACCEPTED_CP_AMT,
                      EACH_REC.ACCEPTED_CP_CURR,
                      EACH_REC.GROUPTOTAL_AMT,
                      EACH_REC.GROUPTOTAL_CURR,
                      EACH_REC.BASE_AMT,
                      EACH_REC.BASE_CURR,
                      EACH_REC.RATE_AMOUNT,
                      EACH_REC.RATE_CURR,
                      EACH_REC.PERCENTAGE_ACCEPTANCE,
                      SYSDATE,
                      SYSDATE,
                      '4.3 Upgrade',
                      SYSDATE,
                      SYSDATE,
                      56,
                      1,
                      1,
                      NULL,
                      EACH_REC.ID
                    );
                  IF EACH_REC.ACCEPTED_AMT_FOR_CP > 0 THEN
                    SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
                    INSERT
                    INTO ADDITIONAL_PAYMENT_INFO
                      (
                        ID,
                        TYPE,
                        ADDITIONAL_AMT,
                        ADDITIONAL_CURR,
                        PERCENTAGE_ACCEPTANCE
                      )
                      VALUES
                      (
                        V_ADD_PMT_INFO_ID,
                        'ACCEPTED_FOR_CP',
                        EACH_REC.ACCEPTED_AMT_FOR_CP,
                        EACH_REC.ACCEPTED_CURR_FOR_CP,
                        DECODE(EACH_REC.BASE_AMT, 0, 0, ROUND((EACH_REC.ACCEPTED_AMT_FOR_CP/EACH_REC.BASE_AMT)*100, 2))
                      );
                    INSERT
                    INTO ADD_PAYMENT_INFO
                      (
                        ADDITIONAL_PAYMENT_INFO,
                        LINE_ITEM_GROUP
                      )
                      VALUES
                      (
                        V_ADD_PMT_INFO_ID,
                        V_LINE_ITEM_GROUP_ID
                      );
                    --                  DBMS_OUTPUT.PUT_LINE('cp > 0');
                  END IF;
                  SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
                  INSERT
                  INTO ADDITIONAL_PAYMENT_INFO
                    (
                      ID,
                      TYPE,
                      ADDITIONAL_AMT,
                      ADDITIONAL_CURR,
                      PERCENTAGE_ACCEPTANCE
                    )
                    VALUES
                    (
                      V_ADD_PMT_INFO_ID,
                      'ACCEPTED_FOR_WNTY',
                      EACH_REC.ACCEPTED_AMT_FOR_WNTY,
                      EACH_REC.ACCEPTED_CURR_FOR_WNTY,
                      DECODE(EACH_REC.BASE_AMT, 0, 0, ROUND((EACH_REC.ACCEPTED_AMT_FOR_WNTY/EACH_REC.BASE_AMT)*100, 2))
                    );
                  INSERT
                  INTO ADD_PAYMENT_INFO
                    (
                      ADDITIONAL_PAYMENT_INFO,
                      LINE_ITEM_GROUP
                    )
                    VALUES
                    (
                      V_ADD_PMT_INFO_ID,
                      V_LINE_ITEM_GROUP_ID
                    );
                  --end if;
                  IF
                    (
                      each_claim.CLAIM_LIST_INDEX = V_MAX_AUDIT_ORDER
                    )
                    THEN
                    BEGIN
                      IF EACH_REC.ACCEPTED_AMT_FOR_CP > 0 THEN
                        SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
                        INSERT
                        INTO ADDITIONAL_PAYMENT_INFO
                          (
                            ID,
                            TYPE,
                            ADDITIONAL_AMT,
                            ADDITIONAL_CURR,
                            PERCENTAGE_ACCEPTANCE
                          )
                          VALUES
                          (
                            V_ADD_PMT_INFO_ID,
                            'ACCEPTED_FOR_CP',
                            EACH_REC.ACCEPTED_AMT_FOR_CP,
                            EACH_REC.ACCEPTED_CURR_FOR_CP,
                            DECODE(EACH_REC.BASE_AMT, 0, 0, ROUND((EACH_REC.ACCEPTED_AMT_FOR_CP/EACH_REC.BASE_AMT)*100, 2))
                          );
                        INSERT
                        INTO ADD_PAYMENT_INFO
                          (
                            ADDITIONAL_PAYMENT_INFO,
                            LINE_ITEM_GROUP
                          )
                          VALUES
                          (
                            V_ADD_PMT_INFO_ID,
                            EACH_REC.LIG_ID
                          );
                        --                      DBMS_OUTPUT.PUT_LINE('cp > 0 : last');
                      END IF;
                      SELECT ADDITIONAL_PAYMENT_INFO_SEQ.NEXTVAL INTO V_ADD_PMT_INFO_ID FROM DUAL;
                      INSERT
                      INTO ADDITIONAL_PAYMENT_INFO
                        (
                          ID,
                          TYPE,
                          ADDITIONAL_AMT,
                          ADDITIONAL_CURR,
                          PERCENTAGE_ACCEPTANCE
                        )
                        VALUES
                        (
                          V_ADD_PMT_INFO_ID,
                          'ACCEPTED_FOR_WNTY',
                          EACH_REC.ACCEPTED_AMT_FOR_WNTY,
                          EACH_REC.ACCEPTED_CURR_FOR_WNTY,
                          DECODE(EACH_REC.BASE_AMT, 0, 0, ROUND((EACH_REC.ACCEPTED_AMT_FOR_WNTY/EACH_REC.BASE_AMT)*100, 2))
                        );
                      INSERT
                      INTO ADD_PAYMENT_INFO
                        (
                          ADDITIONAL_PAYMENT_INFO,
                          LINE_ITEM_GROUP
                        )
                        VALUES
                        (
                          V_ADD_PMT_INFO_ID,
                          EACH_REC.LIG_ID
                        );
                    END;
                  END IF;
                END;
              END LOOP; -- all_liga LOOP ENDS HERE
            END;
          END IF; --IF V_LIGA_AUDIT_EXISTS = 1 THEN ENDS HERE
        END;
      END IF;
      V_TEMP_CLM_AUDIT_ID := EACH_CLAIM.CLAIM_AUDIT_ID;
      V_CLAIM_ADT_INDEX   := V_CLAIM_ADT_INDEX + 1;
      V_TEMP_CLAIM_ID     := EACH_CLAIM.CLAIM_ID;
      IF
        (
          V_COUNT = 100
        )
        THEN
        COMMIT;
        V_COUNT := 0;
      ELSE
        V_COUNT := V_COUNT + 1;
      END IF;
    END;
  END LOOP;
  --Updating payment for the last claim
  IF V_CLAIM_ADT_INDEX > 0 AND V_LIGA_FOR_CA > 0 THEN
    IF
      (
        V_CLAIM_STATE = 'ACCEPTED_AND_CLOSED' OR V_CLAIM_STATE = 'DENIED_AND_CLOSED'
      )
      THEN
      BEGIN
        IF
          (
            (V_PREVIOUS_STATE IS NULL AND V_CLAIM_STATE = 'DENIED_AND_CLOSED') OR (V_DISBURSED_AMT = 0 AND LENGTH(V_PREVIOUS_STATE) > 1) OR (V_TOT_CR_AMT = 0)
          )
          THEN
          BEGIN
            --DO NOTHING
            NULL;
          END;
        ELSE
          BEGIN
            V_PREVIOUS_STATE := V_CLAIM_STATE;
            SELECT CM.ID
            INTO V_MEMO_ID
            FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM,
              CREDIT_MEMO CM
            WHERE PPCM.PAYMENT             = V_CLAIM_PAYMENT
            AND PPCM.PREVIOUS_CREDIT_MEMOS = CM.ID
            AND V_ORDER                    =
              (SELECT COUNT(*)
              FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM1,
                CREDIT_MEMO CM1
              WHERE PPCM1.PAYMENT             = PPCM.PAYMENT
              AND PPCM1.PREVIOUS_CREDIT_MEMOS = CM1.ID
              AND CM1.id                      < CM.id
              );
            V_ORDER := V_ORDER + 1;
          END;
        END IF;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        --DBMS_OUTPUT.PUT_LINE('@@@EXCEPTION OCCURED HERE FOR CLAIM '||V_CLM_NUMBER);
         TAV_GIM_INITIAL_SETUP.proc_insert_error_record
               (GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
                G_ISSUE_ID               =>    '1.0'                        ,                       --2
                G_TABLE_NAME             =>    'CREDIT_MEMO'               ,                       --3
                G_ISSUE_COL_NAME         =>    'CREDIT_MEMO'                  ,                       --4
                G_ISSUE_COL_VALUE        =>    'CREDIT_MEMO'                 ,                       --5
                G_ISSUE_TYPE             =>    'Issue while looking up credit memo id @@'  ,    --6
                G_KEY_COL_NAME_1         =>    'CLAIM_STATE'    ,                                 --7
                G_KEY_COL_VALUE_1        =>    V_CLAIM_STATE    ,                                 --8
                G_KEY_COL_NAME_2         =>    'V_ORDER'    ,                                 --9
                G_KEY_COL_VALUE_2        =>    V_ORDER    ,                                 --10
                G_KEY_COL_NAME_3         =>    ''    ,                                 --11
                g_key_col_value_3        =>    ''    ,                                 --12
                g_ora_error_message      =>      SQLERRM             ,                                 --13
                G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
                G_BLOCK_NAME             =>   'Block 1'                                       --15
               );
--        ROLLBACK;
--        RAISE;
      END;
    END IF;
    --     IF (V_MAX_AUDIT_ORDER <> V_TEMP_LIST_INDEX) THEN
    SELECT SEQ_Payment.NEXTVAL
    INTO V_PAYMENT_ID
    FROM DUAL;
    INSERT
    INTO PAYMENT
      (
        ID,
        CLAIMED_AMOUNT_AMT,
        CLAIMED_AMOUNT_CURR,
        TOTAL_AMOUNT_AMT,
        TOTAL_AMOUNT_CURR,
        VERSION,
        ACTIVE_CREDIT_MEMO,
        D_CREATED_ON,
        D_CREATED_TIME,
        D_INTERNAL_COMMENTS,
        D_UPDATED_ON,
        D_UPDATED_TIME,
        D_LAST_UPDATED_BY,
        D_ACTIVE
      )
      VALUES
      (
        V_PAYMENT_ID,
        V_CLAIMED_AMT,
        V_CURR,
        V_ACCEPTED_AMT,
        V_CURR,
        1,
        V_MEMO_ID,
        SYSDATE,
        SYSDATE,
        '4.3 Upgrade',
        SYSDATE,
        SYSDATE,
        56,
        1
      );
    FOR i IN V_LIG_IDS.first..V_LIG_IDS.last
    LOOP
      --Insert record into line item groups table for payment line item group mapping
      INSERT
      INTO LINE_ITEM_GROUPS
        (
          FOR_PAYMENT,
          LINE_ITEM_GROUPS
        )
        VALUES
        (
          V_PAYMENT_ID,
          V_LIG_IDS(i)
        );
    END LOOP;
    V_LIG_IDS.delete;
    --  end if;
    UPDATE CLAIM_AUDIT
    SET PAYMENT           = V_PAYMENT_ID
    WHERE ID              = V_TEMP_CLM_AUDIT_ID;
    IF (V_MAX_AUDIT_ORDER = V_TEMP_LIST_INDEX) THEN
      BEGIN
        UPDATE PAYMENT
        SET CLAIMED_AMOUNT_AMT = V_CLAIMED_AMT,
          CLAIMED_AMOUNT_CURR  = V_CURR,
          TOTAL_AMOUNT_AMT     = V_ACCEPTED_AMT,
          TOTAL_AMOUNT_CURR    = V_CURR,
          D_INTERNAL_COMMENTS  = D_INTERNAL_COMMENTS
          || '4.3 Upgrade',
          D_UPDATED_ON   = sysdate ,
          D_UPDATED_TIME = sysdate
        WHERE id         = V_CLAIM_PAYMENT;
      END;
    END IF;
  END IF;
  --end here
  COMMIT;
  v_xstatus     := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );
  DBMS_OUTPUT.PUT_LINE('END: ' || CURRENT_TIMESTAMP);
EXCEPTION
WHEN OTHERS THEN
  V_ERROR_LOG := 'CLAIM: ' || V_CLM_NUMBER || ' ~~~ \n' || SUBSTR(SQLERRM,0,3500);
  DBMS_OUTPUT.PUT_LINE('ERROR END: ' || CURRENT_TIMESTAMP);
  dbms_output.put_line('error occured for ::: '||V_ERROR_LOG);
  DBMS_OUTPUT.PUT_LINE(SQLERRM);
    TAV_GIM_INITIAL_SETUP.proc_insert_error_record
    (GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
    G_ISSUE_ID               =>    '2.0'                        ,                       --2
    G_TABLE_NAME             =>    'CLAIM'               ,                       --3
    G_ISSUE_COL_NAME         =>    'CLAIM_NUMBER'                  ,                       --4
    G_ISSUE_COL_VALUE        =>    V_CLM_NUMBER                 ,                       --5
    G_ISSUE_TYPE             =>    'Issue occurred for the claim'  ,    --6
    G_KEY_COL_NAME_1         =>    'Claim_audit'    ,                                 --7
    G_KEY_COL_VALUE_1        =>    V_TEMP_CLM_AUDIT_ID    ,                                 --8
    G_KEY_COL_NAME_2         =>    ''    ,                                 --9
    G_KEY_COL_VALUE_2        =>    ''    ,                                 --10
    G_KEY_COL_NAME_3         =>    ''    ,                                 --11
    g_key_col_value_3        =>    ''    ,                                 --12
    g_ora_error_message      =>      SQLERRM             ,                                 --13
    G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
    G_BLOCK_NAME             =>   'Block 3'                                       --15
    );
  ROLLBACK;
  RAISE;
END;
/
--Populate modifiers proc starts
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
      LINE_ITEM_GROUP LIG, line_item_groups ligs 
      WHERE M.LINE_ITEM_GROUP_AUDIT = LIG.EXIST_LINE_ITEM_GROUP_AUDIT
	  and ligs.line_item_groups = lig.id
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
        LINE_ITEM_GROUP LIG, line_item_groups ligs
      WHERE L.LINE_ITM_GRP_AUDIT = LIG.EXIST_LINE_ITEM_GROUP_AUDIT
	  and ligs.line_item_groups = lig.id
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
/
--Populate modifiers proc ends
--- credit memo logic starts---
create table credit_memo_error_log(
claim_number varchar2(255)
)
/
create table CLAIMS_MISSING_CREDIT_MEMO
(id number(19),
	claim_number varchar2(255)
)
/
--credit memo proc starts
create or replace
PROCEDURE UPDATE_PAYMENT_CREDIT_MEMO
AS
  --Anonymous pl/sql block to populate payment and lineitemgroup.
  CURSOR ALL_CLAIMS
  IS
    SELECT DISTINCT C.id CLAIM_ID,
      C.CLAIM_NUMBER,
      C.PAYMENT,
      CA.id CLAIM_AUDIT_ID,
      CA.LIST_INDEX CLAIM_LIST_INDEX,
      CA.PREVIOUS_STATE CLAIM_STATE,
	  Ca.PAYMENT ca_payment,
      lig.id AS lig_id,
      lig.name,
      LIGA.*
    FROM CLAIM C,
      claim_audit ca,
      line_item_group_audit LIGA,
      LINE_ITEM_GROUP lig, CLAIMS_MISSING_CREDIT_MEMO MEMO
    WHERE ca.for_claim          = c.id
   -- AND CA.PAYMENT             IS NULL
    AND LIGA.FOR_CLAIM_AUDIT (+)= CA.id
	AND C.ID = MEMO.ID
    AND lig.id(+)               = LIGA.FOR_LINE_ITEM_GRP
    AND c.state NOT            IN ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND C.CLAIM_NUMBER NOT     IN ('10218241', '10212907')
--  	and c.claim_number = '10228142'
--    AND C.id                   IN (1000000018405, 1000000019235, 1000000019267)
--      and  C.id > 1000000018430 and C.id <= 1000000019000
    ORDER BY C.id, ca.list_index;

  CURSOR ALL_LIGA (p_for_claim_audit NUMBER)
  IS
    SELECT lig.name,
      LIG.ID AS LIG_ID,
      liga.*
    FROM line_item_group_audit liga,
      LINE_ITEM_GROUP lig
    WHERE liga.FOR_CLAIM_AUDIT = p_for_claim_audit
    AND LIGA.FOR_LINE_ITEM_GRP = LIG.ID;
	
	V_CLAIMED_AMT        NUMBER(17,2)  := 0.00;
	V_ACCEPTED_AMT       NUMBER(17,2)  := 0.00;
	V_PAYMENT_ID         NUMBER(19)    := 0;
	V_ADD_PMT_INFO_ID    NUMBER(19)    := 0;
	V_ORDER              NUMBER(19)    := 0;
	V_MEMO_ID            NUMBER(19)    := 0;
	V_DISBURSED_AMT      NUMBER(17,2)  := 0.00;
	V_TOT_CR_AMT         NUMBER(17,2)  := 0.00;
	V_LINE_ITEM_GROUP_ID NUMBER(19)    := 0;
	V_CURR               VARCHAR(30)   := '';
	V_PREVIOUS_STATE     VARCHAR(50)   := '';
	V_CLM_NUMBER         VARCHAR(50)   := '';
	TYPE T_LIG_IDS IS      VARRAY(100) OF NUMBER ;
	V_LIG_IDS T_LIG_IDS;
	V_LIGA_COUNT        NUMBER     := 1;
	V_MAX_AUDIT_ORDER   NUMBER     :=0;
	V_LIGA_FOR_CA       NUMBER(19) := 0;
	V_MAX_LIG_ID        NUMBER(19) := 0;
	V_LIGA_AUDIT_EXISTS NUMBER     :=0;
	V_MODIFIER_ID       NUMBER(19) := 0;
	V_LBR_SPT_DTL_ID    NUMBER(19) := 0;
	V_START_TIME TIMESTAMP;
	V_REC_PROCESSED         NUMBER;
	V_REC_REMAINING         NUMBER;
	V_REC_NOTAPPL           NUMBER;
	V_ERROR_LOG             VARCHAR(4000) := '';
	V_COUNT                 NUMBER        := 0;
	V_TEMP_CLAIM_ID         NUMBER(19)    :=0;
	V_TEMP_LIST_INDEX       NUMBER        :=0;
	V_TEMP_CLM_AUDIT_ID     NUMBER(19)    := 0;
	V_CLAIM_ADT_INDEX       NUMBER(19)    :=0;
	V_CLAIM_STATE           VARCHAR2(50)  :='';
	V_CLAIM_PAYMENT         NUMBER(19)    :=0;
	V_PREV_LINE_ITM_GRP_ADT NUMBER(19)    :=0;
	V_EXST_LINE_ITM_GRP_ADT NUMBER(19)    :=0;
	V_PREV_TOT_CR_AMT     NUMBER(17,2)  := 0.00;
	v_clm_cred_memo_cnt		number(19) 	  := 0;
	v_clmadt_cred_memo_cnt  number(19)	  := 0;
	V_CLAIM_ADT_PAYMENT		number(19):=0;
  V_LATEST_PAYMENT		number(19):=0;
  v_cred_memo number(19):=0;

	v_program_name varchar2(255) :=null;
	v_migration_date timestamp := systimestamp;
	v_job_seq_id number(19):=0;
  
--BEGIN
BEGIN
  SELECT CURRENT_TIMESTAMP INTO V_START_TIME FROM dual;
  --DBMS_OUTPUT.PUT_LINE('Start: ' || V_START_TIME);
    v_program_name := 'UPDATE_PAYMENT_CREDIT_MEMO';
  v_migration_date := SYSTIMESTAMP;
  v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);
  
  FOR each_CLAIM IN ALL_CLAIMS
  LOOP
    BEGIN
      --if claim audit changes then initialize the below values to
      IF V_TEMP_CLM_AUDIT_ID <> EACH_CLAIM.CLAIM_AUDIT_ID THEN
        -- FIRST CREATE A PAYMENT AND THEN REINITIALIZE
        IF V_CLAIM_ADT_INDEX > 0 AND V_LIGA_FOR_CA > 0 THEN
          IF (V_CLAIM_STATE  = 'ACCEPTED_AND_CLOSED' OR V_CLAIM_STATE = 'DENIED_AND_CLOSED') THEN
            BEGIN
            --dbms_output.put_line('Total credit amount '||V_TOT_CR_AMT||'  V_PREV_TOT_CR_AMT '||V_PREV_TOT_CR_AMT||' claim audit  '||V_TEMP_CLM_AUDIT_ID||'   Claim audit state  '||V_CLAIM_STATE);
              IF (NVL(V_PREV_TOT_CR_AMT,0) - nvl(V_TOT_CR_AMT, 0) = 0) THEN
                BEGIN
                  --DO NOTHING
                  NULL;
                END;
              ELSE
                BEGIN
                  V_PREV_TOT_CR_AMT := V_TOT_CR_AMT;
                  
                  SELECT CM.ID
                  INTO V_MEMO_ID
                  FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM,
                    CREDIT_MEMO CM
                  WHERE PPCM.PAYMENT             = V_CLAIM_PAYMENT
                  AND PPCM.PREVIOUS_CREDIT_MEMOS = CM.ID
                  AND V_ORDER                    =
                    (SELECT COUNT(*)
                    FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM1,
                      CREDIT_MEMO CM1
                    WHERE PPCM1.PAYMENT             = PPCM.PAYMENT
                    AND PPCM1.PREVIOUS_CREDIT_MEMOS = CM1.ID
                    AND CM1.id                      < CM.id
                    );
                  V_ORDER := V_ORDER + 1;
                   V_LATEST_PAYMENT := V_CLAIM_ADT_PAYMENT;
 
                END;
              END IF;

            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              --DBMS_OUTPUT.PUT_LINE('###EXCEPTION OCCURED HERE FOR CLAIM '||V_CLM_NUMBER||'    :: Error Occurred '||SQLERRM);
            --  DBMS_OUTPUT.PUT_LINE('V_ORDER '||V_ORDER||' :: claim '||V_CLM_NUMBER||' :: claim Audit '||EACH_CLAIM.CLAIM_AUDIT_ID||' :: temp claim audit '||V_TEMP_CLM_AUDIT_ID);
				TAV_GIM_INITIAL_SETUP.proc_insert_error_record
               (GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
                G_ISSUE_ID               =>    '1.0'                        ,                       --2
                G_TABLE_NAME             =>    'CREDIT_MEMO'               ,                       --3
                G_ISSUE_COL_NAME         =>    'CREDIT_MEMO'                  ,                       --4
                G_ISSUE_COL_VALUE        =>    'CREDIT_MEMO'                 ,                       --5
                G_ISSUE_TYPE             =>    'Issue while looking up credit memo id ##'  ,    --6
                G_KEY_COL_NAME_1         =>    'CLAIM_STATE'    ,                                 --7
                G_KEY_COL_VALUE_1        =>    V_CLAIM_STATE    ,                                 --8
                G_KEY_COL_NAME_2         =>    'V_ORDER'    ,                                 --9
                G_KEY_COL_VALUE_2        =>    V_ORDER    ,                                 --10
                G_KEY_COL_NAME_3         =>    ''    ,                                 --11
                g_key_col_value_3        =>    ''    ,                                 --12
                g_ora_error_message      =>      SQLERRM             ,                                 --13
                G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
                G_BLOCK_NAME             =>   'Block 1'                                       --15
               );
              ROLLBACK;
            END;
          END IF;
		  
		  --Update payment of the claim audit with the active credit memo
		  update payment 
		  set ACTIVE_CREDIT_MEMO = V_MEMO_ID 
		  where id = V_CLAIM_ADT_PAYMENT;

      commit;
      
       END IF;
	   
        V_CLAIMED_AMT       := 0.00;
        V_ACCEPTED_AMT      := 0.00;
        V_MEMO_ID           := NULL;
        V_DISBURSED_AMT     := 0.00;
        V_TOT_CR_AMT        := 0.00;
        V_LIG_IDS           := T_LIG_IDS();
        V_LIGA_COUNT        := 1;
        V_LIGA_AUDIT_EXISTS := 0;
        V_CLAIM_ADT_INDEX   := 0;
        V_CLAIM_STATE       := EACH_CLAIM.CLAIM_STATE;
        V_TEMP_LIST_INDEX   := EACH_CLAIM.CLAIM_LIST_INDEX;
		V_CLAIM_ADT_PAYMENT := each_claim.ca_payment;
      END IF;
	  
      IF V_TEMP_CLAIM_ID        <> each_CLAIM.CLAIM_ID THEN

		IF  V_LIGA_FOR_CA > 0 THEN

      update payment 
      set active_credit_memo = (select max(id) from credit_memo where claim_number = v_clm_number)
      where id = V_LATEST_PAYMENT;
      commit;

			SELECT count(*) 
			into v_clm_cred_memo_cnt
			FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM,
			CREDIT_MEMO CM1
			WHERE ppcm.payment = V_CLAIM_PAYMENT
			AND PPCM.PREVIOUS_CREDIT_MEMOS = CM1.ID;
			
			SELECT COUNT(p.active_credit_memo)
			into v_clmadt_cred_memo_cnt
			FROM claim c  ,
			claim_audit ca ,
			payment p
			WHERE ca.payment        = p.id (+)
			AND ca.for_claim          = c.id
			AND c.claim_number        = V_CLM_NUMBER
			AND ca.previous_state    IN ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED')
			AND p.active_credit_memo IS NOT NULL;
			
			--If the count of credit memos for claim and claim audits does not match then log error
			if v_clm_cred_memo_cnt <> v_clmadt_cred_memo_cnt then
				insert into credit_memo_error_log values(V_CLM_NUMBER);
			end if;
		end if;
		
        V_PREVIOUS_STATE        := '';
        V_ORDER                 := 0;
        V_LIGA_FOR_CA           := 0;
        V_CLM_NUMBER            := EACH_CLAIM.CLAIM_NUMBER;
        V_TEMP_LIST_INDEX       :=0;
        V_CLAIM_PAYMENT         := EACH_CLAIM.PAYMENT;
        V_PREV_LINE_ITM_GRP_ADT :=0;
        V_EXST_LINE_ITM_GRP_ADT :=0;
        V_PREV_TOT_CR_AMT		:= 0;
        v_clm_cred_memo_cnt		:= 0;
        v_clmadt_cred_memo_cnt  := 0;
        V_LATEST_PAYMENT		:= 0;

        BEGIN
          SELECT MAX(ca.list_index)
          INTO V_MAX_AUDIT_ORDER
          FROM CLAIM_AUDIT CA
          WHERE CA.FOR_CLAIM = EACH_CLAIM.CLAIM_ID;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          --DBMS_OUTPUT.PUT_LINE('$$$EXCEPTION OCCURED HERE FOR CLAIM '||V_CLM_NUMBER||'    :: Error Occurred '||SQLERRM);
		  TAV_GIM_INITIAL_SETUP.proc_insert_error_record
               (GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
                G_ISSUE_ID               =>    '1.0'                        ,                       --2
                G_TABLE_NAME             =>    'CREDIT_MEMO'               ,                       --3
                G_ISSUE_COL_NAME         =>    'CREDIT_MEMO'                  ,                       --4
                G_ISSUE_COL_VALUE        =>    'CREDIT_MEMO'                 ,                       --5
                G_ISSUE_TYPE             =>    'Issue while looking up max list index for claim audit'  ,    --6
                G_KEY_COL_NAME_1         =>    'List index'    ,                                 --7
                G_KEY_COL_VALUE_1        =>    V_MAX_AUDIT_ORDER    ,                                 --8
                G_KEY_COL_NAME_2         =>    'Claim'    ,                                 --9
                G_KEY_COL_VALUE_2        =>    EACH_CLAIM.CLAIM_ID    ,                                 --10
                G_KEY_COL_NAME_3         =>    ''    ,                                 --11
                g_key_col_value_3        =>    ''    ,                                 --12
                g_ora_error_message      =>      SQLERRM             ,                                 --13
                G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
                G_BLOCK_NAME             =>   'Block 2'                                       --15
               );
          ROLLBACK;

          insert into credit_memo_error_log values(V_CLM_NUMBER||'  Failed');  
          commit;
        END;
      END IF;
      SELECT NVL(EACH_CLAIM.FOR_CLAIM_AUDIT, V_LIGA_FOR_CA)
      INTO V_LIGA_FOR_CA
      FROM DUAL;
      SELECT DECODE(EACH_CLAIM.FOR_CLAIM_AUDIT, NULL, 0, 1)
      INTO V_LIGA_AUDIT_EXISTS
      FROM DUAL;
      IF V_liga_audit_exists    <> 1 THEN
        V_PREV_LINE_ITM_GRP_ADT := V_EXST_LINE_ITM_GRP_ADT;
        V_EXST_LINE_ITM_GRP_ADT := NULL;
      ELSE
        V_PREV_LINE_ITM_GRP_ADT := NULL;
        V_EXST_LINE_ITM_GRP_ADT := each_claim.id;
      END IF;
      IF (V_LIGA_FOR_CA > 0) THEN
        BEGIN
          IF V_LIGA_AUDIT_EXISTS = 1 THEN
            V_CURR              := EACH_CLAIM.BASE_CURR;
            V_MAX_LIG_ID        := 0;
            V_MODIFIER_ID       := 0;
            V_LBR_SPT_DTL_ID    := 0;
            IF EACH_CLAIM.NAME   = 'Claim Amount' THEN
              V_ACCEPTED_AMT    := EACH_CLAIM.ACCEPTED_AMT;
              SELECT NVL(EACH_CLAIM.TOTAL_CREDIT_AMOUNT, 0) INTO V_TOT_CR_AMT FROM DUAL;
              SELECT NVL(EACH_CLAIM.DISBURSED_AMT, 0) INTO V_DISBURSED_AMT FROM DUAL;
            ELSE
              V_CLAIMED_AMT := V_CLAIMED_AMT + EACH_CLAIM.BASE_AMT;
            END IF;

		ELSIF V_LIGA_AUDIT_EXISTS <> 1 THEN
            BEGIN
              FOR EACH_REC IN ALL_LIGA
              (
                V_LIGA_FOR_CA
              )
              LOOP
                BEGIN
                  V_CURR           := EACH_REC.BASE_CURR;
                  V_MAX_LIG_ID     := 0;
                  V_MODIFIER_ID    := 0;
                  V_LBR_SPT_DTL_ID := 0;
                  IF EACH_REC.NAME  = 'Claim Amount' THEN
                    V_ACCEPTED_AMT := EACH_REC.ACCEPTED_AMT;
                    SELECT NVL(EACH_REC.TOTAL_CREDIT_AMOUNT, 0) INTO V_TOT_CR_AMT FROM DUAL;
                    SELECT NVL(EACH_REC.DISBURSED_AMT, 0) INTO V_DISBURSED_AMT FROM DUAL;
                  ELSE
                    V_CLAIMED_AMT := V_CLAIMED_AMT + EACH_REC.BASE_AMT;
                  END IF;
              
                END;
              END LOOP; -- all_liga LOOP ENDS HERE
            END;
          END IF; --IF V_LIGA_AUDIT_EXISTS = 1 THEN ENDS HERE
        END;
      END IF;
      V_TEMP_CLM_AUDIT_ID := EACH_CLAIM.CLAIM_AUDIT_ID;
      V_CLAIM_ADT_INDEX   := V_CLAIM_ADT_INDEX + 1;
      V_TEMP_CLAIM_ID     := EACH_CLAIM.CLAIM_ID;
      IF
        (
          V_COUNT = 100
        )
        THEN
        COMMIT;
        V_COUNT := 0;
      ELSE
        V_COUNT := V_COUNT + 1;
      END IF;
    END;
  END LOOP;
  --Updating payment for the last claim
  IF V_CLAIM_ADT_INDEX > 0 AND V_LIGA_FOR_CA > 0 THEN
    IF
      (
        V_CLAIM_STATE = 'ACCEPTED_AND_CLOSED' OR V_CLAIM_STATE = 'DENIED_AND_CLOSED'
      )
      THEN
      BEGIN
     -- dbms_output.put_line('Total credit amount '||V_TOT_CR_AMT||'  V_PREV_TOT_CR_AMT '||V_PREV_TOT_CR_AMT||' claim audit  '||V_TEMP_CLM_AUDIT_ID||'   Claim audit state  '||V_CLAIM_STATE);
        IF (NVL(V_PREV_TOT_CR_AMT,0) - nvl(V_TOT_CR_AMT, 0) = 0) THEN
          BEGIN
            --DO NOTHING
            NULL;
          END;
        ELSE
          BEGIN
           V_PREV_TOT_CR_AMT := V_TOT_CR_AMT;
            SELECT CM.ID
            INTO V_MEMO_ID
            FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM,
              CREDIT_MEMO CM
            WHERE PPCM.PAYMENT             = V_CLAIM_PAYMENT
            AND PPCM.PREVIOUS_CREDIT_MEMOS = CM.ID
            AND V_ORDER                    =
              (SELECT COUNT(*)
              FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM1,
                CREDIT_MEMO CM1
              WHERE PPCM1.PAYMENT             = PPCM.PAYMENT
              AND PPCM1.PREVIOUS_CREDIT_MEMOS = CM1.ID
              AND CM1.id                      < CM.id
              );
            V_ORDER := V_ORDER + 1;
            v_latest_payment := V_CLAIM_ADT_PAYMENT;
          END;
        END IF;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        --DBMS_OUTPUT.PUT_LINE('@@@EXCEPTION OCCURED HERE FOR CLAIM '||V_CLM_NUMBER||'  '||V_ORDER||'    :: Error Occurred '||SQLERRM);
		TAV_GIM_INITIAL_SETUP.proc_insert_error_record
		(GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
		G_ISSUE_ID               =>    '1.0'                        ,                       --2
		G_TABLE_NAME             =>    'CREDIT_MEMO'               ,                       --3
		G_ISSUE_COL_NAME         =>    'CREDIT_MEMO'                  ,                       --4
		G_ISSUE_COL_VALUE        =>    'CREDIT_MEMO'                 ,                       --5
		G_ISSUE_TYPE             =>    'Issue while looking up credit memo id ##'  ,    --6
		G_KEY_COL_NAME_1         =>    'CLAIM_STATE'    ,                                 --7
		G_KEY_COL_VALUE_1        =>    V_CLAIM_STATE    ,                                 --8
		G_KEY_COL_NAME_2         =>    'V_ORDER'    ,                                 --9
		G_KEY_COL_VALUE_2        =>    V_ORDER    ,                                 --10
		G_KEY_COL_NAME_3         =>    ''    ,                                 --11
		g_key_col_value_3        =>    ''    ,                                 --12
		g_ora_error_message      =>      SQLERRM             ,                                 --13
		G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
		G_BLOCK_NAME             =>   'Block 3'                                       --15
		);
        ROLLBACK;
        
        insert into credit_memo_error_log values(V_CLM_NUMBER||'  Failed');  
        commit;
      END;
    END IF;
    
	--Update payment of the claim audit with the active credit memo
	update payment 
	set ACTIVE_CREDIT_MEMO = V_MEMO_ID 
	where id = V_CLAIM_ADT_PAYMENT;
  
  commit;
	
  update payment 
  set active_credit_memo = (select max(id) from credit_memo where claim_number = v_clm_number)
  where id = V_LATEST_PAYMENT;
       commit;
  
	SELECT count(*) 
	into v_clm_cred_memo_cnt
	FROM PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM,
	CREDIT_MEMO CM1
	WHERE ppcm.payment = V_CLAIM_PAYMENT
	AND PPCM.PREVIOUS_CREDIT_MEMOS = CM1.ID;
	
	SELECT COUNT(p.active_credit_memo)
	into v_clmadt_cred_memo_cnt
	FROM claim c  ,
	claim_audit ca ,
	payment p
	WHERE ca.payment        = p.id (+)
	AND ca.for_claim          = c.id
	AND c.claim_number        = V_CLM_NUMBER
	AND ca.previous_state    IN ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED')
	AND p.active_credit_memo IS NOT NULL;
	
	--If the count of credit memos for claim and claim audits does not match then log error
	if v_clm_cred_memo_cnt <> v_clmadt_cred_memo_cnt then
		insert into credit_memo_error_log values(V_CLM_NUMBER);
	end if;
	
  END IF;
  --end here
  COMMIT;
  DBMS_OUTPUT.PUT_LINE('END: ' || CURRENT_TIMESTAMP);
EXCEPTION
WHEN OTHERS THEN
  V_ERROR_LOG := 'CLAIM: ' || V_CLM_NUMBER || ' ~~~ \n' || SUBSTR(SQLERRM,0,3500);
  --DBMS_OUTPUT.PUT_LINE('ERROR END: ' || CURRENT_TIMESTAMP);
  --dbms_output.put_line('error occured for ::: '||V_ERROR_LOG);
  --DBMS_OUTPUT.PUT_LINE(SQLERRM);
	TAV_GIM_INITIAL_SETUP.proc_insert_error_record
	(GJOB_SEQ_ID              =>    v_job_seq_id               ,                       --1
	G_ISSUE_ID               =>    '1.0'                        ,                       --2
	G_TABLE_NAME             =>    ''               ,                       --3
	G_ISSUE_COL_NAME         =>    ''                  ,                       --4
	G_ISSUE_COL_VALUE        =>    ''                 ,                       --5
	G_ISSUE_TYPE             =>    'Issue occurred at program level'  ,    --6
	G_KEY_COL_NAME_1         =>    'V_CLM_NUMBER'    ,                                 --7
	G_KEY_COL_VALUE_1        =>    V_CLM_NUMBER    ,                                 --8
	G_KEY_COL_NAME_2         =>    'CLM_AUDIT_ID'    ,                                 --9
	G_KEY_COL_VALUE_2        =>    V_TEMP_CLM_AUDIT_ID    ,                                 --10
	G_KEY_COL_NAME_3         =>    ''    ,                                 --11
	g_key_col_value_3        =>    ''    ,                                 --12
	g_ora_error_message      =>      SQLERRM             ,                                 --13
	G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
	G_BLOCK_NAME             =>   'Block 1'                                       --15
	);
  ROLLBACK;
END;
/
--credit memo proc ends here
-----credit memo logic ends
begin
UPDATE_CLAIM_AUDIT_PAYMENT_NEW();
end;
/
begin
POPULATE_PAYMENT_MODIFIERS();
end;
/
insert into CLAIMS_MISSING_CREDIT_MEMO(ID, CLAIM_NUMBER)(
select CM1.ID, CM1.CLAIM_NUMBER1 AS CLAIM_NUMBER from 
(SELECT C.ID, c.claim_number as claim_number1, count(*) as count1
			FROM claim c, PAYMENT_PREVIOUS_CREDIT_MEMOS PPCM,
			CREDIT_MEMO CM1
			WHERE ppcm.payment = c.payment
			AND PPCM.PREVIOUS_CREDIT_MEMOS = CM1.ID group by C.ID,c.claim_number HAVING count(*) > 1) cm1,
(select C.ID,c.claim_number as claim_number2, count(*) as count2
             from claim c
             , claim_audit ca
             , payment p
             where ca.payment = p.id
             and ca.for_claim = c.id
             and p.active_credit_memo is not null group by C.ID, c.claim_number) cm2
where cm1.claim_number1 = cm2.claim_number2 and cm1.count1 <> cm2.count2)
/
commit
/
begin
UPDATE_PAYMENT_CREDIT_MEMO();
end;
/
ALTER TABLE LINE_ITEM_GROUP SET UNUSED (EXIST_LINE_ITEM_GROUP_AUDIT)
/
ALTER TABLE LINE_ITEM_GROUP SET UNUSED (PREV_LINE_ITEM_GROUP_AUDIT)
/
ALTER TABLE LINE_ITEM SET UNUSED (LINE_ITEM_GROUP_AUDIT)
/
ALTER TABLE LINE_ITEM SET UNUSED (LINE_ITEM_GROUP)
/
ALTER TABLE LABOR_SPLIT_DETAIL_AUDIT SET UNUSED (LINE_ITEM_GROUP_AUDIT)
/
ALTER TABLE LABOR_SPLIT_DETAIL_AUDIT SET UNUSED (LINE_ITEM_GROUP)
/
ALTER TABLE MODIFIERS RENAME TO MODIFIERS_BACKUP_HISTORY
/
ALTER TABLE LABOR_SPLIT_DETAILS RENAME TO LABOR_SPLIT_DETAILS_BAckup
/
alter table MODIFIERS_STAGE add CONSTRAINT MODIFIERS_LINE1_ITE_FK1 FOREIGN KEY ("MODIFIERS") REFERENCES LINE_ITEM ("ID")
/
alter table MODIFIERS_STAGE add CONSTRAINT MODIFIERS_LINE_ITEM_GROUP_FK1 FOREIGN KEY ("LINE_ITEM_GROUP") REFERENCES LINE_ITEM_GROUP ("ID")
/
ALTER TABLE MODIFIERS_STAGE RENAME TO MODIFIERS
/
alter table LABOR_SPLIT_DETAILS_STAGE add CONSTRAINT SLT_DTL_GRP_AUDIT_FK1 FOREIGN KEY ("LABOR_SPLIT_DETAIL_AUDIT") REFERENCES LABOR_SPLIT_DETAIL_AUDIT ("ID")
/
alter table LABOR_SPLIT_DETAILS_STAGE add CONSTRAINT LBR_SPLT_LINE_ITEM_GROUP_FK1 FOREIGN KEY ("LINE_ITEM_GROUP") 	REFERENCES LINE_ITEM_GROUP ("ID") 
/
ALTER TABLE LABOR_SPLIT_DETAILS_STAGE RENAME TO LABOR_SPLIT_DETAILS
/
--CREATE UNIQUE INDEX modifiers_unq_indx ON MODIFIERS (MODIFIERS)
--/
COMMIT
/