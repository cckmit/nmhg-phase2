--Purpose    : Fix for SI bug TWMS4.3U-380  part-2.
--Created On : Apr 15,2011
--Created By : Prabhu R

-- The below mentioned anonymous block needs to be executed in the destination database
DECLARE

	CURSOR cur_notification_param 
	IS SELECT * FROM notification_parameter;
	
	v_stmt_number NUMBER := 0;
	v_key notification_parameter.key%TYPE;
	v_value notification_parameter.value%TYPE;
	v_notification_param_value NUMBER;

BEGIN
	
	FOR cur_notification_param_rec IN cur_notification_param LOOP
		
		-- Retrieve the key type from notification parameter for a particular id
		v_stmt_number := 10;
		SELECT key,value INTO v_key, v_value 
			FROM notification_parameter 
		WHERE id = cur_notification_param_rec.id;
		
		CASE v_key
		
			WHEN 'dealerGroup'	THEN			
				
				v_stmt_number := 20;
				
				SELECT id INTO v_notification_param_value
					FROM dealer_group 
				WHERE id = v_value;			
							
			WHEN 'claimId' OR 'id' THEN				
			
				v_stmt_number := 30;
				
				SELECT id INTO v_notification_param_value
					FROM claim
				WHERE id = v_value;
							
			WHEN 'taskInstanceId'  THEN				
				
				v_stmt_number := 40;
				
				SELECT id INTO v_notification_param_value
					FROM part_return
				WHERE id = v_value;		
		END CASE;				
		
		-- Updating notification parameter with the appropriate VALUE column
		UPDATE notification_parameter
			SET value = v_notification_param_value
		WHERE id = cur_notification_param_rec.id;
	
	END LOOP;
	
	COMMIT;

EXCEPTION
WHEN OTHERS THEN
	ROLLBACK;
	DBMS_OUTPUT.PUT_LINE('Exception Occured : ' || SUBSTR(SQLERRM,1,255));
END;
/
commit
/