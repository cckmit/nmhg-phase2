--Purpose    : Patch TO ADD COLUMN IN INDIVIDUAL_INE_ITEM TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 14-APR-2014
alter  table
individual_Line_Item
add
(	  
dealer_netprice_updated NUMBER(1)   
)
/
update individual_Line_Item set dealer_netprice_updated=0 where dealer_netprice_updated is null
/
commit
/