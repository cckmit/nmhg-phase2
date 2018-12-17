--Purpose    : Used for Item validation
--Author     : Priyank Gupta
--Created On : 14-Mar-09

CREATE OR REPLACE
PROCEDURE ITEM_Validation 
AS

  CURSOR ALL_REC 
  IS
  	SELECT *
	FROM ITEM_STAGING
	WHERE NVL(ERROR_STATUS,'N') = 'N';
	
	v_error_code		VARCHAR2(4000)	  		:=  		NULL;
	v_count				VARCHAR2(1)		   		:= 		 	NULL;
	v_var				NUMBER;
	v_scheme_id 	  	NUMBER;
	v_bu				VARCHAR2(4000)	  		:=  		NULL;

  BEGIN

  	   FOR EACH_REC IN ALL_REC
	   LOOP
	   	   BEGIN
		   		
	   			v_error_code := NULL;
				
			    BEGIN
            SELECT NAME
            INTO v_bu
            FROM BUSINESS_UNIT WHERE UPPER(NAME) = UPPER(EACH_REC.BUSINESS_UNIT);
          EXCEPTION
          WHEN OTHERS THEN
						SELECT DECODE(v_error_code,NULL,'NUL0008',v_error_code||',NUL0008')
						INTO v_error_code
						FROM DUAL;
          END;
				
				BEGIN
					SELECT id INTO v_scheme_id FROM item_scheme WHERE NAME = 'Prod Struct Scheme' 
					AND BUSINESS_UNIT_INFO = v_bu;
				END;
				
				BEGIN
					 IF EACH_REC.ITEM_NUMBER IS NULL
					 THEN
					 
						SELECT DECODE(v_error_code,NULL,'NUL0001',v_error_code||',NUL0001')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;
				
				BEGIN
					 IF EACH_REC.ITEM_DESC IS NULL
					 THEN
					 	 
						SELECT DECODE(v_error_code,NULL,'NUL0002',v_error_code||',NUL0002')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;

				BEGIN
					 IF EACH_REC.ITEM_GROUP_CODE IS NULL
					 THEN
					 	  
						SELECT DECODE(v_error_code,NULL,'NUL0003',v_error_code||',NUL0003')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;
				
				BEGIN
					 IF EACH_REC.IS_SERIALIZED IS NULL
					 THEN
					 	 
						SELECT DECODE(v_error_code,NULL,'NUL0004',v_error_code||',NUL0004')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;
				
				BEGIN
					 IF EACH_REC.PART_MANUFACTURING_CODE IS NULL
					 THEN
					 	 
						SELECT DECODE(v_error_code,NULL,'NUL0005',v_error_code||',NUL0005')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;
				
				BEGIN
					 IF EACH_REC.ITEM_STATUS IS NULL 
					 THEN
					 	 
						SELECT DECODE(v_error_code,NULL,'NUL0006',v_error_code||',NUL0006')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;

				BEGIN
					IF EACH_REC.BUSINESS_UNIT IS NULL
					THEN
						
						SELECT DECODE(v_error_code,NULL,'NUL0007',v_error_code||',NUL0007')
						INTO v_error_code
						FROM DUAL;
					END IF;
				END;

				BEGIN
					 IF Upper(EACH_REC.IS_SERIALIZED) NOT IN ('Y','N')
					 THEN
					 	 
						SELECT DECODE(v_error_code,NULL,'INV0001',v_error_code||',INV0001')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;

				BEGIN
				IF EACH_REC.HAS_HOUR_METER IS NULL OR NVL(EACH_REC.HAS_HOUR_METER, 'N') NOT IN ('N','Y')
					THEN
					 	 
						SELECT DECODE(v_error_code,NULL,'INV0002',v_error_code||',INV0002')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;
				
				BEGIN
					 IF UPPER(EACH_REC.ITEM_STATUS) NOT IN ('ACTIVE','INACTIVE')
					 THEN
					 	 
						SELECT DECODE(v_error_code,NULL,'INV0003',v_error_code||',INV0003')
						INTO v_error_code
						FROM DUAL;
					 END IF;				
		   		END;	

				BEGIN
					 IF (EACH_REC.OWNER) IS NOT NULL 
					 THEN
					 	 BEGIN
					 	 SELECT 1
						 INTO v_var
						 FROM SUPPLIER S, PARTY P, BU_ORG_MAPPING BOM
						 WHERE 
						 	   S.SUPPLIER_NUMBER = EACH_REC.OWNER 
							   AND S.ID = P.ID
							   AND P.ID = BOM.ORG
							   AND BOM.BU = EACH_REC.BUSINESS_UNIT;
						EXCEPTION
						 WHEN NO_DATA_FOUND
						 THEN
						 	  
							SELECT DECODE(v_error_code,NULL,'INV0004',v_error_code||',INV0004')
							INTO v_error_code
							FROM DUAL;
						END;
					 END IF;				
		   		END;
				
				BEGIN
					 IF UPPER(EACH_REC.ITEM_GROUP_CODE) IS NOT NULL 
					 THEN
					 	 BEGIN
						 	  SELECT 1
							  INTO v_count
							  FROM ITEM_GROUP IG
							  WHERE
									IG.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT
									AND UPPER(IG.GROUP_CODE) = UPPER(EACH_REC.ITEM_GROUP_CODE)
									AND IG.SCHEME = v_scheme_id
									AND ROWNUM = 1;
							   EXCEPTION
							 WHEN NO_DATA_FOUND
							 THEN
								
									SELECT DECODE(v_error_code,NULL,'INV0005',v_error_code||',INV0005')
									INTO v_error_code
									FROM DUAL;
						END;					 	  
					 END IF;				
		   		END;

				BEGIN
					IF UPPER(NVL(EACH_REC.UPDATES,'N'))  NOT IN ('Y', 'N')
					THEN
						
						SELECT DECODE(v_error_code,NULL,'INV006',v_error_code||',INV006')
						INTO v_error_code
						FROM DUAL;
					END IF;
				END;
				
				BEGIN
					SELECT 1
					INTO v_var
					FROM 
						BUSINESS_UNIT
					WHERE
						UPPER(NAME) = UPPER(EACH_REC.BUSINESS_UNIT);
				EXCEPTION
				WHEN NO_DATA_FOUND
				THEN
					
					SELECT DECODE(v_error_code,NULL,'INV007',v_error_code||',INV007')
					INTO v_error_code
					FROM DUAL;
				END;

				BEGIN
					SELECT 1
					INTO v_var
					FROM ITEM_STAGING
					WHERE
						ITEM_NUMBER = EACH_REC.ITEM_NUMBER AND
						PART_MANUFACTURING_CODE = EACH_REC.PART_MANUFACTURING_CODE AND
						NVL(OWNER,'X') = EACH_REC.OWNER AND
						ID <> EACH_REC.ID;

					SELECT DECODE(v_error_code,NULL,'DUP0001',v_error_code||',DUP0001')
					INTO v_error_code
					FROM DUAL;

				EXCEPTION
				WHEN NO_DATA_FOUND
				THEN
					
	        NULL;
				END;			

				BEGIN
					 IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NOT NULL
					 AND EACH_REC.UPDATES IS NULL
					 THEN
							BEGIN
						 	 SELECT 1
							 INTO v_count
							 FROM 
							 	  ITEM I,
								  SUPPLIER S,
								  Bu_org_mapping BOM
							 WHERE
	 						 	  EACH_REC.OWNER = S.SUPPLIER_NUMBER AND
	 							  S.ID = I.OWNED_BY AND BOM.ORG = S.ID AND BOM.BU = v_bu
								  AND I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
								  AND I.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT 
								  AND ROWNUM = 1 ;
							
								  SELECT DECODE(v_error_code,NULL,'DUP0002',v_error_code||',DUP0002')
								  INTO v_error_code
								  FROM DUAL;
							EXCEPTION WHEN
							NO_DATA_FOUND THEN
							NULL;
							END;
                   	ELSE 
						 IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NULL
						 AND UPPER(NVL(EACH_REC.UPDATES,'N')) = 'N'
						 THEN
							BEGIN
						 	 SELECT 1
						 	 INTO v_count
						 	 FROM 
						 	 	   ITEM I,
								   PARTY P
								   
						 	 WHERE
							  	    I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
									AND I.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT
									AND I.OWNED_BY = P.ID 
									AND UPPER(P.NAME) = 'OEM'
									AND	ROWNUM = 1;		

									
									SELECT DECODE(v_error_code,NULL,'DUP0003',v_error_code||',DUP0003')
									INTO v_error_code
									FROM DUAL;
								EXCEPTION WHEN
									NO_DATA_FOUND THEN
									NULL;
									END;

						END IF;
                  END IF;
				END;

				BEGIN
					 IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NOT NULL
						AND EACH_REC.UPDATES = 'Y' THEN
						BEGIN
						 	 SELECT 1
							 INTO v_count
							 FROM 
							 	  ITEM I,
								  SUPPLIER S
							 WHERE
	 						 	  S.SUPPLIER_NUMBER =  EACH_REC.OWNER 
	 							  AND S.ID = I.OWNED_BY 
								  AND I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
								  AND I.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT 
								  AND ROWNUM = 1 ;
							EXCEPTION
								 WHEN NO_DATA_FOUND
								 THEN
										
										SELECT DECODE(v_error_code,NULL,'INV0008',v_error_code||',INV0008')
										INTO v_error_code
										FROM DUAL;	
						END;

						 ELSE 
						 IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NULL AND EACH_REC.UPDATES = 'Y'
						 THEN
							BEGIN
							 	 SELECT 1
							 	 INTO v_count
							 	 FROM 
							 	 	   ITEM I,
									   PARTY P
									   
							 	 WHERE
								  	    I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
										AND I.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT
										AND I.OWNED_BY = P.ID 
										AND UPPER(P.NAME) = 'OEM'
										AND	ROWNUM = 1;		

								 EXCEPTION
								 WHEN NO_DATA_FOUND
								 THEN
										
										SELECT DECODE(v_error_code,NULL,'INV0009',v_error_code||',INV0009')
										INTO v_error_code
										FROM DUAL;	
							END;				 
						END IF;
                  END IF;
				END;
			
			   IF v_error_code IS NULL
			   THEN
			   	   
			   	   UPDATE ITEM_STAGING
				   SET
				   	  ERROR_STATUS = 'Y',
					  ERROR_CODE = NULL			   	
					WHERE
					  ID = EACH_REC.ID;
			   ELSE
			   	   
			   	   	UPDATE ITEM_STAGING
				   SET
				   	  ERROR_STATUS = 'N',
				   	  ERROR_CODE = v_error_code
					WHERE
					  ID = EACH_REC.ID;		   
			   END IF;
			   
			   COMMIT;		   
		   END;	   
	   END LOOP; 

	   COMMIT;
END;
/
COMMIT
/