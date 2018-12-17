--PURPOSE    : Insert MST_ADMIN_ACTION, MST_ADMIN_SUBJECT_AREA, MST_ADMIN_FNC_AREA, SUBJECT_FUNC_AREA_MAPPING
--AUTHOR     : Chetan
--CREATED ON : 10-MAR-2014
-- Insert MST_ADMIN_SUBJECT_AREA
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'BUConfigurations', 'BUConfigurations')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'ClaimSubmissionandProcessing', 'ClaimSubmissionandProcessing')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'FieldModifications', 'FieldModifications')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'Inventory', 'Inventory')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'PartReturnManagement', 'PartReturnManagement')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'Read-OnlyAccesstoSLMS', 'Read-OnlyAccesstoSLMS')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'Registration/Transfer', 'Registration/Transfer')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'Reports', 'Reports')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'WarrantyAdmin', 'WarrantyAdmin')
/
-- Insert MST_ADMIN_FNC_AREA
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'bUConfigurationsSetup', 'BU Configurations Setup')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'createWarrantyClaimDealer', 'Create Warranty Claim Dealer')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'createWarrantyClaimProcessor', 'Create Warranty Claim Processor')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'defineClaimSearchQuery', 'Define Claim Search Query ')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'multiClaimMaintenance', 'Multi Claim Maintenance ')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'preDefinedClaimSearch', 'Pre Defined Claim Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reOpenClaim', 'Re Open Claim')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'waiveLateFee', 'Waive Late Fee')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'fieldModificationsTab', 'Field Modifications Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'pendingFieldModificationUpdate', 'Pending Field Modification Update')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'requests', 'Requests')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'preDefinedFPISearch', 'Pre Defined FPI Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'submitFieldModificationClaim', 'Submit Field Modification Claim')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'inventoryTab', 'Inventory Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'deliveryReport', 'Delivery Report ')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'defineInventorySearchQuery', 'Define Inventory Search Query ')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'preDefinedInventorySearch', 'Pre Defined Inventory Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'stock/Retail', 'Stock/Retail')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'quickInventorySearch', 'Quick Inventory Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'registerMajorComponent', 'Register Major Component')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'retailsearch', 'Retail search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'stocksearch', 'Stock search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnTab', 'Part Return Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'definePartReturnSearchQuery', 'Define Part Return Search Query')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'preDefinedPartReturnsSearch', 'Pre Defined Part Returns Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'readOnlyAccesstoSLMS', 'Read Only Access to SLMS')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'extensionforApproval', 'Extension for Approval')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reducedCoverage', 'Reduced Coverage')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reportTab', 'Report Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageUploads/Download', 'Manage Uploads/Download')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'switchUser', 'Switch User')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminTab', 'Warranty Admin Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'additionalAttributes', 'Additional Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'costCategoryConfiguration', 'Cost Category Configuration')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'createCustomer', 'Create Customer')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'createLabelsforinventory', 'Create Labels for inventory')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dataManagement', 'Data Management')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dealerGroups', 'Dealer Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'emailNotification', 'Email Notification')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'fieldNotification', 'Field Notification')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'listModels', 'List Models')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'listOfValues', 'List Of Values')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'maintainUOMMappings', 'Maintain UOM Mappings')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageAdditionalLabour', 'Manage Additional Labour')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'eligility', 'Eligility')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageAlarmCode', 'Manage Alarm Code')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageBusinessCondition', 'Manage Business Condition')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageBusinessRuleGroups', 'Manage Business Rule Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageBusinessRule', 'Manage Business Rule')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageClaimPayment', 'Manage Claim Payment')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageCustomReports', 'Manage Custom Reports')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageFailureStructure', 'Manage Failure Structure')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageFlatRates', 'Manage Flat Rates')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageFreight/Shippers', 'Manage Freight/Shippers')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageGroups', 'Manage Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageInclusiveJobCodes', 'Manage Inclusive Job Codes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageLaborSplit', 'Manage Labor Split')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageLOASchemes', 'Manage LOA Schemes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageUserAvailability', 'Manage User Availability')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageWarehouses', 'Manage Warehouses')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'manageWarrantyPolicies', 'Manage Warranty Policies')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'miscellaneousParts', 'Miscellaneous Parts')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnConfiguration', 'Part Return Configuration')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'predefinedSearch(Items)', 'Predefined Search(Items)')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'viewLabels', 'View Labels')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'viewSupplier', 'View Supplier')
/
-- SUBJECT_FUNC_AREA_MAPPING Mappings
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='BU Configurations Setup'),(select id from MST_ADMIN_SUBJECT_AREA where name='BUConfigurations'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Create Warranty Claim Dealer'),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Create Warranty Claim Processor'),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Define Claim Search Query '),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Multi Claim Maintenance '),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Pre Defined Claim Search'),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Re Open Claim'),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Waive Late Fee'),(select id from MST_ADMIN_SUBJECT_AREA where name='ClaimSubmissionandProcessing'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Field Modifications Tab'),(select id from MST_ADMIN_SUBJECT_AREA where name='FieldModifications'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Pending Field Modification Update'),(select id from MST_ADMIN_SUBJECT_AREA where name='FieldModifications'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Requests'),(select id from MST_ADMIN_SUBJECT_AREA where name='FieldModifications'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Pre Defined FPI Search'),(select id from MST_ADMIN_SUBJECT_AREA where name='FieldModifications'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Submit Field Modification Claim'),(select id from MST_ADMIN_SUBJECT_AREA where name='FieldModifications'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Inventory Tab'),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Delivery Report '),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Define Inventory Search Query '),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Pre Defined Inventory Search'),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Stock/Retail'),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Quick Inventory Search'),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Register Major Component'),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Retail search'),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Stock search'),(select id from MST_ADMIN_SUBJECT_AREA where name='Inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Part Return Tab'),(select id from MST_ADMIN_SUBJECT_AREA where name='PartReturnManagement'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Define Part Return Search Query'),(select id from MST_ADMIN_SUBJECT_AREA where name='PartReturnManagement'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Pre Defined Part Returns Search'),(select id from MST_ADMIN_SUBJECT_AREA where name='PartReturnManagement'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Read Only Access to SLMS'),(select id from MST_ADMIN_SUBJECT_AREA where name='Read-OnlyAccesstoSLMS'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Extension for Approval'),(select id from MST_ADMIN_SUBJECT_AREA where name='Registration/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Reduced Coverage'),(select id from MST_ADMIN_SUBJECT_AREA where name='Registration/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Report Tab'),(select id from MST_ADMIN_SUBJECT_AREA where name='Reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Uploads/Download'),(select id from MST_ADMIN_SUBJECT_AREA where name='Reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Switch User'),(select id from MST_ADMIN_SUBJECT_AREA where name='Reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Warranty Admin Tab'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Additional Attributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Cost Category Configuration'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Create Customer'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Create Labels for inventory'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Data Management'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Dealer Groups'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Email Notification'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Field Notification'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='List Models'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='List Of Values'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Maintain UOM Mappings'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Additional Labour'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Eligility'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Alarm Code'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Business Condition'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='manageBusinessConfigurations'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Business Rule Groups'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Business Rule'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Claim Payment'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Custom Reports'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Failure Structure'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Flat Rates'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Freight/Shippers'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Groups'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Inclusive Job Codes'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Labor Split'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage LOA Schemes'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage User Availability'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Warehouses'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Manage Warranty Policies'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Miscellaneous Parts'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Part Return Configuration'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='Predefined Search(Items)'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='View Labels'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where description='View Supplier'),(select id from MST_ADMIN_SUBJECT_AREA where name='WarrantyAdmin'))
/
commit
/