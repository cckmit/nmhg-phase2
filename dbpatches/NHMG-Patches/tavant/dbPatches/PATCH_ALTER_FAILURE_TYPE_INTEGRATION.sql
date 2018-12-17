--PURPOSE    : PATCH FOR ADDING NEW COLUMN TO FAILURE_TYPE FOR FAILURE CODE SYNC INTEGRATION CHANGE 
--AUTHOR     : ROOPA KARIYAPPA
--CREATED ON : 30-AUGUST-12

alter table failure_type add FOR_FAULT_CODE  NUMBER(19,0)
/
ALTER TABLE failure_type add CONSTRAINT FAILURETYPE_FORFAULTCODE_FK  FOREIGN KEY (FOR_FAULT_CODE) REFERENCES fault_code(id)
/