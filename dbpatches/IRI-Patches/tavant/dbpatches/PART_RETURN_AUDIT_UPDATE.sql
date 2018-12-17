-- Purpose: increased the size of comments since its there in R3
-- Author: Hari Krishna Y D
-- Created On: 18 June 2009

alter table part_return_audit modify(comments VARCHAR2(4000))
/
COMMIT
/