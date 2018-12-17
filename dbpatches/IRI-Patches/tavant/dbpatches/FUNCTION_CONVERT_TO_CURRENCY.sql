--Purpose : Convert to currency based on claim repair date for vendor recovery extract
--Author  : raghuram.d
--Date    : 11/Jan/2010

CREATE OR REPLACE FUNCTION convert_to_currency (
        p_claim_number VARCHAR2,
        p_amount NUMBER,
        p_from_curr VARCHAR2,
        p_to_curr VARCHAR2)
RETURN NUMBER IS

v_amount_usd NUMBER(19,2);
v_amount NUMBER(19,2);
v_error NUMBER(1);

BEGIN

v_error := 0;

IF p_from_curr = 'USD' OR p_from_curr = p_to_curr THEN
    SELECT CAST(p_amount AS NUMBER(19,2)) INTO v_amount_usd FROM DUAL;
ELSE
    BEGIN
        SELECT CAST(factor * CAST(p_amount AS NUMBER(19,2)) AS NUMBER(19,2)) INTO v_amount_usd
        FROM currency_conversion_factor WHERE parent = (
          SELECT id FROM currency_exchange_rate 
          WHERE from_currency = p_from_curr AND to_currency = 'USD'
        ) AND (SELECT repair_date FROM claim WHERE UPPER(claim_number) = UPPER(p_claim_number)) 
            BETWEEN from_date AND till_date 
        AND ROWNUM=1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_error := 1;
    END;
END IF;

IF v_error = 0 THEN
    IF p_to_curr = 'USD' OR p_from_curr = p_to_curr THEN
        v_amount := v_amount_usd;
    ELSE
        BEGIN
            SELECT CAST(factor * v_amount_usd AS NUMBER(19,2)) INTO v_amount
            FROM currency_conversion_factor WHERE parent = (
              SELECT id FROM currency_exchange_rate 
              WHERE from_currency = 'USD' AND to_currency = p_to_curr
            ) AND (SELECT repair_date FROM claim WHERE UPPER(claim_number) = UPPER(p_claim_number)) 
                BETWEEN from_date AND till_date 
            AND ROWNUM=1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            v_error := 1;
        END;
    END IF;
END IF;

IF v_error = 1 THEN
    v_amount := -1;
END IF;

RETURN v_amount;

END;
/