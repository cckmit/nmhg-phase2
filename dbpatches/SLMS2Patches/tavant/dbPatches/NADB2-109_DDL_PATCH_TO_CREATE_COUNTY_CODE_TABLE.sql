--PURPOSE    : patch to create table county_code_mapping
--AUTHOR     : Raghu
--CREATED ON : 28-MAR-2014

create table county_code_mapping 
(id number(19),
state VARCHAR2(255),
county_code varchar2(255),
county_name varchar2(255),
D_CREATED_ON DATE,
D_INTERNAL_COMMENTS VARCHAR2(255),
country varchar2(255),  
D_UPDATED_ON DATE, 
D_LAST_UPDATED_BY NUMBER(19), 
D_CREATED_TIME  TIMESTAMP , 
D_UPDATED_TIME  TIMESTAMP , 
D_ACTIVE  NUMBER(1) DEFAULT 1)
/
alter table county_code_mapping add constraint county_code_mapping_pk primary key (id)
/
alter table county_code_mapping add unique (county_name)
/