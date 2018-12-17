--Purpose    : Performance fix for EHP
--Created On : 03-Jun-2010
--Created By : Ramalakshmi P
--Impact     : EHP display for pending extended warranty notification


CREATE INDEX EXT_WNTY_NOTIFY_FORUNIT_IDX ON extended_warranty_notification(FOR_UNIT)
/
COMMIT
/