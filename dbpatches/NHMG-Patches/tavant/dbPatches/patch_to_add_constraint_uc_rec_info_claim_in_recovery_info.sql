--PURPOSE    : patch_to_add_constraint uc_rec_info_claim
--AUTHOR     : Pracher
--CREATED ON : 16-OCT-13

alter table recovery_info ADD CONSTRAINT uc_rec_info_claim UNIQUE (warranty_claim, d_active) 
  USING INDEX 
    STORAGE ( 
      NEXT       1024 K
    ) 
/
commit
/