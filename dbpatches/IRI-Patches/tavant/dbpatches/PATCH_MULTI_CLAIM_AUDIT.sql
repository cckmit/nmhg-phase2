--Purpose : Audits created for multi claim maintenance not to be displayed on UI
--Author : raghuram.d
--Date : 17-Nov-09

alter table claim_audit add multi_claim_maintenance number(1,0) default 0
/