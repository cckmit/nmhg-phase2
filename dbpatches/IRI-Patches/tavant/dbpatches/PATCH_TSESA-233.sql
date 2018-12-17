--Purpose : for issue TSESA-233, We have created new table for storing format type
--Author  : surajdeo.prasad
--Date    : 10/06/2010

CREATE SEQUENCE  "UPLOAD_MGT_META_DATA_SEQ"  
MINVALUE 1 
MAXVALUE 999999999999999999999999999 
INCREMENT BY 20 
START WITH 1 CACHE 20 NOORDER  NOCYCLE 
/
CREATE TABLE "UPLOAD_MGT_META_DATA" 
   (	"ID" NUMBER(19,0) NOT NULL ENABLE, 
	"COLUMN_NAME" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "COLUMN_TYPE" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	"COLUMN_ORDER" NUMBER(9,0) NOT NULL ENABLE, 
  "UPLOAD_MGT" NUMBER(19,0) NOT NULL ENABLE,
	 CONSTRAINT "UPLOAD_MGT_META_DATA_PK" PRIMARY KEY ("ID"),
   CONSTRAINT "UPLOAD_MGT_META_DATA_FK" FOREIGN KEY ("UPLOAD_MGT")
	  REFERENCES "UPLOAD_MGT" ("ID") ENABLE
  )
/  
declare 
v_upload_mgt_id NUMBER := NULL;
BEGIN
    SELECT id INTO v_upload_mgt_id FROM upload_mgt WHERE name_of_template = 'costPriceUpload';
	INSERT INTO "UPLOAD_MGT_META_DATA" (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT) VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Claim Number', 'Text', '1', v_upload_mgt_id);
INSERT INTO "UPLOAD_MGT_META_DATA" (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT) VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Supplier Number', 'Text', '2', v_upload_mgt_id);
INSERT INTO "UPLOAD_MGT_META_DATA" (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT) VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Part Number', 'Text', '3', v_upload_mgt_id);
INSERT INTO "UPLOAD_MGT_META_DATA" (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT) VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Cost Price', 'Number', '4', v_upload_mgt_id);
INSERT INTO "UPLOAD_MGT_META_DATA" (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT) VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Currency', 'Text', '5', v_upload_mgt_id);
INSERT INTO "UPLOAD_MGT_META_DATA" (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT) VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Override', 'Text', '6', v_upload_mgt_id);
COMMIT;
END;
/
begin
  create_upload_error('costPriceUpload','en_US','CLAIM NUMBER','FORMAT_1','Data Format of Claim Number is not valid');
  create_upload_error('costPriceUpload','en_US','SUPPLIER NUMBER','FORMAT_2','Data Format of Supplier Number is not valid');
  create_upload_error('costPriceUpload','en_US','PART NUMBER','FORMAT_3','Data Format of Part Number is not valid');
  create_upload_error('costPriceUpload','en_US','COST PRICE','FORMAT_4','Data Format of Cost Price is not valid');
  create_upload_error('costPriceUpload','en_US','CURRENCY','FORMAT_5','Data Format of Currency is not valid');
  create_upload_error('costPriceUpload','en_US','OVERRIDE','FORMAT_6','Data Format of Override is not valid');
end;
/