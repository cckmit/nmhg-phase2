-- Patch to delete customer type mapping for customer types other than 'NationalAccount', 'EndCustomer', 'Demo', 'Dealer Rental', 'DirectCustomer' in config_param_options_mapping
-- Author: PARTHASARATHY R
-- Created On : 30-OCT-2012

DELETE FROM CONFIG_PARAM_OPTIONS_MAPPING WHERE PARAM_ID = (SELECT ID FROM CONFIG_PARAM WHERE NAME='customersFilingDR') AND OPTION_ID NOT IN (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE IN ('NationalAccount', 'EndCustomer', 'Demo', 'Dealer Rental', 'DirectCustomer'))
/
DELETE FROM CONFIG_PARAM_OPTIONS_MAPPING WHERE PARAM_ID = (SELECT ID FROM CONFIG_PARAM WHERE NAME='customersFilingETR') AND OPTION_ID NOT IN (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE IN ('NationalAccount', 'EndCustomer', 'Demo', 'Dealer Rental', 'DirectCustomer'))
/
DELETE FROM CONFIG_PARAM_OPTIONS_MAPPING WHERE PARAM_ID = (SELECT ID FROM CONFIG_PARAM WHERE NAME='customersForPolicy') AND OPTION_ID NOT IN (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE IN ('NationalAccount', 'EndCustomer', 'Demo', 'Dealer Rental', 'DirectCustomer'))
/
DELETE FROM CONFIG_PARAM_OPTIONS_MAPPING WHERE PARAM_ID = (SELECT ID FROM CONFIG_PARAM WHERE NAME='customersFilingClaim') AND OPTION_ID NOT IN (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE IN ('NationalAccount', 'EndCustomer', 'Demo', 'Dealer Rental', 'DirectCustomer'))
/
COMMIT
/