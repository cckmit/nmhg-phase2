--Purpose    : Patch for adding the column load Type in shipment table.
--Author     : Deepak Patel
--Created On : 17-Dec-2012

alter table SHIPMENT add (LOAD_TYPE varchar2(10))
/
