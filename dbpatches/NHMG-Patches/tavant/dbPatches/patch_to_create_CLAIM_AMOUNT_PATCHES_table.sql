--PURPOSE    : patch_to_create_CLAIM_AMOUNT_PATCHES_table
--AUTHOR     : Pracher
--CREATED ON : 16-OCT-13


CREATE TABLE CLAIM_AMOUNT_PATCHES 
(
  CLAIM_ID NUMBER(19, 0) NOT NULL 
, CLAIM_NUMBER VARCHAR2(255 CHAR) 
, PARTS_COST_AMT NUMBER(19, 2) 
, LANDED_COST_AMT NUMBER(19, 2) 
, NON_OEM_PARTS_AMT NUMBER(19, 2) 
, TRAVEL_TOTAL_AMT NUMBER(19, 2) 
, TRAVEL_BY_TRIP_AMT NUMBER(19, 2) 
, TRAVEL_BY_DISTANCE_AMT NUMBER(19, 2) 
, TRAVEL_BY_HOURS_AMT NUMBER(19, 2) 
, ADTNL_TRAVEL_AMT NUMBER(19, 2) 
, LABOR_COST_AMT NUMBER(19, 2) 
, TOTAL_COST_AMT number(19, 2) 
)
/
CREATE TABLE MONTHEND_DATES(
	MONTH_END_DATE DATE,
	REPORT_FROM_DATE DATE,
	REPORT_TO_DATE DATE,
	ACTIVE_MONTHEND CHAR(1)
)
/
commit
/