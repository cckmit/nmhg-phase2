create or replace
PROCEDURE STG_ALTER_SN_VALIDATION AS 
	CURSOR ALL_REC IS
		SELECT * FROM STG_ALTER_SERIALNUMBER
		WHERE NVL(ERROR_STATUS,'N') = 'N'
			AND UPLOAD_STATUS IS NULL 
		ORDER BY ID ASC;

    v_error_code			    VARCHAR2(4000) := NULL;
    v_error                     VARCHAR2(4000) := NULL;
    v_uploaded_by               VARCHAR2(255);
    v_bu_name                   VARCHAR2(255);
    v_success_count             NUMBER  := 0;
    v_error_count               NUMBER  := 0;
    v_count                     NUMBER  := NULL;
    v_file_upload_mgt_id        NUMBER  := 0;
	
    v_item_id                   NUMBER  := NULL;
    v_clm_count                 NUMBER;
    v_compgn_clm_count          NUMBER;
    v_sn_id                     NUMBER;
    v_pending_wnty              NUMBER;
   
BEGIN
	
	SELECT u.login, f.business_unit_info 
	INTO v_uploaded_by, v_bu_name
	FROM org_user u,file_upload_mgt f
	WHERE u.id = f.uploaded_by  AND f.id in
		(SELECT file_upload_mgt_id FROM STG_ALTER_SERIALNUMBER WHERE rownum = 1);

	
	--MAIN  LOOP FOR ALL ROWS. EACH ROW WILL BE VALIDATED ONE BY ONE
	FOR EACH_REC IN ALL_REC
	LOOP
		
	--RESETING VARIABLES FOR EACH LOOP.
	v_error_code              := NULL;
	v_clm_count               := 0;
	v_compgn_clm_count        := 0;
	v_sn_id                   :=  NULL;
	v_item_id                 := NULL;
	v_pending_wnty            := 0;
	
	-- VALIDATION FOR ITEM NUMBER   
	IF each_rec.item_number IS NULL THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS002');
	ELSIF NOT COMMON_VALIDATION_UTILS.isValidItemNumber(each_rec.item_number, v_bu_name) THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS005');
	ELSE
	BEGIN
		SELECT i.id
		INTO v_item_id
		FROM ITEM i, PARTY p 
		WHERE ( lower(i.alternate_item_number) = lower(ltrim(rtrim(each_rec.item_number)))  OR 
			lower(i.item_number) = lower(ltrim(rtrim(each_rec.item_number))) )
			AND lower(i.business_unit_info) = lower(v_bu_name)
			AND i.owned_by = p.ID 
			AND p.NAME = common_utils.constant_oem_name and i.d_active = 1 AND ROWNUM = 1;
	EXCEPTION
	WHEN NO_DATA_FOUND THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS005');
	END;
	END IF;
      
	-- VALIDATION FOR ACTION
	IF each_rec.action IS NULL THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS003');
	ELSIF UPPER(TRIM(EACH_REC.action)) NOT IN ('ACTIVATE', 'DEACTIVATE') THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS006');
	END IF;
	      
	-- VALIDATION FOR COMMENT
	IF each_rec.internal_comments IS NOT NULL and LENGTH(each_rec.internal_comments) > 255 THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS007');
	END IF;
      
	-- VALIDATION FOR SERIAL NUMBER
	IF each_rec.serial_number IS NULL THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS001');
	ELSIF v_item_id is not null THEN
	BEGIN
		IF each_rec.action IS NOT NULL AND UPPER(TRIM(EACH_REC.action)) = 'DEACTIVATE' THEN
			SELECT inv.id,inv.pending_warranty 
				INTO v_sn_id, v_pending_wnty
			FROM inventory_item inv
			WHERE UPPER(inv.serial_number) = UPPER(each_rec.serial_number) 
				AND inv.business_unit_info = v_bu_name
				AND inv.of_type = v_item_id
				AND inv.d_active=1; 
		ELSIF each_rec.action IS NOT NULL AND UPPER(TRIM(EACH_REC.action)) = 'ACTIVATE' THEN
			SELECT inv.id,inv.pending_warranty 
			INTO v_sn_id, v_pending_wnty
			FROM inventory_item inv
			WHERE UPPER(inv.serial_number) = UPPER(each_rec.serial_number) 
				AND inv.business_unit_info = v_bu_name
				AND inv.of_type = v_item_id
				AND inv.d_active=0 AND ROWNUM=1; 
		END IF;
	EXCEPTION
	WHEN NO_DATA_FOUND THEN
		BEGIN
			SELECT inv.id INTO v_sn_id
			FROM inventory_item inv
			WHERE UPPER(inv.serial_number) = UPPER(each_rec.serial_number) 
				AND inv.business_unit_info = v_bu_name
				AND inv.of_type = v_item_id
				AND ROWNUM=1; 
		IF UPPER(TRIM(EACH_REC.action)) = 'DEACTIVATE' THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS012');
		ELSIF UPPER(TRIM(EACH_REC.action)) = 'ACTIVATE' THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS013');
		END IF;
			v_sn_id := NULL;
		EXCEPTION
		WHEN NO_DATA_FOUND THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS004');
		END;
	WHEN OTHERS THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS004');
	END;
	END IF;
      
	-- VALIDATION FOR OPEN CLAIMS
	IF (v_sn_id IS NOT NULL AND UPPER(TRIM(EACH_REC.action)) = 'DEACTIVATE') THEN 
		select count(*) as cnt into v_clm_count 
		from claim c,claimed_item ci
		where ci.item_ref_inv_item = v_sn_id 
			and c.state not in ('DELETED','DEACTIVATED','DRAFT_DELETED','DRAFT');
		IF v_clm_count > 0 THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS008');
		END IF;
	  
		-- VALIDATION FOR COMPAIGN  CLAIM AGAGINST A SERIAL NUMBER
		select  count(*) as cnt1 into v_compgn_clm_count  
		from campaign_notification
		where item = v_sn_id and notification_status = 'PENDING';
		IF(v_compgn_clm_count > 0) THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS009');
		END IF;

		-- VALIDATION FOR ANY PENDING TRANSACTION AGAINST A SERILA NUMBER LIKE WR, ETR, RMT etc
		IF(v_pending_wnty > 0) THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'SAS010');
		END IF; 
	END IF ;
	
	IF v_error_code IS NULL THEN
		UPDATE STG_ALTER_SERIALNUMBER
		SET ERROR_STATUS = 'Y',
			ERROR_CODE = NULL,
			SN_ID = v_sn_id 
		WHERE id = EACH_REC.id;
	ELSE
		UPDATE STG_ALTER_SERIALNUMBER
		SET ERROR_STATUS = 'N',
			ERROR_CODE = v_error_code
		WHERE id = EACH_REC.id;						
	END IF;
	commit;
       
	END LOOP;				

	BEGIN
        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
        FROM STG_ALTER_SERIALNUMBER WHERE ROWNUM = 1;
    
        -- Success Count
        BEGIN
            SELECT count(*) INTO v_success_count
            FROM STG_ALTER_SERIALNUMBER 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_success_count := 0;
        END;
            
        -- Error Count
        BEGIN
            SELECT count(*) INTO v_error_count
            FROM STG_ALTER_SERIALNUMBER 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := 0;
        END;

        -- Total Count
        SELECT count(*) INTO v_count
        FROM STG_ALTER_SERIALNUMBER 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id;
    
        UPDATE file_upload_mgt SET 
            success_records= v_success_count, 
            error_records= v_error_count,
            total_records = v_count
        WHERE id = v_file_upload_mgt_id;
    
		COMMIT;
    EXCEPTION
    WHEN OTHERS THEN
		ROLLBACK;
		v_error := SUBSTR(SQLERRM, 1, 4000);
		UPDATE file_upload_mgt 
		SET error_message = v_error
		WHERE id = v_file_upload_mgt_id;
    END;
    commit;
  			
END STG_ALTER_SN_VALIDATION;
/
create or replace
PROCEDURE STG_ALTER_SN_UPLOAD
IS
  CURSOR ALL_REC
  IS
  SELECT *
  FROM STG_ALTER_SERIALNUMBER
  WHERE NVL(ERROR_STATUS, 'N') = 'Y'
  AND ERROR_CODE                IS NULL
  AND NVL(UPLOAD_STATUS, 'N')    = 'N'
  ORDER BY ID ASC;
  
  v_serial_number VARCHAR2(255);
  v_item_number   VARCHAR2(255);
  v_action        VARCHAR2(255);
  v_comments      VARCHAR2(1000);
  v_error         VARCHAR2 (1000);
  v_uploaded_by   NUMBER :=0;
  v_file_upload_id NUMBER;
BEGIN

 SELECT u.id,f.id
         INTO v_uploaded_by,v_file_upload_id
         FROM org_user u,
         file_upload_mgt f
         WHERE u.id = f.uploaded_by
      AND f.id    IN
        (SELECT file_upload_mgt_id FROM STG_ALTER_SERIALNUMBER WHERE rownum = 1
        );

  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
      
      IF(UPPER(EACH_REC.ACTION) = 'ACTIVATE' ) THEN
         UPDATE inventory_item
        SET D_ACTIVE          =1                         ,
          d_internal_comments =EACH_REC.INTERNAL_COMMENTS,
          d_updated_on        = sysdate                  ,
		  d_updated_time      = systimestamp,
          d_last_updated_by   = v_uploaded_by
          WHERE id = EACH_REC.SN_ID AND
          UPPER(serial_number) = UPPER(EACH_REC.SERIAL_NUMBER);
      ELSE
	  BEGIN
         UPDATE inventory_item
        SET D_ACTIVE          =0                         ,
          d_internal_comments =EACH_REC.INTERNAL_COMMENTS,
          d_updated_on        = sysdate                  ,
		  d_updated_time      = systimestamp,
          d_last_updated_by   = v_uploaded_by
          WHERE id = EACH_REC.SN_ID;
	  EXCEPTION
	  WHEn OTHERS THEN
		UPDATE inventory_item
        SET D_ACTIVE          =0                         ,
		  serial_number = serial_number || '_DEACTIVE_' || v_file_upload_id,
          d_internal_comments =EACH_REC.INTERNAL_COMMENTS,
          d_updated_on        = sysdate                  ,
		  d_updated_time      = systimestamp,
          d_last_updated_by   = v_uploaded_by
          WHERE id = EACH_REC.SN_ID;
	  END;
      END IF;
       UPDATE STG_ALTER_SERIALNUMBER
      SET UPLOAD_STATUS = 'Y',
        UPLOAD_DATE     = SYSDATE
        WHERE id        = EACH_REC.id;
    END;
    COMMIT;
  END LOOP;
END STG_ALTER_SN_UPLOAD;
/