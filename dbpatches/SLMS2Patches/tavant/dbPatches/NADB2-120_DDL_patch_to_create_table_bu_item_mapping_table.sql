--PURPOSE    : patch to create bu_item_mapping table
--AUTHOR     : Raghu
--CREATED ON : 09-APR-2014


create table bu_item_mapping (ITEM NUMBER(19,0) not null, BU VARCHAR2(255 CHAR) not null)
/
alter table bu_item_mapping add constraint "item_fk" foreign key ("ITEM") references ITEM("ID") ENABLE
/
alter table bu_item_mapping add constraint "bu_fk" foreign key ("BU") references BUSINESS_UNIT ("NAME") ENABLE
/

