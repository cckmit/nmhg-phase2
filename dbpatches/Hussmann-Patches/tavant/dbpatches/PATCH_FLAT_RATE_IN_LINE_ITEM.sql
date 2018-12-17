ALTER TABLE
   LINE_ITEM
ADD
   (
    IS_FLAT_RATE NUMBER(1,0)  
)
/
update LINE_ITEM set is_flat_rate = 0 where is_flat_rate is null
/
commit
/
