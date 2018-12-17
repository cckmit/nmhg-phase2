--PURPOSE: TO CREATE A NCR USER ROLE
--AUTHOR     : AJIT
--CREATED ON : 15-july-13

insert into role values(ROLE_SEQ.nextval, 'ncrProcessor', 1, sysdate, 'NCR Processor role', sysdate, null, sysdate, sysdate, 1 , 'NCR Processor','INTERNAL','NCR Processor')
/
insert into role values(ROLE_SEQ.nextval, 'ncrAdvisor', 1, sysdate, 'NCR Advisor role', sysdate, null, sysdate, sysdate, 1 , 'NCR Advisor','INTERNAL','NCR Advisor')
/
commit
/