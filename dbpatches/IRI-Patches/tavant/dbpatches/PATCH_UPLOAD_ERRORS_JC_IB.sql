--Purpose    : Upload errors for Jobcode and Install base uploads
--Author     : raghuram.d
--Created On : 08-Jun-09

CREATE OR REPLACE PROCEDURE create_upload_error(
    p_template_name IN VARCHAR2,
    p_locale IN VARCHAR2,
    p_field_name IN VARCHAR2,
    p_err_code IN VARCHAR2,
    p_err_desc IN VARCHAR2) 
AS
    v_upload_mgt_id         NUMBER := NULL;
    v_upload_err_id         NUMBER := NULL;
    v_i18nupload_err_id     NUMBER := NULL;
BEGIN
    SELECT id INTO v_upload_mgt_id FROM upload_mgt WHERE name_of_template = p_template_name;
    BEGIN
        SELECT e.id INTO v_upload_err_id
        FROM upload_error e, upload_mgt_upload_errors me
        WHERE upper(e.code)=upper(p_err_code) AND upper(e.upload_field)=upper(p_field_name)
            AND e.id=me.upload_errors AND me.upload_mgt=v_upload_mgt_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT upload_error_seq.nextval INTO v_upload_err_id FROM DUAL;
            INSERT INTO upload_error (id, code, upload_field)
            VALUES (v_upload_err_id, p_err_code, p_field_name);        
            INSERT INTO upload_mgt_upload_errors (upload_mgt, upload_errors)
            VALUES (v_upload_mgt_id, v_upload_err_id);
    END;
    BEGIN
        SELECT id INTO v_i18nupload_err_id
        FROM i18nupload_error_text
        WHERE upload_error=v_upload_err_id
            AND locale=p_locale;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT i18n_upload_error_seq.nextval INTO v_i18nupload_err_id FROM DUAL;
            INSERT INTO i18nupload_error_text (id, locale, description, upload_error)
            VALUES(v_i18nupload_err_id, p_locale, p_err_desc, v_upload_err_id);
    END;
    COMMIT;
END create_upload_error;
/
commit
/
update upload_mgt set columns_to_capture=39 where name_of_template='draftWarrantyClaims'
/
update upload_mgt set columns_to_capture=14 where name_of_template='uploadJobCodes'
/
update upload_mgt set columns_to_capture=7 where name_of_template='copyJobCodeFaultFound'
/
BEGIN
    create_upload_error('uploadJobCodes','en_US','BUSINESS UNIT NAME','JC001','Business Unit Name is not specified');
    create_upload_error('uploadJobCodes','en_US','BUSINESS UNIT NAME','JC002','Business Unit Name is not valid');
    create_upload_error('uploadJobCodes','en_US','PRODUCT CODE','JC003','Product Code is not specified');
    create_upload_error('uploadJobCodes','en_US','PRODUCT CODE','JC004','Product Code is not valid');
    create_upload_error('uploadJobCodes','en_US','FIELD MODEL','JC005','Field Model is not specified');
    create_upload_error('uploadJobCodes','en_US','FIELD MODEL','JC006','Field Model is not valid for the product');
    create_upload_error('uploadJobCodes','en_US','JOB CODE','JC007','Job Code is not specified');
    create_upload_error('uploadJobCodes','en_US','JOB CODE','JC008','Job Code already exists');
    create_upload_error('uploadJobCodes','en_US','ACTION','JC009','Action is not specified');
    create_upload_error('uploadJobCodes','en_US','LABOR STANDARD HOURS','JC010','Standard Labor time should be specified');
    create_upload_error('uploadJobCodes','en_US','LABOR STANDARD HOURS','JC011','Labor Standard Hours is not in range 0 to 99');
    create_upload_error('uploadJobCodes','en_US','FIELD MODIFICATION ONLY','JC012','Field Modification Only is not specified');
    create_upload_error('uploadJobCodes','en_US','JOB CODE','JC013','Invalid format for Job Code');
    create_upload_error('uploadJobCodes','en_US','JOB CODE','JC014','Invalid Product Sub Component in Job Code');
    create_upload_error('uploadJobCodes','en_US','JOB CODE','JC015','Invalid Product Component in Job Code');
    create_upload_error('uploadJobCodes','en_US','JOB CODE','JC016','Invalid Product Sub System in Job Code');
    create_upload_error('uploadJobCodes','en_US','JOB CODE','JC017','Invalid Product System in Job Code');
    create_upload_error('uploadJobCodes','en_US','ACTION','JC018','Action is not valid');
    create_upload_error('uploadJobCodes','en_US','FIELD MODIFICATION ONLY','JC019','Field Modification Only is not valid');
    create_upload_error('copyJobCodeFaultFound','en_US','BUSINESS UNIT NAME','CJ001','Business Unit Name is not specified');
    create_upload_error('copyJobCodeFaultFound','en_US','BUSINESS UNIT NAME','CJ002','Business Unit Name is not valid');
    create_upload_error('copyJobCodeFaultFound','en_US','FROM PRODUCT CODE','CJ003','From Product Code is not specified');
    create_upload_error('copyJobCodeFaultFound','en_US','FROM PRODUCT CODE','CJ004','From Product Code is not valid');
    create_upload_error('copyJobCodeFaultFound','en_US','FROM MODEL NUMBER','CJ005','From Model Number is not specified');
    create_upload_error('copyJobCodeFaultFound','en_US','FROM MODEL NUMBER','CJ006','From Model Number is not valid');
    create_upload_error('copyJobCodeFaultFound','en_US','TO PRODUCT CODE','CJ007','To Product Code is not specified');
    create_upload_error('copyJobCodeFaultFound','en_US','TO  PRODUCT CODE','CJ008','To Product Code is not valid');
    create_upload_error('copyJobCodeFaultFound','en_US','TO MODEL NUMBER','CJ009','TO Model Number is not specified');
    create_upload_error('copyJobCodeFaultFound','en_US','TO MODEL NUMBER','CJ010','To Model Number is not valid');
    create_upload_error('copyJobCodeFaultFound','en_US','COPY','CJ011','Copy is not specified');
    create_upload_error('copyJobCodeFaultFound','en_US','COPY','CJ012','Copy is not valid');
    create_upload_error('installBaseUpload','en_US','BUSINESS UNIT NAME','IB001','Business Unit Name is not specified');
    create_upload_error('installBaseUpload','en_US','BUSINESS UNIT NAME','IB002','Business Unit Name is not valid');
    create_upload_error('installBaseUpload','en_US','SERIAL NUMBER','IB003','Serial Number is not specified');
    create_upload_error('installBaseUpload','en_US','SERIAL NUMBER','IB004','Serial Number exists');
    create_upload_error('installBaseUpload','en_US','ITEM NUMBER','IB005','Item Number is not specified');
    create_upload_error('installBaseUpload','en_US','ITEM NUMBER','IB006','Item Number is not valid');
    create_upload_error('installBaseUpload','en_US','LIFECYCLE STATUS','IB007','Lifecycle Status is not valid');
    create_upload_error('installBaseUpload','en_US','STOCK OR RETAIL','IB008','Stock Or Retail is not specified');
    create_upload_error('installBaseUpload','en_US','STOCK OR RETAIL','IB009','Stock Or Retail is not valid');
    create_upload_error('installBaseUpload','en_US','ACR DATE','IB010','ACR Date is not valid');
    create_upload_error('installBaseUpload','en_US','MACHINE BUILD DATE','IB011','Machine Build Date is not valid');
    create_upload_error('installBaseUpload','en_US','HOURS IN SERVICE','IB012','Hours In Service is not valid');
    create_upload_error('installBaseUpload','en_US','SHIPMENT DATE','IB013','Shipment Date is not specified');
    create_upload_error('installBaseUpload','en_US','SHIPMENT DATE','IB014','Shipment Date is not valid');
    create_upload_error('installBaseUpload','en_US','DELIVERY DATE','IB015','Delivery Date is not specified');
    create_upload_error('installBaseUpload','en_US','DELIVERY DATE','IB016','Delivery Date is not valid');
    create_upload_error('installBaseUpload','en_US','INSTALLATION DATE','IB017','Installation Date is not valid');
    create_upload_error('installBaseUpload','en_US','DELIVERY DATE','IB018','Delivery Date is before Shipment Date');
    create_upload_error('installBaseUpload','en_US','INSTALLATION DATE','IB019','Installation Date is before Delivery Date');
    create_upload_error('installBaseUpload','en_US','INSTALLATION DATE','IB020','Installation Date is before Shipment Date');
    create_upload_error('installBaseUpload','en_US','WARRANTY START DATE','IB021','Warranty Start Date is not valid');
    create_upload_error('installBaseUpload','en_US','WARRANTY START DATE','IB022','Warranty Start Date is before Delivery Date');
    create_upload_error('installBaseUpload','en_US','INVOICE NUMBER','IB023','Invoice Number is not specified');
    create_upload_error('installBaseUpload','en_US','INVOICE DATE','IB024','Invoice Date is not valid');
    create_upload_error('installBaseUpload','en_US','DEALER NUMBER','IB025','Dealer Number is not specified');
    create_upload_error('installBaseUpload','en_US','DEALER NUMBER','IB026','Dealer Number is not valid');
    create_upload_error('installBaseUpload','en_US','END CUSTOMER NAME','IB027','End Customer Name is not specified');
    create_upload_error('installBaseUpload','en_US','E MAIL','IB028','E Mail is not specified');
    create_upload_error('installBaseUpload','en_US','ADDRESS LINE1','IB029','Address Line1 is not specified');
    create_upload_error('installBaseUpload','en_US','COUNTRY','IB030','Country is not specified');
    create_upload_error('installBaseUpload','en_US','COUNTRY','IB031','Country is not valid');
    create_upload_error('installBaseUpload','en_US','STATE','IB032','State is not specified');
    create_upload_error('installBaseUpload','en_US','STATE','IB033','State is not valid');
    create_upload_error('installBaseUpload','en_US','CITY','IB034','City is not specified');
    create_upload_error('installBaseUpload','en_US','CITY','IB035','City is not valid');
    create_upload_error('installBaseUpload','en_US','ZIPCODE','IB036','Zipcode is not specified');
    create_upload_error('installBaseUpload','en_US','ZIPCODE','IB037','Zipcode is not valid');
    create_upload_error('installBaseUpload','en_US','PREFERED LANGUAGE','IB038','Prefered Language is not specified');
    create_upload_error('installBaseUpload','en_US','PREFERED LANGUAGE','IB039','Prefered Language is not valid');
    create_upload_error('installBaseUpload','en_US','APPLY COVERAGE','IB040','Apply Coverage is not valid');
    create_upload_error('installBaseUpload','en_US','INVENTORY ITEM TYPE','IB041','Inventory Item Type is not valid');
    create_upload_error('installBaseUpload','en_US','WMS DEPLOYMENT STACK','IB042','WMS Deployment Stack is not valid');
    create_upload_error('installBaseUpload','en_US','SHIP FROM WAREHOUSE','IB043','Ship From Warehouse is not specified');
    create_upload_error('installBaseUpload','en_US','SHIP FROM WAREHOUSE','IB044','Ship From Warehouse is not valid');
END;
/
commit
/