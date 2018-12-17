--Purpose    : Domain changes for supplier recovery module
--Author     : sUDAKSH cHOHAN
--Created On : 05/02/10
--Impact     : None

alter table recovery_claim_info add ( causal_Part_Recovery number(1,0))
/
commit
/
alter table recovery_info drop column causal_part_recovery
/
alter table recovery_claim_info add (recovery_claim number(19,0))
/
alter table recovery_claim add(recovery_claim_info number(19,0))
/
ALTER TABLE recovery_claim_info ADD (
	CONSTRAINT FK_REC_RECOVERY_CLAIM
	FOREIGN KEY (RECOVERY_CLAIM) 
	REFERENCES RECOVERY_CLAIM (ID)
)
/
ALTER TABLE RECOVERY_CLAIM ADD (
	CONSTRAINT FK_REC_RECOVERY_CLAIM_INFO
	FOREIGN KEY (RECOVERY_CLAIM_INFO) 
	REFERENCES RECOVERY_CLAIM_INFO (ID)
)
/
drop table rec_claim_info_rec_claims
/
commit
/


