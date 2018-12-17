--Puppose : To setup different claim number sequences for different BUs
--Author  : raghuram.d
--Date    : 02/Sep/2009

ALTER TABLE claim_number_pattern ADD sequence_name VARCHAR2(255) DEFAULT 'claim_number_seq'
/
declare
  start_id number;
begin
  select max( common_utils.get_delimited_value(claim_number,'-',2) )+1 into start_id
  from claim where claim_number like 'HUS%'
  and length(claim_number)=11;

  execute immediate 'CREATE SEQUENCE claim_number_hussmann_seq' ||
        ' MINVALUE 1 MAXVALUE 999999999999999999999999999' ||
        ' START WITH ' || start_id ||
        ' INCREMENT BY 1 NOCACHE';
end;
/
UPDATE claim_number_pattern SET sequence_name='claim_number_hussmann_seq' 
WHERE business_unit_info='Hussmann' AND pattern_type != 'DCAP'
/
UPDATE claim_number_pattern SET sequence_name='dcap_claim_number_seq' 
WHERE pattern_type = 'DCAP'
/
COMMIT
/

