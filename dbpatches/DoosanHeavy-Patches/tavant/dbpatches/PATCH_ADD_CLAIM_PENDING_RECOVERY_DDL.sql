--PURPOSE    : PATCH TO ADD RECOVERYINITIATED COLUMN
--AUTHOR     : GHANASHYAM DAS
--CREATED ON : 31-MAY-12


alter table claim add (pending_recovery NUMBER(1))
/
create index pending_recovery_idx on claim (pending_recovery)
/