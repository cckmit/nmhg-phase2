--Purpose : Allow draft claim upload with multiple job codes
--Author : raghuram.d
--Date : 10/Sep/2009

BEGIN
    create_upload_error('draftWarrantyClaims','en_US','LABOUR HOURS','DC067','Invalid Labour Hours');
END;
/
delete from stg_draft_claim
/
commit
/
alter table stg_draft_claim modify labour_hours varchar2(255)
/