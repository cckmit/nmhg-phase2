--Purpose    : Patch TO ADD COLUMN IN LINE_ITEM TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 14-JAN-2014

alter table Line_Item add
  (	  
   smandate_modifier_percent NUMBER(19,6)
  )
/