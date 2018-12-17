-- Changing the datatype of code from long to varchar 
-- Author Hari Krishna Y D
-- April 14 2009

ALTER TABLE market_type ADD(code_backup VARCHAR2(255))
/
UPDATE market_type s SET s.code_backup = s.code
/
ALTER TABLE market_type DROP COLUMN code
/
ALTER TABLE market_type RENAME COLUMN code_backup TO code
/
COMMIT
/