--Purpose    : Changing the display name for late fee as part of Business Unit Configuration
--Author     : Arpitha Nadig AR
--Created On : 10-JAN-2013
UPDATE CONFIG_PARAM set DISPLAY_NAME='Percentage deducted from total amount for filing claims later than repair date by 61-90 days(%):' where DESCRIPTION='Late fee percentage value for claims when filed later than repair date by 61-90 days'
/
UPDATE CONFIG_PARAM SET DISPLAY_NAME='Percentage deducted from total amount for filing claims later than repair date by 91-120 days(%):' where DESCRIPTION='Late fee percentage value for claims when filed later than repair date by 91-120 days'
/