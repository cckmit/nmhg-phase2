--Purpose    : Functional index on party name column
--Author     : Nandakumar Devi
--Created On : 04-AUG-09

CREATE INDEX PARTY_I4 ON PARTY(UPPER(NAME))
/