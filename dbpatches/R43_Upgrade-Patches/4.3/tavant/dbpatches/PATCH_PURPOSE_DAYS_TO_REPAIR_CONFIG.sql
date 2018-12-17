--Purpose    : Add new purpose for days to repair configuration for all BUs, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 11-Oct-2010


/* INSERT INTO purpose values(PURPOSE_SEQ.NEXTVAL,'Extended days for repair limit',1,SYSDATE,'Thermoking TSA',SYSDATE,null,cast(SYSDATE as TIMESTAMP),cast(SYSDATE as TIMESTAMP),'Thermo King TSA',1)
/ */
INSERT INTO PURPOSE VALUES(PURPOSE_SEQ.NEXTVAL,'Extended days for repair limit',1,SYSDATE,'4.3 Upgrade-Migration',SYSDATE,NULL,CAST(SYSDATE AS TIMESTAMP),CAST(SYSDATE AS TIMESTAMP),
'Transport Solutions ESA',1)
/
INSERT INTO PURPOSE VALUES(PURPOSE_SEQ.NEXTVAL,'Extended days for repair limit',1,SYSDATE,'4.3 Upgrade-Migration',SYSDATE,NULL,CAST(SYSDATE AS TIMESTAMP),CAST(SYSDATE AS TIMESTAMP),
'Hussmann',1)
/
INSERT INTO PURPOSE VALUES(PURPOSE_SEQ.NEXTVAL,'Extended days for repair limit',1,SYSDATE,'4.3 Upgrade-Migration',SYSDATE,NULL,CAST(SYSDATE AS TIMESTAMP),CAST(SYSDATE AS TIMESTAMP),
'TFM',1)
/
INSERT INTO PURPOSE VALUES(PURPOSE_SEQ.NEXTVAL,'Extended days for repair limit',1,SYSDATE,'4.3 Upgrade-Migration',SYSDATE,NULL,CAST(SYSDATE AS TIMESTAMP),CAST(SYSDATE AS TIMESTAMP),
'AIR',1)
/
INSERT INTO PURPOSE VALUES(PURPOSE_SEQ.NEXTVAL,'Extended days for repair limit',1,SYSDATE,'4.3 Upgrade-Migration',SYSDATE,NULL,CAST(SYSDATE AS TIMESTAMP),CAST(SYSDATE AS TIMESTAMP),
'Clubcar ESA',1)
/
commit
/