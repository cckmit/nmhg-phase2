-- Patch to update inbox view field names containing 'latestWarranty.discountNumber' or 'latestWarranty.discountPercentage'
-- Created On : 09-Sep-14
-- Created by : Chetan K
UPDATE inbox_view
SET FIELD_NAMES=REPLACE(REPLACE(FIELD_NAMES,'latestWarranty.discountNumber','discAuthorizationNumber'),'latestWarranty.discountPercentage','discountPercent')
WHERE FIELD_NAMES LIKE '%latestWarranty.discountNumber%'
OR FIELD_NAMES LIKE '%latestWarranty.discountPercentage%'
/