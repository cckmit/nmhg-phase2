--Purpose    : Fix for SI bug TWMS4.3U-407.
--Created On : Apr 15,2011
--Created By : Prabhu R
--Comment	 : Change the DB link to the appropriate value for ex tg_shipment@toiriup03 change to which is needed

UPDATE part_return_action a
SET a.shipment_id =
  (SELECT b.id FROM tg_shipment@toiriup03 b WHERE b.old_43_id = a.shipment_id
  )
WHERE LENGTH(id)   > 13
AND a.shipment_id IS NOT NULL
/
COMMIT
/