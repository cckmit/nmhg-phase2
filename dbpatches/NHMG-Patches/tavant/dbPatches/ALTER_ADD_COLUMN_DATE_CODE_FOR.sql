--Purpose    : Patch for adding the column in installed_parts & oem_part_replaced table.
--Author     : Noor Pasha
--Created On : 05-MAR-2013

alter table installed_parts add (DATE_CODE VARCHAR2(100))
/
alter table oem_part_replaced add (DATE_CODE VARCHAR2(100))
/
