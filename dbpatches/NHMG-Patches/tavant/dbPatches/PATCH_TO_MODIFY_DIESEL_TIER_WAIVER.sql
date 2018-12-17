--PURPOSE    : To Drop Not Null Constraint for approved_by_agent_name in diesel_tier_waiver table
--AUTHOR     : ParthaSarathy R
--CREATED ON : 03-OCT-13

ALTER TABLE diesel_tier_waiver modify approved_by_agent_name varchar2(255) NULL
/