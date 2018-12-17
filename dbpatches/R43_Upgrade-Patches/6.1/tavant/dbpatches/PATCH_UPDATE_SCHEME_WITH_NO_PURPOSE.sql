--PURPOSE    : PATCH FOR UPDATING SCHEMES WHICH DO NOT HAVE PURPOSE
--AUTHOR     : ROHIT MEHROTRA
--CREATED ON : 29-APR-11
--IMPACT     : MANAGE GROUPS

update item_scheme set d_active=0 where id not in (select item_scheme from item_scheme_purposes)
/
commit
/
update user_scheme set d_active=0 where id not in (select user_scheme from user_scheme_purposes)
/
commit
/
update dealer_scheme set d_active=0 where id not in (select dealer_scheme from dealer_scheme_purposes)
/
commit
/