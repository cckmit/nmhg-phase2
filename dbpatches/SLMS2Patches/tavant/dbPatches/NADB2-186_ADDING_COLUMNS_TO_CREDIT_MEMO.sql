--Purpose    : Patch TO ADD COLUMN IN CREDIT_MEMO TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 22-JULY-2014
alter table credit_memo add
(
CREDIT_MEMO_COMMENTS VARCHAR2(4000 CHAR) 
)
/