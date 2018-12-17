--Purpose    : Add new purpose for days to file the claim configuration for all BUs, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 12-Oct-2010


/* INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Extended days for days to file limit',1,sysdate,'Thermoking TSA',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),'Thermo King TSA',1)
/ */
INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Extended days for days to file limit',1,sysdate,'4.3 Upgrade-Migration',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),
'Transport Solutions ESA',1)
/
INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Extended days for days to file limit',1,sysdate,'4.3 Upgrade-Migration',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),
'Hussmann',1)
/
INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Extended days for days to file limit',1,sysdate,'4.3 Upgrade-Migration',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),
'TFM',1)
/
INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Extended days for days to file limit',1,sysdate,'4.3 Upgrade-Migration',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),
'AIR',1)
/
INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Extended days for days to file limit',1,sysdate,'4.3 Upgrade-Migration',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),
'Clubcar ESA',1)
/
commit
/