--Purpose    : Patch TO ADD COLUMN IN LINE_ITEM_GROUP TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 13-DEC-2013

alter table Line_Item_group add
     (
      asked_qty_hrs VARCHAR2(255 CHAR),
      accepted_qty_hrs VARCHAR2(255 CHAR),
      grouptotal_statemandate_curr  VARCHAR2(255 CHAR),  
      grouptotal_statemandate_amt NUMBER(19,2)
     )
/