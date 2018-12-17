--Purpose    : Patch to drop load dimensions column from shipment table.
--Author     : Deepak Patel
--Created On : 28-FEB-2013



alter table shipment drop (HEIGHT, WEIGHT, BREADTH, LENGTH, LOAD_TYPE)
/