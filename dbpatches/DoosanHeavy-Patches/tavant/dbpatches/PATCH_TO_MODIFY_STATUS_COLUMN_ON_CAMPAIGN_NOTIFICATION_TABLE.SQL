--PURPOSE    : PATCH_TO_MODIFY_STATUS_COLUMN_ON_CAMPAIGN_NOTIFICATION_TABLE
--AUTHOR     : RAVIKUMAR.Y
--CREATED ON : 26-AUG-12

alter table campaign_notification modify (status default 'Active')
/