INSERT INTO role (id, name, version,d_created_on,d_internal_comments,
d_updated_on, d_created_time,d_updated_time,d_active,display_name,role_type)
VALUES(role_group_seq.nextval,'enterpriseDealership',0,SYSDATE,'Enterprise Dealership Access |APPLICATION',SYSDATE,
SYSDATE,SYSDATE,1,'Enterprise Dealership','APPLICATION')
/
commit
/