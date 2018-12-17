--PURPOSE    : PATCH TO ALTER claim_number_pattern template 
--AUTHOR     : Raghavendra
--CREATED ON : 10-MAR-13

update claim_number_pattern set template='YY-NNN' where business_unit_info='AMER'
/
COMMIT
/