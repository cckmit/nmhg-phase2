--Naae : Joseph
--Impact : Patch to fix duplicate list indexes for four claims reported by the OBIEE team
declare
v_new_list number :=0;
begin

delete from CLAIM_AUDIT where FOR_CLAIM = 1120025854620 and LIST_INDEX = 8;

for K in (select FOR_CLAIM,LIST_INDEX,COUNT(*) from CLAIM_AUDIT where FOR_CLAIM in (1120114299140,1120048294500,1120073978820) group by FOR_CLAIM,LIST_INDEX  having COUNT(*) > 1)
LOOP
  V_NEW_LIST := 0;
  for I in (select id from claim_audit where FOR_CLAIM = k.FOR_CLAIM order by id asc)
  LOOP
  update CLAIM_AUDIT set LIST_INDEX = V_NEW_LIST where id = I.id;
  V_NEW_LIST := V_NEW_LIST+1;
  end loop;

end LOOP;
commit;
end;
/
commit
/