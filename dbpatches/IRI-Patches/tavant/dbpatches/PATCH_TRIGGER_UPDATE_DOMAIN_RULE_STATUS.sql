--Purpose    : Update Domain rule whenever its audit is updated
--Author     : prashanth konda
--Created On : 19-Nov-2008

CREATE OR REPLACE TRIGGER UPDATE_DOMAIN_RULE_STATUS
AFTER INSERT  OR UPDATE OR DELETE
ON DOMAIN_RULE_AUDIT
 REFERENCING NEW AS newRow
FOR EACH ROW
BEGIN
UPDATE DOMAIN_RULE SET STATUS= :newRow.status where id= :newRow.domain_rule;
END;
/
commit
/