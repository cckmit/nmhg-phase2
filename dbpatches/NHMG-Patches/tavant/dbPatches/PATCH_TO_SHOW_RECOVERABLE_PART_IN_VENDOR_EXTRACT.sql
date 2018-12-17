-- Purpose    : Patch for showing recoverable_part_number in vendor_recovery_extract 
-- Courtesy   : Pracher
-- Created On : 10-Apr-2014

create or replace FUNCTION  "GET_REPLACED_PARTS" (p_service NUMBER) RETURN VARCHAR2 IS

V_RETURN_STR	VARCHAR2(1000);

cursor all_parts (p_service_id number) is   
   select i.item_number
    from huss_parts_replaced_installed sopr,oem_part_replaced opr,item i
    where sopr.service_detail = p_service_id and sopr.id = opr.oem_replaced_parts
        and opr.item_ref_item = i.id ;

BEGIN
    V_RETURN_STR := null;

    for rec in all_parts (p_service) loop
        if v_return_str is null then
            v_return_str := rec.item_number ; 
        else 
            v_return_str := v_return_str || ', ' || rec.item_number;
        end if ;
    END LOOP; 

RETURN V_RETURN_STR;

END;