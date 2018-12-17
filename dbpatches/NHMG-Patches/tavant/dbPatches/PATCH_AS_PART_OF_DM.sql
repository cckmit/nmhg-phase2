--Purpose    : Patch for adding indexes as part of new STAGING server fixes to bring up server
--Author     : Saibal
--Created On : 21-APR-2013

CREATE INDEX inventory_item_ship_to_idx ON inventory_item(ship_to)
/
CREATE INDEX options_slno_idx
ON inventory_item_options (
inventory_item
)
STORAGE (
NEXT 1024 K
)
/
create index part_groups_slno_idx on inventory_item_part_groups(inventory_item)
/