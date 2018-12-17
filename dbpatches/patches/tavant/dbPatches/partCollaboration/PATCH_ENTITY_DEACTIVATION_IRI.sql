--Purpose    : Patch For Implementing De-Activation Of Missing and New Entities
--Author     : Hari Krishna Y D
--Created On : 06-Jan-09

alter table OEM_PART_REPLACED add (d_active number(1, 0) default 1)
/
alter table non_OEM_PART_REPLACED add (d_active number(1, 0) default 1)
/
alter table LINE_ITEM_GROUP_AUDIT add (d_active number(1, 0) default 1)
/
alter table discharge_test add (d_active number(1, 0) default 1)
/
alter table Full_charge_battery_test add (d_active number(1, 0) default 1)
/
alter table hydrometer_test add (d_active number(1, 0) default 1)
/
alter table on_charge_voltage_test add (d_active number(1, 0) default 1)
/
alter table non_oem_part_to_replace add (d_active number(1, 0) default 1)
/
alter table oem_part_to_replace add (d_active number(1, 0) default 1)
/
alter table list_of_values add (d_active number(1, 0) default 1)
/
alter table Part_Return_Configuration add (d_active number(1, 0) default 1)
/
alter table Base_Price_Value add (d_active number(1, 0) default 1)
/
alter table Criteria_Based_Value add (d_active number(1, 0) default 1)
/
alter table Currency_Conversion_Factor add (d_active number(1, 0) default 1)
/
alter table Item_Price_Modifier add (d_active number(1, 0) default 1)
/
alter table Labor_Rate add (d_active number(1, 0) default 1)
/
alter table Policy_Rate add (d_active number(1, 0) default 1)
/
alter table Travel_Rate add (d_active number(1, 0) default 1)
/
alter table dcap_claim_audit add (d_active number(1, 0) default 1)
/
alter table dcap_detail_history add (d_active number(1, 0) default 1)
/
alter table ATTR_VALUE add (d_active number(1, 0) default 1)
/
alter table role_group add (d_active number(1, 0) default 1)
/
alter table role_scheme add (d_active number(1, 0) default 1)
/
alter table Warranty_Audit add (d_active number(1, 0) default 1)
/
alter table REQUEST_WNTY_CVG_AUDIT add (d_active number(1, 0) default 1)
/
alter table REQUEST_WNTY_CVG add (d_active number(1, 0) default 1)
/
alter table uom_mappings add (d_active number(1, 0) default 1)
/
alter table warranty_task_instance add (d_active number(1, 0) default 1)
/