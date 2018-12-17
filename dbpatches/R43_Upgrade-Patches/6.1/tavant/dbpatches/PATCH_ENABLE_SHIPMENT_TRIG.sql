--PURPOSE    : To ENABLE trigger on the shipment table after migration
--AUTHOR     : Joseph Tharakan
--CREATED ON : 12-MAY-11
--IMPACT     : ENABLE trigger

ALTER TRIGGER GENERATE_SHIPMENT_NUMBER ENABLE
/
COMMIT
/