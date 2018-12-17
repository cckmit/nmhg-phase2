update config_value set config_param_option=(select id from CONFIG_PARAM_OPTION where value='true') 
where config_param=(select id from CONFIG_PARAM where NAME='enableReportFilingAnyDealer')
/
Commit
/