package tavant.twms.integration.layer.transformer.global.dealerinterfaces.claimsubmission;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.infra.HibernateCast;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.GoogleMapDistanceCalculater;
import tavant.twms.integration.layer.util.IntegrationLayerUtil;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.base.Rounding;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.CustomerDetails;

public class TravelDetailsCalculator {
	private final Logger logger = Logger.getLogger("dealerAPILogger");

	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;
	private SecurityHelper securityHelper;

	public void setTravelDetails(Claim claim,
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Map<String, String> errorCodeMap) {
		TravelDetail travelDetail = new TravelDetail();
		travelDetail.setUom("miles");
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo()
				.getName())) {
			setTravelDeatilsUsingGoogleMap(claim, claimSubmissionDTO,
					travelDetail, errorCodeMap);
		} else {
			if (claimSubmissionDTO.getAdditionalTravelHours() != null
					&& claimSubmissionDTO.getAdditionalTravelHours().intValue() > 0)
				travelDetail.setAdditionalHours(claimSubmissionDTO
						.getAdditionalTravelHours());
			if (claimSubmissionDTO.getTravelbyDistance() != null
					&& claimSubmissionDTO.getTravelbyDistance().intValue() > 0)
				travelDetail.setDistance(claimSubmissionDTO
						.getTravelbyDistance());
			if (claimSubmissionDTO.getTravelLocation() != null)
				travelDetail
						.setLocation(claimSubmissionDTO.getTravelLocation());
			BigInteger travelByTrip = claimSubmissionDTO.getTravelbyTrip();
			if (travelByTrip != null && travelByTrip.intValue() > 0) {
				travelDetail.setTrips(travelByTrip.intValue());
			}
			if (claimSubmissionDTO.getTravelbyHours() != null
					&& claimSubmissionDTO.getTravelbyHours().intValue() > 0)
				travelDetail.setHours(claimSubmissionDTO.getTravelbyHours().toString());
		}
		serviceDetail.setTravelDetails(travelDetail);
	}

	private void setTravelDeatilsUsingGoogleMap(Claim claim,
			ClaimSubmission claimSubmissionDTO, TravelDetail travelDetail,
			Map<String, String> errorCodeMap) {
		Map<BigDecimal, BigDecimal> distanceHoursMap = new HashMap<BigDecimal, BigDecimal>();
		if ((((claimSubmissionDTO.getTravelbyDistance() != null && claimSubmissionDTO
				.getTravelbyDistance().intValue() != 0) || (claimSubmissionDTO
				.getTravelbyHours() != null && claimSubmissionDTO
				.getTravelbyHours().intValue() != 0))
				&& claim.getServicingLocation() != null)) {
			String dealerServicingLocation = getAddressForGoogleMap(claim
					.getServicingLocation());
			String customerLocation = getCustomerLocationForGoogleMap(claim,
					claimSubmissionDTO, claimSubmissionDTO.getCustomerDetails());
			if (customerLocation == null
					|| StringUtils.isBlank(customerLocation)) {
				errorCodeMap
						.put(
								DealerInterfaceErrorConstants.CSA0135,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0135));
			} else {
				try {
					distanceHoursMap = GoogleMapDistanceCalculater
							.calculateDistance(dealerServicingLocation,
									customerLocation);
				} catch (Exception e) {
					logger.error(e);
					errorCodeMap
							.put(
									DealerInterfaceErrorConstants.CSA0138,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0138));
				}
				validateAndSetTravelElements(claim, claimSubmissionDTO,
						travelDetail, customerLocation, distanceHoursMap,
						errorCodeMap);
			}
		}
	}

	private void validateAndSetTravelElements(Claim claim,
			ClaimSubmission claimSubmissionDTO, TravelDetail travelDetail,
			String customerLocation,
			Map<BigDecimal, BigDecimal> distanceHoursMap,
			Map<String, String> errorCodeMap) {
		if (customerLocation != null
				&& StringUtils.isNotBlank(customerLocation)) {
			travelDetail.setLocation(customerLocation);
		}
		if (claimSubmissionDTO.getTravelbyTrip() != null) {
			travelDetail.setTrips(claimSubmissionDTO.getTravelbyTrip()
					.intValue());
		}
		if ((claimSubmissionDTO.getAdditionalTravelHours() != null
				&& claimSubmissionDTO.getAdditionalTravelHours().intValue() > 0 )&&
					(claimSubmissionDTO.getReasonForAdditionalTravelHours() == null
					|| StringUtils.isEmpty(claimSubmissionDTO
							.getReasonForAdditionalTravelHours()))) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA0139,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0139));
			}
		
		if (claimSubmissionDTO.getReasonForAdditionalTravelHours() != null
				&& StringUtils.isNotEmpty(claimSubmissionDTO
						.getReasonForAdditionalTravelHours())) {
			travelDetail.setAdditionalHoursReason(claimSubmissionDTO
					.getReasonForAdditionalTravelHours());
		}
		validateAndSetTravelAddressChange(claim,
				claimSubmissionDTO.getCustomerDetails(), travelDetail);
		validateAndSetTravelDistanceAndHours(claim, claimSubmissionDTO,
				travelDetail, distanceHoursMap, errorCodeMap);
	}

	private void validateAndSetTravelAddressChange(Claim claim,
			CustomerDetails customerDetails, TravelDetail travelDetail) {
		if (claim.getItemReference() != null
				&& claim.getItemReference().getReferredInventoryItem() != null
				&& (claim.getItemReference().getReferredInventoryItem()
						.isRetailed() || claim.getItemReference()
						.getReferredInventoryItem().getPreOrderBooking())) {
			if (claim.getItemReference().getReferredInventoryItem()
					.getOwnedBy() != null
					&& customerDetails != null
					&& claim.getItemReference().getReferredInventoryItem()
							.getOwnedBy().getAddress() != null) {
				if (isCustomerAddressChanged(claim.getItemReference()
						.getReferredInventoryItem().getOwnedBy().getAddress(),
						customerDetails)) {
					travelDetail.setTravelAddressChanged(Boolean.TRUE);
				}
			}

		}

	}

	/**
	 * checking all the address elements for address change
	 * 
	 * @param address
	 * @param customerDetails
	 * @return
	 */
	private boolean isCustomerAddressChanged(Address address,
			CustomerDetails customerDetails) {

		if (isAddressElementChanged(address.getAddressLine1(),
				customerDetails.getCustomerAddress())
				|| isAddressElementChanged(address.getAddressLine2(),
						customerDetails.getCustomerAddress2())
				|| isAddressElementChanged(address.getState(),
						customerDetails.getCustomerState())
				|| isAddressElementChanged(address.getCountry(),
						customerDetails.getCustomerCountry())
				|| isAddressElementChanged(address.getZipCode(),
						customerDetails.getCustomerZip())
				|| isAddressElementChanged(address.getCity(),
						customerDetails.getCustomerCity())) {
			return true;
		}
		return false;
	}

	private boolean isAddressElementChanged(String invCustAddressElem,
			String batchClaimAddressElem) {
		if (((invCustAddressElem == null || StringUtils
				.isBlank(invCustAddressElem)) && (batchClaimAddressElem != null && StringUtils
				.isNotBlank(batchClaimAddressElem)))
				|| ((invCustAddressElem != null && StringUtils
						.isNotBlank(invCustAddressElem)) && (batchClaimAddressElem == null || StringUtils
						.isBlank(batchClaimAddressElem)))
				|| (StringUtils.isEmpty(invCustAddressElem) && StringUtils
						.isNotEmpty(batchClaimAddressElem))
				|| (StringUtils.isEmpty(batchClaimAddressElem)
						&& StringUtils.isNotEmpty(invCustAddressElem) || (invCustAddressElem != null
						&& StringUtils.isNotEmpty(invCustAddressElem)
						&& batchClaimAddressElem != null
						&& StringUtils.isNotEmpty(batchClaimAddressElem) && !batchClaimAddressElem
							.equalsIgnoreCase(invCustAddressElem)))) {
			return true;
		}
		return false;
	}

	private void validateAndSetTravelDistanceAndHours(Claim claim,
			ClaimSubmission claimSubmissionDTO, TravelDetail travelDetail,
			Map<BigDecimal, BigDecimal> distanceHoursMap,
			Map<String, String> errorCodeMap) {
		if(distanceHoursMap==null||distanceHoursMap.isEmpty()){
			errorCodeMap
			.put(DealerInterfaceErrorConstants.CSA0145,
					dealerInterfaceErrorConstants
							.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0145));
		}
		for (Map.Entry<BigDecimal, BigDecimal> entry : distanceHoursMap
				.entrySet()) {
			BigDecimal googleMapsCalTravelMinsInHrsForTrips = getTravelHoursBasedOnTrips(GoogleMapDistanceCalculater
					.convertTravelSecsInHrs(entry.getKey()),claimSubmissionDTO.getTravelbyTrip());
			BigDecimal googleMapsCalDistanceInMilesForTrips=getTravelDistanceBasedOnTrips(GoogleMapDistanceCalculater
					.convertDistanceInMiles(entry.getValue()),claimSubmissionDTO.getTravelbyTrip());
			if (googleMapsCalDistanceInMilesForTrips != null
					&& googleMapsCalDistanceInMilesForTrips
							.equals(claimSubmissionDTO.getTravelbyDistance())) {
				travelDetail.setDistance(claimSubmissionDTO
						.getTravelbyDistance());
			} else if(googleMapsCalDistanceInMilesForTrips.floatValue()>claimSubmissionDTO
						.getTravelbyDistance().floatValue()){
				travelDetail.setDistance(googleMapsCalDistanceInMilesForTrips);
			}else if(googleMapsCalDistanceInMilesForTrips.floatValue()<claimSubmissionDTO
						.getTravelbyDistance().floatValue()){
				travelDetail.setDistance(claimSubmissionDTO
						.getTravelbyDistance());
			}
			if (googleMapsCalTravelMinsInHrsForTrips != null
					&& claimSubmissionDTO.getTravelbyHours().equals(
							googleMapsCalTravelMinsInHrsForTrips)) {
				travelDetail.setHours(claimSubmissionDTO.getTravelbyHours().toString());
			} else if(googleMapsCalTravelMinsInHrsForTrips.compareTo(claimSubmissionDTO.getTravelbyHours())<1){
				travelDetail.setHours(googleMapsCalTravelMinsInHrsForTrips.toString());
				BigDecimal dealerTravelHoursInSec=getTimeInSeconds(claimSubmissionDTO.getTravelbyHours().toString());
				BigDecimal googleMapsTravelHoursInSec=getTimeInSeconds(googleMapsCalTravelMinsInHrsForTrips.toString());
				BigDecimal diffOfDealerAnGoogleMpasHrs=dealerTravelHoursInSec.subtract(googleMapsTravelHoursInSec);
			   convertAndAddTheAdditionalTravelHours(diffOfDealerAnGoogleMpasHrs,claimSubmissionDTO.getAdditionalTravelHours(),travelDetail);
			}else if(googleMapsCalTravelMinsInHrsForTrips!=null){
				travelDetail.setHours(googleMapsCalTravelMinsInHrsForTrips.toString());
			}
		}
	}

	private BigDecimal getTravelHoursBasedOnTrips(
			BigDecimal convertTravelSecsInHrs, BigInteger travelbyTrip) {
		if (travelbyTrip != null && travelbyTrip.intValue() > 1) {
			BigDecimal timeInSeconds = getTimeInSeconds(convertTravelSecsInHrs
					.toString());
			return GoogleMapDistanceCalculater
					.convertDistanceInMiles(timeInSeconds.multiply(BigDecimal
							.valueOf(travelbyTrip.intValue())));
		}
		return convertTravelSecsInHrs;
	}

	private BigDecimal getTimeInSeconds(String timeStr) {
		String[] timeStrArray = timeStr.split(".");
		BigDecimal travelHoursInSec = null;
		if (timeStrArray.length > 1) {
			String hourStr = timeStrArray[0];
			String minsStr = timeStrArray[1];
			BigDecimal hoursInSec = new BigDecimal(Integer.parseInt(hourStr))
					.multiply(new BigDecimal(3600));
			BigDecimal minsInSec = new BigDecimal(Integer.parseInt(minsStr))
					.multiply(new BigDecimal(60));
			travelHoursInSec = hoursInSec.add(minsInSec);
		} else {
			travelHoursInSec = new BigDecimal(Float.parseFloat(timeStr))
					.multiply(new BigDecimal(3600));
		}
		return travelHoursInSec;
	}

	private BigDecimal getTravelDistanceBasedOnTrips(
			BigDecimal convertDistanceInMiles, BigInteger travelTrips) {
		if (travelTrips != null && travelTrips.intValue() > 1) {
			return convertDistanceInMiles.multiply(BigDecimal
					.valueOf(travelTrips.intValue()));
		}
		return convertDistanceInMiles;
	}

	private void convertAndAddTheAdditionalTravelHours(
			BigDecimal googleMapsDiffHours, BigDecimal additionalTravelHours2,
			TravelDetail travelDetail) {
		BigDecimal totalHours;
		if (additionalTravelHours2 != null
				&& additionalTravelHours2.floatValue() > 0) {
			BigDecimal totalAdditionalHrsInSecs=googleMapsDiffHours.add(getTimeInSeconds(additionalTravelHours2.toString()));
			 totalHours=GoogleMapDistanceCalculater
			.convertTravelSecsInHrsForAdditional(totalAdditionalHrsInSecs);
				travelDetail
						.setAdditionalHours(totalHours);
		} else {
			totalHours=GoogleMapDistanceCalculater
			.convertTravelSecsInHrsForAdditional(googleMapsDiffHours);
			travelDetail.setAdditionalHours(totalHours);
			if (travelDetail.getAdditionalHoursReason() == null) {
				travelDetail
						.setAdditionalHoursReason(IntegrationConstants.DEAFAULT_ADDITIONAL_TRAVEL_REASON);
			}
		}
	}

	public String getCustomerLocationForGoogleMap(
			Claim claim, ClaimSubmission claimSubmissionDTO, CustomerDetails customerDetails) {
		StringBuilder sb = new StringBuilder();
		if(claimSubmissionDTO.getTravelLocation()!=null&&StringUtils.isNotBlank(claimSubmissionDTO.getTravelLocation())){
			sb.append(claimSubmissionDTO.getTravelLocation());
		}else if(customerDetails!=null){
		if (customerDetails.getCustomerAddress() != null
				&& StringUtils.isNotBlank(customerDetails.getCustomerAddress())) {
			String addressLine1 = customerDetails.getCustomerAddress();
			sb.append(addressLine1);
		}
		if (customerDetails.getCustomerAddress2() != null
				&& StringUtils
						.isNotBlank(customerDetails.getCustomerAddress2())) {
			String addressLine2 = customerDetails.getCustomerAddress2();
			sb.append(addressLine2);
		}
		if (customerDetails.getCustomerCity() != null
				&& StringUtils.isNotBlank(customerDetails.getCustomerCity())) {
			String city = customerDetails.getCustomerCity();
			sb.append(",").append(city);
		}
		if (customerDetails.getCustomerState() != null
				&& StringUtils.isNotBlank(customerDetails.getCustomerState())) {
			String state = customerDetails.getCustomerState();
			sb.append(",").append(state);
		}
		if (customerDetails.getCustomerZip() != null
				&& StringUtils.isNotBlank(customerDetails.getCustomerZip())) {
			String zip = customerDetails.getCustomerZip();
			sb.append(",").append(zip);
		}
		if (customerDetails.getCustomerCountry() != null
				&& StringUtils.isNotBlank(customerDetails.getCustomerCountry())) {
			String country = customerDetails.getCustomerCountry();
			sb.append(",").append(country);
		}
		} else if (claim.getItemReference() != null
				&& claim.getItemReference().getReferredInventoryItem() != null
				&& claim.getItemReference().getReferredInventoryItem()
						.getOwnedBy() != null
				&& claim.getItemReference().getReferredInventoryItem()
						.isRetailed()
				&& claim.getItemReference().getReferredInventoryItem()
						.getOwnedBy().getAddress() != null) {
			sb.append(getAddressForGoogleMap( claim.getItemReference().getReferredInventoryItem()
						.getOwnedBy().getAddress()));
		}
		return sb.toString();
	}

	public String getAddressForGoogleMap(
			Address organizationAddress) {
		StringBuilder sb = new StringBuilder();
		if (organizationAddress.getAddressLine1() != null
				&& StringUtils
						.isNotBlank(organizationAddress.getAddressLine1())) {
			String addressLine1 = organizationAddress.getAddressLine1();
			sb.append(addressLine1);
		}
		if (organizationAddress.getAddressLine2() != null
				&& StringUtils
						.isNotBlank(organizationAddress.getAddressLine2())) {
			String addressLine2 = organizationAddress.getAddressLine2();
			sb.append(",").append(addressLine2);
		}
		if (organizationAddress.getCity() != null
				&& StringUtils.isNotBlank(organizationAddress.getCity())) {
			String city = organizationAddress.getCity();
			sb.append(",").append(city);
		}
		if (organizationAddress.getState() != null
				&& StringUtils.isNotBlank(organizationAddress.getState())) {
			String state = organizationAddress.getState();
			sb.append(",").append(state);
		}
		if (organizationAddress.getZipCode() != null
				&& StringUtils.isNotBlank(organizationAddress.getZipCode())) {
			String zip = organizationAddress.getZipCode();
			sb.append(",").append(zip);
		}
		if (organizationAddress.getCountry() != null
				&& StringUtils.isNotBlank(organizationAddress.getCountry())) {
			String country = organizationAddress.getCountry();
			sb.append(",").append(country);
		}
		return sb.toString();
	}


	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

}
