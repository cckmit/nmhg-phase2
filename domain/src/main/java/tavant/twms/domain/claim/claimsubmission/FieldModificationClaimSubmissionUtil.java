package tavant.twms.domain.claim.claimsubmission;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignLaborDetail;
import tavant.twms.domain.campaign.CampaignSectionPrice;
import tavant.twms.domain.campaign.CampaignServiceDetail;
import tavant.twms.domain.campaign.CampaignTravelDetail;
import tavant.twms.domain.campaign.HussPartsToReplace;
import tavant.twms.domain.campaign.NonOEMPartToReplace;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.orgmodel.ServiceProvider;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class FieldModificationClaimSubmissionUtil {

    private ClaimSubmissionUtil claimSubmissionUtil;

	private FailureStructureService failureStructureService;

	private MiscellaneousItemConfigService miscellaneousItemConfigService;

	public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
		this.claimSubmissionUtil = claimSubmissionUtil;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

	public Map<String, String[]> validate(CampaignClaim claim,
			Campaign campaign, List<ClaimedItem> tempList) {
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		if ((campaign == null || (campaign != null && campaign.getId() == null))
				&& (claim.getId() == null || claim.getCampaign() == null)) {
			errorCodeMap.put("error.newClaim.invalidCampaignCode", null);
		}

		if (claim.getForDealer() == null) {
			errorCodeMap.put("error.newClaim.selectDealer", null);
		}

		if (campaign != null && ("Inactive").equals(campaign.getStatus())) {
			errorCodeMap.put("error.newClaim.inactiveCampaignCode", null);
		}

		if (!claim.getForMultipleItems() && claim.getClaimedItems().size() != 0) {
			if (claim.getClaimedItems().get(0).getItemReference()
					.getReferredInventoryItem() == null) {
				errorCodeMap.put("error.newClaim.itemRequired", null);
			}
		}

		if (claim.getRepairDate() != null) {
			CalendarDate campaignActiveFrom = null;
			CalendarDate campaignActiveTill = null;
			if (campaign != null) {
				campaignActiveFrom = campaign.getFromDate();
				campaignActiveTill = campaign.getTillDate();
			} else if (claim.getCampaign() != null) {
				campaignActiveFrom = claim.getCampaign().getFromDate();
				campaignActiveTill = claim.getCampaign().getTillDate();
			}
			if (claim.getRepairDate().isBefore(campaignActiveFrom)
					|| claim.getRepairDate().isAfter(campaignActiveTill)) {
				errorCodeMap.put("error.newClaim.invalidRepairDateForCampaign",
						null);
			}
			if (!claim.getForMultipleItems()) {
				claimSubmissionUtil.validateRepairDate(claim, errorCodeMap);
			}
		}
		if (claim.getForMultipleItems() && claim.getClaimedItems().size() > 0) {
			if (claim.getRepairDate() != null) {
				claimSubmissionUtil.validateRepairDate(claim, errorCodeMap);
			}
			for (Iterator<ClaimedItem> iterator = claim.getClaimedItems()
					.iterator(); iterator.hasNext();) {
				ClaimedItem claimedItem = (ClaimedItem) iterator.next();
				if (claimedItem.getItemReference().getReferredInventoryItem() == null) {
					iterator.remove();
				}
			}
			if (claim.getClaimedItems().size() == 0) {
				errorCodeMap.put("error.newClaim.itemRequired", null);
				claim.setClaimedItems(tempList);
			} else {
				for (ClaimedItem claimedItem : claim.getClaimedItems()) {
					if (claimedItem.getHoursInService() == null
							|| (claimedItem.getHoursInService() != null && claimedItem
									.getHoursInService().intValue() < 0)) {
						errorCodeMap.put("error.newClaim.serviceHrsInvalid",
								null);
						break;
					}

				}
			}
		}
		return errorCodeMap;
	}

	public void populateMiscPartsFromCampaign(Campaign campaign,
			CampaignClaim claim) {
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();
		List<NonOEMPartReplaced> miscPartsReplaced = fetchMiscPartsReplaced(
				claim, campaign);
		for (NonOEMPartReplaced replaced : miscPartsReplaced) {
			serviceDetail.addMiscPartsReplaced(replaced);
		}
	}

	public void populateReplacedPartsFromCampaign(Campaign campaign,
			CampaignClaim claim, boolean partsReplacedInstalledSectionVisible,
			boolean buPartReplaceableByNonBUPart) {
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();
		if (!partsReplacedInstalledSectionVisible) {
			List<OEMPartReplaced> partsReplaced = fetchOEMPartsReplaced(claim, campaign);
			List<NonOEMPartReplaced> nonOemPartsReplaced = fetchNonOEMPartsReplaced(
					claim, campaign);
			for (OEMPartReplaced replaced : partsReplaced) {
				serviceDetail.addOEMPartReplaced(replaced);
			}

			for (NonOEMPartReplaced replaced : nonOemPartsReplaced) {
				serviceDetail.addNonOEMPartReplaced(replaced);
			}
		} else {
			for (HussPartsToReplace element : campaign.getHussPartsToReplace()) {
				serviceDetail.addHussmanPartsReplacedInstalled(element
						.fetchHussmannPartsReplacedInstalled(claim));
			}
			if (!buPartReplaceableByNonBUPart) {
				List<NonOEMPartReplaced> nonOemPartsReplaced = fetchNonOEMPartsReplaced(
						claim, campaign);
				for (NonOEMPartReplaced replaced : nonOemPartsReplaced) {
					serviceDetail.addNonOEMPartReplaced(replaced);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	public void populateLaborDetailsFromCampaign(Campaign campaign,
			CampaignClaim claim) {
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();
		List<LaborDetail> laborDetails = new ArrayList<LaborDetail>();
		if (claim.getItemReference().getReferredInventoryItem() != null) {
			Item item = claim.getItemReference().getReferredInventoryItem()
					.getOfType();
			CampaignServiceDetail campaignServiceDetail = campaign
					.getCampaignServiceDetail();
			if (campaignServiceDetail != null) {
				List<CampaignLaborDetail> laborLimits = campaignServiceDetail
						.getCampaignLaborLimits();
				for (CampaignLaborDetail detail : laborLimits) {
					LaborDetail laborDetail = new LaborDetail();
					ServiceProcedure serviceProcedure = failureStructureService
							.findServiceProcedureByDefinitionAndItem(
									detail.getServiceProcedureDefinition(),
									claim);
					if (!detail.isLaborStandardsUsed()
							&& detail.getSpecifiedLaborHours() != null) {
						laborDetail.setSpecifiedHoursInCampaign(detail
								.getSpecifiedLaborHours().multiply(
										new BigDecimal(claim.getClaimedItems()
												.size())));

					}
					laborDetail.setServiceProcedure(serviceProcedure);
					laborDetails.add(laborDetail);
				}
			}
		}
		serviceDetail.setLaborPerformed(laborDetails);
	}

	public void populateMiscellaneousDetailsFromCampaign(Campaign campaign,
			CampaignClaim claim) {
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();
		CampaignServiceDetail campaignServiceDetail = campaign
				.getCampaignServiceDetail();
		CampaignTravelDetail campaignTravelDetail = campaignServiceDetail
				.getTravelDetails();
		if (campaignTravelDetail != null) {
			TravelDetail travelDetail = new TravelDetail();
			travelDetail.setDistance(campaignTravelDetail.getDistance());
			travelDetail.setUom(campaignTravelDetail.getUom());
			travelDetail.setLocation(campaignTravelDetail.getLocation());
			travelDetail.setTrips(campaignTravelDetail.getTrips());
			if(campaignTravelDetail.getHours()!=null)
				travelDetail.setHours(campaignTravelDetail.getHours().toString());			
			travelDetail.setAdditionalHours(campaignTravelDetail
					.getAdditionalHours());
			serviceDetail.setTravelDetails(travelDetail);
		}
		serviceDetail.setItemFreightAndDuty(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.FREIGHT_DUTY_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setMealsExpense(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.MEALS_HOURS_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setParkingAndTollExpense(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.PARKING_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setPerDiem(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.PER_DIEM_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setRentalCharges(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.RENTAL_CHARGES_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setLocalPurchaseExpense(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.LOCAL_PURCHASE_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setTollsExpense(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.TOLLS_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setOtherFreightDutyExpense(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setOthersExpense(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.OTHERS_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setHandlingFee(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.HANDLING_FEE_CODE,
						claim.getCurrencyForCalculation()));
		serviceDetail.setTransportationAmt(campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.TRANSPORTATION_COST_CATEGORY_CODE,
						claim.getCurrencyForCalculation()));
	}

	public List<OEMPartReplaced> fetchOEMPartsReplaced(CampaignClaim claim, Campaign campaign) {
		List<OEMPartToReplace> partsToReplace = campaign.getOemPartsToReplace();
		List<OEMPartReplaced> partsReplaced = new ArrayList<OEMPartReplaced>();
		for (OEMPartToReplace replace : partsToReplace) {
			OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
			oemPartReplaced.setNumberOfUnits(replace.getNoOfUnits());
			oemPartReplaced.setItemReference(new ItemReference(replace
					.getItem()));
			oemPartReplaced.setBrandItem(replace.getItem().getBrandItem(claim.getBrand()));
			oemPartReplaced.setShippedByOem(replace.isShippedByOem());
			partsReplaced.add(oemPartReplaced);
		}
		return partsReplaced;
	}

	public List<NonOEMPartReplaced> fetchNonOEMPartsReplaced(
			CampaignClaim claim, Campaign campaign) {
		List<NonOEMPartToReplace> partsToReplace = campaign
				.getNonOEMpartsToReplace();
		List<NonOEMPartReplaced> partsReplaced = new ArrayList<NonOEMPartReplaced>();
		for (NonOEMPartToReplace replace : partsToReplace) {
			NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
			nonOEMPartReplaced.setNumberOfUnits(replace.getNoOfUnits());
			nonOEMPartReplaced.setDescription(replace.getDescription());
			nonOEMPartReplaced.setPricePerUnit(fetchPrice(replace,
					claim.getForDealer()));
			partsReplaced.add(nonOEMPartReplaced);
		}
		return partsReplaced;
	}

	private List<NonOEMPartReplaced> fetchMiscPartsReplaced(CampaignClaim claim, Campaign campaign) {
		List<NonOEMPartToReplace> partsToReplace = campaign.getMiscPartsToReplace();
		List<NonOEMPartReplaced> partsReplaced = new ArrayList<NonOEMPartReplaced>();
		for (NonOEMPartToReplace replace : partsToReplace) {
			MiscellaneousItemConfiguration mic = miscellaneousItemConfigService
					.findMiscellanousPartConfigurationForDealerAndMiscPart(
							claim.getForDealer().getId(), replace.getMiscItem()
									.getPartNumber());
			if(mic != null) {
				NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
				nonOEMPartReplaced.setNumberOfUnits(replace.getNoOfUnits());
				nonOEMPartReplaced.setMiscItem(replace.getMiscItem());
				nonOEMPartReplaced.setMiscItemConfig(mic);
				nonOEMPartReplaced.setPricePerUnit(replace
						.getMiscItemConfig().getMiscItemRateForCurrency(
								claim.getCurrencyForCalculation()).getRate());
				partsReplaced.add(nonOEMPartReplaced);
			}
		}
		return partsReplaced;
	}

	public Money fetchPrice(NonOEMPartToReplace replace,
			ServiceProvider forDealer) {
		Money pricePerUnit = Money.valueOf(new BigDecimal(0.00),
				forDealer.getPreferredCurrency());
		Currency preferredCurrency = forDealer.getPreferredCurrency();
		for (CampaignSectionPrice campaignNonOem : replace
				.getCampaignSectionPrice()) {
			if (preferredCurrency.getCurrencyCode().equalsIgnoreCase(
					campaignNonOem.getPricePerUnit()
							.breachEncapsulationOfCurrency().getCurrencyCode())) {
				pricePerUnit = campaignNonOem.getPricePerUnit();
				break;
			}
		}
		return pricePerUnit;
	}

}
