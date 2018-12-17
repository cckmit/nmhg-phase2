--Purpose    : PATCH FOR CHANGING FAULT FOUND/CAUSED BY ON CLAIM TO ID
--Author     : ramalakshmi p
--Created On : 28-SEP-08


alter table service_information add(caused_by_new NUMBER(19))
/
alter table service_information add(fault_found_new NUMBER(19))
/
update service_information t1 set t1.fault_found_new = (select id from failure_type_definition where name = t1.fault_found) where fault_found is not null
/
update service_information t1 set t1.caused_by_new = (select id from  failure_cause_definition where name = t1.caused_by) where caused_by is not null
/
alter table SERVICE_INFORMATION rename column FAULT_FOUND TO FAULT_FOUND_OLD
/
alter table SERVICE_INFORMATION rename column CAUSED_BY TO CAUSED_BY_OLD
/
ALTER TABLE SERVICE_INFORMATION RENAME COLUMN FAULT_FOUND_NEW TO FAULT_FOUND
/
ALTER TABLE SERVICE_INFORMATION RENAME COLUMN CAUSED_BY_NEW TO CAUSED_BY
/
alter table service_information add constraint service_info_fauldfound_fk foreign key (fault_found) references failure_type_definition
/
alter table service_information add constraint service_info_causedby_fk foreign key (caused_by) references failure_cause_definition
/
COMMIT
/
