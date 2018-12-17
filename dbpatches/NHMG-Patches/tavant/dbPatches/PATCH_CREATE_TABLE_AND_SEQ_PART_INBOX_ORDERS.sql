--PURPOSE    : PATCH FOR CREATING table parts_inbox_types, Seq PARTS_INBOX_SEQ and inserting the default values
--AUTHOR     : ROHIT MEHROTRA
--CREATED ON : 20-FEB-13

create table parts_inbox_types (id number(19,2),inbox_name VARCHAR2(255 CHAR),priority number (19,2))
/
CREATE SEQUENCE "PARTS_INBOX_SEQ" MINVALUE 1000 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1000 NOCACHE NOORDER NOCYCLE
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Prepare Due Parts',0)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Due Parts',1)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Overdue Parts',2)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Shipment Generated',3)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Parts Shipped',4)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Rejected Parts',5)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Claimed Parts Receipt',6)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Required Parts Return',7)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Required Parts From Dealer',8)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'WPRA Generated For Parts',9)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Due Parts Receipt',10)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Due Parts Inspection',11)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Dealer Requested Part',12)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Shipment Generated For Dealer',13)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Dealer Requested Parts Shipped',14)
/
insert into parts_inbox_types values(PARTS_INBOX_SEQ.nextVal,'Third Party Due Parts',15)
/
commit
/