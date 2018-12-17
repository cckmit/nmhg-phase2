--Purpose    : Added tables I18NCampaign_Text,I18NNon_Oem_Parts_Description,I18NAdditional_Attribute_Name,I18NModifier_Name --and created sequences I18N_MODIFIER_NAME_SEQ,ADDITIONAL_ATTRIBUTES_SEQ,ADDITIONAL_ATTRIBUTES_SEQ,I18N_NON_OEM_PARTS_SEQ
--Author     : rakesh.r
--Created On : 12-SEP-08

INSERT INTO ROLE(ID,NAME,VERSION) VALUES (
(select max(id)+1 from role),'inventoryFullView',0)
/
COMMIT
/