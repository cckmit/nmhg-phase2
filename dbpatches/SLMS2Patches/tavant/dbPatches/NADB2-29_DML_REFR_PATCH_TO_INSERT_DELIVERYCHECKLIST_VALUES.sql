--PURPOSE    : PATCH_TO_insert_values_deliverychecklist
--AUTHOR     : Raghavendra
--CREATED ON : 11-JAN-14


insert into delivery_check_list values(hibernate_sequence.nextval,upper('Capacity Limitations'))
/
insert into delivery_check_list values(hibernate_sequence.nextval, upper('Operator Safety Rules'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('Nameplate'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('Location and use of instruments &' || ' controls'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('Operator Procedures and techniques'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('Operator restriant System'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('periodic maintenance requirements'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('operation and maintenance of attachement'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('parts ordering procedures'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('dealer''s support services'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('operating manual delivered'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('keys delivered'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('Sio package (if applicable)'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('Guide for users of industrial lift trucks'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('warranty policy'))
/
insert into delivery_check_list values(hibernate_sequence.nextval,upper('tether line &' || ' Harness Delivered (IF Applicable)'))
/
commit
/