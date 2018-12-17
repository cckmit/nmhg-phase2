--Purpose    : Patch for adding new auditable colums for IRI Tables
--Author     : Ramalakshmi P.
--CREATED On : 06-SEP-08

alter table role_scheme add d_created_time timestamp
/
alter table role_scheme  add d_updated_time timestamp
/
alter table role_group  add d_updated_time timestamp
/
alter table role_group  add d_created_time timestamp
/

