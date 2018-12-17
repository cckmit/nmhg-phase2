--Purpose    : DDL for NMHGSLMS-425 -Adding deductibles for products.
--Author     : Arpitha Nadig AR
--Created On : 05-JAN-2013
--------------------------------------------------------
--  DDL for Table POLICY_PRODUCT_MAPPING
--------------------------------------------------------
CREATE TABLE "POLICY_PRODUCT_MAPPING"
(	"ID" NUMBER(19,0) NOT NULL ENABLE, 
    "DEDUCTIBLE" NUMBER(19,2),
    "PRODUCT" NUMBER(19,0),
    "VERSION" NUMBER(10,0) NOT NULL ENABLE,
    "D_CREATED_ON" DATE,
    "D_INTERNAL_COMMENTS" VARCHAR2(255 CHAR),
    "D_UPDATED_ON" DATE,
    "D_LAST_UPDATED_BY" NUMBER(19,0),
    "D_CREATED_TIME" TIMESTAMP (6),
    "D_UPDATED_TIME" TIMESTAMP (6),
    "D_ACTIVE" NUMBER(1,0) DEFAULT 1,
    "POLICY_DEFN" NUMBER(19,0),
    CONSTRAINT "POLICY_PROD_MAPPING_PK" PRIMARY KEY ("ID")
   )
/