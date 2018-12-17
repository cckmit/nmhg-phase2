--Purpose    : Internationalization of upload error messages
--Author     : raghuram.d
--Created On : 20-May-09


CREATE TABLE UPLOAD_ERROR
(
    ID                      NUMBER(19)              NOT NULL,
    CODE                    VARCHAR2(255 CHAR)      NOT NULL,
    UPLOAD_FIELD            VARCHAR2(255 CHAR)      NOT NULL
)
/
ALTER TABLE UPLOAD_ERROR ADD CONSTRAINT UPLOAD_ERROR_PK	PRIMARY KEY( 	ID	)
/
CREATE SEQUENCE UPLOAD_ERROR_SEQ
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER
/
CREATE TABLE I18NUPLOAD_ERROR_TEXT
(
    ID                  NUMBER(19)               NOT NULL,
    LOCALE              VARCHAR2(255 CHAR),
    DESCRIPTION         VARCHAR2(255 CHAR),
    UPLOAD_ERROR        NUMBER(19)               NOT NULL  
)
/
ALTER TABLE I18NUPLOAD_ERROR_TEXT ADD CONSTRAINT I18NUPLOAD_ERROR_TEXT_PK	PRIMARY KEY( 	ID	)
/
ALTER TABLE I18NUPLOAD_ERROR_TEXT ADD (
    CONSTRAINT I18NUPLOAD_ERROR_TEXT_FK 
    FOREIGN KEY (UPLOAD_ERROR) 
    REFERENCES UPLOAD_ERROR(ID)
)
/
CREATE SEQUENCE I18N_Upload_Error_SEQ
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER
/
CREATE TABLE UPLOAD_MGT_UPLOAD_ERRORS
(
    UPLOAD_MGT              NUMBER(19)               NOT NULL,
    UPLOAD_ERRORS           NUMBER(19)               NOT NULL  
)
/
ALTER TABLE UPLOAD_MGT_UPLOAD_ERRORS ADD (
    CONSTRAINT UPLOAD_ERRORS_ERROR_FK 
    FOREIGN KEY (UPLOAD_ERRORS) 
    REFERENCES UPLOAD_ERROR(ID)
)
/
ALTER TABLE UPLOAD_MGT_UPLOAD_ERRORS ADD (
    CONSTRAINT UPLOAD_ERRORS_MGT_FK 
    FOREIGN KEY (UPLOAD_MGT) 
    REFERENCES UPLOAD_MGT(ID)
)
/
COMMIT
/
update upload_mgt set columns_to_capture=36 where name_of_template='draftWarrantyClaims'
/
alter table stg_draft_claim rename column root_cause to failure_detail
/
alter table stg_draft_claim rename column fault_code to fault_location
/
alter table stg_draft_claim rename column general_comments to claim_notes
/
CREATE OR REPLACE PROCEDURE create_upload_error(
    p_template_name IN VARCHAR2,
    p_locale IN VARCHAR2,
    p_field_name IN VARCHAR2,
    p_err_code IN VARCHAR2,
    p_err_desc IN VARCHAR2) 
AS
    v_upload_mgt_id         NUMBER := NULL;
    v_upload_err_id         NUMBER := NULL;
    v_i18nupload_err_id     NUMBER := NULL;
BEGIN
    SELECT id INTO v_upload_mgt_id FROM upload_mgt WHERE name_of_template = p_template_name;
    BEGIN
        SELECT e.id INTO v_upload_err_id
        FROM upload_error e, upload_mgt_upload_errors me
        WHERE upper(e.code)=upper(p_err_code) AND upper(e.upload_field)=upper(p_field_name)
            AND e.id=me.upload_errors AND me.upload_mgt=v_upload_mgt_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT upload_error_seq.nextval INTO v_upload_err_id FROM DUAL;
            INSERT INTO upload_error (id, code, upload_field)
            VALUES (v_upload_err_id, p_err_code, p_field_name);        
            INSERT INTO upload_mgt_upload_errors (upload_mgt, upload_errors)
            VALUES (v_upload_mgt_id, v_upload_err_id);
    END;
    BEGIN
        SELECT id INTO v_i18nupload_err_id
        FROM i18nupload_error_text
        WHERE upload_error=v_upload_err_id
            AND locale=p_locale;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT i18n_upload_error_seq.nextval INTO v_i18nupload_err_id FROM DUAL;
            INSERT INTO i18nupload_error_text (id, locale, description, upload_error)
            VALUES(v_i18nupload_err_id, p_locale, p_err_desc, v_upload_err_id);
    END;
    COMMIT;
END create_upload_error;
/
commit
/
BEGIN
    create_upload_error('draftWarrantyClaims','en_US','BUSINESS UNIT NAME','DC001','Invalid Business Unit Name');
    create_upload_error('draftWarrantyClaims','en_US','UNIQUE IDENTIFIER','DC002','Empty Unique Identifier');
    create_upload_error('draftWarrantyClaims','en_US','CLAIM TYPE','DC003','Invalid Claim Type');
    create_upload_error('draftWarrantyClaims','en_US','SERIAL NUMBER','DC004','Empty Serial Number');
    create_upload_error('draftWarrantyClaims','en_US','SERIAL NUMBER','DC005','Invalid Serial Number');
    create_upload_error('draftWarrantyClaims','en_US','MODEL NUMBER','DC006','Specify Model Number for duplicate Serial Number');
    create_upload_error('draftWarrantyClaims','en_US','MODEL NUMBER','DC007','Invalid Model Number');
    create_upload_error('draftWarrantyClaims','en_US','MODEL NUMBER','DC008','Invalid Model for Serial Number');
    create_upload_error('draftWarrantyClaims','en_US','MODEL NUMBER','DC009','Empty Model Number');
    create_upload_error('draftWarrantyClaims','en_US','MODEL NUMBER','DC010','Invalid Model Number');
    create_upload_error('draftWarrantyClaims','en_US','ITEM NUMBER','DC011','Empty Item Number');
    create_upload_error('draftWarrantyClaims','en_US','ITEM NUMBER','DC012','Invalid Item Number');
    create_upload_error('draftWarrantyClaims','en_US','MODEL NUMBER','DC013','Invalid Model for Item Number');
    create_upload_error('draftWarrantyClaims','en_US','PART ITEM NUMBER','DC014','Empty Part Item');
    create_upload_error('draftWarrantyClaims','en_US','PART ITEM NUMBER','DC015','Invalid Part Item');
    create_upload_error('draftWarrantyClaims','en_US','HOURS IN SERVICE','DC016','Empty Hours In Service');
    create_upload_error('draftWarrantyClaims','en_US','HOURS IN SERVICE','DC017','Inavlid Hours In Service');
    create_upload_error('draftWarrantyClaims','en_US','REPAIR DATE','DC018','Invalid Repair Date');
    create_upload_error('draftWarrantyClaims','en_US','FAILURE DATE','DC019','Invalid Failure Date');
    create_upload_error('draftWarrantyClaims','en_US','INSTALLATION DATE','DC020','Invalid Installation Date');
    create_upload_error('draftWarrantyClaims','en_US','WORK ORDER NUMBER','DC021','Empty Work Order Number');
    create_upload_error('draftWarrantyClaims','en_US','CONDITIONS FOUND','DC022','Empty Conditions Found');
    create_upload_error('draftWarrantyClaims','en_US','WORK PERFORMED','DC023','Empty Work Performed');
    create_upload_error('draftWarrantyClaims','en_US','PROBABLE CAUSE','DC024','Empty Probable Cause');
    create_upload_error('draftWarrantyClaims','en_US','CAUSAL PART','DC025','Invalid Causal Part');
    create_upload_error('draftWarrantyClaims','en_US','CAMPAIGN CODE','DC026','Empty Campaign Code');
    create_upload_error('draftWarrantyClaims','en_US','REPLACED IR PARTS QUANTITY','DC027','Empty Replaced IR Parts Quantity');
    create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS QUANTITY','DC028','Empty Replaced Non IR Parts quantity');
    create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS PRICE','DC029','Empty Replaced Non IR Parts Price');
    create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS DESC','DC030','Empty Replaced Non IR parts description');
    create_upload_error('draftWarrantyClaims','en_US','SMR CLAIM','DC031','Invalid SMR Claim');
    create_upload_error('draftWarrantyClaims','en_US','REASON FOR SMR CLAIM','DC032','Empty Reason for SMR Claim');
    create_upload_error('draftWarrantyClaims','en_US','REASON FOR SMR CLAIM','DC033','Invalid Reason for SMR Claim');
    create_upload_error('draftWarrantyClaims','en_US','INVOICE NUMBER','DC034','Empty Invoice number');
    create_upload_error('draftWarrantyClaims','en_US','HOURS ON PARTS','DC035','Invalid Hours on parts');
    create_upload_error('draftWarrantyClaims','en_US','REASON FOR EXTRA LABOR HOURS','DC036','Empty Reason for Extra Labor Hours');
    create_upload_error('draftWarrantyClaims','en_US','REPAIR DATE','DC037','RepairDate is less than FailureDate');
    create_upload_error('draftWarrantyClaims','en_US','FAULT LOCATION','DC038','Invalid Fault Location');
    create_upload_error('draftWarrantyClaims','en_US','JOB CODE','DC039','Invalid Job Code');
    create_upload_error('draftWarrantyClaims','en_US','FAULT FOUND','DC040','Invalid Fault Found');
    create_upload_error('draftWarrantyClaims','en_US','CAUSED BY','DC041','Invalid Caused By');
    create_upload_error('draftWarrantyClaims','en_US','FAILURE DETAIL','DC042','Invalid Failure Detail');
    create_upload_error('draftWarrantyClaims','en_US','TECHNICIAN ID','DC043','Invalid Technician');
    create_upload_error('draftWarrantyClaims','en_US','BUSINESS UNIT NAME','DC044','Dealer does not belong to BU');
END;
/
COMMIT
/