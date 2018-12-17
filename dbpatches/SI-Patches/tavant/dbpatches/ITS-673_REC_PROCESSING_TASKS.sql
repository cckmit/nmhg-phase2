--Purpose    : created tasks to initiate recovery and send claims to supplier
--Created On : 17/Mar/2012
--Created By : raghuram.d

create table STG_INIT_RECOVERY (
  claim_number varchar2(255),
  is_causal_part varchar2(1),
  part_number varchar2(255),
  contract_id number,
  comments varchar2(4000),
  contract_name varchar2(255),
  upload_status varchar2(1),
  upload_message varchar2(4000)
)
/
create table STG_RECOVERY_PROCESSING (
  rec_claim_number varchar2(255),
  rec_processor varchar2(255),
  comments varchar2(4000),
  upload_status varchar2(1),
  upload_message varchar2(4000)
)
/