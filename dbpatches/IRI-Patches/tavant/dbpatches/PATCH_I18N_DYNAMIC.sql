--Purpose    : Added tables I18NCampaign_Text,I18NNon_Oem_Parts_Description,I18NAdditional_Attribute_Name,I18NModifier_Name --and created sequences I18N_MODIFIER_NAME_SEQ,ADDITIONAL_ATTRIBUTES_SEQ,ADDITIONAL_ATTRIBUTES_SEQ,I18N_NON_OEM_PARTS_SEQ
--Author     : rakesh.r
--Created On : 22-Aug-08

CREATE TABLE i18nadditional_attribute_name (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), name VARCHAR2(255 CHAR), additional_attributes_name NUMBER(19,0) NOT NULL)
/
ALTER TABLE i18nadditional_attribute_name  ADD CONSTRAINT i18naddn_attr_name_PK	PRIMARY KEY( 	ID	)
/
CREATE TABLE i18ncampaign_text (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), description VARCHAR2(255 CHAR), campaign_description NUMBER(19,0) NOT NULL)
/
ALTER TABLE i18ncampaign_text  ADD CONSTRAINT i18ncampaign_text_PK	PRIMARY KEY( 	ID	)
/
CREATE TABLE i18nmodifier_name (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), name VARCHAR2(255 CHAR), modifier_name NUMBER(19,0) NOT NULL)
/
ALTER TABLE i18nmodifier_name ADD CONSTRAINT i18nmodifier_name_PK	PRIMARY KEY( 	ID	)
/
CREATE TABLE i18nnon_oem_parts_description (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), description VARCHAR2(255 CHAR), non_oem_parts_description NUMBER(19,0) NOT NULL)
/
ALTER TABLE i18nnon_oem_parts_description ADD CONSTRAINT  i18nnon_oem_parts_desc_PK	PRIMARY KEY( 	ID	)
/
ALTER TABLE i18nadditional_attribute_name ADD CONSTRAINT i18naddn_attr_name_name_FK FOREIGN KEY (additional_attributes_name) REFERENCES additional_attributes
/
ALTER TABLE i18ncampaign_text ADD CONSTRAINT i18ncamp_text_camp_desc_FK FOREIGN KEY (campaign_description) REFERENCES campaign
/
ALTER TABLE i18nmodifier_name ADD CONSTRAINT i18nmodifier_name_mod_name FOREIGN KEY (modifier_name) REFERENCES payment_variable
/
ALTER TABLE i18nnon_oem_parts_description ADD CONSTRAINT i18nnon_oem_parts_desc_desc FOREIGN KEY (non_oem_parts_description) REFERENCES non_oem_part_to_replace
/
CREATE SEQUENCE I18N_ADDITIONAL_ATTRIBUTE_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE SEQUENCE I18N_Campaign_Text_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE SEQUENCE I18N_MODIFIER_NAME_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE SEQUENCE I18N_NON_OEM_PARTS_SEQ START WITH 1000 INCREMENT BY 20
/
ALTER TABLE CAMPAIGN MODIFY(DESCRIPTION NULL)
/