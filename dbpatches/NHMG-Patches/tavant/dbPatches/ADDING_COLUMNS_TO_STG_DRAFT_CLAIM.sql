--Purpose    : Patch for Adding and Renaming the columns in STG_DRAFT_CLAIM table
--Author     : VAMSI KRISHNA JOLLA
--Created On : 15-MAR-2013

alter table stg_draft_claim add (BRAND VARCHAR2(4000))
/
alter table stg_draft_claim add (REPAIR_START_DATE VARCHAR2(4000))
/
alter table stg_draft_claim add (AUTHORIZATION_RECEIVED VARCHAR2(255))
/
alter table stg_draft_claim add (AUTHORIZATION_NUMBER VARCHAR2(4000))
/
alter table stg_draft_claim add (CONTACT_MANAGEMENT_TICKET_NUM VARCHAR2(4000))
/
alter table stg_draft_claim add (HOURS_ON_TRUCK_DURING_INSTALL NUMBER(7,0))
/
ALTER TABLE stg_draft_claim RENAME COLUMN SERIAL_NUMBER TO TRUCK_SERIAL_NUMBER
/
ALTER TABLE stg_draft_claim RENAME COLUMN MACHINE_HOURS TO HOURS_ON_TRUCK
/
ALTER TABLE stg_draft_claim RENAME COLUMN REPAIR_DATE TO REPAIR_END_DATE
/
