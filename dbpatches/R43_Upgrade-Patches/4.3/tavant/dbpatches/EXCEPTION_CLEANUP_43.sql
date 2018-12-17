--Name : Raghu
--Purpose : to clean up records in the R4 instance based on the exception log

--CONFIG_PARAM_OPTION
delete from config_param_option a where 
(select count(*) from config_param_options_mapping b,config_param p 
where b.option_id=a.id and b.param_id=p.id and p.d_active=1)=0
and (select count(*) from config_value v where v.config_param_option=a.id)=0
/
--I18NASSEMBLY_DEFINITION
delete from i18nassembly_definition where id in (
  select min(ad.id)
  from i18nassembly_definition ad
  group by locale,assembly_definition,name having count(*)>1
)
/
--I18NFAILURE_TYPE_DEFINITION
delete from i18nfailure_type_definition where id in (
  select min(ad.id)
  from i18nfailure_type_definition ad
  group by locale,failure_type_definition,upper(name) having count(*)>1
)
/
--ORG_USER
--Deactivate users without Login
update service set technician=null,d_updated_on=sysdate,d_updated_time=systimestamp,
  d_internal_comments=d_internal_comments||':Deactivate invalid Technician'
where id in (
  select s.id from claim c,service_information si,service s,org_user u
    where c.service_information=si.id and si.service_detail=s.id
    and s.technician=u.id and u.login is null
)
/
update org_user u set
  d_active=0,d_updated_on=sysdate,d_updated_time=systimestamp,
  d_internal_comments=d_internal_comments||':Deactivate invalid Technician'
where login is null
and (select count(*) from org_user_belongs_to_orgs where org_user=u.id)=0
and (select count(*) from user_roles where org_user=u.id)=0
/
--Duplicate login(kevin) : deactivate the one created later
update org_user set d_active=0,login='kevin_deact' where id=1100005369140 and login='kevin'
/
--PARTY
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