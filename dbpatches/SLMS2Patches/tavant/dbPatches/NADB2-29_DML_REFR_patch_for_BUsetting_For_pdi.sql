--PURPOSE    : patch_for_BUsetting_For_pdi
--AUTHOR     : Raghavendra
--CREATED ON : 11-JAN-14


insert into bu_settings values(BU_SETTINGS_SEQ.nextVal,'DR.print.pdi.AMER','true',1,sysdate,'PDI for AMER',sysdate,56,sysdate,sysdate,1,'AMER')
/
insert into bu_settings values(BU_SETTINGS_SEQ.nextVal,'DR.print.pdi.EMEA','false',1,sysdate,'PDI for EMEA',sysdate,56,sysdate,sysdate,1,'EMEA')
/
commit
/