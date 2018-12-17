/* Patch for Updating Domain Predicate and Domain Rule Audit XML*/
UPDATE domain_predicate
SET predicate_asxml   = REPLACE(predicate_asxml,'<domainPredicate id="', '<domainPredicate id="10')
WHERE predicate_asxml LIKE '%domainPredicate%'
AND length(id) > 13
/
commit
/
UPDATE domain_rule_audit
SET rule_snapshot_string = REPLACE(rule_snapshot_string,'<id>','<id>10')
WHERE length(id) > 13
/
commit
/