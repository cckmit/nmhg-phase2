--Purpose    : Scripts for creating SYNC_TYPE table, changes made as a part of request sent by Single Instance DEV Team
--Created On : 16-Mar-2011
--Created By : Kuldeep Patil
--Impact     : None

alter table PART_RETURN_ACTION add CONSTRAINT PART_RETURN_ACTION_FK1 FOREIGN KEY (D_LAST_UPDATED_BY ) REFERENCES ORG_USER ("ID")
/
commit
/