delete from i18ndomain_rule_text where failure_description like 'Claiming dealer is same as the Retailing Dealer'
/
delete from domain_rule_audit  where domain_rule in (
select domain_rule from i18ndomain_rule_text where failure_description like 'Claiming dealer is same as the Retailing Dealer')
/
delete from domain_rule where id in (
select domain_rule from i18ndomain_rule_text where failure_description like 'Claiming dealer is same as the Retailing Dealer')
/
delete from domain_predicate where id in(
select d.predicate from i18ndomain_rule_text i,domain_rule d where d.id=i.domain_rule and failure_description like 'Claiming dealer is same as the Retailing Dealer')
/
