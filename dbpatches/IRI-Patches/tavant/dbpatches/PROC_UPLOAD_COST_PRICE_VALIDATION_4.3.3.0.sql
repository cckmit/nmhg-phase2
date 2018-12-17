--Purpose : Fix for TSESA-229 & TSESA-231
--Author  : raghuram.d
--Date    : 22/Jan/2010

CREATE OR REPLACE PROCEDURE upload_cost_price_validation AS

CURSOR all_rec IS
    SELECT * FROM stg_cost_price
    WHERE NVL(error_status,'N') = 'N'
        AND upload_status IS NULL
    ORDER BY id;

v_bu_name VARCHAR2(255) := NULL;
v_count NUMBER;
v_error_code VARCHAR2(4000) := NULL;
v_loop_count NUMBER := 0;
v_error VARCHAR2(4000) := NULL;
v_file_upload_mgt_id NUMBER := 0;
v_success_count NUMBER := 0;
v_error_count NUMBER := 0;

v_cp_curr VARCHAR2(10);
v_claim_id NUMBER;
v_rec_claim_id NUMBER;
v_contract_id NUMBER;
v_causal_part NUMBER;
v_service_id NUMBER;
v_opr_id NUMBER;
v_part_id NUMBER;
v_cp_rounded NUMBER(19,2);
v_dealer_id NUMBER;
v_dealer_currency VARCHAR2(10);

BEGIN

BEGIN
    SELECT f.business_unit_info INTO v_bu_name
    FROM file_upload_mgt f
    WHERE f.id = (SELECT file_upload_mgt_id FROM stg_cost_price WHERE rownum = 1);
EXCEPTION 
    WHEN OTHERS THEN
        NULL;
END;

FOR each_rec IN all_rec LOOP

    v_error_code := NULL;
    v_cp_curr := NULL;
    v_claim_id := NULL;
    v_rec_claim_id := NULL;
    v_contract_id := NULL;
    v_causal_part := NULL;
    v_service_id := NULL;
    v_opr_id := NULL;
    v_part_id := NULL;
    v_cp_rounded := NULL;
    v_dealer_id := NULL;
    v_dealer_currency := NULL;

    IF each_rec.claim_number IS NULL THEN
        --Claim Number is mandatory
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CP001');
    ELSE
        BEGIN
            SELECT c.id,r.id,r.contract,si.causal_part,si.service_detail,c.for_dealer
            INTO v_claim_id,v_rec_claim_id,v_contract_id,v_causal_part,v_service_id,v_dealer_id
            FROM claim c, recovery_claim r, service_information si
            WHERE c.business_unit_info=v_bu_name AND UPPER(c.claim_number)=UPPER(each_rec.claim_number)
                AND c.service_information = si.id
                AND r.claim=c.id AND r.id=(
                    SELECT MAX(id) FROM recovery_claim WHERE claim=c.id);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                --Invalid recovery claim for the selected BU
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CP002');
            WHEN OTHERS THEN
                --Invalid recovery claim for the selected BU
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CP002');
        END;
    END IF;

    IF each_rec.supplier_number IS NULL THEN
        --Supplier Number is mandatory
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CP004');
    ELSIF v_claim_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count
        FROM contract c,supplier s
        WHERE c.supplier = s.id AND c.id=v_contract_id 
            AND UPPER(s.supplier_number)=UPPER(each_rec.supplier_number);
        IF v_count != 1 THEN
            --Invalid supplier number on the recovery claim
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CP005');
        END IF;
    END IF;

    IF each_rec.part_number IS NULL THEN
        --Part Number is mandatory
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CP006');
    ELSIF v_claim_id IS NOT NULL THEN
    BEGIN
        SELECT opr.id, i.id INTO v_opr_id, v_part_id
        FROM service_oemparts_replaced sopr,oem_part_replaced opr, item i
        WHERE sopr.service=v_service_id AND sopr.oemparts_replaced=opr.id
            AND opr.item_ref_unszed_item = i.id
            AND (UPPER(i.item_number)=UPPER(each_rec.part_number) OR 
                UPPER(i.alternate_item_number)=UPPER(each_rec.part_number));
        --IF v_causal_part = v_part_id THEN
            --Is a causal part
            --v_error_code := common_utils.addErrorMessage(v_error_code, 'CP007');
        --END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        --Invalid part number on the recovery claim
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CP008');
    END;
    END IF;

    IF each_rec.cost_price IS NULL THEN
        --Cost Price is mandatory
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CP009');
    ELSE
    BEGIN
        SELECT cast( each_rec.cost_price as number(19,2)) INTO v_cp_rounded FROM DUAL;
    EXCEPTION WHEN OTHERS THEN
        --Invalid cost price value
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CP010');
    END;
    END IF;

    IF each_rec.currency IS NULL THEN
        --Currency is mandatory
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CP011');
    ELSE
        SELECT COUNT(*) INTO v_count
        FROM currency_exchange_rate WHERE to_currency='USD' AND UPPER(from_currency)=UPPER(each_rec.currency);
        IF v_count = 0 THEN
            --Invalid currency, exchange rate not specified to convert it to USD
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CP012');
        ELSE
            v_cp_curr := UPPER(each_rec.currency);
        END IF;
    END IF;

    IF each_rec.override IS NOT NULL THEN
        IF UPPER(each_rec.override) NOT IN ('Y','N') THEN
            --Valid values for override are y,Y,n,N
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CP013');
        END IF;
    END IF;

    IF v_error_code IS NULL THEN

        IF v_dealer_id IS NOT NULL THEN
            SELECT preferred_currency into v_dealer_currency 
            FROM organization WHERE id=v_dealer_id;
        END IF;

        UPDATE stg_cost_price SET
            error_status = 'Y',
            error_code = NULL,
            dealer_currency = v_dealer_currency,
            oem_part_replaced = v_opr_id,
            currency = UPPER(currency),
            cost_price = v_cp_rounded,
            recovery_claim = v_rec_claim_id
        WHERE id = each_rec.id;
    ELSE
        UPDATE stg_cost_price SET
            error_status = 'N',
            error_code = v_error_code
        WHERE id = each_rec.id;
    END IF;

    v_loop_count := v_loop_count + 1;
    IF v_loop_count = 10 THEN
        COMMIT;
        v_loop_count := 0; 
    END IF;

END LOOP;

IF v_loop_count > 0 THEN
    COMMIT;
END IF;

BEGIN
    SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
    FROM stg_cost_price WHERE ROWNUM = 1;

    -- Success Count
    BEGIN
        SELECT count(*) INTO v_success_count
        FROM stg_cost_price
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
    EXCEPTION
        WHEN OTHERS THEN
            v_success_count := 0;
    END;
        
    -- Error Count
    BEGIN
        SELECT count(*) INTO v_error_count
        FROM stg_cost_price 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
    EXCEPTION
        WHEN OTHERS THEN
            v_error_count := 0;
    END;

    -- Total Count
    SELECT count(*) INTO v_count
    FROM stg_cost_price
    WHERE file_upload_mgt_id = v_file_upload_mgt_id;

    UPDATE file_upload_mgt SET 
        success_records= v_success_count, 
        error_records= v_error_count,
        total_records = v_count
    WHERE id = v_file_upload_mgt_id;
    
EXCEPTION
    WHEN OTHERS THEN
        v_error := SUBSTR(SQLERRM, 1, 4000);
        UPDATE file_upload_mgt SET 
            error_message = v_error
        WHERE id = v_file_upload_mgt_id;
END;

COMMIT;

END;
/