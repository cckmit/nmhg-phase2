--Purpose    : Patch for Adding NAME COLUMN IN ORGANIZATION_ADDRESS TABLE
--Author     : KALYANI
--Created On : 20-MAR-2013

alter table organization_address add (NAME VARCHAR2(255 CHAR))
/

