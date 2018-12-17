CREATE TABLE I18NLOV_TEXT
(
  ID                   NUMBER(19)               NOT NULL,
  LOCALE               VARCHAR2(255 BYTE),
  DESCRIPTION          VARCHAR2(255 BYTE),
  LIST_OF_I18N_VALUES  NUMBER(19)
)
/
ALTER TABLE I18NLOV_TEXT ADD CONSTRAINT I18NLOV_TEXT_PK	PRIMARY KEY( 	ID	)
/
/* Formatted on 2008/07/11 16:22 (Formatter Plus v4.8.7) */
CREATE OR REPLACE PROCEDURE LIST_OF_I18N_VALUES_UPDATE AS
   CURSOR LOVS IS
      SELECT *
      FROM LIST_OF_VALUES;

BEGIN
   FOR EACH_REC IN LOVS LOOP
      BEGIN
         INSERT INTO I18NLOV_TEXT
                     (ID, locale,
                      description, list_of_i18n_values
                     )
              VALUES (i18n_lov_text_seq.NEXTVAL, 'en_US',
                      each_rec.description, each_rec.ID
                     );
      END;
   END LOOP;

   COMMIT;  
  
END;
/
BEGIN
LIST_OF_I18N_VALUES_UPDATE();
END;
/





