--Purpose    : Added New column in Failed Rule to store Default en_US failure Msg
--Author     : rakesh.r
--Created On : 20-SEP-08


ALTER TABLE FAILED_RULE
ADD (default_rule_msg_in_us VARCHAR2(255 BYTE))
/