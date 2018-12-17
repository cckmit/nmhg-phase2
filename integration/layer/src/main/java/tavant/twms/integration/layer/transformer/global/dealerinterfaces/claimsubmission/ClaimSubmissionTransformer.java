package tavant.twms.integration.layer.transformer.global.dealerinterfaces.claimsubmission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.alarmcode.AlarmCodeService;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimXMLConverter;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.MarketingGroupsLookup;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.integration.layer.component.global.dealerinterfaces.claimsubmission.ClaimSubmissionProcessor;
import tavant.twms.integration.layer.component.global.dealerinterfaces.claimsubmission.ClaimSubmssionValidator;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.IntegrationLayerUtil;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.AlarmCodes;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.AlarmCodes.EachAlarmCode;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.ComponentsReplaced;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.ComponentsReplaced.NMHGInstalledParts;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.ComponentsReplaced.NMHGReplacedParts;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.MiscellanousParts;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.MiscellanousParts.EachMiscellanousPart;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.ReplacedNonNMHGParts;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.ReplacedNonNMHGParts.EachPart;

public class ClaimSubmissionTransformer implements BeanFactoryAware {

	private final Logger logger = Logger.getLogger("dealerAPILogger");

	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

	private InventoryService inventoryService;

	protected OrgService orgService;

	private TravelDetailsCalculator travelDetailsCalculator;

	private BatchClaimCostCategoryConfigurationDetails batchClaimCostCategoryConfigurationDetails;

	private ConfigParamService configParamService;

	private CampaignService campaignService;

	private FailureStructureService failureStructureService;

	private final static String MACHINE = "MACHINE";

	public final static String IS_SERIALIZED = "IS_SERIALIZED";

	private final static String PARTS = "PARTS";

	private final static String FIELDMODIFICATION = "FIELDMODIFICATION";

	private SecurityHelper securityHelper;

	private CatalogService catalogService;

	private BeanFactory beanFactory;

	private AlarmCodeService alarmCodeService;

	private MiscellaneousItemConfigService miscellaneousItemConfigService;

	private ClaimSubmssionValidator claimSubmssionValidator;

	private ClaimSubmissionProcessor claimSubmissionProcessor;
	
	private UserRepository userRepository;
	
	private ClaimSubmissionUtil claimSubmissionUtil;
	
	 private ClaimService claimService;

	private static final String REPLACED_PART_ITEM_TYPE = "replacedpart";
	
	private final String FPI_WARRANTY_TYPE="FPI";

	public ClaimSubmissionDocument convertXMLToRequestDTO(String inputXML)
			throws XmlException {
		ClaimSubmissionDocument claimSubmissionDocDTO = null;
		if (!StringUtils.isBlank(inputXML)) {
			try {
				claimSubmissionDocDTO = ClaimSubmissionDocument.Factory
						.parse(inputXML);

				// Create an XmlOptions instance and set the error listener.
				XmlOptions validateOptions = new XmlOptions();
				ArrayList errorList = new ArrayList();
				validateOptions.setErrorListener(errorList);

				if (!claimSubmissionDocDTO.validate(validateOptions)) {

					XmlError error = (XmlError) errorList.get(0);
					throw new XmlException(error.getMessage()
							+ " : For field : "
							+ error.getCursorLocation().getDomNode()
									.getLocalName());
				}

			} catch (XmlException xe) {
				logger.error("Error in XML parsing or validation", xe);
				throw xe;
			}
		}
		return claimSubmissionDocDTO;
	}

	public Claim getClaim(ClaimSubmission claimSubmissionDTO,
			Organization organization,
			Map<String, String> claimPage1errorCodeMap) {
		Claim claim = null;
		String claimType = claimSubmissionDTO.getClaimType().toString();
		if (MACHINE.equals(claimType)) {
			claim = new MachineClaim();
			claim.setClmTypeName(ClaimType.MACHINE.getType());
		} else if (PARTS.equals(claimType)) {
			claim = new PartsClaim();
			claim.setClmTypeName(ClaimType.PARTS.getType());
		} else if (FIELDMODIFICATION.equals(claimType)) {
			claim = new CampaignClaim();
			claim.setClmTypeName(ClaimType.CAMPAIGN.getType());
		}else if(IntegrationConstants.CLAIM_TYPE_FLEET.equalsIgnoreCase(claimType)){
			claimPage1errorCodeMap
			.put(DealerInterfaceErrorConstants.CSA0153,
					dealerInterfaceErrorConstants
							.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0153));
		}
		if (claim != null) {
			claim.setClaimXMLConverter(getClaimXMLConverter());
			claim.setSecurityHelper(getSecurityHelper());
			claim.setEventService(getEventService());
			claim.setOrgService(getOrgService());

		}
		setDefaultValues(claim, claimSubmissionDTO,
				organization, claimPage1errorCodeMap);
		return claim;
	}

	private ClaimXMLConverter getClaimXMLConverter() {
		return (ClaimXMLConverter) this.beanFactory
				.getBean("claimXMLConverter");
	}

	private SecurityHelper getSecurityHelper() {
		return (SecurityHelper) this.beanFactory.getBean("securityHelper");
	}

	public EventService getEventService() {
		return (EventService) this.beanFactory.getBean("eventService");
	}

	public OrgService getOrgService() {
		return (OrgService) this.beanFactory.getBean("orgService");
	}

	private ServiceProvider getOwiningDealer(Claim claim,Organization organization,Map<String, String> errorCodeMap) {
		if ((AdminConstants.EXTERNAL.equals(securityHelper.getLoggedInUser()
				.getUserType()) || AdminConstants.DEALER_USER
				.equals(securityHelper.getLoggedInUser().getUserType()))) {

			return getLoggedInUsersDealership(organization,errorCodeMap);
		}
		return null;
	}

	private void setAlarmCodes(ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		AlarmCodes alarmCodesType = claimSubmissionDTO.getAlarmCodes();
		if (alarmCodesType != null) {
			boolean isAlarmCodesVisble = configParamService
					.getBooleanValue(ConfigName.ALARM_CODE_SECTION_VISIBLE
							.getName());
			EachAlarmCode[] eachAlarmCodes = claimSubmissionDTO.getAlarmCodes()
					.getEachAlarmCodeArray();

			if (eachAlarmCodes != null && isAlarmCodesVisble) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA0102,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0102));
			} else {
				setAlarmCodeValuesForClaim(eachAlarmCodes, claim, errorCodeMap);

			}
		}
	}

	private void setAlarmCodeValuesForClaim(EachAlarmCode[] eachAlarmCodes,
			Claim claim, Map<String, String> errorCodes) {
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		int totalCodes = eachAlarmCodes.length;
		ItemGroup itemGroup = null;
		if (isNotNull(claim.getClaimedItems().get(0).getItemReference()
				.getReferredInventoryItem())) {
			itemGroup = claim.getClaimedItems().get(0).getItemReference()
					.getReferredInventoryItem().getOfType().getProduct();
		} else if (isNotNull(claim.getClaimedItems().get(0).getItemReference()
				.getModel())) {
			itemGroup = claim.getClaimedItems().get(0).getItemReference()
					.getModel();
		}
		List<String> alarmCodeList = new ArrayList<String>();
		for (int index = 0; index < totalCodes; index++) {
			String code = eachAlarmCodes[index].getCode();
			if (StringUtils.isNotBlank(code)) {
				if (alarmCodeList.contains(code)) {
					errorCodeMap.put(DealerInterfaceErrorConstants.CSA0104,
							dealerInterfaceErrorConstants.getPropertyMessage(
									DealerInterfaceErrorConstants.CSA0104,
									new String[] { code }));
					break;
				} else {
					alarmCodeList.add(code.toUpperCase());
				}
			}
		}
		if (!alarmCodeList.isEmpty() && errorCodeMap.isEmpty()) {
			List<AlarmCode> alarmCodes = alarmCodeService
					.findAlarmCodesOfProductByCodes(alarmCodeList, itemGroup);
			for (AlarmCode alarmCode : alarmCodes) {
				String code = alarmCode.getCode().toUpperCase();
				if (alarmCodeList.contains(code)) {
					claim.getAlarmCodes().add(alarmCode);
					alarmCodeList.remove(code);
				}
			}
			for (String code : alarmCodeList) {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA0103,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA0103,
								new String[] { code }));
			}
		}
		if (!errorCodeMap.isEmpty()) {
			errorCodes.putAll(errorCodeMap);
		}
	}

	private boolean isNotNull(Object object) {
		return object == null ? false : true;
	}
	private void setNMHGPartsReplaced(NMHGReplacedParts[] partInfo,
			HussmanPartsReplacedInstalled hussmanPartReplacedInstalled,
			Claim claim, Map<String, String> errorCodeMap) {
		List<OEMPartReplaced> partReplacedList = hussmanPartReplacedInstalled
				.getReplacedParts();
		if (partInfo != null) {
			for (NMHGReplacedParts replacedPart : partInfo) {
				OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
				oemPartReplaced.setPartScrapped(false);
				oemPartReplaced.setPartReturnInitiatedBySupplier(false);
				oemPartReplaced.setShippedByOem(false);
				oemPartReplaced.setInventoryLevel(false);
				if (replacedPart.getComponentSerialNumber() != null
						&& StringUtils.isNotBlank(replacedPart
								.getComponentSerialNumber())) {
					oemPartReplaced.setSerialNumber(replacedPart
							.getComponentSerialNumber());
				}
				if (replacedPart.getComponentDateCode() != null
						&& !StringUtils.isBlank(replacedPart
								.getComponentDateCode())) {
					oemPartReplaced.setDateCode(replacedPart
							.getComponentDateCode());
				}
				validateandSetQuantity(replacedPart.getPartQuantity(),
						oemPartReplaced, errorCodeMap, null, false);
				if ((replacedPart.getPartNumber() == null || StringUtils
						.isBlank(replacedPart.getPartNumber()))
						&& (replacedPart.getComponentDateCode() != null || replacedPart
								.getComponentSerialNumber() != null)) {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA0123,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0123));
				}
				if (replacedPart.getPartNumber() != null
						&& !StringUtils.isBlank(replacedPart.getPartNumber())) {
					setReplacedPart(claim, replacedPart, oemPartReplaced,
							errorCodeMap);
				}
				partReplacedList.add(oemPartReplaced);
			}
		}
	}

	private void setReplacedPart(Claim claim, NMHGReplacedParts replacedPart,
			OEMPartReplaced oemPartReplaced, Map<String, String> errorCodeMap) {
		BrandItem brandItem = getBrandItem(replacedPart.getPartNumber(), claim);
		if (brandItem != null) {
			oemPartReplaced.getItemReference().setReferredItem(
					brandItem.getItem());
			oemPartReplaced.setBrandItem(brandItem);
		} else {
			Item item = claimSubmssionValidator.getOEMPartItemNumbers(claim,
					replacedPart.getPartNumber(), REPLACED_PART_ITEM_TYPE);
			if (item != null) {
				oemPartReplaced.getItemReference().setReferredItem(item);
			} else {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA081,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA081,
								new String[] { replacedPart.getPartNumber() }));
			}
		}
	}

	private BrandItem getBrandItem(String partNumber, Claim claim) {
		BrandItem brandItem = null;
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo()
				.getName())) {
			brandItem = claimSubmssionValidator
					.getOEMPartBrandItemNumbersForAmer(claim, partNumber,
							REPLACED_PART_ITEM_TYPE);
		} else {
			brandItem = claimSubmssionValidator.getOEMPartBrandItemNumbers(
					claim, partNumber, REPLACED_PART_ITEM_TYPE,
					claim.getBrand());
		}
		return brandItem;
	}

	private void validateandSetQuantity(int partQty,
			OEMPartReplaced oEMPartReplaced, Map<String, String> errorCodeMap,
			InstalledParts installedPart, boolean isInstalled) {
		if (partQty == 0 || partQty < 1) {
			errorCodeMap
					.put(
							DealerInterfaceErrorConstants.CSA083,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA083));
		} else {
			if (installedPart != null) {
				installedPart.setNumberOfUnits(partQty);
			} else if (oEMPartReplaced != null) {
				oEMPartReplaced.setNumberOfUnits(partQty);
			}
		}
	}

	private Map<String, String> setMiscPartsReplaced(
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Claim claim, Map<String, String> errorCodeMap) {
		List<NonOEMPartReplaced> nonOEMparts = serviceDetail
				.getMiscPartsReplaced();
		MiscellanousParts miscPartType = claimSubmissionDTO
				.getMiscellanousParts();
		if (miscPartType != null) {
			EachMiscellanousPart[] miscParts = miscPartType
					.getEachMiscellanousPartArray();
			if (miscParts != null
					&& miscParts.length > 0
					&& !configParamService
							.getBooleanValue(ConfigName.MISCELLANEOUS_PARTS_SECION_VISIBLE
									.getName())) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA0130,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0130));
			} else if (miscParts != null) {
				for (EachMiscellanousPart miscPart:miscParts ) {
					String miscPartItemNumber = miscPart
							.getPartItemNumber();
					if (StringUtils.isNotBlank(miscPartItemNumber)) {
						NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
						MiscellaneousItem miscItem = miscellaneousItemConfigService
								.findMiscellaneousItemByPartNumber(miscPartItemNumber);
						if (miscItem == null) {
							errorCodeMap
									.put(DealerInterfaceErrorConstants.CSA091,
											dealerInterfaceErrorConstants
													.getPropertyMessage(
															DealerInterfaceErrorConstants.CSA091,
															new String[] { miscPartItemNumber }));
						} else {
							MiscellaneousItemConfiguration miscellaneousItemConfig = this.miscellaneousItemConfigService
									.findMiscellanousPartConfigurationForDealerAndMiscPart(claim.getForDealer().getId(), miscItem
													.getPartNumber());

							if (miscellaneousItemConfig != null) {
								nonOEMPartReplaced
										.setMiscItemConfig(miscellaneousItemConfig);
								nonOEMPartReplaced
										.setMiscItem(miscellaneousItemConfig
												.getMiscellaneousItem());
								validateAndSetPartQuantiyMiscItems(nonOEMPartReplaced,miscPart
										.getPartQuantity(),errorCodeMap);
								nonOEMparts.add(nonOEMPartReplaced);
							}

						}
					}
				}
			}
		}
		return errorCodeMap;
	}

	private void validateAndSetPartQuantiyMiscItems(
			NonOEMPartReplaced nonOEMPartReplaced, int partQuantity,
			Map<String, String> errorCodeMap) {
		if (partQuantity == 0 || partQuantity < 1) {
			errorCodeMap
					.put(
							DealerInterfaceErrorConstants.CSA0151,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0151));
		} else {
			nonOEMPartReplaced.setNumberOfUnits(partQuantity);
		}
	}

	private void setNonOEMPartsInstalled(ReplacedNonNMHGParts nonNmhhgparts,
			Currency currency, ServiceDetail serviceDetail,Map<String,String> errorCodeMap) {
		List<NonOEMPartReplaced> nonOEMpartsReplaced = serviceDetail
				.getNonOEMPartsReplaced();
		if (nonNmhhgparts!=null&&nonNmhhgparts.getEachPartArray() != null) {
			for (EachPart installParts : nonNmhhgparts.getEachPartArray()) {
				String description = installParts.getNonNMHGPartDescription();
				if (StringUtils.isNotBlank(description)) {
					NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
					nonOEMPartReplaced.setDescription(installParts
							.getNonNMHGPartDescription());
					nonOEMPartReplaced.setPricePerUnit(Money.valueOf(
							installParts.getNonNMHGPartPrice(), currency));
					validateAndSetPartQuantiyNonOemParts(nonOEMPartReplaced,installParts
							.getNonNMHGPartQuantity(),errorCodeMap);
					nonOEMpartsReplaced.add(nonOEMPartReplaced);
				}else{
					errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA0132,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0132));
				}
			}
		}
	}

	private void validateAndSetPartQuantiyNonOemParts(
			NonOEMPartReplaced nonOEMPartReplaced, int nonNMHGPartQuantity,
			Map<String, String> errorCodeMap) {
		if (nonNMHGPartQuantity == 0 || nonNMHGPartQuantity < 1) {
			errorCodeMap
					.put(
							DealerInterfaceErrorConstants.CSA0151,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0151));
		} else {
			nonOEMPartReplaced.setNumberOfUnits(nonNMHGPartQuantity);
		}
	}

	private void setNMHGPartsInstalled(NMHGInstalledParts[] partInfo,
			HussmanPartsReplacedInstalled hussmanPartReplacedInstalled,
			Claim claim, Map<String, String> errorCodeMap) {
		List<InstalledParts> hussmanInstalledParts = new ArrayList<InstalledParts>();
		if (partInfo != null) {
			for (NMHGInstalledParts installedPartObj : partInfo) {
				InstalledParts installedPart = new InstalledParts();
				if (claim.getPartItemReference() != null
						&& claim.getPartItemReference().isSerialized()
						&& claim.getPartItemReference()
								.getReferredInventoryItem() != null) {
					installedPart.setSerialNumber(claim.getPartItemReference()
							.getReferredInventoryItem().getSerialNumber());
				}
				installedPart.setShippedByOem(false);
				installedPart.setInventoryLevel(false);
				installedPart.setIsHussmanPart(true);
				if (installedPartObj.getComponentSerialNumber() != null
						&& StringUtils.isNotBlank(installedPartObj
								.getComponentSerialNumber())) {
					installedPart.setSerialNumber(installedPartObj
							.getComponentSerialNumber());
				}
				if (installedPartObj.getComponentDateCode() != null
						&& !StringUtils.isBlank(installedPartObj
								.getComponentDateCode())) {
					installedPart.setDateCode(installedPartObj
							.getComponentDateCode());
				}
				validateandSetQuantity(installedPartObj.getPartQuantity(),
						null, errorCodeMap, installedPart, true);
				if ((installedPartObj.getPartNumber() == null || StringUtils
						.isBlank(installedPartObj.getPartNumber()))
						&& (installedPartObj.getComponentDateCode() != null || installedPartObj
								.getComponentSerialNumber() != null)) {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA0124,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0124));
				}
				if (installedPartObj.getPartNumber() != null
						&& !StringUtils.isBlank(installedPartObj
								.getPartNumber())) {
					setInstalledPart(claim, installedPartObj, installedPart,
							errorCodeMap);
				}
				hussmanInstalledParts.add(installedPart);
			}
			hussmanPartReplacedInstalled
					.setHussmanInstalledParts(hussmanInstalledParts);
		}
	}

	private void setInstalledPart(Claim claim,
			NMHGInstalledParts installedPartObj, InstalledParts installedPart,
			Map<String, String> errorCodeMap) {
		BrandItem brandItem = getBrandItem(installedPartObj.getPartNumber(),
				claim);
		if (brandItem != null) {
			installedPart.setItem(brandItem.getItem());
			installedPart.setBrandItem(brandItem);
		} else {
			Item item = claimSubmssionValidator.getOEMPartItemNumbers(claim,
					installedPartObj.getPartNumber(), REPLACED_PART_ITEM_TYPE);
			if (item != null) {
				installedPart.setItem(item);
			} else {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA084,
						dealerInterfaceErrorConstants
								.getPropertyMessage(
										DealerInterfaceErrorConstants.CSA084,
										new String[] { installedPartObj
												.getPartNumber() }));
			}
		}

	}

	public void isClaimedItemSerilized(ClaimSubmission claimSubmissionDTO,
			Claim claim, Organization organization, Map<String, String> resultMap, String buName) {
		String inventorySerialNumber = claimSubmissionDTO
				.getInventorySerialNumber();
		String inventoryItemNumber = claimSubmissionDTO
				.getInventoryItemNumber();
		String inventoryModelNumber = claimSubmissionDTO
				.getInventoryModelNumber();
		ClaimedItem claimedItem = new ClaimedItem();
		claim.getClaimedItems().add(claimedItem);
		if (InstanceOfUtil.isInstanceOfClass(MachineClaim.class, claim)) {
			checkIsMachineClaimIsSerialized(claimSubmissionDTO,
					inventorySerialNumber, inventoryModelNumber,
					inventoryItemNumber, resultMap, claimedItem, organization,claim,buName);
		} else if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
			checkIsPartClaimSerilized(claimSubmissionDTO, resultMap, claim,
					inventorySerialNumber, inventoryItemNumber,
					inventoryModelNumber, organization,buName);
		} else {
			checkIsCmapaignClaimIsSerilized(claimSubmissionDTO,
					inventorySerialNumber, claim, claimedItem, resultMap,
					organization,buName);
		}
		claimedItem.setClaim(claim);
	}

	private boolean checkIsCmapaignClaimIsSerilized(
			ClaimSubmission claimSubmissionDTO, String inventorySerialNumber,
			Claim claim, ClaimedItem claimedItem,
			Map<String, String> resultMap, Organization organization, String buName) {
		String campaignCode = claimSubmissionDTO.getCampaignCode();
		InventoryItem inventoryItem = null;
		Campaign campaign = null;
		CampaignNotification campaignNotificationObj = null;
		if (StringUtils.isNotBlank(campaignCode)) {
			campaign = campaignService.findByCode(campaignCode);
			if (campaign == null) {
				resultMap
				.put(DealerInterfaceErrorConstants.CSA018,
						dealerInterfaceErrorConstants
								.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA018));
			}
			if (StringUtils.isNotBlank(inventorySerialNumber)) {
				inventoryItem = getInventoryItem(claim,claimSubmissionDTO,
						inventorySerialNumber, resultMap, organization,buName);
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA023,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA023));
			}
			if (inventoryItem != null && campaign != null
					&& claim.getForDealer() != null
					&& claim.getForDealer().getServiceProviderNumber() != null) {
				campaignNotificationObj = campaignService
						.findCampaignNotification(inventorySerialNumber, claim
								.getForDealer().getServiceProviderNumber(),
								campaignCode);
			}

			if (campaignNotificationObj == null && inventoryItem != null
					&& campaign != null) {
				resultMap.put(DealerInterfaceErrorConstants.CSA021,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA021,
								new String[] { campaignCode,
										inventorySerialNumber }));
			} else if (campaignNotificationObj != null
					&& campaignNotificationObj.getClaim() != null) {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA022,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA022));
			} else if(campaign!=null&&inventoryItem!=null){
				claim.setCampaign(campaign);
				claimedItem.getItemReference().setReferredInventoryItem(
						inventoryItem);
				claimedItem.getItemReference().setSerialized(true);
				resultMap.putAll(validateMarketingGroupCodes(claimSubmissionDTO.getBUName().toString(),
						claim, inventoryItem));
				return true;
			}
		} else {
			resultMap
					.put(DealerInterfaceErrorConstants.CSA019,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA019));
		}
		return false;
	}

	private boolean checkIsPartClaimSerilized(
			ClaimSubmission claimSubmissionDTO, Map<String, String> resultMap,
			Claim claim, String inventorySerialNumber,
			String inventoryItemNumber, String inventoryModelNumber,
			Organization organization, String buName) {
		boolean isPartInstalled = false;
		if (inventorySerialNumber != null
				&& StringUtils.isNotBlank(inventorySerialNumber)) {
			InventoryItem inventoryItem = getInventoryItem(claim,claimSubmissionDTO,
					inventorySerialNumber, resultMap, organization,buName);
			if (inventoryItem != null) {
				claim.getItemReference()
						.setReferredInventoryItem(inventoryItem);
				claim.getItemReference().setSerialized(true);
				((PartsClaim) claim).setPartInstalled(true);
				validateMktgGrpCodesForNonSerializedClaims(buName,claim);
				return true;
			}
		} else {
			isPartInstalled = isPartInstalledOnNonSerializedMachine(claim,
					inventoryModelNumber, inventoryItemNumber,
					claimSubmissionDTO, resultMap);
			if (isPartInstalled) {
				setPartReferenceDetails(claim, resultMap, claimSubmissionDTO
						.getBUName().toString());
			} else if ((inventorySerialNumber == null || StringUtils
					.isBlank(inventorySerialNumber))
					&& (inventoryItemNumber == null || StringUtils
							.isBlank(inventoryItemNumber))
					&& (inventoryModelNumber == null || StringUtils
							.isBlank(inventoryModelNumber))
					&& (claimSubmissionDTO.getCompetitorModel() == null && StringUtils
							.isBlank(claimSubmissionDTO.getCompetitorModel()))) {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA048,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA048));
			}
			return false;
		}
		return false;
	}

	private void setPartReferenceDetails(Claim claim,
			Map<String, String> resultMap, String buName) {
		Map<String, String> errorCodeMap = null;
		resultMap.putAll(validateMktgGrpCodesForNonSerializedClaims(buName,
				claim));
		claim.getItemReference().setSerialized(false);
		((PartsClaim) claim).setPartInstalled(true);
	}

	/**
	 * 1. For serialized machine either machine serial number or container
	 * number required 2. For non-serialized machine item number and model
	 * number required
	 * @param claim 
	 * @param buName 
	 */
	private boolean checkIsMachineClaimIsSerialized(
			ClaimSubmission claimSubmissionDTO, String inventorySerialNumber,
			String inventoryModelNumber, String inventoryItemNumber,
			Map<String, String> resultMap, ClaimedItem claimedItem,
			Organization organization, Claim claim, String buName) {
		claimedItem.getItemReference().setSerialized(false);
		if ((inventorySerialNumber == null || StringUtils
				.isBlank(inventorySerialNumber))
				&& (inventoryModelNumber == null || StringUtils
						.isBlank(inventoryModelNumber))
				&& (inventoryItemNumber == null || StringUtils
						.isBlank(inventoryItemNumber))) {
			resultMap
					.put(DealerInterfaceErrorConstants.CSA0128,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0128));
		} else {
		}
		if (inventorySerialNumber != null
				&& StringUtils.isNotBlank(inventorySerialNumber)) {
			InventoryItem inventoryItem = getInventoryItem(claim,claimSubmissionDTO,
					inventorySerialNumber, resultMap, organization,buName);
			if (inventoryItem != null) {
				claimedItem.getItemReference().setReferredInventoryItem(
						inventoryItem);
				claimedItem.getItemReference().setSerialized(true);
				resultMap.putAll(validateMarketingGroupCodes(claimSubmissionDTO.getBUName().toString(),
						claim, inventoryItem));
				return true;
			}
		} else if (StringUtils.isBlank(inventorySerialNumber)) {
			if (nonSerlizedClaimAllowed()) {
				boolean isRequired = configParamService
						.getBooleanValue(ConfigName.IS_ITEM_NUMBER_DISPLAY_REQUIRED
								.getName());
				if (isRequired) {
					Item item = getItemByItemNumber(inventoryItemNumber);
					if (item == null) {
						resultMap
								.put(DealerInterfaceErrorConstants.CSA013,
										dealerInterfaceErrorConstants
												.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA013));
					} else {
						setItemReferenceDetails(claimedItem, claimSubmissionDTO
								.getBUName().toString(), item, claim,resultMap);
					}
				} else if (StringUtils.isNotBlank(inventoryModelNumber)) {
					ItemGroup itemGroup = getItemByModelName(inventoryModelNumber);
					if (itemGroup == null) {
						resultMap
								.put(DealerInterfaceErrorConstants.CSA009,
										dealerInterfaceErrorConstants
												.getPropertyMessage(
														DealerInterfaceErrorConstants.CSA009,
														new String[] { inventoryModelNumber }));
					} else {
						setItemGroupDetails(claimedItem,claimSubmissionDTO
								.getBUName().toString(),itemGroup,claim,resultMap);
					}
				} else {
					resultMap
							.put(DealerInterfaceErrorConstants.CSA015,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA015));
				}
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA0129,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0129));
			}
		}
		return false;

	}

	private void setItemReferenceDetails(ClaimedItem claimedItem,
			String buName, Item item, Claim claim,Map<String,String> resultMap) {
		Map<String, String> errorCodeMap = null;
			errorCodeMap = validateMktgGrpCodesForNonSerializedClaims(buName,claim);
		if (errorCodeMap == null || errorCodeMap.isEmpty()) {
			claimedItem.getItemReference().setModel(item.getModel());
			claimedItem.getItemReference().setReferredItem(item);
			claimedItem.getItemReference().setSerialized(false);
		}else{
			resultMap.putAll(errorCodeMap);
		}
	}

	private void setItemGroupDetails(ClaimedItem claimedItem, String buName,
			ItemGroup itemGroup, Claim claim,Map<String,String> resultMap) {
		Map<String, String> errorCodeMap = null;
			errorCodeMap = validateMktgGrpCodesForNonSerializedClaims(buName,claim);
		if (errorCodeMap == null || errorCodeMap.isEmpty()) {
			claimedItem.getItemReference().setModel(itemGroup);
			claimedItem.getItemReference().setSerialized(false);
		}else{
			resultMap.putAll(errorCodeMap);
		}
	}

	public Map<String, String> validateMarketingGroupCodes(String buName,Claim claim,
			InventoryItem inventoryItem) {
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		if (IntegrationLayerUtil.isAMERBusinessUnit(buName)
						&& configParamService.getBooleanValue(
								ConfigName.VALIDATE_MARKETING_GROUP_CODES.getName()) ) {
		MarketingGroupsLookup mkgtGrouplookup = new MarketingGroupsLookup();
		mkgtGrouplookup.setClaimType(claim.getType().getType());
		mkgtGrouplookup.setTruckMktgGroupCode(inventoryItem
				.getMarketingGroupCode());
		if (!claim.getNcrClaimCheck()) {
			claimSubmissionUtil.setPolicyForClaim(claim);
		}
		if (claim.isOfType(ClaimType.MACHINE)
				|| claim.isOfType(ClaimType.PARTS))
			mkgtGrouplookup
					.setWarrantyType(claim.getApplicablePolicy() != null ? claim
							.getApplicablePolicy().getWarrantyType().getType()
							: WarrantyType.POLICY.getType());

		if (claim.isOfType(ClaimType.CAMPAIGN)) {
			mkgtGrouplookup.setWarrantyType(FPI_WARRANTY_TYPE);
		}

		if (inventoryItem.getType().equals(InventoryType.STOCK)) {
			mkgtGrouplookup.setWarrantyType(WarrantyType.STANDARD.getType());
		}

		mkgtGrouplookup.setDealerMktgGroupCode(new HibernateCast<Dealership>()
				.cast(claim.getForDealer()).getMarketingGroup());

		List<MarketingGroupsLookup> lookUpResults = claimService
				.lookUpMktgGroupCodes(mkgtGrouplookup, false);
		if (null == lookUpResults || lookUpResults.isEmpty()) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA0143,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0143));
		}
		if (null != lookUpResults && !lookUpResults.isEmpty()) {
			boolean isDealerMarketingGroupExist = false;
			StringBuilder resultedMktGroups = new StringBuilder();
			for (MarketingGroupsLookup lookUpResult : lookUpResults) {
				if (mkgtGrouplookup.getDealerMktgGroupCode().equals(
						lookUpResult.getDealerMktgGroupCode())) {
					isDealerMarketingGroupExist = true;
				}
				resultedMktGroups.append(lookUpResult.getDealerMktgGroupCode());
				resultedMktGroups.append(",");
			}
			if (!isDealerMarketingGroupExist)
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA0144,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA0144,
								new String[] { resultedMktGroups.toString() }));
		}
		}
		return errorCodeMap;
	}

	public Map<String, String> validateMktgGrpCodesForNonSerializedClaims(String buName,
			Claim claim) {
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		if (IntegrationLayerUtil.isAMERBusinessUnit(buName)	&& configParamService.getBooleanValue(
				ConfigName.VALIDATE_MARKETING_GROUP_CODES.getName())) {
		String delaerMktgGrpCode = new HibernateCast<Dealership>().cast(
				claim.getForDealer()).getMarketingGroup();
		boolean allowedMktgGrpCode = false;
		String allowedDealerMktgGroup = configParamService
				.getStringValue(ConfigName.ALLOWED_MKTG_GRP_CODES_NON_SERIALIZED_CLAIMS
						.getName());
		String mktgGrps[] = allowedDealerMktgGroup.split(",");
		for (String mktgGrpCode : mktgGrps) {
			if (delaerMktgGrpCode.equals(mktgGrpCode)) {
				allowedMktgGrpCode = true;
				break;
			}

		}
		if (!allowedMktgGrpCode) {
			errorCodeMap.put(DealerInterfaceErrorConstants.CSA0144,
					dealerInterfaceErrorConstants.getPropertyMessage(
							DealerInterfaceErrorConstants.CSA0144,
							new String[] { allowedDealerMktgGroup }));
		}
		}
		return errorCodeMap;
	}
	private boolean nonSerlizedClaimAllowed() {
		boolean isRequired = configParamService
				.getBooleanValue(ConfigName.NON_SERIALIZED_CLAIM_ALLOWED
						.getName());
		return isRequired;
	}

	private boolean nonSerlizedClaimAllowed(String number,
			Map<String, String> resultMap) {
		boolean isRequired = configParamService
				.getBooleanValue(ConfigName.NON_SERIALIZED_CLAIM_ALLOWED
						.getName());
		if (number != null && !isRequired)
			resultMap
					.put(DealerInterfaceErrorConstants.CSA0129,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0129));

		return isRequired;
	}

	private InventoryItem getInventoryItem(Claim claim,
			ClaimSubmission claimSubmissionDTO, String inventorySerialNumber,
			Map<String, String> resultMap, Organization organization,
			String buName) {
		InventoryItem inventoryItem = null;
		inventoryItem = getInventoryItem(inventorySerialNumber,organization, resultMap,
				buName);
		if (StringUtils.isNotBlank(inventorySerialNumber)
				&& inventoryItem == null) {
			resultMap
					.put(
							DealerInterfaceErrorConstants.CSA008,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA008));
			return null;
		} else if (inventoryItem != null) {
			boolean isInventoryBelongsToLoggedInUser = checkLoggedInDealerOwner(
					inventoryItem, organization,resultMap, claimSubmissionDTO
							.getBUName().toString());
			if (isInventoryBelongsToLoggedInUser) {
				return inventoryItem;
			} else {
				resultMap.put(DealerInterfaceErrorConstants.CSA016,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA016,
								new String[] { inventorySerialNumber }));
			}
		}
		return null;
	}

	public boolean checkLoggedInDealerOwner(InventoryItem item,
			Organization authUserOrganization,Map<String, String> resultMap,String buName) {
		ServiceProvider loggedInUserDealership = getLoggedInUsersDealership(authUserOrganization,resultMap);
		boolean canSearchOtherDealersRetail  = configParamService
        .getBooleanValue(ConfigName.CAN_DEALER_SEARCH_OTHER_DEALERS_RETAIL.getName());
		if(loggedInUserDealership==null){
			 loggedInUserDealership = new HibernateCast<ServiceProvider>()
					.cast(authUserOrganization);
		}
		Organization itemCurrentOwner = item.getCurrentOwner();
		Organization shipToDealer=item.getShipTo();
		if (itemCurrentOwner == null && item.getSerializedPart()) {
			itemCurrentOwner = (Organization) inventoryService
					.findInventoryItemForMajorComponent(item.getId())
					.getOwnedBy();
		}
		boolean canDealerCanFileClaimOnStock = Boolean.FALSE;
		if (item.getType().equals(InventoryType.STOCK)) {
			canDealerCanFileClaimOnStock = configParamService
					.getBooleanValue(ConfigName.STOCK_CLAIM_ALLOWED.getName());
			if (!canDealerCanFileClaimOnStock) {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA017,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA017));
				return true;
			}
		}
		if (loggedInUserDealership != null
				&& item.getType().equals(InventoryType.RETAIL)
				&& (loggedInUserDealership.getId().equals(
						itemCurrentOwner.getId()) || (shipToDealer!=null&&loggedInUserDealership.getId().equals(
								shipToDealer.getId()))||(loggedInUserDealership
						.getChildDealersIds() != null && loggedInUserDealership
						.getChildDealersIds()
						.contains(itemCurrentOwner.getId())))) {
			return true;
		}
      else if (item.getType().equals(InventoryType.RETAIL)
				&& canSearchOtherDealersRetail
				&& IntegrationLayerUtil.isAMERBusinessUnit(buName)) {
			String loggedInUserBrand = new HibernateCast<Dealership>().cast(
					loggedInUserDealership).getBrand();
			if (isLoggedInUserDualBrandDealer(loggedInUserDealership)
					|| item.getBrandType().equals(loggedInUserBrand)) {
				return true;
			}
		}
		if (loggedInUserDealership != null
				&& canDealerCanFileClaimOnStock == true
				&& (loggedInUserDealership.getId().equals(
						itemCurrentOwner.getId()) || (shipToDealer!=null&&loggedInUserDealership.getId().equals(
								shipToDealer.getId()))||(loggedInUserDealership
						.getChildDealersIds() != null
						&& !loggedInUserDealership.getChildDealersIds()
								.isEmpty() && loggedInUserDealership
						.getChildDealersIds()
						.contains(itemCurrentOwner.getId())))) {
			return true;
		}

		// if logged in dealer is parent of some dealer he is allowed
		// NMHGSLMS-387
		List<Long> dealerIds = orgService
				.getChildOrganizationsIds(authUserOrganization.getId());
		if (dealerIds.contains(item.getCurrentOwner().getId())) {
			return true;
		}
		return false;

	}

	public boolean isLoggedInUserDualBrandDealer(
			ServiceProvider loggedInUserDealership) {
		if (loggedInUserDealership != null
				&& null != new HibernateCast<Dealership>().cast(
						loggedInUserDealership).getDualDealer())
			return true;
		else
			return false;
	}

	private InventoryItem getInventoryItem(String inventorySerialNumber,Organization organization,
			Map<String, String> resultMap,String buName) {
		InventoryItem inventoryItem = getInventoryItemBySNOrContainerNumber(
				inventorySerialNumber,organization, true,resultMap,buName);
		return inventoryItem;
	}

	private ItemGroup getItemByModelName(String inventoryModelName) {
		ItemGroup itemGroup = null;
		try {
			if (StringUtils.isNotBlank(inventoryModelName)) {
				itemGroup = catalogService
						.findModelByModelName(inventoryModelName);
			}
		} catch (CatalogException ce) {
			logger.error("Error in getItemByModelName", ce);
		}
		return itemGroup;
	}

	private Item getItemByItemNumber(String itemNumber) {
		Item item = null;
		try {
			if (StringUtils.isNotBlank(itemNumber)) {
				item = catalogService
						.findItemByItemNumberOwnedByManuf(itemNumber);
			}
		} catch (CatalogException ce) {
			logger.error("Error in getItemByItemNumber", ce);
		}
		return item;
	}

	private InventoryItem getInventoryItemBySNOrContainerNumber(
			String numberToSearch,Organization organization, boolean isSerialNumber,
			Map<String, String> resultMap,String buName) {
		InventoryItem inventoryItem = null;
		try {
			if (StringUtils.isNotBlank(numberToSearch)) {
				List<InventoryItem> items = new ArrayList<InventoryItem>();
				if (isSerialNumber) {
					items = inventoryService
							.findItemBySerialNumber(numberToSearch);
				}

				if (!(securityHelper.getLoggedInUser().getUserType()
						.equals(AdminConstants.SUPPLIER_USER))
						&& orgService
								.isDealer(securityHelper.getLoggedInUser())) {
					Map<Object, Object> keyValueOfCustomerTypes = this.configParamService
							.getKeyValuePairOfObjects(ConfigName.WARRANTY_CONFIG_CUSTOMER_TYPES_ALLOWED_IN_QUICK_SEARCH
									.getName());
					for (Iterator<InventoryItem> iterator = items.iterator(); iterator
							.hasNext();) {
						InventoryItem item = iterator.next();
						if (InventoryType.STOCK.getType().equals(
								item.getType().getType())) {
							Collections.sort(item.getTransactionHistory());
							if (!checkLoggedInDealerOwner(item,organization, resultMap,buName)) {
								if (!inventoryService
										.isInvItemOwnedByAllowedCustomerType(
												item, keyValueOfCustomerTypes)) {
									iterator.remove();
								}
							}
						}
					}
				}
				if (!items.isEmpty()) {
					inventoryItem = items.get(0);
				}
			}

		} catch (ItemNotFoundException infe) {
			logger.error("Error in getItemByItemNumber", infe);
		}
		return inventoryItem;
	}

	public ServiceProvider getLoggedInUsersDealership(Organization dealerOrg,
			Map<String, String> resultMap) {
		ServiceProvider loggedInUserOrganization = null;
		try {
			loggedInUserOrganization = new HibernateCast<ServiceProvider>()
					.cast(dealerOrg);
		} catch (ClassCastException e) {
			logger.error(e);
		}
		return loggedInUserOrganization;
	}
	
	public Organization getLoggedInUsersDealershipOrganization(String dealerNumber,
			Map<String, String> resultMap) {
		Organization loggedInUserOrganization = null;
		try {
			List<Organization> userOrgs = securityHelper.getLoggedInUser()
					.getBelongsToOrganizations();

			if (userOrgs != null && !userOrgs.isEmpty()) {
				for (Organization userOrg : userOrgs) {
					ServiceProvider serviceProvider = new HibernateCast<ServiceProvider>()
							.cast(userOrg);
					if (serviceProvider.getDealerNumber().equalsIgnoreCase(
							dealerNumber)) {
						loggedInUserOrganization = userOrg;
					}
				}
			}
		} catch (ClassCastException e) {
			logger.error(e);
		}
		return loggedInUserOrganization;
	}

	private boolean isPartInstalledOnNonSerializedMachine(Claim claim,
			String itemModelNumber, String itemPartNumber,
			ClaimSubmission claimSubmissionDTO, Map<String, String> resultMap) {
		boolean isPartHosted = false;
		switch (0) {
		case 0:
			if (StringUtils.isNotBlank(itemModelNumber)
					&& nonSerlizedClaimAllowed(itemModelNumber, resultMap)) {
				ItemGroup itemGroup = getItemByModelName(itemModelNumber);
				if (itemGroup == null) {
					resultMap.put(DealerInterfaceErrorConstants.CSA009,
							dealerInterfaceErrorConstants.getPropertyMessage(
									DealerInterfaceErrorConstants.CSA009,
									new String[] { itemModelNumber }));
				} else {
					claim.getItemReference().setModel(itemGroup);
					isPartHosted = true;
				}
				break;
			}
		case 1:
			if (StringUtils.isNotBlank(itemPartNumber)
					&& nonSerlizedClaimAllowed(itemPartNumber, resultMap)) {
				Item item = getItemByItemNumber(itemPartNumber);
				if (item == null) {
					resultMap.put(DealerInterfaceErrorConstants.CSA010,
							dealerInterfaceErrorConstants.getPropertyMessage(
									DealerInterfaceErrorConstants.CSA010,
									new String[] { itemPartNumber }));
				} else {
					claim.getItemReference().setReferredItem(item);
					isPartHosted = true;
				}
				break;
			}

		case 2:
			if (claimSubmissionDTO.getCompetitorModel() != null
					&& StringUtils.isNotBlank(claimSubmissionDTO
							.getCompetitorModel())) {
				claim.setClaimCompetitorModel(null);
				claim.setCompetitorModelDescription(claimSubmissionDTO
						.getCompetitorModel());
				isPartHosted = true;
			}
		}
		return isPartHosted;
	}

	public void populateSourceWarehouse(Claim claim) {
		if (claim.getClaimedItems() != null
				&& !claim.getClaimedItems().isEmpty()) {
			if (claim.getClaimedItems().get(0).getItemReference() != null
					&& claim.getClaimedItems().get(0).getItemReference()
							.getReferredInventoryItem() != null) {
				claim.setSourceWarehouse(claim.getClaimedItems().get(0)
						.getItemReference().getReferredInventoryItem()
						.getSourceWarehouse());
			}
		}
	}

	/*private void validateConfiguredCostCategories(
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Currency currency, Map<String, String> resultMap, Claim claim, FailureStructure failureStructure) {
		List<Object> configuredCostCategories = configParamService
				.getListofObjects(ConfigName.CONFIGURED_COST_CATEGORIES
						.getName());
		List<String> configuredCostCategoriesCode = new ArrayList<String>();

		for (Object object : configuredCostCategories) {
			CostCategory costCategory = new HibernateCast<CostCategory>()
					.cast(object);
			configuredCostCategoriesCode.add(costCategory.getCode());
		}
		setCostCategories(claimSubmissionDTO, resultMap,
				configuredCostCategoriesCode, serviceDetail, currency,  claim,  failureStructure);
	}*/
	
	public ItemGroup getProduct(ItemGroup itemGroup) {
		if (itemGroup == null)
			return null;
		if (itemGroup.getIsPartOf() != null
				&& !ItemGroup.PRODUCT.equals(itemGroup.getIsPartOf()
						.getItemGroupType())) {
			return getProduct(itemGroup.getIsPartOf());
		} else {
			return itemGroup.getIsPartOf();
		}
	}

	private void validateAndSetTechnicianData(Claim claim,
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Map<String, String> errorCodeMap) {
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo()
				.getName())) {
			setAMERTechncianDetails(claim,
					claimSubmissionDTO.getTechnicianId(), serviceDetail,
					errorCodeMap);
		} else {
			User technician = null;
			try {
				technician = getTechnician(claimSubmissionDTO.getTechnicianId());
				if (technician == null
						&& !claim.getBusinessUnitInfo().getName()
								.equals(IntegrationConstants.NMHG_EMEA)) {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA65,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA65));
				} else if (technician != null) {
					serviceDetail.setTechnician(technician);
				}
			} catch (IncorrectResultSizeDataAccessException he) {
				logger.error("Error in setServiceInformation", he);
				if (!claim.getBusinessUnitInfo().equals(
						IntegrationConstants.NMHG_EMEA))
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA0101,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0101));
			}
		}
	}

	private void setAMERTechncianDetails(Claim claim, String technicianId,
			ServiceDetail serviceDetail, Map<String, String> errorCodeMap) {
		if ((technicianId == null || StringUtils.isEmpty(technicianId))
				&& (InstanceOfUtil.isInstanceOfClass(MachineClaim.class, claim)
						|| InstanceOfUtil.isInstanceOfClass(Campaign.class,
								claim) || (configParamService
						.getBooleanValue(ConfigName.TECHNICIAN_CERTIFICATION_FOR_FPI_CLAIMS
								.getName()) && InstanceOfUtil
						.isInstanceOfClass(PartsClaim.class, claim)))) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA0140,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0140));
		} else {
			serviceDetail.setServiceTechnician(technicianId);
		}
	}

	private User getTechnician(final String technicainId) {
		User technician = null;
		if (StringUtils.isNotBlank(technicainId)) {
			ServiceProvider dealer = new HibernateCast<ServiceProvider>()
					.cast(securityHelper.getLoggedInUser()
							.getBelongsToOrganization());
			technician = this.userRepository
					.findTechnicianForDealerByLoginName(dealer.getId(),
							technicainId);
		}
		return technician;
	}
	
	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public void setClaimpage1Data(ClaimSubmission claimSubmissionDTO,
			Claim claim, Organization organization, Map<String, String> errorCodeMap) {
		setDealerJobNumber(claim, claimSubmissionDTO.getDealerJobNumber(),
				claim.getBusinessUnitInfo().getName(), errorCodeMap);
		if (claimSubmissionDTO.getCmsTicketNumber() != null
				&& StringUtils.isNotBlank(claimSubmissionDTO
						.getCmsTicketNumber())) {
			claim.setCmsTicketNumber(claimSubmissionDTO.getCmsTicketNumber());
		}
		if (claimSubmissionDTO.getInvoiceNumber() != null
				&& StringUtils
						.isNotBlank(claimSubmissionDTO.getInvoiceNumber())
				&& !claim.getClaimedItems().get(0).getItemReference()
						.isSerialized()) {
			claim.setInvoiceNumber(claimSubmissionDTO.getInvoiceNumber());
		}
		if (claimSubmissionDTO.getBrand() != null
				&& claim.getClaimedItems().get(0).getItemReference() != null
				&& !claim.getClaimedItems().get(0).getItemReference()
						.isSerialized()
				&& StringUtils.isNotBlank(claimSubmissionDTO.getBrand()
						.toString())) {
			claim.setBrand(claimSubmissionDTO.getBrand().toString());
		} else if (claim.getItemReference().getReferredInventoryItem() != null
				&& claim.getItemReference().getReferredInventoryItem()
						.getBrandType() != null) {
			claim.setBrand(claim.getItemReference().getReferredInventoryItem()
					.getBrandType());
		}
		if (claimSubmissionDTO.getDealerClaimNumber() != null
				&& StringUtils.isNotBlank(claimSubmissionDTO
						.getDealerClaimNumber())) {
			claim.setHistClmNo(claimSubmissionDTO.getDealerClaimNumber());
		}
		if (claimSubmissionDTO.getUnszdSlNo() != null
				&& claim.getCompetitorModelDescription() == null
				&& InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)
				&& !claim.getClaimedItems().get(0).getItemReference()
						.isSerialized()
				&& StringUtils.isNotBlank(claimSubmissionDTO.getUnszdSlNo())) {
			claim.getItemReference().setUnszdSlNo(
					claimSubmissionDTO.getUnszdSlNo());
		}
		if (claimSubmissionDTO.getComponentSerialNumber() != null
				&& InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)
				&& StringUtils.isNotBlank(claimSubmissionDTO
						.getComponentSerialNumber())) {
			claim.setPartSerialNumber(claimSubmissionDTO
					.getComponentSerialNumber());
		}
		setEmissionFlag(claim,claimSubmissionDTO.getIsEmission());
	}

	private void setEmissionFlag(Claim claim, boolean isEmission) {
		if (configParamService.getBooleanValue(ConfigName.ENABLE_EMISSION
				.getName())) {
			claim.setEmission(isEmission);
		}
	}

	private void setDefaultValues(Claim claim, ClaimSubmission claimSubmissionDTO,
			Organization organization, Map<String, String> errorCodeMap) {
		claim.setSource(AdminConstants.WEBSERVICE);
		if (IntegrationLayerUtil.isAMERBusinessUnit(claimSubmissionDTO.getBUName().toString())) {
			ServiceProvider loggedInUserOrganization = new HibernateCast<ServiceProvider>()
					.cast(organization);
			claim.setForDealer(loggedInUserOrganization);
		} else {
			claim.setForDealer(getOwiningDealer(claim,organization, errorCodeMap));
		}
		claim.setFiledBy(securityHelper.getLoggedInUser());
	   // securityHelper.getDefaultBusinessUnit().setName(buName);
		claim.setState(ClaimState.DRAFT);
	}

	private void setDealerJobNumber(Claim claim, String dealerJobNumber,
			String businessUnit, Map<String, String> errorCodeMap) {
		if (dealerJobNumber != null && StringUtils.isNotBlank(dealerJobNumber)) {
			claim.setWorkOrderNumber(dealerJobNumber);
		} else if (!businessUnit.equalsIgnoreCase(IntegrationConstants.BUSINESS_UNIT_EMEA)) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA049,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA049));
		}
	}

	public void validateAndSetClaimPage2OptionalData(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> claimPage2errorCodeMap) {
		
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();
		FailureStructure failureStructure = failureStructureService
				.getFailureStructure(claim, null);
		if(failureStructure==null||failureStructure.getAssemblies().isEmpty()){
			failureStructure=getFailureStructureForProduct(claim);
		}
		setInstalledOrReplacedPartsInfo(claimSubmissionDTO, claim,
				failureStructure, claimPage2errorCodeMap);
		if(claim.getForDealer()!=null){
		Currency currency = claim.getForDealer().getPreferredCurrency();
		setNonOEMPartsInstalled(claimSubmissionDTO.getReplacedNonNMHGParts(),
				currency, serviceDetail,claimPage2errorCodeMap);
		setMiscPartsReplaced(claimSubmissionDTO, serviceDetail, claim,
				claimPage2errorCodeMap);
		batchClaimCostCategoryConfigurationDetails
					.validateConfiguredCostCategories(claimSubmissionDTO,
							serviceDetail, currency, claimPage2errorCodeMap,
							claim, failureStructure);
		}
		travelDetailsCalculator.setTravelDetails(claim, claimSubmissionDTO, serviceDetail,
				claimPage2errorCodeMap);
		validateAndSetTechnicianData(claim, claimSubmissionDTO, serviceDetail, claimPage2errorCodeMap);
		setAlarmCodes(claimSubmissionDTO, claim, claimPage2errorCodeMap);
		populateSourceWarehouse(claim);
		
	}

	private FailureStructure getFailureStructureForProduct(Claim claim) {
		ItemGroup productToConsider = null;
		FailureStructure failureStructure = null;
		if (claim.getItemReference()!=null&&claim.getItemReference().isSerialized()
				&& claim.getItemReference().getReferredInventoryItem() != null) {
			productToConsider = claim.getItemReference()
					.getReferredInventoryItem().getOfType().getProduct();
		} else {
			productToConsider = getProduct(claim.getItemReference().getModel());
		}
		failureStructure = failureStructureService
				.getFailureStructureForItemGroup(productToConsider);
		return failureStructure;
	}

	private void setInstalledOrReplacedPartsInfo(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			FailureStructure failureStructure,
			Map<String, String> claimPage2errorCodeMap) {
		ComponentsReplaced[] nmhgPartsType = claimSubmissionDTO
				.getComponentsReplacedArray();
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();
		if (nmhgPartsType != null) {
			if (!claim.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled().isEmpty()) {
				removeExistingInstalledOrRemovedParts(claim, nmhgPartsType);
			}
			for (int i = 0; i < nmhgPartsType.length; i++) {
				HussmanPartsReplacedInstalled hussmanPartReplacedInstalled = new HussmanPartsReplacedInstalled();
				NMHGReplacedParts[] nmhgReplacedParts = nmhgPartsType[i]
						.getNMHGReplacedPartsArray();
				NMHGInstalledParts[] nmhgInstalledParts = nmhgPartsType[i]
						.getNMHGInstalledPartsArray();
				setNMHGPartsReplaced(nmhgReplacedParts,
						hussmanPartReplacedInstalled, claim,
						claimPage2errorCodeMap);
				setNMHGPartsInstalled(nmhgInstalledParts,
						hussmanPartReplacedInstalled, claim,
						claimPage2errorCodeMap);
				if (!hussmanPartReplacedInstalled.getHussmanInstalledParts()
						.isEmpty()
						|| !hussmanPartReplacedInstalled.getReplacedParts()
								.isEmpty()) {
					Map<String, String[]> errorMap = claimService
							.validateReplacedParts(
									hussmanPartReplacedInstalled, claim);
					if (errorMap.isEmpty()) {
						serviceDetail.getHussmanPartsReplacedInstalled().add(
								hussmanPartReplacedInstalled);
					} else {
						claimSubmissionProcessor.setErrorCodeMapValues(
								claimPage2errorCodeMap, errorMap);
					}
				}
			}
		}
	}

	private void removeExistingInstalledOrRemovedParts(Claim claim,
			ComponentsReplaced[] nmhgPartsType) {
		if ((nmhgPartsType[0].getNMHGInstalledPartsArray() != null && nmhgPartsType[0]
				.getNMHGInstalledPartsArray().length > 0)
				|| (nmhgPartsType[0].getNMHGReplacedPartsArray() != null && nmhgPartsType[0]
						.getNMHGReplacedPartsArray().length > 0))
			claim.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled().clear();
	}

	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.beanFactory = arg0;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setAlarmCodeService(AlarmCodeService alarmCodeService) {
		this.alarmCodeService = alarmCodeService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

	public ClaimSubmssionValidator getClaimSubmssionValidator() {
		return claimSubmssionValidator;
	}

	public void setClaimSubmssionValidator(
			ClaimSubmssionValidator claimSubmssionValidator) {
		this.claimSubmssionValidator = claimSubmssionValidator;
	}

	public ClaimSubmissionProcessor getClaimSubmissionProcessor() {
		return claimSubmissionProcessor;
	}

	public void setClaimSubmissionProcessor(
			ClaimSubmissionProcessor claimSubmissionProcessor) {
		this.claimSubmissionProcessor = claimSubmissionProcessor;
	}

	public TravelDetailsCalculator getTravelDetailsCalculator() {
		return travelDetailsCalculator;
	}

	public void setTravelDetailsCalculator(
			TravelDetailsCalculator travelDetailsCalculator) {
		this.travelDetailsCalculator = travelDetailsCalculator;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public BatchClaimCostCategoryConfigurationDetails getBatchClaimCostCategoryConfigurationDetails() {
		return batchClaimCostCategoryConfigurationDetails;
	}

	public void setBatchClaimCostCategoryConfigurationDetails(
			BatchClaimCostCategoryConfigurationDetails batchClaimCostCategoryConfigurationDetails) {
		this.batchClaimCostCategoryConfigurationDetails = batchClaimCostCategoryConfigurationDetails;
	}

	public ClaimSubmissionUtil getClaimSubmissionUtil() {
		return claimSubmissionUtil;
	}

	public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
		this.claimSubmissionUtil = claimSubmissionUtil;
	}

	public ClaimService getClaimService() {
		return claimService;
	}
	
}
