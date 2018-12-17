-- Purpose    : Manufacturing Site & Factory order number added to install bsae upload
-- Author     : raghuram.d
-- Created On : 01-Aug-09

ALTER TABLE stg_install_base ADD factory_order_number VARCHAR2(255)
/
ALTER TABLE stg_install_base ADD manufacturing_site VARCHAR2(255)
/
BEGIN
    create_upload_error('installBaseUpload','en_US','FACTORY ORDER NUMBER','IB047','Factory Order Number is not specified');
    create_upload_error('installBaseUpload','en_US','MANUFACTURING SITE','IB048','Manufacturing Site is not specified');
    create_upload_error('installBaseUpload','en_US','MANUFACTURING SITE','IB049','Manufacturing Site is not valid');
END;
/
UPDATE upload_mgt SET columns_to_capture=39 WHERE name_of_template='installBaseUpload'
/