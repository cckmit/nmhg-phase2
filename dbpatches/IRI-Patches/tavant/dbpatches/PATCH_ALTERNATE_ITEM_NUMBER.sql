--Purpose    : Alter Item Table to add column alternate_item_number for storing Segment1#Segment2 for IRI
--Author     : Jitesh Jain
--Created On : 27-Dec-08


ALTER TABLE ITEM 
ADD (ALTERNATE_ITEM_NUMBER VARCHAR2(255 BYTE))
/