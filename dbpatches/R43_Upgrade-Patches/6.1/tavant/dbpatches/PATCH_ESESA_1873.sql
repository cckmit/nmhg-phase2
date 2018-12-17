--Purpose : Fix for ESESA-1873. Populating the unserialized info for serialized claims for predefined search.
--Author  : anjani.kumar
--Date    : 11/OCT/2011

update claimed_item c set model_ref_for_unszed = (select model from item where id in (select of_type from inventory_item where id = c.item_ref_inv_item)), d_updated_on=sysdate,d_updated_time=sysdate, d_internal_comments = 'ESESA-1873 :: '|| d_internal_comments  where model_ref_for_unszed is null and item_ref_inv_item is not null
/
commit
/