--Purpose    : DML for NMHGSLMS-425 -Patch to enable State_Mandate in Cost_Category
--Author     : Arpitha Nadig AR
--Created On : 16-JAN-2013
UPDATE cost_category SET state_mandate='1' WHERE code='NON_OEM_PARTS'
/
UPDATE cost_category SET state_mandate='1' WHERE code='LABOR'
/
UPDATE cost_category SET state_mandate='1' WHERE code='TRAVEL_DISTANCE'
/
UPDATE cost_category SET state_mandate='1' WHERE code='FREIGHT_DUTY'
/
UPDATE cost_category SET state_mandate='1' WHERE code='MEALS'
/
UPDATE cost_category SET state_mandate='1' WHERE code='PARKING'
/
UPDATE cost_category SET state_mandate='1' WHERE code='TRAVEL_TRIP'
/
UPDATE cost_category SET state_mandate='1' WHERE code='MISC_PARTS'
/
UPDATE cost_category SET state_mandate='1' WHERE code='PER_DIEM'
/
UPDATE cost_category SET state_mandate='1' WHERE code='RENTAL_CHARGES'
/
UPDATE cost_category SET state_mandate='1' WHERE code='ADDITIONAL_TRAVEL_HOURS'
/
UPDATE cost_category SET state_mandate='1' WHERE code='OTHER_FREIGHT_DUTY'
/
UPDATE cost_category SET state_mandate='1' WHERE code='LOCAL_PURCHASE'
/
UPDATE cost_category SET state_mandate='1' WHERE code='OTHERS'
/
UPDATE cost_category SET state_mandate='1' WHERE code='TOLLS'
/
UPDATE cost_category SET state_mandate='1' WHERE code='TRAVEL'
/
UPDATE cost_category SET state_mandate='1' WHERE code='TRANSPORTATION'
/
UPDATE cost_category SET state_mandate='1' WHERE code='HANDLING_FEE'
/
UPDATE cost_category SET state_mandate='0' WHERE code='OEM_PARTS'
/
commit
/