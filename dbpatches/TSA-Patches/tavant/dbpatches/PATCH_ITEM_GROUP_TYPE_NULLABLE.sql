--Purpose    : Change made to remove item group type not null constraint. Item group type should be populated only for Product Struct Schme which will be handled from backend and item sync.
--Author     : Bharath
--Created On : 11-Jan-10

ALTER TABLE ITEM_GROUP MODIFY (ITEM_GROUP_TYPE NULL)
/
commit
/