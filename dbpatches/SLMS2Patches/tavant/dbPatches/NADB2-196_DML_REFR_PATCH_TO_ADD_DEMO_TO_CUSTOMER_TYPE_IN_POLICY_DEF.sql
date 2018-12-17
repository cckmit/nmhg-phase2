-- PURPOSE    : PATCH_TO_ADD_DEMO_TO_CUSTOMER_TYPE_IN_POLICY_DEF
-- AUTHOR     : P RAGHAVENDRA RAJU.
-- CREATED ON : 19-AUGUST-2014

INSERT INTO config_param_options_mapping VALUES(CONFIG_PARAM_OPT_SEQ.nextval,(SELECT id FROM config_param WHERE name='customersForPolicy' and rownum=1),(SELECT id FROM config_param_option WHERE value = 'Demo' and rownum=1))
/
COMMIT
/