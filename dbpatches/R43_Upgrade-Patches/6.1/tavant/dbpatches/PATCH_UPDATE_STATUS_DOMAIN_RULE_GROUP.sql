--PURPOSE    : PATCH FOR UPDATING COL STATUS OF TABLE DOMAIN_RULE_GROUP
--AUTHOR     : ROHIT MEHROTRA
--CREATED ON : 28-APR-11
--Impact     : Business Rule Group Listing

update domain_rule_group set status='ACTIVE' where name='Claim Processing Rules' and business_unit_info='Hussmann' and status is null
/
commit
/