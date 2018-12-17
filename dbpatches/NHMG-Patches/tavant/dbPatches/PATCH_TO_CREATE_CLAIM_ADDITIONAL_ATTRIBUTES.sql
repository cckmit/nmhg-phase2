--PURPOSE    : PATCH TO CREATE CLAIM_ADDITIONAL_ATTRIBUTES
--AUTHOR     : SUSHMA
--CREATED ON : 10-JULY-12

CREATE TABLE CLAIM_ADDITIONAL_ATTRIBUTES
  (
    CLAIM                                    NUMBER(19,0) NOT NULL ENABLE,
    CLAIM_ADDITIONAL_ATTRIBUTES              NUMBER(19,0) NOT NULL ENABLE,
    CONSTRAINT "CLMATTRIBUTE_CLMATT_FK" FOREIGN KEY ("CLAIM_ADDITIONAL_ATTRIBUTES") REFERENCES CLAIM_ATTRIBUTES ("ID") ENABLE,
    CONSTRAINT "CLMATTR_FK" FOREIGN KEY ("CLAIM") REFERENCES CLAIM ("ID") ENABLE
  )
/