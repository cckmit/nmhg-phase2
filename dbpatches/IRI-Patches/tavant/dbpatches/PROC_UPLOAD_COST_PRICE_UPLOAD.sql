--Purpose : Cost Price Upload
--Author  : raghuram.d
--Date    : 05/Jan/2010

CREATE OR REPLACE PROCEDURE upload_cost_price_upload AS

CURSOR all_rec IS
    SELECT *
    FROM stg_cost_price
    WHERE NVL(error_status, 'N') = 'Y'
    AND error_code IS NULL
    AND NVL(upload_status, 'N')  = 'N'
    ORDER BY id;

v_cp_in_usd NUMBER(19,2);
v_cp_in_dealer_curr NUMBER(19,2);
v_count NUMBER;
v_error_code VARCHAR2(100);
v_upload_error VARCHAR2(4000) := NULL;

BEGIN

FOR each_rec IN all_rec LOOP
BEGIN
    v_cp_in_usd := NULL;
    v_cp_in_dealer_curr := NULL;
    v_error_code := NULL;


    IF each_rec.currency = 'USD' OR each_rec.currency = each_rec.dealer_currency THEN
        SELECT CAST(each_rec.cost_price AS NUMBER(19,2)) INTO v_cp_in_usd FROM DUAL;
    ELSE
        BEGIN
            SELECT CAST(factor * CAST(each_rec.cost_price AS NUMBER(19,2)) AS NUMBER(19,2)) INTO v_cp_in_usd
            FROM currency_conversion_factor WHERE parent = (
              SELECT id FROM currency_exchange_rate 
              WHERE from_currency = each_rec.currency AND to_currency = 'USD'
            ) AND (SELECT repair_date FROM claim WHERE UPPER(claim_number) = UPPER(each_rec.claim_number)) 
                BETWEEN from_date AND till_date 
            AND ROWNUM=1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CP015');
        END;
    END IF;

    IF v_error_code IS NULL THEN
        IF each_rec.dealer_currency = 'USD' OR each_rec.currency = each_rec.dealer_currency THEN
            v_cp_in_dealer_curr := v_cp_in_usd;
        ELSE
            BEGIN
                SELECT CAST(factor * v_cp_in_usd AS NUMBER(19,2)) INTO v_cp_in_dealer_curr
                FROM currency_conversion_factor WHERE parent = (
                  SELECT id FROM currency_exchange_rate 
                  WHERE from_currency = 'USD' AND to_currency = each_rec.dealer_currency
                ) AND (SELECT repair_date FROM claim WHERE UPPER(claim_number) = UPPER(each_rec.claim_number)) 
                    BETWEEN from_date AND till_date 
                AND ROWNUM=1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CP016');
            END;
        END IF;
    END IF;

    IF v_error_code IS NULL AND NVL(UPPER(each_rec.override),'N') = 'N' THEN
        SELECT COUNT(*) INTO v_count FROM oem_part_replaced 
        WHERE id=each_rec.oem_part_replaced
            AND (material_cost_amt IS NULL OR (material_cost_amt = v_cp_in_dealer_curr
                AND material_cost_curr = each_rec.dealer_currency));
        IF v_count = 0 THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CP014');
        END IF;
    END IF;

    IF v_error_code IS NULL THEN
        UPDATE oem_part_replaced SET
            material_cost_amt = v_cp_in_dealer_curr,
            material_cost_curr = each_rec.dealer_currency,
            cost_price_per_unit_amt = v_cp_in_dealer_curr,
            cost_price_per_unit_curr = each_rec.dealer_currency
        WHERE id = each_rec.oem_part_replaced;
    END IF;

    IF v_error_code IS NULL THEN
        UPDATE stg_cost_price SET 
            upload_status = 'Y',
            upload_error = NULL
        WHERE id = each_rec.id;
    ELSE
        UPDATE stg_cost_price SET 
            error_status = 'N',
            error_code = v_error_code
        WHERE id = each_rec.id;
        UPDATE file_upload_mgt SET 
            success_records = success_records-1,
            error_records = error_records+1
        WHERE id = each_rec.file_upload_mgt_id;
    END IF;

EXCEPTION WHEN OTHERS THEN
    ROLLBACK;
    v_upload_error := SUBSTR(SQLERRM,0,3500);
    UPDATE stg_cost_price SET 
        upload_status = 'N',
        upload_error = v_upload_error
    WHERE id = each_rec.id;
END;

COMMIT;
END LOOP;

END;
/