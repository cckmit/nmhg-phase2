alter table criteria_evaluation_precedence add  business_unit_info VARCHAR2(255 CHAR)
/
UPDATE criteria_evaluation_precedence set business_unit_info = 'Thermo King TSA' 
/
COMMIT
/