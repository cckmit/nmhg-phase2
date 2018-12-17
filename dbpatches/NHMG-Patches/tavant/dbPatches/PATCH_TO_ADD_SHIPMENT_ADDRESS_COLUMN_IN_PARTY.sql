--PURPOSE    : PATCH_TO_ADD_SHIPMENT_ADDRESS_COLUMN_IN_PARTY
--AUTHOR     : Ajit
--CREATED ON : 04-SEP-13

  alter table
   party
  add
    (
      shipment_address NUMBER(19)
   )
/
commit
/