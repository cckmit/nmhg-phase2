--Purpose    : Performance issue fix for Required parts return
--Author     : Saya Sudha
--Created On : 20/12/2011

CREATE INDEX IG_ITEMS_IN_GROUP_IDX ON ITEMS_IN_GROUP(ITEM_GROUP)
/
COMMIT
/