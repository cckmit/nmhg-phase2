--Purpose    : Alter org_user table for adding preferred_bu column
--Author     : Ramprasad
--Created On : 20-Jan-11

alter table org_user add preferred_bu varchar(30)
/
COMMIT
/