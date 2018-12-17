--  REFERENCE   : SLMSPOD-970 
--	AUTHOR		: Ravi K Sinha

ALTER TABLE CLAIM ADD ("INV_CLASS_30_DAY_NCR" NUMBER(19,0))
/

ALTER TABLE CLAIM ADD CONSTRAINT "CLAIM_INV_CLS_30_DAY_NCR_FK" FOREIGN KEY ("INV_CLASS_30_DAY_NCR") REFERENCES "INVENTORY_CLASS" ("ID")
/

COMMIT
/