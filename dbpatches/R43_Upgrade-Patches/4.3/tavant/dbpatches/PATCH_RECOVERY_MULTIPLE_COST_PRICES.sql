--Purpose    : db patch for maintaing multiple cost prices
--Created On : 23-FEB-2010
--Created By : Sudaksh Chohan
--Impact     : None

--alter table recoverable_part add (material_cost_amt NUMBER(19,2))
--/
--alter table recoverable_part add (material_cost_curr VARCHAR2(255 CHAR))
--/
--alter table recoverable_part add (cost_price_per_unit_amt NUMBER(19,2))
--/
--alter table recoverable_part add (cost_price_per_unit_curr VARCHAR2(255 CHAR))
--/
--KULDEEP : Moved this part to PATCH_SUPPLIER_RECOVERY.sql under RECOVERABLE_PART table creation script, so this script is not needed.
Commit
/