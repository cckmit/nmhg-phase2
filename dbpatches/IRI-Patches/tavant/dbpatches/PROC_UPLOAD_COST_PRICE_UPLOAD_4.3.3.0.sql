--Purpose : Fix for TSESA-229 & TSESA-231
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

CURSOR uploaded_claims IS
    SELECT distinct claim_number,dealer_currency
    FROM stg_cost_price
    WHERE upload_status = 'Y';

v_cp_in_usd NUMBER(19,2);
v_cp_in_dealer_curr NUMBER(19,2);
v_count NUMBER;
v_error_code VARCHAR2(100);
v_upload_error VARCHAR2(4000) := NULL;
v_mapping_fraction NUMBER;

v_oemparts_lig NUMBER;
v_claimamount_lig NUMBER;
v_oemparts_cp NUMBER(19,2);

v_bu_name VARCHAR2(255);
v_cp_config VARCHAR2(255);

BEGIN

FOR each_rec IN all_rec LOOP
BEGIN
    v_cp_in_usd := NULL;
    v_cp_in_dealer_curr := NULL;
    v_error_code := NULL;
    v_mapping_fraction := 1;

    IF v_bu_name IS NULL THEN
        SELECT business_unit_info INTO v_bu_name
        FROM file_upload_mgt WHERE id=each_rec.file_upload_mgt_id AND ROWNUM=1;

        SELECT o.value INTO v_cp_config
        FROM config_param c,config_value v,config_param_option o
        WHERE c.name='costPriceConfiguration' AND c.id=v.config_param
            AND c.d_active=1 AND v.business_unit_info=v_bu_name
            AND v.d_active=1 AND v.config_param_option=o.id
            AND ROWNUM=1;
    END IF;

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

    BEGIN
        SELECT uom.mapping_fraction INTO v_mapping_fraction
        FROM oem_part_replaced opr,uom_mappings uom
        WHERE opr.id=each_rec.oem_part_replaced AND opr.uom_mapping IS NOT NULL
            AND opr.uom_mapping=uom.id;
        
        v_cp_in_dealer_curr := (v_cp_in_dealer_curr * v_mapping_fraction);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_mapping_fraction := 1;
    END;

    IF v_error_code IS NULL AND NVL(UPPER(each_rec.override),'N') = 'N' THEN
        IF LOWER(v_cp_config) = 'materialcost' THEN
            SELECT COUNT(*) INTO v_count FROM oem_part_replaced 
            WHERE id=each_rec.oem_part_replaced
                AND (material_cost_amt IS NULL OR (material_cost_amt = v_cp_in_dealer_curr
                    AND material_cost_curr = each_rec.dealer_currency));
        ELSE
            SELECT COUNT(*) INTO v_count FROM oem_part_replaced 
            WHERE id=each_rec.oem_part_replaced
                AND (cost_price_per_unit_amt IS NULL OR (cost_price_per_unit_amt = v_cp_in_dealer_curr
                    AND cost_price_per_unit_curr = each_rec.dealer_currency));
        END IF;
        IF v_count = 0 THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CP014');
        END IF;
    END IF;

    IF v_error_code IS NULL THEN
        IF LOWER(v_cp_config) = 'materialcost' THEN
            UPDATE oem_part_replaced SET
                material_cost_amt = (v_cp_in_dealer_curr),
                material_cost_curr = each_rec.dealer_currency
            WHERE id = each_rec.oem_part_replaced;
        ELSE
            UPDATE oem_part_replaced SET
                cost_price_per_unit_amt = (v_cp_in_dealer_curr),
                cost_price_per_unit_curr = each_rec.dealer_currency
            WHERE id = each_rec.oem_part_replaced;
        END IF;
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

FOR each_claim IN uploaded_claims LOOP
    v_oemparts_lig := NULL;
    v_claimamount_lig := NULL;
    v_oemparts_cp := NULL;

    BEGIN
        SELECT lig.id INTO v_oemparts_lig
        FROM claim c,line_item_groups ligs,line_item_group lig
        WHERE c.claim_number=each_claim.claim_number
            AND c.payment=ligs.for_payment AND ligs.line_item_groups=lig.id
            AND lig.name='Club Car Parts';
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_oemparts_lig := NULL;
    END;

    BEGIN
        SELECT lig.id INTO v_claimamount_lig
        FROM claim c,line_item_groups ligs,line_item_group lig
        WHERE c.claim_number=each_claim.claim_number
            AND c.payment=ligs.for_payment AND ligs.line_item_groups=lig.id
            AND lig.name='Claim Amount';
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_claimamount_lig := NULL;
    END;

    BEGIN
        IF LOWER(v_cp_config) = 'materialcost' THEN
            SELECT SUM(cost_price/mapping_fraction) INTO v_oemparts_cp
            FROM (
            SELECT (opr.material_cost_amt * opr.number_of_units) cost_price,
                CASE WHEN uom_mapping IS NULL THEN 1 
                ELSE (SELECT mapping_fraction FROM uom_mappings WHERE id=opr.uom_mapping)
                END mapping_fraction
            FROM claim c,service_information s,service_oemparts_replaced sopr,oem_part_replaced opr
            WHERE c.service_information=s.id AND s.service_detail=sopr.service
                AND sopr.oemparts_replaced=opr.id AND c.claim_number=each_claim.claim_number
                AND opr.material_cost_amt IS NOT NULL AND opr.number_of_units IS NOT NULL
                AND (opr.read_only IS NULL OR opr.read_only=0)
                AND (opr.shipped_by_oem IS NULL OR opr.shipped_by_oem=0)
            );
        ELSE
            SELECT SUM(cost_price/mapping_fraction) INTO v_oemparts_cp
            FROM (
            SELECT (opr.cost_price_per_unit_amt * opr.number_of_units) cost_price,
                CASE WHEN uom_mapping IS NULL THEN 1 
                ELSE (SELECT mapping_fraction FROM uom_mappings WHERE id=opr.uom_mapping)
                END mapping_fraction
            FROM claim c,service_information s,service_oemparts_replaced sopr,oem_part_replaced opr
            WHERE c.service_information=s.id AND s.service_detail=sopr.service
                AND sopr.oemparts_replaced=opr.id AND c.claim_number=each_claim.claim_number
                AND opr.material_cost_amt IS NOT NULL AND opr.number_of_units IS NOT NULL
                AND (opr.read_only IS NULL OR opr.read_only=0)
                AND (opr.shipped_by_oem IS NULL OR opr.shipped_by_oem=0)
            );
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_oemparts_cp := NULL;
    END;

    IF v_oemparts_lig IS NOT NULL AND v_oemparts_cp IS NOT NULL THEN
        UPDATE line_item_group_audit SET 
            accepted_cp_amt=v_oemparts_cp, 
            accepted_cp_curr=each_claim.dealer_currency
        WHERE for_line_item_grp = v_oemparts_lig
            AND list_index=(SELECT MAX(list_index) 
                FROM line_item_group_audit WHERE for_line_item_grp = v_oemparts_lig);
    END IF;

    IF v_claimamount_lig IS NOT NULL AND v_oemparts_cp IS NOT NULL THEN
        UPDATE line_item_group_audit SET 
            accepted_cp_amt=v_oemparts_cp, 
            accepted_cp_curr=each_claim.dealer_currency
        WHERE for_line_item_grp = v_claimamount_lig
            AND list_index=(SELECT MAX(list_index) 
                FROM line_item_group_audit WHERE for_line_item_grp = v_claimamount_lig);
    END IF;

    COMMIT;
END LOOP;

END;
/