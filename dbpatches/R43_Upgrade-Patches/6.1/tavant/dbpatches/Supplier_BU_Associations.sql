--Map supplier to BU which has the supplier's logins
insert into bu_org_mapping (org,bu)
select distinct s.id,bum.bu
from party p,supplier s,org_user_belongs_to_orgs ou,org_user u,bu_user_mapping bum 
where p.id=s.id and p.d_active=1 and s.id=ou.belongs_to_organizations
and ou.org_user=u.id and u.d_active=1 and bum.org_user=u.id
and (select count(*) from bu_org_mapping bom
  where bom.org=p.id and bom.bu=bum.bu)=0
/
--Map supplier to BU which has the supplier's contracts
insert into bu_org_mapping (org,bu)
select distinct s.id,c.business_unit_info
from contract c,supplier s,party p
where c.d_active=1 and c.supplier=s.id and s.id=p.id
and (select count(*) from bu_org_mapping bom 
  where bom.org=s.id and bom.bu=c.business_unit_info)=0
/
--Map supplier to BU which has the supplier's items
insert into bu_org_mapping (org,bu)
select distinct s.id, i.business_unit_info
from item i,supplier s,party p
where i.d_active=1 and i.owned_by=s.id and s.id=p.id
and (select count(*) from bu_org_mapping bom
  where bom.org=s.id and bom.bu=i.business_unit_info)=0
/
commit
/
--Merger suppliers with same name & number
declare 
  cursor all_dups is
  select p.name,s.supplier_number
  from party p,supplier s
  where p.id=s.id and p.d_active=1
  group by p.name,s.supplier_number having count(*)>1;
  
  cursor dups(supName varchar2,supNumber varchar2,supId number) is
  select s.id
  from party p,supplier s
  where p.id=s.id and p.d_active=1 and p.name=supName
    and s.supplier_number=supNumber and s.id != supId;
    
  sup_id number(19,0);
begin

for each_rec in all_dups loop
  select max(p.id) into sup_id
  from party p,supplier s
  where p.id=s.id and p.d_active=1
    and p.name=each_rec.name 
    and s.supplier_number=each_rec.supplier_number;
  
  for dup in dups(each_rec.name,each_rec.supplier_number,sup_id) loop

    update bu_org_mapping set org=sup_id
    where org=dup.id and (select count(*) from bu_org_mapping t 
      where t.org=sup_id and t.bu=bu_org_mapping.bu)=0;
      
    delete from bu_org_mapping where org=dup.id;
    
    update contract set supplier=sup_id where supplier=dup.id;
    update item set owned_by=sup_id where owned_by=dup.id;
    update org_user_belongs_to_orgs set belongs_to_organizations=sup_id 
      where belongs_to_organizations=dup.id;
    update recovery_claim set supplier=sup_id where supplier=dup.id;
    update attribute_association set supplier=sup_id where supplier=dup.id;
    update supplier_labels set supplier=sup_id where supplier=dup.id;
    update supplier_locations set supplier=sup_id where supplier=dup.id;
    
    update party set name='Inactive_'||name,
      d_active=0,d_updated_on=sysdate,d_updated_time=systimestamp,
      d_internal_comments=d_internal_comments||':Deactivate Duplicate Supplier'
      where id = dup.id;
      
    commit;
  end loop;
  
end loop;
end;
/
commit
/