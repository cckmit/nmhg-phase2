package tavant.twms.integration.layer.component.global;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.CriteriaBasedValue;
import tavant.twms.domain.claim.payment.definition.modifiers.DealerSummaryService;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.integration.SyncTrackerDAO;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.CalendarUtil;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.nmhg.syncitalyqanotification.MTSyncItalyQANotificationSLMSDocument;
import com.nmhg.syncitalyqanotification.SyncItalyQANotification;
import com.nmhg.syncitalyqanotification.SyncItalyQANotification.ApplicationArea;
import com.nmhg.syncitalyqanotification.SyncItalyQANotification.ApplicationArea.Sender;
import com.nmhg.syncitalyqanotification.SyncItalyQANotification.DataArea;

public class ProcessItalyClaimNotification {
	 private static Logger logger = Logger.getLogger(ProcessItalyClaimNotification.class
	            .getName());
	 private IntegrationPropertiesBean integrationPropertiesBean;
	 private OrgService orgService;
	 private SyncTrackerDAO syncTrackerDAO;
	 private DealerSummaryService dealerSummaryService;
	 private final String SEQUENCE = "REFERENCE_ID_SEQ"; 
	 private ItemMappingService itemMappingService;
	 private SupplierService supplierService;
	 
	 public MTSyncItalyQANotificationSLMSDocument syncNotificationClaimDetails(Claim claim) {
	        if (logger.isInfoEnabled()) {
	            logger.info("ProcessItalyClaimNotification :: syncNotificationClaimDetails , The Claim :    "
	                    + claim.getClaimNumber());
	        }
	        return createBodFromObject(claim);
	    }
	private MTSyncItalyQANotificationSLMSDocument createBodFromObject(Claim claim) {
		MTSyncItalyQANotificationSLMSDocument mTSyncItalyQANotificationSLMSDocument = MTSyncItalyQANotificationSLMSDocument.Factory
				.newInstance();
		SyncItalyQANotification syncItalyQANotification = mTSyncItalyQANotificationSLMSDocument
				.addNewMTSyncItalyQANotificationSLMS();
		ApplicationArea applicationAreaTypeDTO = createApplicationArea(claim);
		DataArea dataAreaTypeDTO = createDataArea(claim);
		syncItalyQANotification.setApplicationArea(applicationAreaTypeDTO);
		syncItalyQANotification.setDataArea(dataAreaTypeDTO);
		return mTSyncItalyQANotificationSLMSDocument;
	}
	private ApplicationArea createApplicationArea(Claim claim) {
		ApplicationArea applicationAreaTypeDTO = ApplicationArea.Factory
                .newInstance();
        Sender senderTypeDto = Sender.Factory.newInstance();
        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessItalyQANotification :: createApplicationArea , The Claim :    "
                            + claim.getClaimNumber());
        }
        senderTypeDto.setReferenceId(claim.getClaimNumber() + "_Notification");
        {
            logger
                    .info("ProcessItalyQANotification :: exiting createApplicationArea , The Claim :    "
                            + claim.getClaimNumber());
        }
        senderTypeDto.setLogicalId(integrationPropertiesBean.getLogicalIdForItalyClaimNotification());
        senderTypeDto.setTask(integrationPropertiesBean.getTaskIdForItalyClaimNotification());
        senderTypeDto.setReferenceId(syncTrackerDAO.getReferenceId(SEQUENCE));
        applicationAreaTypeDTO.setSender(senderTypeDto);
        Calendar calendar = Calendar.getInstance();
		applicationAreaTypeDTO.setCreationDateTime(CalendarUtil
				.convertToDateTimeToString(calendar.getTime()));
        applicationAreaTypeDTO.setInterfaceNumber(integrationPropertiesBean.getBodIdForItalyClaimNotification());
        applicationAreaTypeDTO.setBODId(integrationPropertiesBean.getBodIdForItalyClaimNotification());
        return applicationAreaTypeDTO;
	}
	
	private DataArea createDataArea(Claim claim){
		logger.error("##################Processing Italy Claim number #####################"+claim.getClaimNumber());
		DataArea dataAreaTypeDTO = DataArea.Factory.newInstance();
		if(syncTrackerDAO.isClaimExistInSyncTracker(claim.getClaimNumber(),IntegrationConstants.ITALY_CLAIM_SYNC_TYPE)){
			dataAreaTypeDTO.setTransactionType(SyncItalyQANotification.DataArea.TransactionType.Enum.forString(IntegrationConstants.CHANGED));
		}else{
			dataAreaTypeDTO.setTransactionType(SyncItalyQANotification.DataArea.TransactionType.Enum.forString(IntegrationConstants.ADDED));
		} 
		
		if((claim.isNcr()!=null&&claim.isNcr())||(claim.isNcrWith30Days()!=null&&claim.isNcrWith30Days())){
					dataAreaTypeDTO.setIsNCR(SyncItalyQANotification.DataArea.IsNCR.Enum.forString(IntegrationConstants.NCR));
		}else{
			dataAreaTypeDTO.setIsNCR(SyncItalyQANotification.DataArea.IsNCR.Enum.forString(IntegrationConstants.WAR));
		}
		String status = claim.getActiveClaimAudit().getState().getState();
		if (ClaimState.ACCEPTED.equals(claim.getActiveClaimAudit().getState())
				|| ClaimState.ACCEPTED_AND_CLOSED.equals(claim
						.getActiveClaimAudit().getState())
				|| ClaimState.PENDING_PAYMENT_RESPONSE.equals(claim
						.getActiveClaimAudit().getState())
				|| ClaimState.PENDING_PAYMENT_SUBMISSION.equals(claim
						.getActiveClaimAudit().getState())) {
			status = IntegrationConstants.ACCEPTED;
		} else if(ClaimState.REJECTED.equals(claim.getActiveClaimAudit().getState()) || ClaimState.DENIED_AND_CLOSED.equals(claim.getActiveClaimAudit().getState())){
			status = IntegrationConstants.REJECTED;
		}else{
			status = IntegrationConstants.WORKINPROGRESS;
		}
		dataAreaTypeDTO.setStatus(SyncItalyQANotification.DataArea.Status.Enum.forString(status));
		String billToCode = claim.getForDealer().getDealerNumber();
		if(billToCode.length()<=6){
			dataAreaTypeDTO.setBillToCode(billToCode);
		}else{
			dataAreaTypeDTO.setBillToCode(billToCode.substring((billToCode.length()-6),(billToCode.length())));
		}
		if(claim.getServicingLocation()!=null){
			dataAreaTypeDTO.setShipToCode(claim.getServicingLocation().getSiteNumber().substring(8,12));
		}
		dataAreaTypeDTO.setClaimNumber(claim.getClaimNumber().substring(0, Math.min(claim.getClaimNumber().length(), 13)));
		if(BrandType.HYSTER.getType().equals(claim.getBrand())){
			dataAreaTypeDTO.setTruckBrand(IntegrationConstants.HYSTER);
		}else if(BrandType.YALE.getType().equals(claim.getBrand())){
			dataAreaTypeDTO.setTruckBrand(IntegrationConstants.YALE);
		}
		if(claim.getForItem()!=null){
			dataAreaTypeDTO.setMarketingGroupCode(claim
					.getForItem()
					.getMarketingGroupCode()
					.substring(
							0,
							Math.min(claim.getForItem().getMarketingGroupCode()
									.length(), 3)));
			dataAreaTypeDTO.setSerialNumber(claim
					.getForItem()
					.getSerialNumber()
					.substring(
							0,
							Math.min(claim.getForItem().getSerialNumber()
									.length(), 11)));
			if(claim.getForItem().getType().equals(InventoryType.RETAIL)){
				Calendar beginDate = CalendarUtil.convertToXMLCalendar((claim.getForItem().getDeliveryDate()));
				Calendar endDate = CalendarUtil.convertToXMLCalendar(claim.getFiledOnDate());
				int daysDivider=24*60*60*1000;
				if(beginDate.after(endDate)){
					Calendar date = beginDate;
					beginDate = endDate;
					endDate = date;
				}
				long differenceInMillis=endDate.getTimeInMillis()-beginDate.getTimeInMillis();
				int diffInDays = ((int)(differenceInMillis/daysDivider)/30)+1;
				dataAreaTypeDTO.setDaysBucket(diffInDays);
			}
		}
		dataAreaTypeDTO.setEntryDate(CalendarUtil.convertToXMLCalendar(claim.getFiledOnDate()));
		dataAreaTypeDTO.setClaimDate(CalendarUtil.convertToXMLCalendar(claim.getFiledOnDate()));
		dataAreaTypeDTO.setPartFittedDate(CalendarUtil.convertToXMLCalendar(claim.getActiveClaimAudit().getRepairDate()));
		if(claim.getHoursInService()!=null){
			String strValue = claim
					.getHoursInService()
					.toPlainString()
					.substring(
							0,
							Math.min(claim.getHoursInService().toPlainString()
									.length(), 5));
			int hoursOnTruck = claim.getHoursInService().intValue();;
			dataAreaTypeDTO.setHourMeterReading(Integer.parseInt(strValue));
			if(hoursOnTruck<=10){
				dataAreaTypeDTO.setHourBucket(10);
			}else if(hoursOnTruck>10 && hoursOnTruck<=50){
				dataAreaTypeDTO.setHourBucket(50);
			}else if(hoursOnTruck>50 && hoursOnTruck<=100){
				dataAreaTypeDTO.setHourBucket(100);
			}else if(hoursOnTruck>100){
				Long hours = (long) ((((float)hoursOnTruck / 100) * 100) + 100);
				if(hours>99999){
					dataAreaTypeDTO.setHourBucket(99999);
				}else{
					dataAreaTypeDTO.setHourBucket(hours.intValue());
				}
			}
		}
		if(claim.getServiceInformation()!=null && claim.getServiceInformation().getCausalPart()!=null){
			dataAreaTypeDTO
					.setPrimaryFailedPart(claim
							.getServiceInformation()
							.getCausalPart()
							.getNumber()
							.substring(
									0,
									Math.min(claim.getServiceInformation()
											.getCausalPart().getNumber()
											.length(), 15)));
			dataAreaTypeDTO
					.setPrimaryFailedDesc(claim
							.getServiceInformation()
							.getCausalPart()
							.getDescription()
							.substring(
									0,
									Math.min(claim
											.getServiceInformation()
											.getCausalPart()
											.getDescription()
											.length(), 30)));
			if(claim.getServiceInformation().getCausedBy()!=null){
				dataAreaTypeDTO.setMalFunctionCode(claim.getServiceInformation().getCausedBy().getCode().substring(
						0,
						Math.min(claim.getServiceInformation().getCausedBy().getCode()
								.length(), 2)));
			}
			if(claim.getServiceInformation().getFaultFound()!=null){
				dataAreaTypeDTO.setDivisionCode(claim.getServiceInformation().getFaultFound().getCode().substring(
						0,
						Math.min(claim.getServiceInformation().getFaultFound().getCode()
								.length(), 5)));
			}
			String dateCode = null;
			String componentMarkings = null;
			String vendor = null;
			if(claim.getServiceInformation().getServiceDetail()!=null){
			for(HussmanPartsReplacedInstalled part : claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled()){
				for(OEMPartReplaced replacedPart : part.getReplacedParts())	{
					if(claim.getServiceInformation().getCausalPart().getNumber().equals(replacedPart.getItemReference().getReferredItem().getNumber())){
						dateCode = replacedPart.getDateCode();
						componentMarkings = replacedPart.getSerialNumber();
					}
					}
				}
			}
			if(claim.getServiceInformation().getCausalPart().getNumber()!=null){
				List<ItemMapping> itemMappings = itemMappingService.findItemMappingForItem(claim.getServiceInformation().getCausalPart());
				if(!itemMappings.isEmpty()){
				Supplier ownedBy = supplierService.findById(itemMappings.get(0).getToItem().getOwnedBy().getId());
				vendor=ownedBy.getSupplierNumber();
				}
			}
			if(dateCode!=null &&componentMarkings!=null){
				String componentMarkingValue=dateCode.trim()+" "+componentMarkings;
				dataAreaTypeDTO.setComponentMarking(componentMarkingValue.substring(0, Math.min(componentMarkingValue.length(), 30)));
			}
			else if (dateCode!=null){
				dataAreaTypeDTO.setComponentMarking(dateCode.trim().substring(0, Math.min(dateCode.trim().length(), 30)));
			}else if (componentMarkings!=null){
				dataAreaTypeDTO.setComponentMarking(componentMarkings.trim().substring(0, Math.min(componentMarkings.trim().length(), 30)));
			}
			if(vendor!=null && !vendor.isEmpty()){
				dataAreaTypeDTO.setVendor(vendor.substring(
						0,
						Math.min(vendor.length(), 6)));
			}
		}
		/**Each truck series belongs to one of those classes ((Class 1, 2, 3, 4 or 5)). 
		C = Counterbalanced  (Class 1, 4 or 5)
				W = Warehouse (Class 2 or 3)**/
		if (claim.getItemReference().getReferredItem() != null) {
			String truckType = claim.getItemReference().getReferredItem()
					.getProduct().getIsPartOf().getGroupCode();
			if (IntegrationConstants.TRUCK_CLASS1.equalsIgnoreCase(truckType)
					|| IntegrationConstants.TRUCK_CLASS4
							.equalsIgnoreCase(truckType)
					|| IntegrationConstants.TRUCK_CLASS5
							.equalsIgnoreCase(truckType)) {
				truckType = IntegrationConstants.TRUCK_C;
			} else if (IntegrationConstants.TRUCK_CLASS2
					.equalsIgnoreCase(truckType)
					|| IntegrationConstants.TRUCK_CLASS3
							.equalsIgnoreCase(truckType)) {
				truckType =IntegrationConstants.TRUCK_W;
			}
			dataAreaTypeDTO
					.setTruckType(SyncItalyQANotification.DataArea.TruckType.Enum
							.forString(truckType));
		}
		
		if(claim.getActiveClaimAudit().getAccountabilityCode()!=null){
			dataAreaTypeDTO.setResponsibilityCode(claim.getActiveClaimAudit().getAccountabilityCode().getCode());
		}
		dataAreaTypeDTO.setCurrencyCode(claim.getForDealer()
				.getPreferredCurrency().getCurrencyCode());
		BigDecimal labourCost = (Money.valueOf(0.00, claim.getForDealer()
				.getPreferredCurrency())).breachEncapsulationOfAmount();
		if (claim.getActiveClaimAudit().getPayment() != null) {
			setTravelCost(claim,dataAreaTypeDTO);
			if(claim.getActiveClaimAudit().getPayment().getLineItemGroup(Section.LABOR)!=null&&claim.getActiveClaimAudit().getPayment().getLineItemGroup(Section.LABOR).getTotalCreditAmount()!=null){
				labourCost=claim.getActiveClaimAudit().getPayment().getLineItemGroup(Section.LABOR).getTotalCreditAmount().breachEncapsulationOfAmount();
				if (claim.getServiceInformation() != null
						&& claim.getServiceInformation().getServiceDetail() != null
						&& claim.getServiceInformation().getServiceDetail()
								.getLaborPerformed() != null
						&& !claim.getServiceInformation().getServiceDetail()
								.getLaborPerformed().isEmpty()) {
					if (claim.getServiceInformation().getServiceDetail()
							.getLaborPerformed().get(0) != null
							&& claim.getServiceInformation().getServiceDetail()
									.getLaborPerformed().get(0).getTotalHours() != null) {
						dataAreaTypeDTO.setLabourHours(claim
								.getServiceInformation().getServiceDetail()
								.getLaborPerformed().get(0).getTotalHours());
					}
					if (claim.getServiceInformation().getServiceDetail()
							.getLaborPerformed().get(0) != null
							&& claim.getServiceInformation().getServiceDetail()
									.getLaborPerformed().get(0).getLaborRate() != null) {
					dataAreaTypeDTO.setLabourRate(claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0).getLaborRate().breachEncapsulationOfAmount());
					}
				}
			}
		}
		dataAreaTypeDTO.setLabourCost(labourCost);
		if(dataAreaTypeDTO.getLabourHours()==null){
			dataAreaTypeDTO.setLabourHours(Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()).breachEncapsulationOfAmount());
		}
		if(dataAreaTypeDTO.getLabourRate()==null){
			dataAreaTypeDTO.setLabourRate(Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()).breachEncapsulationOfAmount());
		}
		
		if (claim.getActiveClaimAudit().getAssignToUser() != null
				&& claim.getActiveClaimAudit().getAssignToUser().getName() != null
				&& StringUtils.hasText(claim.getActiveClaimAudit()
						.getAssignToUser().getName())) {
			dataAreaTypeDTO.setActionedBy(claim.getActiveClaimAudit().getAssignToUser().getName().substring(0, Math.min(claim.getActiveClaimAudit().getAssignToUser().getName().length(), 10)));
		}
		if(claim.getPayment()!=null){
			if(claim.getPayment().getActiveCreditMemo()!=null){
				dataAreaTypeDTO.setCreditMemoNumber(claim.getPayment().getActiveCreditMemo().getCreditMemoNumber().substring(0, Math.min(claim.getPayment().getActiveCreditMemo().getCreditMemoNumber().length(), 10)));
				if(claim.getPayment().getActiveCreditMemo().getCreditMemoDate()!=null)
				dataAreaTypeDTO.setCreditMemoDate(CalendarUtil.convertToXMLCalendar(claim.getPayment().getActiveCreditMemo().getCreditMemoDate()));
			}
		}
		String description = claim.getActiveClaimAudit().getConditionFound();
		String[] descriptions = new String[6];;
		if (description != null&&StringUtils.hasText(description)) {
			int length = description.length();
			for (int i = 0,j=0; i < length; i += 70) {
				if(j<6){
				descriptions[j]=(description.substring(i,
						Math.min(length, i + 70)));
				j++;
				}
			}
			if (descriptions[0] != null) {
				dataAreaTypeDTO.setClaimDescription1(descriptions[0]);
			}
			if (descriptions[1] != null) {
				dataAreaTypeDTO.setClaimDescription2(descriptions[1]);
			}
			if (descriptions[2] != null) {
				dataAreaTypeDTO.setClaimDescription3(descriptions[2]);
			}
			if (descriptions[3] != null) {
				dataAreaTypeDTO.setClaimDescription4(descriptions[3]);
			}
			if (descriptions[4] != null) {
				dataAreaTypeDTO.setClaimDescription5(descriptions[4]);
			}
			if (descriptions[5] != null) {
				dataAreaTypeDTO.setClaimDescription6(descriptions[5]);
			}
		}
		if(claim.getFiledBy().getName()!=null&&StringUtils.hasText(claim.getFiledBy().getName())){
		dataAreaTypeDTO.setCreatedUser(claim.getFiledBy().getName().substring(0, Math.min(claim.getFiledBy().getName().length(), 10)));
		}
		if(claim.getLastUpdatedBy().getName()!=null&&StringUtils.hasText(claim.getLastUpdatedBy().getName())){
		dataAreaTypeDTO.setLastUpdatedUser(claim.getLastUpdatedBy().getName().substring(0, Math.min(claim.getLastUpdatedBy().getName().length(), 10)));
		}
		Date lastUpdatedDate=claim.getLastUpdatedOnDate();
		Calendar calDate = Calendar.getInstance();
		calDate.setTime(lastUpdatedDate);
	    int year = calDate.get(Calendar.YEAR);
	    int month = calDate.get(Calendar.MONTH)+1;
	    int day = calDate.get(Calendar.DAY_OF_MONTH);										
		CalendarDate lastUpdateDate = CalendarDate.date(year,month,day);
		dataAreaTypeDTO.setLastUpdatedDate(CalendarUtil.convertToXMLCalendar(lastUpdateDate));
		claim.getRejectionReasons();
		if(claim.getRejectionReasons()!=null && claim.getRejectionReasons().size()>0){
			RejectionReason rejectionReason = claim.getRejectionReasons().get(0);
			if(rejectionReason.getCode()!=null && !rejectionReason.getCode().isEmpty()){
			dataAreaTypeDTO.setRejectedMessageCode(getModifiedResultString(rejectionReason.getCode(),2));
				//Dealership dealer = orgService.findDealerDetailsByNumber(claim.getForDealer().getServiceProviderNumber());
				String locale = claim.getLastUpdatedBy().getLocale().getLanguage().toString().toUpperCase();
				dataAreaTypeDTO.setRejectedMessageLangCode(locale.substring(0, Math.min(locale.length(), 2)));
			}
			String rejectedDescription = rejectionReason.getDescription();
			String[] rejectedDescriptions = new String[2];
			if (rejectedDescription != null) {
				int rejectedMessageLength = rejectedDescription.length();
				for (int i = 0,j=0; j < 2&&i<rejectedDescription.length(); i += 55) {
					if(j<1){
						rejectedDescriptions[j]=(rejectedDescription.substring(i,
							Math.min(rejectedMessageLength, i + 55)));
					}
					if(j==1){
						rejectedDescriptions[j]=(rejectedDescription.substring(i,
								Math.min(rejectedMessageLength, i + 70)));
					}
					j++;
				}
				if (rejectedDescriptions[0] != null) {
					dataAreaTypeDTO.setRejectedDescription1(rejectedDescriptions[0]);
				}
				if (rejectedDescriptions[1] != null) {
					dataAreaTypeDTO.setRejectedDescription2(rejectedDescriptions[1]);
				}
			}
		}
		if(claim.getActiveClaimAudit().getUpdatedTime()!=null && !(new Date(0).equals(claim.getActiveClaimAudit().getUpdatedTime()))){
			dataAreaTypeDTO.setActionedDate(CalendarUtil.convertToXMLCalendar(CalendarUtil.convertToCalendarDate((claim.getActiveClaimAudit().getUpdatedTime()))));
		}else{
			CalendarDate cal = CalendarDate.date(0001,01,01);
			dataAreaTypeDTO.setActionedDate(CalendarUtil.convertToXMLCalendar(cal));
		}
		Set<BusinessUnit> businessUnitSet=claim.getForDealer().getBusinessUnits();
        List<String> businessUnitNameList=new ArrayList<String>();
        for(BusinessUnit bu:businessUnitSet){
            businessUnitNameList.add(bu.getName());
        }
      
        BigDecimal landedCost = (Money.valueOf(0.00, claim.getForDealer()
				.getPreferredCurrency())).breachEncapsulationOfAmount();
        BigDecimal landedCostRate = (Money.valueOf(0.00, claim.getForDealer()
				.getPreferredCurrency())).breachEncapsulationOfAmount();;
        List<CriteriaBasedValue> criteriaBasedValuesList = dealerSummaryService.findCriteriaBasedValues(claim.getForDealer(),businessUnitNameList);
        for(CriteriaBasedValue criteriaBasedValue : criteriaBasedValuesList){
        	if(criteriaBasedValue.getParent().isLandedCost()!=null&&criteriaBasedValue.getParent().isLandedCost()==true){
        		if(Section.OEM_PARTS.equals(criteriaBasedValue.getParent().getForPaymentVariable().getSection().getName())){
        			if(claim.getActiveClaimAudit().getPayment()!=null){
						if (claim.getActiveClaimAudit().getPayment()
								.getLineItemGroup(Section.OEM_PARTS)
								.getModifierMap() != null
								&& !claim.getActiveClaimAudit().getPayment()
										.getLineItemGroup(Section.OEM_PARTS)
										.getModifierMap().isEmpty()
								&& claim.getActiveClaimAudit()
										.getPayment()
										.getLineItemGroup(Section.OEM_PARTS)
										.getModifierMap()
										.get(criteriaBasedValue.getParent()
												.getForPaymentVariable()
												.getName().trim())!=null&&claim.getActiveClaimAudit()
														.getPayment()
														.getLineItemGroup(Section.OEM_PARTS)
														.getModifierMap()
														.get(criteriaBasedValue.getParent()
																.getForPaymentVariable()
																.getName().trim()).getValue() != null) {
							landedCost = claim
									.getActiveClaimAudit()
									.getPayment()
									.getLineItemGroup(Section.OEM_PARTS)
									.getModifierMap()
									.get(criteriaBasedValue.getParent()
											.getForPaymentVariable().getName()
											.trim()).getValue()
									.breachEncapsulationOfAmount();
							if(criteriaBasedValue.getIsFlatRate()){
			        			landedCostRate = new BigDecimal(criteriaBasedValue.getValue().toString());
			        		}else{
			        			landedCostRate = new BigDecimal(criteriaBasedValue.getPercentage().toString());
			        		}
						}
        			}
        		}
        	}
        }
        	dataAreaTypeDTO.setLandedCost(landedCost);
        	if(!landedCost.equals(new BigDecimal(0))){
            dataAreaTypeDTO.setLandedCostRate(landedCostRate);
        	}else{
        		 dataAreaTypeDTO.setLandedCostRate((Money.valueOf(0.00, claim.getForDealer()
        					.getPreferredCurrency())).breachEncapsulationOfAmount());	
        	}
		if (claim.getActiveClaimAudit().getPayment() != null
				&& claim.getActiveClaimAudit().getPayment()
						.getLineItemGroup(Section.OEM_PARTS) != null) {
			dataAreaTypeDTO.setPartsCost(claim
					.getActiveClaimAudit()
					.getPayment()
					.getLineItemGroup(Section.OEM_PARTS)
					.getTotalCreditAmount()
					.minus(Money.valueOf(landedCost, claim.getForDealer()
							.getPreferredCurrency()))
					.breachEncapsulationOfAmount());
		}else{
			dataAreaTypeDTO.setPartsCost(Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()).breachEncapsulationOfAmount());
		}
			dataAreaTypeDTO.setOtherCost(getOtherCostAmount(claim,dataAreaTypeDTO));
			//to do for Rejected claims
		if (ClaimState.REJECTED.equals(claim.getActiveClaimAudit().getState())
				|| ClaimState.DENIED_AND_CLOSED.equals(claim
						.getActiveClaimAudit().getState())) {
			setTheCostsForRejectedClaims(dataAreaTypeDTO, claim,businessUnitNameList);
		}
		logger.error("##################Italy Claim xml has created for number #####################"+claim.getClaimNumber());
		
		return dataAreaTypeDTO;
	}
	private BigDecimal getOtherCostAmount(Claim claim, DataArea dataAreaTypeDTO) {
		if (claim.getActiveClaimAudit() != null
				&& claim.getActiveClaimAudit().getPayment() != null) {
			Currency currency = claim.getForDealer().getPreferredCurrency();
			Money totalClaimAmount=(Money.valueOf(0.00, currency));
			if (claim.getActiveClaimAudit().getPayment()
					.getEffectiveAmountToBePaid() != null
					&& claim.getActiveClaimAudit().getPayment()
							.getEffectiveAmountToBePaid().isZero()
					&& claim.getActiveClaimAudit().getPayment()
							.getClaimedAmount() != null
					&& !claim
							.getActiveClaimAudit()
							.getPayment()
							.getClaimedAmount()
							.equals(Money.valueOf(0.00, claim.getForDealer()
									.getPreferredCurrency()))) {
				totalClaimAmount=claim
						.getActiveClaimAudit()
						.getPayment()
						.getClaimedAmount();
			}else if(claim.getActiveClaimAudit().getPayment()
					.getEffectiveAmountToBePaid() != null){
			totalClaimAmount=claim.getActiveClaimAudit().getPayment().getEffectiveAmountToBePaid();
			}
			Money othercost = totalClaimAmount
					.minus(Money.valueOf(dataAreaTypeDTO.getTravelCost(),
							currency))
					.minus(Money.valueOf(dataAreaTypeDTO.getLabourCost(),
							currency))
					.minus(Money.valueOf(dataAreaTypeDTO.getLandedCost(),
							currency))
					.minus(Money.valueOf(dataAreaTypeDTO.getPartsCost(),
							currency));
			return othercost.breachEncapsulationOfAmount();
		}
		return Money.valueOf(0.00,
				claim.getForDealer().getPreferredCurrency())
				.breachEncapsulationOfAmount();
	}
	private void setTheCostsForRejectedClaims(DataArea dataAreaTypeDTO,
			Claim claim, List<String> businessUnitNameList) {
		setTheLandedCostDetailsForRejectedClaims(dataAreaTypeDTO, claim, businessUnitNameList);
		if (claim.getPaymentForDealerAudit() != null
				&& claim.getPaymentForDealerAudit().getLineItemGroup(
						Section.OEM_PARTS) != null) {
			dataAreaTypeDTO.setPartsCost(claim
					.getPaymentForDealerAudit()
					.getLineItemGroup(Section.OEM_PARTS)
					.getAcceptedTotal()
					.minus(Money.valueOf(dataAreaTypeDTO.getLandedCost(), claim
							.getForDealer().getPreferredCurrency()))
					.breachEncapsulationOfAmount());
		}else{
			dataAreaTypeDTO.setPartsCost(Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()).breachEncapsulationOfAmount());
		}
		setLabourCostForRejectedClaims(dataAreaTypeDTO,claim);
		setTravelCostForRejectedClaims(dataAreaTypeDTO,claim);
		setOtherCostForRejectedClaims(dataAreaTypeDTO,claim);
	}
	private void setOtherCostForRejectedClaims(DataArea dataAreaTypeDTO,
			Claim claim) {
		if (claim.getPaymentForDealerAudit()!=null) {
			Currency currency = claim.getForDealer().getPreferredCurrency();
			Money totalClaimAmount = (Money.valueOf(0.00, currency));
			if (claim.getPaymentForDealerAudit().getEffectiveAmountToBePaid() != null&&claim.getPaymentForDealerAudit().getEffectiveAmountToBePaid()
					.isZero()&& claim.getPaymentForDealerAudit().getClaimedAmount() != null
							&& !claim
							.getActiveClaimAudit()
							.getPayment()
							.getClaimedAmount()
							.equals(Money.valueOf(0.00, claim.getForDealer()
									.getPreferredCurrency()))) {
				totalClaimAmount = claim.getPaymentForDealerAudit()
						.getClaimedAmount();
			} else if(claim.getPaymentForDealerAudit().getEffectiveAmountToBePaid() != null){
				totalClaimAmount = claim.getPaymentForDealerAudit()
						.getEffectiveAmountToBePaid();
			}
			Money othercost = totalClaimAmount
					.minus(Money.valueOf(dataAreaTypeDTO.getTravelCost(),
							currency))
					.minus(Money.valueOf(dataAreaTypeDTO.getLabourCost(),
							currency))
					.minus(Money.valueOf(dataAreaTypeDTO.getLandedCost(),
							currency))
					.minus(Money.valueOf(dataAreaTypeDTO.getPartsCost(),
							currency));
			dataAreaTypeDTO.setOtherCost(othercost
					.breachEncapsulationOfAmount());
		}else{
			dataAreaTypeDTO.setOtherCost(Money.valueOf(0.00,
					claim.getForDealer().getPreferredCurrency())
					.breachEncapsulationOfAmount());
		}

	}
	private void setTravelCostForRejectedClaims(DataArea dataAreaTypeDTO,
			Claim claim) {
		if (claim.getPaymentForDealerAudit() != null) {
			Money travelCost = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			Money travelCost1 = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			Money travelCost2 = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			Money travelCost3 = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			if (claim.getPaymentForDealerAudit().getLineItemGroup(
					Section.TRAVEL_BY_DISTANCE) != null) {
				travelCost1 = claim.getPaymentForDealerAudit()
						.getLineItemGroup(Section.TRAVEL_BY_DISTANCE)
						.getTotalCreditAmount();
			}
			if (claim.getPaymentForDealerAudit().getLineItemGroup(
					Section.TRAVEL_BY_TRIP) != null) {
				travelCost2 = claim.getPaymentForDealerAudit()
						.getLineItemGroup(Section.TRAVEL_BY_TRIP)
						.getTotalCreditAmount();
			}
			if (claim.getPaymentForDealerAudit().getLineItemGroup(
					Section.TRAVEL_BY_HOURS) != null) {
				travelCost3 = claim.getPaymentForDealerAudit()
						.getLineItemGroup(Section.TRAVEL_BY_HOURS)
						.getTotalCreditAmount();
			}
			travelCost = travelCost1.plus(travelCost2).plus(travelCost3);
			dataAreaTypeDTO.setTravelCost(travelCost
					.breachEncapsulationOfAmount());
		}

	}
	private void setLabourCostForRejectedClaims(DataArea dataAreaTypeDTO,
			Claim claim) {
			if (claim.getPaymentForDealerAudit() != null&&claim.getPaymentForDealerAudit()
					.getLineItemGroup(Section.LABOR) != null&&claim.getPaymentForDealerAudit()
							.getLineItemGroup(Section.LABOR).getTotalCreditAmount()!=null) {
				dataAreaTypeDTO.setLabourCost(claim.getPaymentForDealerAudit()
						.getLineItemGroup(Section.LABOR).getTotalCreditAmount()
						.breachEncapsulationOfAmount());
				if (claim.getServiceInformation() != null
						&& claim.getServiceInformation().getServiceDetail() != null
						&& !claim.getServiceInformation().getServiceDetail()
								.getLaborPerformed().isEmpty()) {
					dataAreaTypeDTO.setLabourHours(claim
							.getServiceInformation().getServiceDetail()
							.getLaborPerformed().get(0).getTotalHours());
					dataAreaTypeDTO.setLabourRate(claim.getServiceInformation()
							.getServiceDetail().getLaborPerformed().get(0)
							.getLaborRate().breachEncapsulationOfAmount());
				}
			}else{
				dataAreaTypeDTO.setLabourCost(Money.valueOf(0.00, claim.getForDealer()
						.getPreferredCurrency()).breachEncapsulationOfAmount());
				if(dataAreaTypeDTO.getLabourHours()==null){
					dataAreaTypeDTO.setLabourHours(Money.valueOf(0.00, claim.getForDealer()
							.getPreferredCurrency()).breachEncapsulationOfAmount());
				}
				if(dataAreaTypeDTO.getLabourRate()==null){
					dataAreaTypeDTO.setLabourRate(Money.valueOf(0.00, claim.getForDealer()
							.getPreferredCurrency()).breachEncapsulationOfAmount());
				}
			}
	}
	private void setTheLandedCostDetailsForRejectedClaims(DataArea dataAreaTypeDTO, Claim claim,List<String> businessUnitNameList) {
		BigDecimal landedCost = (Money.valueOf(0.00, claim.getForDealer()
				.getPreferredCurrency())).breachEncapsulationOfAmount();
        BigDecimal landedCostRate = (Money.valueOf(0.00, claim.getForDealer()
				.getPreferredCurrency())).breachEncapsulationOfAmount();
        List<CriteriaBasedValue> criteriaBasedValuesList = dealerSummaryService.findCriteriaBasedValues(claim.getForDealer(),businessUnitNameList);
        for(CriteriaBasedValue criteriaBasedValue : criteriaBasedValuesList){
        	if(criteriaBasedValue.getParent().isLandedCost()!=null&&criteriaBasedValue.getParent().isLandedCost()==true){
        		if(Section.OEM_PARTS.equals(criteriaBasedValue.getParent().getForPaymentVariable().getSection().getName())){
        			if(claim.getPaymentForDealerAudit()!=null&&claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS)!=null){
						if (claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS).getModifierMap()!= null
								&& !claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS)
										.getModifierMap().isEmpty()
								&& claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS)
										.getModifierMap()
										.get(criteriaBasedValue.getParent()
												.getForPaymentVariable()
												.getName().trim())!=null&&claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS)
														.getModifierMap()
														.get(criteriaBasedValue.getParent()
																.getForPaymentVariable()
																.getName().trim()).getValue() != null) {
							landedCost = claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS)
									.getModifierMap()
									.get(criteriaBasedValue.getParent()
											.getForPaymentVariable().getName()
											.trim()).getValue()
									.breachEncapsulationOfAmount();
							if(criteriaBasedValue.getIsFlatRate()){
			        			landedCostRate = new BigDecimal(criteriaBasedValue.getValue().toString());
			        		}else{
			        			landedCostRate = new BigDecimal(criteriaBasedValue.getPercentage().toString());
			        		}
						}
		        		
        			}
        		}
        	}
        }
        dataAreaTypeDTO.setLandedCost(landedCost);
        if(!landedCost.equals(new BigDecimal(0))){
        dataAreaTypeDTO.setLandedCostRate(landedCostRate);
        }else{
        	dataAreaTypeDTO.setLandedCostRate((Money.valueOf(0.00, claim.getForDealer()
    				.getPreferredCurrency())).breachEncapsulationOfAmount());
        }
	}
	private void setTravelCost(Claim claim,DataArea dataAreaTypeDTO){
		if (claim.getActiveClaimAudit().getPayment() != null) {
			Money travelCost = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			Money travelCost1 = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			Money travelCost2 = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			Money travelCost3 = (Money.valueOf(0.00, claim.getForDealer()
					.getPreferredCurrency()));
			if (claim.getActiveClaimAudit().getPayment()
					.getLineItemGroup(Section.TRAVEL_BY_DISTANCE) != null) {
				travelCost1 = claim.getActiveClaimAudit().getPayment()
						.getLineItemGroup(Section.TRAVEL_BY_DISTANCE)
						.getTotalCreditAmount();
			}
			if (claim.getActiveClaimAudit().getPayment()
					.getLineItemGroup(Section.TRAVEL_BY_TRIP) != null) {
				travelCost2 = claim.getActiveClaimAudit().getPayment()
						.getLineItemGroup(Section.TRAVEL_BY_TRIP)
						.getTotalCreditAmount();
			}
			if (claim.getActiveClaimAudit().getPayment()
					.getLineItemGroup(Section.TRAVEL_BY_HOURS) != null) {
				travelCost3 = claim.getActiveClaimAudit().getPayment()
						.getLineItemGroup(Section.TRAVEL_BY_HOURS)
						.getTotalCreditAmount();
			}
				travelCost = travelCost1.plus(travelCost2).plus(travelCost3);
				dataAreaTypeDTO.setTravelCost(travelCost
						.breachEncapsulationOfAmount());
		}
			
	}
	private String getModifiedResultString(String code,int requiredMaxLenght) {
		if (code != null) {
			String modifiedString = code.length() <= requiredMaxLenght
					? code
					: code
							.substring(code.length() - requiredMaxLenght);
			return modifiedString;
		}
		return null;
	}
	public IntegrationPropertiesBean getIntegrationPropertiesBean() {
		return integrationPropertiesBean;
	}
	public void setIntegrationPropertiesBean(
			IntegrationPropertiesBean integrationPropertiesBean) {
		this.integrationPropertiesBean = integrationPropertiesBean;
	}
	public OrgService getOrgService() {
		return orgService;
	}
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	public DealerSummaryService getDealerSummaryService() {
		return dealerSummaryService;
	}
	public void setDealerSummaryService(DealerSummaryService dealerSummaryService) {
		this.dealerSummaryService = dealerSummaryService;
	}
	public SyncTrackerDAO getSyncTrackerDAO() {
		return syncTrackerDAO;
	}
	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}
	public ItemMappingService getItemMappingService() {
		return itemMappingService;
	}
	public void setItemMappingService(ItemMappingService itemMappingService) {
		this.itemMappingService = itemMappingService;
	}
	public SupplierService getSupplierService() {
		return supplierService;
	}
	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}
	
	
	

}
