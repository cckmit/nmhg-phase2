--PURPOSE: TO UPDATE TRAVEL_CONFIG VALUE TO CLAIM_AUDIT
--AUTHOR     : AJIT
--CREATED ON : 14-Jan-14

update claim_audit set travel_Config=1 where travel_Config is null
/
update claim_audit set transportation=0 where transportation is null
/