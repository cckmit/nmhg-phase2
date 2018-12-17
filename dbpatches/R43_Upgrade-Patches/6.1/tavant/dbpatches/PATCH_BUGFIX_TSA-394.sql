--Purpose    : Changing the display name for param enable dealers to view partlist, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 11-Oct-2010

UPDATE CONFIG_PARAM SET DISPLAY_NAME = 'Enable Dealers to view Part Return list' WHERE 
NAME = 'enableDealersToViewPartReturnsList'
/
UPDATE CONFIG_PARAM SET DESCRIPTION = 'Enable Dealers to view Part Return list' WHERE 
NAME = 'enableDealersToViewPartReturnsList'
/
COMMIT
/