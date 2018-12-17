-- PURPOSE    : INSERT DATA - SUBJECT AREA, FUNCTIONAL AREA, SUBJECT FUNCTIONAL MAPPING
-- AUTHOR     : Chetan
-- CREATED ON : 30-APR-2014
-- SUBJECT AREA INSERT SCRIPTS
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'warrantyAdmin', 'Warranty Admin')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'claims', 'Claims')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'inventory', 'Inventory')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'fPI', 'FPI')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'partReturns', 'Part Returns')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'partsRecovery', 'Parts Recovery')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'processorRecovery', 'Processor Recovery')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'contractAdmin', 'Contract Admin')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'items', 'Items')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'reports', 'Reports')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'dealerInformation', 'Dealer Information')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'dRApproval/Transfer', 'DR Approval/Transfer')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'settings', 'Settings')
/
INSERT INTO MST_ADMIN_SUBJECT_AREA values(MST_ADMIN_SUBJECT_AREA_seq.nextval, 'accounts', 'Accounts')
/
--FUNCTIONAL AREA INSERT SCRIPTS
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminWarrantyAdminTab', 'Warranty Admin Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageBusinessConfigurations', 'Manage Business Configurations')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageWarrantyPolicies', 'Manage Warranty Policies')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminPolicyDefinition', 'Policy Definition')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageFlatRates', 'Manage Flat Rates')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminLaborRates', 'Labor Rates')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminTravelRates', 'Travel Rates')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminItemPriceModifiers', 'Item Price Modifiers')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManagePartsCost', 'Manage Parts Cost')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminPartReturnConfiguration', 'Part Return Configuration')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageStateMandates', 'Manage State Mandates')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageClaimPayment', 'Manage Claim Payment')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminCreateClaimPaymentModifier', 'Create Claim Payment Modifier')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminConfigureClaimPaymentDefinition', 'Configure Claim Payment Definition')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageFailureStructure', 'Manage Failure Structure')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminSeriesFailureHierarchy', 'Series Failure Hierarchy')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminListFaultLocations', 'List Fault Locations')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminListServiceProcedures', 'List Service Procedures')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminFaultFound/CausedByAssociation', 'FaultFound/CausedBy Association')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminMaintainFailureDetailofFailureURL', 'Maintain Failure Detail of Failure URL')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageGroups', 'Manage Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminItemGroups', 'Item Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminDealerGroups', 'Dealer Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminUserGroups', 'User Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageBusinessRuleGroups', 'Manage Business Rule Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminClaimsProcessing', 'Claims Processing')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminClaimProcessorRouting', 'Claim Processor Routing')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminDSMRouting', 'DSM Routing')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminAdvisorRouting', 'Advisor Routing')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageBusinessRules', 'Manage Business Rules')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminEntryValidations', 'Entry Validations')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminProcessorAuthority', 'Processor Authority')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageBusinessConditions', 'Manage Business Conditions')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminWarrantyBusinessConditions', 'Warranty Business Conditions')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageUserAvailability', 'Manage User Availability')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminProcessor', 'Processor')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminDSM', 'DSM')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminDSMAdvisor', 'DSM Advisor')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminRecoveryProcessor', 'Recovery Processor')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminFieldProductImprovement', 'Field Product Improvement')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminRelatedFPIsManagement', 'Related FPIs Management')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageWarehouses', 'Manage Warehouses')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminListOfValues', 'List Of Values')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageDisplayedFaultCode', 'Manage Displayed Fault Code')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminAdditionalAttributes', 'Additional Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminCreate/UpdateAttributes', 'Create/Update Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminAssociate/DissociatePartAttributes', 'Associate/Dissociate Part Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'Associate/DissociateFlatRatesAttributes', 'Associate/Dissociate Flat Rates Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'Associate/DissociateClaimedInventoryAttributes', 'Associate/Dissociate Claimed Inventory Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminAssociate/DissociateClaimAttributes', 'Associate/Dissociate Claim Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageFreight/Shippers', 'Manage Freight/Shippers')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminViewSupplier', 'View Supplier')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminViewLabels', 'View Labels')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageLaborSplit', 'Manage Labor Split')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminCreateLabelsforinventory', 'Create Labels for inventory')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminCreateLabelsforModels', 'Create Labels for Models')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminMiscellaneousParts', 'Miscellaneous Parts')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageMiscellaneousParts', 'Manage Miscellaneous Parts')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminConfigureMiscellaneousParts', 'Configure Miscellaneous Parts')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminCostCategoryConfiguration', 'Cost Category Configuration')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageLOASchemes', 'Manage LOA Schemes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'warrantyAdminManageAdditionalLaborEligibility', 'Manage Additional Labor Eligibility')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsClaimTab', 'Claim Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsMultiClaimMaintenance', 'Multi Claim Maintenance')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsClaimAttributes', 'Claim Attributes')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsTransfer/Re-process', 'Transfer/Re-process')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsAdviceRequest', 'Advice Request')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsAppeals', 'Appeals')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsDraftClaim', 'Draft Claim')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsForwardedExternally', 'Forwarded Externally')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsForwardedInternally', 'Forwarded Internally')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsInProgress', 'In Progress')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsOnHoldForPartReturn', 'On Hold For Part Return')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsPartShippedNotReceived', 'Part Shipped Not Received')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsPendingAuthorization', 'Pending Authorization')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsNew', 'New')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsRejectedPartReturn', 'Rejected Part Return')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsReplies', 'Replies')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsServiceManagerReview', 'Service Manager Review')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsTransferred', 'Transferred')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsWaitingForLabor', 'WaitingForLabor')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsPendingRecoveryInitiation', 'Pending Recovery Initiation')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsDefineSearchQuery', 'Define Search Query')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'claimsPredefinedSearch', 'Predefined Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'inventoryInventoryTab', 'Inventory Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'inventoryStock', 'Stock')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'inventoryRetailed', 'Retailed')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'inventoryDefineSearchQuery', 'Define Search Query')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'inventoryPredefinedStockSearch', 'Predefined Stock Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'inventoryPredefinedRetailSearch', 'Predefined Retail Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'fPIFPITab', 'FPI Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'fPIPendingFPIUpdateRequests', 'Pending FPI Update Requests')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'fPIPredefinedSearch', 'Predefined Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsPartReturnsTab', 'Part Returns Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsDuePartsReceipt', 'Due Parts Receipt')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsDuePartsInspection', 'Due Parts Inspection')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsAwaitingShipmentforDealer', 'Awaiting Shipment for Dealer')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsShipmentGeneratedForDealer', 'Shipment Generated For Dealer')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsDealerRequestedPartsShipped', 'Dealer Requested Parts Shipped')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsRequiredPartsfromDealer', 'Required Parts from Dealer')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsDealerRequestedPart', 'Dealer Requested Part')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsDefineSearchQuery', 'Define Search Query')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partReturnsPredefinedSearch', 'Predefined Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partsRecoveryPartsRecoveryTab', 'Parts Recovery Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partsRecoveryAwaitingShipment', 'Awaiting Shipment')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partsRecoveryAwaitingShipmentToWarehouse', 'Awaiting Shipment To Warehouse')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partsRecoveryAwaitingShipmenttoSupplier', 'Awaiting Shipment to Supplier')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partsRecoverySupplierPartsShipped', 'Supplier Parts Shipped')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partsRecoveryShipmentGenerated', 'Shipment Generated')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'partsRecoveryPredefinedSearch', 'Predefined Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryProcessorRecoveryTab', 'Processor Recovery Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryClaimsDueForRecovery', 'Claims Due For Recovery')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryNew', 'New')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryInProgress', 'In Progress')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryNotForRecoveryRequest', 'Not For Recovery Request')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryNotForRecoveryResponse', 'Not For Recovery Response')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryTransferred', 'Transferred')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryReopened', 'Reopened')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryClaimsUnderRecovery', 'Claims Under Recovery')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryDisputed', 'Disputed')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryAccepted', 'Accepted')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryAwaitingSupplierResponse', 'Awaiting Supplier Response')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryNotForRecovery', 'Not For Recovery')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryMultiClaimMaintenance', 'Multi Claim Maintenance')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryReadyForDebit', 'Ready For Debit')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryDefineSearchQuery', 'Define Search Query')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'processorRecoveryPreDefinedSearch', 'PreDefined Search')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminContractAdminTab', 'Contract Admin Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminMaintainSupplierContracts', 'Maintain Supplier Contracts')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminManageBusinessConditions', 'Manage Business Conditions')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminManageBusinessRules', 'Manage Business Rules')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminRecoveryClaimProcessorRouting', 'Recovery Claim Processor Routing')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminManageSuppliers', 'Manage Suppliers')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminViewPartSourceHistory', 'View Part Source History')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminViewSupplier', 'View Supplier')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminManageGroups', 'Manage Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'contractAdminUserGroups', 'User Groups')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'itemsItemsTab', 'Items Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'itemsDefinedSearchQuery', 'Defined Search Query')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reportsReportsTab', 'Reports Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reportsVendorRecoveryReport', 'Vendor Recovery Report')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reportsManageUploads/Downloads', 'Manage Uploads/Downloads')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reportsUploadManagement', 'Upload Management')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'reportsDownloadManagement', 'Download Management')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dealerInformationDealerSummary', 'Dealer Summary')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferDRApproval/TransferTab', 'DR Approval/Transfer Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferDeliveryReport', 'Delivery Report')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferDraft', 'Draft')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferPendingforApproval', 'Pending for Approval')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferForwardedToDealer', 'Forwarded To Dealer')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferRejected', 'Rejected')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferReplies', 'Replies')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferResubmitted', 'Resubmitted')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferUnitTransfer', 'Unit Transfer')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dRApproval/TransferDeleted', 'Deleted')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsSettingsTab', 'Settings Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsProfile', 'Profile')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsUserManagement', 'User Management')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsCreateUser', 'Create User')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsUpdateUser', 'Update User')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsSeriesReftoCertification', 'Series Ref to Certification')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsCreateInternalUser', 'Create Internal User')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsUpdateInternalUser', 'Update Internal User')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsManageUserBusinessUnitMapping', 'Manage User Business Unit Mapping')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsEmailSubscription', 'Email Subscription')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsMapServiceProviderBusinessUnit', 'Map Service Provider Business Unit')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsMapSupplierBusinessUnit', 'Map Supplier Business Unit')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'settingsManageRoles', 'Manage Roles')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'accountsAccountsTab', 'Accounts Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'accountsRemoteInteractionsLogs', 'Remote Interactions Logs')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'accountsMonthEndScheduler', 'Month End Scheduler')
/
-- SUBJECT & FUNCTIONAL AREA MAP INSERT SCRIPTS
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminWarrantyAdminTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageBusinessConfigurations'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageWarrantyPolicies'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminPolicyDefinition'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageFlatRates'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminLaborRates'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminTravelRates'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminItemPriceModifiers'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManagePartsCost'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminPartReturnConfiguration'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageStateMandates'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageClaimPayment'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminCreateClaimPaymentModifier'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminConfigureClaimPaymentDefinition'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageFailureStructure'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminSeriesFailureHierarchy'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminListFaultLocations'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminListServiceProcedures'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminFaultFound/CausedByAssociation'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminMaintainFailureDetailofFailureURL'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageGroups'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminItemGroups'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminDealerGroups'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminUserGroups'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageBusinessRuleGroups'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminClaimsProcessing'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminClaimProcessorRouting'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminDSMRouting'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminAdvisorRouting'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageBusinessRules'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminEntryValidations'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminProcessorAuthority'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageBusinessConditions'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminWarrantyBusinessConditions'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageUserAvailability'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminProcessor'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminDSM'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminDSMAdvisor'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminRecoveryProcessor'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminFieldProductImprovement'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminRelatedFPIsManagement'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageWarehouses'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminListOfValues'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageDisplayedFaultCode'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminAdditionalAttributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminCreate/UpdateAttributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminAssociate/DissociatePartAttributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='Associate/DissociateFlatRatesAttributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='Associate/DissociateClaimedInventoryAttributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminAssociate/DissociateClaimAttributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageFreight/Shippers'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminViewSupplier'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminViewLabels'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageLaborSplit'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminCreateLabelsforinventory'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminCreateLabelsforModels'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminMiscellaneousParts'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageMiscellaneousParts'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminConfigureMiscellaneousParts'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminCostCategoryConfiguration'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageLOASchemes'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='warrantyAdminManageAdditionalLaborEligibility'),(select id from MST_ADMIN_SUBJECT_AREA where name='warrantyAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsClaimTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsMultiClaimMaintenance'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsClaimAttributes'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsTransfer/Re-process'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsAdviceRequest'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsAppeals'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsDraftClaim'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsForwardedExternally'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsForwardedInternally'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsInProgress'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsOnHoldForPartReturn'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsPartShippedNotReceived'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsPendingAuthorization'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsNew'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsRejectedPartReturn'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsReplies'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsServiceManagerReview'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsTransferred'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsWaitingForLabor'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsPendingRecoveryInitiation'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsDefineSearchQuery'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='claimsPredefinedSearch'),(select id from MST_ADMIN_SUBJECT_AREA where name='claims'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='inventoryInventoryTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='inventoryStock'),(select id from MST_ADMIN_SUBJECT_AREA where name='inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='inventoryRetailed'),(select id from MST_ADMIN_SUBJECT_AREA where name='inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='inventoryDefineSearchQuery'),(select id from MST_ADMIN_SUBJECT_AREA where name='inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='inventoryPredefinedStockSearch'),(select id from MST_ADMIN_SUBJECT_AREA where name='inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='inventoryPredefinedRetailSearch'),(select id from MST_ADMIN_SUBJECT_AREA where name='inventory'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='fPIFPITab'),(select id from MST_ADMIN_SUBJECT_AREA where name='fPI'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='fPIPendingFPIUpdateRequests'),(select id from MST_ADMIN_SUBJECT_AREA where name='fPI'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='fPIPredefinedSearch'),(select id from MST_ADMIN_SUBJECT_AREA where name='fPI'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsPartReturnsTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsDuePartsReceipt'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsDuePartsInspection'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsAwaitingShipmentforDealer'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsShipmentGeneratedForDealer'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsDealerRequestedPartsShipped'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsRequiredPartsfromDealer'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsDealerRequestedPart'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsDefineSearchQuery'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partReturnsPredefinedSearch'),(select id from MST_ADMIN_SUBJECT_AREA where name='partReturns'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partsRecoveryPartsRecoveryTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='partsRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partsRecoveryAwaitingShipment'),(select id from MST_ADMIN_SUBJECT_AREA where name='partsRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partsRecoveryAwaitingShipmentToWarehouse'),(select id from MST_ADMIN_SUBJECT_AREA where name='partsRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partsRecoveryAwaitingShipmenttoSupplier'),(select id from MST_ADMIN_SUBJECT_AREA where name='partsRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partsRecoverySupplierPartsShipped'),(select id from MST_ADMIN_SUBJECT_AREA where name='partsRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partsRecoveryShipmentGenerated'),(select id from MST_ADMIN_SUBJECT_AREA where name='partsRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='partsRecoveryPredefinedSearch'),(select id from MST_ADMIN_SUBJECT_AREA where name='partsRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryProcessorRecoveryTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryClaimsDueForRecovery'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryNew'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryInProgress'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryNotForRecoveryRequest'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryNotForRecoveryResponse'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryTransferred'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryReopened'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryClaimsUnderRecovery'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryDisputed'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryAccepted'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryAwaitingSupplierResponse'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryNotForRecovery'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryMultiClaimMaintenance'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryReadyForDebit'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryDefineSearchQuery'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='processorRecoveryPreDefinedSearch'),(select id from MST_ADMIN_SUBJECT_AREA where name='processorRecovery'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminContractAdminTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminMaintainSupplierContracts'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminManageBusinessConditions'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminManageBusinessRules'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminRecoveryClaimProcessorRouting'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminManageSuppliers'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminViewPartSourceHistory'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminViewSupplier'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminManageGroups'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='contractAdminUserGroups'),(select id from MST_ADMIN_SUBJECT_AREA where name='contractAdmin'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='itemsItemsTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='items'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='itemsDefinedSearchQuery'),(select id from MST_ADMIN_SUBJECT_AREA where name='items'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='reportsReportsTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='reportsVendorRecoveryReport'),(select id from MST_ADMIN_SUBJECT_AREA where name='reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='reportsManageUploads/Downloads'),(select id from MST_ADMIN_SUBJECT_AREA where name='reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='reportsUploadManagement'),(select id from MST_ADMIN_SUBJECT_AREA where name='reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='reportsDownloadManagement'),(select id from MST_ADMIN_SUBJECT_AREA where name='reports'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummary'),(select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferDRApproval/TransferTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferDeliveryReport'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferDraft'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferPendingforApproval'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferForwardedToDealer'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferRejected'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferReplies'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferResubmitted'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferUnitTransfer'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dRApproval/TransferDeleted'),(select id from MST_ADMIN_SUBJECT_AREA where name='dRApproval/Transfer'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsSettingsTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsProfile'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsUserManagement'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsCreateUser'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsUpdateUser'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsSeriesReftoCertification'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsCreateInternalUser'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsUpdateInternalUser'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsManageUserBusinessUnitMapping'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsEmailSubscription'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsMapServiceProviderBusinessUnit'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsMapSupplierBusinessUnit'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='settingsManageRoles'),(select id from MST_ADMIN_SUBJECT_AREA where name='settings'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='accountsAccountsTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='accounts'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='accountsRemoteInteractionsLogs'),(select id from MST_ADMIN_SUBJECT_AREA where name='accounts'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='accountsMonthEndScheduler'),(select id from MST_ADMIN_SUBJECT_AREA where name='accounts'))
/