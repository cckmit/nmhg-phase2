--PURPOSE    : PATCH TO insert values into table wpra_number_pattern  
--AUTHOR     : Raghu
--CREATED ON : 10-Apr-13

INSERT INTO wpra_number_pattern (id,is_active,template,d_active,business_unit_info)
VALUES (HIBERNATE_SEQUENCE.nextval,1,'T-W-YY-NNN',1,'EMEA')
/
COMMIT
/