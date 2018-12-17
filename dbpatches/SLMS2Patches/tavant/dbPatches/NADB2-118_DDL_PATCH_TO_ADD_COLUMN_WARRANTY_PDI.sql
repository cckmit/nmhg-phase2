--PURPOSE    : patch to add column in warranty to check PDI Generated
--AUTHOR     : Raghu
--CREATED ON : 08-APR-2014

alter table warranty add pdi_Generated Number(1,0)
/