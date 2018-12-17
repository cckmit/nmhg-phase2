--create new table without prev_claim_snapshot_string column
CREATE TABLE CLAIM_AUDIT_NEW AS SELECT ID, EXTERNAL_COMMENTS, INTERNAL,INTERNAL_COMMENTS,PREVIOUS_STATE,UPDATED_ON,VERSION,FOR_CLAIM,UPDATED_BY,LIST_INDEX,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,UPDATED_TIME,DECISION,MULTI_CLAIM_MAINTENANCE,PAYMENT from claim_audit
/
--add new column to table
alter table CLAIM_AUDIT_NEW add (CLAIM_SNAPSHOT number (19,0))
/

create unique index TEMP_CLAIM_AUDIT_NEW_IDX on CLAIM_AUDIT_NEW(ID)
/

--populate the referance data to new column
update CLAIM_AUDIT_NEW set CLAIM_SNAPSHOT = ID where id in (select ID from CLAIM_AUDIT where prev_claim_snapshot_string is not null)
/

commit
/

drop index TEMP_CLAIM_AUDIT_NEW_IDX
/

--rename old claim_audit table
alter table CLAIM_AUDIT rename to CLAIM_SNAPSHOT_XML
/
--rename new claim audit table
alter table CLAIM_AUDIT_NEW rename to CLAIM_AUDIT
/

--drop all the unwanted columns from the old claim audit table
ALTER TABLE CLAIM_SNAPSHOT_XML DROP (EXTERNAL_COMMENTS,INTERNAL,INTERNAL_COMMENTS,PREVIOUS_STATE,UPDATED_ON,VERSION,FOR_CLAIM,UPDATED_BY,LIST_INDEX,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,UPDATED_TIME,DECISION,MULTI_CLAIM_MAINTENANCE,PAYMENT)
/

-- rename the pk constraint on claim_snapshot_xml column
alter table CLAIM_SNAPSHOT_XML drop constraint CLAIM_AUDIT_PK
/


-- drop the associated index as well
drop index CLAIM_AUDIT_PK
/

--add new pk constrain to id column of claim_snapshot_xml table
alter table CLAIM_SNAPSHOT_XML add constraint CLAIM_SNAPSHOT_XML_PK PRIMARY KEY(ID)
/

--delete rows where prev_claim_snapshot_string = null
delete from CLAIM_SNAPSHOT_XML where prev_claim_snapshot_string is null
/
commit
/

--Enable constraints on the new table
alter table claim_audit add constraint CLAIM_SNAPSHOT_XML_FK FOREIGN KEY(CLAIM_SNAPSHOT) REFERENCES CLAIM_SNAPSHOT_XML(ID)
/
alter table claim_audit add constraint CLAIMAUDIT_FORCLAIM_FK FOREIGN KEY(FOR_CLAIM) REFERENCES CLAIM(ID)
/
alter table claim_audit add constraint CLAIMAUDIT_UPDATEDBY_FK FOREIGN KEY(UPDATED_BY) REFERENCES ORG_USER(ID)
/
alter table claim_audit add constraint CLAIM_AUDIT_PAYMENT_FK1 FOREIGN KEY(PAYMENT) REFERENCES PAYMENT(ID)
/
alter table claim_audit add constraint CLM_AUDIT_LST_UPDT_BY_FK FOREIGN KEY(D_LAST_UPDATED_BY) REFERENCES ORG_USER(ID)
/
alter table claim_audit add constraint CLAIM_AUDIT_PK PRIMARY KEY(ID)
/

--create the necessary indexes
create index CLAIM_AUDIT_I2 on CLAIM_AUDIT(FOR_CLAIM, PREVIOUS_STATE)
/
create index CLAIMAUDIT_FORCLAIM_IX on CLAIM_AUDIT(FOR_CLAIM)
/
create index CLAIMAUDIT_UPDATEDBY_IX on CLAIM_AUDIT(UPDATED_BY)
/
create index CLAIM_AUDIT_UPDATED_TIME_IDX on CLAIM_AUDIT(UPDATED_TIME)
/
create unique index CLAIM_AUDIT_SNAP_SHOT_IDX on CLAIM_AUDIT(CLAIM_SNAPSHOT)
/

--create sequence with max value as start
DECLARE
startNum number;
temp number;
BEGIN
select (max(id) + 1) into startNum from claim_audit;
temp := mod(startNum,100);
temp := (100 - temp);
startNum := startNum + temp; -- nearest 100th value :)
EXECUTE immediate 'CREATE SEQUENCE CLAIM_SNAPSHOT_XML_SEQ INCREMENT BY 20 START WITH ' || startNum || ' NOCACHE NOCYCLE' ;
END;
/