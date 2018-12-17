--Purpose    : Populate config param options for BU 
--Author     : prashanth konda
--Created On : 29-SEP-08


CREATE OR REPLACE PROCEDURE POPULATE_CONFIG_PARAM_OPTIONS AS
   radio_id1                NUMBER := 0;
   radio_id2                NUMBER := 0;
   mapping_index                    NUMBER := 0;
   CURSOR params IS
      SELECT id
      FROM  config_param where type='boolean';
	  
BEGIN
  select id into radio_id1 from config_param_option where value = 'true';
  select id into radio_id2 from config_param_option where value = 'false';       

   FOR each_rec IN params LOOP
	   BEGIN
         INSERT INTO config_param_options_mapping
              values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval ,each_rec.id, radio_id1);	   
         INSERT INTO config_param_options_mapping
              values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval ,each_rec.id, radio_id2);			   
		END;	      
   END LOOP;
   COMMIT;
END;
/
BEGIN
POPULATE_CONFIG_PARAM_OPTIONS();
END;
/