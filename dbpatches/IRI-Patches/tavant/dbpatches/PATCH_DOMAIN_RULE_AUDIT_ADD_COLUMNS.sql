-- Author Prashanth Konda
-- Reason: To audit rules we need to store certain columns in domain rule audit
-- These columns need to be removed from domain rule to avoid duplication

alter table domain_rule_audit add failure_message varchar2(255 char)
/
alter table domain_rule_audit add name varchar2(255 char)
/
alter table domain_rule_audit add action number(19,0)
/
alter table domain_rule_audit add constraint DOMAIN_RULE_AUDIT_ACTION_FK foreign key (action) references domain_rule_action
/
commit
/