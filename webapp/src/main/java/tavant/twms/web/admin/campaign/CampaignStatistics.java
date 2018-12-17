package tavant.twms.web.admin.campaign;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignStatus;
import tavant.twms.domain.campaign.FieldModUpdateStatus;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.common.CurrencyConversionException;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class CampaignStatistics {
	private int totalSerialNumberFixed = 0;
	int totalClaimsAccepted = 0;
	private int unitsPercentageFixed;
	private int claimsAcceptancePercentage;
	private int unitsUnavailable = 0;
	private int unitsUnavailablePercentage;
	int unitsRemaining = 0;
	private int unitsRemainingPercentage;
	private CampaignAssignmentService campaignAssignmentService;
	private CurrencyExchangeRateRepository currencyExchangeRateRepository;
	int unitsCompleted =0;
	int unitsCompletedPercentage;

	Money amountPaidForCompletedItems = Money.valueOf(0.0,
			Currency.getInstance("USD"));

	public int getTotalSerialNumberFixed() {
		return totalSerialNumberFixed;
	}

	public Money getAmountPaidForCompletedItems() {
		return amountPaidForCompletedItems;
	}

	public int getUnitsRemainingPercentage() {
		return unitsRemainingPercentage;
	}

	public void setUnitsRemainingPercentage(int unitsRemainingPercentage) {
		this.unitsRemainingPercentage = unitsRemainingPercentage;
	}

	public int getUnitsUnavailable() {
		return unitsUnavailable;
	}

	public void setUnitsUnavailable(int unitsUnavailable) {
		this.unitsUnavailable = unitsUnavailable;
	}

	public int getUnitsRemaining() {
		return unitsRemaining;
	}

	public void setUnitsRemaining(int unitsRemaining) {
		this.unitsRemaining = unitsRemaining;
	}

	public int getUnitsUnavailablePercentage() {
		return unitsUnavailablePercentage;
	}

	public void setUnitsUnavailablePercentage(int unitsUnavailablePercentage) {
		this.unitsUnavailablePercentage = unitsUnavailablePercentage;
	}

	public void setAmountPaidForCompletedItems(Money amountPaidForCompletedItems) {
		this.amountPaidForCompletedItems = amountPaidForCompletedItems;
	}

	public void setTotalSerialNumberFixed(int totalSerialNumberFixed) {
		this.totalSerialNumberFixed = totalSerialNumberFixed;
	}

	public int getTotalClaimsAccepted() {
		return totalClaimsAccepted;
	}

	public void setTotalClaimsAccepted(int totalClaimsAccepted) {
		this.totalClaimsAccepted = totalClaimsAccepted;
	}

	public int getUnitsPercentageFixed() {
		return unitsPercentageFixed;
	}

	public void setUnitsPercentageFixed(int unitsPercentageFixed) {
		this.unitsPercentageFixed = unitsPercentageFixed;
	}

	public int getClaimsAcceptancePercentage() {
		return claimsAcceptancePercentage;
	}

	public void setClaimsAcceptancePercentage(int claimsAcceptancePercentage) {
		this.claimsAcceptancePercentage = claimsAcceptancePercentage;
	}

	public CampaignAssignmentService getCampaignAssignmentService() {
		return campaignAssignmentService;
	}

	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public void setCurrencyExchangeRateRepository(
			CurrencyExchangeRateRepository currencyExchangeRateRepository) {
		this.currencyExchangeRateRepository = currencyExchangeRateRepository;
	}
	
	public int getUnitsCompletedPercentage() {
		return unitsCompletedPercentage;
	}

	public void setUnitsCompletedPercentage(int unitsCompletedPercentage) {
		this.unitsCompletedPercentage = unitsCompletedPercentage;
	}
	
	public int getUnitsCompleted() {
		return unitsCompleted;
	}

	public void setUnitsCompleted(int unitsCompleted) {
		this.unitsCompleted = unitsCompleted;
	}

	public void setStatisticsData(
			List<CampaignNotification> campaignNotifications,List<Claim> claimsForCampaignNotifications) {
		String[] notificationStatus = {CampaignNotification.COMPLETE,CampaignNotification.INPROCESS};
		if (campaignNotifications != null && !campaignNotifications.isEmpty()) {
			for (CampaignNotification campaignNotification : campaignNotifications) {
				if (Arrays.asList(notificationStatus).contains(
						campaignNotification.getNotificationStatus())) {
					this.totalSerialNumberFixed++;
					/*for (Claim claims : claimsForCampaignNotifications) {
						convertFromNaturalToBaseCurrency(claims);
					}*/
				}
				if(("Inactive").equalsIgnoreCase(campaignNotification.getStatus()) && FieldModUpdateStatus.ACCEPTED.equals(campaignNotification.getCampaignStatus())){
					this.unitsUnavailable++;
				}
			}
			this.unitsRemaining = (campaignNotifications.size() - (this.unitsUnavailable + this.totalClaimsAccepted));
			
			if (totalSerialNumberFixed > 0) {
				setUnitsPercentageFixed((int) (this.totalSerialNumberFixed * 100.0f)
						/ campaignNotifications.size());
			}
			if (totalClaimsAccepted > 0) {
				setClaimsAcceptancePercentage((int) (this.totalClaimsAccepted * 100.0f)
						/ campaignNotifications.size());
			}
			if (unitsRemaining > 0) {
				setUnitsRemainingPercentage((int) (this.unitsRemaining * 100.0f)
						/ campaignNotifications.size());
			}
			
			if(unitsUnavailable>0) {
				setUnitsUnavailablePercentage((int) (this.unitsUnavailable * 100.0f)
						/ campaignNotifications.size());
			}
			
			if (unitsCompleted > 0) {
				setUnitsCompletedPercentage((int) (this.unitsCompleted * 100.0f)
						/ campaignNotifications.size());
			}
		}

	}
	/*
	 * private Object convertFromNaturalToBaseCurrency(Claim claim) { try { if
	 * (claim.getActiveClaimAudit().getPayment() != null &&
	 * claim.getActiveClaimAudit
	 * ().getState().equals(ClaimState.ACCEPTED_AND_CLOSED)) { Money amountPaid
	 * = claim.getActiveClaimAudit().getPayment() .getTotalAmount();
	 * CalendarDate dateToBeUsed = claim.getCreditDate() != null ? claim
	 * .getCreditDate() : claim.getRepairDate(); this.totalClaimsAccepted++; if
	 * (amountPaid != null) { Currency naturalCurrency = amountPaid
	 * .breachEncapsulationOfCurrency(); Currency baseCurrency =
	 * Currency.getInstance("USD"); CurrencyConversionFactor conversionFactor =
	 * this.currencyExchangeRateRepository
	 * .findConversionFactor(naturalCurrency, baseCurrency, dateToBeUsed); if
	 * (conversionFactor == null) { return Money.valueOf(0.0, baseCurrency); }
	 * Money convertedValue = conversionFactor.convert(amountPaid,
	 * dateToBeUsed); this.amountPaidForCompletedItems =
	 * this.amountPaidForCompletedItems .plus(convertedValue); } }
	 * 
	 * } catch (CurrencyConversionException ex) { throw new RuntimeException(
	 * "Failed to convert currencies for claim [" + claim + "]", ex); } return
	 * null; }
	 */

}