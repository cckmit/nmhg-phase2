--Purpose    : Patch for adding the columns pdi_form_name in item table.
--Author     : Pracher Pancholi
--Created On : 12-Sep-2012

alter table item add (pdi_form_name varchar2(100))
/