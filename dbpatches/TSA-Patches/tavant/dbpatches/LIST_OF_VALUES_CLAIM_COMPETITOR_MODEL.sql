--Purpose    : Claim Competitor model Added to Claim
--Author     : lavin.hawes
--Created On : 05/02/10
--Impact     : None

ALTER TABLE CLAIM ADD ( CLAIM_COMPETITOR_MODEL number(19,0))
/


ALTER TABLE CLAIM ADD (
	CONSTRAINT CLAIM_COMPETITORMODEL_FK
	FOREIGN KEY (CLAIM_COMPETITOR_MODEL) 
	REFERENCES LIST_OF_VALUES(ID)
)
/


COMMIT
/


