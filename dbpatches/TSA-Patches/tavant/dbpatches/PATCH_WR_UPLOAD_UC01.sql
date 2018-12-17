--Purpose    : Adding upload mgt entries for warranty registration
--Author     : Kuldeep
--Created On : 08/07/2010

Insert into UPLOAD_MGT values (UPLOAD_MGT_SEQ.NEXTVAL,'warrantyRegistrations','Warranty Registrations','Warranty Registrations','pages/secure/admin/upload/templates/Template-WarrantyRegistrationsUpload.xls','STG_WARRANTY_REGISTRATIONS',null,'UPLOAD_WARRANTY_REG_VALIDATION','UPLOAD_WARRANTY_REG_UPLOAD',29,null,6,1,null)
/
Insert into upload_roles values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from role where name = 'dealer'))
/
COMMIT
/