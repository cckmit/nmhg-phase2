-- Patch to add columns to Part_Recovery_Audit
-- Author		: ParthaSarathy R
-- Created Date : 12-Feb-2014

alter table part_recovery_audit add(failure_cause varchar2(255))
/
alter table part_recovery_audit add(acceptance_cause varchar2(255))
/