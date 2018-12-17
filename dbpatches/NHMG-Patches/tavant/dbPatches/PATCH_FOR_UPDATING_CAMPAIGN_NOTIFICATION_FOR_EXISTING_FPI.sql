--PURPOSE    : Patch to update campaign notification for existing FPI campaigns
--AUTHOR     : Ajit Kumsr Singh
--CREATED ON : 31-March-2013

update campaign_notification set d_active=1 where campaign in(select id from campaign where notifications_generated=0)
/
update campaign set notifications_generated=1 where notifications_generated=0
/
commit
/