--Purpose    : Patch TO ADD COLUMN IN INDIVIDUAL_LINE_ITEM TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 15-APR-2014

alter table individual_Line_Item
add
(
brand_item number(19),
service_procedure_definition number(19),
NON_OEM_PART_REPLACED number(19),
 FOREIGN KEY  (brand_item) REFERENCES brand_item(ID),
  FOREIGN KEY  (service_procedure_definition) REFERENCES service_procedure_definition(ID),
   FOREIGN KEY  (NON_OEM_PART_REPLACED) REFERENCES NON_OEM_PART_REPLACED(ID)
)
/