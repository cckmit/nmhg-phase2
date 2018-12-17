--Purpose    : Patch for adding the column in installed_parts & oem_part_replaced table.
--Author     : Sumesh kumar
--Created On : 27-DEC-2013

alter table TECHNICIAN_CERTIFICATION add (CATEGORY_LEVEL VARCHAR2(10))
/
alter table TECHNICIAN_CERTIFICATION add (CATEGORY_NAME VARCHAR2(100))
/