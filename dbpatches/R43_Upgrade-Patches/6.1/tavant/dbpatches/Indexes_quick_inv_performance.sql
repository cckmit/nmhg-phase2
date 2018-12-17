--Name : Prasad
--Impact : To improve performance of the inventory search
create index DCAP_CLAIM_INV_ITEM_IDX on DCAP_CLAIM("INVENTORY_ITEM")
/
create index CAMPAIGN_NOTIFICATION_ITEM_IDX on CAMPAIGN_NOTIFICATION("ITEM")
/