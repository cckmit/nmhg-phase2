--Purpose    : Patch for adding shipping instruction column to Contract table
--Author     : Umesha H B
--Created On : 14-APRIL-2013

alter table contract add (SHIPPING_INSTRUCTION varchar2(2000))
/