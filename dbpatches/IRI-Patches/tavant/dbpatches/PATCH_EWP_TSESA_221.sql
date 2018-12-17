--Purpose : TSESA-221 - To add attachments on Extended Warranty Purchase
--Author  : raghuram.d
--Date    : 01/Mar/2010

ALTER TABLE policy_definition ADD for_internal_users_only NUMBER(1,0)
/
ALTER TABLE policy_definition ADD attachment_mandatory NUMBER(1,0)
/
ALTER TABLE policy_definition ADD min_months_frm_delivery_ewp NUMBER(19,0)
/
CREATE TABLE policy_attachments (
  policy NUMBER(19,0) NOT NULL,
  attachments NUMBER(19,0) NOT NULL
)
/
update policy_definition set 
    for_internal_users_only=0,
    attachment_mandatory=0,
    min_months_frm_delivery_ewp=0
/
commit
/