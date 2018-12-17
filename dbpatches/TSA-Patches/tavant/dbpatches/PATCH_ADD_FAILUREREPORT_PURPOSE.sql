--Purpose    : Add new purpose for failure reports ( Thermoking TSA)
--Created On : 09-MAr-2010
--Created By : Amritha Krishnamoorthy


INSERT INTO purpose values(PURPOSE_SEQ.nextval,'Failure Reports',1,sysdate,'Thermoking TSA',sysdate,null,cast(sysdate as TIMESTAMP),cast(sysdate as TIMESTAMP),
'Thermo King TSA',1)
/
commit
/