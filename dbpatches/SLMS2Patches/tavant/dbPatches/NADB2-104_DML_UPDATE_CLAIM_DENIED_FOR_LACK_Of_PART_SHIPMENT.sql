--PURPOSE    : PATCH_TO_UPDATE_CLAIM_DENIED_FOR_LACK_Of_PART_SHIPMENT_UNDER_EVENT_STATE_TABLE
--AUTHOR     : Sumesh kumar.R
--CREATED ON : 21-MAR-2014
update event_state set display_name='Claim Denied For Lack Of Part Shipment' where display_name='Claim Denied For Lack Of Parts'
/
update event_state set name='CLAIM_DENIED_FOR_LACK_Of_PART_SHIPMENT' where name='CLAIM_DENIED_FOR_LACK_OF_PART_RETURNS'
/
commit
/