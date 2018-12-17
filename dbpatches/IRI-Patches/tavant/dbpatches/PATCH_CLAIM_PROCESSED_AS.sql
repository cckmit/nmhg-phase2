--Purpose: Patch for claim processed as
--Author: Ramalakshmi P
--Created On: JUL 02 2009

alter table claim add (claim_Processed_As varchar2(255))
/
COMMIT
/