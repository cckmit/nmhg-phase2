declare
cursor c1 is select count(*) cnt,org_user,BELONGS_TO_ORGANIZATIONS from org_user_belongs_to_orgs group by org_user,BELONGS_TO_ORGANIZATIONS
having count(*)> 1;
begin
  for each_rec in c1 loop
    delete from org_user_belongs_to_orgs where org_user =each_rec.org_user 
    and BELONGS_TO_ORGANIZATIONS = each_rec.BELONGS_TO_ORGANIZATIONS and rownum <each_rec.cnt;
  end loop;
  commit;
end;
/