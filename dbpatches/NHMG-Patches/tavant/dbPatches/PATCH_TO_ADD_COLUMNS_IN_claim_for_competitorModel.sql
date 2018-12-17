--PURPOSE    : PATCH_TO_ADD_COLUMNS_IN_claim_for_competitorModel
--AUTHOR     : Raghavendra
--CREATED ON : 27-AUG-13

 alter table claim add (COMPETITOR_MODEL_BRAND varchar2(255),
 COMP_MODEL_TRUCKSERIALNUM varchar2(255),
 COMP_MODEL_DESCRIPTION varchar2(255))
/
commit
/