--Purpose    : Added tables I18NAssembly_Definition for internationalization of Assmebly Defintion Names
--Author     : rakesh.r
--Created On : 13-SEP-08

CREATE TABLE I18NAssembly_Definition (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), name VARCHAR2(255 CHAR), assembly_definition NUMBER(19,0) NOT NULL)
/
ALTER TABLE I18NAssembly_Definition  ADD CONSTRAINT i18nAssembly_def_PK  	PRIMARY KEY( 	ID	)
/
ALTER TABLE I18NAssembly_Definition ADD CONSTRAINT i18nAssembly_def_FK FOREIGN KEY (assembly_definition) REFERENCES assembly_definition(ID)
/
CREATE SEQUENCE I18N_Assembly_Definition_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE OR REPLACE PROCEDURE i8n_assembly_def_populate AS
   CURSOR user_rec IS
      SELECT *
      FROM assembly_definition;
BEGIN
   FOR each_rec IN user_rec LOOP
      BEGIN
         INSERT INTO I18NAssembly_Definition
                     (ID, LOCALE,NAME,ASSEMBLY_DEFINITION
                     )
              VALUES (I18N_Assembly_Definition_SEQ.nextval,'en_US', each_rec.NAME,each_rec.id
                     );
		 INSERT INTO I18NAssembly_Definition
                     (ID, LOCALE,NAME,ASSEMBLY_DEFINITION
                     )
              VALUES (I18N_Assembly_Definition_SEQ.nextval,'en_EN', each_rec.NAME,each_rec.id
                     );
		 INSERT INTO I18NAssembly_Definition
                     (ID, LOCALE,NAME,ASSEMBLY_DEFINITION
                     )
              VALUES (I18N_Assembly_Definition_SEQ.nextval,'fr_FR', each_rec.NAME,each_rec.id
                     );
		 INSERT INTO I18NAssembly_Definition
                     (ID, LOCALE,NAME,ASSEMBLY_DEFINITION
                     )
              VALUES (I18N_Assembly_Definition_SEQ.nextval,'de_DE', each_rec.NAME,each_rec.id
                     );
      END;
   END LOOP;

   COMMIT;
END;
/
BEGIN
i8n_assembly_def_populate();
END;
/