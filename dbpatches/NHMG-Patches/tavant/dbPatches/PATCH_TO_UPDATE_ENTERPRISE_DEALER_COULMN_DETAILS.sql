--PURPOSE    : PATCH TO UPDATE DEALERSHIP ENTERPRISE_DEALER
--AUTHOR     : KALYANI
--CREATED ON : 22-MAY-13
update dealership set enterprise_dealer=0  where enterprise_dealer is null
/
commit
/