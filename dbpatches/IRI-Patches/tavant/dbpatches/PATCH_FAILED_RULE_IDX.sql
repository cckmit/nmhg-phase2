-- Patch to create an index on FAILED_RULE.RULE_DETAIL
-- Author: Nandakumar Devi
-- July 23 2009

CREATE INDEX FAILEDRULE_RULEDETAIL_IX ON FAILED_RULE (RULE_DETAIL)
/