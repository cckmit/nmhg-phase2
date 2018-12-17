--Purpose    : Patch TO MODIFY COLUMN TYPE OF TRAVEL DETAILS
--Author     : AJIT KUMAR SINGH
--Created On : 21-AUG-2014

alter table travel_detail add TRAVEL_HR VARCHAR2(20 CHAR)
/
update travel_detail set TRAVEL_HR=HOURS
/
alter table travel_detail drop column HOURS
/
alter table travel_detail add HOURS VARCHAR2(20 CHAR)
/
update travel_detail set HOURS=TRAVEL_HR
/
alter table travel_detail add base_TRAVEL_HR VARCHAR2(20 CHAR)
/
update travel_detail set base_TRAVEL_HR=BASE_HOURS
/
alter table travel_detail drop column BASE_HOURS
/
alter table travel_detail add BASE_HOURS VARCHAR2(20 CHAR)
/
update travel_detail set BASE_HOURS=base_TRAVEL_HR
/