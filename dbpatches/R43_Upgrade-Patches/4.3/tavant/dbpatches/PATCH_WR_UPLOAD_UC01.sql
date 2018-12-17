--Purpose    : Adding upload mgt entries for warranty registration, changes made as a part of 4.3 upgrade
--Author     : Kuldeep
--Created On : 11-Oct-2010

Insert into UPLOAD_MGT values (UPLOAD_MGT_SEQ.NEXTVAL,'warrantyRegistrations','Warranty Registrations','Warranty Registrations','./pages/secure/admin/upload/templates/Template-WarrantyRegistrationsUpload.xls','STG_WARRANTY_REGISTRATIONS',null,'UPLOAD_WARRANTY_REG_VALIDATION','UPLOAD_WARRANTY_REG_UPLOAD',30,null,6,1,null)
/
Insert into upload_roles values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from role where name = 'dealer'))
/
COMMIT
/