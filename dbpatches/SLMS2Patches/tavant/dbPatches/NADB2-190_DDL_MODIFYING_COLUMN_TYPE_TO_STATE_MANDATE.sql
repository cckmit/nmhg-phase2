--Purpose    : Patch TO MODIFY COLUMN TYPE OF STATE_MANDATES TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 05-AUG-2014

alter table state_mandates add OEM_PARTS_PERCENT1 NUMBER(19,2)
/
update state_mandates set OEM_PARTS_PERCENT1=OEM_PARTS_PERCENT
/
alter table state_mandates drop column OEM_PARTS_PERCENT
/
alter table state_mandates add OEM_PARTS_PERCENT NUMBER(19,2)
/
update state_mandates set OEM_PARTS_PERCENT=OEM_PARTS_PERCENT1
/
alter table state_mandates drop column OEM_PARTS_PERCENT1
/