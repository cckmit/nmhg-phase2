--PURPOSE    : Patch for dropping not nullable feilds in the Part_group table
--AUTHOR     : Kalyani
--CREATED ON : 27-SEP-13


alter table part_group modify (PART_GROUP_DESCRIPTION null)
/
alter table part_group modify (QTY null)
/
alter table part_group modify (STANDARD_COST null)
/
