--PURPOSE    : PATCH to reset HUSMAN sequence, reset CLAIM_NUMBER_SEQ ,create CLAIM_NUMBER_TKTSA_SEQ ,CREATE DCAP_CLAIM_NUMBER_TKTSA_SEQ , UPDATE DCAP_CLAIM_NUMBER_SEQ
--AUTHOR     : Joseph Tharakan
--CREATED ON : 21-MAY-11
--IMPACT     : Claim number values reset

declare

v_exists NUMBER := 0;
v_hus_seq NUMBER := 0;
v_tktsa_seq NUMBER := 0;
v_claim_number_seq NUMBER := 0;
v_dcap_tktsa_seq NUMBER := 0;
v_dcap_claim_number_seq NUMBER := 0;

begin

---UPDATE CLAIM_NUMBER_HUSSMANN_SEQ ---

select (max(to_number(regexp_replace(claim_number,'[^[:digit:]]',''))) +1)
into v_hus_seq
from claim where business_unit_info like 'Hussmann'; 


select count(1) into v_exists from user_sequences where upper(sequence_name) = 'CLAIM_NUMBER_HUSSMANN_SEQ';
if v_exists > 0 then
  execute immediate 'DROP SEQUENCE CLAIM_NUMBER_HUSSMANN_SEQ';
end if;

execute immediate 'CREATE SEQUENCE CLAIM_NUMBER_HUSSMANN_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH ' || v_hus_seq || ' NOCACHE NOORDER NOCYCLE';

------CREATE CLAIM_NUMBER_TKTSA_SEQ ----

select (max(to_number(regexp_replace(claim_number,'[^[:digit:]]','')))+1)  
into v_tktsa_seq
from claim
where business_unit_info like 'Thermo King TSA'; 

select count(1) into v_exists from user_sequences where upper(sequence_name) = 'CLAIM_NUMBER_TKTSA_SEQ';
if v_exists > 0 then
  execute immediate 'DROP SEQUENCE CLAIM_NUMBER_TKTSA_SEQ';
end if;

execute immediate 'CREATE SEQUENCE CLAIM_NUMBER_TKTSA_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH ' || v_tktsa_seq || ' NOCACHE NOORDER NOCYCLE';

update claim_number_pattern set sequence_name = 'claim_number_tktsa_seq'
where business_unit_info = 'Thermo King TSA' and pattern_type = 'W';

-------UPDATE CLAIM_NUMBER_SEQ -----

select (max(to_number(regexp_replace(claim_number,'[^[:digit:]]','')))+1)  
into v_claim_number_seq
from claim 
where business_unit_info in 
(
'AIR',
'Transport Solutions ESA',
'TFM',
'Clubcar ESA'
);


select count(1) into v_exists from user_sequences where upper(sequence_name) = 'CLAIM_NUMBER_SEQ';
if v_exists > 0 then
  execute immediate 'DROP SEQUENCE CLAIM_NUMBER_SEQ';
end if;

execute immediate 'CREATE SEQUENCE CLAIM_NUMBER_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH ' || v_claim_number_seq || ' NOCACHE NOORDER NOCYCLE';

---- CREATE DCAP_CLAIM_NUMBER_TKTSA_SEQ ----

select (max(to_number(regexp_replace(claim_number,'[^[:digit:]]','')))+1) 
into v_dcap_tktsa_seq
from dcap_claim 
where business_unit_info like 'Thermo King TSA'; 


select count(1) into v_exists from user_sequences where upper(sequence_name) = 'DCAP_CLAIM_NUMBER_TKTSA_SEQ';
if v_exists > 0 then
  execute immediate 'DROP SEQUENCE DCAP_CLAIM_NUMBER_TKTSA_SEQ';
end if;

execute immediate 'CREATE SEQUENCE DCAP_CLAIM_NUMBER_TKTSA_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH ' || v_dcap_tktsa_seq || ' NOCACHE NOORDER NOCYCLE';

update claim_number_pattern set sequence_name = 'dcap_claim_number_tktsa_seq'
where business_unit_info = 'Thermo King TSA' and pattern_type = 'DCAP';

-------UPDATE DCAP_CLAIM_NUMBER_SEQ -----

select (max(to_number(regexp_replace(claim_number,'[^[:digit:]]','')))+1) 
into v_dcap_claim_number_seq
from dcap_claim 
where business_unit_info in 
(
'Transport Solutions ESA'
);

select count(1) into v_exists from user_sequences where upper(sequence_name) = 'DCAP_CLAIM_NUMBER_SEQ';
if v_exists > 0 then
  execute immediate 'DROP SEQUENCE DCAP_CLAIM_NUMBER_SEQ';
end if;

execute immediate 'CREATE SEQUENCE DCAP_CLAIM_NUMBER_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH ' || v_dcap_claim_number_seq || ' NOCACHE NOORDER NOCYCLE';

commit;

exception 
when others then
rollback;
dbms_output.put_line('Exception' || sqlerrm);
raise;
end;


