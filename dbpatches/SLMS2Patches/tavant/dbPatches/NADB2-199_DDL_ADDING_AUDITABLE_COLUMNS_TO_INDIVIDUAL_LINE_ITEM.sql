--Purpose    : Patch TO ADD AUDITABLE COLUMNS TO INDIVIDUAL_LINE_ITEM TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 22-SEP-2014

alter table individual_line_item 
ADD
(
D_ACTIVE                              NUMBER(1),
D_CREATED_ON                          DATE,
D_CREATED_TIME                        TIMESTAMP(6),       
D_INTERNAL_COMMENTS                   VARCHAR2(255 CHAR), 
D_UPDATED_ON                          DATE,               
D_UPDATED_TIME                        TIMESTAMP(6),       
D_LAST_UPDATED_BY                     NUMBER(19)
)
/