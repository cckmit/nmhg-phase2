--Purpose    : Migrating from CampaignClass to LOV
--Author     : rakesh.r	
--Created On : 03-nov-08

CREATE OR REPLACE PROCEDURE CAMPAIGN_CLASS_TO_LOV AS
   CURSOR user_rec IS
      SELECT *
      FROM  campaign_class;
	v_list_of_values_id		   number;
BEGIN
   FOR each_rec IN user_rec LOOP
   select List_Of_Values_SEQ.nextval into v_list_of_values_id	from dual; 
         INSERT INTO List_of_values
                     (ID,TYPE,CODE,STATE,VERSION,D_CREATED_ON,BUSINESS_UNIT_INFO
                     )
              VALUES (v_list_of_values_id,'CAMPAIGNCLASS',each_rec.CODE,'active',1,
			  		  each_rec.D_CREATED_ON,each_rec.BUSINESS_UNIT_INFO
                     );
		 INSERT INTO I18NLOV_TEXT
                     (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES
                     )
              VALUES (I18N_Lov_Text_SEQ.nextval,'en_US',each_rec.NAME,v_list_of_values_id
                     );
		
   END LOOP;

   COMMIT;
END;
/
BEGIN
CAMPAIGN_CLASS_TO_LOV();
END;
/
COMMIT
/