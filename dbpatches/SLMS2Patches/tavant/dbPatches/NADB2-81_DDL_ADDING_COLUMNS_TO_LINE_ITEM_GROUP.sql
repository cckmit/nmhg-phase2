--Purpose    : Patch TO ADD COLUMN IN LINE_ITEM_GROUP TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 27-FEB-2014

alter table Line_Item_group add
     (
	  percentage_applicable number(19,6)
     )
/