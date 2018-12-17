--Purpose    : Create new table called ALARMCODE
--Author     : Surajdeo Prasad
--Created On : 03-March-10


CREATE TABLE ALARM_CODE
( ID NUMBER(19) PRIMARY KEY,
  VERSION NUMBER(19),
  BUSINESS_UNIT_INFO  VARCHAR2(255),
  CODE VARCHAR2(255),
  DESCRIPTION	VARCHAR2(255),
  D_CREATED_ON DATE, 
  D_INTERNAL_COMMENTS VARCHAR2(255), 
  D_UPDATED_ON DATE, 
  D_LAST_UPDATED_BY NUMBER(19,0), 
  D_CREATED_TIME TIMESTAMP (6), 
  D_UPDATED_TIME TIMESTAMP (6), 
  D_ACTIVE NUMBER(1,0) DEFAULT 1
)
/
CREATE TABLE ITEM_GROUPS_ALARM_CODE
 (
    ALARM_CODE NUMBER(19,0) NOT NULL ENABLE,
    ITEM_GROUP   NUMBER(19,0) NOT NULL ENABLE,
    CONSTRAINT ITEM_GROUPS_ALARM_CODE_FK FOREIGN KEY (ALARM_CODE) REFERENCES ALARM_CODE (ID) ENABLE,
    CONSTRAINT ITEM_GROUPS_ALARM_CODE_GRP_FK FOREIGN KEY (ITEM_GROUP) REFERENCES ITEM_GROUP (ID) ENABLE
)
/
CREATE SEQUENCE  ALARM_CODE_SEQ
MINVALUE 1000
MAXVALUE 99999999999999999999999 
INCREMENT BY 20 
START WITH 1000
CACHE 20 
NOORDER  
NOCYCLE
/
COMMIT
/

CREATE TABLE CLAIM_ALARM_CODES
  (
    CLAIM         NUMBER(19,0) NOT NULL ENABLE,
    ALARM_CODES NUMBER(19,0) NOT NULL ENABLE,
    CONSTRAINT "ALARM_CODES_CLAIM_FK" FOREIGN KEY (CLAIM) REFERENCES CLAIM (ID) ENABLE NOVALIDATE,
    CONSTRAINT "ALARM_CODES_ALARM_CODES_FK" FOREIGN KEY (ALARM_CODES) REFERENCES ALARM_CODE (ID) ENABLE NOVALIDATE
  )
/
COMMIT
/