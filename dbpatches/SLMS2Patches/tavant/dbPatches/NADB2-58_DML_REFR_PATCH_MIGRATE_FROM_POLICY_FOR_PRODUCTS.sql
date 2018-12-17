--Purpose    : DML for NMHGSLMS-425 -To migrate date from 
--Author     : Arpitha Nadig AR
--Created On : 27-JAN-2013
declare
cursor c1 is select * from policy_for_products pp where not exists (select 1 from policy_products ppm 
where ppm.policy_definition = pp.policy_defn and ppm.policy_product_mapping = pp.for_product);
v_id number;
begin
  for each_rec in c1 loop
    select policy_prod_mapping_seq.nextVal into v_id from dual;
    insert into policy_product_mapping(id,deductible,product,version,d_Created_on,d_updated_on,D_LAST_UPDATED_BY,d_created_time,d_updated_time,d_active,DEDUCTIBLE_CURR) 
	values(v_id,null,each_rec.FOR_PRODUCT,0,sysdate,sysdate,56,sysdate,sysdate,1,null);
    insert into policy_products (policy_definition,policy_product_mapping) values(each_Rec.policy_defn,v_id);    
    commit;  
  end loop;
end;
/

