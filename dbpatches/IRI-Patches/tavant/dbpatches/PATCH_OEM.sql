--Purpose    : Patch for OEM
--Author     : saya.sudha	
--Created On : 24-nov-2008
ALTER TABLE inventory_item DROP CONSTRAINT invitem_currentowner_fk
/
ALTER TABLE inventory_item ADD CONSTRAINT invitem_currentowner_fk  FOREIGN KEY (current_owner) REFERENCES ORGANIZATION
/
INSERT INTO config_param_option VALUES (config_param_option_seq.NEXTVAL, 'OriginalEquipManufacturer','OEM')
/
INSERT INTO config_param (ID,description,display_name,NAME,TYPE, d_created_on, d_internal_comments, d_updated_on,d_last_updated_by, d_created_time, d_updated_time)
VALUES (config_param_seq.NEXTVAL,'External Users can search Inventory that belongs to the selected Customer Types using Quick Search Functionality','Customer Types Supported in Quick Search for External Users',
'wntyConfigCustomerTypesAllowedinSearchResult','java.lang.String', SYSDATE, 'Configuration', SYSDATE,NULL, NULL, NULL)
/
INSERT INTO config_value (ID, active, VALUE,config_param,d_created_on, d_internal_comments, d_updated_on,d_last_updated_by, business_unit_info, d_created_time,d_updated_time, config_param_option)
VALUES (config_value_seq.NEXTVAL, 1, '1',(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),SYSDATE, NULL, SYSDATE,NULL, 'Club Car',NULL,NULL,
(SELECT ID FROM config_param_option WHERE display_value = 'OriginalEquipManufacturer'))
/
INSERT INTO config_value (ID, active, VALUE,config_param,d_created_on, d_internal_comments, d_updated_on,d_last_updated_by, business_unit_info, d_created_time,d_updated_time, config_param_option)
VALUES (config_value_seq.NEXTVAL, 1, '1',(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),SYSDATE, NULL, SYSDATE,NULL, 'Club Car',NULL,NULL,
(SELECT ID FROM config_param_option WHERE display_value = 'DirectCustomer'))
/
INSERT INTO config_value (ID, active, VALUE,config_param,d_created_on, d_internal_comments, d_updated_on,d_last_updated_by, business_unit_info, d_created_time,d_updated_time, config_param_option)
VALUES (config_value_seq.NEXTVAL, 1, '1',(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),SYSDATE, NULL, SYSDATE,NULL, 'Club Car',NULL,NULL,
(SELECT ID FROM config_param_option WHERE display_value = 'InterCompany'))
/
INSERT INTO config_value (ID, active, VALUE,config_param,d_created_on, d_internal_comments, d_updated_on,d_last_updated_by, business_unit_info, d_created_time,d_updated_time, config_param_option)
VALUES (config_value_seq.NEXTVAL, 1, '1',(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),SYSDATE, NULL, SYSDATE,NULL, 'Club Car',NULL,NULL,
(SELECT ID FROM config_param_option WHERE display_value = 'NationalAccount'))
/
INSERT INTO config_value (ID, active, VALUE,config_param,d_created_on, d_internal_comments, d_updated_on,d_last_updated_by, business_unit_info, d_created_time,d_updated_time, config_param_option)
VALUES (config_value_seq.NEXTVAL, 1, '1',(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),SYSDATE, NULL, SYSDATE,NULL, 'Club Car',NULL,NULL,
(SELECT ID FROM config_param_option WHERE display_value = 'Dealer'))
/
INSERT INTO config_param_options_mapping VALUES ((SELECT MAX (ID) FROM config_param_options_mapping) + 20,
(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),
(SELECT ID FROM config_param_option WHERE display_value = 'DirectCustomer'))
/
INSERT INTO config_param_options_mapping VALUES ((SELECT MAX (ID) FROM config_param_options_mapping) + 20,
(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),
(SELECT ID FROM config_param_option WHERE display_value = 'InterCompany'))
/
INSERT INTO config_param_options_mapping VALUES ((SELECT MAX (ID) FROM config_param_options_mapping) + 20,
(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),
(SELECT ID FROM config_param_option WHERE display_value = 'NationalAccount'))
/
INSERT INTO config_param_options_mapping VALUES ((SELECT MAX (ID) FROM config_param_options_mapping) + 20,
(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),
(SELECT ID FROM config_param_option WHERE display_value = 'Dealer'))
/
INSERT INTO config_param_options_mapping VALUES ((SELECT MAX (ID) FROM config_param_options_mapping) + 20,
(SELECT ID FROM config_param WHERE display_name = 'Customer Types Supported in Quick Search for External Users'),
(SELECT ID FROM config_param_option WHERE display_value = 'OriginalEquipManufacturer'))
/


