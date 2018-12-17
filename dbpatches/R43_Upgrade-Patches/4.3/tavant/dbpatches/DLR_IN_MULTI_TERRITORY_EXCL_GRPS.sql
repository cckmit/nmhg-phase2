--Dropped the dealer from the group 'Territory Exclusion-TK'
--who is also a member of another group in 'Territory Exclusion' scheme
declare
  cursor dup_grps is
  select dg.id grp,sp.id dealer
  from dealer_scheme ds,dealer_scheme_purposes dsp,purpose p,
    dealer_group dg,dealers_in_group dig,service_provider sp
  where ds.business_unit_info='Transport Solutions ESA'
    and ds.id=dsp.dealer_scheme and dsp.purposes=p.id
    and p.name='Territory Exclusion'
    and dg.scheme=ds.id and dg.name='Territory Exclusion-TK'
    and dg.id=dig.dealer_group and dig.dealer=sp.id
    and (select count(*) from dealer_group a,dealers_in_group b
      where a.id!=dg.id and a.id=b.dealer_group and b.dealer=sp.id and a.scheme=ds.id
    )>0;
begin 
  for grp in dup_grps loop 
    delete from dealers_in_group where dealer=grp.dealer and dealer_group=grp.grp;
  end loop;
  commit;
end;
/
--114287 is member of Hong Kong & China in Terrotory Exclusion
--Is a member of China in Org Hierarchy, so removed the dealer from Hong Kong
delete from dealers_in_group where 
dealer=(select id from service_provider where service_provider_number='114287')
and dealer_group=(select dg.id from dealer_group dg,dealer_scheme ds
  where dg.name='Hong Kong' and dg.scheme=ds.id and ds.name='Territory Exclusion'
    and ds.business_unit_info='Transport Solutions ESA'
)
/
--Territory Excl: Tunisia & Algeria, Org hierarchy: Algeria
delete from dealers_in_group where 
dealer=(select id from service_provider where service_provider_number='115790')
and dealer_group=(select dg.id from dealer_group dg,dealer_scheme ds
  where dg.name='Tunisia' and dg.scheme=ds.id and ds.name='Territory Exclusion'
    and ds.business_unit_info='Transport Solutions ESA'
)
/
--Territory Excl: United States Of America & Russia, Org hierarchy: Russia
delete from dealers_in_group where 
dealer=(select id from service_provider where service_provider_number='115858')
and dealer_group=(select dg.id from dealer_group dg,dealer_scheme ds
  where dg.name='United States Of America' and dg.scheme=ds.id and ds.name='Territory Exclusion'
    and ds.business_unit_info='Transport Solutions ESA'
)
/
--Territory Excl: China & Hong Kong, Org hierarchy: Hong Kong
delete from dealers_in_group where 
dealer=(select id from service_provider where service_provider_number='115928')
and dealer_group=(select dg.id from dealer_group dg,dealer_scheme ds
  where dg.name='China' and dg.scheme=ds.id and ds.name='Territory Exclusion'
    and ds.business_unit_info='Transport Solutions ESA'
)
/
--Territory Excl: Hong Kong & Vietnam, Org hierarchy: Vietnam
delete from dealers_in_group where 
dealer=(select id from service_provider where service_provider_number='116064')
and dealer_group=(select dg.id from dealer_group dg,dealer_scheme ds
  where dg.name='Hong Kong' and dg.scheme=ds.id and ds.name='Territory Exclusion'
    and ds.business_unit_info='Transport Solutions ESA'
)
/
commit
/