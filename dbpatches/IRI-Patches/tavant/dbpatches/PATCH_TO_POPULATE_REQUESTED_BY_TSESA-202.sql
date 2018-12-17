--The REQUESTED_BY field in the  REQUEST_WNTY_CVG table is null for all the MIGRATED records
--Below script will populate the REQUESTED_BY field (with the CURRENT_OWNER filed picked from INVENTORY_ITEM) for all these records

CREATE OR REPLACE PROCEDURE UPDATE_MIGRATED_WNTY_EXT_REQS
AS

CURSOR C1 IS 
select * from REQUEST_WNTY_CVG  where requested_by is null ; 
--and d_internal_comments not like 'IRI-Migration%'

v_flag number := 0 ;
v_current_owner number(28) := 0 ;

BEGIN

FOR C1_REC IN C1 LOOP 

v_flag := v_flag + 1 ;
 
select current_owner into v_current_owner 
from inventory_item where id = c1_rec.inventory_item ;

update  REQUEST_WNTY_CVG  set requested_by = v_current_owner 
where id =  c1_rec.id ;

if (v_flag = 100)
then

commit ;
v_flag := 0; 

end if;

END LOOP;
COMMIT;

END;
/

begin
UPDATE_MIGRATED_WNTY_EXT_REQS();
end;
/