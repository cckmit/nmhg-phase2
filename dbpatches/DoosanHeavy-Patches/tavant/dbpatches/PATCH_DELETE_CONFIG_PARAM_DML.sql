--PURPOSE    : PATCH TO DELETE BARCODE AND PARTOFF FEATURE
--AUTHOR     : GHANASHYAM DAS
--CREATED ON : 11-APR-12

delete from CONFIG_PARAM_OPTIONS_MAPPING where param_id in (select id from CONFIG_PARAM where  name='enableBarCodeFeature')
/
delete from config_value where config_param in (select id from CONFIG_PARAM where  name='enableBarCodeFeature')
/
delete from CONFIG_PARAM where name='enableBarCodeFeature'
/
COMMIT
/
delete from CONFIG_PARAM_OPTIONS_MAPPING where param_id in (select id from CONFIG_PARAM where  name='enablePartOffFeature')
/
delete from config_value where config_param in (select id from CONFIG_PARAM where  name='enablePartOffFeature')
/
delete from config_param where name='enablePartOffFeature'
/
COMMIT
/