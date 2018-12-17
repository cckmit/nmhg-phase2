--Purpose    : Scripts for creating Recovery tables, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

CREATE SEQUENCE RECOVERABLE_PART_SEQ
START WITH 200
INCREMENT BY 20
MINVALUE 0
MAXVALUE 999999999999999999999999999
CACHE 20
NOCYCLE 
NOORDER
/
COMMIT
/
CREATE TABLE RECOVERABLE_PART
(
  ID NUMBER(19),
  OEM_PART NUMBER(19),
  QUANTITY NUMBER(19),
  D_CREATED_ON DATE, 
  D_INTERNAL_COMMENTS VARCHAR2(255 CHAR), 
  D_UPDATED_ON DATE, 
  D_LAST_UPDATED_BY NUMBER(19,0), 
  D_CREATED_TIME TIMESTAMP (6), 
  D_UPDATED_TIME TIMESTAMP (6), 
  D_ACTIVE NUMBER(1,0) DEFAULT 1,
  MATERIAL_COST_AMT NUMBER(19,2),  
  MATERIAL_COST_CURR VARCHAR2(255 CHAR),
  COST_PRICE_PER_UNIT_AMT NUMBER(19,2),
  COST_PRICE_PER_UNIT_CURR VARCHAR2(255 CHAR),
  SUPPLIER_RETURN_NEEDED   NUMBER(1,0),
  SUPPLIER_ITEM            NUMBER(19,0)
)

/
COMMIT
/
ALTER TABLE RECOVERABLE_PART ADD (
  CONSTRAINT PK_RECOVERABLE_PART
 PRIMARY KEY (ID))
/
--ALTER TABLE RECOVERABLE_PART ADD (
 -- CONSTRAINT FK_PART_RECOVERABLE_PART
 --FOREIGN KEY (PART) 
 --REFERENCES ITEM (ID))
--/
ALTER TABLE recoverable_part ADD (
	CONSTRAINT FK_REC_OEM_PART
	FOREIGN KEY (OEM_PART) 
	REFERENCES OEM_PART_REPLACED (ID)
)
/
--ALTER TABLE RECOVERABLE_PART ADD (
--	CONSTRAINT FK_REC_INSTALLED_PART
--	FOREIGN KEY (INSTALLED_PART) 
--	REFERENCES INSTALLED_PARTS (ID)
--)
--/
COMMIT
/
CREATE TABLE RECOVERABLE_PART_BAR_CODES
(
	RECOVERABLE_PART NUMBER(19),
	BAR_CODES_ELEMENT VARCHAR2(255)
)
/
COMMIT
/
ALTER TABLE RECOVERABLE_PART_BAR_CODES ADD (
	CONSTRAINT FK_P_R_PART_BAR_CODES
	FOREIGN KEY (RECOVERABLE_PART)
	REFERENCES RECOVERABLE_PART (ID)
)
/
COMMIT
/
CREATE SEQUENCE RECOVERY_CLAIM_INFO_SEQ
START WITH 200
INCREMENT BY 20
MINVALUE 0
MAXVALUE 999999999999999999999999999
CACHE 20
NOCYCLE 
NOORDER
/
COMMIT
/
CREATE TABLE RECOVERY_CLAIM_INFO
(
	ID NUMBER(19),
	CONTRACT NUMBER(19),
	--ACTIVE NUMBER(1,0) DEFAULT 1,
	D_CREATED_ON DATE, 
	D_INTERNAL_COMMENTS VARCHAR2(255 CHAR), 
	D_UPDATED_ON DATE, 
	D_LAST_UPDATED_BY NUMBER(19,0), 
	D_CREATED_TIME TIMESTAMP (6), 
	D_UPDATED_TIME TIMESTAMP (6), 
	D_ACTIVE NUMBER(1,0) DEFAULT 1,
	RECOVERY_CLAIM NUMBER(19,0),
	CAUSAL_PART_RECOVERY NUMBER(1,0)
)
/
COMMIT
/
ALTER TABLE RECOVERY_CLAIM_INFO ADD (
  CONSTRAINT PK_RECOVERY_CLAIM_INFO
  PRIMARY KEY (ID)
)
/
ALTER TABLE RECOVERY_CLAIM_INFO ADD (
  CONSTRAINT FK_C_RECOVERY_CLAIM_INFO
  FOREIGN KEY (CONTRACT) 
  REFERENCES CONTRACT (ID)
)
/
ALTER TABLE RECOVERY_CLAIM_INFO ADD (
	CONSTRAINT FK_REC_RECOVERY_CLAIM
	FOREIGN KEY (RECOVERY_CLAIM) 
	REFERENCES RECOVERY_CLAIM (ID)
)
/
COMMIT
/
CREATE TABLE REC_CLAIM_INFO_REC_PARTS
(
	RECOVERY_CLAIM_INFO NUMBER(19),
	RECOVERABLE_PARTS NUMBER(19)
)
/
COMMIT
/
ALTER TABLE REC_CLAIM_INFO_REC_PARTS ADD (
	CONSTRAINT FK_R_C_RECOVERABLE_PARTS
	FOREIGN KEY (RECOVERY_CLAIM_INFO) 
	REFERENCES RECOVERY_CLAIM_INFO (ID)
)
/
ALTER TABLE REC_CLAIM_INFO_REC_PARTS ADD (
	CONSTRAINT FK_R_P_RECOVERABLE_PARTS
	FOREIGN KEY (RECOVERABLE_PARTS) 
	REFERENCES RECOVERABLE_PART (ID)
)
/
COMMIT
/
--CREATE TABLE REC_CLAIM_INFO_REC_CLAIMS
--(
--	RECOVERY_CLAIM_INFO NUMBER(19),
--	RECOVERY_CLAIMS NUMBER(19)
--)
--/
--COMMIT
--/
--ALTER TABLE REC_CLAIM_INFO_REC_CLAIMS ADD (
	--CONSTRAINT FK_R_RECOVERY_CLAIMS
--	FOREIGN KEY (RECOVERY_CLAIM_INFO) 
--	REFERENCES RECOVERY_CLAIM_INFO (ID)
--)
--/
--ALTER TABLE REC_CLAIM_INFO_REC_CLAIMS ADD (
--	CONSTRAINT FK_R_RECOVERABLE_PARTS
--	FOREIGN KEY (RECOVERY_CLAIMS) 
--	REFERENCES RECOVERY_CLAIM (ID)
--)
--/
--COMMIT
--/
CREATE SEQUENCE RECOVERY_INFO_SEQ
START WITH 200
INCREMENT BY 20
MINVALUE 0
MAXVALUE 999999999999999999999999999
CACHE 20
NOCYCLE 
NOORDER
/
COMMIT
/
CREATE TABLE RECOVERY_INFO(
  ID NUMBER(19),
  WARRANTY_CLAIM NUMBER(19),
  --CAUSAL_PART_RECOVERY NUMBER(19),
  D_CREATED_ON DATE, 
  D_INTERNAL_COMMENTS VARCHAR2(255 CHAR), 
  D_UPDATED_ON DATE, 
  D_LAST_UPDATED_BY NUMBER(19,0), 
  D_CREATED_TIME TIMESTAMP (6), 
  D_UPDATED_TIME TIMESTAMP (6), 
  D_ACTIVE NUMBER(1,0) DEFAULT 1,
  BUSINESS_UNIT_INFO VARCHAR2(255 CHAR),
  SAVED_AT_PART_LEVEL NUMBER(1,0)
)
/
COMMIT
/
ALTER TABLE RECOVERY_INFO ADD (
  CONSTRAINT PK_RRECOVERY_INFO
  PRIMARY KEY (ID)
)
/
ALTER TABLE RECOVERY_INFO ADD (
  CONSTRAINT FK_W_CLAIM_RECOVERY_INFO
  FOREIGN KEY (WARRANTY_CLAIM) 
  REFERENCES CLAIM (ID)
)
/
ALTER TABLE RECOVERY_INFO ADD (
  CONSTRAINT UC_REC_INFO_CLAIM 
  UNIQUE (WARRANTY_CLAIM)
)
/
--ALTER TABLE RECOVERY_INFO ADD (
 -- CONSTRAINT FK_C_P_REC_INFO
  --FOREIGN KEY (CAUSAL_PART_RECOVERY) 
  --REFERENCES RECOVERY_CLAIM_INFO (ID)
--)
--/
COMMIT
/
CREATE TABLE REC_INFO_REP_PARTS_REC(	
  RECOVERY_INFO NUMBER(19),
  REPLACED_PARTS_RECOVERY NUMBER(19)
)
/
COMMIT
/
ALTER TABLE REC_INFO_REP_PARTS_REC ADD (
	CONSTRAINT FK_R_REP_PARTS_REC
	FOREIGN KEY (RECOVERY_INFO) 
	REFERENCES RECOVERY_INFO (ID)
)
/
ALTER TABLE REC_INFO_REP_PARTS_REC ADD (
	CONSTRAINT FK_R_P_REP_PARTS_REC
	FOREIGN KEY (REPLACED_PARTS_RECOVERY) 
	REFERENCES RECOVERY_CLAIM_INFO (ID)
)
/
COMMIT
/
CREATE TABLE RECOVERY_INFO_COMMENTS(
  RECOVERY_INFO NUMBER(19),
  COMMENTS NUMBER(19)
)
/
COMMIT
/
ALTER TABLE RECOVERY_INFO_COMMENTS ADD (
	CONSTRAINT FK_R_INFO_COMMENTS
	FOREIGN KEY (RECOVERY_INFO) 
	REFERENCES RECOVERY_INFO (ID)
)
/
ALTER TABLE RECOVERY_INFO_COMMENTS ADD (
	CONSTRAINT FK_C_INFO_COMMENTS
	FOREIGN KEY (COMMENTS) 
	REFERENCES USER_COMMENT (ID)
)
/
COMMIT
/
ALTER TABLE CLAIM ADD(RECOVERY_INFO NUMBER(19))
/
ALTER TABLE CLAIM ADD (
	CONSTRAINT FK_CLAIM_REC_INFO
	FOREIGN KEY (RECOVERY_INFO) 
	REFERENCES RECOVERY_INFO (ID)
)
/
COMMIT
/
ALTER TABLE SUPPLIER_PART_RETURN ADD (SUPPLIER_SHIPMENT NUMBER(19,0))
/
ALTER TABLE SUPPLIER_PART_RETURN ADD (
	CONSTRAINT FK_SUP_PART_RETURN_SHIPMENT
	FOREIGN KEY (SUPPLIER_SHIPMENT) 
	REFERENCES SHIPMENT(ID)
)
/
ALTER TABLE SUPPLIER_PART_RETURN ADD (BAR_CODE VARCHAR2(255))
/
ALTER TABLE SUPPLIER_PART_RETURN ADD (RECOVERABLE_PART NUMBER(19,0))
/
ALTER TABLE SUPPLIER_PART_RETURN ADD (
	CONSTRAINT FK_SUP_PART_RETURN_REC_PART
	FOREIGN KEY (RECOVERABLE_PART) 
	REFERENCES RECOVERABLE_PART(ID)
)
/
COMMIT
/
INSERT INTO RECOVERY_INFO(
	ID, 
	WARRANTY_CLAIM, 
	D_CREATED_ON, 
	D_INTERNAL_COMMENTS, 
	D_UPDATED_ON, 
	D_LAST_UPDATED_BY, 
	D_CREATED_TIME, 
	D_UPDATED_TIME, 
	D_ACTIVE, 
	BUSINESS_UNIT_INFO,
	SAVED_AT_PART_LEVEL
) ( 
	SELECT 
	RECOVERY_INFO_SEQ.NEXTVAL, 
	ID,
	SYSDATE,
	'4.3 Upgrade',
	SYSDATE,
	56,
	SYSDATE,
	SYSDATE,
	1,
	BUSINESS_UNIT_INFO,
	1
	FROM CLAIM A 
	WHERE EXISTS
	(SELECT *
	FROM RECOVERY_CLAIM
	WHERE CLAIM = A.ID)
)
/
COMMIT
/
INSERT INTO RECOVERY_CLAIM_INFO(
	ID,
	CONTRACT,
	--ACTIVE,
	D_CREATED_ON,
	D_INTERNAL_COMMENTS,
	D_UPDATED_ON,
	D_LAST_UPDATED_BY,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE,
	RECOVERY_CLAIM,
	CAUSAL_PART_RECOVERY
)(
	SELECT
	RECOVERY_CLAIM_INFO_SEQ.NEXTVAL, 
	CONTRACT,
	--1,
	SYSDATE,
	'4.3 Upgrade',
	SYSDATE,
	56,
	SYSDATE,
	SYSDATE,
	1,
	ID,
	1
	FROM RECOVERY_CLAIM
	WHERE RECOVERY_CLAIM_STATE <> 'WNTY_CLAIM_REOPENED'
)
/
COMMIT
/
INSERT INTO REC_INFO_REP_PARTS_REC(
	RECOVERY_INFO,
	REPLACED_PARTS_RECOVERY
)(
	SELECT B.ID, A.ID 
	FROM RECOVERY_CLAIM_INFO A, RECOVERY_INFO B ,RECOVERY_CLAIM C
	WHERE C.ID = A.RECOVERY_CLAIM
	AND C.CLAIM = B.WARRANTY_CLAIM
)
/
COMMIT
/
DECLARE
  CURSOR ALL_REC
  IS
    SELECT A.ID,
      C.BUSINESS_UNIT_INFO,
      A.RECOVERY_CLAIM
    FROM RECOVERY_CLAIM_INFO A,
      RECOVERY_CLAIM B,
      CLAIM C
    WHERE A.RECOVERY_CLAIM = B.ID
    AND B.CLAIM            = C.ID;
  V_COUNT NUMBER(19,0);
BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
      IF EACH_REC.BUSINESS_UNIT_INFO = 'Hussmann' THEN
        --INSERT THE HUSSMANN PARTS
        INSERT
        INTO RECOVERABLE_PART
          (
            ID,
            OEM_PART,
            QUANTITY,
            D_CREATED_ON,
            D_INTERNAL_COMMENTS,
            D_UPDATED_ON,
            D_LAST_UPDATED_BY,
            D_CREATED_TIME,
            D_UPDATED_TIME,
            D_ACTIVE,
            MATERIAL_COST_AMT,
            MATERIAL_COST_CURR,
            COST_PRICE_PER_UNIT_AMT,
            COST_PRICE_PER_UNIT_CURR,
			SUPPLIER_ITEM,
			SUPPLIER_RETURN_NEEDED
          )
          (SELECT recoverable_part_seq.nextval,
              a.*
            FROM
              (SELECT opr.id                 AS oem_part,
                opr.number_of_units          AS quantity,
                sysdate                      AS d_created_on,
                '4.3 Upgrade'                AS d_internal_comments,
                sysdate                      AS d_updated_on,
                56                           AS d_last_updated_by,
                sysdate                      AS d_created_time,
                sysdate                      AS d_updated_time,
                1                            AS d_active,
                opr.material_cost_amt        AS material_cost_amt,
                opr.material_cost_curr       AS material_cost_curr ,
                opr.cost_price_per_unit_amt  AS cost_price_per_unit_amt,
                OPR.COST_PRICE_PER_UNIT_CURR AS COST_PRICE_PER_UNIT_CURR,
				opr.SUPPLIER_ITEM			 AS SUPPLIER_ITEM,
				opr.SUPPLIER_RETURN_NEEDED	 AS SUPPLIER_RETURN_NEEDED
              FROM RECOVERY_CLAIM RC,
                CLAIM CLM,
                SERVICE_INFORMATION SI,
                SERVICE SER,
                HUSS_PARTS_REPLACED_INSTALLED HPI,
                OEM_PART_REPLACED OPR,
                contract cnt
              WHERE RC.CLAIM                       = CLM.ID
              AND CLM.SERVICE_INFORMATION          = SI.ID
              AND SI.SERVICE_DETAIL                = SER.ID
              AND HPI.SERVICE_DETAIL               = SER.ID
              AND OPR.OEM_REPLACED_PARTS           = HPI.ID
              AND CNT.ID                           = RC.CONTRACT
              AND CNT.COLLATERAL_DAMAGE_TO_BE_PAID = 1
              AND RC.ID                            = EACH_REC.RECOVERY_CLAIM
              UNION
              SELECT opr.id                  AS oem_part,
                opr.number_of_units          AS quantity,
                sysdate                      AS d_created_on,
                '4.3 Upgrade'                AS d_internal_comments,
                sysdate                      AS d_updated_on,
                56                           AS d_last_updated_by,
                sysdate                      AS d_created_time,
                sysdate                      AS d_updated_time,
                1                            AS d_active,
                opr.material_cost_amt        AS MATERIAL_COST_AMT,
                opr.material_cost_curr       AS material_cost_curr,
                opr.cost_price_per_unit_amt  AS cost_price_per_unit_amt,
                OPR.COST_PRICE_PER_UNIT_CURR AS COST_PRICE_PER_UNIT_CURR,
				opr.SUPPLIER_ITEM			 AS SUPPLIER_ITEM,
				opr.SUPPLIER_RETURN_NEEDED	 AS SUPPLIER_RETURN_NEEDED
              FROM RECOVERY_CLAIM RC,
                CLAIM CLM,
                SERVICE_INFORMATION SI,
                SERVICE SER,
                HUSS_PARTS_REPLACED_INSTALLED HPI,
                OEM_PART_REPLACED OPR,
                contract cnt
              WHERE RC.CLAIM                       = CLM.ID
              AND CLM.SERVICE_INFORMATION          = SI.ID
              AND SI.SERVICE_DETAIL                = SER.ID
              AND HPI.SERVICE_DETAIL               = SER.ID
              AND OPR.OEM_REPLACED_PARTS           = HPI.ID
              AND CNT.ID                           = RC.CONTRACT
              AND CNT.COLLATERAL_DAMAGE_TO_BE_PAID = 0
              AND OPR.ITEM_REF_UNSZED_ITEM         = SI.CAUSAL_PART
              AND RC.ID                            = EACH_REC.RECOVERY_CLAIM
              ) a
          );
      ELSE
        --INSERT NON HUSSMANN PARTS
        INSERT
        INTO RECOVERABLE_PART
          (
            ID,
            OEM_PART,
            QUANTITY,
            D_CREATED_ON,
            D_INTERNAL_COMMENTS,
            D_UPDATED_ON,
            D_LAST_UPDATED_BY,
            D_CREATED_TIME,
            D_UPDATED_TIME,
            D_ACTIVE,
            MATERIAL_COST_AMT,
            MATERIAL_COST_CURR,
            COST_PRICE_PER_UNIT_AMT,
            COST_PRICE_PER_UNIT_CURR,
			SUPPLIER_ITEM,
			SUPPLIER_RETURN_NEEDED
          )
          (SELECT recoverable_part_seq.nextval,
              b.*
            FROM
              (SELECT opr.id                 AS oem_part,
                opr.number_of_units          AS quantity,
                sysdate                      AS d_created_on,
                '4.3 Upgrade'                AS d_internal_comments,
                sysdate                      AS d_updated_on,
                56                           AS d_last_updated_by,
                sysdate                      AS d_created_time,
                sysdate                      AS d_updated_time,
                1                            AS d_active,
                opr.material_cost_amt        AS material_cost_amt,
                opr.material_cost_curr       AS material_cost_curr,
                opr.cost_price_per_unit_amt  AS cost_price_per_unit_amt,
                OPR.COST_PRICE_PER_UNIT_CURR AS cost_price_per_unit_curr,
				opr.SUPPLIER_ITEM			 AS SUPPLIER_ITEM,
				opr.SUPPLIER_RETURN_NEEDED	 AS SUPPLIER_RETURN_NEEDED
              FROM RECOVERY_CLAIM RC,
                CLAIM CLM,
                SERVICE_INFORMATION SI,
                SERVICE SER,
                SERVICE_OEMPARTS_REPLACED SOR,
                OEM_PART_REPLACED OPR ,
                contract cnt
              WHERE RC.CLAIM                       = CLM.ID
              AND CLM.SERVICE_INFORMATION          = SI.ID
              AND SI.SERVICE_DETAIL                = SER.ID
              AND SER.ID                           = SOR.SERVICE
              AND SOR.OEMPARTS_REPLACED            = OPR.ID
              AND RC.ID                            = EACH_REC.RECOVERY_CLAIM
              AND CNT.ID                           = RC.CONTRACT
              AND CNT.COLLATERAL_DAMAGE_TO_BE_PAID = 1
              AND RC.ID                            = EACH_REC.RECOVERY_CLAIM
              UNION
              SELECT opr.id                  AS oem_part,
                opr.number_of_units          AS quantity,
                sysdate                      AS d_created_on,
                '4.3 Upgrade'                AS d_internal_comments,
                sysdate                      AS d_updated_on,
                56                           AS d_last_updated_by,
                sysdate                      AS d_created_time,
                sysdate                      AS d_updated_time,
                1                            AS d_active,
                opr.material_cost_amt        AS material_cost_amt,
                opr.material_cost_curr       AS material_cost_curr,
                opr.cost_price_per_unit_amt  AS cost_price_per_unit_amt,
                OPR.COST_PRICE_PER_UNIT_CURR AS COST_PRICE_PER_UNIT_CURR,
				opr.SUPPLIER_ITEM			 AS SUPPLIER_ITEM,
				opr.SUPPLIER_RETURN_NEEDED	 AS SUPPLIER_RETURN_NEEDED
              FROM RECOVERY_CLAIM RC,
                CLAIM CLM,
                SERVICE_INFORMATION SI,
                SERVICE SER,
                SERVICE_OEMPARTS_REPLACED SOR,
                OEM_PART_REPLACED OPR ,
                CONTRACT CNT
              WHERE RC.CLAIM                       = CLM.ID
              AND CLM.SERVICE_INFORMATION          = SI.ID
              AND SI.SERVICE_DETAIL                = SER.ID
              AND SER.ID                           = SOR.SERVICE
              AND SOR.OEMPARTS_REPLACED            = OPR.ID
              AND RC.ID                            = EACH_REC.RECOVERY_CLAIM
              AND CNT.ID                           = RC.CONTRACT
              AND CNT.COLLATERAL_DAMAGE_TO_BE_PAID = 0
              AND OPR.ITEM_REF_UNSZED_ITEM         = SI.CAUSAL_PART
              ) b
          );
      END IF;
      IF V_COUNT = 10 THEN
        COMMIT;
        V_COUNT := -1;
      END IF;
      V_COUNT := V_COUNT + 1;
    END;
  END LOOP;
  COMMIT;
  INSERT
  INTO REC_CLAIM_INFO_REC_PARTS
    (
      RECOVERY_CLAIM_INFO,
      RECOVERABLE_PARTS
    )
    (SELECT rirpr.replaced_parts_recovery,
        rp.id
      FROM recoverable_part rp,
        oem_part_replaced opr,
        huss_parts_replaced_installed hpri,
        service_information si,
        claim clm,
        recovery_info ri,
        rec_info_rep_parts_rec rirpr
      WHERE NOT EXISTS
        (SELECT 1 FROM rec_claim_info_rec_parts WHERE recoverable_parts = rp.id
        )
      AND rp.oem_part            = opr.id
      AND opr.oem_replaced_parts = hpri.id
      AND hpri.service_detail    = si.service_detail
      AND si.id                  = clm.service_information
      AND clm.id                 = ri.warranty_claim
      AND ri.id                  = rirpr.recovery_info
    );
  COMMIT;
  UPDATE supplier_part_return spr
  SET recoverable_part =
    (SELECT rp.id
    FROM recoverable_part rp,
      oem_part_replaced opr
    WHERE rp.oem_part            = opr.id
    AND opr.supplier_part_return = spr.id
    );
  COMMIT;
EXCEPTION
WHEN OTHERS THEN
  ROLLBACK;
END;
/
COMMIT
/
ALTER TABLE RECOVERY_CLAIM ADD(RECOVERY_CLAIM_INFO NUMBER(19,0))
/
ALTER TABLE RECOVERY_CLAIM ADD (
	CONSTRAINT FK_REC_RECOVERY_CLAIM_INFO
	FOREIGN KEY (RECOVERY_CLAIM_INFO) 
	REFERENCES RECOVERY_CLAIM_INFO (ID)
)
/
UPDATE RECOVERY_CLAIM RC SET RECOVERY_CLAIM_INFO = 
	(SELECT RCI.ID FROM RECOVERY_CLAIM_INFO RCI WHERE RCI.RECOVERY_CLAIM = RC.ID)
/
INSERT INTO ROLE values ((select max(id)+1 from role),'supplierRecoveryInitiator',1,sysdate,'Supplier Recovery Claim Initiator|Internal',sysdate,NULL,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'Supplier Recovery Claim Initiator')
/
COMMIT
/