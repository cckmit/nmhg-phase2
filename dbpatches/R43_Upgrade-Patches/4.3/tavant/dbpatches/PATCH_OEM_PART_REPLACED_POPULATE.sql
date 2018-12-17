--Purpose    : Populate OEM PART REPLACED either value is null or no record found.
--Author     : Manish kumar sinha
--Created On : 18-Oct-2010

update config_value
set config_param_option = (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'true')
where config_param=(select id from config_param where name = 'isPartsReplacedInstalledSectionVisible')
/
Insert into HUSS_PARTS_REPLACED_INSTALLED (
select HUSSPARTS_REPINSTALLED_SEQ.nextval, a.* from (
select distinct b.service,0, null from oem_part_replaced a, service_oemparts_replaced b 
where a.oem_replaced_parts is null
and a.id = b.oemparts_replaced
And not exists (select 1 from Huss_Parts_Replaced_Installed c where c.service_detail = B.Service)
) a)
/
COMMIT
/
DECLARE
CURSOR huss_install_parts
	IS
	select c.* 
	from oem_part_replaced a, service_oemparts_replaced b, HUSS_PARTS_REPLACED_INSTALLED c
	where a.oem_replaced_parts is null
	and a.id = b.oemparts_replaced
	and b.service = c.service_detail;

CURSOR oem_parts_replaced (p_service number)
	IS
	select a.*
	from oem_part_replaced a, service_oemparts_replaced b
	where a.oem_replaced_parts is null
	and a.id = b.oemparts_replaced
	and b.service = p_service;
	
BEGIN

 FOR each_huss_install_part IN huss_install_parts LOOP
 
   BEGIN
	
	for each_oem_part_replaced IN oem_parts_replaced(each_huss_install_part.SERVICE_DETAIL) LOOP
		
		BEGIN
	
		INSERT
		INTO INSTALLED_PARTS
		  (
			ID,
			PART_NUMBER,
			COST_PRICE_PER_UNIT_AMT,
			COST_PRICE_PER_UNIT_CURR,
			NUMBER_OF_UNITS,
			PRICE_PER_UNIT_AMT,
			PRICE_PER_UNIT_CURR,
			INVENTORY_LEVEL,
			MATERIAL_COST_AMT,
			MATERIAL_COST_CURR,
			DESCRIPTION,
			PRICE,
			IS_HUSSMAN_PART,
			OEM_PARTS_INSTALLED,
			NON_OEM_PARTS_INSTALLED,
			ITEM,
			D_CREATED_ON,
			D_INTERNAL_COMMENTS,
			D_UPDATED_ON,
			D_LAST_UPDATED_BY,
			STATUS,
			D_CREATED_TIME,
			D_ACTIVE,
			D_UPDATED_TIME,
			READ_ONLY,
			OEM_DEALER_PART_REPLACED,
			SUPPLIER_ITEM,
			SHIPPED_BY_OEM
		  )
		  VALUES
		  (
			SEQ_PartReplaced.NEXTVAL,
			NULL,
			NULL,
			NULL,
			each_oem_part_replaced.number_of_units,
			each_oem_part_replaced.price_per_unit_amt,
			each_oem_part_replaced.price_per_unit_curr,
			0,
			NULL,
			NULL,
			NULL,
			NULL,
			'1',
			each_huss_install_part.id,
			NULL,
			each_oem_part_replaced.item_ref_item,
			sysdate,
			'4.3 Upgrade',
			sysdate,
			each_oem_part_replaced.D_LAST_UPDATED_BY,
			each_oem_part_replaced.status,
			CURRENT_TIMESTAMP,
			each_oem_part_replaced.D_ACTIVE,
			CURRENT_TIMESTAMP,
			each_oem_part_replaced.read_only,
			NULL,
			NULL,
			0
		  );		  
	
		update oem_part_replaced
		set OEM_REPLACED_PARTS = each_huss_install_part.id
		where id = each_oem_part_replaced.id;
			
	   END;
	END LOOP;
	END;
  END LOOP;
  COMMIT;
  EXCEPTION WHEN OTHERS THEN
	ROLLBACK;
END;
/
DELETE from service_oemparts_replaced a
where EXISTS (
SELECT 1 from oem_part_replaced b
where a.oemparts_replaced = b.id
and b.OEM_REPLACED_PARTS is not null
)
/
COMMIT
/