--PURPOSE    : Insert Role Permission mapping
--AUTHOR     : Chetan
--CREATED ON : 07-MAR-2014
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='bUConfigurationsSetup'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='BUConfigurations'),'BUConfigurations:bUConfigurationsSetup:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='claimTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='ClaimSubmissionandProcessing'),'ClaimSubmissionandProcessing:claimTab:update' from role where name in ('baserole','processor','dsm','dsmAdvisor','recoveryProcessor','cpAdvisor','dealerWarrantyAdmin','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='createWarrantyClaimDealer'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='ClaimSubmissionandProcessing'),'ClaimSubmissionandProcessing:createWarrantyClaimDealer:update' from role where name in ('dealerWarrantyAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='createWarrantyClaimProcessor'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='ClaimSubmissionandProcessing'),'ClaimSubmissionandProcessing:createWarrantyClaimProcessor:update' from role where name in ('dealerWarrantyAdmin','processor')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='defineClaimSearchQuery'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='ClaimSubmissionandProcessing'),'ClaimSubmissionandProcessing:defineClaimSearchQuery:update' from role where name in ('baserole','processor','dsm','dsmAdvisor','recoveryProcessor','admin','cpAdvisor','dealerWarrantyAdmin','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='multiClaimMaintenance'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='ClaimSubmissionandProcessing'),'ClaimSubmissionandProcessing:multiClaimMaintenance:update' from role where name in ('admin','processor','inventoryAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='preDefinedClaimSearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='ClaimSubmissionandProcessing'),'ClaimSubmissionandProcessing:preDefinedClaimSearch:update' from role where name in ('baserole','processor','dsm','dsmAdvisor','recoveryProcessor','admin','cpAdvisor','dealerWarrantyAdmin','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='fieldModificationsTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='FieldModifications'),'FieldModifications:fieldModificationsTab:update' from role where name in ('admin','processor','dsmAdvisor','recoveryProcessor','dsm','recoveryProcessor','dealerWarrantyAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='pendingFieldModificationUpdate'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='FieldModifications'),'FieldModifications:pendingFieldModificationUpdate:update' from role where name in ('internalUserAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='preDefinedFPISearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='FieldModifications'),'FieldModifications:preDefinedFPISearch:update' from role where name in ('admin','processor','dsmAdvisor','recoveryProcessor','dsm','recoveryProcessor','dealerWarrantyAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='submitFieldModificationClaim'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='FieldModifications'),'FieldModifications:submitFieldModificationClaim:update' from role where name in ('dealerWarrantyAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='inventoryTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Inventory'),'Inventory:inventoryTab:update' from role where name in ('inventorysearch','inventorylisting','inventoryAdmin','admin','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='deliveryReport'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Inventory'),'Inventory:deliveryReport:update' from role where name in ('dealerSalesAdministration','inventoryAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='defineInventorySearchQuery'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Inventory'),'Inventory:defineInventorySearchQuery:update' from role where name in ('inventorysearch','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='preDefinedInventorySearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Inventory'),'Inventory:preDefinedInventorySearch:update' from role where name in ('inventorysearch','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='stock/Retail'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Inventory'),'Inventory:stock/Retail:update' from role where name in ('inventorylisting','admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='retailsearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Inventory'),'Inventory:retailsearch:update' from role where name in ('inventorysearch','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='stocksearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Inventory'),'Inventory:stocksearch:update' from role where name in ('inventorysearch','readOnly')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='partReturnTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='PartReturnManagement'),'PartReturnManagement:partReturnTab:update' from role where name in ('processor','admin','receiver','sra','partshipper','dsmAdvisor','recoveryProcessor','inspector','dealerWarrantyAdmin','cevaProcessor')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='definePartReturnSearchQuery'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='PartReturnManagement'),'PartReturnManagement:definePartReturnSearchQuery:update' from role where name in ('receiverLimitedView','inspectorLimitedView')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='preDefinedPartReturnsSearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='PartReturnManagement'),'PartReturnManagement:preDefinedPartReturnsSearch:update' from role where name in ('receiverLimitedView','inspectorLimitedView')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='reportTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Reports'),'Reports:reportTab:update' from role where name in ('dealerWarrantyAdmin','baserole','processor','dsm','dsmAdvisor','recoveryProcessor','admin','supplier')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageUploads/Download'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='Reports'),'Reports:manageUploads/Download:update' from role
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='warrantyAdminTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:warrantyAdminTab:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='additionalAttributes'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:additionalAttributes:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='costCategoryConfiguration'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:costCategoryConfiguration:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='createCustomer'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:createCustomer:update' from role where name in ('admin','processor','dsmAdvisor','recoveryProcessor','recoveryProcessor','dealerSalesAdministration')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='createLabelsforinventory'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:createLabelsforinventory:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='dataManagement'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:dataManagement:update' from role where name in ('sysAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='dealerGroups'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:dealerGroups:update' from role where name in ('dealer','baserole','processor','dsm','dsmAdvisor','recoveryProcessor','admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='listModels'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:listModels:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='listOfValues'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:listOfValues:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='maintainUOMMappings'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:maintainUOMMappings:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageAdditionalLabour'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageAdditionalLabour:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageAlarmCode'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageAlarmCode:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageBusinessCondition'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageBusinessCondition:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageBusinessConfigurations'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageBusinessConfigurations:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageBusinessRuleGroups'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageBusinessRuleGroups:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageBusinessRule'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageBusinessRule:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageClaimPayment'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageClaimPayment:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageCustomReports'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageCustomReports:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageFailureStructure'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageFailureStructure:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageFlatRates'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageFlatRates:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageFreight/Shippers'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageFreight/Shippers:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageGroups'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageGroups:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageInclusiveJobCodes'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageInclusiveJobCodes:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageLaborSplit'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageLaborSplit:update' from role where name in ('dealer','baserole','processor','dsm','dsmAdvisor','recoveryProcessor','admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageLOASchemes'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageLOASchemes:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageUserAvailability'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageUserAvailability:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageWarehouses'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageWarehouses:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='manageWarrantyPolicies'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:manageWarrantyPolicies:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='miscellaneousParts'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:miscellaneousParts:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='partReturnConfiguration'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:partReturnConfiguration:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='viewLabels'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:viewLabels:update' from role where name in ('admin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='viewSupplier'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='WarrantyAdmin'),'WarrantyAdmin:viewSupplier:update' from role where name in ('admin')
/
commit
/