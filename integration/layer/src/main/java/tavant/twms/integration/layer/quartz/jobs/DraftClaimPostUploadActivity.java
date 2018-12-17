/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

package tavant.twms.integration.layer.quartz.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.domain.additionalAttributes.AttributePurpose;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignNotificationRepository;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.alarmcode.AlarmCodeRepository;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.claim.claimsubmission.FieldModificationClaimSubmissionUtil;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.ClaimCompetitorModel;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.SmrReason;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.upload.connection.ReportTaskDAO;
import tavant.twms.domain.upload.controller.BlobUtil;
import tavant.twms.domain.upload.controller.DataUploadConfig;
import tavant.twms.domain.upload.controller.ReceivedFileDetails;
import tavant.twms.domain.upload.controller.UploadManagement;
import tavant.twms.domain.upload.controller.UploadManagementService;
import tavant.twms.domain.upload.controller.UploadStatusDetail;
import tavant.twms.domain.upload.errormgt.ErrorReportGeneratorFactory;
import tavant.twms.domain.upload.staging.FileReceiver;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FaultCode;

/**
 * @author jhulfikar.ali
 *
 */
public class DraftClaimPostUploadActivity {

	private Logger logger = Logger.getLogger(DraftClaimPostUploadActivity.class);
	
	private ReportTaskDAO reportTaskDAO;
	
	private ClaimService claimService;
	
	private ClaimRepository claimRepository;
	
	private AttributeAssociationService attributeAssociationService;
	
	private ClaimProcessService claimProcessService;
	
	private InventoryService inventoryService;
	
	private OrgService orgService;
	
	private CatalogService catalogService;
	
	private DomainRepository domainRepository;
	
	private LovRepository lovRepository;
	
	private AlarmCodeRepository alarmCodeRepository;
	
	private PolicyService policyService;
	
	private ConfigParamService configParamService;
	
	private FailureStructureService failureStructureService;
	
	private CampaignService campaignService;
	
	private JdbcTemplate jdbcTemplate;
	
	private TransactionTemplate transactionTemplate;
	
	private FileReceiver fileReceiver;
	
	private MiscellaneousItemConfigService miscellaneousItemConfigService;
	
	private DealerGroupService dealerGroupService;
	
	private ClaimSubmissionUtil claimSubmissionUtil;
	
	private FieldModificationClaimSubmissionUtil fieldModificationClaimSubmissionUtil;
	
	private CampaignNotificationRepository campaignNotificationRepository;
	
	private UploadManagementService uploadManagementService;
	
	private ErrorReportGeneratorFactory errorReportGeneratorFactory;
	
	private DataUploadConfig dataUploadConfig;
	
	public DataUploadConfig getDataUploadConfig() {
		return dataUploadConfig;
	}

	public void setDataUploadConfig(DataUploadConfig dataUploadConfig) {
		this.dataUploadConfig = dataUploadConfig;
	}

	private BlobUtil blobUtil;
	
	
	public BlobUtil getBlobUtil() {
		return blobUtil;
	}

	public void setBlobUtil(BlobUtil blobUtil) {
		this.blobUtil = blobUtil;
	}

	public ErrorReportGeneratorFactory getErrorReportGeneratorFactory() {
		return errorReportGeneratorFactory;
	}

	public void setErrorReportGeneratorFactory(
			ErrorReportGeneratorFactory errorReportGeneratorFactory) {
		this.errorReportGeneratorFactory = errorReportGeneratorFactory;
	}

	public UploadManagementService getUploadManagementService() {
		return uploadManagementService;
	}

	public void setUploadManagementService(
			UploadManagementService uploadManagementService) {
		this.uploadManagementService = uploadManagementService;
	}

	public void setFieldModificationClaimSubmissionUtil(
			FieldModificationClaimSubmissionUtil fieldModificationClaimSubmissionUtil) {
		this.fieldModificationClaimSubmissionUtil = fieldModificationClaimSubmissionUtil;
	}

	public void setCampaignNotificationRepository(
			CampaignNotificationRepository campaignNotificationRepository) {
		this.campaignNotificationRepository = campaignNotificationRepository;
	}

	public DealerGroupService getDealerGroupService() {
		return dealerGroupService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public MiscellaneousItemConfigService getMiscellaneousItemConfigService() {
		return miscellaneousItemConfigService;
	}

	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

	public FileReceiver getFileReceiver() {
		return fileReceiver;
	}

	public void setFileReceiver(FileReceiver fileReceiver) {
		this.fileReceiver = fileReceiver;
	}

	List<Claim> claims;
	Map<Long, Long> draftClaims = new HashMap<Long, Long>(5);

	private static final String TEMPLATE_NAME="draftWarrantyClaims";
	
	@SuppressWarnings("unchecked")
	public synchronized void doPostUploadActivities() throws Exception {
				populateDummyAuthentication();
		claims = new ArrayList<Claim>();
		this.jdbcTemplate.execute(new ConnectionCallback() {
			
			public Object doInConnection(Connection conn) throws SQLException,
			DataAccessException {
				PreparedStatement ps = null;
				draftClaims = new HashMap<Long, Long>(5); 
				ResultSet draftClaimRs = null;
				Long draftClaimId = new Long(0);
				try {
					PreparedStatement prepSt = null, prepStatement=null;					
					Statement statement = conn.createStatement();
					ResultSet rs = statement.executeQuery(
							" select f.id, f.uploaded_by from file_upload_mgt f " +
							" where template_name = 'Draft Warranty Claims' and upload_status= " +UploadStatusDetail.STATUS_WAITING_FOR_UPLOAD+
							" order by id desc ");

					// To get all the records of Draft Claim uploaded
					while (rs.next())
					{
						draftClaims.put(rs.getLong(1), rs.getLong(2));
						fileReceiver.updateFileUploadStatus(rs.getLong(1), UploadStatusDetail.STATUS_UPLOADING_DRAFT_CLAIMS);
					}
					 
					// To populate the Draft Claim into system
					for (Iterator<Long> iterator = draftClaims.keySet().iterator(); iterator.hasNext();) {
						draftClaimId = (Long) iterator.next();
						ps = conn.prepareStatement(draftClaimQuery());
						ps.setLong(1, draftClaimId);
						draftClaimRs = ps.executeQuery();
						int totalReocrds = 0, errorRecords = 0;
						Long stgDraftClaimId = new Long(0);
						
						while (draftClaimRs.next())
						{
							totalReocrds++;
							stgDraftClaimId = draftClaimRs.getLong(COL_ID);
							try {
							    Claim claim = null;
							    List<ClaimedItem> claimedItems = new ArrayList<ClaimedItem>(2);
							    claimedItems.add(new ClaimedItem());
							    
								String claimType = draftClaimRs.getString(COL_CLAIM_TYPE); // Claim type to decide
								if ( CLAIM_TYPE_MACHINE_SERIALIZED.equalsIgnoreCase(claimType) || 
										CLAIM_TYPE_MACHINE_NON_SERIALIZED.equalsIgnoreCase(claimType) )
								{
									claim = new MachineClaim();
									if ( CLAIM_TYPE_MACHINE_NON_SERIALIZED.equalsIgnoreCase(claimType) && 
											StringUtils.isNotBlank(draftClaimRs.getString(COL_INSTALLATION_DATE)) )
										claim.setPurchaseDate(
											CalendarDate.from(draftClaimRs.getString(COL_INSTALLATION_DATE), 
													DRAFT_CLAIM_DATE_FORMAT_YYYYMMDD));
								}
                                /*else if ( CLAIM_TYPE_ATTACHMENT_SERIALIZED.equalsIgnoreCase(claimType) ||				//since columns removed
										CLAIM_TYPE_ATTACHMENT_NON_SERIALIZED.equalsIgnoreCase(claimType) )
								{
									claim = new AttachmentClaim();
									if ( CLAIM_TYPE_ATTACHMENT_NON_SERIALIZED.equalsIgnoreCase(claimType) &&
											StringUtils.isNotBlank(draftClaimRs.getString(COL_INSTALLATION_DATE)) )
										claim.setPurchaseDate(
											CalendarDate.from(draftClaimRs.getString(COL_INSTALLATION_DATE),
													DRAFT_CLAIM_DATE_FORMAT_YYYYMMDD));
								}*/
								else if ( CLAIM_TYPE_PARTS_WITH_HOST.equalsIgnoreCase(claimType) || 
											CLAIM_TYPE_PARTS_WITHOUT_HOST.equalsIgnoreCase(claimType) )
								{
									claim = new PartsClaim();
									// Un-serialized Claim
									Long partSerialId = draftClaimRs.getLong(COL_PART_SERIAL_ID);
									if(partSerialId != null && partSerialId > 0) {
										InventoryItem referredInventoryItem = new InventoryItem();
										referredInventoryItem.setId(draftClaimRs
												.getLong(COL_PART_SERIAL_ID));

										Item referredItem = new Item();
										referredItem.setId(draftClaimRs.getLong(COL_PART_ID));

										referredInventoryItem.setOfType(referredItem);
										ItemReference itemReference = new ItemReference();
										itemReference
												.setReferredInventoryItem(referredInventoryItem);
										claim.setPartItemReference(itemReference);
									} else {
										Item referredItem = new Item();
										referredItem.setId(draftClaimRs.getLong(COL_PART_ID));
										ItemReference itemReference = new ItemReference();
										itemReference.setReferredItem(referredItem);
										claim.setPartItemReference(itemReference);
									}

									// Only when Part is installed on host machine
									((PartsClaim) claim).setPartInstalled(CLAIM_TYPE_PARTS_WITH_HOST
											.equalsIgnoreCase(claimType) ? Boolean.TRUE : Boolean.FALSE);
									if ( StringUtils.isNotBlank(draftClaimRs.getString(COL_INSTALLATION_DATE)) )
									{
										claim.setInstallationDate(
												CalendarDate.from(draftClaimRs.getString(COL_INSTALLATION_DATE), 
														DRAFT_CLAIM_DATE_FORMAT_YYYYMMDD));
										
										if (CLAIM_TYPE_PARTS_WITHOUT_HOST.equalsIgnoreCase(claimType)
												&& StringUtils.isNotBlank(draftClaimRs.getString(COL_PART_ITEM_NUMBER)))
											claim.setPurchaseDate(claim.getInstallationDate());
									}
								}
								else if ( CLAIM_TYPE_FIELD_MODIFICATION.equalsIgnoreCase(claimType) )
								{
									claim = new CampaignClaim();
									Campaign campaign = new Campaign();
									campaign.setCode(draftClaimRs.getString(COL_CAMPAIGN_CODE));
									( (CampaignClaim) claim).setCampaign(campaign);
								}
								
								if (StringUtils.isNotBlank(draftClaimRs.getString(COL_HOURS_IN_SERVICE)))
									claimedItems.get(0).setHoursInService(new BigDecimal(draftClaimRs.getString(COL_HOURS_IN_SERVICE))); // Hours in service
								
								Long userId = draftClaims.get(draftClaimId); // Drafting user id
								if (userId!=null)
								{
									User user = new User();
									user.setId(userId); // Populate the user id for later initialization
									claim.setFiledBy(user);
								}
								
								Long serialNumberId = draftClaimRs.getLong(COL_SERIAL_NUMBER_ID);
								String competitorModel = draftClaimRs.getString(COL_COMPETITOR_MODEL);
								String competitorModelBrand = draftClaimRs.getString(COL_COMPETITOR_MODEL_BRAND);
								String competitorModelTruckSerialNumber = draftClaimRs.getString(COL_COMPETITOR_MODEL_SERIAL_NUMBER);
								//Long competitorModelId = draftClaimRs.getLong(COL_COMPETITOR_MODEL_ID);
								if(!CLAIM_TYPE_PARTS_WITHOUT_HOST.equalsIgnoreCase(claimType)){
								if ( serialNumberId != null && serialNumberId > 0) {
									// Serialized Claim
									InventoryItem referredInventoryItem = new InventoryItem();
									referredInventoryItem.setId(serialNumberId);
									ItemReference itemReference = new ItemReference(referredInventoryItem);
									/*itemReference.setReferredInventoryItem(referredInventoryItem);*/
									claimedItems.get(0).setItemReference(itemReference);
								} else if (competitorModel != null && !competitorModel.isEmpty()) {
									//part installed on competitor model
									//ClaimCompetitorModel competitorModel = new ClaimCompetitorModel();
									//competitorModel.setId(competitorModelId);
									claim.setCompetitorModelDescription(competitorModel);
									claim.setCompetitorModelBrand(competitorModelBrand);
									claim.setCompetitorModelTruckSerialnumber(competitorModelTruckSerialNumber);
								} else {
									// Un-serialized Machine Claim & Part Installed on non-serialized host
									Long itemId = draftClaimRs.getLong(COL_ITEM_NUMBER_ID);
									Long modelId = draftClaimRs.getLong(COL_MODEL_ID);
									ItemReference itemReference = new ItemReference();
									if (itemId != null && itemId > 0) {
										Item referredItem = new Item();
										referredItem.setId(itemId);
										itemReference.setReferredItem(referredItem);
									} else if(modelId != null && modelId > 0) {
											ItemGroup model = new ItemGroup();
											model.setId(modelId);
											itemReference.setSerialized(Boolean.FALSE);
											itemReference.setModel(model);
									}
									itemReference.setUnszdSlNo(draftClaimRs.getString(COL_TRUCK_SERIAL_NUMBER));
									claimedItems.get(0).setItemReference(itemReference);
								}
								}
								SelectedBusinessUnitsHolder.setSelectedBusinessUnit(draftClaimRs.getString(COL_BUSINESS_UNIT_NAME));
								claim.setWorkOrderNumber(draftClaimRs.getString(COL_WORK_ORDER_NUMBER)); // Work order number
								
								if(draftClaimRs.getString(COL_SERVICING_LOCATION_ID) != null && StringUtils.isNotEmpty(COL_SERVICING_LOCATION_ID)){								
									OrganizationAddress orgAddress = new OrganizationAddress();
									orgAddress.setSiteNumber(draftClaimRs.getString(COL_SERVICING_LOCATION_ID));
									claim.setServicingLocation(orgAddress);
								}
								
								String failureDate = draftClaimRs.getString(COL_FAILURE_DATE);
								if(failureDate != null && StringUtils.isNotEmpty(failureDate))
									claim.setFailureDate(CalendarDate.from(failureDate, 
										DRAFT_CLAIM_DATE_FORMAT_YYYYMMDD)); // Date of Failure
								
								claim.setRepairDate(CalendarDate.from(draftClaimRs.getString(COL_REPAIR_END_DATE), 
										DRAFT_CLAIM_DATE_FORMAT_YYYYMMDD)); // Date of Repair
								claim.setRepairStartDate(CalendarDate.from(draftClaimRs.getString(COL_REPAIR_START_DATE), 
										DRAFT_CLAIM_DATE_FORMAT_YYYYMMDD)); // Start Date of Repair
								claim.setForMultipleItems(Boolean.FALSE);
								
								if (YES_STRING.equalsIgnoreCase(draftClaimRs.getString(COL_SMR_CLAIM)))
								{
									claim.setServiceManagerRequest(Boolean.TRUE);
									String swcReason = draftClaimRs.getString(COL_REASON_FOR_SMR_CLAIM);
									if (StringUtils.isNotBlank(swcReason)) {
										SmrReason smrReason = new SmrReason();
										smrReason.setId(new Long(swcReason));
										claim.setReasonForServiceManagerRequest(smrReason);
									}
								}
								else
									claim.setServiceManagerRequest(Boolean.FALSE);
								
								if (YES_STRING.equalsIgnoreCase(draftClaimRs.getString(COL_COMMERCIAL_POLICY)))
								{
									claim.setCommercialPolicy(Boolean.TRUE);
								}
								else
									claim.setCommercialPolicy(Boolean.FALSE);
																					
								
								ServiceInformation serviceInformation = new ServiceInformation();
								String tempValue = draftClaimRs.getString(COL_CAUSAL_PART); // Causal Part
								Item causalPart = new Item();
								if (StringUtils.isNotBlank(tempValue)) {									
									causalPart.setNumber(tempValue); // Need to use for later initialization
									serviceInformation.setCausalPart(causalPart);
								} else if (CLAIM_TYPE_PARTS_WITHOUT_HOST.equalsIgnoreCase(claimType)) {
									serviceInformation.setCausalPart(claim.getPartItemReference().getReferredItem());
								}								
								
								tempValue = draftClaimRs.getString(COL_FAULT_CODE); // Fault Code
								if (StringUtils.isNotBlank(tempValue))
								{
									serviceInformation.setFaultCode(tempValue);
								}
								
								tempValue = draftClaimRs.getString(COL_FAULT_FOUND); // Fault Found
								if (StringUtils.isNotBlank(tempValue))
								{
									FailureTypeDefinition faultFound = new FailureTypeDefinition();
									faultFound.setName(tempValue); // Will be used later for post-population
									serviceInformation.setFaultFound(faultFound);
								}	
								
								Long causedByValue = draftClaimRs.getLong(COL_CAUSED_BY); // Fault Found
								if (causedByValue != null && causedByValue > 0)
								{
									FailureCauseDefinition causedBy = new FailureCauseDefinition();
									causedBy.setId(causedByValue); // Will be used later for post-population
									serviceInformation.setCausedBy(causedBy);
								}	
								
								tempValue = draftClaimRs.getString(COL_ROOT_CAUSE); // Root Cause
								if (StringUtils.isNotBlank(tempValue))
								{
									FailureRootCauseDefinition rootCause = new FailureRootCauseDefinition();
									rootCause.setName(tempValue);
									serviceInformation.setRootCause(rootCause);
								}
								
								ServiceDetail serviceDetail = new ServiceDetail();
								List<LaborDetail> laborPerformed = createLaborPerformed(claimType,
										draftClaimRs.getString(COL_JOB_CODE),
										draftClaimRs.getString(COL_LABOUR_HOURS),
										draftClaimRs.getString(COL_REASON_FOR_EXTRA_LABOR_HOURS));
								serviceDetail.setLaborPerformed(laborPerformed);
								
								tempValue = draftClaimRs.getString(COL_TECHNICIAN_ID); // Technician
								if (StringUtils.isNotBlank(tempValue))
								{
									User technician = new User();
									technician.setName(tempValue);
									serviceDetail.setTechnician(technician);
								}
								
								if(YES_STRING.equalsIgnoreCase(draftClaimRs.getString(COL_AUTHORIZATION_RECEIVED))){	//Authorization Received/Number
									claim.setCmsAuthCheck(Boolean.TRUE);
									claim.setAuthNumber(draftClaimRs.getString(COL_AUTHORIZATION_NUMBER));
								}
								
								if(StringUtils.isNotBlank(draftClaimRs.getString(COL_CONTACT_MANAGEMENT_TICKET_NUM)))	//Contact Mgt Ticket Num
									claim.setCmsTicketNumber(draftClaimRs.getString(COL_CONTACT_MANAGEMENT_TICKET_NUM));
								
								if(StringUtils.isNotBlank(draftClaimRs.getString(COL_BRAND)))		//Brand
									claim.setBrand(draftClaimRs.getString(COL_BRAND));
								
								if (StringUtils.isNotBlank(draftClaimRs.getString(COL_HOURS_ON_TRUCK_DURING_INSTALL)))
									claim.setHoursOnTruck(new BigDecimal(draftClaimRs.getString(COL_HOURS_ON_TRUCK_DURING_INSTALL))); // Hours on Truck 
								
								claim.setClaimedItems(claimedItems);
								claim.setConditionFound(draftClaimRs.getString(COL_CONDITIONS_FOUND)); // Condition Found
                                claim.setProbableCause(draftClaimRs.getString(COL_PROBABLE_CAUSE)); // Condition Found
								claim.setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));								
								claim.setWorkPerformed(draftClaimRs.getString(COL_WORK_PERFORMED)); // Work Performed								
								claim.setOtherComments(draftClaimRs.getString(COL_GENERAL_COMMENTS)); // Additional Comment
								claim.setInvoiceNumber(draftClaimRs.getString(COL_INVOICE_NUMBER)); // Invoice Number
								
								if (StringUtils.isNotBlank(draftClaimRs.getString(COL_HOURS_ON_PARTS)))
										claim.setHoursOnPart(new BigDecimal(draftClaimRs.getString(COL_HOURS_ON_PARTS))); // hours on part
								
								// Alarm codes 
								List<AlarmCode> alarmCodeList = createAlarmCodesList(draftClaimRs.getString(COL_ALARM_CODES));
								if(alarmCodeList != null && alarmCodeList.size()!= 0)
								claim.setAlarmCodes(alarmCodeList);
								
								// Replaced OEM Parts and Replaced OEM Parts
								// Quantity
								if (!CLAIM_TYPE_PARTS_WITHOUT_HOST.equalsIgnoreCase(claimType)) {

									List<OEMPartReplaced> partsReplaced = null;

									if (StringUtils
											.isNotBlank(draftClaimRs.getString(COL_REPLACED_OEM_PARTS_SERIAL_NUM))) {
										partsReplaced = createOemPartsReplaced(draftClaimRs
												.getString(COL_REPLACED_OEM_PARTS_SERIAL_NUM), draftClaimRs
												.getString(COL_REPLACED_OEM_PARTS), draftClaimRs
												.getString(COL_REPLACED_OEM_PARTS_QUANTITY));

									} else {

										partsReplaced = createOemPartsReplaced(draftClaimRs
												.getString(COL_REPLACED_OEM_PARTS), draftClaimRs
												.getString(COL_REPLACED_OEM_PARTS_QUANTITY));
									}

									List<InstalledParts> partsInstalled = null;
									if (StringUtils.isNotBlank(draftClaimRs
											.getString(COL_INSTALLED_OEM_PARTS_SERIAL_NUM))) {
										partsInstalled = createOemPartsInstalled(draftClaimRs
												.getString(COL_INSTALLED_OEM_PARTS_SERIAL_NUM), draftClaimRs
												.getString(COL_INSTALLED_OEM_PARTS), draftClaimRs
												.getString(COL_INSTALLED_OEM_PARTS_QUANTITY));

									} else {
										partsInstalled = createOemPartsInstalled(draftClaimRs
												.getString(COL_INSTALLED_OEM_PARTS), draftClaimRs
												.getString(COL_INSTALLED_OEM_PARTS_QUANTITY));
									}

									List<HussmanPartsReplacedInstalled> partsReplacedInstalled = new ArrayList<HussmanPartsReplacedInstalled>();
									int i = 0; // I variable is used for to get replaced part from replaced	 parts list.
									for (OEMPartReplaced replacedPart : partsReplaced) {

										HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled = new HussmanPartsReplacedInstalled();

										List<OEMPartReplaced> replacedParts = new ArrayList<OEMPartReplaced>();
										replacedParts.add(replacedPart);

										List<InstalledParts> installedParts = new ArrayList<InstalledParts>();
										installedParts.add(partsInstalled.get(i));

										hussmanPartsReplacedInstalled.setReplacedParts(replacedParts);
										hussmanPartsReplacedInstalled.setHussmanInstalledParts(installedParts);
										partsReplacedInstalled.add(hussmanPartsReplacedInstalled);
										i++;
									}
									serviceDetail.setHussmanPartsReplacedInstalled(partsReplacedInstalled);

								}
								

								// Replaced_Non_OEM_Parts, Replaced_Non_OEM_Parts_quantity,
								// Replaced_Non_OEM_Parts_Price, and Replaced_Non_OEM_parts_desc
								List<NonOEMPartReplaced> nonOemPartsReplaced = 
									createNonOemPartsReplaced(draftClaimRs.getString(COL_REPLACED_NON_OEM_PARTS), draftClaimRs.getString(COL_REPLACED_NON_OEM_PARTS_QUANTITY),
											draftClaimRs.getString(COL_REPLACED_NON_OEM_PARTS_PRICE), draftClaimRs.getString(COL_REPLACED_NON_OEM_PARTS_DESC));
								serviceDetail.setNonOEMPartsReplaced(nonOemPartsReplaced);
								
								List<NonOEMPartReplaced> miscPartsReplaced = createMiscPartsReplaced(
										draftClaimRs.getString(COL_MISC_PARTS), draftClaimRs.getString(COL_MISC_PARTS_QUANTITY));
								serviceDetail.setMiscPartsReplaced(miscPartsReplaced);
								
								serviceInformation.setServiceDetail(serviceDetail);
								claim.setServiceInformation(serviceInformation);
								claim.setSource(AdminConstants.UPLOAD_WARRANTY_CLAIM);
								
								claims.add(claim);
								
								prepSt = conn.prepareStatement("update stg_draft_claim set upload_status = ? " +
								" where id = ?");
								prepSt.setString(1, YES_STRING); // Success								
								prepSt.setLong(2, stgDraftClaimId);
								prepSt.executeUpdate();
							}
							catch (Exception exception)
							{
								errorRecords++;
								logger.error("Error while trying to upload draft claim: ", exception);
								prepStatement = conn.prepareStatement("update stg_draft_claim set upload_status = ? " +
								" where id = ?");
								prepStatement.setString(1, NO_STRING); // Error
								prepStatement.setLong(2, stgDraftClaimId);
								prepStatement.executeUpdate();
							}
							finally {
								if (prepSt!=null)
									prepSt.close();
								if (prepStatement!=null)
									prepStatement.close();
							}
							
						} // End of ResultSet
						
						ps = conn.prepareStatement("update FILE_UPLOAD_MGT set success_records = (success_records - ?), " +
								" error_records = ( error_records + ?) where id = ?");
						ps.setLong(1, errorRecords);
						ps.setLong(2, errorRecords);
						ps.setLong(3, draftClaimId);
						ps.executeUpdate();
						UploadManagement upload = uploadManagementService.findByTemplateName(TEMPLATE_NAME);
						generateErrorFile(conn, draftClaimId, upload);
					}
					
				} catch (Exception e) {
					logger.error("Error while trying to upload draft claim: ", e);
				} finally {
					if (draftClaimRs!=null)
						draftClaimRs.close();
					if (ps != null)
						ps.close();
				}
				return null;
			}

		});

		for(Claim claim : claims)
			saveDraftClaim(claim);
		
		for(Long fileUploadId : draftClaims.keySet()) {
			fileReceiver.updateFileUploadStatus(fileUploadId, UploadStatusDetail.STATUS_UPLOADED);
		}
	}
	
	private void generateErrorFile(Connection conn,final long dataFileId, final UploadManagement dataUpload) 
	throws SQLException {
		fileReceiver.updateFileUploadStatus(dataFileId, UploadStatusDetail.STATUS_ERR_REPORTING);
		String stagingTable = dataUpload.getStagingTable();
		ReceivedFileDetails data = fileReceiver.getFileReceivedById(dataFileId,conn);
		try {
			{
				File tempFile = File.createTempFile("errorFile", "xls",new File(dataUploadConfig.getTempLocation()));
				errorReportGeneratorFactory.getErrorReportGenerator()
						.generateErrorReport(conn, new FileOutputStream(tempFile), 
								data, dataUpload);
				fileReceiver.writeErrorContentsToBlob(conn, dataFileId, tempFile);
				logger.info("Finished error report generation for Stagin table ["+ stagingTable + "]");
			}
		} catch (Exception exception) {
			logger.error("Error while generating error file for "+ stagingTable + 
					" upload for id: " + data.getId(), exception);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void saveDraftClaim(final Claim claim) {
		this.transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				try {
					populateFiledByUserForClaim(claim);
					populateDealerForClaim(claim);
					populateEquipmentAndServiceInformation(claim);
					if (InstanceOfUtil.isInstanceOfClass(CampaignClaim.class,
							claim))
						populateCampaignClaim((CampaignClaim) claim);
					storeDraftClaim(claim);
					if (InstanceOfUtil.isInstanceOfClass(CampaignClaim.class,
							claim))
						updateCampaignNotification((CampaignClaim) claim);
				} catch (Exception exception) {
					getTransactionTemplate().getTransactionManager().rollback(
							status);
					logger.error("Error [Upload draft claim] exception: ",
							exception);
				}
				return null;
			}
		});
	}

	private void updateCampaignNotification(CampaignClaim claim) {
		if (claim.getCampaign() != null && claim.getId() != null) {
			List<CampaignNotification> notifications = new ArrayList<CampaignNotification>();
			for (ClaimedItem claimedItem : claim.getClaimedItems()) {
				CampaignNotification cn = campaignService
						.findPendingCampaignNotification(claimedItem
								.getItemReference().getReferredInventoryItem(),
								((CampaignClaim) claim).getCampaign());
				cn.setNotificationStatus("COMPLETE");
				cn.setClaim(claim);
				notifications.add(cn);
			}
			campaignService.saveNotifications(notifications);
		}
	}
	
	private void populateCampaignClaim(CampaignClaim claim) {
		if (claim.getCampaign() == null
				|| StringUtils.isBlank(claim.getCampaign().getCode())) {
			return;
		}
		Campaign campaign = campaignService.findByCode(claim
				.getCampaign().getCode());
		claim.setCampaign(campaign);
		boolean partsReplacedInstalledSectionVisible = getConfigParamService()
				.getBooleanValue(
						ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
								.getName());
		boolean buPartReplaceableByNonBUPart = getConfigParamService()
				.getBooleanValue(
						ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName());
		fieldModificationClaimSubmissionUtil.populateReplacedPartsFromCampaign(
				campaign, claim, partsReplacedInstalledSectionVisible,
				buPartReplaceableByNonBUPart);
		fieldModificationClaimSubmissionUtil.populateMiscPartsFromCampaign(
				campaign, claim);
		fieldModificationClaimSubmissionUtil.populateLaborDetailsFromCampaign(
				campaign, claim);
		fieldModificationClaimSubmissionUtil
				.populateMiscellaneousDetailsFromCampaign(campaign, claim);
		
	}
	
	private void populateOemPartReplacedInformation(ServiceDetail serviceDetail, Currency dealerPreferredCurrency)
			throws CatalogException {
		
		// Populate the item information of Oem Parts (RepalcedInstalled)
		List<HussmanPartsReplacedInstalled> partsReplacedInstalled = serviceDetail.getHussmanPartsReplacedInstalled();
		for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : partsReplacedInstalled) {
			
			OEMPartReplaced partReplaced = hussmanPartsReplacedInstalled.getReplacedParts().get(0); // one to one relation between replaced and installed parts.
			if (partReplaced != null)
					if (partReplaced.getItemReference() != null && partReplaced.getItemReference().getReferredInventoryItem() != null)
						try {
							partReplaced.getItemReference()
									.setReferredInventoryItem(
											inventoryService
													.findMajorComponentBySerialNumber(
															partReplaced.getItemReference().getReferredInventoryItem()
																	.getSerialNumber()).get(0));
						} catch (ItemNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} else if (partReplaced.getItemReference() != null && partReplaced.getItemReference().getReferredItem() != null)
							partReplaced.getItemReference().setReferredItem(
								catalogService.findItemOwnedByManuf(partReplaced.getItemReference().getReferredItem()
										.getNumber()));
			InstalledParts partInstalled = hussmanPartsReplacedInstalled.getHussmanInstalledParts().get(0);
			if ( partInstalled.getItem()!=null)
				partInstalled.setItem(
						catalogService.findItemOwnedByManuf(partInstalled.getItem().getNumber()));
		}
		
		// Populate the item information of Non-Oem Parts Replaced
		List<NonOEMPartReplaced> nonOemPartReplaced = serviceDetail.getNonOEMPartsReplaced();
		if(nonOemPartReplaced != null)
		for (Iterator<NonOEMPartReplaced> iterator = nonOemPartReplaced.iterator(); iterator
				.hasNext();) {
			NonOEMPartReplaced nonOemPart = (NonOEMPartReplaced) iterator.next();
			if (nonOemPart.getPricePerUnit()!=null)
			{
				BigDecimal priceAmount = nonOemPart.getPricePerUnit().breachEncapsulationOfAmount();
				nonOemPart.setPricePerUnit(new Money(priceAmount, dealerPreferredCurrency)); 
			}
		}
	}
	
	private List<LaborDetail> createLaborPerformed(String claimType, String delimitedJobCodes, String delimitedLabourHours, 
				String delimitedReasonForLabourHours) {
		List<LaborDetail> laborPerformed = new ArrayList<LaborDetail>();
		if (CLAIM_TYPE_PARTS_WITHOUT_HOST.equalsIgnoreCase(claimType)
				&& StringUtils.isNotBlank(delimitedLabourHours)) {				
			LaborDetail laborDetail = new LaborDetail();
			laborDetail.setHoursSpent(BigDecimal.valueOf(Double.valueOf(delimitedLabourHours)));
			laborPerformed.add(laborDetail);
		} else if (StringUtils.isNotBlank(delimitedJobCodes)) {
			String[] jobCodes = delimitedJobCodes.split(",");
			String[] laborHours = new String[jobCodes.length];
			String[] reasonForLabourHours = new String[jobCodes.length];
			int i = 0;
			if(StringUtils.isNotBlank(delimitedLabourHours))
			for(String temp : delimitedLabourHours.split(",")) {
				laborHours[i++] = temp;
				if(i==laborHours.length)
					break;
			}
			i = 0 ;
			if(StringUtils.isNotBlank(delimitedReasonForLabourHours))
			for(String temp : delimitedReasonForLabourHours.split(DRAFT_CLAIM_DELIMITER)) {
				reasonForLabourHours[i++] = temp;
				if(i==reasonForLabourHours.length)
					break;
			}
			i = 0;
			for(String jobCode : jobCodes) {
				if (StringUtils.isNotBlank(jobCode)) {
					LaborDetail laborDetail = new LaborDetail();
					ServiceProcedure serviceProcedure = new ServiceProcedure();
					ServiceProcedureDefinition serviceProcedureDefinition = new ServiceProcedureDefinition();
					serviceProcedureDefinition.setCode(jobCode);
					serviceProcedure.setDefinition(serviceProcedureDefinition);
					laborDetail.setServiceProcedure(serviceProcedure);
					if (StringUtils.isNotBlank(laborHours[i]) && !"0".equals(laborHours[i])) {
						laborDetail.setAdditionalLaborHours(new BigDecimal(laborHours[i]));
						laborDetail.setReasonForAdditionalHours(reasonForLabourHours[i]); 
					}
					laborPerformed.add(laborDetail);
				}
				i++;
			}
		}
		return laborPerformed;
	}
	
	private List<NonOEMPartReplaced> createNonOemPartsReplaced(
			String nonOemPartsReplaced, String nonOemPartsReplacedQtys, String nonOemPartsReplacedPrices,
			String nonOemPartsReplacedDescs) {
		List<NonOEMPartReplaced> nonOemPartReplaced = new ArrayList<NonOEMPartReplaced>(); 
		if (StringUtils.isNotBlank(nonOemPartsReplaced))
		{
			String[] replacedNonOemParts = nonOemPartsReplaced.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedNonOemPartsQtys = nonOemPartsReplacedQtys.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedNonOemPartsPrices = nonOemPartsReplacedPrices.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedNonOemPartsDescs = nonOemPartsReplacedDescs.split(DRAFT_CLAIM_DELIMITER);
			if (replacedNonOemPartsQtys!=null && replacedNonOemPartsQtys.length>0)
			{
				for (int partIter = 0; partIter < replacedNonOemPartsQtys.length; 
						partIter++) {
					NonOEMPartReplaced nonOemPart = new NonOEMPartReplaced();
					nonOemPart.setNumberOfUnits(new Integer(replacedNonOemPartsQtys[partIter]));
					Double amount = null;
					try {
						amount = new Double(replacedNonOemPartsPrices[partIter]);
					}
					catch (NumberFormatException exception){
						amount = new Double(0);
					}
					nonOemPart.setPricePerUnit(Money.valueOf(new BigDecimal(amount), 
							CURRENCY_USD, BigDecimal.ROUND_HALF_UP));  
					nonOemPart.setDescription(replacedNonOemParts[partIter]+"-"+replacedNonOemPartsDescs[partIter]);
					nonOemPartReplaced.add(nonOemPart);
				}
			}
		}
		return nonOemPartReplaced;
	}

	private List<AlarmCode> createAlarmCodesList(String alarmCodes) {
		List<AlarmCode> alarmCodesList = new ArrayList<AlarmCode>();
		if (StringUtils.isNotBlank(alarmCodes)) {
			String[] alarmCodesArray = alarmCodes.split(",");
			for (int i = 0; i < alarmCodesArray.length; i++) {
				AlarmCode alarmCode = new AlarmCode();
				alarmCode.setCode(alarmCodesArray[i]);
				alarmCodesList.add(alarmCode);
			}
		}
		return alarmCodesList;
	}
	
	private List<OEMPartReplaced> createOemPartsReplaced(String oemPartsReplaced, String oemPartsReplacedQtys) {
		List<OEMPartReplaced> partsReplaced = new ArrayList<OEMPartReplaced>(); 
		if (StringUtils.isNotBlank(oemPartsReplaced))
		{
			String[] replacedOemParts = oemPartsReplaced.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedOEMPartsQty = oemPartsReplacedQtys.split(DRAFT_CLAIM_DELIMITER);
			if (replacedOemParts!=null && replacedOemParts.length>0)
			{
				for (int partIter = 0; partIter < replacedOemParts.length; 
						partIter++) {
					OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
					Item referredItem = new Item();
					referredItem.setNumber(replacedOemParts[partIter]);
					oemPartReplaced.setItemReference(new ItemReference(referredItem));
					oemPartReplaced.setShippedByOem(Boolean.FALSE);
					oemPartReplaced.setNumberOfUnits(
							new Integer(replacedOEMPartsQty[partIter]));
					partsReplaced.add(oemPartReplaced);
				}
			}
		}
			
		return partsReplaced;
	}
	
	private List<OEMPartReplaced> createOemPartsReplaced(String oemPartsSerialNums, String oemPartsReplaced, String oemPartsReplacedQtys) {
		List<OEMPartReplaced> partsReplaced = new ArrayList<OEMPartReplaced>(); 
		if (StringUtils.isNotBlank(oemPartsReplaced))
		{
			String[] replacedOemPartsSerialNums = (null != oemPartsSerialNums ? oemPartsSerialNums.split(DRAFT_CLAIM_DELIMITER): new String[0]);
			String[] replacedOemParts = oemPartsReplaced.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedOEMPartsQty = oemPartsReplacedQtys.split(DRAFT_CLAIM_DELIMITER);
			if (replacedOemParts!=null && replacedOemParts.length>0)
			{
				for (int partIter = 0; partIter < replacedOemParts.length; 
						partIter++) {
					OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
					Item referredItem = new Item();
					referredItem.setNumber(replacedOemParts[partIter]);
					
					oemPartReplaced.setItemReference(new ItemReference(referredItem));
					oemPartReplaced.setShippedByOem(Boolean.FALSE);
					oemPartReplaced.setNumberOfUnits(
							new Integer(replacedOEMPartsQty[partIter]));
					
					if(partIter < replacedOemPartsSerialNums.length && null !=replacedOemPartsSerialNums[partIter] ){
						oemPartReplaced.setSerialNumber(replacedOemPartsSerialNums[partIter]);
					}
					partsReplaced.add(oemPartReplaced);
				}
			}
		}
		return partsReplaced;
	}
	
	private List<InstalledParts> createOemPartsInstalled(String oemPartsInstalledSerialNumbers, String oemPartsInstalled, String oemPartsInstalledQtys) {
		
		List<InstalledParts> partsInstalled = new ArrayList<InstalledParts>(); 
		if (StringUtils.isNotBlank(oemPartsInstalled))
		{
			if (partsInstalled==null)
				partsInstalled = new ArrayList<InstalledParts>();
			
			String[] installedOemPartsSerialNums = (oemPartsInstalledSerialNumbers==null?
						(new String[0]):oemPartsInstalledSerialNumbers.split(DRAFT_CLAIM_DELIMITER)); 
			String[] installedOemParts = oemPartsInstalled.split(DRAFT_CLAIM_DELIMITER);
			String[] installedOEMPartsQty = oemPartsInstalledQtys.split(DRAFT_CLAIM_DELIMITER);
			if (installedOemParts!=null && installedOemParts.length>0)
			{
				for (int partIter = 0; partIter < installedOemParts.length; 
						partIter++) {
					InstalledParts oemPartInstalled = new InstalledParts();
					
					
					if(partIter < installedOemPartsSerialNums.length && null !=installedOemPartsSerialNums[partIter] ){
					
						Item referredItem = new Item();
						referredItem.setNumber(installedOemParts[partIter]);
						oemPartInstalled.setSerialNumber(installedOemPartsSerialNums[partIter] );
						oemPartInstalled.setItem(referredItem);
					} else{
						Item referredItem = new Item();
						referredItem.setNumber(installedOemParts[partIter]);
						oemPartInstalled.setItem(referredItem);
					}
					
					oemPartInstalled.setShippedByOem(Boolean.FALSE);
					oemPartInstalled.setNumberOfUnits(
							new Integer(installedOEMPartsQty[partIter]));
					partsInstalled.add(oemPartInstalled);
				}
			}
		}
			
		return partsInstalled;
	}
	
	private List<NonOEMPartReplaced> createOemPartsReplacedInstalled(
			String nonOemPartsReplaced, String nonOemPartsReplacedQtys, String nonOemPartsReplacedPrices,
			String nonOemPartsReplacedDescs) {
		List<NonOEMPartReplaced> nonOemPartReplaced = null; 
		if (StringUtils.isNotBlank(nonOemPartsReplaced))
		{
			if (nonOemPartReplaced==null)
				nonOemPartReplaced = new ArrayList<NonOEMPartReplaced>();
			String[] replacedNonOemParts = nonOemPartsReplaced.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedNonOemPartsQtys = nonOemPartsReplacedQtys.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedNonOemPartsPrices = nonOemPartsReplacedPrices.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedNonOemPartsDescs = nonOemPartsReplacedDescs.split(DRAFT_CLAIM_DELIMITER);
			if (replacedNonOemPartsQtys!=null && replacedNonOemPartsQtys.length>0)
			{
				for (int partIter = 0; partIter < replacedNonOemPartsQtys.length; 
						partIter++) {
					NonOEMPartReplaced nonOemPart = new NonOEMPartReplaced();
					nonOemPart.setNumberOfUnits(new Integer(replacedNonOemPartsQtys[partIter]));
					Double amount = null;
					try {
						amount = new Double(replacedNonOemPartsPrices[partIter]);
					}
					catch (NumberFormatException exception){
						amount = new Double(0);
					}
					nonOemPart.setPricePerUnit(Money.valueOf(new BigDecimal(amount), 
							CURRENCY_USD, BigDecimal.ROUND_HALF_UP));  
					nonOemPart.setDescription(replacedNonOemParts[partIter]+"-"+replacedNonOemPartsDescs[partIter]);
					nonOemPartReplaced.add(nonOemPart);
				}
			}
		}
		return nonOemPartReplaced;
	}
	
private List<InstalledParts> createOemPartsInstalled(String oemPartsInstalled, String oemPartsInstalledQtys) {
		
		List<InstalledParts> partsInstalled = new ArrayList<InstalledParts>(); 
		if (StringUtils.isNotBlank(oemPartsInstalled))
		{
			if (partsInstalled==null)
				partsInstalled = new ArrayList<InstalledParts>();
			String[] installedOemParts = oemPartsInstalled.split(DRAFT_CLAIM_DELIMITER);
			String[] installedOEMPartsQty = oemPartsInstalledQtys.split(DRAFT_CLAIM_DELIMITER);
			if (installedOemParts!=null && installedOemParts.length>0)
			{
				for (int partIter = 0; partIter < installedOemParts.length; 
						partIter++) {
					InstalledParts oemPartInstalled = new InstalledParts();
					Item referredItem = new Item();
					referredItem.setNumber(installedOemParts[partIter]);
					oemPartInstalled.setItem(referredItem);
					oemPartInstalled.setShippedByOem(Boolean.FALSE);
					oemPartInstalled.setNumberOfUnits(
							new Integer(installedOEMPartsQty[partIter]));
					partsInstalled.add(oemPartInstalled);
				}
			}
		}
			
		return partsInstalled;
	}
	
	private List<NonOEMPartReplaced> createMiscPartsReplaced(
			String miscPartsReplaced, String miscPartsReplacedQtys) {
		List<NonOEMPartReplaced> miscPartReplaced = new ArrayList<NonOEMPartReplaced>();
		if (StringUtils.isNotBlank(miscPartsReplaced))
		{
			String[] replacedMiscParts = miscPartsReplaced.split(DRAFT_CLAIM_DELIMITER);
			String[] replacedMiscPartsQtys = miscPartsReplacedQtys.split(DRAFT_CLAIM_DELIMITER);
			if (replacedMiscPartsQtys!=null && replacedMiscPartsQtys.length>0)
			{
				for (int partIter = 0; partIter < replacedMiscPartsQtys.length; 
						partIter++) {
					NonOEMPartReplaced miscPart = new NonOEMPartReplaced();
					miscPart.setNumberOfUnits(new Integer(replacedMiscPartsQtys[partIter]));
					miscPart.setMiscItemConfig(new MiscellaneousItemConfiguration());
					miscPart.getMiscItemConfig().setMiscellaneousItem(new MiscellaneousItem());
					miscPart.getMiscItemConfig().getMiscellaneousItem().setPartNumber(replacedMiscParts[partIter]);
					miscPartReplaced.add(miscPart);
				}
			}
		}
		return miscPartReplaced;
	}
	
	
	private void populateEquipmentAndServiceInformation(Claim claim) throws Exception {
		BusinessUnitInfo businessUnitInfo = null;
		Item modelForClaim = new Item();
		
		// we are setting servicing location information.
		if(claim.getServicingLocation() != null){						
			claim.setServicingLocation(orgService.getOrgAddressBySiteNumberForUpload(claim.getServicingLocation().getSiteNumber(), 
                    claim.getFiledBy().getBelongsToOrganization().getId()));
		}		
		
		if (CollectionUtils.isNotEmpty(claim.getClaimedItems()))
		{
			if(claim.getServiceManagerRequest() && claim.getReasonForServiceManagerRequest() != null) {
				Long smrReason = claim.getReasonForServiceManagerRequest().getId();
				claim.setReasonForServiceManagerRequest(
						(SmrReason) lovRepository.findById(
						SmrReason.class.getSimpleName(), smrReason));
			}
			
			if(claim.getClaimCompetitorModel() != null) {
				Long comModel = claim.getClaimCompetitorModel().getId();
				claim.setClaimCompetitorModel(
						(ClaimCompetitorModel) lovRepository.findById(
								ClaimCompetitorModel.class.getSimpleName(), comModel));
			}
			
			if (claim.getAlarmCodes() != null && claim.getAlarmCodes().size()!=0 ) {
				List<String> codeList = new ArrayList<String>(claim.getAlarmCodes().size());
				for (AlarmCode alarmCode : claim.getAlarmCodes()) {
					codeList.add(alarmCode.getCode());
				}
				claim.setAlarmCodes(alarmCodeRepository.getACListFromAlarmCodes(codeList));
			}
			
			if (claim.getPartItemReference() != null) {
				ItemReference itemReference = new ItemReference();
				if (claim.getPartItemReference().getReferredInventoryItem() != null
					&& claim.getPartItemReference().getReferredInventoryItem().getId() != null) {
					InventoryItem inventoryItem = this.inventoryService.findInventoryItem(
							claim.getPartItemReference().getReferredInventoryItem().getId());
					inventoryItem.getOfType();
					itemReference.setReferredInventoryItem(inventoryItem);
					modelForClaim = itemReference.getUnserializedItem();
					businessUnitInfo = inventoryItem.getBusinessUnitInfo();
				} else if (claim.getPartItemReference().getReferredItem() != null
						&& claim.getPartItemReference().getReferredItem().getId() !=null ) {
					Item referredItem = catalogService.findById(
							claim.getPartItemReference().getReferredItem().getId());	
					itemReference.setReferredItem(referredItem);
//					businessUnitInfo = referredItem.getBusinessUnitInfo();
				}
				claim.setPartItemReference(itemReference);
			}
			
			claim.getClaimedItems().get(0).setClaim(claim);
			if (claim.getClaimedItems().get(0).getItemReference()!=null) {
				ItemReference itemReference = claim.getClaimedItems().get(0).getItemReference();
				ItemReference equipItemReference = new ItemReference();
				if (itemReference.getReferredInventoryItem() != null &&
						itemReference.getReferredInventoryItem().getId() != null) {
					InventoryItem inventoryItem = this.inventoryService.findInventoryItem(
									itemReference.getReferredInventoryItem().getId());
					inventoryItem.getOfType();
					equipItemReference.setReferredInventoryItem(inventoryItem);
					modelForClaim = equipItemReference.getUnserializedItem();
					businessUnitInfo = inventoryItem.getBusinessUnitInfo();
				} else if(itemReference.getReferredItem()!=null &&
						itemReference.getReferredItem().getId()!=null) {
					Item referredItem = catalogService.findById(itemReference.getReferredItem().getId());
					equipItemReference.setReferredItem(referredItem);
					equipItemReference.setModel(referredItem.getModel());
					equipItemReference.setUnszdSlNo(itemReference.getUnszdSlNo());
//					businessUnitInfo = referredItem.getBusinessUnitInfo();
					modelForClaim = referredItem;
				} else if (itemReference.getModel()!=null && itemReference.getModel().getId()!=null) {
					ItemGroup model = catalogService.findItemGroup(itemReference.getModel().getId());
					equipItemReference.setModel(model);
					equipItemReference.setSerialized(false);
					equipItemReference.setUnszdSlNo(itemReference.getUnszdSlNo());
					businessUnitInfo = model.getBusinessUnitInfo();
					modelForClaim = null;
				} else {
					equipItemReference.setSerialized(false);
				}
				claim.getClaimedItems().get(0).setItemReference(equipItemReference);
			}
			if (businessUnitInfo!=null)
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(businessUnitInfo.getName());
//			claim.setBusinessUnitInfo(businessUnitInfo);
		}

		if ( claim.getServiceInformation()!=null)
		{
			// Causal part preparation
			if (claim.getServiceInformation().getCausalPart()!=null && 
					StringUtils.isNotBlank(claim.getServiceInformation().getCausalPart().getNumber()))
			{
				claim.getServiceInformation().setCausalPart(
						catalogService.findItemOwnedByManuf(
								claim.getServiceInformation().getCausalPart().getNumber()));
				ItemGroup model = null;
				if (claim.getServiceInformation().getCausalPart().getModel()!=null)
				{// Need to see how to use this further if required. May throw Lazy error if we remove this!
					model = claim.getServiceInformation().getCausalPart().getModel();
					model.getName();
				}
			}
			try {
				// Fault Code Preparation
				if (StringUtils.isNotBlank(claim.getServiceInformation().getFaultCode()))
				{
					if(modelForClaim!=null)
					{
                        claim.getServiceInformation().setFaultCodeRef(getFaultCodeRef(claim.getClaimedItems().get(0).getItemReference(),claim.getServiceInformation().getFaultCode()));
//					claim.getServiceInformation().setFaultCodeRef(failureStructureService
//						.findFaultCode(modelForClaim, claim.getServiceInformation().getFaultCode()));
					}
					else if(claim.getClaimedItems().get(0).getItemReference()!=null && claim.getClaimedItems().get(0).getItemReference().getModel()!=null)
					{
						claim.getServiceInformation().setFaultCodeRef(failureStructureService
							.findFaultCodeByItemGroup(claim.getClaimedItems().get(0).getItemReference().getModel(), claim.getServiceInformation().getFaultCode()));						
					}
				}
			} catch (Exception exception)
			{
				logger.error("Error at Draft Claim: in Fault Code: ", exception);
			}

			try {
				// Fault Found Preparation
				if (claim.getServiceInformation().getFaultFound()!=null && 
						StringUtils.isNotBlank(claim.getServiceInformation().getFaultFound().getName())) {
							claim.getServiceInformation().setFaultFound(failureStructureService
									.findFaultFoundByName(claim.getServiceInformation().getFaultFound().getName()));
							if(claim.getServiceInformation().getCausedBy() != null &&
									claim.getServiceInformation().getCausedBy().getId() != null)
								claim.getServiceInformation().setCausedBy(failureStructureService
									.findFailureCauseDefinitionById(
											claim.getServiceInformation().getCausedBy().getId()));
				}
			} catch (Exception exception)
			{
				logger.error("Error at Draft Claim: in Fault Found: ", exception);
			}
			
			try {
				// Root Cause Preparation
				if (claim.getServiceInformation().getRootCause()!=null &&
						StringUtils.isNotBlank(claim.getServiceInformation().getRootCause().getName()) )
					claim.getServiceInformation().setRootCause(failureStructureService
						.findFailureRootCauseDefinitionByName(claim.getServiceInformation().getRootCause().getName()));
			} catch (Exception exception)
			{
				logger.error("Error at Draft Claim: in Root Cause: ", exception);
			}

			try {
				// Technician preparation
				if (claim.getServiceInformation().getServiceDetail() != null) 
				{
					if ( claim.getServiceInformation().getServiceDetail().getTechnician() != null
							&& StringUtils.isNotBlank(claim.getServiceInformation()
							.getServiceDetail().getTechnician().getName()))
						claim.getServiceInformation().getServiceDetail()
								.setTechnician(orgService.findUserByName(claim.getServiceInformation()
												.getServiceDetail().getTechnician().getName()));
					
					if (CollectionUtils.isNotEmpty(claim.getServiceInformation().getServiceDetail().getLaborPerformed()))
					{
						List<LaborDetail> laborPerformed = claim.getServiceInformation().getServiceDetail().getLaborPerformed();
						for(LaborDetail laborDetail : laborPerformed) {
							if (laborDetail.getServiceProcedure() != null && 
									laborDetail.getServiceProcedure().getDefinition()!=null)
							{
//								laborDetail
//								.setServiceProcedure(failureStructureService.findServiceProcedure(modelForClaim, 
//										laborDetail.getServiceProcedure().getDefinition().getCode()));
//								;
                                laborDetail.setServiceProcedure(getServiceProcedure(claim.getClaimedItems().get(0).getItemReference(),
                                        laborDetail.getServiceProcedure().getDefinition().getCode()));
							}
						}
					}
					populateOemPartReplacedInformation(claim.getServiceInformation().getServiceDetail(), 
							claim.getForDealer().getPreferredCurrency());
					populateMiscellaneousParts(claim);
				}
			} catch (Exception exception)
			{
				logger.error("Error at Draft Claim: in Technician: ", exception);
			}
			
		}
	}

	private void populateMiscellaneousParts(Claim claim) {
		List<NonOEMPartReplaced> miscParts = claim.getServiceInformation()
				.getServiceDetail().getMiscPartsReplaced();
		if (miscParts != null) {
			miscParts.removeAll(Collections.singleton(null));
		}
		if (miscParts != null && miscParts.size() > 0) {
			ServiceProvider dealer = claim.getForDealer();
			Iterator<NonOEMPartReplaced> it = miscParts.iterator();
			while (it.hasNext()) {
				NonOEMPartReplaced element = it.next();
				MiscellaneousItemConfiguration miscellaneousItemConfig = this.miscellaneousItemConfigService.
        			findMiscellanousPartConfigurationForDealerAndMiscPart(dealer.getId(),
        					element.getMiscItemConfig().getMiscellaneousItem().getPartNumber());
				element.setMiscItem(miscellaneousItemConfig.getMiscellaneousItem());
				element.setMiscItemConfig(miscellaneousItemConfig);
			}
		}
	}
	
	private void populateFiledByUserForClaim(Claim claim) {
		if ( claim.getFiledBy()!=null && claim.getFiledBy().getId()!=null )
		{
			User user = orgService.findUserById(claim.getFiledBy().getId());
			new tavant.twms.security.authz.infra.SecurityHelper().populateTestUserCredentials(user);
			// Above code should work but for a safety pre-caution we populate user again
			claim.setFiledBy(user);
		}
	}

	private void populateDealerForClaim(Claim claim) {
		if (claim.getFiledBy() == null
				|| claim.getFiledBy().getBelongsToOrganizations() == null)
			return;
		for (Organization org : claim.getFiledBy().getBelongsToOrganizations())
			if ((InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, org)))
				claim.setForDealer(new HibernateCast<ServiceProvider>()
						.cast(org));
	}
	
	void storeDraftClaim(Claim claim){
		if (claim.getPartItemReference() != null
				&& (claim.getPartItemReference().getReferredInventoryItem() != null || claim.getPartItemReference()
						.getReferredItem() != null)) {
			if (getConfigParamService().getBooleanValue(ConfigName.CONSIDER_WARRANTY_COVERAGE_FOR_PART_CLAIM.getName()))
				setPolicyOnClaimedParts(claim);
		} else {
			setPolicyOnClaimedItems(claim);
		}
		prepareAttributesForClaim(claim);		
        this.claimService.initializeClaim(claim);
        
        this.claimService.createClaim(claim);
        this.claimProcessService.startClaimProcessingWithTransition(claim, "Draft");
       
	}

    private void prepareAttributesForClaim(Claim claim) {
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            List<AdditionalAttributes> additionalAttributes = new ArrayList<AdditionalAttributes>();
            if (claimedItem.getItemReference()!=null && claimedItem.getItemReference().getReferredInventoryItem()!=null &&
            		claimedItem.getItemReference().getReferredInventoryItem().getOfType()!=null)
            {
        	   List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();
               itemGroups.add(claimedItem.getItemReference().getReferredInventoryItem().getOfType().getModel());
               itemGroups.add(claimedItem.getItemReference().getReferredInventoryItem().getOfType().getProduct());
               if(!itemGroups.isEmpty()){
              	 additionalAttributes = this.attributeAssociationService
                  .findAttributesForItemGroups(itemGroups,  claim.getType(),AttributePurpose.CLAIMED_INVENTORY_PURPOSE);
          
               }
		        for (AdditionalAttributes addAttribute : additionalAttributes) {
		            claimedItem.addClaimAttributes(new ClaimAttributes(addAttribute, null));
		        }		        
            }
        }
    }

	void setPolicyOnClaimedItems(Claim theClaim) {
		if (!theClaim.canPolicyBeComputed()) {
			return;
		}
		Claim claim = theClaim;
		for (ClaimedItem claimedItem : theClaim.getClaimedItems()) {
			try {
				claimedItem.setClaim(claim);
				Policy applicablePolicy = this.policyService
						.findApplicablePolicy(claimedItem);
				claimedItem.setApplicablePolicy(applicablePolicy);
			} catch (PolicyException e) {
				throw new RuntimeException(
						"Failed to find policy for Claimed Item [ " + claimedItem + "]", e);
			}
		}
	}
    
	void setPolicyOnClaimedParts(Claim theClaim) {
		claimSubmissionUtil.setPolicyOnClaimedParts(theClaim);
	}
	
	private String draftClaimQuery() {
		return SELECT_CLAUSE + COL_CLAIM_TYPE + SELECT_DELIMITER + 
				COL_UNIQUE_IDENTIFIER + SELECT_DELIMITER + 
				COL_TRUCK_SERIAL_NUMBER + SELECT_DELIMITER + 
				COL_SERIAL_NUMBER_ID + SELECT_DELIMITER + 
				COL_ITEM_NUMBER_ID + SELECT_DELIMITER + 
				COL_MODEL_ID + SELECT_DELIMITER +
				COL_BUSINESS_UNIT_NAME + SELECT_DELIMITER + 
				COL_WORK_ORDER_NUMBER + SELECT_DELIMITER +  
				COL_FAILURE_DATE + SELECT_DELIMITER + 
				COL_REPAIR_END_DATE + SELECT_DELIMITER + 
				COL_SMR_CLAIM + SELECT_DELIMITER + 
				COL_REASON_FOR_SMR_CLAIM + SELECT_DELIMITER +
				COL_PART_ITEM_NUMBER + SELECT_DELIMITER + COL_PART_ID + SELECT_DELIMITER + 
				COL_INSTALLATION_DATE + SELECT_DELIMITER + 
				COL_CAUSAL_PART + SELECT_DELIMITER + COL_FAULT_CODE + SELECT_DELIMITER + 
				COL_FAULT_FOUND + SELECT_DELIMITER + COL_CAUSED_BY + SELECT_DELIMITER + 
				COL_ROOT_CAUSE + SELECT_DELIMITER + COL_JOB_CODE + SELECT_DELIMITER + 
				COL_LABOUR_HOURS + SELECT_DELIMITER + COL_TECHNICIAN_ID + SELECT_DELIMITER + 
				COL_CONDITIONS_FOUND + SELECT_DELIMITER + COL_PROBABLE_CAUSE + SELECT_DELIMITER + 
				COL_WORK_PERFORMED + SELECT_DELIMITER + COL_GENERAL_COMMENTS + SELECT_DELIMITER + 
				COL_HOURS_IN_SERVICE + SELECT_DELIMITER + COL_ID + SELECT_DELIMITER + 
				COL_CAMPAIGN_CODE + SELECT_DELIMITER + COL_SERVICING_LOCATION_ID + SELECT_DELIMITER + 
				COL_INVOICE_NUMBER + SELECT_DELIMITER + COL_HOURS_ON_PARTS + SELECT_DELIMITER + 
				COL_REASON_FOR_EXTRA_LABOR_HOURS + SELECT_DELIMITER + 
				COL_REPLACED_OEM_PARTS + SELECT_DELIMITER +
				COL_REPLACED_OEM_PARTS_SERIAL_NUM + SELECT_DELIMITER +
				COL_INSTALLED_OEM_PARTS + SELECT_DELIMITER +
				COL_INSTALLED_OEM_PARTS_QUANTITY + SELECT_DELIMITER +
				COL_INSTALLED_OEM_PARTS_SERIAL_NUM + SELECT_DELIMITER +
				COL_REPLACED_OEM_PARTS_QUANTITY + SELECT_DELIMITER +
				COL_REPLACED_NON_OEM_PARTS + SELECT_DELIMITER +
				COL_REPLACED_NON_OEM_PARTS_QUANTITY + SELECT_DELIMITER +
				COL_REPLACED_NON_OEM_PARTS_PRICE + SELECT_DELIMITER +
				COL_REPLACED_NON_OEM_PARTS_DESC + SELECT_DELIMITER +
				COL_MISC_PARTS + SELECT_DELIMITER +
				COL_MISC_PARTS_QUANTITY + SELECT_DELIMITER +
				COL_COMMERCIAL_POLICY + SELECT_DELIMITER +
				COL_PART_SERIAL_ID + SELECT_DELIMITER +
				COL_COMPETITOR_MODEL_SERIAL_NUMBER + SELECT_DELIMITER +
				COL_COMPETITOR_MODEL_BRAND + SELECT_DELIMITER +
				COL_COMPETITOR_MODEL + SELECT_DELIMITER +
				COL_ALARM_CODES + SELECT_DELIMITER +
				COL_REPAIR_START_DATE + SELECT_DELIMITER +
				COL_BRAND + SELECT_DELIMITER +
				COL_HOURS_ON_TRUCK_DURING_INSTALL + SELECT_DELIMITER +
				COL_AUTHORIZATION_RECEIVED + SELECT_DELIMITER +
				COL_AUTHORIZATION_NUMBER + SELECT_DELIMITER +
				COL_CONTACT_MANAGEMENT_TICKET_NUM +
				" from stg_draft_claim " +
				" where ERROR_STATUS = 'Y' and NVL(upload_status, 'N') = 'N' " +
				" AND file_upload_mgt_id = ? ";
	}

	public ReportTaskDAO getReportTaskDAO() {
		return reportTaskDAO;
	}

	public void setReportTaskDAO(ReportTaskDAO reportTaskDAO) {
		this.reportTaskDAO = reportTaskDAO;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public AttributeAssociationService getAttributeAssociationService() {
		return attributeAssociationService;
	}

	public void setAttributeAssociationService(
			AttributeAssociationService attributeAssociationService) {
		this.attributeAssociationService = attributeAssociationService;
	}

	public ClaimProcessService getClaimProcessService() {
		return claimProcessService;
	}

	public void setClaimProcessService(ClaimProcessService claimProcessService) {
		this.claimProcessService = claimProcessService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public DomainRepository getDomainRepository() {
		return domainRepository;
	}

	public void setDomainRepository(DomainRepository domainRepository) {
		this.domainRepository = domainRepository;
	}

	public PolicyService getPolicyService() {
		return policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public FailureStructureService getFailureStructureService() {
		return failureStructureService;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public ClaimRepository getClaimRepository() {
		return claimRepository;
	}

	public void setClaimRepository(ClaimRepository claimRepository) {
		this.claimRepository = claimRepository;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	
	public void setAlarmCodeRepository(AlarmCodeRepository alarmCodeRepository) {
		this.alarmCodeRepository = alarmCodeRepository;
	}

	public CampaignService getCampaignService() {
		return campaignService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}
	
	public ClaimSubmissionUtil getClaimSubmissionUtil() {
		return claimSubmissionUtil;
	}

	public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
		this.claimSubmissionUtil = claimSubmissionUtil;
	}
	
    private static final String COL_REPLACED_NON_OEM_PARTS_DESC = "REPLACED_NON_OEM_PARTS_DESC";

	private static final String COL_REPLACED_NON_OEM_PARTS_PRICE = "REPLACED_NON_OEM_PARTS_PRICE";

	private static final String COL_REPLACED_NON_OEM_PARTS_QUANTITY = "REPLACED_NON_OEM_PARTS_QTY";

	private static final String COL_REPLACED_NON_OEM_PARTS = "REPLACED_NON_OEM_PARTS";

	private static final String COL_REPLACED_OEM_PARTS_QUANTITY = "REMOVED_OEM_PARTS_QUANTITY";

	private static final String COL_REPLACED_OEM_PARTS = "REMOVED_OEM_PARTS";

	private static final String COL_REPLACED_OEM_PARTS_SERIAL_NUM = "REPLACED_OEM_PARTS_SERIAL_NUM";

	private static final String COL_INSTALLED_OEM_PARTS = "INSTALLED_OEM_PARTS";

	private static final String COL_INSTALLED_OEM_PARTS_QUANTITY = "INSTALLED_OEM_PARTS_QUANTITY";

	private static final String COL_INSTALLED_OEM_PARTS_SERIAL_NUM = "INSTALLED_OEM_PARTS_SERIAL_NUM";

	private static final String COL_REASON_FOR_EXTRA_LABOR_HOURS = "REASON_FOR_EXTRA_LABOR_HOURS";

	private static final String COL_HOURS_ON_PARTS = "HOURS_ON_PARTS";

	private static final String COL_INVOICE_NUMBER = "INVOICE_NUMBER";

	private static final String COL_SERVICING_LOCATION_ID = "SERVICING_LOCATION_ID";

	private static final String COL_CAMPAIGN_CODE = "CAMPAIGN_CODE";

	private static final String COL_ID = "ID";
	
	private static final String COL_HOURS_IN_SERVICE = "HOURS_ON_TRUCK";

	private static final String COL_GENERAL_COMMENTS = "CLAIM_NOTES";

	private static final String COL_WORK_PERFORMED = "WORK_PERFORMED";

	private static final String COL_PROBABLE_CAUSE = "PROBABLE_CAUSE";

	private static final String COL_CONDITIONS_FOUND = "CONDITIONS_FOUND";

	private static final String COL_TECHNICIAN_ID = "TECHNICIAN_ID";

	private static final String COL_LABOUR_HOURS = "LABOUR_HOURS";

	private static final String COL_JOB_CODE = "JOB_CODE";

	private static final String COL_ROOT_CAUSE = "FAILURE_DETAIL";
	
	private static final String COL_ALARM_CODES = "ALARM_CODES"; 
	
	private static final String COL_CAUSED_BY = "CAUSED_BY_ID";

	private static final String COL_FAULT_FOUND = "FAULT_FOUND";

	private static final String COL_FAULT_CODE = "FAULT_LOCATION";

	private static final String COL_CAUSAL_PART = "CAUSAL_PART";

	private static final String COL_INSTALLATION_DATE = "INSTALLATION_DATE";

	private static final String COL_PART_ITEM_NUMBER = "PART_ITEM_NUMBER";
	private static final String COL_PART_ID = "PART_ID";
	private static final String COL_PART_SERIAL_NUMBER = "PART_SERIAL_NUMBER";
	private static final String COL_PART_SERIAL_ID = "PART_SERIAL_ID";
	private static final String COL_PART_NUMBER = "PART_NUMBER";
	
	private static final String COL_REASON_FOR_SMR_CLAIM = "REASON_FOR_SMR_CLAIM";

	private static final String COL_SMR_CLAIM = "SMR_CLAIM";
	
	private static final String COL_COMMERCIAL_POLICY =  "COMMERCIAL_POLICY";
	
	private static final String COL_COMPETITOR_MODEL_ID =  "COMPETITOR_MODEL_ID";
	
	private static final String COL_COMPETITOR_MODEL =  "COMPETITOR_MODEL";

	private static final String COL_COMPETITOR_MODEL_BRAND =  "BRAND_ON_COMPETITOR_MODEL";

	private static final String COL_COMPETITOR_MODEL_SERIAL_NUMBER =  "COMPETITOR_MODEL_SERIAL_NUMBER";


	private static final String COL_REPAIR_END_DATE = "REPAIR_END_DATE";

	private static final String COL_FAILURE_DATE = "FAILURE_DATE";

	private static final String SELECT_DELIMITER = ", ";

	private static final String COL_WORK_ORDER_NUMBER = "WORK_ORDER_NUMBER";

	private static final String COL_BUSINESS_UNIT_NAME = "BUSINESS_UNIT_NAME";

	private static final String COL_ITEM_NUMBER = "ITEM_NUMBER";
	private static final String COL_ITEM_NUMBER_ID = "ITEM_NUMBER_ID";
	private static final String COL_MODEL_NUMBER = "MODEL_NUMBER";
	private static final String COL_MODEL_ID = "MODEL_ID";
	private static final String COL_TRUCK_SERIAL_NUMBER = "TRUCK_SERIAL_NUMBER";
	private static final String COL_SERIAL_NUMBER_ID = "SERIAL_NUMBER_ID";
	private static final String COL_CONTAINER_NUMBER = "CONTAINER_NUMBER";
	
	private static final String COL_UNIQUE_IDENTIFIER = "UNIQUE_IDENTIFIER";

	private static final String COL_CLAIM_TYPE = "CLAIM_TYPE";
	
	private static final String COL_MISC_PARTS = "MISCELLANEOUS_PARTS";
	private static final String COL_MISC_PARTS_QUANTITY = "MISC_PARTS_QUANTITY";

	public static final String DRAFT_CLAIM_DELIMITER = "#\\$#";

	public static final String DRAFT_CLAIM_DATE_FORMAT_YYYYMMDD = "yyyyMMdd";

	public static final String CLAIM_TYPE_FIELD_MODIFICATION = "FPI";
	
	public static final String FIELD_MODIFICATION = "FIELD MODIFICATION";

	public static final String CLAIM_TYPE_PARTS_WITHOUT_HOST = "PART INSTALLED ON NON-SERIALIZED HOST TRUCK";

	public static final String CLAIM_TYPE_PARTS_WITH_HOST = "PART INSTALLED ON SERIALIZED HOST TRUCK";

	public static final String CLAIM_TYPE_MACHINE_NON_SERIALIZED = "MACHINE NON SERIALIZED";

	public static final String CLAIM_TYPE_MACHINE_SERIALIZED = "MACHINE SERIALIZED";

	// public static final String CLAIM_TYPE_ATTACHMENT_NON_SERIALIZED = "ATTACHMENT NON SERIALIZED";

	//public static final String CLAIM_TYPE_ATTACHMENT_SERIALIZED = "ATTACHMENT SERIALIZED";

	private static final String SELECT_CLAUSE = "select ";

	private static final Currency CURRENCY_USD = Currency.getInstance("USD");

	private static final String NO_STRING = "N";

	private static final String YES_STRING = "Y";
	
	private static final String COL_REPAIR_START_DATE = "REPAIR_START_DATE";
	
	private static final String COL_BRAND = "BRAND";
	
	private static final String COL_HOURS_ON_TRUCK_DURING_INSTALL = "HOURS_ON_TRUCK_DURING_INSTALL";
	
	private static final String COL_AUTHORIZATION_RECEIVED = "AUTHORIZATION_RECEIVED";
	
	private static final String COL_AUTHORIZATION_NUMBER = "AUTHORIZATION_NUMBER";
	
	private static final String COL_CONTACT_MANAGEMENT_TICKET_NUM = "CONTACT_MANAGEMENT_TICKET_NUM";

    private FaultCode getFaultCodeRef(ItemReference itemReference, String faultCode) {
        FaultCode faultCodeRef = null;
        if(itemReference.getReferredInventoryItem().getOfType().getProduct() != null){
            faultCodeRef = failureStructureService.findFaultCodeByItemGroup(itemReference.getReferredInventoryItem().getOfType().getProduct(), faultCode);
        }
        if(faultCodeRef == null){
            faultCodeRef = failureStructureService.findFaultCodeByItemGroup(itemReference.getReferredInventoryItem().getOfType().getModel(), faultCode);
        }
        return faultCodeRef;
    }

    private ServiceProcedure getServiceProcedure(ItemReference itemReference, String code) {
        ServiceProcedure serviceProcedure = null;
        if(itemReference.getReferredInventoryItem().getOfType().getProduct() != null){
            FailureStructure failureStructure = failureStructureService.getFailureStructureForItemGroup(itemReference.getReferredInventoryItem().getOfType().getProduct());
            if(failureStructure != null)
                serviceProcedure = failureStructure.findSeriveProcedure(code);
        }
        if(serviceProcedure == null){
            FailureStructure failureStructure = failureStructureService.getFailureStructureForItemGroup(itemReference.getReferredInventoryItem().getOfType().getModel());
            if(failureStructure != null)
                serviceProcedure = failureStructure.findSeriveProcedure(code);
        }
        return serviceProcedure;
    }

}