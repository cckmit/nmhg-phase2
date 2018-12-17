ALTER table stg_install_base Add(Dcap_Flag varchar2(10))
/
update upload_mgt
set columns_to_capture = columns_to_capture + 1
where name_of_template = 'installBaseUpload'
/
COMMIT
/