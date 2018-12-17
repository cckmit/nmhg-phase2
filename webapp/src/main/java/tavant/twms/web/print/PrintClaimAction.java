package tavant.twms.web.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;

import org.springframework.util.StringUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAttributes;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.FailedRuleDetail;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.RuleFailure;
import tavant.twms.domain.claim.payment.AdditionalPaymentType;
import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.domain.claim.payment.IndividualLineItem;
import tavant.twms.domain.claim.payment.LineItem;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.PutOnHoldReason;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.RequestInfoFromUser;
import tavant.twms.domain.complaints.CountryState;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionRepository;
import tavant.twms.domain.policy.PolicyProductMapping;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.domain.stateMandates.StateMandates;
import tavant.twms.domain.stateMandates.StateMandatesService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.opensymphony.xwork2.Preparable;



/**
 * @author mritunjay.kumar
 * 
 */

@SuppressWarnings("serial")
public class PrintClaimAction extends PrintAction implements BUSpecificSectionNames , Preparable {
	private PrintClaimObject printClaimObject = new PrintClaimObject();
	private Long claimId;
	private Claim claim;
	private ClaimAudit claimAudit;
	private ClaimService claimService;
	private CatalogService catalogService;	
	private String dealerOrSp;
	private String formatedFailureDate;
	private String formatedRepairDate;
	private String formatedRepairStartDate;
	private String formatedWarrantyStartDate;
	private String formatedWarrantyEndDate;
	private String formatedfiledOnDate;
	private String formatedPurchaseDate;
	private String formatedInstallationDate;
	private String unserializedItemProduct;
	private Boolean advisor;
	private ConfigParamService configParamService;
    private boolean displayCPFlagOnClaimPgOne;
    private boolean legalDisclaimerAllowed;
    private boolean flatAmountApplied=Boolean.FALSE;
    private String cpAmount;
    private boolean testing;
    private String dateOfdelivery;
    private String unserializedItemGroupCode;
    private String faultCodeDescription;
    private StateMandatesService stateMandatesService;
    private RuleAdministrationService ruleAdministrationService;
	private CountryStateService countryStateService;
    private boolean incidentalsAvaialable;
	private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;
	  private ValidationResults messages;
		private Boolean showClaimAudit = new Boolean(false);
		private String claimState;
		private List<ClaimAttributes> claimSpecificAttributes = new ArrayList<ClaimAttributes>();
		private PolicyDefinitionRepository policyDefinitionRepository;

		
		
  	public String getUnserializedItemGroupCode() {
		return unserializedItemGroupCode;
	}

	public void setUnserializedItemGroupCode(String unserializedItemGroupCode) {
		this.unserializedItemGroupCode = unserializedItemGroupCode;
	}

	public String getDateOfdelivery() {
		return dateOfdelivery;
	}

	public void setDateOfdelivery(String dateOfdelivery) {
		this.dateOfdelivery = dateOfdelivery;
	}

	public String getCpAmount() {
		return cpAmount;
	}

	public String getFormatedRepairStartDate() {
		return formatedRepairStartDate;
	}

	public void setFormatedRepairStartDate(String formatedRepairStartDate) {
		this.formatedRepairStartDate = formatedRepairStartDate;
	}

	public boolean isFlatAmountApplied() {
		return flatAmountApplied;
	}

	public void setFlatAmountApplied(boolean flatAmountApplied) {
		this.flatAmountApplied = flatAmountApplied;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setDisplayCPFlagOnClaimPgOne(boolean displayCPFlagOnClaimPgOne) {
		this.displayCPFlagOnClaimPgOne = displayCPFlagOnClaimPgOne;
	}

	public void setCpAmount(String cpAmount) {
		this.cpAmount = cpAmount;
	}

	public String getWarrantyAmount() {
		return warrantyAmount;
	}

	public void setWarrantyAmount(String warrantyAmount) {
		this.warrantyAmount = warrantyAmount;
	}

	private String warrantyAmount;
    
	private List<HussmannPartReplacedInstalledDTO> hussmannPartReplacedInstalledDTOList
				= new ArrayList<HussmannPartReplacedInstalledDTO>();
	
	
	public String getDealerOrSp() {
		return dealerOrSp;
	}

	public void setDealerOrSp(String dealerOrSp) {
		this.dealerOrSp = dealerOrSp;
	}

	public PrintClaimObject getPrintClaimObject() {
		return printClaimObject;
	}

	public void setPrintClaimObject(PrintClaimObject printClaimObject) {
		this.printClaimObject = printClaimObject;
	}

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}
	
	 public boolean getDisplayCPFlagOnClaimPgOne() {
			return configParamService.getBooleanValue(ConfigName.COMMERCIAL_POLICY_CLAIM_PAGE.getName());
		}
	 
	
	 protected boolean isstateMondateEnabled(Claim claim)
		{
			claim.setStateMandate(getStateMandate(claim));
			if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	   public boolean isLegalDisclaimerAllowed(){
	        return getConfigParamService().getBooleanValue(ConfigName.LEGAL_DISCLAIMER_ALLOWED.getName());
	     }

		public void setLegalDisclaimerAllowed(boolean legalDisclaimerAllowed) {
			this.legalDisclaimerAllowed = legalDisclaimerAllowed;
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
			 policyDefinition=claim.getApplicablePolicy().getPolicyDefinition();
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
	public void prepare() throws Exception {
		if (claim == null) {
			claim = claimService.findClaim(claimId);
			
			printClaimObject.setClaim(claim);
		} else {
			printClaimObject.setClaim(claim);
			
			setReasonsList(claim);
            if(claim.getClaimAdditionalAttributes().size()>0 && claimSpecificAttributes.isEmpty()){
            	for(ClaimAttributes  attributes : claim.getClaimAdditionalAttributes()){
            		AdditionalAttributes additionalAttributes = attributes.getAttributes();
            		attributes.setName(additionalAttributes.getName());
            		if(additionalAttributes.getName()!=null)
            		attributes.setAttrValue(attributes.getAttrValue());
            		claimSpecificAttributes.add(attributes);
            		
            	}
            
            	
            }
			printClaimObject.setBuTitle(claim.getBusinessUnitInfo().getName());
			if (getShowClaimAudit()) { // if request is Claim Audit history
				// opening
							this.claimAudit = this.claimService.findClaimAudit(claim.getId());
							if (this.claimAudit != null) {
							this.claim = this.claimAudit.getForClaim();
							}
							} else {
							this.claim = claimService
							.findClaimWithServiceInfoAttributes(Long.valueOf(claim.getId()));
							}
		}
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(printClaimObject
				.getClaim().getBusinessUnitInfo().getName());
			
	}
	private void findTotal(String lineItemGroup,Payment dealerPayment,String totalLineItemGroupName,List<ClaimPaymentObject> paymentObjs) {
		LineItemGroup claimAmountGrp = new LineItemGroup();
		if(claim.getPayment() != null){
			
			 claimAmountGrp = claim.getPayment().getLineItemGroup(lineItemGroup);
			    if(claimAmountGrp !=null && dealerPayment!=null && !dealerPayment.getLineItemGroups().isEmpty() && dealerPayment.getLineItemGroup(claimAmountGrp.getName()) != null)
		         {
		         	  ClaimPaymentObject paymentObjectTwo = new ClaimPaymentObject("Accepted "+ totalLineItemGroupName,
		                       dealerPayment.getLineItemGroup(claimAmountGrp.getName()).getAcceptedTotal().breachEncapsulationOfAmount(),
		                       claimAmountGrp.getAcceptedTotal().breachEncapsulationOfAmount(),
		                       claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP) != null
		                           ? claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP).breachEncapsulationOfAmount()
		                           : new BigDecimal(0.0), Boolean.TRUE, Boolean.TRUE,isCPAdvisorEnabled());
			                    	  paymentObjectTwo.setAcceptedQtyHrs(claimAmountGrp.getAcceptedQtyHrs()!=null ?claimAmountGrp.getAcceptedQtyHrs() : null);
			                    	  paymentObjectTwo.setAskedQtyHrs(claimAmountGrp.getAskedQtyHrs()!=null ?claimAmountGrp.getAskedQtyHrs() : null );
			                    	  paymentObjectTwo.setPercentageAcceptance(claimAmountGrp.getPercentageAcceptance()!=null ?claimAmountGrp.getPercentageAcceptance().toString() : null);
			                    	  paymentObjectTwo.setCurrencyCode(claimAmountGrp.getAcceptedTotal().breachEncapsulationOfCurrency().getCurrencyCode()!=null ?claimAmountGrp.getAcceptedTotal().breachEncapsulationOfCurrency().getCurrencyCode() :null );
			                    	  paymentObjectTwo.setReviwedAmount(claimAmountGrp.getAcceptedTotal()!=null ?
			                    			  claimAmountGrp.getAcceptedTotal().breachEncapsulationOfAmount() : 
			                    				  null);
			                       	if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy())
			            			{
			                       		paymentObjectTwo.setStateMondateAmount(claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount()!=null ?
			                       				claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount() :
			                       					null);
			            			}
			                        if(paymentObjectTwo.getClaimedValue()==null)
			                        {
			                        	paymentObjectTwo.setClaimedValueDisplay(Boolean.FALSE);
			                        }else{
			                        	paymentObjectTwo.setClaimedValueDisplay(Boolean.TRUE);
			                        }
			                          paymentObjs.add(paymentObjectTwo);
		             
		         }
			  }
	 }
	

	
	private void setReasonsList(Claim claim) {
		List<String> reasons = new ArrayList<String>();

		claim.getClaimAdditionalAttributes();
		List<PutOnHoldReason> holdReasons =claim.getActiveClaimAudit().getPutOnHoldReasons(); 
		Iterator<PutOnHoldReason> resonItr = holdReasons.iterator();	
		
		while(resonItr.hasNext()){
			PutOnHoldReason putOnHoldReason = resonItr.next();
			claimState="label.common.putOnHold";	
			reasons.add(putOnHoldReason.getDescription());
		}
		List<RejectionReason> rejectionReasons = claim.getActiveClaimAudit().getRejectionReasons();
		Iterator<RejectionReason> resonItr1 =rejectionReasons.iterator();
		while(resonItr1.hasNext()){
			RejectionReason rejectionReason = resonItr1.next();
			reasons.add(rejectionReason.getDescription());
			claimState="label.common.denyReasons";
		}
		List<RequestInfoFromUser> fromUser = claim.getActiveClaimAudit().getRequestInfoFromUser();
		Iterator<RequestInfoFromUser> resonItr2 =fromUser.iterator();
		while(resonItr2.hasNext()){
			RequestInfoFromUser user =resonItr2.next(); 
			reasons.add(user.getDescription());
			claimState="label.common.reqInfoFromDealer";	
		}
		StringBuilder s = new StringBuilder();
		if(!reasons.isEmpty()){
			s.append(getText(claimState));
			s.append(": ");		
		}
		for(String str :reasons ){
			s.append(str+"\n");
			s.append("            ");
		}
		printClaimObject.setReasonsList(s.toString());
	}

	
	public void printPaymentForAMER()
	{
		User loggedInUser = getLoggedInUser();
		List<LineItemGroup> lineItemGroups = new ArrayList<LineItemGroup>();
		 printClaimObject.setNewPaymentSectionVisible(Boolean.TRUE);
		 if(claim.getItemDutyConfig()||claim.getMealsConfig()||claim.getPerDiemConfig()
	    			||claim.getParkingConfig()||claim.getRentalChargesConfig()||claim.getLocalPurchaseConfig()
	    			||claim.getTollsConfig()||claim.getOtherFreightDutyConfig()||claim.getOthersConfig() || claim.getHandlingFeeConfig() || claim.getTransportation()){
	    		this.incidentalsAvaialable=true;
	    	}

		 if(claim.getPayment() != null){
					lineItemGroups = claim.getPayment().getLineItemGroups();	
		}
		List<ClaimPaymentObject> paymentObjs = new ArrayList<ClaimPaymentObject>();
		boolean showPercentageAccepted = isShowPercentageAccepted(claim);
		if((claim.getCompetitorModelBrand()!=null && !claim.getCompetitorModelBrand().isEmpty() && !claim.getCompetitorModelDescription().isEmpty() && !claim.getCompetitorModelTruckSerialnumber().isEmpty()))
        {
			printClaimObject.setPartInstalledOn("Part installed on competitor model");
                
        }
		else if(claim.getClaimedItems().get(0).getItemReference().isSerialized())
			{
				printClaimObject.setPartInstalledOn("Part installed on serialized host Unit");
			}
			else if(claim.getClaimedItems().get(0).getItemReference().isSerialized())
			{
				printClaimObject.setPartInstalledOn("Part installed on non-serialized host Unit");
			}
			else
			{
				printClaimObject.setPartInstalledOn("Sold Alone Part Not Installed");
			}
		
		boolean showSateMondateEnabled = isstateMondateEnabled(claim);
     	Money deductable=getDeductableAmount(claim);		
		Payment dealerPayment = claim.getPaymentForDealerAudit();
		int travelCounter=0;
		int travelCounterOne=0;
		int othersCounter=0;
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if (lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL_BY_TRIP)||
					lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL_BY_HOURS)||
					lineItemGroup.getName().equalsIgnoreCase(Section.ADDITIONAL_TRAVEL_HOURS))
		         	{
				          travelCounterOne++;
		         	}
		}
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if (!lineItemGroup.getName().equalsIgnoreCase(Section.TOTAL_CLAIM))
			{
			
				if (lineItemGroup.getName().equalsIgnoreCase(Section.OEM_PARTS))
				{

					   populatePaymentData(paymentObjs, lineItemGroup);
					    String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(lineItemGroup.getName()));
					    findTotal(Section.OEM_PARTS, dealerPayment, totalLineItemGroupName, paymentObjs);
					  
				}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.NON_OEM_PARTS))
				{
				    	populatePaymentData(paymentObjs, lineItemGroup);
					   String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(lineItemGroup.getName()));
					   findTotal(lineItemGroup.getName(), dealerPayment, totalLineItemGroupName, paymentObjs);
					  
				}
				
				if (lineItemGroup.getName().equalsIgnoreCase(Section.LABOR))
				{

				    	populatePaymentData(paymentObjs, lineItemGroup);
						if((claim.getPaymentForDealerAudit().getLineItemGroup(lineItemGroup.getName())!=null ?
								claim.getPaymentForDealerAudit().getLineItemGroup(lineItemGroup.getName()).getRate() :
									null)!=null)
						{
							ClaimPaymentObject paymentObjectTwo = new ClaimPaymentObject("Rate:",Boolean.FALSE,Boolean.FALSE);
							if (dealerPayment != null && !dealerPayment.getLineItemGroups().isEmpty() && dealerPayment.getLineItemGroup(lineItemGroup.getName()) != null 
					        		&&lineItemGroup.getIndividualLineItems().isEmpty())
					        {
								paymentObjectTwo.setClaimedValue(dealerPayment.getLineItemGroup(lineItemGroup.getName())
					            		.getRate().breachEncapsulationOfAmount());
								paymentObjectTwo.setCurrencyCode(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getPercentageAcceptance()+"%="+dealerPayment.getLineItemGroup(lineItemGroup.getName())
										.getRate().breachEncapsulationOfCurrency().getCurrencyCode());
					           
					        }
							paymentObjectTwo.setRateValueDisplay(Boolean.TRUE);
							paymentObjectTwo.setClaimedValueDisplay(Boolean.FALSE);
							paymentObjectTwo.setRateClaimedValue(lineItemGroup.getPercentageApplicable()+"%="+lineItemGroup.getRate().breachEncapsulationOfCurrency()+lineItemGroup.getRate().breachEncapsulationOfAmount());
							paymentObjectTwo.setRateValue(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getPercentageAcceptance()+"%="+dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfCurrency().getCurrencyCode()+dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfAmount());
							paymentObjectTwo.setReviwedAmount(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate()!=null ?
					       			dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfAmount() : 
				          				  null);
						    if (lineItemGroup != null && lineItemGroup.getBaseAmount() != null) 
						     {
								paymentObjectTwo.setValue(lineItemGroup.getRate().breachEncapsulationOfAmount());
							
						     }
						    if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy()){

						     paymentObjectTwo.setStateMondateRateValue((lineItemGroup.getStateMandateRatePercentage()!=null ?lineItemGroup.getStateMandateRatePercentage().toString()+"%=":null) + 
						    		 (lineItemGroup.getStateMandateRate()!=null ?lineItemGroup.getStateMandateRate().breachEncapsulationOfCurrency().getCurrencyCode() +  lineItemGroup.getStateMandateRate().breachEncapsulationOfAmount() :null));
						    }
							paymentObjs.add(paymentObjectTwo);
					   }
				
					   String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(Section.LABOR));
					   findTotal(Section.LABOR, dealerPayment, totalLineItemGroupName, paymentObjs);
					  
				}
				if (claim.getPaymentForDealerAudit().getLineItemGroup(
						Section.TRAVEL_BY_DISTANCE) != null) {
					if (lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL_BY_DISTANCE))
					{
						
					        populatePaymentData(paymentObjs, lineItemGroup);
							String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(Section.TRAVEL_BY_DISTANCE));
					    	findTotal(Section.TRAVEL_BY_DISTANCE, dealerPayment, totalLineItemGroupName, paymentObjs);
					}
				}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL_BY_TRIP)||
						lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL_BY_HOURS)||
						lineItemGroup.getName().equalsIgnoreCase(Section.ADDITIONAL_TRAVEL_HOURS))
			         	{
							if(travelCounter==0)
							{
								ClaimPaymentObject paymentObjectOne = new ClaimPaymentObject(getText(NAMES_AND_KEY.get(Section.TRAVEL)),
						                 Boolean.FALSE,
						                 Boolean.FALSE);
								 paymentObjs.add(paymentObjectOne);
								 populatePaymentData(paymentObjs, lineItemGroup);
								 travelCounter++;
						   }
							else
							{
								populatePaymentData(paymentObjs, lineItemGroup);
							  travelCounter++;
							}
							if(travelCounter==travelCounterOne)
							{
								if((claim.getPaymentForDealerAudit().getLineItemGroup(lineItemGroup.getName())!=null ?
										claim.getPaymentForDealerAudit().getLineItemGroup(lineItemGroup.getName()).getRate() :
											null)!=null)
								{
										ClaimPaymentObject paymentObjectTwo = new ClaimPaymentObject("Rate:",Boolean.FALSE,Boolean.FALSE);
										if (dealerPayment != null && !dealerPayment.getLineItemGroups().isEmpty() && dealerPayment.getLineItemGroup(lineItemGroup.getName()) != null 
								        		&&lineItemGroup.getIndividualLineItems().isEmpty())
								        {
											paymentObjectTwo.setClaimedValue(dealerPayment.getLineItemGroup(lineItemGroup.getName())
								            		.getRate().breachEncapsulationOfAmount());
											paymentObjectTwo.setCurrencyCode("@"+dealerPayment.getLineItemGroup(lineItemGroup.getName())
													.getRate().breachEncapsulationOfCurrency().getCurrencyCode());
								           
								        }
										paymentObjectTwo.setClaimedValue(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfAmount());
										paymentObjectTwo.setCurrencyCode("@"+dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfCurrency().getCurrencyCode());
										paymentObjectTwo.setReviwedAmount(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate()!=null ?
								       			dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfAmount() : 
							          				  null);
									    if (lineItemGroup != null && lineItemGroup.getBaseAmount() != null) 
									     {
											paymentObjectTwo.setValue(lineItemGroup.getRate().breachEncapsulationOfAmount());
									     }
									    if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy()){
									     paymentObjectTwo.setStateMandateRatePercentage(lineItemGroup.getStateMandateRatePercentage()!=null ?
									    		 lineItemGroup.getStateMandateRatePercentage():
									    			 null);
									     paymentObjectTwo.setStateMondateAmount(lineItemGroup.getStateMandateRate()!=null ?
									    		 lineItemGroup.getStateMandateRate().breachEncapsulationOfAmount() :
									    			 null);
									    }
										paymentObjs.add(paymentObjectTwo);
									}
							    	String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(Section.TRAVEL));
							    	findTotal(Section.TRAVEL, dealerPayment, totalLineItemGroupName, paymentObjs);
							
								
					  }
			       }
				if (lineItemGroup.getName().equalsIgnoreCase(Section.HANDLING_FEE)||
						lineItemGroup.getName().equalsIgnoreCase(Section.ITEM_FREIGHT_DUTY)||
						lineItemGroup.getName().equalsIgnoreCase(Section.TRANSPORTATION_COST))
				    {
						if(othersCounter==0)
						{
							ClaimPaymentObject paymentObjectOne = new ClaimPaymentObject(getText(NAMES_AND_KEY.get(Section.OTHERS)),
					                 Boolean.FALSE,
					                 Boolean.TRUE);
							 paymentObjs.add(paymentObjectOne);
							 populatePaymentData(paymentObjs, lineItemGroup);
							 othersCounter++;
							
					}
					else
					{
						populatePaymentData(paymentObjs, lineItemGroup);
						othersCounter++;
					}
					
				}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.DEDUCTIBLE))
				{
			   
					if(isInternalUser(getLoggedInUser()))
				 populatePaymentData(paymentObjs, lineItemGroup);
				}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.LATE_FEE))
				{
					 populatePaymentData(paymentObjs, lineItemGroup);
				}
		
				
				   
            }
        }
		LineItemGroup claimAmountGrp = new LineItemGroup();
		if(claim.getPayment() != null)
		 claimAmountGrp = claim.getPayment().getLineItemGroup(Section.TOTAL_CLAIM);
		String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(claimAmountGrp.getName()));
		if (claimAmountGrp!=null && dealerPayment != null && !dealerPayment.getLineItemGroups().isEmpty() && dealerPayment.getLineItemGroup(claimAmountGrp.getName()) != null)
		{
			  ClaimPaymentObject paymentObjectOne = new ClaimPaymentObject(totalLineItemGroupName,
						dealerPayment.getLineItemGroup(claimAmountGrp.getName()).getGroupTotal().breachEncapsulationOfAmount(),
						claimAmountGrp.getGroupTotal().breachEncapsulationOfAmount(),
						claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP) != null
	                    ? claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP).breachEncapsulationOfAmount()
	                    : new BigDecimal(0.0), Boolean.TRUE, Boolean.TRUE,isCPAdvisorEnabled());
				paymentObjectOne.setAcceptedQtyHrs(claimAmountGrp.getAcceptedQtyHrs()!=null ?
						claimAmountGrp.getAcceptedQtyHrs() :
							null);
	       	    paymentObjectOne.setAskedQtyHrs(claimAmountGrp.getAskedQtyHrs()!=null ?
	       	    		claimAmountGrp.getAskedQtyHrs() :
	       	    			null);
	       	    paymentObjectOne.setPercentageAcceptance(claimAmountGrp.getPercentageAcceptance()!=null ?
	       	    		claimAmountGrp.getPercentageAcceptance().toString() :
	       	    			null);
	       	    paymentObjectOne.setCurrencyCode(claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode()!=null ?
	       	    		claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode() :
	       	    			null);
	       	 paymentObjectOne.setReviwedAmount(claimAmountGrp.getAcceptedTotal()!=null ?
          			  claimAmountGrp.getAcceptedTotal().breachEncapsulationOfAmount() : 
          				  null);
	       	 if(claim.getPayment().getTotalAcceptanceChkbox()){
	       		paymentObjectOne.setTotalAcceptanceChkbox(Boolean.TRUE);
	       	 }
	         
	       		if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy()){
	       			paymentObjectOne.setStateMondateAmount(claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount()!=null ?
	       					claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount() :
	       						null);
	       			if(claim.getPayment().getTotalAcceptStateMdtChkbox())
	       			{
	       				paymentObjectOne.setTotalAcceptStateMdtChkbox(Boolean.TRUE);
	       			}
	       		}
	       		paymentObjectOne.setTotalClaimAmountCheckBox("Total Claim Amount");
	       	   paymentObjs.add(paymentObjectOne);
		}
		else
		{
			  ClaimPaymentObject paymentObjectTwo = new ClaimPaymentObject(totalLineItemGroupName,
						null,
						claimAmountGrp.getGroupTotal() != null ? claimAmountGrp.getGroupTotal().breachEncapsulationOfAmount() : null,
						claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP) != null
	                    ? claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP).breachEncapsulationOfAmount()
	                    : new BigDecimal(0.0), Boolean.TRUE, Boolean.TRUE,isCPAdvisorEnabled());
			  paymentObjectTwo.setAcceptedQtyHrs(claimAmountGrp.getAcceptedQtyHrs()!=null ?
					  claimAmountGrp.getAcceptedQtyHrs() :
						  null);
			  paymentObjectTwo.setAskedQtyHrs(claimAmountGrp.getAskedQtyHrs()!=null ?
					  claimAmountGrp.getAskedQtyHrs() :
						  null);
			  paymentObjectTwo.setPercentageAcceptance(claimAmountGrp.getPercentageAcceptance()!=null ?
					  claimAmountGrp.getPercentageAcceptance().toString() :
						  null);
	       	    paymentObjectTwo.setCurrencyCode(claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode()!=null ?
	       	    		claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode() :
	       	    			null);
	       	 paymentObjectTwo.setReviwedAmount(claimAmountGrp.getAcceptedTotal()!=null ?
	          			  claimAmountGrp.getAcceptedTotal().breachEncapsulationOfAmount() : 
	          				  null);
	       	 if(claim.getPayment().getTotalAcceptanceChkbox()){
	       		paymentObjectTwo.setTotalAcceptanceChkbox(Boolean.TRUE);
		       	 }
	     		if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy()){
	     			paymentObjectTwo.setStateMondateAmount(claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount()!=null ?
	     					claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount():
	     						null);
	       			if(claim.getPayment().getTotalAcceptStateMdtChkbox())
	       			{
	       				paymentObjectTwo.setTotalAcceptStateMdtChkbox(Boolean.TRUE);
	       			}
	       		}
	        	 paymentObjs.add(paymentObjectTwo);
		}
		if(claim.getPayment().getTotalAcceptanceChkbox()){
		    printClaimObject.setWarrantyAmount(claimAmountGrp.getAcceptedTotal()!=null ?
		    		claimAmountGrp.getAcceptedTotal().breachEncapsulationOfAmount().toString() :
					null);
		}
		if(claim.getPayment().getTotalAcceptStateMdtChkbox()){
		    printClaimObject.setWarrantyAmount(claimAmountGrp.getGroupTotalStateMandateAmount()!=null ?
		    		claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount().toString() :
					null);
		}


		printClaimObject.setCpAmount(claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP)!=null ?
				claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP).breachEncapsulationOfAmount().toString() :
					null);
		
		printClaimObject.setInternalUser(isInternalUser(loggedInUser));
		printClaimObject.setShowPaymentInfo(isShowPaymentInfo(loggedInUser,
				claim.getForDealer().getId()));
		printClaimObject.setShowPercentageAccepted(showPercentageAccepted);
		printClaimObject.setStateMondateEnabled(showSateMondateEnabled);
		
		printClaimObject.setPayment(paymentObjs);
		 List<ManageDocumentObject>  documentObjects = new ArrayList<ManageDocumentObject>();
		 List<Document> documents = claim.getActiveClaimAudit().getAttachments()!=null  ?claim.getActiveClaimAudit().getAttachments() : null;
		 for(Document document : documents){
			 ManageDocumentObject doc= new ManageDocumentObject();
			 doc.setName(document.getFileName());
			 doc.setDescription(document.getDocumentType()!=null ? document.getDocumentType().getDescription()!=null ?document.getDocumentType().getDescription().toUpperCase():null:null);
			 documentObjects.add(doc);
		 }
		 printClaimObject.setDocumentObjects(documentObjects) ;
		 SortedSet<RuleFailure> ruleFailures =  claim.getActiveClaimAudit().getRuleFailures()!=null ? claim.getActiveClaimAudit().getRuleFailures() : null;
		 List<WaraningMessageObject>  warningMessage = new ArrayList<WaraningMessageObject>();
		 for(RuleFailure failure :ruleFailures){
			 List<FailedRuleDetail> failedRules = failure.getFailedRules();
			 for(FailedRuleDetail failedRule :failedRules){
				 WaraningMessageObject messageObject = new WaraningMessageObject();
				 messageObject.setRuleNumber(failedRule.getRuleNumber());
				 messageObject.setRuleMsg(failedRule.getRuleMsg());
				 messageObject.setDefaultRuleMsgInUS(failedRule.getDefaultRuleMsgInUS()!=null ?
						 failedRule.getDefaultRuleMsgInUS() : null);
				 messageObject.setRuleAction(failedRule.getRuleAction()!=null ?
						 failedRule.getRuleAction() : null );
				 warningMessage.add(messageObject);
			 }
		 }
		 if(isInternalUser(loggedInUser))
		 {
			 printClaimObject.setWarningMessages(warningMessage);
		 }
		 else
		 {
			 messages = ruleAdministrationService.executeClaimEntryValidationRules(claim)!=null ?
					 ruleAdministrationService.executeClaimEntryValidationRules(claim) :
						 null;
					 List<String> warnings = messages.getWarnings();
					 List<WaraningMessageObject> result = new ArrayList<WaraningMessageObject>();
						for (String warning : warnings) {
							if (org.springframework.util.StringUtils.hasText(warning))
							{
								 WaraningMessageObject messageObject = new WaraningMessageObject();
								int pos1 = warning.indexOf(")", 1);
								int pos2 = warning.indexOf("[");
								warning = warning.substring(pos1 + 1, pos2);
								 messageObject.setRuleMsg(warning);
								result.add(messageObject);
							}
						}
			 printClaimObject.setWarningMessages(result);
		 }
		   String stdr[]= {claim.getFiledBy().getCompleteName()};
			   Locale.setDefault(getLoggedInUser().getLocale());
			   String mess = getText("message.legalDisclaimer",stdr);
			   String[] arr =mess.split("<BR></BR>");
			   String disclaimerInfo="";
			   for(int i =0;i<arr.length;i++){
				   disclaimerInfo = disclaimerInfo + arr[i]+"\n\n\n";
			   }
				
			   printClaimObject.setDisclaimerInfo(disclaimerInfo);
			  
			   if (!claim.getServiceInformation().getServiceDetail().getLaborPerformed().isEmpty())
			   {
	                      List<LaborDetail> laborDetails=claim.getServiceInformation().getServiceDetail().getLaborPerformed();
	                      List<JobCodes>  jobCodes = new ArrayList<JobCodes>();
					      for(LaborDetail laborDetail : laborDetails){
						   JobCodes jobCode = new JobCodes();
						   jobCode.setCode(laborDetail.getServiceProcedure()!=null ? laborDetail.getServiceProcedure().getDefinition().getCode():null);
						   jobCode.setDescription(laborDetail.getServiceProcedure()!=null ?(laborDetail.getServiceProcedure().getDefinedFor()!=null ? 
								   laborDetail.getServiceProcedure().getDefinedFor().getJobCodeDescription() :null):null);
						   jobCode.setStdLabHours(laborDetail.getLaborHrsEntered()!=null ? laborDetail.getLaborHrsEntered().toString() : null);
						   jobCode.setAddLabHours(laborDetail.getAdditionalLaborHours()!=null ? laborDetail.getAdditionalLaborHours().toString() : null);
						   jobCode.setReasonForAdditionalHours(laborDetail.getReasonForAdditionalHours());
						   jobCode.setHoursSpent(laborDetail.getHoursSpent());
						   jobCode.setLaborHrsEntered(laborDetail.getLaborHrsEntered());
						   jobCodes.add(jobCode);
						   
					   }
					      printClaimObject.setCodes(jobCodes);
				 
			  }
			   if(claim.getItemDutyConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getItemFreightAndDuty()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getItemFreightAndDuty().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getItemFreightAndDuty().breachEncapsulationOfAmount().toString();
					   String fileName=  claim.getServiceInformation().getServiceDetail().getFreightDutyInvoice()!=null ?  claim.getServiceInformation().getServiceDetail().getFreightDutyInvoice().getFileName():null;
					   printClaimObject.setItemFreightDuty(currencyCodePrice);
					   printClaimObject.setItemFreightDutyFileName(fileName);
					   
				   }
			   }
			   if(claim.getMealsConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getMealsExpense()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getMealsExpense().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getMealsExpense().breachEncapsulationOfAmount().toString();
					   String fileName=  claim.getServiceInformation().getServiceDetail().getMealsInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getMealsInvoice().getFileName() : null;
					   printClaimObject.setMealsExpense(currencyCodePrice);
					   printClaimObject.setMealsExpenseFileName(fileName);  
				   }
			   }
			   if(claim.getPerDiemConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getPerDiem()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getPerDiem().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getPerDiem().breachEncapsulationOfAmount().toString();
					   String fileName=  claim.getServiceInformation().getServiceDetail().getPerDiemInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getPerDiemInvoice().getFileName() : null;
					   printClaimObject.setPerDiem(currencyCodePrice);
					   printClaimObject.setPerDiemFileName(fileName);  
				   }
			   }
			   if(claim.getParkingConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getParkingAndTollExpense()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getParkingAndTollExpense().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getParkingAndTollExpense().breachEncapsulationOfAmount().toString();
					   String fileName=  claim.getServiceInformation().getServiceDetail().getParkingAndTollInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getParkingAndTollInvoice().getFileName() : null;
					   printClaimObject.setParkingAndTollExpense(currencyCodePrice);
					   printClaimObject.setParkingAndTollExpenseFileName(fileName);  
				   }
			   }
			   if(claim.getRentalChargesConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getRentalCharges()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getRentalCharges().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getRentalCharges().breachEncapsulationOfAmount().toString();
					   String fileName=  claim.getServiceInformation().getServiceDetail().getRentalChargesInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getRentalChargesInvoice().getFileName(): null;
					   printClaimObject.setRentalCharges(currencyCodePrice);
					   printClaimObject.setRentalChargesFileName(fileName);  
				   }
			   }
			   if(claim.getLocalPurchaseConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getLocalPurchaseExpense()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getLocalPurchaseExpense().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getLocalPurchaseExpense().breachEncapsulationOfAmount().toString();
					   String fileName=  claim.getServiceInformation().getServiceDetail().getLocalPurchaseInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getLocalPurchaseInvoice().getFileName() : null;
					   printClaimObject.setLocalPurchaseExpense(currencyCodePrice);
					   printClaimObject.setLocalPurchaseExpenseFileName(fileName);  
				   }
			   }
			   if(claim.getTollsConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getTollsExpense()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getTollsExpense().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getTollsExpense().breachEncapsulationOfAmount().toString();
					   String fileName= claim.getServiceInformation().getServiceDetail().getTollsInvoice().getFileName();
					   printClaimObject.setTollsExpense(currencyCodePrice);
					   printClaimObject.setTollsExpenseFileName(fileName);  
				   }
			   }
			   if(claim.getOtherFreightDutyConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getOtherFreightDutyExpense()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getOtherFreightDutyExpense().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getOtherFreightDutyExpense().breachEncapsulationOfAmount().toString();
					   String fileName= claim.getServiceInformation().getServiceDetail().getOtherFreightDutyInvoice() != null ?  claim.getServiceInformation().getServiceDetail().getOtherFreightDutyInvoice().getFileName() : null;
					   printClaimObject.setOtherFreightDutyExpense(currencyCodePrice);
					   printClaimObject.setOtherFreightDutyExpenseFileName(fileName);   
				   }
			   }
			   if(claim.getOtherFreightDutyConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getOtherFreightDutyExpense()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getOtherFreightDutyExpense().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getOtherFreightDutyExpense().breachEncapsulationOfAmount().toString();
					   String fileName= claim.getServiceInformation().getServiceDetail().getOtherFreightDutyInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getOtherFreightDutyInvoice().getFileName() : null;
					   printClaimObject.setOtherFreightDutyExpense(currencyCodePrice);
					   printClaimObject.setOtherFreightDutyExpenseFileName(fileName);   
				   }
			   }
			   if(claim.getTransportation()){
				   if(claim.getServiceInformation().getServiceDetail().getTransportationAmt()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getTransportationAmt().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getTransportationAmt().breachEncapsulationOfAmount().toString();
					   String fileName= claim.getServiceInformation().getServiceDetail().getTransportationInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getTransportationInvoice().getFileName() : null;
					   printClaimObject.setTransportationAmt(currencyCodePrice);
					   printClaimObject.setTransportationAmtFileName(fileName);    
				   }
			   }
			   
			   if(claim.getHandlingFeeConfig()){
				   if(claim.getServiceInformation().getServiceDetail().getHandlingFee()!=null)
				   {
					   String currencyCodePrice = claim.getServiceInformation().getServiceDetail().getHandlingFee().breachEncapsulationOfCurrency().toString()+claim.getServiceInformation().getServiceDetail().getHandlingFee().breachEncapsulationOfAmount().toString();
					   String fileName=claim.getServiceInformation().getServiceDetail().getHandlingFeeInvoice()!=null ? claim.getServiceInformation().getServiceDetail().getHandlingFeeInvoice().getFileName() : null;
					   printClaimObject.setHandlingFee(currencyCodePrice);
					   printClaimObject.setHandlingFeeFileName(fileName);   
				   }
			   }
			   
	}
	public void printPaymentForEMEA()
	{
		User loggedInUser = getLoggedInUser();
		List<LineItemGroup> lineItemGroups = new ArrayList<LineItemGroup>();
		if(claim.getPayment() != null){
			if(claim.getPayment().getLineItemGroup("Claim Amount").getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)!=null){
			cpAmount= claim.getPayment().getLineItemGroup("Claim Amount").getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_WNTY).toString();
			}
			if(claim.getPayment().getLineItemGroup("Claim Amount").getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP)!=null){
		    warrantyAmount=claim.getPayment().getLineItemGroup("Claim Amount").getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP).toString();
			}
			lineItemGroups = claim.getPayment().getLineItemGroups();	
		}
		List<ClaimPaymentObject> paymentObjs = new ArrayList<ClaimPaymentObject>();
		boolean showPercentageAccepted = isShowPercentageAccepted(claim);
		Payment dealerPayment = claim.getPaymentForDealerAudit();
		Money deductable=getDeductableAmount(claim);		
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if (!lineItemGroup.getName().equalsIgnoreCase(Section.TOTAL_CLAIM)) {
				if (lineItemGroup.getName().equalsIgnoreCase(Section.OEM_PARTS)){
					populatePaymentData(paymentObjs, lineItemGroup);
					    String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(lineItemGroup.getName()));
					    findTotal(Section.OEM_PARTS, dealerPayment, totalLineItemGroupName, paymentObjs);
				}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.NON_OEM_PARTS)){
					populatePaymentData(paymentObjs, lineItemGroup);
					   String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(lineItemGroup.getName()));
					   findTotal(lineItemGroup.getName(), dealerPayment, totalLineItemGroupName, paymentObjs);
				}
				
				if (lineItemGroup.getName().equalsIgnoreCase(Section.LABOR)){
					populatePaymentData(paymentObjs, lineItemGroup);
						if((claim.getPaymentForDealerAudit().getLineItemGroup(lineItemGroup.getName())!=null ?
								claim.getPaymentForDealerAudit().getLineItemGroup(lineItemGroup.getName()).getRate() :
									null)!=null)
						{
							ClaimPaymentObject paymentObjectTwo = new ClaimPaymentObject("Rate:",Boolean.FALSE,Boolean.FALSE);
							if (dealerPayment != null && !dealerPayment.getLineItemGroups().isEmpty() && dealerPayment.getLineItemGroup(lineItemGroup.getName()) != null 
					        		&&lineItemGroup.getIndividualLineItems().isEmpty())
					        {
								paymentObjectTwo.setClaimedValue(dealerPayment.getLineItemGroup(lineItemGroup.getName())
					            		.getRate().breachEncapsulationOfAmount());
								paymentObjectTwo.setCurrencyCode("@"+dealerPayment.getLineItemGroup(lineItemGroup.getName())
										.getRate().breachEncapsulationOfCurrency().getCurrencyCode());
					           
					        }
							paymentObjectTwo.setClaimedValue(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfAmount());
							paymentObjectTwo.setCurrencyCode("@"+dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfCurrency().getCurrencyCode());
							paymentObjectTwo.setReviwedAmount(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate()!=null ?
					       			dealerPayment.getLineItemGroup(lineItemGroup.getName()).getRate().breachEncapsulationOfAmount() : 
				          				  null);
						    if (lineItemGroup != null && lineItemGroup.getBaseAmount() != null) 
						     {
								paymentObjectTwo.setValue(lineItemGroup.getRate().breachEncapsulationOfAmount());
						     }
						    
							paymentObjs.add(paymentObjectTwo);
					   }
				
					   String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(Section.LABOR));
					   findTotal(Section.LABOR, dealerPayment, totalLineItemGroupName, paymentObjs);
				}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL_BY_TRIP)||
						lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL_BY_HOURS)||
						lineItemGroup.getName().equalsIgnoreCase(Section.ADDITIONAL_TRAVEL_HOURS))
			         	{
				              	populatePaymentData(paymentObjs, lineItemGroup);
			         	}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.HANDLING_FEE)||
						lineItemGroup.getName().equalsIgnoreCase(Section.ITEM_FREIGHT_DUTY)||
						lineItemGroup.getName().equalsIgnoreCase(Section.TRANSPORTATION_COST))
				     {
						populatePaymentData(paymentObjs, lineItemGroup);
				    }
				if (lineItemGroup.getName().equalsIgnoreCase(Section.DEDUCTIBLE))
				{
			    	if(deductable!=null)
				 populatePaymentData(paymentObjs, lineItemGroup);
				}
				if (lineItemGroup.getName().equalsIgnoreCase(Section.LATE_FEE))
				{
					 populatePaymentData(paymentObjs, lineItemGroup);
				}

            }
			
        }
		LineItemGroup claimAmountGrp = new LineItemGroup();
		if(claim.getPayment() != null)
		 claimAmountGrp = claim.getPayment().getLineItemGroup(Section.TOTAL_CLAIM);
		String totalLineItemGroupName = "Total " + getText(NAMES_AND_KEY.get(claimAmountGrp.getName()));
		if (claimAmountGrp!=null && dealerPayment != null && !dealerPayment.getLineItemGroups().isEmpty() && dealerPayment.getLineItemGroup(claimAmountGrp.getName()) != null)
		{
			  ClaimPaymentObject paymentObjectOne = new ClaimPaymentObject(totalLineItemGroupName,
						dealerPayment.getLineItemGroup(claimAmountGrp.getName()).getGroupTotal().breachEncapsulationOfAmount(),
						claimAmountGrp.getGroupTotal().breachEncapsulationOfAmount(),
						claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP) != null
	                    ? claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP).breachEncapsulationOfAmount()
	                    : new BigDecimal(0.0), Boolean.TRUE, Boolean.TRUE,isCPAdvisorEnabled());
				paymentObjectOne.setAcceptedQtyHrs(claimAmountGrp.getAcceptedQtyHrs()!=null ?
						claimAmountGrp.getAcceptedQtyHrs() :
							null);
	       	    paymentObjectOne.setAskedQtyHrs(claimAmountGrp.getAskedQtyHrs()!=null ?
	       	    		claimAmountGrp.getAskedQtyHrs() :
	       	    			null);
	       	    paymentObjectOne.setPercentageAcceptance(claimAmountGrp.getPercentageAcceptance()!=null ?
	       	    		claimAmountGrp.getPercentageAcceptance().toString() :
	       	    			null);
	       	    paymentObjectOne.setCurrencyCode(claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode()!=null ?
	       	    		claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode() :
	       	    			null);
	       	 paymentObjectOne.setReviwedAmount(claimAmountGrp.getAcceptedTotal()!=null ?
          			  claimAmountGrp.getAcceptedTotal().breachEncapsulationOfAmount() : 
          				  null);
	       	
	       		if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy()){
	       			paymentObjectOne.setStateMondateAmount(claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount()!=null ?
	       					claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount() :
	       						null);
	       		}
	       	   paymentObjs.add(paymentObjectOne);
		}
		else
		{
			  ClaimPaymentObject paymentObjectTwo = new ClaimPaymentObject(totalLineItemGroupName,
						null,
						claimAmountGrp.getGroupTotal() != null ? claimAmountGrp.getGroupTotal().breachEncapsulationOfAmount() : null,
						claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP) != null
	                    ? claimAmountGrp.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP).breachEncapsulationOfAmount()
	                    : new BigDecimal(0.0), Boolean.TRUE, Boolean.TRUE,isCPAdvisorEnabled());
			  paymentObjectTwo.setAcceptedQtyHrs(claimAmountGrp.getAcceptedQtyHrs()!=null ?
					  claimAmountGrp.getAcceptedQtyHrs() :
						  null);
			  paymentObjectTwo.setAskedQtyHrs(claimAmountGrp.getAskedQtyHrs()!=null ?
					  claimAmountGrp.getAskedQtyHrs() :
						  null);
			  paymentObjectTwo.setPercentageAcceptance(claimAmountGrp.getPercentageAcceptance()!=null ?
					  claimAmountGrp.getPercentageAcceptance().toString() :
						  null);
	       	    paymentObjectTwo.setCurrencyCode(claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode()!=null ?
	       	    		claimAmountGrp.getGroupTotal().breachEncapsulationOfCurrency().getCurrencyCode() :
	       	    			null);
	       	 paymentObjectTwo.setReviwedAmount(claimAmountGrp.getAcceptedTotal()!=null ?
	          			  claimAmountGrp.getAcceptedTotal().breachEncapsulationOfAmount() : 
	          				  null);
	       	 if(claim.getPayment().getTotalAcceptanceChkbox()){
	       		paymentObjectTwo.setTotalAcceptanceChkbox(Boolean.TRUE);
		       	 }
	     		if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy()){
	     			paymentObjectTwo.setStateMondateAmount(claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount()!=null ?
	     					claimAmountGrp.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount():
	     						null);
	       			if(claim.getPayment().getTotalAcceptStateMdtChkbox())
	       			{
	       				paymentObjectTwo.setTotalAcceptStateMdtChkbox(Boolean.TRUE);
	       			}
	       		}
	        	 paymentObjs.add(paymentObjectTwo);
		}
		 if (!claim.getServiceInformation().getServiceDetail().getLaborPerformed().isEmpty())
		   {
                    List<LaborDetail> laborDetails=claim.getServiceInformation().getServiceDetail().getLaborPerformed();
                    List<JobCodes>  jobCodes = new ArrayList<JobCodes>();
				      for(LaborDetail laborDetail : laborDetails){
					   JobCodes jobCode = new JobCodes();
					   jobCode.setCode(laborDetail.getServiceProcedure()!=null ? laborDetail.getServiceProcedure().getDefinition().getCode():null);
					   jobCode.setDescription(laborDetail.getServiceProcedure()!=null ?(laborDetail.getServiceProcedure().getDefinedFor()!=null ? 
							   laborDetail.getServiceProcedure().getDefinedFor().getJobCodeDescription() :null):null);
					   jobCode.setStdLabHours(laborDetail.getLaborHrsEntered()!=null ? laborDetail.getLaborHrsEntered().toString() : null);
					   jobCode.setAddLabHours(laborDetail.getAdditionalLaborHours()!=null ? laborDetail.getAdditionalLaborHours().toString() : null);
					   jobCode.setReasonForAdditionalHours(laborDetail.getReasonForAdditionalHours());
					   jobCode.setHoursSpent(laborDetail.getHoursSpent());
					   jobCode.setLaborHrsEntered(laborDetail.getLaborHrsEntered());
					   jobCodes.add(jobCode);
					   
				   }
				      printClaimObject.setCodes(jobCodes);
		 
		  }
		  printClaimObject.setWarrantyAmount(warrantyAmount);
				printClaimObject.setCpAmount(cpAmount);
				
		printClaimObject.setInternalUser(isInternalUser(loggedInUser));
		printClaimObject.setShowPaymentInfo(isShowPaymentInfo(loggedInUser,
				claim.getForDealer().getId()));
		printClaimObject.setShowPercentageAccepted(showPercentageAccepted);
		printClaimObject.setPayment(paymentObjs);
		
	}
	
	public String printClaim() {
		User loggedInUser = getLoggedInUser();
		printClaimObject.setAdvisorEnabled(isCPAdvisorEnabled());
		printClaimObject.setIsSmrClaimAllowedForDealer(isDealerEligibleToFillSmrClaim());
		printClaimObject.setIsTechnicianEnabled(isTechnicianEnable());
		printClaimObject.setIsTechnicianEnabled(isTechnicianEnable());
		if(isBuConfigAMER())
		{
		    printPaymentForAMER();
		}
		else
		{
			printPaymentForEMEA();
	    }
		if(isBuConfigAMER())
			printClaimObject.setPolicyApplied(getClaimProcessedAsForDisplay(claim));
		
		printClaimObject.setIsPartReplacedInstalledSectionVisible(configParamService.
				getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName()));
		printClaimObject.setBuPartReplacedByNonBUPart(configParamService.
				getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName()));
		printClaimObject.setIsRootCauseVisible(configParamService.
				getBooleanValue(ConfigName.IS_ROOT_CAUSE_ALLOWED.getName()));
		printClaimObject.setDealerJobNumberEnabled(configParamService.getBooleanValue(ConfigName.SHOW_DEALER_JOB_NUMBER.getName()));
		printClaimObject.setLaborSplitEnabled(configParamService
				.getBooleanValue(ConfigName.ENABLE_LABOR_SPLIT.getName()));
		printClaimObject
				.setLaborSplitOption(configParamService
								.getBooleanValue(ConfigName.ENABLE_LABOR_SPLIT.getName())&& configParamService
								.getBooleanValue(ConfigName.LABOR_SPLIT_DISTRIBUTION
										.getName()));
		printClaimObject.setIsAlarmCodesSectionVisible(configParamService
				.getBooleanValue(ConfigName.ALARM_CODE_SECTION_VISIBLE
						.getName()));	
		if (printClaimObject.getIsPartReplacedInstalledSectionVisible()
				&& !printClaimObject.getBuPartReplacedByNonBUPart()) {
            printClaimObject.setIsNonOEMPartsSectionVisible(true);
		}
		if(claim.getServiceInformation() !=null && claim.getServiceInformation().getFaultCodeDescription() !=null)
		{
		setFaultCodeDescription(claim.getServiceInformation().getFaultCodeDescription());
		}
		String selectedBU = SelectedBusinessUnitsHolder
				.getSelectedBusinessUnit();
		if (org.apache.commons.lang.StringUtils.isNotEmpty(claim.getBrand())) {
			if (claim.getBrand().equals(BrandType.HYSTER.getType())) {
				printClaimObject.setGifName("Hyster_Header.jpg");
			
		} 
		if(claim.getBrand().equals(BrandType.YALE.getType())){
			printClaimObject.setGifName("Yale_Header.jpg");
		}
		if(claim.getBrand().equals(BrandType.UTILEV.getType())){
			String dealerBrand = this.orgService.findDealerBrands(claim
					.getForDealer());
			if(dealerBrand.equals(BrandType.YALE.getType())){
			  printClaimObject.setGifName("Yale_Header.jpg");
			}
			if(dealerBrand.equals(BrandType.HYSTER.getType())){
			  printClaimObject.setGifName("Hyster_Header.jpg");
			}
		}
	}
		dealerOrSp =(selectedBU.equalsIgnoreCase("Hussmann")) ? "Service Provider:" : "Dealer:";
		if(isBuConfigAMER()){
			formatedRepairDate  = printClaimObject.getClaim().getRepairDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
			formatedfiledOnDate = printClaimObject.getClaim().getFiledOnDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
			formatedRepairStartDate=printClaimObject.getClaim().getRepairStartDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
			if(!claim.getType().getType().equalsIgnoreCase("CAMPAIGN")){
				formatedFailureDate = printClaimObject.getClaim().getFailureDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
			}
			if(printClaimObject.getClaim().getInstallationDate() != null){
				formatedInstallationDate = printClaimObject.getClaim().getInstallationDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
			}
		
			if(printClaimObject.getClaim().getPurchaseDate() != null){
				formatedPurchaseDate = printClaimObject.getClaim().getPurchaseDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
			}
		}
		else{
			formatedRepairDate  = printClaimObject.getClaim().getRepairDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
			formatedfiledOnDate = printClaimObject.getClaim().getFiledOnDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
			formatedRepairStartDate=printClaimObject.getClaim().getRepairStartDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
			if(!claim.getType().getType().equalsIgnoreCase("CAMPAIGN")){
				formatedFailureDate = printClaimObject.getClaim().getFailureDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
			}
			if(printClaimObject.getClaim().getInstallationDate() != null){
				formatedInstallationDate = printClaimObject.getClaim().getInstallationDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
			}
			if(printClaimObject.getClaim().getPurchaseDate() != null){
				formatedPurchaseDate = printClaimObject.getClaim().getPurchaseDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
			}
		
		}
		populateEndCustomerInformation();
		List<ClaimedItem> claimedItem = printClaimObject.getClaim().getClaimedItems();
		for (ClaimedItem claimedItem2 : claimedItem) {
			
			//To get Product name for unserialized Item (not productfamily)						
			if (!claim.getType().equals(ClaimType.PARTS) || (new HibernateCast<PartsClaim>().cast(claim).getPartInstalled() && (claim.getCompetitorModelBrand() == null && claim.getCompetitorModelDescription() == null && claim.getCompetitorModelTruckSerialnumber() == null))) {
				printClaimObject.setShowEquipmentInfoSection(true);
				if(!claimedItem2.getItemReference().isSerialized()){
					unserializedItemProduct = getProduct(claimedItem2.getItemReference().getModel()).getItemGroupDescription();	
					unserializedItemGroupCode=getProduct(claimedItem2.getItemReference().getModel()).getGroupCode();
				}				
			}
			if (claimedItem2.getItemReference().isSerialized() && claimedItem2.getItemReference().getReferredInventoryItem()!=null && claimedItem2.getItemReference().getReferredInventoryItem().isRetailed()) {
                 
				if(isBuConfigAMER()){
					formatedWarrantyEndDate = claimedItem2.getItemReference().getReferredInventoryItem().getWntyEndDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getWntyEndDate().toString(
							TWMSDateFormatUtil
									.DEFAULT_DATE_PATTERN) : "";
			    formatedWarrantyStartDate = claimedItem2.getItemReference().getReferredInventoryItem().getWntyStartDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getWntyStartDate().toString(
							TWMSDateFormatUtil
							.DEFAULT_DATE_PATTERN) :"";
		        	dateOfdelivery = claimedItem2.getItemReference().getReferredInventoryItem().getDeliveryDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getDeliveryDate().toString(
									TWMSDateFormatUtil
									.DEFAULT_DATE_PATTERN) : "";
					
				}
				else{
				formatedWarrantyEndDate = claimedItem2.getItemReference().getReferredInventoryItem().getWntyEndDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getWntyEndDate().toString(
								TWMSDateFormatUtil
										.getDateFormatForLoggedInUser()) : "";
				formatedWarrantyStartDate = claimedItem2.getItemReference().getReferredInventoryItem().getWntyStartDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getWntyStartDate().toString(
								TWMSDateFormatUtil
										.getDateFormatForLoggedInUser()):"";
				dateOfdelivery = claimedItem2.getItemReference().getReferredInventoryItem().getDeliveryDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getDeliveryDate().toString(
										TWMSDateFormatUtil
												.getDateFormatForLoggedInUser()) : "";
				}
			}
			else {
				formatedWarrantyEndDate = "";
				formatedWarrantyStartDate= "";
				dateOfdelivery= "";
			}
		}
		List<OEMPartReplaced> oemPartReplaced = claim.getServiceInformation().getServiceDetail().getOemPartsReplaced();
		
		if (claim.getType().equals(ClaimType.MACHINE)
				||(claim.getType().equals(ClaimType.CAMPAIGN))|| (claim.getType().equals(ClaimType.PARTS)
						&& (new HibernateCast<PartsClaim>().cast(claim).getPartInstalled() && (claim.getCompetitorModelBrand() == null && claim.getCompetitorModelDescription() == null && claim.getCompetitorModelTruckSerialnumber() == null)))) {
			printClaimObject.setShowServiceProcedureSection(true);
		}
		else
		{
			printClaimObject.setShowServiceProcedureSection(false);
		}
		/**
		 * This logic is introduced as oempartreplaced subreport throws exception when there is no replaced part.
		 * So made the sub report inclusion conditional
		 */	
		if (oemPartReplaced != null && oemPartReplaced.size() > 0){
			printClaimObject.setIsOEMReplacedPartPresent(Boolean.TRUE);
	    }	
		if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : claim
					.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled()) {
				if (hussmanPartsReplacedInstalled != null) {
					for (OEMPartReplaced partReplaced : hussmanPartsReplacedInstalled.getReplacedParts()) {
						if( partReplaced != null ) {
							HussmannPartReplacedInstalledDTO hussmannPartReplacedInstalledDTO =
									new HussmannPartReplacedInstalledDTO(partReplaced, claim.getBrand());
							hussmannPartReplacedInstalledDTOList.
								add(hussmannPartReplacedInstalledDTO);
						}
					}
					for (InstalledParts hussmannInstalledpart : hussmanPartsReplacedInstalled.getHussmanInstalledParts()) {
						if( hussmannInstalledpart != null ) {
							HussmannPartReplacedInstalledDTO hussmannPartReplacedInstalledDTO =
									new HussmannPartReplacedInstalledDTO(hussmannInstalledpart,true,printClaimObject.getBuPartReplacedByNonBUPart().booleanValue(),claim.getBrand());
							hussmannPartReplacedInstalledDTOList.
								add(hussmannPartReplacedInstalledDTO);
						}
					}
					for (InstalledParts nonHussmannInstalledpart : hussmanPartsReplacedInstalled.getNonHussmanInstalledParts()) {
							if( nonHussmannInstalledpart != null ) {
								HussmannPartReplacedInstalledDTO hussmannPartReplacedInstalledDTO =
										new HussmannPartReplacedInstalledDTO(nonHussmannInstalledpart,false,printClaimObject.getBuPartReplacedByNonBUPart().booleanValue(),claim.getBrand());
								hussmannPartReplacedInstalledDTOList.
									add(hussmannPartReplacedInstalledDTO);
							}
						}
					

				}
			}
			
		}
	
		return SUCCESS;
	}
	public String getClaimProcessedAsForDisplay(Claim claim) {
		if (!org.apache.commons.lang.StringUtils.isEmpty(claim.getPolicyCode())) {
			return claim.getPolicyCode();
		}
		if (Constants.INVALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
			return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_STOCK.equals(claim.getClaimProcessedAs())) {
			return getText("label.common.itemInStock");
		} else if (Constants.VALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_OUT_OF_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return getText("label.common.outOfWarranty");
		}
		return claim.getClaimedItems().get(0).getApplicablePolicy()!=null ? claim.getClaimedItems().get(0).getApplicablePolicy().getCode() :null;
	}

	
	public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
			boolean isNumber, Organization organization) {

		Organization loggedInUserOrganization = getLoggedInUser()
				.getBelongsToOrganization();
		if (oemDealerItem != null
				&& isOEMDealerPartExistForPart(item, organization)) {
			if (InstanceOfUtil.isInstanceOfClass(Dealership.class,
					loggedInUserOrganization)) {
				if (isNumber) {
					if(oemDealerItem.getDuplicateAlternateNumber())
					{
						return oemDealerItem.getNumber();
					}
					else
					{
						return oemDealerItem.getAlternateNumber();
					}					
				} else {
					return oemDealerItem.getDescription();
				}
			} else {
				if (isNumber) {
					return (oemDealerItem.getDuplicateAlternateNumber()? item.getNumber() : item.getAlternateNumber() ) + "(" + oemDealerItem.getNumber()
							+ ")";
				} else {
					return item.getDescription() + "("
							+ oemDealerItem.getDescription() + ")";
				}
			}
		} else {
			if (isNumber) {
				if(item.getDuplicateAlternateNumber())
				{
					return item.getNumber();
				}
				else
				{
					return item.getAlternateNumber();
				}	
			} else {
				return item.getDescription();
			}
		}
	}
	
	private boolean isOEMDealerPartExistForPart(Item fromItem,
			Organization organization) {
		Item item = this.catalogService.findOEMDealerPartForPart(fromItem,
				organization);
		if (item != null) {
			return true;
		}
		return false;
	}



	private void populatePaymentData(List<ClaimPaymentObject> paymentObjs,
			LineItemGroup lineItemGroup) {
		ClaimPaymentObject paymentObject = new ClaimPaymentObject(
                getText(NAMES_AND_KEY.get(lineItemGroup.getName())),
                 Boolean.FALSE,
                 Boolean.FALSE);
		paymentObject.setAdvisorEnabled(isCPAdvisorEnabled());
		Payment dealerPayment = claim.getPaymentForDealerAudit();
        if (dealerPayment != null && !dealerPayment.getLineItemGroups().isEmpty() && dealerPayment.getLineItemGroup(lineItemGroup.getName()) != null 
        		&&lineItemGroup.getIndividualLineItems().isEmpty()&&
        		!Section.NON_OEM_PARTS.equals(lineItemGroup.getName())&&
        		!Section.TRAVEL_BY_TRIP.equals(lineItemGroup.getName())&&
        		!Section.OEM_PARTS.equals(lineItemGroup.getName())
        		&&!Section.LABOR.equals(lineItemGroup.getName()))
        {
        	
            paymentObject.setClaimedValue(dealerPayment.getLineItemGroup(lineItemGroup.getName())
            		.getBaseAmount()!=null ?dealerPayment.getLineItemGroup(lineItemGroup.getName())
                    		.getBaseAmount().breachEncapsulationOfAmount() :
                    			null);
            paymentObject.setCurrencyCode(dealerPayment.getLineItemGroup(lineItemGroup.getName()).getBaseAmount()!=null ?
            				dealerPayment.getLineItemGroup(lineItemGroup.getName()).getBaseAmount().breachEncapsulationOfCurrency().getCurrencyCode() :
                    			null);
            if(paymentObject.getClaimedValue()==null)
            {
            	paymentObject.setClaimedValueDisplay(Boolean.FALSE);
            }else{
            	paymentObject.setClaimedValueDisplay(Boolean.TRUE);
            }
       
        }
        if (lineItemGroup != null && lineItemGroup.getBaseAmount() != null 
        		&& lineItemGroup.getIndividualLineItems().isEmpty()&&
        		!Section.NON_OEM_PARTS.equals(lineItemGroup.getName())&&
        		!Section.TRAVEL_BY_TRIP.equals(lineItemGroup.getName())&&
        		!Section.OEM_PARTS.equals(lineItemGroup.getName())
        		&&!Section.LABOR.equals(lineItemGroup.getName())) {
            paymentObject.setValue(lineItemGroup.getBaseAmount()!=null ?
            		lineItemGroup.getBaseAmount().breachEncapsulationOfAmount() :
            			null);
            paymentObject.setReviwedAmount(lineItemGroup.getAcceptedTotal()!=null ?
            		lineItemGroup.getAcceptedTotal().breachEncapsulationOfAmount() : 
         				  null);
            paymentObject.setCurrencyCode(lineItemGroup.getBaseAmount()!=null ?
            		lineItemGroup.getBaseAmount().breachEncapsulationOfCurrency().getCurrencyCode().toString() :
                    			null);
            if(lineItemGroup.getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP)!= null){
            	paymentObject.setCpValue(lineItemGroup.getAdditionalPaymentInfoOfType(
            			AdditionalPaymentType.ACCEPTED_FOR_CP).breachEncapsulationOfAmount());

            }
        }
        if(lineItemGroup != null && lineItemGroup.getIndividualLineItems().isEmpty()
        		&&!Section.NON_OEM_PARTS.equals(lineItemGroup.getName())
        		&&!Section.TRAVEL_BY_TRIP.equals(lineItemGroup.getName())&&
        		!Section.OEM_PARTS.equals(lineItemGroup.getName())
        		&&!Section.LABOR.equals(lineItemGroup.getName()))
	     {
        	paymentObject.setAcceptedQtyHrs(lineItemGroup.getAcceptedQtyHrs()!=null ?
        			lineItemGroup.getAcceptedQtyHrs() :
        				null);
        	paymentObject.setAskedQtyHrs(lineItemGroup.getAskedQtyHrs()!=null ?
        			lineItemGroup.getAskedQtyHrs() :
        				null);
        	if((!lineItemGroup.getName().equals(Section.DEDUCTIBLE))){
        	paymentObject.setPercentageAcceptance(lineItemGroup.getPercentageAcceptance()!=null ?
        			lineItemGroup.getPercentageAcceptance().toString() :
        				null);
        	}
         	if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy())
			{
            	paymentObject.setStateMondateAmount(lineItemGroup.getGroupTotalStateMandateAmount()!=null ?
            			lineItemGroup.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount() :
            				null);
			}
    
   
	     }
        if(lineItemGroup != null &&Section.TRAVEL_BY_TRIP.equals(lineItemGroup.getName()))
	     {
        	paymentObject.setAcceptedQtyHrs(lineItemGroup.getAcceptedQtyHrs() !=null ?
        			lineItemGroup.getAcceptedQtyHrs() :
        				null);
        	paymentObject.setAskedQtyHrs(lineItemGroup.getAskedQtyHrs() !=null ?
        			lineItemGroup.getAskedQtyHrs() :
        				null);
        	paymentObject.setValue(lineItemGroup.getBaseAmount()!=null ? 
        			lineItemGroup.getBaseAmount().breachEncapsulationOfAmount():
        			null);
	     }
       	paymentObjs.add(paymentObject);
       	if(lineItemGroup.getName().equals(Section.OEM_PARTS)){
	        	for (int i = 0; i <lineItemGroup.getIndividualLineItems().size(); i++) {
	        		IndividualLineItem individualLineItem = lineItemGroup.getIndividualLineItems().get(i);
	        		individualLineItem.getBrandItem().getItemNumber();
	        		individualLineItem.getBrandItem().getItem().getDescription();
	        		ClaimPaymentObject claimPaymentObject = new ClaimPaymentObject(individualLineItem.getBrandItem()!=null ?
	        				individualLineItem.getBrandItem().getItemNumber()+"-"+individualLineItem.getBrandItem().getItem().getDescription() :individualLineItem.getBrandItem().getItem()!=null ?
	        						individualLineItem.getBrandItem().getItem().getDescription() :"" ,
	    	   		   				null,
	    	   				individualLineItem.getAcceptedAmount().breachEncapsulationOfAmount(),
	    					individualLineItem.getStateMandateAmount().breachEncapsulationOfAmount(),Boolean.FALSE,Boolean.FALSE,isCPAdvisorEnabled());
	    			   		claimPaymentObject.setAcceptedQtyHrs(individualLineItem.getAcceptedQty() !=null ?
	    			   				individualLineItem.getAcceptedQty().toString() :
	    			   					null);    // for oem parts,it will be accepted qty not hrs
	    			   		try{
		   		   				BigDecimal claimedValue = dealerPayment.getLineItemGroup(lineItemGroup.getName())!=null ?dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getBaseAmount()!=null ?
		   		   							dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getBaseAmount().breachEncapsulationOfAmount() :
		   		   							null :null;
		   		   			
	    			   			String askedqty =	dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getAskedQty()!=null ? 
	    			   						dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getAskedQty().toString() :null;
		   		   		        	claimPaymentObject.setClaimedValue(claimedValue);
		   		   		      claimPaymentObject.setAskedQtyHrs(askedqty); // for oem parts,it will be askedqty not hrs
	    		        		}
	    		        		catch(Exception e){
	    		        			claimPaymentObject.setAskedQtyHrs(null);
	    		        		}
	    			   		claimPaymentObject.setPercentageAcceptance(individualLineItem.getPercentageAcceptance()!=null ?
	    			   				individualLineItem.getPercentageAcceptance().toString() :
	    			   					null);
	    			   		claimPaymentObject.setCurrencyCode(individualLineItem.getBaseAmount()!=null ?
	    			   				individualLineItem.getBaseAmount().breachEncapsulationOfCurrency().getCurrencyCode() :
	    			   					null);
	    			   		claimPaymentObject.setReviwedAmount(individualLineItem.getAcceptedAmount()!=null ?
	    			   				individualLineItem.getAcceptedAmount().breachEncapsulationOfAmount() : 
	    		         				  null);
	    			   	 	if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy())
	    					{
	    			   			claimPaymentObject.setStateMondateAmount(individualLineItem.getStateMandateAmount()!=null ?
	    			   					individualLineItem.getStateMandateAmount().breachEncapsulationOfAmount() :
	    			   						null);
	    					}
	    			   	 if(claimPaymentObject.getClaimedValue()==null)
	    		            {
	    			   		claimPaymentObject.setClaimedValueDisplay(Boolean.FALSE);
	    		            }else{
	    		            	claimPaymentObject.setClaimedValueDisplay(Boolean.TRUE);
	    		            }
	        		     paymentObjs.add(claimPaymentObject);
				
			 }
       	}
       	if(lineItemGroup.getName().equals(Section.NON_OEM_PARTS)){
        	for (int i = 0; i <lineItemGroup.getIndividualLineItems().size(); i++) {

        		IndividualLineItem individualLineItem = lineItemGroup.getIndividualLineItems().get(i);
        		ClaimPaymentObject claimPaymentObject = new ClaimPaymentObject(individualLineItem.getNonOemPartReplaced()!=null ? individualLineItem.getNonOemPartReplaced() :null,
    	   		   		null,
    	   				individualLineItem.getAcceptedAmount().breachEncapsulationOfAmount(),
    					individualLineItem.getStateMandateAmount().breachEncapsulationOfAmount(),Boolean.FALSE,Boolean.FALSE,isCPAdvisorEnabled());
    			   		claimPaymentObject.setAcceptedQtyHrs(individualLineItem.getAcceptedQty() !=null ?
    			   				individualLineItem.getAcceptedQty().toString() :
    			   					null);
    			   		
    			   		try{
	   		   				BigDecimal claimedValue = dealerPayment.getLineItemGroup(lineItemGroup.getName())!=null ?dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getBaseAmount()!=null ?
	   		   							dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getBaseAmount().breachEncapsulationOfAmount() :
	   		   							null :null;
	   		   			
    			   			String askedqty =	dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getAskedQty()!=null ? 
    			   						dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getAskedQty().toString() :null;
	   		   		        	claimPaymentObject.setClaimedValue(claimedValue);
	   		   		      claimPaymentObject.setAskedQtyHrs(askedqty);
    		        		}
    		        		catch(Exception e){
    		        			claimPaymentObject.setAskedQtyHrs(null);
    		        		}
    		        		
    		
    			   		claimPaymentObject.setPercentageAcceptance(individualLineItem.getPercentageAcceptance()!=null ?
    			   				individualLineItem.getPercentageAcceptance().toString() :
    			   					null);
    			   		claimPaymentObject.setCurrencyCode(individualLineItem.getBaseAmount()!=null ?
    			   				individualLineItem.getBaseAmount().breachEncapsulationOfCurrency().getCurrencyCode() :
    			   					null);
    			   		claimPaymentObject.setReviwedAmount(individualLineItem.getAcceptedAmount()!=null ?
    			   				individualLineItem.getAcceptedAmount().breachEncapsulationOfAmount() : 
    		         				  null);
    			   	 	if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy())
    					{
    			   			claimPaymentObject.setStateMondateAmount(individualLineItem.getStateMandateAmount()!=null ?
    			   					individualLineItem.getStateMandateAmount().breachEncapsulationOfAmount() :
    			   						null);
    					}
    			   	 if(claimPaymentObject.getClaimedValue()==null)
    		            {
    			   		claimPaymentObject.setClaimedValueDisplay(Boolean.FALSE);
    		            }else{
    		            	claimPaymentObject.setClaimedValueDisplay(Boolean.TRUE);
    		            }
        		     paymentObjs.add(claimPaymentObject);
			
		 
        		
			
		 }
    	}
       	if(lineItemGroup.getName().equals(Section.LABOR)){
        	for (int i = 0; i <lineItemGroup.getIndividualLineItems().size(); i++) {
        		IndividualLineItem individualLineItem = lineItemGroup.getIndividualLineItems().get(i);
        		ClaimPaymentObject claimPaymentObject = new ClaimPaymentObject(individualLineItem.getServiceProcedureDefinition()!=null ?
    	   				individualLineItem.getServiceProcedureDefinition().getCode()+"-"+individualLineItem.getServiceProcedureDefinition().getDescription() :individualLineItem.getServiceProcedureDefinition()!=null ?
    	   						individualLineItem.getServiceProcedureDefinition().getDescription() :"" ,
    	   		                  null,
    	   				individualLineItem.getAcceptedAmount().breachEncapsulationOfAmount(),
    					individualLineItem.getStateMandateAmount().breachEncapsulationOfAmount(),Boolean.FALSE,Boolean.FALSE,isCPAdvisorEnabled());
    			   		claimPaymentObject.setAcceptedQtyHrs(individualLineItem.getAcceptedHrs() !=null ?
    			   				individualLineItem.getAcceptedHrs().toString() :
    			   					null);
    			   		try{
	   		   				BigDecimal claimedValue = dealerPayment.getLineItemGroup(lineItemGroup.getName())!=null ?dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getBaseAmount()!=null ?
	   		   							dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getBaseAmount().breachEncapsulationOfAmount() :
	   		   							null :null;
	   		   			
    			   			String askedHours =	dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getAskedHrs()!=null ? 
    			   						dealerPayment.getLineItemGroup(lineItemGroup.getName()).getIndividualLineItems().get(i).getAskedHrs().toString() :null;
	   		   		        	claimPaymentObject.setClaimedValue(claimedValue);
	   		   		      claimPaymentObject.setAskedQtyHrs(askedHours);
    		        		}
    		        		catch(Exception e){
    		        			claimPaymentObject.setAskedQtyHrs(null);
    		        		}
    			   		claimPaymentObject.setPercentageAcceptance(individualLineItem.getPercentageAcceptance()!=null ?
    			   				individualLineItem.getPercentageAcceptance().toString() :
    			   					null);
    			   		claimPaymentObject.setCurrencyCode(individualLineItem.getBaseAmount()!=null ?
    			   				individualLineItem.getBaseAmount().breachEncapsulationOfCurrency().getCurrencyCode() :
    			   					null);
    			   		claimPaymentObject.setReviwedAmount(individualLineItem.getAcceptedAmount()!=null ?
    			   				individualLineItem.getAcceptedAmount().breachEncapsulationOfAmount() : 
    		         				  null);
    			   	 	if(claim.getStateMandate()!=null&&!claim.isGoodWillPolicy())
    					{
    			   			claimPaymentObject.setStateMondateAmount(individualLineItem.getStateMandateAmount()!=null ?
    			   					individualLineItem.getStateMandateAmount().breachEncapsulationOfAmount() :
    			   						null);
    					}
    			   	 if(claimPaymentObject.getClaimedValue()==null)
    		            {
    			   		claimPaymentObject.setClaimedValueDisplay(Boolean.FALSE);
    		            }else{
    		            	claimPaymentObject.setClaimedValueDisplay(Boolean.TRUE);
    		            }
        		     paymentObjs.add(claimPaymentObject);
			
		 
			
		 }
    	}

	List<LineItem> lineItems = lineItemGroup.getModifiers();
		for (LineItem lineItem : lineItems) {
            ClaimPaymentObject lineItemPmt = null;
            boolean isModifierExisting = false;
            for (LineItem item : lineItems) {
                if(lineItem.getName().equals(item.getName())){
                     lineItemPmt = new ClaimPaymentObject((lineItem
					.getPaymentVariable()!=null?lineItem
							.getPaymentVariable().getDisplayName():null)+" "+(lineItem.getModifierPercentage()!=null ?lineItem.getModifierPercentage().toString()+"%" :null) , item.getClaimedValue().breachEncapsulationOfAmount(),
					lineItem.getValue().breachEncapsulationOfAmount(),
					lineItem.getCpValue().breachEncapsulationOfAmount(),Boolean.FALSE,
					Boolean.FALSE,isCPAdvisorEnabled());
                     lineItemPmt.setPercentageAcceptance(lineItem.getPercentageAcceptance()!=null ?
                    		 lineItem.getPercentageAcceptance().toString() : null);
                     lineItemPmt.setCurrencyCode(lineItem.getValue().breachEncapsulationOfCurrency().getCurrencyCode() !=null ?
                    		 lineItem.getValue().breachEncapsulationOfCurrency().getCurrencyCode() :
                    			 null);
                     lineItemPmt.setReviwedAmount(lineItem.getAcceptedCost()!=null ?
                    		 lineItem.getAcceptedCost().breachEncapsulationOfAmount() : 
		         				  null);
                    isModifierExisting=true;
   			   	 if(lineItemPmt!=null ? lineItemPmt.getClaimedValue()==null:Boolean.FALSE)
		            {
			   		lineItemPmt.setClaimedValueDisplay(Boolean.FALSE);
		            }else{
		            	lineItemPmt.setClaimedValueDisplay(Boolean.TRUE);
		            }
                }
                

            }
            if(!isModifierExisting){
              lineItemPmt = new ClaimPaymentObject(lineItem
					.getName(), Money.valueOf(0.0D, GlobalConfiguration.getInstance()
				                    .getBaseCurrency()).breachEncapsulationOfAmount(),
					lineItem.getValue().breachEncapsulationOfAmount(),
					lineItem.getCpValue().breachEncapsulationOfAmount(),Boolean.FALSE,
					Boolean.FALSE,isCPAdvisorEnabled());
                    lineItemPmt.setCurrencyCode(lineItem.getValue().breachEncapsulationOfCurrency().getCurrencyCode()!=null ?
                    		lineItem.getValue().breachEncapsulationOfCurrency().getCurrencyCode() :
                    			null);
                   lineItemPmt.setPercentageAcceptance(lineItem.getPercentageAcceptance()!=null ?
                		   lineItem.getPercentageAcceptance().toString() :
                			   null);
                   lineItemPmt.setReviwedAmount(lineItem.getAcceptedCost()!=null ?
                  		 lineItem.getAcceptedCost().breachEncapsulationOfAmount() : 
		         				  null);
  			   	 if(lineItemPmt!=null ? lineItemPmt.getClaimedValue()==null:Boolean.FALSE)
		            {
			   		lineItemPmt.setClaimedValueDisplay(Boolean.FALSE);
		            }else{
		            	lineItemPmt.setClaimedValueDisplay(Boolean.TRUE);
		            }
            }
            paymentObjs.add(lineItemPmt);
            
            /*SLMSPROD-1499,State mandate modifier line item is not displaying on Claim Print report for Processor login. */
            if(claim.getPayment()!=null && claim.getPayment().getStateMandateActive() && lineItemGroup.getName().equalsIgnoreCase(Section.OEM_PARTS) && !claim.isGoodWillPolicy()){
            	String modifierName = "("+getText("label.payment.stateMandate")+")"+ " "+(lineItem
        				.getPaymentVariable()!=null?lineItem
        						.getPaymentVariable().getDisplayName():null)+" "+(lineItem.getPercentageConfiguredSMandate()!=null ?lineItem.getPercentageConfiguredSMandate().toString()+"%" :null);
                lineItemPmt = new ClaimPaymentObject(modifierName , null,
				null,
				null,Boolean.FALSE,
				Boolean.FALSE,isCPAdvisorEnabled());
                lineItemPmt.setCurrencyCode(lineItem.getStateMandateAmount()!=null ?
                		lineItem.getStateMandateAmount().breachEncapsulationOfCurrency().getCurrencyCode() :
           					null);
                lineItemPmt.setStateMondateAmount(lineItem.getStateMandateAmount()!=null ?
                		lineItem.getStateMandateAmount().breachEncapsulationOfAmount() :
           					null);
                paymentObjs.add(lineItemPmt);
           }
		}
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public String getFormatedFailureDate() {
		return formatedFailureDate;
	}

	public void setFormatedFailureDate(String formatedFailureDate) {
		this.formatedFailureDate = formatedFailureDate;
	}

	public String getFormatedRepairDate() {
		return formatedRepairDate;
	}

	public void setFormatedRepairDate(String formatedRepairDate) {
		this.formatedRepairDate = formatedRepairDate;
	}

	public String getFormatedWarrantyStartDate() {
		return formatedWarrantyStartDate;
	}

	public void setFormatedWarrantyStartDate(String formatedWarrantyStartDate) {
		this.formatedWarrantyStartDate = formatedWarrantyStartDate;
	}

	public String getFormatedWarrantyEndDate() {
		return formatedWarrantyEndDate;
	}

	public void setFormatedWarrantyEndDate(String formatedWarrantyEndDate) {
		this.formatedWarrantyEndDate = formatedWarrantyEndDate;
	}

	public String getFormatedfiledOnDate() {
		return formatedfiledOnDate;
	}

	public void setFormatedfiledOnDate(String formatedfiledOnDate) {
		this.formatedfiledOnDate = formatedfiledOnDate;
	}
	
	public Boolean getAdvisor() {
		return advisor;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public List<HussmannPartReplacedInstalledDTO> getHussmannPartReplacedInstalledDTOList() {
		return hussmannPartReplacedInstalledDTOList;
	}

	public void setHussmannPartReplacedInstalledDTOList(
			List<HussmannPartReplacedInstalledDTO> hussmannPartReplacedInstalledDTOList) {
		this.hussmannPartReplacedInstalledDTOList = hussmannPartReplacedInstalledDTOList;
	}

	public void setAdvisor(Boolean advisor) {
		this.advisor = advisor;
	}

	
	public boolean isCPAdvisorEnabled() {
		if(advisor==null){
			setAdvisor(this.configParamService
				.getBooleanValue(ConfigName.ENABLE_CP_ADVISOR.getName()));
			return advisor;
		}else{
			return advisor;
		}
    }

	public String getFormatedInstallationDate() {
		return formatedInstallationDate;
	}

	public void setFormatedInstallationDate(String formatedInstallationDate) {
		this.formatedInstallationDate = formatedInstallationDate;
	}

	public String getFormatedPurchaseDate() {
		return formatedPurchaseDate;
	}

	public void setFormatedPurchaseDate(String formatedPurchaseDate) {
		this.formatedPurchaseDate = formatedPurchaseDate;
	}
	
	private void populateEndCustomerInformation(){
		Claim claim = this.printClaimObject.getClaim();
		if(claim.getClaimedItems() != null && claim.getClaimedItems().get(0).getItemReference() !=null 
				&& claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem() != null && 
				claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getType().getType().equals("RETAIL"))
		{	
			this.printClaimObject.setIsOwnerInfoDisplayNeeded(true);
			if(claim.getMatchReadInfo() != null){
				//Match case info takes precedence
				this.printClaimObject.setOwnerName(claim.getMatchReadInfo().getOwnerName());
				this.printClaimObject.setOwnerCountry(claim.getMatchReadInfo().getOwnerCountry());
				this.printClaimObject.setOwnerCity(claim.getMatchReadInfo().getOwnerCity());
				this.printClaimObject.setOwnerState(claim.getMatchReadInfo().getOwnerState());
				this.printClaimObject.setOwnerZipCode(claim.getMatchReadInfo().getOwnerZipcode());				
			}
			else{
				//Details against inventory should be displayed
				Party owner = claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getOwnedBy();
				this.printClaimObject.setOwnerName(owner.getName());
				this.printClaimObject.setOwnerCountry(owner.getAddress().getCountry() != null ? owner.getAddress().getCountry() : "");
				this.printClaimObject.setOwnerCity(owner.getAddress().getCity() != null ? owner.getAddress().getCity() : "");
				this.printClaimObject.setOwnerState(owner.getAddress().getState() != null ? owner.getAddress().getState() : "");
				this.printClaimObject.setOwnerZipCode(owner.getAddress().getZipCode() != null ? owner.getAddress().getZipCode() : "");
			}
		}
		if ((ClaimType.MACHINE.getType().equals(claim.getType()
				.getType()) && !claim.getItemReference().isSerialized()) || 
				(claim.getType().equals("Parts") && new HibernateCast<PartsClaim>().cast(claim).getPartInstalled() 
						&& !claim.getItemReference().isSerialized()))
  		{
			this.printClaimObject.setIsOwnerInfoDisplayNeeded(true);
			//Parts or Machine Non Serialized
			this.printClaimObject.setOwnerName(claim.getOwnerInformation().getBelongsTo() != null ? claim.getOwnerInformation().getBelongsTo().getName() : "");
			this.printClaimObject.setOwnerCountry(claim.getOwnerInformation().getCountry() != null ? claim.getOwnerInformation().getCountry() : "");
			this.printClaimObject.setOwnerCity(claim.getOwnerInformation().getCity() != null ? claim.getOwnerInformation().getCity() : "");
			this.printClaimObject.setOwnerState(claim.getOwnerInformation().getState() != null ? claim.getOwnerInformation().getState() : "");
			this.printClaimObject.setOwnerZipCode(claim.getOwnerInformation().getZipCode() != null ? claim.getOwnerInformation().getZipCode() : "");
  		}
	}

	
    public ItemGroup getProduct(ItemGroup itemGroup) {
    	ItemGroup product = null;	
    	if (!ItemGroup.PRODUCT.equals(itemGroup.getIsPartOf().getItemGroupType())) {
    		product = getProduct(itemGroup.getIsPartOf());
    	} else {
    		product = itemGroup.getIsPartOf();
    	}
    	return product;
   }

	public String getUnserializedItemProduct() {
		return unserializedItemProduct;
	}

	public void setUnserializedItemProduct(String unserializedItemProduct) {
		this.unserializedItemProduct = unserializedItemProduct;
	}
public boolean isDealerEligibleToFillSmrClaim(){
        	 boolean isEligible = false;
        Map<String, List<Object>> buValues = getConfigParamService().
                getValuesForAllBUs(ConfigName.SMR_CLAIM_ALLOWED.getName());
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isEligible=true;
                 break;
              }
        }
        return isEligible;
    }
	
public boolean isTechnicianEnable(){
    boolean isEligible = false;
    Map<String, List<Object>> buValues = getConfigParamService().
            getValuesForAllBUs(ConfigName.ENABLE_TECHNICIAN.getName());
    for (String buName : buValues.keySet()) {
          Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
          if(booleanValue){
             isEligible=true;
             break;
          }
    }
    return isEligible;
}

public String getFaultCodeDescription() {
	return faultCodeDescription;
}

public void setFaultCodeDescription(String faultCodeDescription) {
	this.faultCodeDescription = faultCodeDescription;
}

public StateMandatesService getStateMandatesService() {
	return stateMandatesService;
}

public void setStateMandatesService(StateMandatesService stateMandatesService) {
	this.stateMandatesService = stateMandatesService;
}

public RuleAdministrationService getRuleAdministrationService() {
	return ruleAdministrationService;
}

public void setRuleAdministrationService(RuleAdministrationService ruleAdministrationService) {
	this.ruleAdministrationService = ruleAdministrationService;
}

public ValidationResults getMessages() {
	return messages;
}

public void setMessages(ValidationResults messages) {
	this.messages = messages;
}

/**
 * @return the incidentalsAvaialable
 */
public boolean isIncidentalsAvaialable() {
	return incidentalsAvaialable;
}

/**
 * @param incidentalsAvaialable the incidentalsAvaialable to set
 */
public void setIncidentalsAvaialable(boolean incidentalsAvaialable) {
	this.incidentalsAvaialable = incidentalsAvaialable;
}

/**
 * @return the claimCurrencyConversionAdvice
 */
public ClaimCurrencyConversionAdvice getClaimCurrencyConversionAdvice() {
	return claimCurrencyConversionAdvice;
}

/**
 * @param claimCurrencyConversionAdvice the claimCurrencyConversionAdvice to set
 */
public void setClaimCurrencyConversionAdvice(
		ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
	this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
}

/**
 * @return the countryStateService
 */
public CountryStateService getCountryStateService() {
	return countryStateService;
}

/**
 * @param countryStateService the countryStateService to set
 */
public void setCountryStateService(CountryStateService countryStateService) {
	this.countryStateService = countryStateService;
}

/**
 * @return the showClaimAudit
 */
public Boolean getShowClaimAudit() {
	return showClaimAudit;
}

/**
 * @param showClaimAudit the showClaimAudit to set
 */
public void setShowClaimAudit(Boolean showClaimAudit) {
	this.showClaimAudit = showClaimAudit;
}

/**
 * @return the claimAudit
 */
public ClaimAudit getClaimAudit() {
	return claimAudit;
}

/**
 * @param claimAudit the claimAudit to set
 */
public void setClaimAudit(ClaimAudit claimAudit) {
	this.claimAudit = claimAudit;
}

/**
 * @return the claimState
 */
public String getClaimState() {
	return claimState;
}

/**
 * @param claimState the claimState to set
 */
public void setClaimState(String claimState) {
	this.claimState = claimState;
}

/**
 * @return the claimSpecificAttributes
 */
public List<ClaimAttributes> getClaimSpecificAttributes() {
	return claimSpecificAttributes;
}

/**
 * @param claimSpecificAttributes the claimSpecificAttributes to set
 */
public void setClaimSpecificAttributes(List<ClaimAttributes> claimSpecificAttributes) {
	this.claimSpecificAttributes = claimSpecificAttributes;
}

/**
 * @return the policyDefinitionRepository
 */
public PolicyDefinitionRepository getPolicyDefinitionRepository() {
	return policyDefinitionRepository;
}

/**
 * @param policyDefinitionRepository the policyDefinitionRepository to set
 */
public void setPolicyDefinitionRepository(PolicyDefinitionRepository policyDefinitionRepository) {
	this.policyDefinitionRepository = policyDefinitionRepository;
}






}