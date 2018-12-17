-- Patch to Add column Receipt_Date in Shipment Table
-- Author		: ParthaSarathy R
-- Created On	: 30-Jan-2014

alter table SHIPMENT ADD (RECEIPT_DATE TIMESTAMP(6))
/