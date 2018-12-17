-- Purpose    : adding column for Assign To User functionality 
-- Author     : RaviKumar.Y
-- Created On : 04-May-2012

ALTER TABLE CLAIM ADD ASSIGN_TO_USER NUMBER(19,0)
/
ALTER TABLE CLAIM add CONSTRAINT "CLAIM_ASSIGNTOUSER_FK" FOREIGN KEY ("ASSIGN_TO_USER") REFERENCES ORG_USER ("ID") ENABLE
/