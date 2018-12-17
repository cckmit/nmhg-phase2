-- Author: Jitesh Jain
-- Date : 12th September,2008
-- Reason: Number of days from which a dealer is displayed a warning regarding the ageing of an equipment in stock

	
INSERT
INTO config_param(id,   description,   display_name,   name,   type,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time)
VALUES(config_param_seq.nextval,   'Number of days from which a dealer is displayed a warning regarding the ageing of an equipment in stock.',   'Stock Inventory Warning Window',   'daysForStockInventoryWarningWindow',   'number',   sysdate,   'Configuration',   sysdate,   NULL,   NULL,   NULL)
/
INSERT
INTO config_value(id,   active,   VALUE,   config_param,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   business_unit_info,   d_created_time,   d_updated_time)
VALUES(config_value_seq.nextval,   1,   '90',  (select id from config_param where name = 'daysForStockInventoryWarningWindow') ,   sysdate,   NULL,   sysdate,   NULL,   'Club Car',   NULL,   NULL)
/
commit
/
