--Purpose    : To add constraint on ORG_USER column to refer ORG_USER table on the request of Single Instance migration team.
--Created On : 15-Feb-2011
--Created By : kuldeep.patil

ALTER TABLE BU_USER_MAPPING ADD CONSTRAINT BU_USER_MAPPING_ORG_USER_FK FOREIGN KEY (ORG_USER) REFERENCES ORG_USER(ID)
/
commit
/