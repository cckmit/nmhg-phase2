--Purpose    : Rejection Reason is added as forein key in Domain_Rule
--Author     : pratima.rajak
--Created On : 19-July-08

alter table domain_rule add rejection_reason number(19)
/
commit
/