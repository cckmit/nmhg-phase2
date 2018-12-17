--Purpose    : relationship between shipment and oempartreplaced is not ManyToMany.Hence mapping table required
--Created On : 07-APR-2010
--Created By : Sudaksh Chohan

--CREATE TABLE SHIPMENT_SUPPLIER_PARTS 
--(
--SHIPMENT NUMBER(19,0) ,
--SUPPLIER_PARTS NUMBER (19,0)
--)
--/
--ALTER TABLE SHIPMENT_SUPPLIER_PARTS ADD (
--	CONSTRAINT FK_SHIP_SUP_PARTS_OEMPART_REP
--	FOREIGN KEY (SUPPLIER_PARTS) 
--	REFERENCES OEM_PART_REPLACED(ID)
--)
--/
--ALTER TABLE SHIPMENT_SUPPLIER_PARTS ADD (
--	CONSTRAINT FK_SHIP_SUP_PARTS_SHIPMENT
--	FOREIGN KEY (SHIPMENT) 
--	REFERENCES SHIPMENT(ID)
--)
--/
---- Kuldeep - This DB patch is not required as this table is being dropped in DB Patch PATCH_SHIPMENT_SUPPLIER_PARTS_DROPPED.sql 
commit
/