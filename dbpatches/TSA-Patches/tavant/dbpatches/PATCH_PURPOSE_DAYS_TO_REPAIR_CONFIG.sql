--Purpose    : Add new purpose for days to repair configuration  ( Thermoking TSA)
--Author     : Mayank Vikram
--Created On : 17-Mar-10


INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Extended days for repair limit',1,sysdate,'Thermoking TSA',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),'Thermo King TSA',1)
/
commit
/