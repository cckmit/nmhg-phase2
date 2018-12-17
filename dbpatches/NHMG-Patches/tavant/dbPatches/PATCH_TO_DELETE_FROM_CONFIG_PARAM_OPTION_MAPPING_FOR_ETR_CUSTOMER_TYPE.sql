-- Patch to delete customer type mapping for customer types for ETR other than 'NationalAccount', 'EndCustomer', 'DirectCustomer' in config_param_options_mapping
-- Author: PARTHASARATHY R
-- Created On : 08-Mar-2013

DELETE FROM CONFIG_PARAM_OPTIONS_MAPPING WHERE PARAM_ID = (SELECT ID FROM CONFIG_PARAM WHERE NAME='customersFilingETR') AND OPTION_ID NOT IN (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE IN ('NationalAccount', 'EndCustomer', 'Demo', 'Dealer Rental', 'DirectCustomer'))
/
COMMIT
/