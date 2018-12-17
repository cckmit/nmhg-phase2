--Purpose    : db patch for Supplier recovery module 
--Created On : 25-Jan-2010
--Created By : Sudaksh Chohan
--Impact     : None


--alter table recoverable_part_bar_codes rename column bar_codes to BAR_CODES_ELEMENT
--/
--KULDEEP : Moved this part to PATCH_SUPPLIER_RECOVERY.sql under RECOVERABLE_PART_BAR_CODES table creation script, so this script is not needed.
Commit
/