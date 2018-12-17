--Purpose    : Added New column in Failed Rule to store Default en_US failure Msg
--Author     : ramalakshmi.p
--Created On : 30-APR-09

alter table failed_rule modify (rule_msg varchar2(4000))
/
alter table failed_rule modify (default_rule_msg_in_us varchar2(4000))
/

