--PURPOSE    : PATCH_TO_UPDATE_BU_CONFIG_FOR_CP_ADVISOR
--AUTHOR     : Priyanka S
--CREATED ON : 7-JAN-14


update config_value set config_param_option = (select id from config_param_option where value='false') where config_param = (SELECT id FROM config_param cp WHERE cp.name='enableCPAdvisor')
/
update config_param set sections = null where name ='enableCPAdvisor'
/
commit
/