--Purpose    : Patch for adding ACCEPATANCE_CAUSE,FAILURE_CAUSE column to part_return_audit table.
--Author     : ROHIT MEHROTRA
--Created On : 28-APR-2013

ALTER TABLE part_return_audit ADD ACCEPTANCE_CAUSE VARCHAR2(255)
/
ALTER TABLE part_return_audit ADD FAILURE_CAUSE VARCHAR2(255)
/
