--Purpose    : Updated tables I18NCampaign_Text,I18NNon_Oem_Parts_Description,I18NAdditional_Attribute_Name,I18NModifier_Name and created sequences I18N_MODIFIER_NAME_SEQ,ADDITIONAL_ATTRIBUTES_SEQ,ADDITIONAL_ATTRIBUTES_SEQ,I18N_NON_OEM_PARTS_SEQ
--Author     : rakesh.r
--Created On : 15-SEP-08


CREATE OR REPLACE PROCEDURE Additional_Attributes_i18 AS
   CURSOR user_rec IS
      SELECT *
      FROM  Additional_Attributes;
BEGIN
   FOR each_rec IN user_rec LOOP
      IF each_rec.NAME is not null or each_rec.NAME <> '' THEN
	   BEGIN
         INSERT INTO i18NAdditional_Attribute_name
                     (ID, LOCALE,NAME,ADDITIONAL_ATTRIBUTES_NAME
                     )
              VALUES (I18N_ADDITIONAL_ATTRIBUTE_SEQ.nextval,'en_US', each_rec.NAME,each_rec.id
                     );
		END;
	  END IF;     
   END LOOP;
   COMMIT;
END;
/
BEGIN
Additional_Attributes_i18();
END;
/
CREATE OR REPLACE PROCEDURE Campaign_i18_populate AS
   CURSOR user_rec IS
      SELECT *
      FROM  Campaign;
BEGIN
   FOR each_rec IN user_rec LOOP
      IF each_rec.DESCRIPTION is not null or each_rec.DESCRIPTION <> '' THEN
	   BEGIN
         INSERT INTO i18nCampaign_text
                     (ID, LOCALE,DESCRIPTION,CAMPAIGN_DESCRIPTION
                     )
              VALUES (I18N_Campaign_Text_SEQ.nextval,'en_US', each_rec.DESCRIPTION,each_rec.id
                     );
		END;
	  END IF;     
   END LOOP;
   COMMIT;
END;
/
BEGIN
Campaign_i18_populate();
END;
/
CREATE OR REPLACE PROCEDURE Payment_Variable_i18_populate AS
   CURSOR user_rec IS
      SELECT *
      FROM  Payment_Variable;
BEGIN
   FOR each_rec IN user_rec LOOP
      IF each_rec.NAME is not null or each_rec.NAME <> '' THEN
	   BEGIN
         INSERT INTO i18NMODIFIER_NAME
                     (ID, LOCALE,NAME,MODIFIER_NAME
                     )
              VALUES (I18N_MODIFIER_NAME_SEQ.nextval,'en_US', each_rec.NAME,each_rec.id
                     );
		END;
	  END IF;     
   END LOOP;
   COMMIT;
END;
/
BEGIN
Payment_Variable_i18_populate ();
END;
/
CREATE OR REPLACE PROCEDURE Non_OEM_Part_18_populate AS
   CURSOR user_rec IS
      SELECT *
      FROM  Non_OEM_Part_To_Replace;
BEGIN
   FOR each_rec IN user_rec LOOP
      IF each_rec.DESCRIPTION is not null or each_rec.DESCRIPTION <> '' THEN
	   BEGIN
         INSERT INTO I18NNON_OEM_PARTS_DESCRIPTION
                     (ID, LOCALE,DESCRIPTION,NON_OEM_PARTS_DESCRIPTION
                     )
              VALUES (I18N_NON_OEM_PARTS_SEQ.nextval,'en_US', each_rec.DESCRIPTION,each_rec.id
                     );
		END;
	  END IF;     
   END LOOP;
   COMMIT;
END;
/
BEGIN
Non_OEM_Part_18_populate();
END;
/