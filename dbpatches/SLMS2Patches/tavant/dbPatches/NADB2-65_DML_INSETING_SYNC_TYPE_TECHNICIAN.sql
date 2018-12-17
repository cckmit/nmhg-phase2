--Purpose    : Patch inserting sync type for techncican
--Author     : Kalyani
--Created On : 06-Feb-2013
Insert into sync_type (TYPE,D_CREATED_ON,D_INTERNAL_COMMENTS,
D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('Technician',sysdate,null,sysdate,null,systimestamp,systimestamp,1)
/
commit
/