--PURPOSE    : PATCH TO CHANGE DISPLAY NAME TO DEALER REPRESENTATIVE IN ROLE
--AUTHOR     : Raghu
--CREATED ON : 15-Apr-2013

update role set display_name='Dealer Representative' where name='salesPerson'
/
COMMIT
/
