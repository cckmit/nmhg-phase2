CREATE TABLE CUST_REPORT_APP_PART
  ( ID   NUMBER(19,0) PRIMARY KEY,
    VERSION   NUMBER(19,0),
    ITEM_CRITERION_ITEM_GROUP NUMBER(19,0),
    ITEM_CRITERION_ITEM    NUMBER(19,0),
    APPLICABILITY       VARCHAR2(255 CHAR),
    ITEM_IDENTIFIER  VARCHAR2(255 CHAR),
    BY_QUANTITY NUMBER(1,0),
    D_CREATED_ON DATE,
    D_INTERNAL_COMMENTS VARCHAR2(255 BYTE),
    D_UPDATED_ON DATE,
    D_LAST_UPDATED_BY NUMBER(19,0),
    D_CREATED_TIME TIMESTAMP (6),
    D_UPDATED_TIME TIMESTAMP (6),
    D_ACTIVE NUMBER(1,0) DEFAULT 1
  )
/
CREATE TABLE custom_report_applicable_parts(
custom_report NUMBER(19,0),
applicable_parts NUMBER(19,0)
)
/
alter table CUST_REPORT_APP_PART
add constraint CUSTREPORT_ITEMCRITITEM_FK foreign key(ITEM_CRITERION_ITEM) references ITEM(ID)
/
alter table CUST_REPORT_APP_PART
add constraint CUSTREPORT_ITEMGRP_FK foreign key(ITEM_CRITERION_ITEM_GROUP) references ITEM_GROUP(ID)
/
ALTER TABLE REPORT_FORM_ANSWER_OPTION 
ADD  IS_DEFAULT NUMBER(1,0)
/
ALTER TABLE REPORT_FORM_ANSWER_OPTION 
ADD ATTACHMENT NUMBER(19,0)
/
ALTER TABLE REPORT_FORM_ANSWER_OPTION
 add constraint REP_FORMANS_OP_ATTACHMENT_FK foreign key(ATTACHMENT) references DOCUMENT(ID)
/
ALTER TABLE REPORT_FORM_QUESTION 
ADD   INCLUDE_OTHER_AS_AN_OPTION NUMBEr(1,0)
/
ALTER TABLE REPORT_FORM_QUESTION
ADD POST_INSTRUCTIONS NUMBER(19,0)
/
ALTER TABLE REPORT_FORM_QUESTION
ADD PRE_INSTRUCTIONS NUMBER(19,0)
/
CREATE TABLE CUSTOM_REPORT_INSTRUCTIONS
  (
    ID NUMBER(19,0) PRIMARY KEY, 
    INSTRUCTIONS VARCHAR2(255 CHAR),
    ATTACHMENT NUMBER(19,0) REFERENCES DOCUMENT(ID),
     D_CREATED_ON  DATE,
    D_CREATED_TIME TIMESTAMP (6),
    D_INTERNAL_COMMENTS VARCHAR2(255 CHAR),
    D_UPDATED_ON DATE,
    D_UPDATED_TIME TIMESTAMP (6),
    D_LAST_UPDATED_BY NUMBER(19,0)  REFERENCES ORG_USER(ID),
    D_ACTIVE   NUMBER(1,0)
    )
/
ALTER table REPORT_FORM_QUESTION
 add constraint REPFORMQUES_PREINST_FK foreign key(PRE_INSTRUCTIONS) references CUSTOM_REPORT_INSTRUCTIONS(ID)
/
ALTER table REPORT_FORM_QUESTION 
ADD constraint REPFORMQUES_POSTINST_FK foreign key(POST_INSTRUCTIONS) references CUSTOM_REPORT_INSTRUCTIONS(ID)
/
CREATE SEQUENCE CUST_REPORT_APP_PART_SEQ  MINVALUE 20 MAXVALUE 999999999999999999999999999  
 INCREMENT BY 20 START WITH 20   CACHE 20   NOORDER NOCYCLE 
/
COMMIT
/