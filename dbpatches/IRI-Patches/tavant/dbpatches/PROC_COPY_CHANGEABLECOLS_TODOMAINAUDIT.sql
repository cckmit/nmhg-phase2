--Purpose    : Changeable Columns from domain_rule are moved to domain_rule_audit
--Purpose    : Data for the same is being migrated
--Author     : prashanth
--Created On : 15-JUL-08


CREATE OR REPLACE PROCEDURE COPY_COLS_TODOMAINAUDIT AS
   CURSOR all_rec IS
      SELECT *
      FROM domain_rule;
BEGIN
   FOR each_rec IN all_rec LOOP
   BEGIN
      UPDATE domain_rule_audit
         SET failure_message = each_rec.failure_message,
             NAME = each_rec.NAME,
             action = each_rec.action
       WHERE domain_rule=each_rec.ID and
	   	   created_on = (SELECT MAX (created_on)
                           FROM domain_rule_audit da1
                           WHERE da1.domain_rule = each_rec.ID);
      EXCEPTION
         WHEN OTHERS THEN
            DBMS_OUTPUT.put_line (SQLERRM);
			END;
      COMMIT;
   END LOOP;
END;
/
BEGIN
COPY_COLS_TODOMAINAUDIT();
END;
/
