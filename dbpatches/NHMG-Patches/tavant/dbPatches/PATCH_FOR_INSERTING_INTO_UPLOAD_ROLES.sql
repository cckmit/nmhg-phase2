-- Patch for enabling admin, processor, inventoryAdmin to view upload history of Delivery Reports
-- Author		: PARTHASARATHY R
-- Created On	: 30-July-2013

insert into upload_roles values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'), (select id from role where name='processor'))
/
insert into upload_roles values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'), (select id from role where name='admin'))
/
insert into upload_roles values((select id from upload_mgt where name_of_template = 'warrantyRegistrations'), (select id from role where name='inventoryAdmin'))
/
commit
/