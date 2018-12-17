--Purpose    : Add column customer_classification party table. 
--Created On : 15-Mar-2011
--Created By : Surendra Varma
--Impact     : None

alter table party  add customer_classification  varchar2(255 CHAR)
/
COMMIT
/