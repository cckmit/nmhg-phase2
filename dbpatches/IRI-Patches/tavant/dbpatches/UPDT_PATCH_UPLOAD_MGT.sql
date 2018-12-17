--Purpose    : Role permission to Dealer for Draft Claim upload
--Author     : Jhulfikar Ali. A
--Created On : 13-Feb-09

insert into upload_roles values (
(select id from upload_mgt where name_of_template='draftWarrantyClaims') , 
(select id from role where name='dealer'))
/
delete from upload_roles where upload_mgt in
(select id from upload_mgt where name_of_template='draftWarrantyClaims') and
roles in (select id from role where name='admin')
/
alter table upload_mgt add (consume_rows_from number, header_row_to_capture number)
/
update upload_mgt set consume_rows_from = 6, header_row_to_capture = 1
/
update upload_mgt set columns_to_capture = 38, consume_rows_from = 10, header_row_to_capture = 0 where name_of_template='draftWarrantyClaims'
/
alter table file_upload_mgt add uploaded_by NUMBER(19)
/
ALTER TABLE file_upload_mgt ADD (CONSTRAINT file_upload_mgt_user_FK FOREIGN KEY (uploaded_by) REFERENCES ORG_USER (ID))
/
drop table stg_draft_claim
/
create table stg_draft_claim (
ID									NUMBER,
FILE_UPLOAD_MGT_ID					NUMBER,
Business_Unit_Name					VARCHAR2(4000),
Unique_identifier					VARCHAR2(4000),
Claim_Type							VARCHAR2(4000),
Serial_Number						VARCHAR2(4000),
Model_Number						VARCHAR2(4000),
Item_Number							VARCHAR2(4000),
Part_Item_Number					VARCHAR2(4000),
Hours_In_Service					NUMBER,
Repair_Date							VARCHAR2(4000),
Failure_Date						VARCHAR2(4000),
Installation_Date					VARCHAR2(4000),
Work_Order_Number					VARCHAR2(4000),
Conditions_Found					VARCHAR2(4000),
Work_Performed						VARCHAR2(4000),
Probable_Cause						VARCHAR2(4000),
General_Comments					VARCHAR2(4000),
Causal_Part						VARCHAR2(4000),
Labour_Hours						NUMBER,
Campaign_Code						VARCHAR2(4000),
Replaced_IR_Parts					VARCHAR2(4000),
Replaced_IR_Parts_Quantity			VARCHAR2(4000),
Replaced_Non_IR_Parts				VARCHAR2(4000),
Replaced_Non_IR_Parts_quantity		VARCHAR2(4000),
Replaced_Non_IR_Parts_Price			VARCHAR2(4000),
Replaced_Non_IR_parts_desc	VARCHAR2(4000),
Fault_Code							VARCHAR2(4000),
Job_Code							VARCHAR2(4000),
Fault_Found							VARCHAR2(4000),
Caused_By							VARCHAR2(4000),
Root_Cause							VARCHAR2(4000),
Technician_Id						VARCHAR2(4000),
Servicing_Location_Id				VARCHAR2(4000),
SMR_Claim							VARCHAR2(4000),
Invoice_number						VARCHAR2(4000),
Hours_on_parts						NUMBER,
Reason_for_Extra_Labor_Hours		VARCHAR2(4000),
Reason_for_SMR_Claim				VARCHAR2(4000),
ERROR_STATUS						VARCHAR2(20),
ERROR_CODE							VARCHAR2(4000),
UPLOAD_STATUS						VARCHAR2(20),
UPLOAD_ERROR						VARCHAR2(4000),
UPLOAD_DATE							VARCHAR2(20)
)
/
COMMIT
/