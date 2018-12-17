--Purpose    : Patch for Cost Categories dependent on unit�s product as a part of 4.3 upgrade 
--Author     : Mayank Vikram
--Created On : 29-MAR-10


CREATE TABLE COSTCAT_APPL_PRODUCTS
	 (
	    COST_CATEGORY NUMBER(19,0) NOT NULL ENABLE,
	    ITEM_GROUP   NUMBER(19,0) NOT NULL ENABLE,    
	    CONSTRAINT COSTCAT_APPL_PRD_COSTCAR_FK FOREIGN KEY (COST_CATEGORY) REFERENCES COST_CATEGORY (ID) ENABLE,    
	    CONSTRAINT COSTCAT_APPL_PRD_GRP_FK FOREIGN KEY (ITEM_GROUP) REFERENCES ITEM_GROUP (ID) ENABLE
	)
/
INSERT INTO COSTCAT_APPL_PRODUCTS (COST_CATEGORY, ITEM_GROUP)
(
SELECT CC.ID as ccid, IG.ID as igid
FROM CONFIG_PARAM CP, CONFIG_VALUE CV, ITEM_GROUP IG, COST_CATEGORY CC, ITEM_SCHEME ISC
WHERE CP.NAME = 'configuredCostCategories'
AND CP.ID = CV.CONFIG_PARAM
AND CV.ACTIVE = 1
AND CV.VALUE = CC.ID
AND CV.BUSINESS_UNIT_INFO = IG.BUSINESS_UNIT_INFO
AND IG.ITEM_GROUP_TYPE = 'PRODUCT'
AND IG.SCHEME = ISC.ID
AND ISC.NAME = 'Prod Struct Scheme'
)
/
COMMIT
/