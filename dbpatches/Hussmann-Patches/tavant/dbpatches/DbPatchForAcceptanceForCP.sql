CREATE TABLE ACCEPTANCE_REASON_FOR_CP
(
  CODE                 VARCHAR2(255 CHAR)       NOT NULL,
  DESCRIPTION          VARCHAR2(255 CHAR),
  STATE                VARCHAR2(255 CHAR),
  VERSION              NUMBER(10)               NOT NULL,
  D_CREATED_ON         DATE,
  D_INTERNAL_COMMENTS  VARCHAR2(255 CHAR),
  D_UPDATED_ON         DATE,
  D_LAST_UPDATED_BY    NUMBER(19),
  D_CREATED_TIME       TIMESTAMP(6),
  D_UPDATED_TIME       TIMESTAMP(6),
  BUSINESS_UNIT_INFO   VARCHAR2(255 CHAR)
)
/
CREATE UNIQUE INDEX ACCEPTANCE_REASON_FOR_CP_PK ON ACCEPTANCE_REASON_FOR_CP(CODE)
/
ALTER TABLE ACCEPTANCE_REASON_FOR_CP ADD (
  CONSTRAINT ACCEPTANCE_REASON_FOR_CP_PK
 PRIMARY KEY (CODE))
/
ALTER TABLE ACCEPTANCE_REASON_FOR_CP ADD (
  CONSTRAINT ACCPT_RSN_CP_LST_UPDT_BY_FK 
 FOREIGN KEY (D_LAST_UPDATED_BY) 
 REFERENCES ORG_USER (ID))
/
ALTER TABLE CLAIM
ADD (ACCEPTANCE_REASON_FOR_CP  NUMBER(19))
/
ALTER TABLE CLAIM ADD (
  CONSTRAINT CLAIM_ACCEPTANCEREASONFORCP_FK 
 FOREIGN KEY (ACCEPTANCE_REASON_FOR_CP) 
 REFERENCES LIST_OF_VALUES (ID))
/
ALTER TABLE LINE_ITEM_GROUP
ADD (PERCENTAGE_ACCEPTANCE_FOR_CP NUMBER(9,2))
/
ALTER TABLE LINE_ITEM_GROUP_AUDIT
ADD (ACCEPTED_AMT_FOR_CP NUMBER(9,2))
/
ALTER TABLE LINE_ITEM_GROUP_AUDIT
ADD (ACCEPTED_CURR_FOR_CP VARCHAR2(255))
/
ALTER TABLE LINE_ITEM_GROUP_AUDIT
ADD (PERCENTAGE_ACCEPTANCE_FOR_CP  NUMBER(19,2))
/
ALTER TABLE CLAIM ADD (CP_REVIEWED NUMBER(1))
/
ALTER TABLE LINE_ITEM_GROUP_AUDIT
ADD (BASE_AMT_CP  NUMBER(19,2))
/
ALTER TABLE LINE_ITEM_GROUP_AUDIT
ADD (BASE_CURR_CP  VARCHAR2 (765))
/
ALTER TABLE LINE_ITEM_GROUP_AUDIT
ADD (GROUPTOTAL_AMT_CP  NUMBER(19,2))
/
ALTER TABLE LINE_ITEM_GROUP_AUDIT
ADD (GROUPTOTAL_CURR_CP  VARCHAR2 (765))
/