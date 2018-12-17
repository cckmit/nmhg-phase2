-- Purpose: Adding business unit info to Miscellenous Item Configuration
-- Author: Jhulfikar Ali A
-- Created On: 18 Feb 2009

alter table MISC_ITEM_CRITERIA add(business_Unit_Info VARCHAR2(255))
/
alter table MISC_ITEM_CRITERIA 
add (d_active number(1, 0) default 1, D_CREATED_BY NUMBER, D_LAST_UPDATED_BY NUMBER,
D_CREATED_ON DATE,D_UPDATED_ON DATE,D_INTERNAL_COMMENTS VARCHAR2(255),
D_CREATED_TIME TIMESTAMP, D_UPDATED_TIME TIMESTAMP)
/
alter table MISC_ITEM add(business_Unit_Info VARCHAR2(255))
/
COMMIT
/