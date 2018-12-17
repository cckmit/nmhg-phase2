--Purpose    : Added Miscellaneous parts to draft claim upload
--Author     : raghuram.d
--Created On : 01-Jun-09

update i18nupload_error_text set description='Invalid Hours In Service'
where upload_error=(select id from upload_error where code='DC017')
and locale='en_US'
/
delete from upload_roles where upload_mgt=(select id from upload_mgt where name_of_template='draftWarrantyClaims')
/
insert into upload_roles (upload_mgt,roles)
select (select id from upload_mgt where name_of_template='draftWarrantyClaims'),
    (select id from role where name='dealerWarrantyAdmin')
from dual
/
delete from upload_roles where upload_mgt=(select id from upload_mgt where name_of_template='customerUpload')
/
insert into upload_roles (upload_mgt,roles)
select (select id from upload_mgt where name_of_template='customerUpload'),
    (select id from role where name='admin')
from dual
/
UPDATE upload_mgt SET columns_to_capture = 38 WHERE name_of_template = 'draftWarrantyClaims'
/
ALTER TABLE stg_draft_claim ADD miscellaneous_parts VARCHAR2(4000)
/
ALTER TABLE stg_draft_claim ADD misc_parts_quantity VARCHAR2(4000)
/
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
BEGIN
    create_upload_error('draftWarrantyClaims','en_US','REPLACED_IR_PARTS','DC045','Invalid format for Replaced IR Parts');
    create_upload_error('draftWarrantyClaims','en_US','REPLACED_IR_PARTS','DC046','One or more Replaced IR Parts are invalid');
    create_upload_error('draftWarrantyClaims','en_US','REPLACED_IR_PARTS_QUANTITY','DC047','Invalid format for Replaced IR Parts Quantity');
    create_upload_error('draftWarrantyClaims','en_US','REPLACED_IR_PARTS_QUANTITY','DC048','Number of quantity values do not match the number of Replaced IR Parts');
    create_upload_error('draftWarrantyClaims','en_US','MISCELLANEOUS PARTS','DC049','Invalid format for Miscellaneous Parts');
    create_upload_error('draftWarrantyClaims','en_US','MISCELLANEOUS PARTS','DC050','One or more Miscellaneous Parts are invalid');
    create_upload_error('draftWarrantyClaims','en_US','MISC PARTS QUANTITY','DC051','Empty Misc Parts Quantity');
    create_upload_error('draftWarrantyClaims','en_US','MISC PARTS QUANTITY','DC052','Invalid format for Misc Parts Quantity');
    create_upload_error('draftWarrantyClaims','en_US','MISC PARTS QUANTITY','DC053','Number of quantity values do not match the number of Miscellaneous Parts');
END;
/
commit
/