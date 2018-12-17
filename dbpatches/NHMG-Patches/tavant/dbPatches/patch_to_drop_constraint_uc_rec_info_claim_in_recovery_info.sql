--PURPOSE    : patch_to_drop_constraint uc_rec_info_claim
--AUTHOR     : Pracher
--CREATED ON : 16-OCT-13

alter table recovery_info DROP CONSTRAINT uc_rec_info_claim
/
DROP index uc_rec_info_claim
/
commit
/