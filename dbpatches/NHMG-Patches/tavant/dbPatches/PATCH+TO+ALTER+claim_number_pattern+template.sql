--PURPOSE    : PATCH TO ALTER claim_number_pattern template from T-YY-NNN to YY-NNN
--AUTHOR     : Raghavendra
--CREATED ON : 08-APR-13

update claim_number_pattern set template='YY-NNN' where business_unit_info='EMEA'
/
COMMIT
/