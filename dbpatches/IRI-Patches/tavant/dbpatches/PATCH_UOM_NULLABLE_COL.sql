-- Partch for UOM Mapping
-- Author: Ramalakshmi P
-- May 28 2009

ALTER TABLE uom_mappings MODIFY (BASE_UOM NOT NULL)
/
ALTER TABLE uom_mappings MODIFY (MAPPED_UOM NOT NULL)
/