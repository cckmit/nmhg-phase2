INSERT INTO CONFIG_VALUE VALUES(
CONFIG_VALUE_SEQ.NEXTVAL,1,null,(SELECT ID FROM CONFIG_PARAM WHERE NAME IN ('displayCPFlagOnClaimPgOne')),SYSDATE,'UPGRADE-FIX',
sysdate,null,current_timestamp,current_timestamp,1,'Thermo King TSA',(select id from config_param_option where value = 'true'))
/
INSERT INTO CONFIG_VALUE VALUES(
CONFIG_VALUE_SEQ.NEXTVAL,1,null,(SELECT ID FROM CONFIG_PARAM WHERE NAME IN ('showPartSerialNumber')),SYSDATE,'UPGRADE-FIX',
sysdate,null,current_timestamp,current_timestamp,1,'Thermo King TSA',(select id from config_param_option where value = 'true'))
/
INSERT INTO CONFIG_VALUE VALUES(
CONFIG_VALUE_SEQ.NEXTVAL,1,null,(SELECT ID FROM CONFIG_PARAM WHERE NAME IN ('useInstalledPartsForRecoveryClaim')),SYSDATE,'UPGRADE-FIX',
sysdate,null,current_timestamp,current_timestamp,1,'Thermo King TSA',(select id from config_param_option where value = 'false'))
/
COMMIT
/