--PURPOSE    : PATCH FOR ADDING NEW COLUMNS TO COVERAGE TERMS FOR EWP LOGIC
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 14-JUL-09

ALTER TABLE Policy_Definition ADD months_frm_shipment_ewp number(19,0)
/
ALTER TABLE Policy_Definition ADD months_frm_delivery_ewp number(19,0)
/
update policy_definition set months_frm_delivery_ewp=0, months_frm_shipment_ewp=0 where warranty_type='EXTENDED'
/
COMMIT
/