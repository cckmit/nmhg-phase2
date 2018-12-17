--Purpose    : Added tables I18NAction_Definition,I18NFailure_Type_Definition,I18NFailure_Cause_Definition for --internationalization of Assmebly Defintion Names
--Author     : rakesh.r
--Created On : 15-SEP-08

CREATE TABLE I18NFailure_Cause_Definition (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), name VARCHAR2(255 CHAR), Failure_Cause_definition NUMBER(19,0) NOT NULL)
/
ALTER TABLE I18NFailure_Cause_Definition  ADD CONSTRAINT i18nFailure_Cause_def_PK	PRIMARY KEY(ID)
/
ALTER TABLE I18NFailure_Cause_Definition ADD CONSTRAINT i18nAssembly_Cause_FK FOREIGN KEY (Failure_Cause_definition) REFERENCES Failure_Cause_definition(ID)
/
CREATE TABLE I18NFailure_Type_Definition(id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), name VARCHAR2(255 CHAR), Failure_Type_definition NUMBER(19,0) NOT NULL)
/
ALTER TABLE I18NFailure_Type_Definition  ADD CONSTRAINT i18nFailure_Type_def_PK	PRIMARY KEY(ID)
/
ALTER TABLE I18NFailure_Type_Definition ADD CONSTRAINT i18nFailure_Type_def_FK FOREIGN KEY (Failure_Type_definition) REFERENCES Failure_Type_definition(ID)
/
CREATE TABLE I18NAction_Definition (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), name VARCHAR2(255 CHAR), Action_Definition NUMBER(19,0) NOT NULL)
/
ALTER TABLE I18NAction_Definition  ADD CONSTRAINT i18nAction_def_PK	PRIMARY KEY(ID)
/
ALTER TABLE I18NAction_Definition ADD CONSTRAINT i18nAction_def_FK FOREIGN KEY (Action_Definition) REFERENCES Action_Definition(ID)
/
CREATE SEQUENCE I18N_Failure_Cause_Def_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE SEQUENCE I18N_Failure_Type_Def_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE SEQUENCE I18N_ACTION_DEF_SEQ START WITH 1000 INCREMENT BY 20
/
CREATE OR REPLACE PROCEDURE i18nfailure_type_def_populate AS
   CURSOR user_rec IS
      SELECT *
      FROM failure_type_definition;
BEGIN
   FOR each_rec IN user_rec LOOP
      BEGIN
         INSERT INTO i18nfailure_type_definition
                     (ID, LOCALE,NAME,Failure_Type_definition
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'en_US', each_rec.NAME,each_rec.id
                     );
		 INSERT INTO i18nfailure_type_definition
                     (ID, LOCALE,NAME,Failure_Type_definition
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'en_UK', each_rec.NAME,each_rec.id
                     );
		INSERT INTO i18nfailure_type_definition
                     (ID, LOCALE,NAME,Failure_Type_definition
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'fr_FR', each_rec.NAME,each_rec.id
                     );
		INSERT INTO i18nfailure_type_definition
                     (ID, LOCALE,NAME,Failure_Type_definition
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'de_DE', each_rec.NAME,each_rec.id
                     );
      END;
   END LOOP;

   COMMIT;
END;
/

BEGIN
i18nfailure_type_def_populate();
END;
/

CREATE OR REPLACE PROCEDURE i18nfailure_Cause_def_populate AS
   CURSOR user_rec IS
      SELECT *
      FROM failure_cause_definition;
BEGIN
   FOR each_rec IN user_rec LOOP
      BEGIN
         INSERT INTO i18nfailure_cause_definition
                     (ID, LOCALE,NAME,Failure_cause_definition
                     )
              VALUES (I18N_Failure_Cause_Def_SEQ.nextval,'en_US', each_rec.NAME,each_rec.id
                     );
		INSERT INTO i18nfailure_cause_definition
                     (ID, LOCALE,NAME,Failure_cause_definition
                     )
              VALUES (I18N_Failure_Cause_Def_SEQ.nextval,'en_UK', each_rec.NAME,each_rec.id
                     );
  	    INSERT INTO i18nfailure_cause_definition
                     (ID, LOCALE,NAME,Failure_cause_definition
                     )
              VALUES (I18N_Failure_Cause_Def_SEQ.nextval,'fr_FR', each_rec.NAME,each_rec.id
                     );
     INSERT INTO i18nfailure_cause_definition
                     (ID, LOCALE,NAME,Failure_cause_definition
                     )
              VALUES (I18N_Failure_Cause_Def_SEQ.nextval,'de_DE', each_rec.NAME,each_rec.id
                     );
     
      END;
   END LOOP;

   COMMIT;
END;
/

BEGIN
i18nfailure_Cause_def_populate();
END;
/
CREATE OR REPLACE PROCEDURE i8n_action_def_populate AS
   CURSOR user_rec IS
      SELECT *
      FROM action_definition;
BEGIN
   FOR each_rec IN user_rec LOOP
      BEGIN
         INSERT INTO I18Naction_Definition
                     (ID, LOCALE,NAME,action_DEFINITION
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'en_US', each_rec.NAME,each_rec.id
                     );
		 INSERT INTO I18Naction_Definition
                     (ID, LOCALE,NAME,action_DEFINITION
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'en_EN', each_rec.NAME,each_rec.id
                     );
		 INSERT INTO I18Naction_Definition
                     (ID, LOCALE,NAME,action_DEFINITION
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'fr_FR', each_rec.NAME,each_rec.id
                     );
		 INSERT INTO I18Naction_Definition
                     (ID, LOCALE,NAME,action_DEFINITION
                     )
              VALUES (I18N_Failure_Type_Def_SEQ.nextval,'de_DE', each_rec.NAME,each_rec.id
                     );
      END;
   END LOOP;

   COMMIT;
END;
/

BEGIN
i8n_action_def_populate();
END;
/


