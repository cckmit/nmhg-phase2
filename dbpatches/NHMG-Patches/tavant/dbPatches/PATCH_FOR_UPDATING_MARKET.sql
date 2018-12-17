-- Patch to update market table
-- Author: PARTHASARATHY R
-- Created On : 09-NOV-2012

DELETE FROM MARKET WHERE TYPE='Market Type' AND TITLE='Rental' AND CODE='DRUS'
/
UPDATE MARKET SET "PARENT_ID"=(SELECT ID FROM MARKET WHERE TYPE='Market Type' AND TITLE='Rental' AND BUSINESS_UNIT_INFO='NMHG EMEA') WHERE TYPE='Market Application' AND TITLE='Rental' AND BUSINESS_UNIT_INFO='NMHG EMEA'
/
UPDATE MARKET SET "PARENT_ID"=(SELECT ID FROM MARKET WHERE TYPE='Market Type' AND TITLE='Demo' AND BUSINESS_UNIT_INFO='NMHG EMEA') WHERE TYPE='Market Application' AND TITLE='Demo' AND BUSINESS_UNIT_INFO='NMHG EMEA'
/
UPDATE MARKET SET "PARENT_ID"=(SELECT ID FROM MARKET WHERE TYPE='Market Type' AND TITLE='Rental' AND BUSINESS_UNIT_INFO='NMHG US') WHERE TYPE='Market Application' AND TITLE='Rental' AND BUSINESS_UNIT_INFO='NMHG US'
/
UPDATE MARKET SET "PARENT_ID"=(SELECT ID FROM MARKET WHERE TYPE='Market Type' AND TITLE='Demo' AND BUSINESS_UNIT_INFO='NMHG US') WHERE TYPE='Market Application' AND TITLE='Demo' AND BUSINESS_UNIT_INFO='NMHG US'
/
COMMIT
/