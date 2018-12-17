update upload_mgt
set columns_to_capture = columns_to_capture + 1
where name_of_template = 'installBaseUpload'
/
Alter Table Stg_Install_Base Add (Dcap_Model_Code Varchar2(50))
/
COMMIT
/