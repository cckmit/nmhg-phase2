alter table campaign_serial_range add (ATTACH_OR_DELETE varchar2(25))
/
alter table campaign_coverage add (range_coverage NUMBER(19,0))
/
alter table campaign_coverage add  (SERIAL_NUMBER_COVERAGE number(19,0))
/
alter table campaign_coverage add CONSTRAINT CAMP_RANGE_COVERAGE Foreign Key (range_coverage) references campaign_range_coverage(ID)
/
alter table campaign_coverage modify (constraint CAMP_SER_NUM_COVERAGE foreign key (SERIAL_NUMBER_COVERAGE) references CAMPAIGN_SNO_COVERAGE)
/
create table campaign_sno_coverage_items (campaign_sno_coverage NUMBER(19,0),items NUMBER(19,0))
/
create table campaign_range_coverage_items (campaign_range_coverage NUMBER(19,0),items NUMBER(19,0))
/
commit
/