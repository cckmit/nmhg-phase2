--PURPOSE    : Index for WPRA 
--AUTHOR     : Saibal
--CREATED ON : 09-SEP-13


create index oem_part_replaced_wpra_idx on oem_part_replaced(wpra)
/
create index claim_audit_service_info_idx on claim_audit(service_information)
/
