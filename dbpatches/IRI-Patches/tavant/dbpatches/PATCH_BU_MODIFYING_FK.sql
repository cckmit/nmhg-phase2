--Purpose: For creating a Foreign Key between entity classes and the BusinessUnit class
--Author: Shraddha Nanda P 
--Created On: Date 06 Sept 2008

alter table claim 
add constraint claim_Business_Unit_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table upload_history 
add constraint upload_history_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table domain_rule 
add constraint domain_rule_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table domain_predicate 
add constraint domain_predicate_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table campaign 
add constraint campaign_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table item_group 
add constraint item_group_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table battery_test_sheet 
add constraint battery_test_sheet_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table additional_attributes 
add constraint additional_attributes_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table attribute 
add constraint attribute_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table campaign_class 
add constraint campaign_class_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table claim_number_pattern 
add constraint claim_number_pattern_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table config_value 
add constraint config_value_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table dcap_claim
add constraint dcap_claim_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table dealer_scheme
add constraint dealer_scheme_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table fault_code_definition
add constraint fault_code_definition_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table inventory_item
add constraint inventory_item_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table item_base_price
add constraint item_base_price_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table item_price_modifier
add constraint item_price_modifier_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table item_price_criteria
add constraint item_price_criteria_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table labor_rates
add constraint labor_rates_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table list_of_values
add constraint list_of_values_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table item_scheme
add constraint item_scheme_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table part_return_definition
add constraint part_return_definition_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table payment_definition
add constraint payment_definition_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table payment_variable
add constraint payment_variable_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table policy_rates
add constraint policy_rates_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table role_scheme
add constraint role_scheme_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table supplier
add constraint supplier_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table sync_tracker
add constraint sync_tracker_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table travel_rates
add constraint travel_rates_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table user_scheme
add constraint user_scheme_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table warehouse
add constraint warehouse_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table policy_definition
add constraint policy_definition_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table Localized_Messages
add constraint Localized_Messages_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
alter table service_procedure_definition
add constraint service_prc_def_BU_fk
Foreign Key(business_unit_info)
references business_unit(name)
/
