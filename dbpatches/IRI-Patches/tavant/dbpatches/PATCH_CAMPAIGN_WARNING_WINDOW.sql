-- Author: Rakesh R
-- Date : 03rd  Oct,2008
-- Reason: Number of days from which yellow and red warning images  is displayed  regarding the duration of an campaign


INSERT
INTO config_param(id,   description,   display_name,   name,   type,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time)
VALUES(config_param_seq.nextval,   'Number of days from which yellow warning is displayed  regarding the duration of an campaign',   'Campaign Warning Window',   'daysForCampaignYellowWarningWindow',   'number',   sysdate,   'Configuration',   sysdate,   NULL,   NULL,   NULL)
/
INSERT
INTO config_value(id,   active,   VALUE,   config_param,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   business_unit_info,   d_created_time,   d_updated_time)
VALUES(config_value_seq.nextval,   1,   '30',  (select id from config_param where name = 'daysForCampaignYellowWarningWindow') ,   sysdate,   NULL,   sysdate,   NULL,   'Club Car',   NULL,   NULL)
/
INSERT
INTO config_param(id,   description,   display_name,   name,   type,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time)
VALUES(config_param_seq.nextval,   'Number of days from which red warning is displayed  regarding the duration of an campaign',   'Campaign Warning Window',   'daysForCampaignRedWarningWindow',   'number',   sysdate,   'Configuration',   sysdate,   NULL,   NULL,   NULL)
/
INSERT
INTO config_value(id,   active,   VALUE,   config_param,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   business_unit_info,   d_created_time,   d_updated_time)
VALUES(config_value_seq.nextval,   1,   '40',  (select id from config_param where name = 'daysForCampaignRedWarningWindow') ,   sysdate,   NULL,   sysdate,   NULL,   'Club Car',   NULL,   NULL)
/
update config_param set param_display_type = 'textbox' where name = 'daysForCampaignYellowWarningWindow'
/
update config_param set param_display_type = 'textbox' where name = 'daysForCampaignRedWarningWindow'
/
commit
/
