--PURPOSE: TO CREATE INDIVIDUAL LINE ITEM TABLE
--AUTHOR     : AJIT
--CREATED ON : 13-Dec-13

create table Individual_line_item 
   (
   id number(19) not null,
   item  VARCHAR2(255 CHAR),
   description VARCHAR2(255 CHAR),
   base_curr  VARCHAR2(255 CHAR),  
   base_amt NUMBER(19,2),
   state_mandate_curr  VARCHAR2(255 CHAR),  
   state_mandate_amt NUMBER(19,2),  
   accepted_curr  VARCHAR2(255 CHAR),  
   accepted_amt NUMBER(19,2),    
   PERCENTAGE_ACCEPTANCE NUMBER(19,6),
   asked_hrs NUMBER(19,6),
   accepted_hrs NUMBER(19,6),
   asked_qty NUMBER(10),
   accepted_qty NUMBER(10),
   line_item_group number(19) ,
   PRIMARY KEY (id), 
   FOREIGN KEY  (line_item_group) REFERENCES line_item_group(ID) 
   )
/
create index Individual_line_item_item on Individual_line_item ( item)
/
create index Individual_line_item_desc on Individual_line_item ( description)
/