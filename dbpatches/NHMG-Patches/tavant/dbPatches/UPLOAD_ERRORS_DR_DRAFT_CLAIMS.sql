delete from upload_error e 
where not exists (select 1 from upload_mgt_upload_errors ue where ue.upload_errors=e.id)
  and not exists (select 1 from i18nupload_error_text t where t.upload_error=e.id)
/
BEGIN
 --Delivery Report
  create_upload_error('warrantyRegistrations','en_US','CONTRACT CODE','WR061','Contract Code is not specified');
  create_upload_error('warrantyRegistrations','en_US','CONTRACT CODE','WR062','Contract Code is not valid');
  create_upload_error('warrantyRegistrations','en_US','MAINTENANCE CONTRACT','WR063','Maintenance Contract is not specified');
  create_upload_error('warrantyRegistrations','en_US','MAINTENANCE CONTRACT','WR064','Maintenance Contract is not valid');
  create_upload_error('warrantyRegistrations','en_US','INDUSTRY CODE','WR065','Industry Code is not specified');
  create_upload_error('warrantyRegistrations','en_US','INDUSTRY CODE','WR066','Industry Code is not valid');
  create_upload_error('warrantyRegistrations','en_US','SERIAL NUMBER','WR0060','Unit has already been registered');
 --Draft Claims
  create_upload_error('draftWarrantyClaims','en_US','REPAIR START DATE','DC108','Repair Start Date format is not valid');
  create_upload_error('draftWarrantyClaims','en_US','REPAIREND DATE','DC109','Repair End Date is before Repair Start Date');
  create_upload_error('draftWarrantyClaims','en_US','HOURS ON TRUCK WHEN PART INSTALLED','DC110','Hours on Truck when part Installed is not specified');
  create_upload_error('draftWarrantyClaims','en_US','HOURS ON TRUCK WHEN PART INSTALLED','DC111','Hours on Truck when part Installed is not valid');
  create_upload_error('draftWarrantyClaims','en_US','BRAND','DC112','Brand specified is not valid');
  create_upload_error('draftWarrantyClaims','en_US','AUTHORIZATION NUMBER','DC113','Authorization Number is not specified');
  create_upload_error('draftWarrantyClaims','en_US','REMOVED OEM PARTS','DC114','Removed OEM Parts are not valid Brand Parts');
  create_upload_error('draftWarrantyClaims','en_US','PART ITEM NUMBER','DC115','Part Item Number is not a valid Brand Part');
END;
/
commit
/