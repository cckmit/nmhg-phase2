alter table Recoverable_part add (RECEIVED_FROM_SUPPLIER NUMBER(4,0))
/
update Recoverable_part set RECEIVED_FROM_SUPPLIER = 0
/
commit
/