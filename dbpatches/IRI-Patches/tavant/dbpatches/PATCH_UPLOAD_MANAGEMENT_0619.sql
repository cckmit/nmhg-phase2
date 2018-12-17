--Purpose    : Fixes for PartSource history & Customer upload
--Author     : raghuram.d
--Created On : 19-Jun-09

update upload_mgt set template_path='.\pages\secure\admin\upload\templates\Template-InstallBaseUpload.xls' where name_of_template='installBaseUpload'
/
BEGIN
   create_upload_error('draftWarrantyClaims','en_US','REPLACED IR PARTS QUANTITY','DC054','One or more Replaced IR Parts Quantity is not valid');
   create_upload_error('draftWarrantyClaims','en_US','MISC PARTS QUANTITY','DC055','One or more Misc Parts Quantity is not valid');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS','DC056','Invalid format for Replcaed Non IR Parts');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS QUANTITY','DC057','Invalid format for Replaced Non IR Parts Quantity');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS QUANTITY','DC058','Number of quantity values do not match the number of Replaced Non IR Parts');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS QUANTITY','DC059','One or more Replaced Non IR Parts Quantity is not valid');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS PRICE','DC060','Invalid format for Replaced Non IR Parts Price');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS PRICE','DC061','Number of price values do not match the number of Replaced Non IR Parts');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS DESC','DC062','Invalid format for Replaced Non IR Parts Desc');
   create_upload_error('draftWarrantyClaims','en_US','REPLACED NON IR PARTS DESC','DC063','Number of desc values do not match the number of Replaced Non IR Parts');
   create_upload_error('draftWarrantyClaims','en_US','SERIAL NUMBER','DC064','Invalid Serial Number for the Campaign Code');
   create_upload_error('draftWarrantyClaims','en_US','CAMPAIGN CODE','DC065','Invalid Campaign Code');
   create_upload_error('draftWarrantyClaims','en_US','CLAIM TYPE','DC066','Claim Type not allowed');
   create_upload_error('uploadJobCodes','en_US','LABOR STANDARD MINUTES','JC020','Labor Standard Minutes is not valid');
END;
/
UPDATE upload_mgt SET 
    population_procedure = NULL,
    columns_to_capture = 7
WHERE name_of_template = 'partSourceHistory'
/
BEGIN
    create_upload_error('partSourceHistory','en_US','BUSINESS UNIT NAME','PS001','Business Unit Name is not specified');
    create_upload_error('partSourceHistory','en_US','BUSINESS UNIT NAME','PS002','Business Unit Name is not valid');
    create_upload_error('partSourceHistory','en_US','ITEM NUMBER','PS003','Item Number is not specified');
    create_upload_error('partSourceHistory','en_US','ITEM NUMBER','PS004','Item Number is not valid');
    create_upload_error('partSourceHistory','en_US','SUPPLIER NUMBER','PS005','Supplier Number is not specified');
    create_upload_error('partSourceHistory','en_US','SUPPLIER NUMBER','PS006','Supplier Number is not valid');
    create_upload_error('partSourceHistory','en_US','FROM DATE','PS007','From Date is not specified');
    create_upload_error('partSourceHistory','en_US','FROM DATE','PS008','From Date is not valid');
    create_upload_error('partSourceHistory','en_US','TO DATE','PS009','To Date is not specified');
    create_upload_error('partSourceHistory','en_US','TO DATE','PS010','To Date is not valid');
    create_upload_error('partSourceHistory','en_US','CATALOG NAME','PS011','Catalog Name is not specified');
    create_upload_error('partSourceHistory','en_US','CATALOG NAME','PS012','Catalog Name is not valid');
    create_upload_error('partSourceHistory','en_US','ACTION','PS013','Action is not specified');
    create_upload_error('partSourceHistory','en_US','ACTION','PS014','Action is not valid');
    create_upload_error('partSourceHistory','en_US','ACTION','PS015','Action Failed - period overlaps with an existing mapping');
    create_upload_error('partSourceHistory','en_US','ACTION','PS016','Action Failed - mapping for the period does not exist');
    create_upload_error('partSourceHistory','en_US','TO DATE','PS017','To Date is before From Date');
END;
/
update upload_mgt set columns_to_capture=16 where name_of_template='customerUpload'
/
alter table customer_staging rename column address1 to address
/
BEGIN
    create_upload_error('customerUpload','en_US','BUSINESS UNIT','CU001','Business Unit is not specified');
    create_upload_error('customerUpload','en_US','BUSINESS UNIT','CU002','Business Unit is not valid');
    create_upload_error('customerUpload','en_US','CUSTOMER NUMBER','CU003','Customer Number is not specified');
    create_upload_error('customerUpload','en_US','CUSTOMER NAME','CU004','Cusotmer Name is not specified');
    create_upload_error('customerUpload','en_US','EMAIL','CU005','Email is not specified');
    create_upload_error('customerUpload','en_US','ADDRESS','CU006','Address is not specified');
    create_upload_error('customerUpload','en_US','CITY','CU007','City is not specified');
    create_upload_error('customerUpload','en_US','COUNTRY','CU008','Country is not specified');
    create_upload_error('customerUpload','en_US','COUNTRY','CU009','Country is not valid');
    create_upload_error('customerUpload','en_US','STATE','CU010','State is not specified');
    create_upload_error('customerUpload','en_US','STATE','CU011','State is not valid');
    create_upload_error('customerUpload','en_US','CITY','CU012','City is not valid');
    create_upload_error('customerUpload','en_US','POSTAL CODE','CU013','Postal Code is not specified');
    create_upload_error('customerUpload','en_US','POSTAL CODE','CU014','Postal Code is not valid');
    create_upload_error('customerUpload','en_US','CUSTOMER TYPE','CU015','Customer Type is not specified');
    create_upload_error('customerUpload','en_US','CUSTOMER TYPE','CU016','Customer Type is not valid');
    create_upload_error('customerUpload','en_US','CURRENCY','CU017','Currency is not specified');
    create_upload_error('customerUpload','en_US','CURRENCY','CU018','Currency is not valid');
    create_upload_error('customerUpload','en_US','STATUS','CU019','Status is not specified');
    create_upload_error('customerUpload','en_US','STATUS','CU020','Status is not valid');
    create_upload_error('customerUpload','en_US','UPDATES','CU021','Updates is not valid');
    create_upload_error('customerUpload','en_US','CUSTOMER NUMBER','CU022','Customer Number is repeated');
    create_upload_error('customerUpload','en_US','CUSTOMER NUMBER','CU023','Customer Number already exists');
    create_upload_error('customerUpload','en_US','CUSTOMER NUMBER','CU024','Customer Number does not exist');
END;
/
COMMIT
/