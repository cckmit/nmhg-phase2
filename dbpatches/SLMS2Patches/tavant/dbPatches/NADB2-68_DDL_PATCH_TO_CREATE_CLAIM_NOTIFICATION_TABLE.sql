--PURPOSE    : patch to create claim notification table
--AUTHOR     : Raghu
--CREATED ON : 10-FEB-2014

create table claim_audit_notifications (claim_audit NUMBER(19),notifications varchar2(2000))
/