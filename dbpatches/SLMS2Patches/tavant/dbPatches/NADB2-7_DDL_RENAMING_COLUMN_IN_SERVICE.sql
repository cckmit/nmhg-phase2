--Purpose    : Patch for renaming the column name from no_invoice_available to invoice_available
--Author     : Priyanka S
--Created On : 13-DEC-2013

ALTER TABLE SERVICE RENAME COLUMN NO_INVOICE_AVAILABLE to INVOICE_AVAILABLE
/