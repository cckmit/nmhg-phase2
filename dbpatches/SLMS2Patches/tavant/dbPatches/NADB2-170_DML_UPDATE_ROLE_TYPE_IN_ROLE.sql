--PURPOSE    : 'inventorysearch','inventorylisting','dealerSalesAdministration','dealerAdministrator' to be ROLE_TYPE EXTERNAL.
--AUTHOR     : Chetan K
--CREATED ON : 04-JUN-2014
update role set role_type='EXTERNAL' where name in ('inventorysearch','inventorylisting','dealerSalesAdministration','dealerAdministrator')
/