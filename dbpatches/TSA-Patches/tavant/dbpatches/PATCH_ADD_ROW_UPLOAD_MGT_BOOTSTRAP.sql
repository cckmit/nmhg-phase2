--Author     : Bharath kumar
--Created On : 14-4-2010



Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (1,'installBaseUpload','Install Base Upload','Upload Scheme - Install Base Upload','./pages/secure/admin/upload/templates/Template-InstallBaseUpload.xls','STG_INSTALL_BASE',null,'UPLOAD_INSTALL_BASE_VALIDATION','UPLOAD_INSTALL_BASE_UPLOAD',40,null,6,1,null)
/
Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (2,'copyJobCodeFaultFound','Copy Job Codes And Fault Found','Upload Scheme - Copy Job Codes And Fault Found','./pages/secure/admin/upload/templates/Template-CopyJobCodesAndFaultFound.xls','STG_COPY_JOB_CODE_FF',null,'UPLOAD_COPY_JC_FF_VALIDATION','UPLOAD_COPY_JC_FF_UPLOAD',7,null,6,1,null)
/
Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (3,'uploadJobCodes','Upload Job Codes','Upload Scheme - Upload Job Codes','./pages/secure/admin/upload/templates/Template-UploadJobCodes.xls','STG_JOB_CODE',null,'UPLOAD_JOB_CODE_VALIDATION','UPLOAD_JOB_CODE_UPLOAD',14,'UPLOAD_JOB_CODE_POPULATION',6,1,null)
/
Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (4,'draftWarrantyClaims','Draft Warranty Claims','Upload Scheme - Draft Warranty Claims',	'./pages/secure/admin/upload/templates/Template-DraftClaimUpload.xls','STG_DRAFT_CLAIM','','UPLOAD_DRAFT_CLAIM_VALIDATION','',40,'',10,0,'')
/
Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (5,'partSourceHistory','Part Source History Information','Upload Scheme - Part Source History Information','./pages/secure/admin/upload/templates/Template-PartSourceHistory.xls','STG_PART_SRC_HIST_UPLD',null,'UPLOAD_PART_SRC_HIST_VLD','UPLOAD_PART_SRC_HIST_UPLD',7,null,6,1,null)
/
Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (6,'itemUpload','Item Upload','Upload Scheme - Item Upload','./pages/secure/admin/upload/templates/Template-ItemUpload.xls','ITEM_STAGING',null,'ITEM_Validation','ITEM_Upload',15,null,6,1,null)
/
Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (7,'customerUpload','Customer Upload','Upload Scheme - Customer Upload','./pages/secure/admin/upload/templates/Template-CustomerUpload.xls','CUSTOMER_STAGING',null,'CUSTOMER_VALIDATION_PROCEDURE','CUSTOMER_UPLOAD_PROCEDURE',16,null,6,1,null)
/
Insert into upload_mgt (ID,NAME_OF_TEMPLATE,NAME_TO_DISPLAY,DESCRIPTION,TEMPLATE_PATH,STAGING_TABLE,STAGING_PROCEDURE,VALIDATION_PROCEDURE,UPLOAD_PROCEDURE,COLUMNS_TO_CAPTURE,POPULATION_PROCEDURE,CONSUME_ROWS_FROM,HEADER_ROW_TO_CAPTURE,BACKUP_TABLE) values (8,'costPriceUpload','Cost Price Upload','Cost Price Upload','./pages/secure/admin/upload/templates/Template-CostPriceUpload.xls','STG_COST_PRICE',null,'UPLOAD_COST_PRICE_VALIDATION','UPLOAD_COST_PRICE_UPLOAD',7,null,6,1,'stg_cost_price_bkp')
/
COMMIT
/