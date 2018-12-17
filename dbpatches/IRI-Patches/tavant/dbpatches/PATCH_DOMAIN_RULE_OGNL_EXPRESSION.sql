--Purpose    : Patch for adding a new column (ognl_expression) to domain_rule table. 
--             This column will store the rule's ognl expression.
--Author     : nandakumar.devi
--Created On : 01-July-09

ALTER TABLE domain_rule ADD (ognl_expression CLOB)
/
commit
/