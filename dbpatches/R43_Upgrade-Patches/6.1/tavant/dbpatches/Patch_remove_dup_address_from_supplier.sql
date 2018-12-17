--Name:Joseph
--Date : 22 June 2011
--impact : Removes duplicate addresses for supplier

DECLARE
V_MIN_SUP NUMBER := 0;
V_MIN_LOC NUMBER := 0;
c number :=0;

BEGIN


FOR I IN 
(
	SELECT COUNT(A.ID) CNT_LOC,UPPER(B.CODE) code ,C.ADDRESS_LINE1,C.ADDRESS_LINE2,C.CITY,C.COUNTRY,C.STATE,C.ZIP_CODE,A.SUPPLIER
	FROM SUPPLIER_LOCATIONS A , LOCATION B , ADDRESS C
	WHERE A.LOCATIONS = B.ID
	AND B.ADDRESS = C.ID
	AND B.D_ACTIVE = 1
	and c.d_active = 1
	--AND B.CODE = 'HQ01'
	GROUP BY upper(B.CODE),C.ADDRESS_LINE1,C.ADDRESS_LINE2,C.CITY,C.COUNTRY,C.STATE,C.ZIP_CODE,a.supplier
	HAVING COUNT(*) > 1
)
LOOP

	BEGIN                  ---> getting the sup whose address con incides with that of the supplier
	SELECT A.ID ,A.LOCATIONS into v_Min_sup,V_MIN_LOC
	FROM SUPPLIER_LOCATIONS A , LOCATION B , ADDRESS C , party d
	WHERE A.LOCATIONS = B.ID
	AND B.ADDRESS = C.ID
	AND B.D_ACTIVE = 1
	AND C.D_ACTIVE = 1
	and nvl(upper(B.CODE),'-99')               = nvl(i.CODE ,'-99')
	AND nvl(C.ADDRESS_LINE1,'-99')      = nvl(i.ADDRESS_LINE1,'-99')
	AND nvl(C.ADDRESS_LINE2,'-99')      = nvl(i.ADDRESS_LINE2,'-99')
	and nvl(C.CITY,'-99')               = nvl(i.CITY,'-99')
	AND nvl(C.COUNTRY,'-99')           = nvl(i.COUNTRY,'-99')
	and nvl(C.STATE,'-99')              = nvl(i.STATE,'-99')
	AND nvl(C.ZIP_CODE,'-99')           = nvl(i.ZIP_CODE,'-99')
	AND NVL(A.SUPPLIER,-99)           = NVL(I.SUPPLIER,-99)
	AND A.SUPPLIER = D.ID
	AND d.ADDRESS = C.ID
	and rownum = 1;
	EXCEPTION
	WHEN OTHERS THEN 
		begin
		SELECT A.ID ,A.LOCATIONS into v_Min_sup,V_MIN_LOC      --->getting any address since the supplier address does not conincide
		FROM SUPPLIER_LOCATIONS A , LOCATION B , ADDRESS C
		WHERE A.LOCATIONS = B.ID
		AND B.ADDRESS = C.ID
		AND B.D_ACTIVE = 1
		AND C.D_ACTIVE = 1
		and nvl(upper(B.CODE),'-99')               = nvl(i.CODE ,'-99')
		AND nvl(C.ADDRESS_LINE1,'-99')      = nvl(i.ADDRESS_LINE1,'-99')
		AND nvl(C.ADDRESS_LINE2,'-99')      = nvl(i.ADDRESS_LINE2,'-99')
		and nvl(C.CITY,'-99')               = nvl(i.CITY,'-99')
		AND nvl(C.COUNTRY,'-99')           = nvl(i.COUNTRY,'-99')
		and nvl(C.STATE,'-99')              = nvl(i.STATE,'-99')
		AND nvl(C.ZIP_CODE,'-99')           = nvl(i.ZIP_CODE,'-99')
		AND NVL(A.SUPPLIER,-99)           = NVL(I.SUPPLIER,-99)
		and rownum = 1;
			EXCEPTION
			WHEN OTHERS THEN 
			v_Min_sup := NULL;
			V_MIN_LOC := NULL;
			NULL;
		end;
	end;

	If v_Min_sup is not null and V_MIN_LOC is not null then

			for k in
			(
			SELECT a.id sup_id, b.id LOC_ID ,c.id add_id
			FROM SUPPLIER_LOCATIONS A , LOCATION B , ADDRESS C
			WHERE A.LOCATIONS = B.ID
			AND B.ADDRESS = C.ID
			AND B.D_ACTIVE = 1
			AND C.D_ACTIVE = 1
			and nvl(upper(B.CODE),'-99')               = nvl(i.CODE ,'-99')
			AND nvl(C.ADDRESS_LINE1,'-99')      = nvl(i.ADDRESS_LINE1,'-99')
			AND nvl(C.ADDRESS_LINE2,'-99')      = nvl(i.ADDRESS_LINE2,'-99')
			and nvl(C.CITY,'-99')               = nvl(i.CITY,'-99')
			AND nvl(C.COUNTRY,'-99')           = nvl(i.COUNTRY,'-99')
			and nvl(C.STATE,'-99')              = nvl(i.STATE,'-99')
			AND nvl(C.ZIP_CODE,'-99')           = nvl(i.ZIP_CODE,'-99')
			AND NVL(A.SUPPLIER,-99)           = NVL(I.SUPPLIER,-99)
			AND A.ID <> V_MIN_SUP
			)
			loop

				begin
				UPDATE CONTRACT SET LOCATION = V_MIN_LOC WHERE LOCATION = K.LOC_ID;   --> updating contract with  surviving location
				EXCEPTION
				WHEN OTHERS THEN NULL ;
				end;

				BEGIN
				DELETE FROM SUPPLIER_LOCATIONS WHERE ID = K.SUP_ID;  -->del dup supplier location
				EXCEPTION
				WHEN OTHERS THEN NULL ;
				end;

				BEGIN
				DELETE FROM LOCATION WHERE ID = K.LOC_ID;  ---> del dup locations
				EXCEPTION
				When OTHERS THEN 
				--DBMS_OUTPUT.PUT_LINE('loc id >' || K.LOc_ID || SQLERRM);
				NULL;
				END;

				BEGIN
				DELETE FROM address where id = k.add_id;  --> del dup addresses
				EXCEPTION
				WHEN OTHERS THEN 
				NULL;
				--DBMS_OUTPUT.PUT_LINE('Address id >' || K.ADD_ID || SQLERRM);
				--raise;
				END;

			end loop;

	end if; --end if for v_Min_sup,V_MIN_LOC being not null

END LOOP ;
commit;
END;