--Purpose    : PROCEDURE FOR NMHGSLMS-431 : Supplier Decision Upload Errors
--Author     : Arpitha Nadig AR
--Created On : 16-JULY-2014
create or replace 
PROCEDURE   SUPPLIER_DECISION_VALIDATION AS

CURSOR all_rec IS
    SELECT * FROM stg_supplier_decision
    WHERE NVL(error_status,'N') = 'N'
        AND upload_status IS NULL
    ORDER BY id;

v_error_code VARCHAR2(4000) := NULL;
v_loop_count NUMBER := 0;
v_error VARCHAR2(4000) := NULL;
v_file_upload_mgt_id NUMBER := 0;
v_success_count NUMBER := 0;
v_error_count NUMBER := 0;
v_recovery_claim_id NUMBER := 0;
v_rec_claim_state VARCHAR2(255);
v_contract VARCHAR2(255);
v_reason_id NUMBER;
v_return_loc NUMBER;
v_part_return_req_action NUMBER;
v_valid VARCHAR2(10);
v_user_locale  VARCHAR2(10);
v_countDuplicate NUMBER;
v_maxNoOfDisputes NUMBER := 0;
v_noOfTimesClaimsDisputed NUMBER;
V_BUSINESS_UNIT VARCHAR(255) := NULL;


BEGIN

    BEGIN 
       SELECT file_upload_mgt_id  INTO v_file_upload_mgt_id 
       FROM stg_supplier_decision WHERE ROWNUM = 1;

       SELECT  o.locale, f.BUSINESS_UNIT_INFO 
		INTO  v_user_locale, V_BUSINESS_UNIT
       FROM file_upload_mgt f,org_user o 
       WHERE f.id=v_file_upload_mgt_id 
       AND o.id=f.uploaded_by ;

        SELECT VAL.VALUE into v_maxNoOfDisputes 
		FROM CONFIG_PARAM PARAM,CONFIG_VALUE VAL
		WHERE VAL.CONFIG_PARAM = PARAM.ID 
		AND PARAM.NAME  = 'maximumDisputeAllowed' 
		AND VAL.BUSINESS_UNIT_INFO = V_BUSINESS_UNIT
		AND VAL.ACTIVE = 1;

  FOR each_rec IN all_rec LOOP


      v_error_code:=NULL;
      v_recovery_claim_id := NULL;
      v_rec_claim_state := NULL;
      v_contract := NULL;
      v_reason_id := NULL;
      v_return_loc := NULL;
      v_part_return_req_action :=NULL;
      v_valid :='N';
      v_countDuplicate :=0;
      v_noOfTimesClaimsDisputed :=0;

      IF each_rec.recovery_claim_number IS NULL THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC001');
      END IF;  
      IF each_rec.decision IS NULL THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC002');
      END IF;
      IF each_rec.decision_reason IS NULL AND ((trim(lower(each_rec.decision))= 'accepted') OR (trim(lower(each_rec.decision))= 'disputed')) THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC003');
      END IF;
      IF each_rec.supplier_comments IS NULL AND (trim(lower(each_rec.decision))= 'disputed') THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC004');
      END IF;
       IF each_rec.supplier_contract_code IS NULL THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC013');
      END IF;
      IF(trim(lower(each_rec.decision))= 'part return requested') AND (each_rec.part_return_request is NULL OR (trim(lower(each_rec.part_return_request))!='yes')) THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC014');
      END IF;
        IF each_rec.part_return_request IS NOT NULL AND (trim(lower(each_rec.part_return_request))='yes') AND each_rec.return_location_code is NULL THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC015');
      END IF;
	  IF each_rec.decision IS NOT NULL AND (trim(lower(each_rec.decision))= 'disputed') AND (trim(lower(each_rec.part_return_request))='yes') THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC019');
      END IF;
      IF each_rec.decision IS NOT NULL AND (trim(lower(each_rec.decision))= 'accepted') THEN
       IF each_rec.credit_memo_date is NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'RC022');  
       END IF;
        IF each_rec.credit_memo_number is NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'RC023');  
       END IF;
        IF each_rec.credit_memo_amount is NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'RC024');  
       END IF;
        IF each_rec.credit_memo_currency is NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'RC025');  
       END IF;
      END IF;
     IF (each_rec.credit_memo_date IS NOT NULL) AND NOT (common_utils.isValidDateFormat(each_rec.credit_memo_date,'MM/DD/YYYY')) THEN
         v_error_code := common_utils.addErrorMessage(v_error_code, 'RC020');     
      END IF;   
      BEGIN 
          SELECT claim.id,claim.recovery_claim_state  into v_recovery_claim_id,v_rec_claim_state
          FROM  recovery_claim claim 
          WHERE claim.recovery_claim_number=each_rec.recovery_claim_number;

          BEGIN
              IF (v_rec_claim_state = 'IN_RECOVERY' OR v_rec_claim_state = 'ON_HOLD_FOR_PART_RETURN') 
                THEN v_valid :='Y';
              END IF;
          END;
           IF (v_valid = 'N') THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'RC008');
         END IF;
			SELECT rc.contract into v_contract 
			FROM recovery_claim rc,contract cntrct
			WHERE rc.recovery_claim_number=each_rec.recovery_claim_number AND rc.contract = cntrct.id;

			IF (v_contract != each_rec.supplier_contract_code) THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'RC017');
			END IF;

       IF each_rec.return_location_code IS NOT NULL THEN 
          BEGIN
          SELECT count(1) into v_return_loc
          FROM supplier_locations loc,location l 
          WHERE loc.supplier in (SELECT c.supplier from contract c where c.id=each_rec.supplier_contract_code) and l.id=loc.locations
          and l.code=each_rec.return_location_code;

           IF v_return_loc=0 then           
                 v_error_code := common_utils.addErrorMessage(v_error_code, 'RC018');
           END IF;      
          END;
          END IF;
          
        IF each_rec.decision IS NOT NULL AND (trim(lower(each_rec.decision))= 'part return requested') THEN
        BEGIN
        select count(1) into v_part_return_req_action FROM JBPM_TASKINSTANCE t, RECOVERY_CLAIM rc
        where rc.id = t.claim_id and rc.recovery_claim_number = each_rec.recovery_claim_number
        and t.isopen_ = 1 and t.name_ = 'New';
        IF v_part_return_req_action=0 then
          v_error_code := common_utils.addErrorMessage(v_error_code, 'RC021');
          END IF;
          END;
          END IF;
          
         SELECT count(*) INTO v_countDuplicate 
         FROM stg_supplier_decision 
         WHERE recovery_claim_number = each_rec.recovery_claim_number;

         IF (v_countDuplicate >1 ) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'RC011');
         END IF;  

        EXCEPTION WHEN NO_DATA_FOUND THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'RC005');
      END ;


     IF(trim(lower(each_rec.decision))= 'accepted')
        THEN 
            BEGIN 
                  SELECT text.id INTO v_reason_id from  i18nlov_text text , list_of_values lov 
                  WHERE lov.type = 'RECOVERYCLAIMACCEPTANCEREASON' 
				  AND trim(lower(lov.state)) = 'active'
                  AND lov.id=text.list_of_i18n_values 
                  AND trim(lower(text.locale)) =trim(lower(v_user_locale))
                  AND trim(lower(text.description)) = trim(lower(each_rec.decision_reason))
				  AND LOV.BUSINESS_UNIT_INFO = V_BUSINESS_UNIT;
               EXCEPTION WHEN NO_DATA_FOUND THEN
                 v_error_code := common_utils.addErrorMessage(v_error_code, 'RC007');
           END;
		   BEGIN
           IF each_rec.claim_amount_being_accepted IS NOT NULL AND each_rec.claim_amount_being_accepted=0 THEN
           v_error_code := common_utils.addErrorMessage(v_error_code, 'RC016');
           END IF;
           END;
      ELSIF(trim(lower(each_rec.decision))= 'disputed')
      THEN 
         BEGIN 
                  SELECT count (*) INTO v_noOfTimesClaimsDisputed 
                  FROM REC_CLAIM_AUDIT audit1,RECOVERY_CLAIM claim 
                  WHERE audit1.FOR_RECOVERY_CLAIM = claim.ID
                  AND lower(audit1.RECOVERY_CLAIM_STATE) = 'rejected' 
                  AND claim.ID =  v_recovery_claim_id;


              IF(v_noOfTimesClaimsDisputed >=v_maxNoOfDisputes )
                THEN
                  v_error_code := common_utils.addErrorMessage(v_error_code, 'RC012');
              ELSE 
                   BEGIN 
                         SELECT text.id INTO v_reason_id from  i18nlov_text text , list_of_values lov 
                          WHERE lov.type = 'RECOVERYCLAIMREJECTIONREASON' 
						  AND trim(lower(lov.state)) = 'active' 
                          AND lov.id=text.list_of_i18n_values 
                          AND trim(lower(text.locale)) =trim(lower(v_user_locale))
                          AND trim(lower(text.description)) =trim(lower(each_rec.decision_reason))
						  AND LOV.BUSINESS_UNIT_INFO = V_BUSINESS_UNIT;
                           EXCEPTION WHEN NO_DATA_FOUND THEN
                           v_error_code := common_utils.addErrorMessage(v_error_code, 'RC009');
                    END;
              END IF;
          END;
      ELSIF(trim(lower(each_rec.decision))!= 'part return requested')
      THEN
        BEGIN 
           v_error_code := common_utils.addErrorMessage(v_error_code, 'RC006');
        END;
      END IF;

     IF v_error_code IS NULL THEN
       BEGIN
            UPDATE stg_supplier_decision SET
                 error_status='Y'
              WHERE id = each_rec.id;
              v_success_count:=v_success_count+1;

       END;
          ELSE
          BEGIN
               UPDATE stg_supplier_decision
               SET error_code=v_error_code, 
                  error_status = 'N' 
                  WHERE id = each_rec.id;
                 v_error_count:=v_error_count+1;

          END;
      END IF;

      v_loop_count := v_loop_count + 1;
  END LOOP;

  BEGIN 
       UPDATE file_upload_mgt SET 
            success_records= v_success_count, 
            error_records= v_error_count,
            total_records = v_loop_count
        WHERE id = v_file_upload_mgt_id;
  END;

   EXCEPTION WHEN OTHERS THEN 
   v_error_code:='INVALID DATA';
    END;
COMMIT;
END;