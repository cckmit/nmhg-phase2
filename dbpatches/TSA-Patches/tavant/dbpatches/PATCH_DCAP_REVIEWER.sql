--Purpose    : Creating new role dcapClaimReviewer for service allowance
--Author     : Saya Sudha
--Created On : 15-Jan-10

insert into role (id, name, version, d_created_on, d_internal_comments, d_updated_on, d_last_updated_by,
d_created_time, d_updated_time, display_name, d_active)values((select max(id) from role)+1,'dcapClaimReviewer',0,sysdate,null,null,null,null,
null,null,1)
/
COMMIT
/
