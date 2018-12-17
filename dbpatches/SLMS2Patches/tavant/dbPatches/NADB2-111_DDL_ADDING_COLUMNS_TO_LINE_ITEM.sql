--Purpose    : Patch TO ADD COLUMN IN LINE_ITEM TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 02-APR-2014

alter table Line_Item  add
     (
   percentage_Configured NUMBER(19,6),
   percentage_ConfiguredSMandate NUMBER(19,6)
	)
/
