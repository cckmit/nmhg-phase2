--Purpose    : Patch For Implementing De-Activation Of Missing and New Entities
--Author     : Hari Krishna Y D
--Created On : 06-Jan-09

alter table OEM_PART_REPLACED add (d_active number(1, 0) default 1)
/
alter table non_OEM_PART_REPLACED add (d_active number(1, 0) default 1)
/
alter table LINE_ITEM_GROUP_AUDIT add (d_active number(1, 0) default 1)
/
alter table DISCHARGE_TEST add (d_active number(1, 0) default 1)
/
alter table FULL_CHARGE_BATTERY_TEST add (d_active number(1, 0) default 1)
/
alter table HYDROMETER_TEST add (d_active number(1, 0) default 1)
/
alter table ON_CHARGE_VOLTAGE_TEST add (d_active number(1, 0) default 1)
/
alter table NON_OEM_PART_TO_REPLACE add (d_active number(1, 0) default 1)
/
alter table OEM_PART_TO_REPLACE add (d_active number(1, 0) default 1)
/
alter table LIST_OF_VALUES add (d_active number(1, 0) default 1)
/
alter table PART_RETURN_CONFIGURATION add (d_active number(1, 0) default 1)
/
alter table BASE_PRICE_VALUE add (d_active number(1, 0) default 1)
/
alter table CRITERIA_BASED_VALUE add (d_active number(1, 0) default 1)
/
alter table CURRENCY_CONVERSION_FACTOR add (d_active number(1, 0) default 1)
/
alter table ITEM_PRICE_MODIFIER add (d_active number(1, 0) default 1)
/
alter table LABOR_RATE add (d_active number(1, 0) default 1)
/
alter table POLICY_RATE add (d_active number(1, 0) default 1)
/
alter table TRAVEL_RATE add (d_active number(1, 0) default 1)
/
alter table DCAP_CLAIM_AUDIT add (d_active number(1, 0) default 1)
/
alter table DCAP_DETAIL_HISTORY add (d_active number(1, 0) default 1)
/
alter table ATTR_VALUE add (d_active number(1, 0) default 1)
/
alter table ROLE_GROUP add (d_active number(1, 0) default 1)
/
alter table ROLE_SCHEME add (d_active number(1, 0) default 1)
/
alter table WARRANTY_AUDIT add (d_active number(1, 0) default 1)
/
alter table REQUEST_WNTY_CVG_AUDIT add (d_active number(1, 0) default 1)
/
alter table REQUEST_WNTY_CVG add (d_active number(1, 0) default 1)
/
alter table WARRANTY_TASK_INSTANCE add (d_active number(1, 0) default 1)
/
alter table role add (d_active number(1, 0) default 1)
/
alter table CONFIG_PARAM add (d_active number(1, 0) default 1)
/
alter table CONFIG_value add (d_active number(1, 0) default 1)
/