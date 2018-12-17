--Purpose    : As ITS BU does not have PART ,so Updating the config_param_option value to PARTS. 
               --This wont affect other BU,because they have Concept of PARTS and PART.
--AUTHOR     : Surendra
--CREATED ON : 29-04-2011
-- MODIFIED PURPOSE: [TWMS4.3U-589] Adding patch to TS ESA BU for correcting tree rht value as per child node rgt value
-- MoDIFIED BY: DEVENDRA BABU N
-- MODIFIED ON: 12-05-2011

update config_param_option set value='PARTS' WHERE display_value='Part'
/
COMMIT
/
update item_group 
set rgt = (select rgt+1 from item_group where name = 'Part' and business_unit_info = 'Transport Solutions ESA') 
where id = (select is_part_of from item_group where name = 'Part' and business_unit_info = 'Transport Solutions ESA')
/
COMMIT
/