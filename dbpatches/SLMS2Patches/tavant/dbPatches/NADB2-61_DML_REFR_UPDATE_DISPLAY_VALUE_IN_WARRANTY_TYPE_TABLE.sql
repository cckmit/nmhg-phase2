--Purpose    : DML for Req. No 57- Display values for warranty type
--Author     : Arpitha Nadig AR
--Created On : 28-JAN-2013
update warranty_type set display_value='dropdown.common.goodwill' where type='POLICY'
/
update warranty_type set display_value='dropdown.common.standard' where type='STANDARD'
/
update warranty_type set display_value='dropdown.common.extended' where type='EXTENDED'
/
commit
/