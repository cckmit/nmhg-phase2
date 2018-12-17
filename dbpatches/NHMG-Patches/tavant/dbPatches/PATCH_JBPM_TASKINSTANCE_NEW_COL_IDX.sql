--PURPOSE    : Drop the bitmap index from jbpm_variableinstance table
--AUTHOR     : Nandakumar Devi
--CREATED ON : 14-MAY-12
--PURPOSE    : PATCH TO ADD CLAIM_ID & PART_ID COLUMN
--AUTHOR     : GHANASHYAM DAS
--CREATED ON : 08-MAY-12
--PURPOSE    : PATCH TO ADD CLAIM_ID & PART_ID INDEXES
--AUTHOR     : GHANASHYAM DAS
--CREATED ON : 09-MAY-12

drop index IDX_JVI_CLASS_NAME
/
alter table jbpm_taskinstance add (claim_id number(19),part_return_id number(19))
/
create index claim_id_idx on jbpm_taskinstance (claim_id)
/
create index part_return_id_idx on jbpm_taskinstance (part_return_id)
/