--Purpose    : Patch to create index on ALTERNATE_ITEM_NUMBER column in ITEM table (Perf Fix)
--Author     : Aarti Chandrasekar
--Created On : 11-Jun-09

CREATE INDEX ITEM_ALTERNATE_IX ON ITEM (ALTERNATE_ITEM_NUMBER)
/