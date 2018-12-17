--Purpose : Additional properties required for Routing Rule Groups
--Author  : raghuram.d
--Date    : 16-Jul-09

ALTER TABLE domain_rule_group ADD stop_rule_proc_on_no_result NUMBER(1,0) NULL
/
ALTER TABLE domain_rule_group ADD stop_rule_proc_on_multi_result NUMBER(1,0) NULL
/