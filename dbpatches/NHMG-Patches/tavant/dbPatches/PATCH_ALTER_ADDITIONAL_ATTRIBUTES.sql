--PURPOSE    : PATCH TO ADD COLUMN SMR_Reason
--AUTHOR     : SUSHMA MANTHALE
--CREATED ON : 02-JULY-12

alter table attribute_association add SMR_Reason NUMBER(19,0)
/
ALTER TABLE attribute_association add CONSTRAINT "SMR_REASON_FK" FOREIGN KEY ("SMR_REASON") REFERENCES "LIST_OF_VALUES" ("ID") ENABLE
/