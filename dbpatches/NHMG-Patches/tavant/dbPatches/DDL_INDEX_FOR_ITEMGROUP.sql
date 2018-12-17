--PURPOSE    : Creating Index to imporove performance for ITem upload
--AUTHOR     : Saibal
--CREATED ON : 31-may-13

CREATE INDEX itemgroup_grptype_ix 
ON item_group (Upper(item_group_type))
/
