--Purpose    : Patch for adding new roles for limited view 
--Author     : Surendra Varma	
--Created On : 08-August-2011

INSERT INTO ROLE (ID,NAME,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,DISPLAY_NAME) 
VALUES ((select max(id) + 1 from role),'readOnlyDealer',1,CURRENT_TIMESTAMP,'Read Only Role', CURRENT_TIMESTAMP,null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,1,'Read Only View')
/
COMMIT
/