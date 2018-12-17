declare
    cursor c1 is 
      select id,status,for_policy from policy_audit pa where id =  (select max(id) from policy_audit pau where pau.for_policy = pa.for_policy);
    begin
      for each_rec in c1 loop
        update policy set status=each_Rec.status where id = each_rec.for_policy;
        commit;
      end loop;
    end;
/
commit
/