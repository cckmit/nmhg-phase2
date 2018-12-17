--Purpose    : Adding association table to support multi user functionality (user can have belongs to multiple dealerships)
--Author     : Vikas Sasidharan
--Created On : 26-Feb-2009
--Created By : Vikas Sasidharan

CREATE TABLE ORG_USER_BELONGS_TO_ORGS
(
  ORG_USER  		      NUMBER(19)                     NOT NULL,
  BELONGS_TO_ORGANIZATIONS     NUMBER(19)                     NOT NULL
)
/
ALTER TABLE ORG_USER_BELONGS_TO_ORGS ADD (
  CONSTRAINT USER_ORGS_ORG_USER_FK 
 FOREIGN KEY (ORG_USER) 
 REFERENCES ORG_USER (ID),
  CONSTRAINT USER_ORGS_ORGANIZATION_FK 
 FOREIGN KEY (BELONGS_TO_ORGANIZATIONS) 
 REFERENCES ORGANIZATION (ID))
/
insert into org_user_belongs_to_orgs(org_user, belongs_to_organizations) select id, belongs_to_organization from org_user where belongs_to_organization is not null
/
commit
/
