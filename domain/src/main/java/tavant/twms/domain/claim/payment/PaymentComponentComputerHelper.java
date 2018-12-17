package tavant.twms.domain.claim.payment;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.orgmodel.CertificateService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.SeriesCertification;
import tavant.twms.domain.orgmodel.SeriesRefCertification;
import tavant.twms.domain.orgmodel.Technician;
import tavant.twms.domain.orgmodel.TechnicianCertification;
import tavant.twms.infra.HibernateCast;

import com.domainlanguage.time.CalendarDate;

public class PaymentComponentComputerHelper {
	
    private DealerGroupService dealerGroupService;
    private CertificateService certificateService;
    private OrgService orgService;
    
	private static final String TECHNICIAN_CERTIFIED = "Warning Message: Technician is Certified for the Claim";

	private static final String TECHNICIAN_NOTCERTIFIED = "Warning Message: Technician is Not Certified for the Claim";
	
	
	public boolean isLAMDealer(Claim claim){
		Dealership forDealer = new HibernateCast<Dealership>().cast(claim
				.getForDealer());
		DealerGroup forDealerGroup = dealerGroupService.findDealerGroupsForWatchedDealership(forDealer);
		boolean isLamDealer = false;
		if (forDealerGroup != null) {
			isLamDealer = (forDealerGroup.getName().equalsIgnoreCase(
					AdminConstants.LATIN_AMERICAN_DEALERS) || forDealerGroup
					.getDescription()
					.equalsIgnoreCase(AdminConstants.LATIN_AMERICAN_DEALERS)) ? true : false;
		}
		return isLamDealer;
	}
	
	public boolean isTechnicianCertifed(Claim claim) {
		if(verifyIfUtilevTruck(claim)){ // technician certification not needed for utilev trucks/trucks with no series ref certification
			return false;
		}
		String technicianEntered = claim.getServiceInformation()
				.getServiceDetail().getServiceTechnician();
		Technician technician = getOrgService().findTechnicianByName(
				technicianEntered);
		if (!isTechExists(technician)) {
			return false;
		}
		/*boolean isTechBelongingToFilingDealer = isTechBelongsToDealer(
				technician, claim);*/
		Dealership forDealer = new HibernateCast<Dealership>().cast(claim
				.getForDealer());
		ItemGroup series = getSeriesOfClaimedItem(claim);
		if(series!=null){
			ItemGroup sisterSeries = series.getOppositeSeries();
			boolean isTechHasSisterSeriesCore = false;
			boolean isTechHasCore = isTechHasAtleastOneCoreOfSeriesBrand(
					technician, claim,series);
			if(forDealer.getDualDealer()!=null && !isTechHasCore && sisterSeries!=null){
			isTechHasSisterSeriesCore = isTechHasAtleastOneCoreOfSeriesBrand(technician,
						claim,sisterSeries);
			}
			/*isTechBelongingToFilingDealer && */
			if (isTechHasCore) {
				return checkIfPRLevelExists(technician,series,sisterSeries,claim,forDealer); 
			}
			else if(isTechHasSisterSeriesCore){
				boolean isPRExist = checkIfPRLevelExists(technician,sisterSeries,series,claim,forDealer); //this method returns true, if technician has sister series core and (sister series PR OR PR for the brand of the truck)
				if(!isPRExist){
					if(isSeriesHasAtleastOnePRLevel(series, claim.getRepairDate()) && isTechHasPRLevelOfSeries(series, technician,
							claim.getRepairDate())) {
						return true;
					}
				}
				else{
					return isPRExist;
				}
			}
		}		
		return false;
	}
	
	/**
	 * 
	 * @param technician
	 * @param series
	 * @param oppositeSeries
	 * @param claim
	 * @param forDealer
	 * @return boolean
	 * This method checks if PR level exists for the series and technician. If it does not exist
	 * for Series, it checks for sister series
	 */
	private boolean checkIfPRLevelExists(Technician technician,
			ItemGroup series, ItemGroup oppositeSeries, Claim claim,
			Dealership forDealer) {
		if (!isSeriesHasAtleastOnePRLevel(series, claim.getRepairDate())) {
			if (forDealer.getDualDealer() != null && oppositeSeries != null) {
				if (!isSeriesHasAtleastOnePRLevel(oppositeSeries,
						claim.getRepairDate())) {
					return true;
				}
				if (isTechHasPRLevelOfSeries(oppositeSeries, technician,
						claim.getRepairDate())) {
					return true;
				}
			}
			return true;
		} else if (isTechHasPRLevelOfSeries(series, technician,
				claim.getRepairDate())) {
			return true;
		}
		return false;
	}

	private boolean isTechHasAtleastOneCoreOfSeriesBrand(Technician technician,
			Claim claim,ItemGroup series) {
		boolean isVerified = false;
		List<TechnicianCertification> techCertificates = technician
				.getTechnicianCertifications();
		CalendarDate repairDate = claim.getRepairDate();
		String brand = series.getBrandType();
		for (TechnicianCertification eachCertificate : techCertificates) {
			if(eachCertificate.getD().isActive()){
			boolean isCertCoreLevel = eachCertificate.getIsCoreLevel();
			boolean isRepairDateBetweenCertDate = isRepairDateBetweenCertDate(eachCertificate,repairDate);
			boolean isCertBrandMatchesSisterSeriesBrand = eachCertificate.getBrand()
					.equalsIgnoreCase(brand);
			if (isCertCoreLevel && isCertBrandMatchesSisterSeriesBrand
					&& isRepairDateBetweenCertDate) {
				isVerified = true;
				break;
				}
			}
		}
		return isVerified;
	
	}

	private boolean verifyIfUtilevTruck(Claim claim) {
		ItemGroup series = getSeriesOfClaimedItem(claim);
		SeriesRefCertification seriesRefCertification = getCertificateService()
				.findBySeries(series);
		if(seriesRefCertification==null){ //series does not exist in seriesRefCertification, so it might be a Utilev 
			return true;
		}
		return false;
	}
	
	private void notifyProcessor(boolean isVerified, Claim claim) {
		if (isVerified) {
			claim.addNotifications(TECHNICIAN_CERTIFIED);
		} else {
			claim.addNotifications(TECHNICIAN_NOTCERTIFIED);
		}
	}

	private boolean isSeriesHasAtleastOnePRLevel(ItemGroup series,
			CalendarDate repairDate) {
		SeriesRefCertification seriesRefCertification = getCertificateService()
				.findBySeries(series);
		if (seriesRefCertification != null) {
			boolean isSeriesInPeriod = isInPeriodOfRepairDate(
					seriesRefCertification, repairDate);
			if (isSeriesInPeriod) {
				List<SeriesCertification> certifactionsForSeries = seriesRefCertification
						.getSeriesCertification();
				for (SeriesCertification eachCertification : certifactionsForSeries) {
					if (eachCertification.getCategoryLevel().equals(AdminConstants.PRODUCT_CERTIFICATE_LEVEL)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isInPeriodOfRepairDate(
			SeriesRefCertification seriesRefCertification,
			CalendarDate repairDate) {
		CalendarDate startDate = seriesRefCertification.getStartDate();
		CalendarDate endDate = seriesRefCertification.getEndDate();
		if (repairDate.isAfter(startDate) && repairDate.isBefore(endDate)) {
			return true;
		}
		return false;
	}

	private ItemGroup getSeriesOfClaimedItem(Claim claim) {
		ItemReference itemReference = claim.getClaimedItems().get(0)
				.getItemReference();
		ItemGroup series;
		if (itemReference.isSerialized()) {
			series = itemReference.getUnserializedItem().getProduct();
		} else {
			if(itemReference.getModel()==null)
				return null;
			series = itemReference.getModel().getIsPartOf();
		}
		return series;
	}

	private boolean isTechExists(Technician technician) {
		if (technician != null) {
			return true;
		}
		return false;
	}

	private boolean isTechHasSeriesCertification(Technician technician,
			SeriesCertification certification, CalendarDate repairDate) {
		List<TechnicianCertification> techCertifications = technician
				.getTechnicianCertifications();
		for (TechnicianCertification eachTechCertification : techCertifications) {
			if(eachTechCertification.getD().isActive()){
			boolean isCertFromDateBeforeRepairDate = eachTechCertification
					.getCertificationFromDate().isBefore(repairDate)
					|| eachTechCertification.getCertificationFromDate().equals(
							repairDate);
			if (!eachTechCertification.getIsCoreLevel()){
				for(SeriesRefCertification eachCert:eachTechCertification.getSeriesCertification()){
					if(eachCert.equals(certification.getSeriesRefCert()) && isCertFromDateBeforeRepairDate){
						return true;
						}
					}				
				}
			}
		}
		return false;
	}

	private boolean isTechHasPRLevelOfSeries(ItemGroup series,
			Technician technician, CalendarDate repairDate) {
		boolean isVerified = false;
		SeriesRefCertification seriesRefCertification = getCertificateService()
				.findBySeries(series);
		if (seriesRefCertification != null) {
			boolean isSeriesInPeriod = isInPeriodOfRepairDate(
					seriesRefCertification, repairDate);
			if (isSeriesInPeriod) {
				List<SeriesCertification> certifactionsForSeries = seriesRefCertification
						.getSeriesCertification();
				for (SeriesCertification eachCertification : certifactionsForSeries) {
					if (eachCertification.getCategoryLevel().equals(AdminConstants.PRODUCT_CERTIFICATE_LEVEL)) {
						isVerified = isTechHasSeriesCertification(technician,
								eachCertification, repairDate);
						break;
					}
				}
			}
		}
		return isVerified;
	}

	private boolean isRepairDateBetweenCertDate(
			TechnicianCertification eachCertificate, CalendarDate repairDate) {
		return (repairDate.isAfter(eachCertificate
				.getCertificationFromDate()) || repairDate.equals(eachCertificate
				.getCertificationFromDate())) && (repairDate.isBefore(
				 eachCertificate.getCertificationToDate()) || repairDate.equals(eachCertificate
							.getCertificationToDate()));
	}
	
/*	private boolean isTechBelongsToDealer(Technician technician, Claim claim) {
		@SuppressWarnings("unchecked")
		List<Dealership> dealers = (List<Dealership>) (List<?>) technician.getOrgUser()
				.getBelongsToOrganizations();
		Dealership forDealer = new HibernateCast<Dealership>().cast(claim
				.getForDealer());
		if (dealers.contains(forDealer))
			return true;
		return false;

	}*/

	public void isTechCertifiedForNotifyingProcessor(Claim claim) {
		boolean isVerified = isTechnicianCertifed(claim);
		String technicianEntered = claim.getServiceInformation()
				.getServiceDetail().getServiceTechnician();
		if (org.springframework.util.StringUtils.hasText(technicianEntered)) {
			notifyProcessor(isVerified,claim);
		}
	}

	public DealerGroupService getDealerGroupService() {
		return dealerGroupService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public CertificateService getCertificateService() {
		return certificateService;
	}

	public void setCertificateService(CertificateService certificateService) {
		this.certificateService = certificateService;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

}
