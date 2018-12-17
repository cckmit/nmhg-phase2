package tavant.twms.web.supplier;

import static tavant.twms.domain.claim.ClaimState.PROCESSOR_REVIEW;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.claim.payment.definition.PaymentSectionRepository;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.external.PaymentAsyncService;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingServiceImpl;
import tavant.twms.process.ProcessService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;

/**
 * 
 * @author kannan.ekanath
 * 
 */
@SuppressWarnings("serial")
public class SupplierRecoveryAdminAction extends AbstractSupplierActionSupport
		implements Preparable {
	public static final String SUPPLIER_RECOVERY = "SupplierRecovery";
	
	private static Logger logger = Logger
			.getLogger(SupplierRecoveryAdminAction.class);

	private String transitionTaken;

	/*
	 * private String transition;
	 */
	private WorkListItemService workListItemService;

	private PaymentAsyncService paymentAsyncService;

    //	public ClaimService claimService;
	/*
	 * PArt replaced Sercvice has been Commented As its not required as of now
	 */

	//private PartReplacedService partReplacedService;

	private PaymentSectionRepository paymentSectionRepository;

	private List<Long> partsNotToBeShown = new ArrayList<Long>();

	private String debitMemoNumber;

	private CalendarDate debitMemoDate;
	
	private String debitMemoComments; 

	private Money debitMemoAmount;

    // Debit memo amount which is collect as string in UI
    private String debitMemoAmountStr; 
    
    private List<String> currencies = new ArrayList<String>();
    private String debitMemoCurrencyStr;
    private boolean showDebitRelatedError;
    
	private ProcessService processService;

	private RecoveryClaim recoveryClaim;

	private Claim claim;

	boolean flag = false;

	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}

	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
		if (this.getTaskName().equals("Awaiting Shipment")
				&& getInboxViewId() != null
				&& getInboxViewId().equals(DEFAULT_VIEW_ID))
			return workListService
					.getSupplierRecoverySupplierPartReturnBasedView(criteria);
		else if (this.getInboxViewType() != null
				&& this.getInboxViewType().equals("part"))
			return this.workListService
					.getSupplierRecoveryPartBasedView(criteria);
		else
			return workListService.getSupplierRecoveryClaimBasedView(criteria);
	}

	@SuppressWarnings("unchecked")
	protected PageResult<?> getPageResult(List inboxItems,
			PageSpecification pageSpecification, int noOfPages) {
		return new PageResult<RecoveryClaim>(inboxItems, pageSpecification,
				noOfPages);
	}

	@Override
	protected String getAlias() {
		if ((getFolderName().endsWith("_part")))
			return "supplierPartReturns";
		else if (!WorkflowConstants.AWAITING_SHIPMENT.equals(getTaskName())
				|| (WorkflowConstants.AWAITING_SHIPMENT.equals(getTaskName())
						&& getInboxViewId() != null && !getInboxViewId()
						.equals(DEFAULT_VIEW_ID)))
			return "recoveryClaim";
		return null;

	}

	/*
	 * public String submitPreview() {
	 * 
	 * if (getRecoveryClaim().getRecoveryClaimAcceptanceReason() != null &&
	 * getRecoveryClaim().getRecoveryClaimAcceptanceReason().getCode() == null)
	 * { getRecoveryClaim().setRecoveryClaimAcceptanceReason(null); } if
	 * (getRecoveryClaim().getRecoveryClaimRejectionReason() != null &&
	 * getRecoveryClaim().getRecoveryClaimRejectionReason().getCode() == null) {
	 * getRecoveryClaim().setRecoveryClaimRejectionReason(null); }
	 * updateClaimAndPerformTransition();
	 * 
	 * if ("Accept".equals(this.transition)) {
	 * addActionMessage("message.sra.claimsAccepted"); } else {
	 * addActionMessage("message.sra.claimsDisputed"); } return SUCCESS; }
	 * 
	 * private void updateClaimAndPerformTransition() { No updation reqd in
	 * oemparts so commentin Api Rt now
	 * getRecoveryClaim().setLoggedInUser(getLoggedInUser()); //
	 * partReplacedService
	 * .updateOEMPartReplaced(getClaim().getServiceInformation
	 * ().getOemPartReplaced()); startSupplyPartReturnForClaim(); if
	 * (WorkflowConstants.ACCEPT.equals(this.transition)) { updatePayment(); }
	 * 
	 * if (!"".equals(this.transition)) { TaskInstance taskInstance =
	 * this.workListItemService
	 * .findTaskForRecClaimWithTaskName(getRecoveryClaim().getId(),
	 * getTaskName());
	 * this.workListItemService.endTaskWithTransition(taskInstance,
	 * this.transition); } }
	 */
	public String preview() {
		fetchClaimView();
		return SUCCESS;
	}

	@Override
	public void validate() {
		try {
			if (!StringUtils.hasText(getRecoveryClaim().getComments())) {
				addActionError("error.supplier.requiredReason");
			}
			if (WorkflowConstants.ACCEPT.equals(this.transitionTaken)
					&& (getRecoveryClaim().getRecoveryClaimAcceptanceReason() == null || !StringUtils
							.hasText(getRecoveryClaim()
									.getRecoveryClaimAcceptanceReason()
									.getCode()))) {
				addActionError("error.supplier.requiredAcceptanceReason");
			}

			if (WorkflowConstants.TRANSFER.equals(this.transitionTaken)
					&& this.getTransferToUser() == null) {
				addActionError("error.supplier.requiredTransferToUser");
			}
			if ((WorkflowConstants.SEND_TO_SUPPLIER
					.equals(this.transitionTaken) || WorkflowConstants.ACCEPT
					.equals(this.transitionTaken))
					&& getRecoveryClaim().getSupplierUserForRecoveryClaim() == null) {
				addActionError("error.supplier.noSupplier");
			}
			if (WorkflowConstants.CANNOT_RECOVER.equals(this.transitionTaken)
					&& (getRecoveryClaim()
							.getRecoveryClaimCannotRecoverReason() == null || !StringUtils
							.hasText(getRecoveryClaim()
									.getRecoveryClaimCannotRecoverReason()
									.getCode()))) {
				addActionError("error.supplier.requiredCannotRecoverReason");
			}
			validateDocumentTypeForTheAttachment();
		} catch (Exception e) {
			logger.error("Error while validating the claim", e);
		}
	}

	public void prepare() {
		prepareClaimView();
	}

	public String detail() {
		fetchClaimView();
		if (hasActionErrors())
			return INPUT;
		else {
			fetchSuppliers();
          
          if (getShowDebitRelatedError() == true) {
        	  checkDebitMemoInputs(true); // Passing true so that error messages will be set
        	  // We would still return SUCCESS from here as we want to show the page
          }
          
			if (!fetchedRecoveryClaim.getRecoveryClaimInfo()
					.getRecoverableParts().isEmpty()
					&& isBuConfigAMER()) {
				for (RecoverablePart recPart : fetchedRecoveryClaim
						.getRecoveryClaimInfo().getRecoverableParts()) {
					if (null != recPart.getOemPart().getActivePartReturn()
							&& recPart.getOemPart().getActivePartReturn().getStatus()
									.equals(PartReturnStatus.PART_TO_BE_SHIPPED)
							&& recPart.isSupplierReturnNeeded())
						addActionWarning("message.supplier.partShipped");
				}
			}
          
			return SUCCESS;
		}
	}


    /**
     * Checks the input given by user for Debit memo.
     * @param setErrorMessage if <code>true</code> then sets relevant error message(s)
     * @return <code>false</code> is returned if an error was found
     */
    private boolean checkDebitMemoInputs(boolean setErrorMessage) {
		boolean errorFound = false;

		if (getDebitMemoDate() == null) {
			errorFound = true;
			if (setErrorMessage) {
				addActionError("error.sra.invalidMemoDate");
			}


		}
		else {
			CalendarDate debitMemoDate = getDebitMemoDate();
			
			CalendarDate debitMemoCD = CalendarDate.from(debitMemoDate.breachEncapsulationOf_year(), debitMemoDate.breachEncapsulationOf_month(), debitMemoDate.breachEncapsulationOf_day());
			
			Calendar today = new GregorianCalendar();
			CalendarDate todayCD = CalendarDate.from(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
			
			if (debitMemoCD.compareTo(todayCD) == 1) {
				errorFound = true;
				if (setErrorMessage) {
					addActionError("error.sra.invalidMemoDate");
				}
			}
		}

		if (StringUtils.hasText(getDebitMemoNumber()) == false) {
			errorFound = true;
			if (setErrorMessage) {
				addActionError("error.sra.emptyMemoNumber");
			}
		}

		if (StringUtils.hasText(getDebitMemoAmountStr()) == false) {
			errorFound = true;
			if (setErrorMessage) {
				addActionError("error.sra.invalidCreditAmount");
			}
		}
		else {
			try {
				BigDecimal bd = new BigDecimal(getDebitMemoAmountStr());
				
				String zeroFracCurrencies[] = new String[]{"JPY", "ESP", "ITL", "BEF"};
				String threeFracCurrencies[] = new String[]{"BHD"};
				List<String> zeroFracCurrlist = Arrays.asList(zeroFracCurrencies);
				List<String> threeFracCurrlist = Arrays.asList(threeFracCurrencies);
				int scale = 0;
				if (zeroFracCurrlist.contains(getDebitMemoCurrencyStr())) {
					scale = 0;
				}
				else  if (threeFracCurrlist.contains(getDebitMemoCurrencyStr())) {
					scale = 3;
				}
				else {
					scale = 2;
				}
				BigDecimal bd1 = new BigDecimal(getDebitMemoAmountStr()).setScale(scale, RoundingMode.HALF_UP);
				
				setDebitMemoAmountStr(bd1.toPlainString());
				
			} catch (NumberFormatException nfe) {
				errorFound = true;
				if (setErrorMessage) {
					addActionError("error.sra.invalidCreditAmount");
				}
			}
		}

		if (StringUtils.hasText(getDebitMemoCurrencyStr()) == false || getDebitMemoCurrencyStr().equals("-1")) {
			// -1 will be the value when the user selects the header value (which is a non-selection)
			errorFound = true;
			if (setErrorMessage) {
				addActionError("error.sra.emptyCreditCurrency");
			}
		}		

		return errorFound;
    }
    
    
    
	private void prepareClaimView() {

		if (getId() == null && getRecoveryClaim() != null)
			setId(getRecoveryClaim().getId().toString());
		if (getId() != null && getTaskName() != null) {
			Assert.hasText(getId(), "Id should not be empty for fetch");
			TaskInstance taskInstance;
			if ("Awaiting Shipment".equalsIgnoreCase(getTaskName())
					&& getInboxViewId() != null
					&& getInboxViewId().equals(DEFAULT_VIEW_ID))
				taskInstance = workListItemService.findTask(new Long(getId())
						.longValue());
			else
				taskInstance = workListItemService
						.findTaskForRecClaimWithTaskName(
								new Long(getId()).longValue(), getTaskName());

			if (taskInstance != null) {
				fetchedRecoveryClaim = (RecoveryClaim) taskInstance
						.getVariable("recoveryClaim");
				if (fetchedRecoveryClaim != null) {
					SelectedBusinessUnitsHolder
							.setSelectedBusinessUnit(fetchedRecoveryClaim
									.getBusinessUnitInfo().getName());
				}
			} else {
				if (!flag) {
					addActionError("error.recoveryClaim.actionTaken");
					flag = true;
				}
			}
		}
    	
    	// Start - Fix part for SLMSPROD-593
    	String allCurrencies[] = new String[] {
    			"EUR", "GBP", "USD", "NLG", "DEM", "FRF", "JPY", "ESP", "ITL", "BEF", 
    			"DKK", "AUD", "CAD", "CHF", "SEK", "NOK", "IEP", "FIM", "ZAR", "ATS", 
    			"BRL", "MXN", "HKD", "SGD", "CNY", "NZD", "INR", "AED", "BHD", "SAR", 
    			"RUB", "PLN", "THB", "MYR"};
    	
    	Arrays.sort(allCurrencies);
    	
    	for (String curr : allCurrencies) {
    		currencies.add(curr);
	}
    	// End - Fix part for SLMSPROD-593
  }

	private void fetchClaimView() {
		if (fetchedRecoveryClaim == null)
			prepareClaimView();
		setRecoveryClaim(fetchedRecoveryClaim);
		if (getRecoveryClaim() != null) {
			getRecoveryClaim().setLoggedInUser(getLoggedInUser());
			getRecoveryClaim().setCurrentAssignee(
					fetchCurrentClaimAssignee(fetchedRecoveryClaim.getId()));
			setClaim(getRecoveryClaim().getClaim());
		}
	}

	public String getSwimlaneRole() {
		Assert.hasText(getId(), "Id should not be empty for fetch");
		TaskInstance taskInstance = workListItemService
				.findTaskForRecClaimWithTaskName(new Long(getId()).longValue(),
						getTaskName());
		return taskInstance.getSwimlaneInstance().getName();
	}

	public String summary() {
		updateAllCostLineItems();
		return SUCCESS;
	}

	public String submit() {
		getId();				 
		if (getRecoveryClaim().getRecoveryClaimAcceptanceReason() != null
				&& getRecoveryClaim().getRecoveryClaimAcceptanceReason()
						.getCode() == null) {
			getRecoveryClaim().setRecoveryClaimAcceptanceReason(null);
		} else if (getRecoveryClaim().getRecoveryClaimCannotRecoverReason() != null
				&& getRecoveryClaim().getRecoveryClaimCannotRecoverReason()
						.getCode() == null) {
			getRecoveryClaim().setRecoveryClaimCannotRecoverReason(null);
		}

		updateAllCostLineItems();
		// P2 UAT: Automatic part return Changes(NMHGSLMS-1117)
		if ("AMER".equalsIgnoreCase(getRecoveryClaim().getClaim().getBusinessUnitInfo().getName())) 
		{
			if(this.transitionTaken.equalsIgnoreCase("Send To Supplier"))
			{
				if(isReturnThroughDealerDirectly()){
					startPartReturnForRecoveryClaim();
				}else{
					startSupplyPartReturnForClaim();
				}
			}
		}
		else
		{
			if(this.transitionTaken.equalsIgnoreCase("On Hold") || this.transitionTaken.equalsIgnoreCase("Accept") || this.transitionTaken.equalsIgnoreCase("Transfer") || this.transitionTaken.equalsIgnoreCase("Send To Supplier")){
				if(isReturnThroughDealerDirectly()){
					startPartReturnForRecoveryClaim();
				}else{
					startSupplyPartReturnForClaim();
				}

			}
		}

		getRecoveryClaim().setLoggedInUser(getLoggedInUser());
		if (StringUtils.hasText(transitionTaken)) {
			transitionTaken = transitionTaken.trim();
		}
		if (WorkflowConstants.ACCEPT.equals(transitionTaken)) {
			updatePayment();
		}
		TaskInstance taskInstance = workListItemService
				.findTaskForRecClaimWithTaskName(getRecoveryClaim().getId(),
						getTaskName());
        if(taskInstance == null){
            addActionError("error.common.simultaneousAction");
            return INPUT;
        }
		else if ("Transfer".equals(transitionTaken)) {
			workListItemService.endTaskWithReassignment(taskInstance,
					transitionTaken, getTransferToUser().getName());
		} else {
			try {
				workListItemService.endTaskWithTransition(taskInstance,
						transitionTaken);
			} catch (Exception e) {
				logger.error("Error while transitioning the recovery claim", e);
				return INPUT;
			}
		}
        
        if(("AMER".equalsIgnoreCase(getRecoveryClaim().getClaim().getBusinessUnitInfo().getName())) && this.transitionTaken.equalsIgnoreCase("Send To Supplier") && isPartReturnRequested()){
    		TaskInstance taskInstanceOne = this.workListItemService
    				.findTaskForRecClaimWithTaskName(
    						getRecoveryClaim().getId(), "New");
            if(taskInstanceOne == null){
                addActionError("error.common.simultaneousAction");
            }else{
    		this.workListItemService.endTaskWithTransition(taskInstanceOne,
    				"On Hold For Part Return");
            }
        }

		// Update the history on hold// ideally this should be taken care by the
		// jbpm but jbpm does not process if the transition is not changing
		if (taskInstance != null
				&& taskInstance.getName().equals(this.transitionTaken)) {
			getRecoveryClaim().setRecoveryClaimState(
					getRecoveryClaim().getRecoveryClaimState());
			getRecoveryClaimService().updateRecoveryClaim(getRecoveryClaim());
		}
		
		if ("On Hold".equals(transitionTaken))
			addActionMessage("message.sra.claimsMarkedOnHold");
		else if ("Send To Supplier".equals(transitionTaken))
			addActionMessage("message.sra.claimsMarkedForRecovery");
		else if ("Cannot Recover".equals(transitionTaken))
			addActionMessage("message.sra.claimsMarkedCannotRecover");
		else if ("Not For Recovery".equals(transitionTaken))
			addActionMessage("error.sra.claimNotForRecovery");
		else if ("Accept".equals(transitionTaken)) {
			addActionMessage("message.sra.claimsAccepted");
		} else if ("Transfer".equals(transitionTaken)) {
			addActionMessage("message.sra.claimTransferred");
		}
		return SUCCESS;
	}

	public String reopen() {
		/*
		 * if
		 * (!ClaimState.ACCEPTED_AND_CLOSED.equals(getRecoveryClaim().getClaim
		 * ().getState())) { addActionError("error.warrantyClaim.open"); return
		 * INPUT; } else {
		 */
		TaskInstance taskInstance = workListItemService
				.findTaskForRecClaimWithTaskName(getRecoveryClaim().getId(),
						getTaskName());
		getRecoveryClaim().setLoggedInUser(getLoggedInUser());
		if (taskInstance != null) {
			workListItemService.endTaskWithTransition(taskInstance,
					transitionTaken);
		} else if ("Reopen".equals(transitionTaken)) {
			this.processService
					.startProcessWithTransition(
							SUPPLIER_RECOVERY,
							createProcessVariablesForReopenRecClaims(getRecoveryClaim()),
							"Reopen");
		} else {
			Assert.state(taskInstance != null,
					"No Task Instances are associated with the given claim");
		}
		if (WorkflowConstants.REJECT.equals(transitionTaken)) {
			addActionMessage("message.sra.claimsDisputed");
		} else {
			addActionMessage("message.sra.reopened");
		}
		return SUCCESS;
		// }
	}

	private ProcessVariables createProcessVariablesForReopenRecClaims(
			RecoveryClaim recoveryClaim) {
		ProcessVariables processVariables = new ProcessVariables();

		if (recoveryClaim instanceof HibernateProxy) {
			recoveryClaim = (RecoveryClaim) ((HibernateProxy) recoveryClaim)
					.getHibernateLazyInitializer().getImplementation();
		}
		processVariables.setVariable("recoveryClaim", recoveryClaim);
		return processVariables;
	}

	public String debitSupplierRecoveryClaim() {
		//validate for attachment
		validateDocumentTypeForTheAttachment();
       if(hasActionErrors()){
           return INPUT;
       }
    	
    	if (checkDebitMemoInputs(false)) {
    		setShowDebitRelatedError(true);
    		return "db-memo-invalid-input";
    	}
    	
		addActionMessage("message.sra.debit.notified");
		return syncPaymentMadeForSRClaims();
	}

	private String syncPaymentMadeForSRClaims() {
		RecoveryClaim recClaim = getRecoveryClaim();
		recClaim.setLoggedInUser(getLoggedInUser());
		CreditMemo creditMemo = new CreditMemo();
		creditMemo.setRecoveryClaim(recClaim);
		creditMemo.setClaimNumber(recClaim.getClaim().getClaimNumber());
		creditMemo.setCreditMemoDate(getDebitMemoDate());
		creditMemo.setCreditMemoNumber(getDebitMemoNumber());
		Money totalRecoveredCost = null;
		List<RecoveryClaimAudit> recoveryClaimAudit = recClaim
				.getRecoveryClaimAudits();
		Money acceptedAmount = recoveryClaimAudit.get(
				recoveryClaimAudit.size() - 1).getAcceptedAmount();
		if (acceptedAmount != null) {
			totalRecoveredCost = acceptedAmount;
		} else {
			if (recClaim.getRecoveryPayment() != null
					&& recClaim.getRecoveryPayment().getPreviousPaidAmount() != null) {
				totalRecoveredCost = recClaim.getTotalRecoveredCost().minus(
						recClaim.getRecoveryPayment().getPreviousPaidAmount());
			} else {
				totalRecoveredCost = recClaim.getTotalRecoveredCost();
			}
		}
		
		creditMemo.setTaxAmount(Money.valueOf(0,
				totalRecoveredCost.breachEncapsulationOfCurrency()));
		if (totalRecoveredCost.isNegative()) {
			creditMemo.setCrDrFlag("CR");
		} else {
			creditMemo.setCrDrFlag("DR");
		}
		
        Currency creditMemoCurrency = Currency.getInstance(getDebitMemoCurrencyStr()); // Assumed that proper currency code will be sent
        BigDecimal creditMemoAmountBD = new BigDecimal(getDebitMemoAmountStr()); // Assumed that proper amount will be sent to this method
        Money creditMemoAmount = new Money(creditMemoAmountBD, creditMemoCurrency);
        creditMemo.setCreditAmount(creditMemoAmount);
        creditMemo.setCreditMemoComments(getDebitMemoComments());
        totalRecoveredCost=creditMemoAmount;
        creditMemo.setPaidAmount(totalRecoveredCost);
        recClaim.getActiveRecoveryClaimAudit().setRecoveredAmount(totalRecoveredCost);
        this.paymentAsyncService.syncCreditMemo(creditMemo);
		return SUCCESS;
	}

	private void updateAllCostLineItems() {
		getRecoveryClaimService().updateRecoveryClaim(getRecoveryClaim());
	}

	// public Map<Long, Integer> getSuppliersCountMap() {
	// Map<Long, Integer> suppliersCountMap = new HashMap<Long, Integer>();
	//
	// for (OEMPartReplaced oemPartReplaced : getShipOEMParts()) {
	// if (oemPartReplaced.isPartRecoveredFromSupplier(null, null, null)) {
	// SupplierPartReturn supplierPartReturn =
	// oemPartReplaced.getSupplierPartReturn();
	// if (supplierPartReturn != null) {
	// Long supplierId = getRecoveryClaim().getSupplier().getId();
	// if (suppliersCountMap.containsKey(supplierId)) {
	// suppliersCountMap.put(supplierId, (Integer)
	// suppliersCountMap.get(supplierId) + 1);
	// } else {
	// suppliersCountMap.put(supplierId, 1);
	// }
	// }
	// }
	// }
	// return suppliersCountMap;
	// }

	public List<String> getAllTransitions(RecoveryClaim recClaim)
			throws JSONException {
		List<String> allTransitions = new ArrayList<String>();
		if (!RecoveryClaimState.ACCEPTED.equals(recClaim
				.getRecoveryClaimState())) {

			allTransitions.add(getText("label.supplier.onHold").trim());
			allTransitions.add(getText("label.supplier.cannotRecover").trim());
			allTransitions.add(getText("label.supplier.transfer").trim());
			allTransitions.add(getText("label.supplier.sendToSupplier").trim());
			allTransitions.add(getText("label.supplier.notForRecovery").trim());
			allTransitions.add(getText("label.common.accept").trim());
		} else if (!RecoveryClaimState.REJECTED.equals(recClaim
				.getRecoveryClaimState())) {
			allTransitions
					.add(getText("label.supplier.closedRecovered").trim());
		}
		return allTransitions;
	}
	
	public boolean isTaskNameofCurrentRecoveryClaim() {
		if (this.getTaskName().equals("Not For Recovery Request")
				|| this.getTaskName().equals("Supplier Accepted")				
				|| this.getTaskName().equals("Awaiting Shipment")
				|| this.getTaskName().equals("Awaiting Supplier Response")) {
			return true;
		}
		return false;
	}

	/*
	 * public boolean isDisputeValid() { List<RecoveryClaimAudit>
	 * recoveryClaimAudits = getRecoveryClaim().getRecoveryClaimAudits(); int
	 * noOfDisputesDone=0; boolean canDispute = true; Long maxDisputeAllowed =
	 * null;
	 * 
	 * //added by arindam for restricting the disputation number
	 * maxDisputeAllowed=
	 * this.configParamService.getLongValue(ConfigName.MAXIMUM_DISPUTATION_ALLOWED
	 * .getName()); for (RecoveryClaimAudit recAudit :
	 * getRecoveryClaim().getRecoveryClaimAudits()) {
	 * if(RecoveryClaimState.REJECTED
	 * .getState().equals(recAudit.getRecoveryClaimState().getState())){
	 * noOfDisputesDone++; } } if(noOfDisputesDone>=maxDisputeAllowed){
	 * canDispute=false; } return canDispute; }
	 */

	public String getTransitionTaken() {
		return transitionTaken;
	}

	public void setTransitionTaken(String transitionTaken) {
		this.transitionTaken = transitionTaken;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public PaymentSectionRepository getPaymentSectionRepository() {
		return paymentSectionRepository;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<Long> getPartsNotToBeShown() {
		return partsNotToBeShown;
	}

	public void setPartsNotToBeShown(List<Long> partsNotToBeShown) {
		this.partsNotToBeShown = partsNotToBeShown;
	}

	public boolean isCommentViewable(ClaimState claimState) {
		List<ClaimState> notViewableClaimStates = new ArrayList<ClaimState>();
		notViewableClaimStates.add(PROCESSOR_REVIEW);
		if (notViewableClaimStates.contains(claimState)) {
			return false;
		}
		return true;
	}

	public boolean isCommentsExist(Claim claim) {
		for (ClaimAudit claimAudit : claim.getClaimAudits()) {
			if (StringUtils.hasText(claimAudit.getInternalComments())  && !claimAudit.getUpdatedBy().hasRole(Role.SYSTEM)) {
				return true;
			}
		}
		return false;
	}
	
	private void validateDocumentTypeForTheAttachment(){
    	List<Document> attachments = getRecoveryClaim().getAttachments();  
    	attachments.removeAll(Collections.singleton(null));
    	if(attachments != null){
    		for(Document doc : attachments){
        		if(doc.getDocumentType() == null){
        			addActionError("error.selectDocumentType");
        		}
        	}
    	}
    	
    }
	
	public String getDebitMemoComments() {
		return debitMemoComments;
	}

	public void setDebitMemoComments(String debitMemoComments) {
		this.debitMemoComments = debitMemoComments;
	}

	public Money getDebitMemoAmount() {
		return debitMemoAmount;
	}

	public void setDebitMemoAmount(Money debitMemoAmount) {
		this.debitMemoAmount = debitMemoAmount;
	}

	public void setShowDebitRelatedError(boolean showDebitRelatedError) {
		this.showDebitRelatedError = showDebitRelatedError;
	}
	
	public boolean getShowDebitRelatedError() {
		return showDebitRelatedError;
	}
	
	public String getDebitMemoAmountStr() {
		return debitMemoAmountStr;
	}

	public void setDebitMemoAmountStr(String debitMemoAmountStr) {
		this.debitMemoAmountStr = debitMemoAmountStr;
	}
	
	public List<String> getCurrencies() {
		return currencies;
	}
	
	public void setCurrencies(List<String> currencies) {
		this.currencies = currencies;
	}
	
	public String getDebitMemoCurrencyStr() {
		return debitMemoCurrencyStr;
	}
	
	public void setDebitMemoCurrencyStr(String debitMemoCurrencyStr) {
		this.debitMemoCurrencyStr = debitMemoCurrencyStr;
	}

	public CalendarDate getDebitMemoDate() {
		return debitMemoDate;
	}

	public void setDebitMemoDate(CalendarDate debitMemoDate) {
		this.debitMemoDate = debitMemoDate;
	}

	public String getDebitMemoNumber() {
		return debitMemoNumber;
	}

	public void setDebitMemoNumber(String debitMemoNumber) {
		this.debitMemoNumber = debitMemoNumber;
	}

	public void setPaymentAsyncService(PaymentAsyncService paymentAsyncService) {
		this.paymentAsyncService = paymentAsyncService;
	}
}
