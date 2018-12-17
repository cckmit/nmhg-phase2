--Purpose    : Scripts for creating SYNC_TYPE table, changes made as a part of request sent by Single Instance DEV Team
--Created On : 16-Mar-2011
--Created By : Kuldeep Patil
--Impact     : None

CREATE TABLE SYNC_TYPE
(
	TYPE VARCHAR2(100 BYTE) PRIMARY KEY,
	D_CREATED_ON DATE,
	D_INTERNAL_COMMENTS VARCHAR2(255 CHAR),
	D_UPDATED_ON DATE,
	D_LAST_UPDATED_BY NUMBER(19,0),
	D_CREATED_TIME TIMESTAMP (6),
	D_UPDATED_TIME TIMESTAMP (6),
	D_ACTIVE NUMBER(1,0) DEFAULT 1
)
/
alter table SYNC_TYPE add CONSTRAINT SYNC_TYPE_LST_UPDT_BY_FK FOREIGN KEY ("D_LAST_UPDATED_BY") REFERENCES ORG_USER ("ID")
/
commit
/
insert into SYNC_TYPE values('Customer',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('WarrantyClaimCreditNotification',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('Claim',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('InstallBase',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('Item',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

commit
 /