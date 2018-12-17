--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload Changing the error messages
--Author     : Arpitha Nadig AR
--Created On : 15-MAY-2014 
UPDATE i18nupload_error_text SET description ='Invalid decision. Please choose either of [Accepted / Disputed / Part Return Requested ] ' WHERE upload_error IN (SELECT id FROM upload_error WHERE code='RC006')
/
UPDATE i18nupload_error_text SET description ='Claim Amount Being Accepted is mandatory for the decision Accepted' WHERE upload_error IN (SELECT id FROM upload_error WHERE code='RC016')
/
commit
/