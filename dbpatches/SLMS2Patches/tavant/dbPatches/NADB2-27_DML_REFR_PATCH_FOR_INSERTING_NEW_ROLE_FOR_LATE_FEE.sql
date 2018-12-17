--Purpose    : DML for NMHGSLMS-425 -adding warrantySupervisor role for Late fee approval.
--Author     : Arpitha Nadig AR
--Created On : 10-JAN-2013
INSERT INTO ROLE VALUES(ROLE_SEQ.nextval,'warrantySupervisor',1,sysdate,'Warranty Supervisor role',sysdate,null,current_timestamp,current_timestamp,1,'Warranty Supervisor','INTERNAL','Warranty Supervisor')
/