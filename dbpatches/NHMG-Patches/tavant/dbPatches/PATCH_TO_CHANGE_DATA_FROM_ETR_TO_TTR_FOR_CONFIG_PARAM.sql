--PURPOSE    : PATCH TO ALTER CONFIG_PARAM TABLE FOR ETR TO TTR
--AUTHOR     : Jyoti
--CREATED ON : 21-Jan-13

update config_param set DISPLAY_NAME='Customer Types Displayed in TTR', description='List the customer types displayed in the drop down menu on TTR page.' where DISPLAY_NAME='Customer Types Displayed in ETR'
/
update config_param set DISPLAY_NAME='Is Manual Approval Flow required for TTR' , description='Is Manual Approval Flow required for TTR' where DISPLAY_NAME='Is Manual Approval Flow required for ETR'
/
update config_param set DISPLAY_NAME='Capture Installing Dealer/Installation Date on DR/TTR' , description='Capture Installing Dealer/Installation Date on DR/TTR' where DISPLAY_NAME='Capture Installing Dealer/Installation Date on DR/ETR'
/
COMMIT
/