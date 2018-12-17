--Purpose    : Patch for adding new roles for limited view 
--Author     : kuldeep.patil	
--Created On : 7-June-2010

INSERT INTO ROLE (ID,NAME,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,DISPLAY_NAME) 
VALUES (42,'receiverLimitedView',1,CURRENT_TIMESTAMP,'Part Receiver Limited View|Internal', CURRENT_TIMESTAMP,null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,1,'Part Receiver Limited View')
/
INSERT INTO ROLE (ID,NAME,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY, D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,DISPLAY_NAME) 
VALUES (43,'inspectorLimitedView',1,CURRENT_TIMESTAMP, 'Part Inspector Limited View|Internal', CURRENT_TIMESTAMP,null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,1,'Part Inspector Limited View')
/
INSERT INTO ROLE (ID,NAME,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY, D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,DISPLAY_NAME) 
VALUES (44,'partShipperLimitedView',1,CURRENT_TIMESTAMP, 'Part Shipper Limited View|Internal', CURRENT_TIMESTAMP,null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,1,'Part Shipper Limited View')
/
COMMIT
/