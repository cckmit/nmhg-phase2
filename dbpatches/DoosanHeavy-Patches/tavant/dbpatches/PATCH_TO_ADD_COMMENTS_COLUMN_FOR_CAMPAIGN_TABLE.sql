--PURPOSE    : PATCH TO CREATE CAMPAIGN_AUDIT TABLE FOR CAMPAIGN ACTION HISTORY
--AUTHOR     : RAVIKUMAR.Y
--CREATED ON : 04-JULY-12

alter table campaign add comments varchar2(4000 char)
/