--PURPOSE    : PATCH FOR CREATING WPRA Table
--AUTHOR     : Deepak Patel
--CREATED ON : 26-Dec-13

CREATE SEQUENCE BU_SETTINGS_SEQ MINVALUE 1000 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1000 NOCACHE NOORDER NOCYCLE
/
CREATE TABLE BU_SETTINGS
  (
    ID  NUMBER(19,0) NOT NULL ENABLE,
	KEY_NAME VARCHAR2(255 CHAR),
	KEY_VALUE VARCHAR2(255 CHAR),
	VERSION     NUMBER(10,0) NOT NULL ENABLE,
    D_CREATED_ON DATE,
    D_INTERNAL_COMMENTS VARCHAR2(255 CHAR),
    D_UPDATED_ON DATE,
    D_LAST_UPDATED_BY NUMBER(19,0),
    D_CREATED_TIME TIMESTAMP (6),
    D_UPDATED_TIME TIMESTAMP (6),
    D_ACTIVE NUMBER(1,0) DEFAULT 1,
	BUSINESS_UNIT_INFO VARCHAR2(255 CHAR),
    CONSTRAINT BU_SETTINGS_PK PRIMARY KEY (ID), 
    CONSTRAINT BU_SETTINGS_BU_FK FOREIGN KEY (BUSINESS_UNIT_INFO) REFERENCES BUSINESS_UNIT (NAME) ENABLE
  )
/
create index bu_settings_keyname_idx on BU_SETTINGS(KEY_NAME)
/