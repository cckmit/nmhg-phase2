-- Purpose    : Customer number is added to the IB upload template
-- Author     : raghuram.d
-- Created On : 04-Aug-09

ALTER TABLE stg_install_base ADD end_customer_number VARCHAR2(255)
/
UPDATE upload_mgt SET columns_to_capture=40 WHERE name_of_template='installBaseUpload'
/
BEGIN
    create_upload_error('installBaseUpload','en_US','END CUSTOMER NUMBER','IB050','End Customer Number is not specified');
END;
/
COMMIT
/