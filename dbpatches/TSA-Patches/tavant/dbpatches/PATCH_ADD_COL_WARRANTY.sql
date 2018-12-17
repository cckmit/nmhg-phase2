--Purpose    : Columns added to Warranty
--Author     : Lavin Hawes
--Created On : 05/04/10

ALTER TABLE WARRANTY ADD OPERATOR_TYPE VARCHAR2(100 BYTE)
/

ALTER TABLE WARRANTY ADD OPERATOR_ADDRESS_FOR_TRANSFER NUMBER(19,0)
/

ALTER TABLE WARRANTY ADD (
  CONSTRAINT WNTY_OPER_ADD_FOR_TRANSFER_FK
 FOREIGN KEY (OPERATOR_ADDRESS_FOR_TRANSFER) 
 REFERENCES ADDRESS_FOR_TRANSFER(ID))
/

COMMIT
/