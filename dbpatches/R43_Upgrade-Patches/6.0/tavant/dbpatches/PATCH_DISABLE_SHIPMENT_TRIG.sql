--PURPOSE    : To disable trigger on the shipment table during migration
--AUTHOR     : Joseph Tharakan
--CREATED ON : 12-MAY-11
--IMPACT     : Disables trigger

ALTER TRIGGER GENERATE_SHIPMENT_NUMBER DISABLE
/
COMMIT
/