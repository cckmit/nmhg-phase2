--Purpose    : db patch for Supplier recovery module 
--Created On : 27-Jan-2010
--Created By : Sudaksh Chohan
--Impact     : None


alter table recovery_info add (saved_At_Part_Level number(1,0))
/
Commit
/