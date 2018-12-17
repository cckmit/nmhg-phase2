--Purpose    : Patch for Adding BU column in model category,changes made as a part of 4.3 upgrade
--Author     : saya.sudha	
--Created On : 08-jan-2010

ALTER TABLE MODEL_CATEGORY ADD (business_unit_info VARCHAR2(255 BYTE))
/
ALTER TABLE MODEL_CATEGORY ADD 
(CONSTRAINT MODEL_CATEGORY_BU_FK FOREIGN KEY (BUSINESS_UNIT_INFO) REFERENCES BUSINESS_UNIT (NAME))
/
UPDATE MODEL_CATEGORY SET BUSINESS_UNIT_INFO='Transport Solutions ESA'
/
COMMIT
/