--Purpose    : Removed address line 2 & 3,contact middle and last names of end customer from install base upload template
--Author     : raghuram.d
--Created On : 15-Jun-09

update upload_mgt set columns_to_capture=35 where name_of_template='installBaseUpload'
/
alter table stg_install_base rename column address_line1 to address
/
alter table stg_install_base rename column contact_first_name to contact_person_name
/
BEGIN
    create_upload_error('installBaseUpload','en_US','DEALER SITE NUMBER','IB045','Dealer Site Number is not specified');
    create_upload_error('installBaseUpload','en_US','DEALER SITE NUMBER','IB046','Dealer Site Number is not valid');
END;
/
commit
/
