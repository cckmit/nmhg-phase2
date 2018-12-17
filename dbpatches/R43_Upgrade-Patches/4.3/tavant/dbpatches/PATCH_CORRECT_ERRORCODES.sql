
--update i18nupload_error_text 
--set description='Invalid Decision Reason' where id 
--=(selecT id from i18nupload_error_text where upload_error=(select 
--id from upload_error where code='RC010'))
--/
--update i18nupload_error_text 
--set description='Decision Reason is Mandatory' where id 
--=(selecT id from i18nupload_error_text where upload_error=(select 
--id from upload_error where code='RC003'))
--/
--update upload_error set upload_field ='DECISION REASON' where 
--id = (select upload_error from i18nupload_error_text where description like 'Invalid dispute reason entered while marking claim for dispute')
--/
commit
/