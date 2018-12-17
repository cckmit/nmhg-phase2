package tavant.twms.domain.claim.claimsubmission;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.claim.payment.*;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AccountabilityCode;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.complaints.CountryState;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.inventory.*;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionRepository;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyProductMapping;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.stateMandates.StateMandates;
import tavant.twms.domain.stateMandates.StateMandatesService;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClaimSubmissionUtil {
    private final Logger logger = Logger.getLogger(ClaimSubmissionUtil.class.getName());

    private ConfigParamService configParamService;

    private XStream xstream;
    
	private InventoryService inventoryService;

    private OrgService orgService;

    private ClaimService claimService;
   
    private ContractService contractService;

    private PaymentService paymentService;

    private PartReturnService partReturnService;

    private AttributeAssociationService attributeAssociationService;
    
    private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;
    
    private PolicyDefinitionRepository policyDefinitionRepository;
    
    private CountryStateService countryStateService;
    
    private StateMandatesService stateMandatesService;

    public static final String REFRESH_PAYMENT = "refresh_payment";

    public static final String REFRESH_ACTIONS = "refresh_actions";
    
	public final static String TRUE = "true";
	
	public final static String FALSE = "false";

    private SecurityHelper securityHelper;
    
    private PolicyService policyService;

    private ReplacedInstalledPartsService replacedInstalledPartsService;

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public User getLoggedInUser() {
        return securityHelper.getLoggedInUser();
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }
    
    private boolean doesClaimContainRejectedPart(Claim claim) {
        return this.partReturnService.doesClaimHaveRejectedParts(claim);
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setReplacedInstalledPartsService(
            ReplacedInstalledPartsService replacedInstalledPartsService) {
        this.replacedInstalledPartsService = replacedInstalledPartsService;
    }
    
    public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setAttributeAssociationService(AttributeAssociationService attributeAssociationService) {
        this.attributeAssociationService = attributeAssociationService;
    }

    public boolean isServiceManagerReview(Claim claim) {
        return ClaimState.SERVICE_MANAGER_REVIEW.equals(claim.getState());
    }

    public boolean isServiceManagerResponse(Claim claim) {
        return ClaimState.SERVICE_MANAGER_RESPONSE.equals(claim.getState());
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public ClaimCurrencyConversionAdvice getClaimCurrencyConversionAdvice() {
		return claimCurrencyConversionAdvice;
	}

	public void setClaimCurrencyConversionAdvice(
			ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}

	public PolicyDefinitionRepository getPolicyDefinitionRepository() {
		return policyDefinitionRepository;
	}

	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}

	public CountryStateService getCountryStateService() {
		return countryStateService;
	}

	public void setCountryStateService(CountryStateService countryStateService) {
		this.countryStateService = countryStateService;
	}

	public StateMandatesService getStateMandatesService() {
		return stateMandatesService;
	}

	public void setStateMandatesService(StateMandatesService stateMandatesService) {
		this.stateMandatesService = stateMandatesService;
	}

	public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public boolean isRepliesOrForwarded(Claim claim)// fix for bug CCI-839
    {
        return ClaimState.REPLIES.equals(claim.getState())
                || ClaimState.FORWARDED.equals(claim.getState());
    }

    public boolean isForwarded(Claim claim) {
        return ClaimState.FORWARDED.equals(claim.getState());
    }

    public boolean isAdviceRequest(Claim claim) {
        return ClaimState.ADVICE_REQUEST.equals(claim.getState());
    }

    public boolean isMatchReadApplicable() {
        return this.configParamService
                .getBooleanValue(ConfigName.MATCH_READ_APPLICABLITY.getName());
    }

    public boolean isInvoiceNumberApplicable() {
        return configParamService.getBooleanValue(ConfigName.INVOICE_NUMBER_APPLICABLE.getName());
    }

    public boolean isDateCodeEnabled() {
        return this.configParamService.getBooleanValue(ConfigName.IS_DATE_CODE_ENABLED.getName());
    }

    public boolean isItemNumberDisplayRequired() {
        return configParamService.getBooleanValue(ConfigName.IS_ITEM_NUMBER_DISPLAY_REQUIRED
                .getName());
    }

    public Object convertXMLToObject(String xml) {
        Object obj;
        try {
        	xstream = new XStream(new DomDriver());
            obj = xstream.fromXML(xml);
        } catch (RuntimeException e) {
            logger.error("Got an exception while converting xml to object", e);
            throw e;
        }
        return obj;
    }

   	public InventoryItem getInventoryItemForNonSerializedSerialNumber(String serialNumber,
            String model) {
        InventoryItem inventoryItem = null;
        try {
            inventoryItem = inventoryService.findSerializedItem(serialNumber, model);
        } catch (ItemNotFoundException exception) {
            this.logger.error("No inventory item exists for the given serial number ["
                    + serialNumber + "] and" + " model [" + model + "]");
        }
        return inventoryItem;
    }
    
     public void computePayment(Claim theClaim, String taskTransistion) {
    	 try {
    		// NMHGSLMS-425 changes
    			Money deductable=getDeductableAmount(theClaim);	
    			theClaim.setStateMandate(getStateMandate(theClaim));
    			if(theClaim.isGoodWillPolicy())
    			{
    				theClaim.getPayment().setTotalAcceptStateMdtChkbox(true);
    				theClaim.getPayment().setTotalAcceptStateMdtChkbox(false);
    			}
    			setLateFeeForClaim(theClaim);
    			// End
    		 
    		 Payment payment = null;
    		 //SLMSPROD-381( make the amount zero if claim is warranty order claim, else do normal calculation)
    		 if ((theClaim.getWarrantyOrder()&&!theClaim.getState().equals(ClaimState.DRAFT))) {
    			 payment = this.paymentService.calculatePaymentForWarrantyOrderClaim(theClaim);			
    		 } 
    		 else
    		 {
    			 if ("Deny".equalsIgnoreCase(taskTransistion)) {
    				 payment = this.paymentService
    						 .calculatePaymentForDeniedClaim(theClaim);
    				 theClaim.getActiveClaimAudit().setIsPriceFetchDown(false);
    				 theClaim.getActiveClaimAudit().setIsPriceFetchReturnZero(false);
    				 theClaim.getActiveClaimAudit().setPriceFetchErrorMessage(null);
    			 } else {				
    				 payment = this.paymentService
    						 .calculatePaymentForClaim(theClaim,deductable);					
    			 }
    		 }

    		 if(theClaim.getStateMandate()!=null)
    		 {
    			 payment.setStateMandateActive(true);
    		 }
    		 else
    		 {
    			 payment.setStateMandateActive(false);
    		 }
    		           
    		 theClaim.setPayment(payment);
    	 } catch (PaymentCalculationException e) {
    		 throw new RuntimeException("Error occured while performing payment calculation.", e);
    	 }
    }

    public Map<String, String[]> validate(Claim claim, Campaign campaign) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        String errorCode = validateClaimForDuplicateJobCodes(claim);
        if (StringUtils.hasText(errorCode))
            errorCodeMap.put(errorCode, null);

        if (claim.getFailureDate() != null && claim.getPurchaseDate() != null
                && claim.getFailureDate().isBefore(claim.getPurchaseDate())) {
            errorCodeMap.put("error.newClaim.invalidPurchaseDuration", null);
        }
        if(checkDuplicateAlarmCodes(claim)) {
        	errorCodeMap.put("alarmcode.duplicate", null);
		}
        return errorCodeMap;

    }
    
    private boolean checkDuplicateAlarmCodes(Claim claim) {
		boolean flag = false;
		Set<Long> alarmCodeSet = new HashSet<Long>();
		for(AlarmCode alarmCode : claim.getAlarmCodes()) {
			if(!alarmCodeSet.add(alarmCode.getId())) {
				flag = true;
				break;
			}
		}
		return flag;
	}

    public String validateClaimForDuplicateJobCodes(Claim claim) {
        String errorCode = null;
        Set<String> s = new HashSet<String>();
        ServiceInformation serviceInformation = claim.getServiceInformation();
        boolean duplicateJobCodes = false;
        if (serviceInformation != null) {
            List<LaborDetail> laborDetails = serviceInformation.getServiceDetail()
                    .getLaborPerformed();

            if (laborDetails != null) {
                for (int i = 0; i < laborDetails.size(); i++) {
                    ServiceProcedure serviceProcedure = laborDetails.get(i).getServiceProcedure();
                    if (serviceProcedure != null && serviceProcedure.getDefinedFor() != null
                            && !s.add(serviceProcedure.getDefinition().getCode())) {
                        duplicateJobCodes = true;
                        break;

                    }
                }
            }
        }

        if (duplicateJobCodes) {
            errorCode = "error.claim.serviceProcedure.duplicateJobCode";
        }
        return errorCode;
    }

    /**
     * @param claim
     * @return
     */
    public Map<String, String[]> validateClaimForLaborDetail(Claim claim) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        boolean isAdditionalHrsErrorAdded = false;
        boolean isLaborHrsErrorAdded = false;
        boolean isAdditionalHrsReasonErrorAdded = false;
        boolean isLaborHoursErrorAdded = false;
        boolean isPartsClaimWithOutHost = false;
        if (ClaimType.PARTS.getType().equals(claim.getType().getType())) {
            PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
            isPartsClaimWithOutHost = !partsClaim.getPartInstalled();
        }
        boolean isStdLaborEnabled = this.configParamService
                .getBooleanValue(ConfigName.ENABLE_STANDARD_LABOR_HOURS.getName());
        if (claim.getServiceInformation().getServiceDetail().getLaborPerformed() != null) {
            for (LaborDetail labor : claim.getServiceInformation().getServiceDetail()
                    .getLaborPerformed()) {
                if (!isStdLaborEnabled
                        && !isPartsClaimWithOutHost
                        && !isLaborHoursErrorAdded
                        && (labor.getLaborHrsEntered() == null
                                || labor.getLaborHrsEntered().doubleValue() == 0 || labor
                                .getLaborHrsEntered().doubleValue() < 0)) {
                    errorCodeMap.put("error.claim.laborHours", null);
                    isLaborHoursErrorAdded = true;
                }
                if (labor.getHoursSpent().doubleValue() < 0 && !isLaborHrsErrorAdded) {
                    errorCodeMap.put("error.newClaim.negativeLaborHours", null);
                    isLaborHrsErrorAdded = true;
                }
                if (labor.getEmptyAdditionalHours() != null && labor.getEmptyAdditionalHours()) {
                    labor.setAdditionalLaborHours(null);
                }
                if (labor.getAdditionalLaborHours() != null
                        && labor.getAdditionalLaborHours().doubleValue() < 0
                        && !isAdditionalHrsErrorAdded) {
                    errorCodeMap.put("error.newClaim.negativeAdditionHours", null);
                    isAdditionalHrsErrorAdded = true;
                }
                if ((labor.getAdditionalLaborHours() != null && labor.getAdditionalLaborHours().compareTo(BigDecimal.ZERO)==1)
                        && !StringUtils.hasText(labor.getReasonForAdditionalHours())
                        && !isAdditionalHrsReasonErrorAdded) {
                    errorCodeMap.put("error.newClaim.reasonForAdditionHours", null);
                    isAdditionalHrsReasonErrorAdded = true;
                }

            }
        }
        return errorCodeMap;
    }

    public List<String> removeInvalidJobCodes(ServiceInformation si, FailureStructure fs) {
        List<String> invalidJobCodes = new ArrayList<String>();
        for (Iterator<LaborDetail> it = si.getServiceDetail().getLaborPerformed().iterator(); it.hasNext();) {
            LaborDetail ld = it.next();
          //FIX ME @excludeinactive filter is not working for ServiceProcedure , Explicitly added condition
            if (fs != null && ld.getServiceProcedure()!= null && ld.getServiceProcedure().getD().isActive() && fs.findServiceProceduresForDefn(ld.getServiceProcedure().getDefinition()) == null) {
                invalidJobCodes.add(ld.getServiceProcedure().getDefinition().getCode());
                it.remove();
            }
        }
        return invalidJobCodes;
    }

    public List<String> removeInactiveJobcodes(ServiceInformation si) {
        List<String> inactiveJobCodes = new ArrayList<String>();
        for (Iterator<LaborDetail> it = si.getServiceDetail().getLaborPerformed().iterator(); it.hasNext();) {
            LaborDetail ld = it.next();
            ServiceProcedure sp = ld.getServiceProcedure();
            boolean isActive = true;
			if (sp != null && sp.getD().isActive()) {	//FIX ME @excludeinactive filter is not working for ServiceProcedure			
				if (sp.getDefinedFor() != null) {  
					if (!sp.getDefinedFor().getActive() || !sp.getDefinedFor().getDefinedFor().getActive()) { 
						isActive = false;
					}
                Set<Assembly> assemblies = sp.getDefinedFor().getDefinedFor().getComposedOfAssemblies();
                for (Assembly asm : assemblies) {
                    if (!asm.getActive() && asm.getFullCode().equals(sp.getDefinition().getCode())) {
                        isActive = false;
                        break;
                    }
                }
            }
			}
            if (sp !=null&&(!isActive||!sp.getD().isActive())) {
                inactiveJobCodes.add(ld.getServiceProcedure().getDefinition().getCode());
                it.remove();
            }		
       }
        return inactiveJobCodes;
    }

    public Map<String, String[]> validateReplacedInstalledParts(Claim claim,
            boolean isProcessorReview, boolean hasActionErrors) {
        Map<String, String[]> errorCodesMap = new HashMap<String, String[]>();
        List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = claim
                .getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
        List<String> itemNumbers = new ArrayList<String>();
        List<String> listOfPartNumbers = new ArrayList<String>();
        List<String>  listOfInstalledPartNumbers=new ArrayList<String>();
        StringBuffer listOfDiplcatePartNumbers = new StringBuffer("");
        StringBuffer partsWithDueDaysRequired = new StringBuffer("");
        StringBuffer partsWithReturnLocationRequired = new StringBuffer("");
		boolean dueDaysRequiredError=false;
		boolean returnLocationRequiredError=false;
        if (hussmanPartsReplacedInstalled != null) {
            for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled : hussmanPartsReplacedInstalled) {
                if (hussmanPartReplacedInstalled == null) {
                    continue;
                }
                List<OEMPartReplaced> replacedParts = hussmanPartReplacedInstalled
                        .getReplacedParts();
                if (replacedParts != null) {
                    for (OEMPartReplaced replacedPart : replacedParts) {
                        if (replacedPart != null) {
                            if (replacedPart.getItemReference() == null
                                    || replacedPart.getItemReference().getReferredItem() == null
                                    || replacedPart.getItemReference().getReferredItem()
                                            .getNumber() == null) {
                                errorCodesMap.put("error.claim.replacedPartNumberNull",
                                		null);
                            } else if (!listOfPartNumbers.contains(replacedPart.getItemReference()
                                    .getReferredItem().getNumber())) {
                                listOfPartNumbers.add(replacedPart.getItemReference()
                                        .getReferredItem().getNumber());
                            }
                            
                            if(replacedPart.getNumberOfUnits() == null || (replacedPart.getNumberOfUnits() != null && replacedPart.getNumberOfUnits() == 0)){
                            	errorCodesMap.put(
										"error.claim.replacedQuantityNull",
										null);
                            }
								
							if (replacedPart.getNumberOfUnits() != null 
									&& replacedPart.getNumberOfUnits().intValue() < 0) {
									errorCodesMap
											.put(
													"error.claim.replacedQuantityInvalid",
													null);
								}
							}
                            if (isProcessorReview && replacedPart.isPartToBeReturned() && !replacedPart.isPartShippedOrCannotBeShipped()) {
                                if (replacedPart.getPartReturn() == null
                                        || replacedPart.getPartReturn().getReturnLocation() == null
                                        || replacedPart.getPartReturn().getReturnLocation()
                                                .getCode() == null) {
                                	if(partsWithReturnLocationRequired.length()==0)
                                		partsWithReturnLocationRequired=new StringBuffer(replacedPart.getItemReference()
            									.getReferredItem().getNumber());
                                	else{
                                		partsWithReturnLocationRequired.append(",");
                                		partsWithReturnLocationRequired.append(replacedPart.getItemReference()
            									.getReferredItem().getNumber());
                                	}
            						returnLocationRequiredError=true;
                                 }

                                try {
                                    if (replacedPart.getPartReturn() == null
                                            || replacedPart.getPartReturn().getPaymentCondition() == null
                                            && replacedPart.getPartReturn().getPaymentCondition()
                                                    .getCode() != null) {
                                        errorCodesMap
                                                .put(
                                                        "error.partReturnConfiguration.paymentConditionRequired",
                                                        new String[] { replacedPart
                                                                .getItemReference()
                                                                .getReferredItem().getNumber() });
                                    }
                                } catch (Exception e) {
                                    errorCodesMap
                                            .put(
                                                    "error.partReturnConfiguration.paymentConditionRequired",
                                                    new String[] { replacedPart.getItemReference()
                                                            .getReferredItem().getNumber() });
                                }
                                if (replacedPart.getPartReturn() == null
                                        || replacedPart.getPartReturn().getPaymentCondition() == null
                                        || replacedPart.getPartReturn().getPaymentCondition()
                                                .getDescription() == null) {
                                    errorCodesMap
                                            .put(
                                                    "error.partReturnConfiguration.paymentConditionRequired",
                                                    new String[] { replacedPart.getItemReference()
                                                            .getReferredItem().getNumber() });
                                }
                                if (replacedPart.getPartReturn() == null
                                        || (!replacedPart.getPartReturn()
                								.getStatus().isShipmentGenerated() && replacedPart.getPartReturn().getDueDays() <= 0) ) {
                                	if(partsWithDueDaysRequired.length()==0)
                                		partsWithDueDaysRequired=new StringBuffer(replacedPart.getItemReference()
            									.getReferredItem().getNumber());
                                	else{
                                		partsWithDueDaysRequired.append(",");
                                		partsWithDueDaysRequired.append(replacedPart.getItemReference()
            									.getReferredItem().getNumber());
                                	}
            						dueDaysRequiredError=true;
                                }
                            }
                            if(returnLocationRequiredError)
                            	 errorCodesMap.put(
                                         "error.partReturnConfiguration.returnLocationRequired",
                                         new String[] { partsWithReturnLocationRequired.toString() });
                            
                            if(dueDaysRequiredError)
                                errorCodesMap.put("error.partReturnConfiguration.dueDays",
                                       new String[] {partsWithDueDaysRequired.toString()});
                                }
                        } 
                    /*if ((hussmanPartReplacedInstalled.getHussmanInstalledParts() == null || hussmanPartReplacedInstalled
                            .getHussmanInstalledParts().isEmpty())
                            && (hussmanPartReplacedInstalled.getNonHussmanInstalledParts() == null || hussmanPartReplacedInstalled
                                    .getNonHussmanInstalledParts().isEmpty())) {
                        errorCodesMap.put("error.claim.selectAtleastOneOfInstallParts",
                        		null);
                    }*/
                }
            }
            for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled : hussmanPartsReplacedInstalled) {
                if (hussmanPartReplacedInstalled == null) {
                    continue;
                }
                List<InstalledParts> hussmanInstalledParts = hussmanPartReplacedInstalled
                        .getHussmanInstalledParts();
                if (hussmanInstalledParts != null && !hussmanInstalledParts.isEmpty()) {
                    for (InstalledParts hussmanInstalledPart : hussmanInstalledParts) {
                        if (hussmanInstalledPart != null) {
                            if (hussmanInstalledPart.getItem() == null
                                    || hussmanInstalledPart.getItem().getNumber() == null) {
                                errorCodesMap.put("error.claim.installedHussmannPartNumberNull",
                                		null);
                            } 
                            else if (!listOfPartNumbers.contains(hussmanInstalledPart.getItem()
                                    .getNumber())) {
                                listOfPartNumbers.add(hussmanInstalledPart.getItem().getNumber());
                            }
                            
                            //Find duplicate installed parts
                            if (hussmanInstalledPart.getItem() != null
                            		&& hussmanInstalledPart.getItem().getNumber() != null) 
                            {
                            	if (listOfInstalledPartNumbers.contains(hussmanInstalledPart.getItem() .getNumber()))
                            	{
                            		if(listOfDiplcatePartNumbers.length()==0)
                            			listOfDiplcatePartNumbers.append(hussmanInstalledPart.getItem()
                            					.getNumber());
                            		else
                            		{
                            			listOfDiplcatePartNumbers.append(",");
                            			listOfDiplcatePartNumbers.append(hussmanInstalledPart.getItem()
                            					.getNumber());

                            		}
                            	}
                            	else
                            	{                            		
                            			listOfInstalledPartNumbers.add(hussmanInstalledPart.getItem().getNumber());                            		
                            	}
                            }

                            if (hussmanInstalledPart.getNumberOfUnits() == null) {
                                errorCodesMap.put("error.claim.hussmannInstalledQuantityNull",
                                		null);
                            } else if (hussmanInstalledPart.getNumberOfUnits().intValue() <= 0) {
                                errorCodesMap.put("error.claim.hussmannInstalledQuantityInvalid",
                                		null);
                            }
                        }
                    }
                    if(listOfDiplcatePartNumbers.length()>0)
                    {
                    	errorCodesMap.put(
                                "error.claim.installedHussmannPartNumberDuplicate",
                               new String [] {listOfDiplcatePartNumbers.toString()});	
                    }
                }
            }       
            
            
            for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled : hussmanPartsReplacedInstalled) {
                List<InstalledParts> nonHussmanInstalledParts = hussmanPartReplacedInstalled
                        .getNonHussmanInstalledParts();
                if (nonHussmanInstalledParts != null && !nonHussmanInstalledParts.isEmpty()) {
                    for (InstalledParts nonHussmanInstalledPart : nonHussmanInstalledParts) {
                        if (nonHussmanInstalledPart != null) {
                            if (nonHussmanInstalledPart.getPartNumber() == null
                                    || nonHussmanInstalledPart.getPartNumber().equals("")) {
                                errorCodesMap.put("error.claim.installedNonHussmannPartNumberNull",
                                		null);
                            }

                            if (nonHussmanInstalledPart.getNumberOfUnits() == null) {
                                errorCodesMap.put("error.claim.nonHussmannInstalledQuantityNull",
                                		null);
                            } else if (nonHussmanInstalledPart.getNumberOfUnits().intValue() <= 0) {
                                errorCodesMap.put(
                                        "error.claim.nonHussmannInstalledQuantityInvalid",
                                        null);
                            }

                            if (nonHussmanInstalledPart.getPricePerUnit() == null
                                    || nonHussmanInstalledPart.getPricePerUnit()
                                            .breachEncapsulationOfAmount() == null) {
                                errorCodesMap.put("error.claim.priceNull", null);
                            }else  if(nonHussmanInstalledPart.getPricePerUnit().breachEncapsulationOfAmount() != null
                            		&& nonHussmanInstalledPart.getPricePerUnit().breachEncapsulationOfAmount().doubleValue() <= 0) {
                            	errorCodesMap.put("error.claim.nonHussmannInstalledPriceNegativeOrZero", new String[]{nonHussmanInstalledPart.getPartNumber()});
                            }
                        }
                    }
                }
            }
            if (!hasActionErrors && listOfPartNumbers != null && !listOfPartNumbers.isEmpty()) {
                itemNumbers = replacedInstalledPartsService.findItemNumbers(listOfPartNumbers);
                if (itemNumbers.size() < listOfPartNumbers.size()) {
                    for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled : hussmanPartsReplacedInstalled) {
                        List<OEMPartReplaced> replacedParts = hussmanPartReplacedInstalled
                                .getReplacedParts();
                        if (replacedParts != null && !replacedParts.isEmpty()) {
                            for (OEMPartReplaced replacedPart : replacedParts) {
                                if (replacedPart == null) {
                                    continue;
                                }
                                if (!itemNumbers.contains(replacedPart.getItemReference()
                                        .getReferredItem().getNumber())) {
                                    errorCodesMap.put("error.claim.replacedPartNumberInvalid",
                                            new String[] { replacedPart.getItemReference()
                                                    .getReferredItem().getNumber() });
                                    break;
                                }
                            }
                        }

                        List<InstalledParts> hussmanInstalledParts = hussmanPartReplacedInstalled
                                .getHussmanInstalledParts();
                        if (hussmanInstalledParts != null && !hussmanInstalledParts.isEmpty()) {
                            for (InstalledParts hussmanInstalledPart : hussmanInstalledParts) {
                                if (hussmanInstalledPart == null) {
                                    continue;
                                }
                                if (hussmanInstalledPart.getItem() != null
                                        && hussmanInstalledPart.getItem().getNumber() != null
                                        && !itemNumbers.contains(hussmanInstalledPart.getItem()
                                                .getNumber())) {
                                    errorCodesMap.put("error.claim.installedPartNumberInvalid",
                                            new String[] { hussmanInstalledPart.getItem()
                                                    .getNumber() });
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        return errorCodesMap;
    }
    
  //Miscellaneous Expense & Outside Services validation 
    
    public Map<String, String[]> validateMiscelleanousParts(Claim claim) {
    	Map<String, String[]> errorCodesMap = new HashMap<String, String[]>();
    	StringBuffer listOfDiplcateMiscParts = new StringBuffer("");
    	List<String> listOfMiscParts = new ArrayList<String>();
    	List<NonOEMPartReplaced> nonOemParts=claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced();
    	for(NonOEMPartReplaced miscPart:nonOemParts)
    	{
    		//Duplicate parts validation
    		if (miscPart != null&&miscPart.getDescription()!=null) {
    			if (listOfMiscParts.contains(miscPart.getDescription()))
    			{
    				if(listOfDiplcateMiscParts.length()==0)
    					listOfDiplcateMiscParts.append(miscPart.getDescription());
    				else
    				{
    					listOfDiplcateMiscParts.append(",");
    					listOfDiplcateMiscParts.append(miscPart.getDescription());
    				}
    			}else{
    				listOfMiscParts.add(miscPart.getDescription());
    			}
    		}
    	}
    	
    	if(listOfDiplcateMiscParts.length()>0)
        {
        	errorCodesMap.put(
                    "error.claim.miscPartDuplicate",
                   new String [] {listOfDiplcateMiscParts.toString()});	
        }
    	return errorCodesMap;
    	
    }
    public void checkIfInvExistsForNonSerializedSerialNumber(Claim claim) {
        try {
            if (claim.getItemReference().getModel() != null
                    && claim.getItemReference().getUnszdSlNo() != null
                    && StringUtils.hasText(claim.getItemReference().getUnszdSlNo())) {
                InventoryItem inventoryItem = this.inventoryService.findSerializedItem(claim
                        .getItemReference().getUnszdSlNo(), claim.getItemReference().getModel()
                        .getName());
                if (inventoryItem != null) {
                    ItemReference itemReference = claim.getItemReference();
                    itemReference.setReferredInventoryItem(inventoryItem);
                }
            }
        } catch (ItemNotFoundException exception) {

        }
    }

    public Map<String, Map<String, String[]>> validateForAccountabilityCode(Claim theClaim,
            boolean isProcessorReview, String taskTransition) {
        Map<String, Map<String, String[]>> messageMap = new HashMap<String, Map<String, String[]>>();
        Map<String, String[]> warningCodesMap = new HashMap<String, String[]>();
        Map<String, String[]> errorCodesMap = new HashMap<String, String[]>();
        if (isProcessorReview && taskTransition.equalsIgnoreCase("Accept")) {
            AccountabilityCode accountabilityCode = theClaim.getAccountabilityCode();
            Boolean supplierPartRecoverable = theClaim.getServiceInformation()
                    .isSupplierPartRecoverable();
            if (accountabilityCode == null || accountabilityCode.getCode().equalsIgnoreCase("null")
                    || accountabilityCode.getCode() == null
                    || accountabilityCode.getCode().length() == 0) {
                errorCodesMap.put("message.claim.selectAccountabilityCode", null);
            } else {
                if (accountabilityCode.getCode().equalsIgnoreCase("SUP")
                        && (supplierPartRecoverable == null || !supplierPartRecoverable
                                .booleanValue())) {
                    errorCodesMap.put("error.claim.supplierAccountability", null);
                }
            }
            if (supplierPartRecoverable != null && supplierPartRecoverable.booleanValue()) {
                if (theClaim.getServiceInformation().getContract() == null) {
                    errorCodesMap.put("error.claim.causalPartRecovery", null);

                } else {
                    if (!ClaimType.CAMPAIGN.getType()
                            .equalsIgnoreCase(theClaim.getType().getType())) {
                        boolean validContract = false;
                        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(theClaim
                                .getBusinessUnitInfo().getName());
                        List<Contract> applicableContracts = this.contractService.findContract(
                                theClaim, theClaim.getServiceInformation().getCausalPart(), true);

                        for (Contract contract : applicableContracts) {
                            if (contract.getName().equals(
                                    theClaim.getServiceInformation().getContract().getName())) {
                                validContract = true;
                                break;
                            }
                        }
                        if (!validContract) {
                            errorCodesMap
                                    .put(
                                            "The causal part does not have any applicable contract. Please ensure the causal part is covered under a valid contract before marking for recovery",
                                            null);
                        }
                    }
                }
            }

            if (doesClaimContainRejectedPart(theClaim)) {
                warningCodesMap.put("error.claim.partReturnRejected", null);
            }
        }
        messageMap.put("ERROR_CODES", errorCodesMap);
        messageMap.put("WARNING_CODES", warningCodesMap);
        return messageMap;
    }

    public Map<String, String[]> validateClaim(Claim theClaim,
            boolean isProcessorReview, String takenTransition, boolean hasActionErrors,
            String formName, boolean isPartShipNotRcvd) {
        Map<String, String[]> errorCodesMap = new HashMap<String, String[]>();
        boolean isThirdParty = false;
        
        if (ClaimState.SERVICE_MANAGER_REVIEW.getState().equals(theClaim.getState().getState())
                && !StringUtils.hasText(takenTransition)) {
            errorCodesMap.put("error.newClaim.action", null);
        }
        errorCodesMap.putAll(validateReplacedInstalledParts(theClaim, isProcessorReview,
                hasActionErrors));
        errorCodesMap.putAll(validateMiscelleanousParts(theClaim));

        if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, theClaim.getForDealer())) {
            Boolean isThirdPartyLogin = orgService.isThirdPartyDealerWithLogin(theClaim
                    .getForDealer().getId());
            if (!isThirdPartyLogin && !theClaim.isServiceProviderSameAsFiledByOrg()) {
                isThirdParty = true;
            } else if (!isThirdPartyLogin && theClaim.getFiledBy().isInternalUser()) {
                isThirdParty = true;
            }
            if (isThirdParty) {
                if (theClaim.getServiceInformation() != null
                        && theClaim.getServiceInformation().getServiceDetail() != null
                        && theClaim.getServiceInformation().getServiceDetail().getLaborPerformed() != null
                        && !theClaim.getServiceInformation().getServiceDetail().getLaborPerformed()
                                .isEmpty()) {
                    if (theClaim.getServiceInformation().getThirdPartyLaborRate() == null) {
                        errorCodesMap.put("error.claim.thirdPartyLaborRateNull",
                        		null);
                    } else if (theClaim.getServiceInformation().getThirdPartyLaborRate()
                            .isNegative()) {
                        errorCodesMap.put("error.claim.invalidThirdPartyLaborRate",
                        		null);
                    }
                }
            }
        }

        errorCodesMap.putAll(validateClaimForLaborDetail(theClaim));

        setTotalLaborHoursForClaim(theClaim);
        if (!isProcessorReview
                && ClaimState.DRAFT.getState().equals(theClaim.getState().getState())) {
            errorCodesMap
                    .putAll(validateAdditionalAttributes(theClaim, formName, isPartShipNotRcvd));
        }

        // HUSS-275. Scenario: Claim sent for CP Review.
        // Gets Auto replied due to lapse of window period
        // Processor should not be able to accept the CP part.
        // Can be accepted with 0 CP amount though.
        // Any other action can be taken.
        if (isCPAdvisorEnabled()) {
            errorCodesMap.putAll(validateForNoCPOnAutoReply(theClaim, takenTransition));
        }

        errorCodesMap.putAll(validateUniquenessOfOEMPartReplaced(theClaim));
        errorCodesMap.putAll(validateUniquenessOfMiscPartReplaced(theClaim));
        return errorCodesMap;
    }

    private Map<String, String[]> validateUniquenessOfOEMPartReplaced(Claim claim) {
        Map<String, String[]> errorCodesMap = new HashMap<String, String[]>();
    	List<InstalledParts> installedParts = new ArrayList<InstalledParts>();
		List<OEMPartReplaced> replacedParts = new ArrayList<OEMPartReplaced>();
        List<OEMPartReplaced> partReplaced = claim.getServiceInformation().getServiceDetail()
                .getOemPartsReplaced();
        Set<String> parts = new HashSet<String>();
        for (OEMPartReplaced part : partReplaced) {
            if (part.getItemReference() != null
                    && part.getItemReference().getReferredItem() != null) {
                if (parts.contains(part.getItemReference().getReferredItem().getNumber())) {
                    errorCodesMap.put("error.newClaim.invalidOemParts", null);
                    break;
                } else {
                    parts.add(part.getItemReference().getReferredItem().getNumber());
                }
            }
        }
        for(HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled())
		{
			replacedParts.addAll(hussmanPartsReplacedInstalled.getReplacedParts());
			installedParts.addAll(hussmanPartsReplacedInstalled.getHussmanInstalledParts());
		}
		if (!installedParts.isEmpty() && this.hasDuplicateInstalledParts(installedParts)) {
			  errorCodesMap.put("error.claim.duplicateOEMPartsInstalled",null);
		}
		if (!replacedParts.isEmpty() &&  this.hasDuplicateReplacedParts(replacedParts)) {
			  errorCodesMap.put("error.claim.duplicateOEMPartsReplaced",null);
		}
        return errorCodesMap;
    }

    private Map<String, String[]> validateUniquenessOfMiscPartReplaced(Claim claim) {
        Map<String, String[]> errorCodesMap = new HashMap<String, String[]>();
        List<NonOEMPartReplaced> partReplaced = claim.getServiceInformation().getServiceDetail()
                .getMiscPartsReplaced();
        if (partReplaced != null && partReplaced.size() > 0) {
            partReplaced.remove(Collections.singleton(null));
            Set<String> parts = new HashSet<String>();
            for (NonOEMPartReplaced part : partReplaced) {
                if (part != null && part.getMiscItem() != null) {
                    if (parts.contains(part.getMiscItem().getPartNumber())) {
                        errorCodesMap.put("error.newClaim.invalidMiscPart", null);
                        break;
                    } else {
                        parts.add(part.getMiscItem().getPartNumber());
                    }

                }
            }
        }
        return errorCodesMap;
    }

    public Map<String, String[]> validateForNoCPOnAutoReply(Claim claim, String takenTransition) {

        Map<String, String[]> errorCodesMap = new HashMap<String, String[]>();

        if (takenTransition != null && "Accept".equalsIgnoreCase(takenTransition)) {

            boolean isAnyCpPercentageNonZero = false; // processor can Accept if all CP %ages are zero

            for (LineItemGroup lineItemGroup : claim.getPayment().getLineItemGroups()) {
                if (lineItemGroup.getPercentageAcceptanceForCp().doubleValue() > 0) {
                    isAnyCpPercentageNonZero = true;
                    break;
                }
            }

            
        }
        return errorCodesMap;
    }

    public boolean isCPPercentageOnClaimChanged(Claim claim) {
        boolean isLineItemForCPPercentageChanged = false;
        boolean lineItemLevelAccepted = isLineItemForCPPercentageChanged(claim);
        Payment approvedPayment = claimService.getLatestManualCpReviewedPayment(claim);
        
        if(approvedPayment == null){
        	return true;
        }
        List<LineItemGroup> approvedLineItemGroups = approvedPayment.getLineItemGroups();

        for (LineItemGroup lineItemGroup : claim.getPayment().getLineItemGroups()) {
            // Will be the case if none of the CP Reviews have been manually
            // reviewed.
            BigDecimal currentAcceptance = lineItemGroup.getPercentageAcceptanceForCp();
            if (approvedLineItemGroups == null) {
                if (currentAcceptance.compareTo(BigDecimal.ZERO) != 0) {
                    isLineItemForCPPercentageChanged = true;
                    break;
                }
            } else {
                String groupName = lineItemGroup.getName();
                // Else, compare the values.
                LineItemGroup approvedLineItemGroup = approvedPayment.getLineItemGroup(lineItemGroup.getName());
                if (lineItemLevelAccepted && !Section.TOTAL_CLAIM.equalsIgnoreCase(groupName)) {
                    if (approvedLineItemGroup != null
                            && currentAcceptance.compareTo(BigDecimal.ZERO) != 0
                            && currentAcceptance.compareTo(approvedLineItemGroup
                            		.getPercentageAcceptedForAdditionalInfo(AdditionalPaymentType.ACCEPTED_FOR_CP)) > 0) {
                        isLineItemForCPPercentageChanged = true;
                        break;
                    }
                } else if (!lineItemLevelAccepted && Section.TOTAL_CLAIM.equalsIgnoreCase(groupName)) {
                	if (approvedLineItemGroup != null
                            && currentAcceptance.compareTo(BigDecimal.ZERO) != 0
                            && Section.TOTAL_CLAIM.equalsIgnoreCase(groupName)
                            && currentAcceptance.compareTo(approvedLineItemGroup
                            									.getPercentageAcceptedForAdditionalInfo(AdditionalPaymentType.ACCEPTED_FOR_CP)) > 0) {
                        isLineItemForCPPercentageChanged = true;
                        break;
                    }
                }
            }
        }
        return isLineItemForCPPercentageChanged;
    }

    public boolean isLineItemForCPPercentageChanged(Claim claim) {
        boolean isLineItemForCPPercentageChanged = false;
        for (LineItemGroup lineItemGroup : claim.getPayment().getLineItemGroups()) {
            if (!Section.TOTAL_CLAIM.equals(lineItemGroup.getName())
                    && lineItemGroup.getLatestAudit().getPercentageAcceptanceForCp().longValue() != new Long(
                            0).longValue()) {
                isLineItemForCPPercentageChanged = true;
                break;
            }
        }
        return isLineItemForCPPercentageChanged;
    }
    
    public void prepareAttributesForClaim(Claim claim, String formName, boolean isPartShipNotRcvd) {
        if (claim.getState() == ClaimState.DRAFT
                || isJobCodeFaultCodeEditable(formName, isPartShipNotRcvd)) {
            if (!attributeAssociationService.isAnyAttributeConfiguredForBU())
                return;
            claimService.prepareAttributesForInventory(claim);
            claimService.prepareAttributesForFaultCode(claim, (claim.getServiceInformation()
                    .getFaultCodeRef() == null ? null : claim.getServiceInformation()
                    .getFaultCodeRef().getId()));
            if (claim.getServiceInformation().getServiceDetail() != null)
                claimService.prepareAttributesForJobCode(claim);
            claimService.prepareAttributesForCausalPart(claim,
                    (claim.getServiceInformation().getCausalPart() == null ? null : claim
                            .getServiceInformation().getCausalPart()));
            Long srmId = claim.isServiceManagerRequest() && null != claim.getReasonForServiceManagerRequest() ? claim.getReasonForServiceManagerRequest().getId() : 0L;
          claimService.prepareAttributesForClaim(claim,srmId);
            
        }
    }

    public Map<String, String[]> validateAdditionalAttributes(Claim claim, String formName,
            boolean isPartShipNotRcvd) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        prepareAttributesForClaim(claim, formName, isPartShipNotRcvd);
        errorCodeMap.putAll(validateClaimedItemAttributes(claim));
        errorCodeMap.putAll(validateCausalPartAttributes(claim));
        errorCodeMap.putAll(validateFaultCodeAttributes(claim));
        errorCodeMap.putAll(validateJobCodeAttributes(claim));   
        errorCodeMap.putAll(validateClaimAttributes(claim));
        return errorCodeMap;

    }

    private Map<String, String[]> validateClaimedItemAttributes(Claim claim) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        List<ClaimedItem> claimedItems = claim.getClaimedItems();
        for (ClaimedItem claimedItem : claimedItems) {
            List<ClaimAttributes> claimAttributes = claimedItem.getClaimAttributes();
            for (ClaimAttributes claimAttribute : claimAttributes) {
            	if(!checkValidityOfMandatoryAttributes(claimAttribute))
            	{
            		 String inventoryNumber = claimedItem.getItemReference().isSerialized() ? claimedItem
                             .getItemReference().getReferredInventoryItem().getSerialNumber()
                             : claimedItem.getItemReference().getModel().getName();
            		 errorCodeMap
                     .put("error.additionalAttributes.Mandatory", new String[] {
                             claimAttribute.getAttributes().getName(),
                             inventoryNumber });
            	}
            	else if (!checkValidityOfAttributes(claimAttribute)) {
                    String inventoryNumber = claimedItem.getItemReference().isSerialized() ? claimedItem
                            .getItemReference().getReferredInventoryItem().getSerialNumber()
                            : claimedItem.getItemReference().getModel().getName();
                    errorCodeMap.put("error.additionalAttributes.inventory.invalidValue",
                            new String[] { claimAttribute.getAttributes().getName(),
                                    inventoryNumber });
                }
            }
        }
        return errorCodeMap;
    }

    private Map<String, String[]> validateCausalPartAttributes(Claim claim) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        List<ClaimAttributes> claimAttributes = claim.getServiceInformation()
                .getPartClaimAttributes();
        for (ClaimAttributes claimAttribute : claimAttributes) {
        	if(!checkValidityOfMandatoryAttributes(claimAttribute))
        	{
        		 errorCodeMap
                 .put("error.additionalAttributes.Mandatory", new String[] {
                         claimAttribute.getAttributes().getName(),
                         claim.getServiceInformation().getCausalPart().getNumber() });
        	}
        	else if (!checkValidityOfAttributes(claimAttribute)) {
                if (claim.getServiceInformation().getCausalPart() != null) {
                    errorCodeMap.put("error.additionalAttributes.causalPart.invalidValue",
                            new String[] { claimAttribute.getAttributes().getName(),
                                    claim.getServiceInformation().getCausalPart().getNumber() });
                }
            }
        }
        return errorCodeMap;

    }

    private Map<String, String[]> validateFaultCodeAttributes(Claim claim) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        List<ClaimAttributes> claimAttributes = claim.getServiceInformation()
                .getFaultClaimAttributes();
        for (ClaimAttributes claimAttribute : claimAttributes) {
        	
        	if(!checkValidityOfMandatoryAttributes(claimAttribute))
        	{
        		 errorCodeMap
                 .put("error.additionalAttributes.Mandatory", new String[] {
                         claimAttribute.getAttributes().getName(),
                         claim.getServiceInformation().getFaultCodeRef().getDefinition()
                         .getCode() });
        	}
        	
        	else  if (!checkValidityOfAttributes(claimAttribute)) {
                errorCodeMap
                        .put("error.additionalAttributes.faultCode.invalidValue", new String[] {
                                claimAttribute.getAttributes().getName(),
                                claim.getServiceInformation().getFaultCodeRef().getDefinition()
                                        .getCode() });
            }
        }
        return errorCodeMap;

    }

    
    private Map<String, String[]> validateClaimAttributes(Claim claim) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        List<ClaimAttributes> claimAttributes = claim.getClaimAdditionalAttributes();
             
        for (ClaimAttributes claimAttribute : claimAttributes) {
        	if(!checkValidityOfMandatoryAttributes(claimAttribute))
        	{
        		 errorCodeMap
                 .put("error.additionalAttributes.Mandatory", new String[] {
                         claimAttribute.getAttributes().getName(),
                        "ClaimAttributes"});
        	}
        	else if (!checkValidityOfAttributes(claimAttribute)) {
                errorCodeMap
                        .put("error.additionalAttributes.invalidValue", new String[] {
                                claimAttribute.getAttributes().getName(),
                               "ClaimAttributes"});
            }
        }
        return errorCodeMap;

    }

    private Map<String, String[]> validateJobCodeAttributes(Claim claim) {
        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
        List<LaborDetail> laborDetails = claim.getServiceInformation().getServiceDetail()
                .getLaborPerformed();
        for (LaborDetail laborDetail : laborDetails) {
            List<ClaimAttributes> claimAttributes = laborDetail.getClaimAttributes();
            for (ClaimAttributes claimAttribute : claimAttributes) {
            	if(!checkValidityOfMandatoryAttributes(claimAttribute))
            	{
            		 errorCodeMap
                     .put("error.additionalAttributes.Mandatory", new String[] {
                             claimAttribute.getAttributes().getName(),
                             laborDetail.getServiceProcedure().getDefinition().getCode()});
            	}
            	
            	else if (!checkValidityOfAttributes(claimAttribute)) {
                    errorCodeMap.put("error.additionalAttributes.jobCode.invalidValue",
                            new String[] { claimAttribute.getAttributes().getName(),
                                    laborDetail.getServiceProcedure().getDefinition().getCode() });
                }
            }
        }
        return errorCodeMap;

    }

    private boolean checkValidityOfMandatoryAttributes(ClaimAttributes claimAttribute) {
        if (!StringUtils.hasText(claimAttribute.getAttrValue())) {
            if (claimAttribute.getAttributes().getMandatory()) {
                return false;
            }
        }
        return true;
        }
    private boolean checkValidityOfAttributes(ClaimAttributes claimAttribute) {
    	if(claimAttribute.getAttrValue()!=null&&!claimAttribute.getAttrValue().isEmpty()){
		     if (ClaimAttributes.NUMERIC_TYPE.equals(claimAttribute.getAttributes()
		                .getAttributeType())) {
		            try {
		                Double.parseDouble(claimAttribute.getAttrValue());
		            } catch (NumberFormatException numFormatException) {
		                return false;
		            }
		        } else if (ClaimAttributes.DATE_TYPE.equals(claimAttribute.getAttributes()
		                .getAttributeType())) {
		            try {
		                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		                sdf.parse(claimAttribute.getAttrValue());
		            } catch (ParseException parseException) {
		                return false;
		
		            }
		        }
    	}
        return true;
    }

    public boolean isJobCodeFaultCodeEditable(String formName, boolean isPartShipNotRcvd) {
        if (formName != null) {
            if (formName.equalsIgnoreCase("Draft Claim".replaceAll(" ", "_"))
                    || (formName.equalsIgnoreCase("Service Manager Review".replaceAll(" ", "_"))))
                return true;
            else if (formName.equalsIgnoreCase("Processor Review".replaceAll(" ", "_"))
                    && isEditableForProcessor())
                return true;
            else if (formName.equalsIgnoreCase("Forwarded".replaceAll(" ", "_"))
                    && isEditableForDealer())
                return true;
            else if (formName.equalsIgnoreCase("Part Shipped Not Received".replaceAll(" ", "_"))
                    && isPartShipNotRcvd)
                return true;
        }
        return false;
    }

    public boolean isCPAdvisorEnabled() {
        return this.configParamService.getBooleanValue(ConfigName.ENABLE_CP_ADVISOR.getName());
    }

    public boolean isRootCauseAllowed() {
        return this.configParamService.getBooleanValue(ConfigName.IS_ROOT_CAUSE_ALLOWED.getName());
    }

    public boolean isEditableForDealer() {
        return this.configParamService.getBooleanValue(ConfigName.CAN_DEALER_EDIT_FWDED_CLMS
                .getName());
    }

    public boolean isEditableForProcessor() {
        return this.configParamService
                .getBooleanValue(ConfigName.CAN_PROCESSOR_EDIT_CLMS.getName());
    }

    public void mergeAllCodes(Map<String, Map<String, String[]>> messageCodes,
            Map<String, String[]> errrorCodes, Map<String, String[]> warningCodes) {
        if (messageCodes.containsKey("ERROR_CODES")) {
            errrorCodes.putAll(messageCodes.get("ERROR_CODES"));
        }
        if (messageCodes.containsKey("WARNING_CODES")) {
            warningCodes.putAll(messageCodes.get("WARNING_CODES"));
        }

    }
    
    public void validateRepairDate(Claim claim, Map<String, String[]> errorMap) {
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            InventoryItem inventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
            CalendarDate scrapDate = null;
            CalendarDate unScrapDate = null;
            CalendarDate stolenDate = null;
            CalendarDate unStolenDate = null;
            if (inventoryItem != null && inventoryItem.getInventoryItemAttrVals() != null) {
                if (inventoryItem.getInventoryItemAttrVals().size() > 0) {
                    for (InventoryItemAttributeValue inventoryItemAttrVal : inventoryItem
                            .getInventoryItemAttrVals()) {
                        if (AttributeConstants.SCRAP_COMMENTS.equals(inventoryItemAttrVal
                                .getAttribute().getName())) {
                            InventoryScrapTransaction scrap = (InventoryScrapTransaction) convertXMLToObject(inventoryItemAttrVal
                                    .getValue());
                            scrapDate = scrap.getDateOfScrapOrUnscrap();
                        }
                        if (AttributeConstants.UN_SCRAP_COMMENTS.equals(inventoryItemAttrVal
                                .getAttribute().getName())) {
                            InventoryScrapTransaction unScrap = (InventoryScrapTransaction) convertXMLToObject(inventoryItemAttrVal
                                    .getValue());
                            unScrapDate = unScrap.getDateOfScrapOrUnscrap();
                        }
                        if (scrapDate != null && unScrapDate != null) {
                            if ((claim.getRepairDate().isAfter(scrapDate) || claim.getRepairDate().equals(scrapDate))
                                    && (claim.getRepairDate()
                                            .isBefore(unScrapDate) || claim.getRepairDate().equals(unScrapDate))
                                    && !scrapDate.equals(unScrapDate)) {
                                errorMap.put("message.scrap.machineScrapped",
                                        new String[] { inventoryItem.getSerialNumber() });
                            } else {
                                scrapDate = null;
                                unScrapDate = null;
                            }
                        }
                        if (AttributeConstants.STOLEN_COMMENTS.equals(inventoryItemAttrVal
                                .getAttribute().getName())) {
                            InventoryStolenTransaction stolen = (InventoryStolenTransaction) convertXMLToObject(inventoryItemAttrVal
                                    .getValue());
                            stolenDate = stolen.getDateOfStolenOrUnstolen();
                        }
                        if (AttributeConstants.UN_STOLEN_COMMENTS.equals(inventoryItemAttrVal
                                .getAttribute().getName())) {
                            InventoryStolenTransaction unStolen = (InventoryStolenTransaction) convertXMLToObject(inventoryItemAttrVal
                                    .getValue());
                            unStolenDate = unStolen.getDateOfStolenOrUnstolen();
                        }
                        if (stolenDate != null && unStolenDate != null) {
                            if ((claim.getRepairDate().isAfter(stolenDate) || claim.getRepairDate().equals(stolenDate))
                                    && (claim.getRepairDate()
                                            .isBefore(unStolenDate) || claim.getRepairDate().equals(unStolenDate))
                                    && !stolenDate.equals(unStolenDate)) {
                                errorMap.put("message.stole.machineStolen",
                                        new String[] { inventoryItem.getSerialNumber() });
                            } else {
                            	stolenDate = null;
                                unStolenDate = null;
                            }
                        }
                    }
                    if (claim.getType().getType().equalsIgnoreCase("Campaign") && scrapDate != null
                            && unScrapDate == null
                            && (claimedItem.getClaim().getRepairDate().isAfter(scrapDate) || claimedItem
                                    .getClaim().getRepairDate().equals(scrapDate))) {
                        errorMap.put("message.scrap.machineScrapped", new String[] { inventoryItem
                                .getSerialNumber() });
                        errorMap.put("message.scrap.claim", new String[] { scrapDate
                                .toString("MM/dd/yyyy") });
                    }
                    if (!claim.getType().getType().equalsIgnoreCase("Campaign") && scrapDate != null
                            && unScrapDate == null
                            && (claimedItem.getClaim().getRepairDate().isAfter(scrapDate) || claimedItem
                                    .getClaim().getRepairDate().equals(scrapDate))) {
                        errorMap.put("message.scrap.machineScrapped", new String[] { inventoryItem
                                .getSerialNumber() });
                        errorMap.put("message.scrap.claim", new String[] { scrapDate
                                .toString("MM/dd/yyyy") });
                    }
                    if(claim.getType().getType().equalsIgnoreCase("Campaign") && stolenDate != null
                            && unStolenDate == null && claim.getRepairDate().isAfter(stolenDate) || claim.getRepairDate().equals(stolenDate)){
                    	 errorMap.put("message.stole.machineStolen", new String[] { inventoryItem
                                 .getSerialNumber() });
                         errorMap.put("message.stole.claim", new String[] { stolenDate
                                 .toString("MM/dd/yyyy") });
                    }
                    if (!claim.getType().getType().equalsIgnoreCase("Campaign") && stolenDate != null
                            && unStolenDate == null
                            && (claimedItem.getClaim().getRepairDate().isAfter(stolenDate) || claimedItem
                                    .getClaim().getRepairDate().equals(stolenDate))) {
                        errorMap.put("message.stole.machineStolen", new String[] { inventoryItem
                                .getSerialNumber() });
                        errorMap.put("message.stole.claim", new String[] { stolenDate
                                .toString("MM/dd/yyyy") });
                    }
                }
            }
        }
    }
    
    public void setTotalLaborHoursForClaim(Claim claim) {
        List<LaborDetail> LaborDetails = claim.getServiceInformation().getServiceDetail()
                .getLaborPerformed();
        for (LaborDetail labor : LaborDetails) {
            if (labor != null) {
                labor.setHoursSpentForMultiClaim(labor.getHoursSpent().multiply(
                        new BigDecimal(getApprovedClaimedItems(claim))));
                if (labor.getAdditionalLaborHours() != null) {
                    labor.setAdditionalHoursSpentForMultiClaim(labor.getAdditionalLaborHours()
                            .multiply(new BigDecimal(getApprovedClaimedItems(claim))));
                }
            }
        }
    }
    

    /*
     * By default the claimed item is approved It is disapprove only when
     * processor rejects it . Hence we are calculating on the number of claimed
     * items approved
     */
    public int getApprovedClaimedItems(Claim claim) {
            int approvedClaimedItems = 0;
            List<ClaimedItem> claimedItems = claim.getClaimedItems();
            for (ClaimedItem claimedItem : claimedItems) {
                    if (claimedItem.isProcessorApproved()) {
                            approvedClaimedItems++;
                    }
            }
            return approvedClaimedItems;
    }
    
    public boolean isMachineClaim(Claim claim) {
        return InstanceOfUtil.isInstanceOfClass(MachineClaim.class, claim);
}

    public boolean isPartsClaim(Claim claim) {
        return InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim);
    }
    
    
	public void prepareAttributesForClaim(Claim claim, boolean isSerialized) {
		claimService.prepareAttributesForInventory(claim, isSerialized);
		claimService.prepareAttributesForFaultCode(claim, 
    			(claim.getServiceInformation().getFaultCodeRef()==null
    					? null
    					:claim.getServiceInformation().getFaultCodeRef().getId()));
		if(claim.getServiceInformation().getServiceDetail() != null)
	        claimService.prepareAttributesForJobCode(claim);
		claimService.prepareAttributesForCausalPart(claim, 
    			(claim.getServiceInformation().getCausalPart() == null
    					? null
    					: claim.getServiceInformation().getCausalPart()));
		
	}
	
	public void prepareReplacedInstalledParts(Claim claim) {
		List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = claim
				.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled();

		for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled : hussmanPartsReplacedInstalled) {
			if(hussmanPartReplacedInstalled == null ) {
				continue;
			}
			List<InstalledParts> hussmanInstalledParts = hussmanPartReplacedInstalled
					.getHussmanInstalledParts();
			for (InstalledParts hussmanInstalledPart : hussmanInstalledParts) {
				if (hussmanInstalledPart != null) {
					hussmanInstalledPart.setIsHussmanPart(true);
					//Not required to update here so commenting it
					/*if(hussmanInstalledPart.getPriceUpdated())
						claimService.updateInstalledParts((InstalledParts)hussmanInstalledPart);*/
				}
				
			}

			List<InstalledParts> nonHussmanInstalledParts = hussmanPartReplacedInstalled
					.getNonHussmanInstalledParts();
			for (InstalledParts nonHussmanInstalledPart : nonHussmanInstalledParts) {
				if (nonHussmanInstalledPart != null) {
					nonHussmanInstalledPart.setIsHussmanPart(false);
				}
			}
		}
	}
	
	public void setTotalQtyForReplacedParts(Claim claim) {
		boolean partsReplacedInstalledSectionVisible  = configParamService
		.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
				.getName());
		if(!partsReplacedInstalledSectionVisible) {
			setOEMPartReplacedQuantity(claim);
		} else {
			setOEMPartReplacedInstalledQuantity(claim);
		}
	}
	
	public void setQtyForMultipleItems(Claim claim) {
		boolean partsReplacedInstalledSectionVisible  = configParamService
		.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
				.getName());
		if(!partsReplacedInstalledSectionVisible) {
			setOEMPartReplacedQuantity(claim);
		} else {
			setOEMPartReplacedInstalledQuantity(claim);
		}
	}

	private void setOEMPartReplacedQuantity(Claim claim) {
		List<OEMPartReplaced> oemPartReplaced = claim.getServiceInformation()
				.getServiceDetail().getOemPartsReplaced();
		List<NonOEMPartReplaced> nonOemPartReplaced = claim
				.getServiceInformation().getServiceDetail()
				.getNonOEMPartsReplaced();
		for (OEMPartReplaced oemPart : oemPartReplaced) {
			if (oemPart.getInventoryLevel().booleanValue()) {
				oemPart.setNumberOfUnits(oemPart.getNumberOfUnits()
						* getApprovedClaimedItems(claim));
			}
		}
		for (NonOEMPartReplaced nonOemPart : nonOemPartReplaced) {
			if (nonOemPart.getInventoryLevel().booleanValue()) {
				nonOemPart.setNumberOfUnits(nonOemPart.getNumberOfUnits()
						* getApprovedClaimedItems(claim));
			}
		}
	}

	private void setOEMPartReplacedInstalledQuantity(Claim claim) {
		List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = claim
				.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled();
		if( hussmanPartsReplacedInstalled != null) {
			for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled : hussmanPartsReplacedInstalled) {
				if( hussmanPartReplacedInstalled == null ) {
					continue; // This condition is to avoid the NPE due to the indexes problem
				}
				boolean inventoryClaimLevel;
				if( hussmanPartReplacedInstalled
						.getInventoryLevel() != null ) {
					inventoryClaimLevel = hussmanPartReplacedInstalled
							.getInventoryLevel();
				} else {
					inventoryClaimLevel = false;
				}
				List<OEMPartReplaced> oemPartReplaced = hussmanPartReplacedInstalled
						.getReplacedParts();
				List<InstalledParts> oemPartInstalled = hussmanPartReplacedInstalled
						.getHussmanInstalledParts();
				List<InstalledParts> nonOemPartInstalled = hussmanPartReplacedInstalled
						.getNonHussmanInstalledParts();

				if (inventoryClaimLevel) {
					for (OEMPartReplaced oemPart : oemPartReplaced) {
						if( oemPart == null ) {
							continue; // Null check to avoid the sporadic NPE due to indexing problems
						}
						oemPart.setNumberOfUnits(oemPart.getNumberOfUnits()
								* getApprovedClaimedItems(claim));
					}
					for (InstalledParts oemPart : oemPartInstalled) {
						if( oemPart == null ) {
							continue; // Null check to avoid the sporadic NPE due to indexing problems
						}
						oemPart.setNumberOfUnits(oemPart.getNumberOfUnits()
								* getApprovedClaimedItems(claim));
					}
					for (InstalledParts nonOemPart : nonOemPartInstalled) {
						if( nonOemPart == null ) {
							continue; // Null check to avoid the sporadic NPE due to indexing problems
						}
						nonOemPart.setNumberOfUnits(nonOemPart.getNumberOfUnits()
								* getApprovedClaimedItems(claim));
					}
				}
			}
		}
		List<NonOEMPartReplaced> nonOEMPartsReplaced = claim
			.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced();
		if(nonOEMPartsReplaced !=null)
		{
			for (NonOEMPartReplaced nonOemPart : nonOEMPartsReplaced) {
				if (nonOemPart.getInventoryLevel().booleanValue()) {
					nonOemPart.setNumberOfUnits(nonOemPart.getNumberOfUnits()
							* getApprovedClaimedItems(claim));
				}
			}	
		}
	}
	
	public void setPolicyOnClaimedItems(Claim theClaim) {
		
		if (!theClaim.canPolicyBeComputed()) {
			setClaimProcessedAs(theClaim);
			return;
		}
		for (ClaimedItem claimedItem : theClaim.getClaimedItems()) {
			try {
				Policy applicablePolicy = this.policyService
						.findApplicablePolicy(claimedItem);
				claimedItem.setApplicablePolicy(applicablePolicy);
			} catch (PolicyException e) {
				throw new RuntimeException(
						"Failed to find policy for Claimed Item [ "
								+ claimedItem + "]", e);
			}
		}
		setClaimProcessedAs(theClaim);
	}
	
	@SuppressWarnings("deprecation")
	public void setClaimProcessedAs(Claim claim) {
		if (!claim.getItemReference().isSerialized()) {
			claim.setClaimProcessedAs(Constants.INVALID_ITEM_NO_WARRANTY);
		} else if (claim.getItemReference().getReferredInventoryItem() != null
				&& InventoryType.STOCK.getType().equals(
						claim.getItemReference().getReferredInventoryItem()
								.getType().getType())) {
			claim.setClaimProcessedAs(Constants.VALID_ITEM_STOCK);
		} else if (claim.getItemReference().getReferredInventoryItem() != null
				&& InventoryType.RETAIL.getType().equals(
						claim.getItemReference().getReferredInventoryItem()
								.getType().getType())) {
			if(claim.getItemReference().getReferredInventoryItem()
					.getWarranty()!=null){
				if (claim.getItemReference().getReferredInventoryItem()
						.getWarranty().getPolicies() == null
						|| claim.getItemReference().getReferredInventoryItem()
								.getWarranty().getPolicies().isEmpty()) {
					claim.setClaimProcessedAs(Constants.VALID_ITEM_NO_WARRANTY);
				} else if (claim.getClaimedItems().get(0).getApplicablePolicy() == null
						|| claim.getClaimedItems().get(0).getApplicablePolicy()
								.getCode() == null) {
					claim.setClaimProcessedAs(Constants.VALID_ITEM_OUT_OF_WARRANTY);
				}
			}
		}
		if(claim.getClaimedItems() != null && !claim.getClaimedItems().isEmpty()
				&& claim.getClaimedItems().get(0).getApplicablePolicy() != null)
		{
			claim.setClaimProcessedAs(claim.getClaimedItems().get(0).getApplicablePolicy().getCode());
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void setClaimProcessedAsForClaimedParts(Claim claim) {
		if (claim.getPartItemReference().isSerialized() && (claim.getPartItemReference().getReferredInventoryItem()
				.getWarranty() == null)) {
			claim.setClaimProcessedAs(Constants.VALID_ITEM_NO_WARRANTY);
		} 
		
		else if (!claim.getPartItemReference().isSerialized() && claim.getClaimedItems().get(0).getApplicablePolicy() == null
				|| claim.getClaimedItems().get(0).getApplicablePolicy() == null) {
			claim.setClaimProcessedAs(Constants.VALID_ITEM_OUT_OF_WARRANTY);
		}
		
		else if (claim.getPartItemReference().isSerialized() &&
				(claim.getClaimedItems().get(0).getApplicablePolicy() == null
						|| claim.getClaimedItems().get(0).getApplicablePolicy()
								.getCode() == null)) {
			
				claim.setClaimProcessedAs(Constants.VALID_ITEM_OUT_OF_WARRANTY);
			
		}
		if(claim.getClaimedItems() != null && !claim.getClaimedItems().isEmpty()
				&& claim.getClaimedItems().get(0).getApplicablePolicy() != null)
		{
			claim.setClaimProcessedAs(claim.getClaimedItems().get(0).getApplicablePolicy().getCode());
		}
		
	}
	

	public void setPolicyOnClaimedParts(Claim theClaim) {

		try {
			Policy applicablePolicy = this.policyService
					.findApplicablePolicy(theClaim);
			theClaim.setApplicablePolicy(applicablePolicy);
		} catch (PolicyException e) {
			throw new RuntimeException("Failed to find policy for Claim [ "
					+ theClaim + "]", e);
		}

		setClaimProcessedAsForClaimedParts(theClaim);
	}
	
	public void setPolicyForClaim(Claim theClaim) {
		if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, theClaim) && this.configParamService
				.getBooleanValue(ConfigName.CONSIDER_WARRANTY_COVERAGE_FOR_PART_CLAIM.getName()))
		{
		
			setPolicyOnClaimedParts(theClaim);
		}
		else
		{
			setPolicyOnClaimedItems(theClaim);
		}
	}
	
	public List<String> fetchApplicablePolicyCodesForClaim(Claim theClaim)
	{
		if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, theClaim) && this.configParamService
				.getBooleanValue(ConfigName.CONSIDER_WARRANTY_COVERAGE_FOR_PART_CLAIM.getName()))
		{
		
			return this.policyService
				.findApplicablePolicesCodes(theClaim);
		}
		else
		{
			if (!theClaim.canPolicyBeComputed()) {
				
				return null;
			}
			List<String>policyCodes= new ArrayList<String>();
			for (ClaimedItem claimedItem : theClaim.getClaimedItems()) {
				List<String>codes=this.policyService
				.findApplicablePolicesCodes(claimedItem);
				if(codes!=null && codes.size()>0)
				{
					policyCodes.addAll(codes);
				}
					
				
			}
			return policyCodes;
		}
	}
	/**
	 * The method is invoked only for the claim drafted from DealerAPI and validates the required fields on page 1.
	 * @param claim
	 * @return errorCode
	 */
	public Map<String, String[]> validateRequiredFields(Claim claim) {
		Map<String, String[]> errorCodes = new HashMap<String, String[]>();
		if (!InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim)
				&& claim.getFailureDate() == null) {
			errorCodes.put("error.newClaim.invalidDuration", null);
		}
		if (!claim.getItemReference().isSerialized()
				&& !(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim) && (claim.getCompetitorModelBrand()!=null && !claim.getCompetitorModelBrand().isEmpty()
						&& !claim.getCompetitorModelDescription().isEmpty() && !claim
						.getCompetitorModelTruckSerialnumber().isEmpty()))) {
        	
            if (claim.getPurchaseDate() == null && !((PartsClaim)claim).getPartInstalled()) {
                errorCodes.put("error.newClaim.purchasedateRequired", null);
            }
            
            /**
             * If invoice number is configured,it is mandatory
             */
            if(!(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim) && !((PartsClaim)claim).getPartInstalled())) {
				if (isInvoiceNumberApplicable() && !StringUtils.hasText(claim.getInvoiceNumber())) {
					errorCodes.put("message.error.invoiceNumberMandatory", null);
				}
            }

        }
		
		if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim) && ((PartsClaim)claim).getPartInstalled()) {
			if(!claim.getPartItemReference().isSerialized() &&claim.getInstallationDate() == null) {
				errorCodes.put("error.newClaim.invalidInstallationDuration", null);
			}
		}
		return errorCodes;
	}
	
	public boolean hasDuplicateReplacedParts(List<OEMPartReplaced> oemPartsReplaced) {
		HashSet<InventoryItem> setOemPartsReplaced = new HashSet<InventoryItem>();
		for (OEMPartReplaced oemPartReplaced : oemPartsReplaced) {
			if (oemPartReplaced.getItemReference().getReferredInventoryItem() != null) {
				if (!setOemPartsReplaced.add(oemPartReplaced.getItemReference().getReferredInventoryItem()))
					return true;
			}
		}
		return false;
	}
	
	public boolean hasDuplicateInstalledParts(List<InstalledParts> installedParts){
		HashSet<String> listSerialNumbersItemNumbers = new HashSet<String>();		
		for (InstalledParts installedPart : installedParts) {
			if (installedPart.getSerialNumber() != null && !installedPart.getSerialNumber().equalsIgnoreCase("") && installedPart.getItem()!=null) {
				if (!listSerialNumbersItemNumbers.add(installedPart.getSerialNumber()+"#"+installedPart.getItem().getNumber()))
					return true;
			}
		}		
		return false;
	}

    public boolean hasUserPermissionOnInventory(Long dealerId){
        List<Long> organizationList = orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
        if(dealerId == getLoggedInUser().getBelongsToOrganization().getId().longValue()) {
            return true;
        }
        else if(getLoggedInUsersDealership().isEnterpriseDealer()
                && getLoggedInUsersDealership().getChildDealersIds().contains(dealerId)){
            return true;
        } else if(organizationList.contains(dealerId)){
           return true;
        }
        return false;
    }

    //check both current owner and shipto
    public boolean hasUserPermissionOnInventory(InventoryItem item){
        List<Long> organizationList = orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
        Long currentOwnerId= item.getCurrentOwner()!=null ? item.getCurrentOwner().getId() : null;
        Long shipToOrganization = item.getShipTo() != null ? item.getShipTo().getId() : null;
        if(currentOwnerId == getLoggedInUser().getBelongsToOrganization().getId().longValue() || shipToOrganization == getLoggedInUser().getBelongsToOrganization().getId().longValue()) {
            return true;
        }
        else if(getLoggedInUsersDealership().isEnterpriseDealer()
                && (getLoggedInUsersDealership().getChildDealersIds().contains(currentOwnerId) ||getLoggedInUsersDealership().getChildDealersIds().contains(shipToOrganization))){
            return true;
        } else if(organizationList.contains(currentOwnerId) || organizationList.contains(shipToOrganization)){
            return true;
        }
        return false;
    }


    	public ServiceProvider getLoggedInUsersDealership() {
		Organization organization = getLoggedInUser().getBelongsToOrganization();
        return ( organization != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, organization)) ?
        								new HibernateCast<ServiceProvider>().cast(organization) : null;

	}
    	
    	public Money getDeductableAmount(Claim claim)
    	{
    		//To Get series details of applicable policy for which deductible amount should apply
    		ItemReference itemReference=claim.getClaimedItems().get(0).getItemReference();		
    		String seriesName=null;
    		if (!StringUtils.hasText(claim.getCompetitorModelBrand())) { //if claim is a competitor model, as there is no item reference, it should not go the if condition
    			if (itemReference.isSerialized()) {
    				seriesName = itemReference.getUnserializedItem().getProduct()
    						.getName();
    			} else {
    			if(itemReference.getModel()==null)
    					return null;
    				seriesName = itemReference.getModel().getIsPartOf().getName();
    			}
    		}
    		//TO get deductible amount of series of applicable policy
    		Currency baseCurrency = null;
    		List<Money> acceptedTotal = new ArrayList<Money>();
    		Money dealerPreferMoney=null;
    		Money total=null;
    		PolicyDefinition policyDefinition =null;
    		if((claim.getState().equals(ClaimState.DRAFT)||claim.getState().equals(ClaimState.FORWARDED))&&claim.getApplicablePolicy()!=null)
    		{
    			 //policyDefinition=claim.getApplicablePolicy().getPolicyDefinition();
    			if(claim.getPolicyCode()!=null)
    			{
    				policyDefinition=policyDefinitionRepository.findPolicyDefinitionByCode(claim.getPolicyCode());
    			}
    			else
    			{
    				policyDefinition=claim.getApplicablePolicy().getPolicyDefinition();
    			}
    		}
    		else
    		{
    		 policyDefinition  = policyDefinitionRepository.findPolicyDefinitionByCode(claim.getPolicyCode());
    		}		
    		if(WarrantyType.POLICY.getType().equalsIgnoreCase(claim.getPolicyCode())||"Stock".equalsIgnoreCase(claim.getPolicyCode())||(policyDefinition!=null&&!policyDefinition.getWarrantyType().getType().equals(WarrantyType.EXTENDED.getType())))
    		{
    			dealerPreferMoney=null;
    		}
    		else
    		{
    		if(policyDefinition!=null)
    		{
    			List <PolicyProductMapping>  policyForProducts= policyDefinition.getAvailability().getProducts();
    			for(PolicyProductMapping policyProductMapping : policyForProducts){			
    				if(policyProductMapping.getProduct().getName().equals(seriesName)&&policyProductMapping.getDeductibleFee()!=null)
    				{
    					acceptedTotal.add(policyProductMapping.getDeductibleFee());
    					baseCurrency=policyProductMapping.getDeductibleFee().breachEncapsulationOfCurrency();
    				}
    			}
    			if(!acceptedTotal.isEmpty())
    			{
    				if(!claim.getCurrencyForCalculation().equals(baseCurrency))
    				{
    				 total=Money.sum(acceptedTotal);
    				dealerPreferMoney=claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(total, claim.getRepairDate(), claim.getCurrencyForCalculation());				
    				}
    				else
    				{
    					dealerPreferMoney=Money.sum(acceptedTotal);
    				}
    			}
    			
    		}
    		else
    		{
    			dealerPreferMoney=null;

    		}
    		}
    		if(claim!=null &&claim.getPayment()!=null)
    			claim.getPayment().setDeductibleAmount(dealerPreferMoney);
    		return dealerPreferMoney;
    	}
    	
    	public StateMandates getStateMandate(Claim claim)
    	{
    		StateMandates stateMandate =null;
    		String StateCode=claim.getServicingLocation().getState();
    		CountryState state=null;
    		if(StateCode!=null)
    		{
    			state = countryStateService.fetchState(StateCode,"US");
    			if(state!=null)
    				stateMandate = stateMandatesService.findActiveByName(state.getState());
    		}
    		if(stateMandate==null)
    		{	
    			Address customerAddress=null;
    			if(claim.getClaimedItems().get(0).getItemReference().isSerialized()){
    				customerAddress=claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getOwnedBy().getAddress();				
    				StateCode=customerAddress.getState();
    				if(StateCode!=null)
    				{
    					state= countryStateService.fetchState(StateCode,"US");
    					if(state!=null)
    						stateMandate = stateMandatesService.findActiveByName(state.getState());
    				}
    			}else{
    				customerAddress=claim.getOwnerInformation();
    				StateCode=customerAddress.getState();
    				if(StateCode!=null)
    				{
    					state = countryStateService.fetchState(StateCode,"US");
    					if(state!=null)
    						stateMandate = stateMandatesService.findActiveByName(state.getState());
    				}
    			}
    		}
    		return stateMandate;
    	}
    	
    	
    	/**
    	 * This will check if late fee business unit configuration is
    	 * enabled. If so, the percentages to be deducted for 61-90 days and
    	 * 91-120 days will be retrieved and set to the claim.
    	 */
    	public void setLateFeeForClaim(Claim claim) {
    		try {
    			claim.setLateFeeEnabledFrom61to90days(getConfigParamService()
    					.getBooleanValue(
    							ConfigName.LATE_FEE_SETUP_61_90_DAYS.getName()));
    			claim.setLateFeeEnabledFrom91to120days(getConfigParamService()
    					.getBooleanValue(
    							ConfigName.LATE_FEE_SETUP_91_120_DAYS.getName()));
    			claim.setLateFeeValueFrom61to90days(getConfigParamService()
    					.getBigDecimalValue(
    							ConfigName.LATE_FEE_VALUE_61_90_DAYS.getName()));
    			claim.setLateFeeValueFrom91to120days(getConfigParamService()
    					.getBigDecimalValue(
    							ConfigName.LATE_FEE_VALUE_91_120_DAYS.getName()));
    		} catch (NoValuesDefinedException e) {
    			logger.error("Exception EX : " + e);
    		}
    	}
}