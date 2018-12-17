update domain_rule set status=(
  select ra.status from domain_rule_audit ra 
  where ra.domain_rule=domain_rule.id and ra.d_active=1 
  and ra.list_index=(select max(t.list_index) from domain_rule_audit t 
    where t.domain_rule=domain_rule.id and t.d_active=1)
  ) 
where status != (select ra.status from domain_rule_audit ra 
    where ra.domain_rule=domain_rule.id and ra.d_active=1 
    and ra.list_index=(select max(t.list_index) from domain_rule_audit t 
      where t.domain_rule=domain_rule.id and t.d_active=1)
)
/
commit
/