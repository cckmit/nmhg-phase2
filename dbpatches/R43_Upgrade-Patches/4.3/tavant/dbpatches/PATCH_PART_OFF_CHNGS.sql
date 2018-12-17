--PURPOSE    : PATCH FOR ADDING PAYMENT_RECALCULATION_REQUIRED in CLAIM table, CHANGED AS A PART OF 4.3 UPGRADE
--Author     : Kuldeep Patil
--Created On : 11-Oct-10

alter table CLAIM
add PAYMENT_RECALCULATION_REQUIRED NUMBER(1, 0)
/
UPDATE CLAIM 
set payment_recalculation_required = 0
where payment_recalculation_required is null
/
COMMIT
/


