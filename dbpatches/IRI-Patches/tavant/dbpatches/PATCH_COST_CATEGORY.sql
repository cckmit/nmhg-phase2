--Purpose: Added cost categories Local Purchase, Tolls and Other Freight & Duty, Others
--Author: Raghu Ram
--Created On: Date 31 Mar 2009

INSERT INTO cost_category
            (ID, code, description, NAME, VERSION,
             d_created_on, d_created_time, d_internal_comments, d_updated_on,
             d_updated_time, d_last_updated_by, d_active
            )
     VALUES (cost_category_seq.nextval, 'LOCAL_PURCHASE', 'Local Purchase', 'Local Purchase', 0,
             NULL, NULL, NULL, NULL,
             NULL, NULL, 1
            )
/
INSERT INTO cost_category
            (ID, code, description, NAME, VERSION,
             d_created_on, d_created_time, d_internal_comments, d_updated_on,
             d_updated_time, d_last_updated_by, d_active
            )
     VALUES (cost_category_seq.nextval, 'OTHER_FREIGHT_DUTY', 'Other Freight & Duty', 'Other Freight & Duty', 0,
             NULL, NULL, NULL, NULL,
             NULL, NULL, 1
            )
/
INSERT INTO cost_category
            (ID, code, description, NAME, VERSION,
             d_created_on, d_created_time, d_internal_comments, d_updated_on,
             d_updated_time, d_last_updated_by, d_active
            )
     VALUES (cost_category_seq.nextval, 'OTHERS', 'Others', 'Others', 0,
             NULL, NULL, NULL, NULL,
             NULL, NULL, 1
            )
/
INSERT INTO cost_category
            (ID, code, description, NAME, VERSION,
             d_created_on, d_created_time, d_internal_comments, d_updated_on,
             d_updated_time, d_last_updated_by, d_active
            )
     VALUES (cost_category_seq.nextval, 'TOLLS', 'Tolls', 'Tolls', 0,
             NULL, NULL, NULL, NULL,
             NULL, NULL, 1
            )
/
INSERT INTO config_value(id, active, value, config_param, business_unit_info, config_param_option, d_active)
SELECT config_value_seq.nextval, 0, cat.id, (select id from config_param where name='configuredCostCategories'), 'Hussmann', NULL, 1
FROM cost_category cat WHERE code IN ('LOCAL_PURCHASE', 'OTHER_FREIGHT_DUTY', 'TOLLS', 'OTHERS')
/
INSERT INTO config_value(id, active, value, config_param, business_unit_info, config_param_option, d_active)
SELECT config_value_seq.nextval, 0, cat.id, (select id from config_param where name='configuredCostCategories'), 'AIR', NULL, 1
FROM cost_category cat WHERE code IN ('LOCAL_PURCHASE', 'OTHER_FREIGHT_DUTY', 'TOLLS', 'OTHERS')
/
INSERT INTO config_value(id, active, value, config_param, business_unit_info, config_param_option, d_active)
SELECT config_value_seq.nextval, 0, cat.id, (select id from config_param where name='configuredCostCategories'), 'Club Car', NULL, 1
FROM cost_category cat WHERE code IN ('LOCAL_PURCHASE', 'OTHER_FREIGHT_DUTY', 'TOLLS', 'OTHERS')
/
INSERT INTO config_value(id, active, value, config_param, business_unit_info, config_param_option, d_active)
SELECT config_value_seq.nextval, 0, cat.id, (select id from config_param where name='configuredCostCategories'), 'Clubcar ESA', NULL, 1
FROM cost_category cat WHERE code IN ('LOCAL_PURCHASE', 'OTHER_FREIGHT_DUTY', 'TOLLS', 'OTHERS')
/
INSERT INTO config_value(id, active, value, config_param, business_unit_info, config_param_option, d_active)
SELECT config_value_seq.nextval, 0, cat.id, (select id from config_param where name='configuredCostCategories'), 'TFM', NULL, 1
FROM cost_category cat WHERE code IN ('LOCAL_PURCHASE', 'OTHER_FREIGHT_DUTY', 'TOLLS', 'OTHERS')
/
INSERT INTO config_value(id, active, value, config_param, business_unit_info, config_param_option, d_active)
SELECT config_value_seq.nextval, 0, cat.id, (select id from config_param where name='configuredCostCategories'), 'Transport Solutions ESA', NULL, 1
FROM cost_category cat WHERE code IN ('LOCAL_PURCHASE', 'OTHER_FREIGHT_DUTY', 'TOLLS', 'OTHERS')
/
INSERT INTO section (id, display_position, name, version, d_active, message_key)
	VALUES(section_seq.nextval, 14, 'Local Purchase', 1, 1, 'label.section.localPurchase')
/
INSERT INTO section (id, display_position, name, version, d_active, message_key)
	VALUES(section_seq.nextval, 15, 'Tolls', 1, 1, 'label.section.tolls')
/
INSERT INTO section (id, display_position, name, version, d_active, message_key)
	VALUES(section_seq.nextval, 16, 'Other Freight And Duty', 1, 1, 'label.section.otherFreightAndDuty')
/
INSERT INTO section (id, display_position, name, version, d_active, message_key)
	VALUES(section_seq.nextval, 17, 'Others', 1, 1, 'label.section.others')
/
INSERT INTO i18nsection_text(id, locale, section_name, section_i18nname)
	SELECT i18n_section_text_seq.nextval, 'en_US', 'Local Purchase', s.id
	FROM section s WHERE name='Local Purchase'
/
INSERT INTO i18nsection_text(id, locale, section_name, section_i18nname)
	SELECT i18n_section_text_seq.nextval, 'en_US', 'Tolls', s.id
	FROM section s WHERE name='Tolls'
/
INSERT INTO i18nsection_text(id, locale, section_name, section_i18nname)
	SELECT i18n_section_text_seq.nextval, 'en_US', 'Other Freight And Duty', s.id
	FROM section s WHERE name='Other Freight And Duty'
/
INSERT INTO i18nsection_text(id, locale, section_name, section_i18nname)
	SELECT i18n_section_text_seq.nextval, 'en_US', 'Others', s.id
	FROM section s WHERE name='Others'
/
ALTER TABLE claim ADD local_purchase_config NUMBER(1,0)
/
ALTER TABLE claim ADD tolls_config NUMBER(1,0)
/
ALTER TABLE claim ADD other_freight_duty_config NUMBER(1,0)
/
ALTER TABLE claim ADD others_config NUMBER(1,0)
/
ALTER TABLE service ADD local_purchase_amt NUMBER(19,2)
/
ALTER TABLE service ADD local_purchase_curr VARCHAR2(255)
/
ALTER TABLE service ADD tolls_amt NUMBER(19,2)
/
ALTER TABLE service ADD tolls_curr VARCHAR2(255)
/
ALTER TABLE service ADD other_freight_duty_amt NUMBER(19,2)
/
ALTER TABLE service ADD other_freight_duty_curr VARCHAR2(255)
/
ALTER TABLE service ADD others_amt NUMBER(19,2)
/
ALTER TABLE service ADD others_curr VARCHAR2(255)
/
COMMIT
/