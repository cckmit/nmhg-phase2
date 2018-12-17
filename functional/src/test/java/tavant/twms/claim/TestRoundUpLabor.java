package tavant.twms.claim;

import java.math.BigDecimal;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimServiceImpl;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.orgmodel.MinimumLaborRoundUp;
import tavant.twms.domain.orgmodel.MinimumLaborRoundUpService;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.security.SelectedBusinessUnitsHolder;

public class TestRoundUpLabor extends IntegrationTestCase {
	ClaimService claimService;

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public MinimumLaborRoundUpService getMinimumLaborRoundUpService() {
		return minimumLaborRoundUpService;
	}

	public void setMinimumLaborRoundUpService(MinimumLaborRoundUpService minimumLaborRoundUpService) {
		this.minimumLaborRoundUpService = minimumLaborRoundUpService;
	}

	ClaimServiceImpl claimServiceImpl;
	MinimumLaborRoundUpService minimumLaborRoundUpService;

	@SuppressWarnings("deprecation")
	public void testPositiveRoundUpOnLaborHoursForClaim() {
		login("sedinap");
		Claim claim = claimService.findClaimByNumber("10234664");
		assertNotNull(claim);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		assertNotNull(minimumLaborRoundUp);
		assertNotNull(minimumLaborRoundUp.getRoundUpHours());
		assertNotNull(minimumLaborRoundUp.getDaysBetweenRepair());
		BigDecimal totalLaborHours = claimService.totalLaborHoursOnClaim(claim);
		assertTrue(totalLaborHours.compareTo(new BigDecimal(minimumLaborRoundUp.getRoundUpHours())) == -1);
		assertTrue(minimumLaborRoundUp.getApplicableProducts() == null
				|| minimumLaborRoundUp.getApplicableProducts().isEmpty()
				|| (minimumLaborRoundUp.getApplicableProducts() != null && minimumLaborRoundUp.getApplicableProducts()
						.contains(claim.getItemReference().getUnserializedItem().getProduct())));
		claimService.roundUpLaborOnClaim(claim);
		assertTrue(claimService.hasRoundUpCode(claim));
		assertTrue(claimService.totalLaborHoursOnClaim(claim).compareTo(new BigDecimal(minimumLaborRoundUp.getRoundUpHours())) == 0);
	}

	@SuppressWarnings("deprecation")
	public void testNegativeRoundUpOnLaborHoursForClaim() {

		login("sedinap");
		Claim claim = claimService.findClaimByNumber("10234666");
		assertNotNull(claim);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		assertNotNull(minimumLaborRoundUp);
		assertNotNull(minimumLaborRoundUp.getRoundUpHours());
		assertNotNull(minimumLaborRoundUp.getDaysBetweenRepair());
		assertTrue((claimService.getRoundUpHoursOnAllClaimsInWindowPeriod(claimService.getAcceptedClaimsInWindowPeriod(claim)).compareTo(BigDecimal.ZERO)) == 1);
		assertTrue(minimumLaborRoundUp.getApplicableProducts() == null
				|| minimumLaborRoundUp.getApplicableProducts().isEmpty()
				|| (minimumLaborRoundUp.getApplicableProducts() != null && minimumLaborRoundUp.getApplicableProducts()
						.contains(claim.getItemReference().getUnserializedItem().getProduct())));
		assertTrue(claimService.getAcceptedClaimsInWindowPeriod(claim).size() > 0);
		claimService.roundUpLaborOnClaim(claim);
		assertTrue(claimService.hasRoundUpCode(claim));
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(BigDecimal.ZERO) == -1);

	}
	
	@SuppressWarnings("deprecation")
	public void testLaborRoundUpOnClaimAdjustment()
	{
		login("sedinap");
		Claim claim = claimService.findClaimByNumber("10234664");
		assertNotNull(claim);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		assertNotNull(minimumLaborRoundUp);
		assertNotNull(minimumLaborRoundUp.getRoundUpHours());
		assertNotNull(minimumLaborRoundUp.getDaysBetweenRepair());
		claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0).setHoursSpent(new BigDecimal(0));
		BigDecimal totalLaborHours = claimService.totalLaborHoursOnClaim(claim);
		assertTrue(totalLaborHours.equals(BigDecimal.ZERO));
		assertTrue(totalLaborHours.compareTo(new BigDecimal(minimumLaborRoundUp.getRoundUpHours())) == -1);
		assertTrue(minimumLaborRoundUp.getApplicableProducts() == null
				|| minimumLaborRoundUp.getApplicableProducts().isEmpty()
				|| (minimumLaborRoundUp.getApplicableProducts() != null && minimumLaborRoundUp.getApplicableProducts()
						.contains(claim.getItemReference().getUnserializedItem().getProduct())));
		claimService.roundUpLaborOnClaim(claim);
		assertFalse(claimService.hasRoundUpCode(claim));		
		claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0).setHoursSpent(new BigDecimal(minimumLaborRoundUp.getRoundUpHours()));
		claimService.roundUpLaborOnClaim(claim);
		assertFalse(claimService.hasRoundUpCode(claim));
		claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0).setHoursSpent(new BigDecimal(minimumLaborRoundUp.getRoundUpHours()).add(BigDecimal.ONE));
		claimService.roundUpLaborOnClaim(claim);
		assertFalse(claimService.hasRoundUpCode(claim));
		claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0).setHoursSpent(new BigDecimal(minimumLaborRoundUp.getRoundUpHours()).add(BigDecimal.ONE.negate()));
		claimService.roundUpLaborOnClaim(claim);
		assertTrue(claimService.hasRoundUpCode(claim));
	}
	
	 //The Adjustment on a Claim on Credit Submission after a Claim in its window period has been denied 
	public void testLaborRoundUpOnCreditSubmissionAfterClaimDenial()
	{
		login("sedinap");
		Claim claim = claimService.findClaimByNumber("10234668");	
		assertNotNull(claim);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		assertNotNull(minimumLaborRoundUp);
		assertNotNull(minimumLaborRoundUp.getRoundUpHours());
		assertNotNull(minimumLaborRoundUp.getDaysBetweenRepair());
		BigDecimal totalLaborHours = claimService.totalLaborHoursOnClaim(claim);
		assertTrue(totalLaborHours.equals(BigDecimal.ZERO));
		assertTrue(claimService.hasRoundUpCode(claim));
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(BigDecimal.ONE.negate())==0);
		claimService.reopenClaimForLaborRndUpOnCreditSubmission(claim);
		assertTrue(claimService.hasRoundUpCode(claim));
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(BigDecimal.ONE)==0);
		assertTrue(claimService.totalLaborHoursOnClaim(claim).compareTo(new BigDecimal(minimumLaborRoundUp.getRoundUpHours())) == 0);
	}
	
	public void testPositiveLaborAdjustmentsOnClaimReopen()
	{		
		login("sedinap");
		Claim claim = claimService.findClaimByNumber("10234669");	
		assertNotNull(claim);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		assertNotNull(minimumLaborRoundUp);
		assertNotNull(minimumLaborRoundUp.getRoundUpHours());
		assertNotNull(minimumLaborRoundUp.getDaysBetweenRepair());
		assertTrue(claimService.hasRoundUpCode(claim));
		LaborDetail roundUpLaborDetail = claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0);
		LaborDetail laborDetail = claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(1);
		claimService.checkAdjustmentForRndUpLaborOnClaim(claim,roundUpLaborDetail);		
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(new BigDecimal(1.5))==0);
		laborDetail.setHoursSpent(BigDecimal.ZERO);
		claimService.checkAdjustmentForRndUpLaborOnClaim(claim,roundUpLaborDetail);		
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(new BigDecimal(2))==0);
		laborDetail.setHoursSpent(new BigDecimal(2.9));
		claimService.checkAdjustmentForRndUpLaborOnClaim(claim,roundUpLaborDetail);		
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(new BigDecimal(1.0))==0);
		laborDetail.setHoursSpent(new BigDecimal(4));
		claimService.checkAdjustmentForRndUpLaborOnClaim(claim,roundUpLaborDetail);		
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(new BigDecimal(1.0))==0);
		
	}
	
	public void testNegativeLaborAdjustmentsOnClaimReopen()
	{
		login("sedinap");
		Claim claim = claimService.findClaimByNumber("10234671");		
		assertNotNull(claim);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		assertNotNull(minimumLaborRoundUp);
		assertNotNull(minimumLaborRoundUp.getRoundUpHours());
		assertNotNull(minimumLaborRoundUp.getDaysBetweenRepair());
		LaborDetail laborDetail = claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(1);
		LaborDetail roundUpLaborDetail = claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0);
		claimService.checkAdjustmentForRndUpLaborOnClaim(claim,roundUpLaborDetail);		
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(BigDecimal.ONE.negate())==0);
		laborDetail.setHoursSpent(new BigDecimal(0.5));
		claimService.checkAdjustmentForRndUpLaborOnClaim(claim,roundUpLaborDetail);		
		assertTrue(claimService.getRoundUpHoursOnClaim(claim).compareTo(new BigDecimal(0.5).negate())==0);
		laborDetail.setHoursSpent(BigDecimal.ZERO);
		claimService.checkAdjustmentForRndUpLaborOnClaim(claim,roundUpLaborDetail);		
		assertFalse(claimService.hasRoundUpCode(claim));
	}
	
	
	public void testLaborRoundUpOnClaimDenial()
	{
		login("sedinap");
		Claim deniedClaim = claimService.findClaimByNumber("10234670");	
		assertNotNull(deniedClaim);
		Claim acceptedClaimInWindowPeriod = claimService.findClaimByNumber("10234672");
		assertNotNull(acceptedClaimInWindowPeriod);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(deniedClaim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		assertNotNull(minimumLaborRoundUp);
		assertNotNull(minimumLaborRoundUp.getRoundUpHours());
		assertNotNull(minimumLaborRoundUp.getDaysBetweenRepair());
		BigDecimal totalLaborHoursOnDeniedClaim = claimService.totalLaborHoursOnClaim(deniedClaim);
		BigDecimal totalLaborHoursOnAcceptedClaim = claimService.totalLaborHoursOnClaim(acceptedClaimInWindowPeriod);
		assertTrue(totalLaborHoursOnDeniedClaim.compareTo(new BigDecimal(minimumLaborRoundUp.getRoundUpHours()))==0);
		assertTrue(totalLaborHoursOnAcceptedClaim.compareTo(BigDecimal.ZERO)==0);
		assertTrue(claimService.hasRoundUpCode(deniedClaim));
		assertTrue(claimService.hasRoundUpCode(acceptedClaimInWindowPeriod));
		assertTrue(claimService.getRoundUpHoursOnClaim(acceptedClaimInWindowPeriod).compareTo(BigDecimal.ONE.negate())==0);
		claimService.reopenClaimForLaborRndUpOnClaimDenial(deniedClaim);
		totalLaborHoursOnAcceptedClaim = claimService.totalLaborHoursOnClaim(acceptedClaimInWindowPeriod);
		assertTrue(totalLaborHoursOnDeniedClaim.compareTo(new BigDecimal(minimumLaborRoundUp.getRoundUpHours()))==0);
	}
	
	public void testLaborRoundUpFprAllClaimTypes(){
		login("palmerjd");
		Claim claim = claimService.findClaimByNumber("10234152");	
		assertNotNull(claim);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		MinimumLaborRoundUp minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		if(claim.getType().equals(ClaimType.MACHINE)){
			System.out.print(claim.getType());
			System.out.print(minimumLaborRoundUp.getApplMachineClaim());
		}
		if(claim.getType().equals(ClaimType.PARTS)){
			System.out.print(claim.getType());
		
			System.out.print(minimumLaborRoundUp.getApplPartsClaim());
		
		}
		if(claim.getType().equals(ClaimType.CAMPAIGN)){
		
		System.out.print(claim.getType());
		System.out.print(minimumLaborRoundUp.getApplCampaignClaim());
		}
		
}


	
	

}
