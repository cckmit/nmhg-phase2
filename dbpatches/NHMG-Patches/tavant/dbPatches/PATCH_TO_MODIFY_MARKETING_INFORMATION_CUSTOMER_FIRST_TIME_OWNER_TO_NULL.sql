--PURPOSE    : PATCH TO ALTER marketing_information to remove not null constraint for CUSTOMER_FIRST_TIME_OWNER
--AUTHOR     : Pracher
--CREATED ON : 26-March-13

ALTER TABLE marketing_information MODIFY CUSTOMER_FIRST_TIME_OWNER NUMBER(1) NULL
/