-- Patch to add column to Supplier_Part_Return
-- Author		: ParthaSarathy R
-- Created Date : 12-Feb-2014

alter table supplier_part_return add(warehouse_location varchar2(255))
/