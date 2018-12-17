--Purpose    : User related indexes
--Author     : Nandakumar Devi
--Created On : 04-AUG-09

CREATE INDEX ORG_USER_BELONGS_TO_ORGS_I1 ON ORG_USER_BELONGS_TO_ORGS(BELONGS_TO_ORGANIZATIONS)
/
CREATE INDEX BU_USER_MAPPING_I1 ON BU_USER_MAPPING(ORG_USER)
/