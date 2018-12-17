--Patch for adding column in ORG_USER table which will capture the USER type whether its INTERNAL, OR EXTERNAL.

CREATE OR REPLACE PROCEDURE Update_USER_TYPE_IN_ORG_USER
AS

	
	
	-- variables decalaration
	V_BELONGS_TO				NUMBER;
	V_OEM_ID					number;
	v_user_id					NUMBER;
	v_user_type					varchar2(255);
	
	Begin
	-- check whether param exists in config_param table
	Declare CURSOR All_REC 
	IS
		select * from ORG_USER;
	Begin
				--SELECT OEM ID FROM PARTY TABLE
				BEGIN
					SELECT ID INTO V_OEM_ID FROM PARTY WHERE UPPER(NAME) = 'OEM';
				END;
				
				
			for each_rec in All_REC
				loop
					begin
               --select org_user id 
				BEGIN
					SELECT ID
					INTO v_user_id
					FROM ORG_USER 
					WHERE 
						id = each_rec.id;
				END;
				--select belongs_to_organization 
				BEGIN
					SELECT BELONGS_TO_ORGANIZATION INTO V_BELONGS_TO 
					FROM ORG_USER
					WHERE
					ID = v_user_id;
				END;
				
				--check whether its internal user or external user
				BEGIN
					IF 
						V_BELONGS_TO = V_OEM_ID
					THEN
						v_user_type := 'INTERNAL';
					ELSE 
						v_user_type := 'EXTERNAL';
					END IF;
				END;
				
						update org_user set user_type = v_user_type
						where id = each_rec.id;
					End;
				End loop   --end of inner loop	(i.e ALL_BU cursor)				
			COMMIT;	
		End;
	End;