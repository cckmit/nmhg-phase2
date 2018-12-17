--Patch for creating Dealer Warranty Administration role
--author: ashish.agarwal
--Date: 01/04/2009
insert into role(id,name,version,display_name,d_active) 
values ((select max(id)+1 from role),'dealerWarrantyAdmin',1,'Dealer Warranty Administration',1)
/
update role set display_name='Dealer Sales Administration' where name='dealerSalesAdministration'
/
update role set display_name='Dealer Administration' where name='dealerAdministrator'
/
update role set display_name='Dealer Technician' where name='technician'
/
update role set display_name='Dealer SalesPerson' where name='salesPerson'
/
COMMIT
/