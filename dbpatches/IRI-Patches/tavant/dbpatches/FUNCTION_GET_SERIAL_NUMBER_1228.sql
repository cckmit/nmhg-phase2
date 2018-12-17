--Purpose : replaced parts for vendor recovery extract
--Author  : raguram.d
--Date    : 28/Dec/09

create or replace FUNCTION GET_SERIAL_NUMBER (P_CLAIMID NUMBER) RETURN VARCHAR2 IS

V_RETURN_STR	VARCHAR2(255);
v_count 	number;

cursor all_inv(p_claim_id number) is
    SELECT SERIAL_NUMBER FROM INVENTORY_ITEM WHERE ID 
	IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = p_claim_id);

BEGIN

    select count(*) into v_count from claimed_item where claim=P_CLAIMID and item_ref_szed=1 ;
    if v_count > 0 then
        for rec in all_inv(P_CLAIMID) loop
            if v_return_str is null then
                v_return_str := rec.serial_number ; 
            else 
                v_return_str := v_return_str || ', ' || rec.serial_number;
            end if ;
        END LOOP; 
    else
        SELECT ITEM_REFERENCE_UNSZD_SL_NO INTO V_RETURN_STR FROM CLAIMED_ITEM WHERE CLAIM = P_CLAIMID AND ROWNUM = 1;
    end if;

RETURN V_RETURN_STR;

END;
/