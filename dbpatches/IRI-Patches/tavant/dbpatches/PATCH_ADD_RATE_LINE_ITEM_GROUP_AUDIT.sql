--Purpose    : added Money field in line item group audit to capture travel and Labor rate
--Author     : jitesh jain
--Created On : 08-Jun-09

alter table LINE_ITEM_GROUP_AUDIT add(rate_amount NUMBER(19, 2))
/
alter table LINE_ITEM_GROUP_AUDIT add(rate_curr VARCHAR2(255))
/
commit
/