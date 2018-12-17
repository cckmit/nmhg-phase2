--Purpose    : Alter PAYMENT_MODIFIER table following change in AuditableColEntity
--Author     : Hari Krishna Y D
--Created On : 22-Jan-09

alter table PAYMENT_MODIFIER add d_created_on date
/
alter table PAYMENT_MODIFIER add d_internal_comments varchar2(255 char)
/
alter table PAYMENT_MODIFIER add d_updated_on date
/
alter table PAYMENT_MODIFIER add d_last_updated_by number(19,0)
/
alter table PAYMENT_MODIFIER add (d_active number(1, 0) default 1)
/
alter table PAYMENT_MODIFIER add d_created_time timestamp
/
alter table PAYMENT_MODIFIER add d_updated_time timestamp
/
COMMIT
/