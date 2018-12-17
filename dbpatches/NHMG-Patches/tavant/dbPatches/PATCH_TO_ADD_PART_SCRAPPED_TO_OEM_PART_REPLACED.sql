--PURPOSE    : PATCH TO ALTER OEM_PART_REPLACED TABLE
--AUTHOR     : Deepak Patel
--CREATED ON : 21-Mar-12


ALTER TABLE oem_part_replaced ADD part_scrapped NUMBER(1,0) DEFAULT 0
/
ALTER TABLE oem_part_replaced MODIFY (part_scrapped NUMBER(1,0) CONSTRAINT C_partscrapped_notnull NOT NULL)
/
ALTER TABLE oem_part_replaced ADD (SCRAP_DATE DATE)
/
																		