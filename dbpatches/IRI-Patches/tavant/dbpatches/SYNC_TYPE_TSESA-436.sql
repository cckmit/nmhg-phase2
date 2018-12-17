CREATE TABLE SYNC_TYPE
  (
    TYPE VARCHAR2(100) NOT NULL,
    D_CREATED_ON DATE,
    D_INTERNAL_COMMENTS VARCHAR2(255 CHAR),
    D_UPDATED_ON DATE,
    D_LAST_UPDATED_BY NUMBER(19,0),
    D_CREATED_TIME TIMESTAMP (6),
    D_UPDATED_TIME TIMESTAMP (6),
    D_ACTIVE NUMBER(1,0) DEFAULT 1,
    PRIMARY KEY ("TYPE"),
    CONSTRAINT "SYNC_TYPE_LST_UPDT_BY_FK" FOREIGN KEY ("D_LAST_UPDATED_BY") REFERENCES ORG_USER ("ID")
  )
  /


insert into SYNC_TYPE values('Customer',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('CreditNotification',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('TKTSA-Customer',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('FetchFoc',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('Claim',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('InstallBase',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('PostFoc',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('TKTSA-PartsInventory',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('Item',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('ExtWarrantyPurchaseNotification',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

insert into SYNC_TYPE values('CurrencyExchangeRate',sysdate,null,sysdate,null,sysdate,sysdate,1)
 /

commit
 /

create index BUSINESS_UNIT_IDX on SYNC_TRACKER(BUSINESS_UNIT_INFO)
/