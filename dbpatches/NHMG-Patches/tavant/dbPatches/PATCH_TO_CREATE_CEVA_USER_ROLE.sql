-- PURPOSE: TO CREATE A CEVA USER ROLE
--AUTHOR     : DEEPAK
--CREATED ON : 25-may-13

insert into role values(ROLE_SEQ.nextval, 'cevaProcessor', 1, sysdate, 'ceva role', sysdate, null, sysdate, sysdate, 1 , 'CEVA Processor','EXTERNAL','CEVA Processor')
/
commit
/