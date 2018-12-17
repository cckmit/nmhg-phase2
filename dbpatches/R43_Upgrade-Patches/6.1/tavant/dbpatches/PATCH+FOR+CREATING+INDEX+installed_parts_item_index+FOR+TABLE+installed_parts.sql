--PURPOSE    : PATCH FOR CREATING INDEX installed_parts_item_index FOR TABLE installed_parts
--AUTHOR     : ROHIT MEHROTRA
--CREATED ON : 18-MAY-11
--IMPACT     : QUICK SEARCH MAJOR COMPONENT
create index installed_parts_item_index on installed_parts(item)
/
COMMIT
/