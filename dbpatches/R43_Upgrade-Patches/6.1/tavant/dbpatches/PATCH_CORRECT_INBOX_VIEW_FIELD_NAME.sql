--Purpose    : Patch to update the inbox view field from payment.totalAmount to payment.activeCreditMemo.paidAmount
--Created On : 18-Jun-2011
--Created By : Kuldeep Patil
--Impact     : None

UPDATE INBOX_VIEW SET FIELD_NAMES = REPLACE(FIELD_NAMES, 'payment.totalAmount', 'payment.activeCreditMemo.paidAmount.abs()') WHERE TYPE = 'ClaimSearches'
/
COMMIT
/