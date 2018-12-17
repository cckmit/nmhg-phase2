--Purpose    : Adding error codes for warranty registration
--Author     : Rahul
--Created On : 09/07/2010

Update upload_mgt set template_path = './pages/secure/admin/upload/templates/Template-WarrantyRegistrationsUpload.xls', columns_to_capture = 30
where name_of_template = 'warrantyRegistrations'
/
Update UPLOAD_ERROR set UPLOAD_FIELD = 'SERIAL NUMBER' where code = 'WR009'
/
Insert into UPLOAD_MGT_UPLOAD_ERRORS values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from UPLOAD_ERROR where code = 'WR009'))
/
Insert into I18NUPLOAD_ERROR_TEXT values(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Unit has pending warranty registeration', (select id from UPLOAD_ERROR where code = 'WR009'))
/
Insert into UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) values(UPLOAD_ERROR_SEQ.NEXTVAL,'WR049','OPERATOR NUMBER')
/
Insert into UPLOAD_MGT_UPLOAD_ERRORS values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from UPLOAD_ERROR where code = 'WR049'))
/
Insert into I18NUPLOAD_ERROR_TEXT values(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Operator does not belong to dealers address book', (select id from UPLOAD_ERROR where code = 'WR049'))
/
Insert into UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) values(UPLOAD_ERROR_SEQ.NEXTVAL,'WR050','OPERATOR TYPE')
/
Insert into UPLOAD_MGT_UPLOAD_ERRORS values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from UPLOAD_ERROR where code = 'WR050'))
/
Insert into I18NUPLOAD_ERROR_TEXT values(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Operator Type is null though Operator Number is present', (select id from UPLOAD_ERROR where code = 'WR050'))
/
Insert into UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) values(UPLOAD_ERROR_SEQ.NEXTVAL,'WR051','OPERATOR NUMBER')
/
Insert into UPLOAD_MGT_UPLOAD_ERRORS values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from UPLOAD_ERROR where code = 'WR051'))
/
Insert into I18NUPLOAD_ERROR_TEXT values(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Operator Number is null though Operator Type is present', (select id from UPLOAD_ERROR where code = 'WR051'))
/
Insert into UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) values(UPLOAD_ERROR_SEQ.NEXTVAL,'WR052','REQUEST FOR EXTENSION')
/
Insert into UPLOAD_MGT_UPLOAD_ERRORS values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from UPLOAD_ERROR where code = 'WR052'))
/
Insert into I18NUPLOAD_ERROR_TEXT values(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Request for Extenstion value is invalid', (select id from UPLOAD_ERROR where code = 'WR052'))
/
Insert into UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) values(UPLOAD_ERROR_SEQ.NEXTVAL,'WR053','OEM')
/
Insert into UPLOAD_MGT_UPLOAD_ERRORS values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from UPLOAD_ERROR where code = 'WR053'))
/
Insert into I18NUPLOAD_ERROR_TEXT values(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','OEM is invalid', (select id from UPLOAD_ERROR where code = 'WR053'))
/
Insert into UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) values(UPLOAD_ERROR_SEQ.NEXTVAL,'WR054','COMPONENT SERIAL NUMBER')
/
Insert into UPLOAD_MGT_UPLOAD_ERRORS values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'),(select id from UPLOAD_ERROR where code = 'WR054'))
/
Insert into I18NUPLOAD_ERROR_TEXT values(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','One or more components specified already exists on another unit', (select id from UPLOAD_ERROR where code = 'WR054'))
/
update i18nupload_error_text set description = 'Date of Installation is null'
where upload_error = (select id from upload_error where code = 'WR019')
/
update i18nupload_error_text set description = 'One or more component part number is invalid'
where upload_error = (select id from upload_error where code = 'WR042')
/
update i18nupload_error_text set description = 'One or more component installation date is invalid'
where upload_error = (select id from upload_error where code = 'WR043')
/
update i18nupload_error_text set description = 'Number of component serial number and component part number provided are not same'
where upload_error = (select id from upload_error where code = 'WR040')
/
update i18nupload_error_text set description = 'Number of component installation dates and component part number provided are not same'
where upload_error = (select id from upload_error where code = 'WR041')
/
update i18nupload_error_text set description = 'Date of installation is before delivery date'
where upload_error = (select id from upload_error where code = 'WR038')
/
update i18nupload_error_text set description = 'Customer does not exist in dealers address book'
where upload_error = (select id from upload_error where code = 'WR008')
/
update i18nupload_error_text set description = 'Address book for customer type specified does not exist for the dealer'
where upload_error = (select id from upload_error where code = 'WR007')
/
commit
/