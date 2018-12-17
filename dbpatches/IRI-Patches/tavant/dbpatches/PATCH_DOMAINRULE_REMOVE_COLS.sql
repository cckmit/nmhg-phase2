-- Author Prashanth Konda
-- Reason: To audit rules we need to store certain columns in domain rule audit
-- These columns need to be removed from domain rule to avoid duplication
alter table domain_rule drop column name
/
alter table domain_rule drop column failure_message
/
commit
/