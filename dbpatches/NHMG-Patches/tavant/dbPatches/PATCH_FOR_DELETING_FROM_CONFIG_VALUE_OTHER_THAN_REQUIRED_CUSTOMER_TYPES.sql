-- Patch to delete config values other than config_param_options End Customer, Direct Customer, National Account, Government Account, Demo, Dealer Rental
-- Author: PARTHASARATHY R
-- Created On : 19-NOV-2012

DELETE FROM CONFIG_VALUE WHERE CONFIG_PARAM = (SELECT ID FROM CONFIG_PARAM WHERE NAME='customersFilingDR') and CONFIG_PARAM_OPTION NOT IN (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE IN ('EndCustomer', 'DirectCustomer', 'NationalAccount', 'GovernmentAccount', 'Demo', 'Dealer Rental'))
/
COMMIT
/