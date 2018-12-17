package tavant.twms.integration.layer.component.global;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.globalsync.warrantyclaimcreditnotification.ApplicationAreaTypeDTO;
import tavant.globalsync.warrantyclaimcreditnotification.InvoiceTypeDTO;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.external.PaymentAsyncService;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.constants.CreditInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.InstallBaseSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UploadSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.util.CalendarUtil;

import com.domainlanguage.money.Money;

public class ProcessGlobalCreditNotification {
    private static Logger logger = Logger.getLogger(ProcessGlobalCreditNotification.class
            .getName());

    private PaymentAsyncService paymentAsyncService;
    
    private CreditInterfaceErrorConstants creditInterfaceErrorConstants;

    private TransactionTemplate transactionTemplate;

    private ClaimService claimService;
        
    private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;

    public void setPaymentAsyncService(PaymentAsyncService paymentService) {
        this.paymentAsyncService = paymentService;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public List<SyncResponse> sync(final List<InvoiceTypeDTO> invoiceDtoList, final ApplicationAreaTypeDTO applicationArea) {
        if (logger.isDebugEnabled()) {
            logger.debug("Received " + invoiceDtoList.size()
                    + " credit Notification(s).");
        }

        List<SyncResponse> responses = new ArrayList<SyncResponse>();

        for (final InvoiceTypeDTO invoiceTypeDTO : invoiceDtoList) {
            SyncResponse response = new SyncResponse();
            final Map<String,String> errorMessageCodes = new HashMap<String,String>();
            response.setTask(applicationArea.getSender().getTask());
            response.setLogicalId(applicationArea.getSender().getLogicalId());
            response.setReferenceId(applicationArea.getSender().getReferenceId());
            response.setInterfaceNumber(applicationArea.getInterfaceNumber());
            response.setBodId(applicationArea.getBODId());
            response.setCreationDateTime(applicationArea.getCreationDateTime());
            response.setBusinessId(invoiceTypeDTO.getHeader().getDocumentIds().getDocumentId().getId());
            response.setUniqueIdName(IntegrationConstants.CLAIM_NUMBER);
            response.setUniqueIdValue(invoiceTypeDTO.getHeader().getUserArea().getClaimNumber());

            try {
                transactionTemplate
                        .execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(
                                    TransactionStatus ts) {
                                    CreditMemo creditMemo = transform(invoiceTypeDTO,errorMessageCodes);
                                    paymentAsyncService.syncCreditMemo(creditMemo);
                                   
                                }
                        });
	             if(!errorMessageCodes.isEmpty()){
	 		           response.setErrorMessages(errorMessageCodes);
	 		           response.setSuccessful(false);
	             }else{
	          	   response.setErrorMessages(errorMessageCodes);
	          	   response.setSuccessful(true);
	             }
            } catch(IllegalArgumentException  ex){
				String error = ex.getMessage();
				if(error != null){
					errorMessageCodes.put("Xml Validation Error", error);
				}
				response = buildErrorResponse(response, ex.getMessage(),
	         			  invoiceTypeDTO.getHeader().getUserArea().getClaimNumber(),errorMessageCodes);
			} catch (RuntimeException e) {
            	logger.error("Exception Occurred in GlobalCreditNotification!!", e);
    			
    			if(!errorMessageCodes.isEmpty()){
         		   response.setErrorMessages(errorMessageCodes);
         	   }else{
         		   if(e.getMessage()!=null){
         			   String errorMessage = e.getMessage();
         			   if(errorMessage!=null){
         				   String[] message = errorMessage.split(":");
         				   if(message.length>1){
         					  String messageKey=creditInterfaceErrorConstants.getErrorMessage(message[0]);
         					  if(messageKey!=null){
         					   errorMessageCodes.put(message[0],message[1]);
         					  }
         				   }
         			   }
         		   }
         		   response.setErrorMessages(errorMessageCodes);
         	   }
         	   response = buildErrorResponse(response, e.getMessage(),
         			  invoiceTypeDTO.getHeader().getUserArea().getClaimNumber(),errorMessageCodes);
            } finally {
            	response.setErrorMessages(errorMessageCodes);
                responses.add(response);
            }
        }
        return responses;
    }
    
    private SyncResponse buildErrorResponse(SyncResponse response,
			String message, String claimNumber, Map<String,String> errorMessageCodes) {
		response.setSuccessful(false);
		response.setException(new StringBuilder()
				.append(" Error Syncing Credit Notification, with Claim Number: ")
				.append(claimNumber)
				.append(" The Reason for the Error is : ").append(message)
				.append("\n").append("\n").toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		if (errorMessageCodes.isEmpty()) {
			errorMessageCodes
					.put(CreditInterfaceErrorConstants.CN008,
							creditInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(CreditInterfaceErrorConstants.CN008));
		}
		response.setErrorMessages(errorMessageCodes);
		return response;
	}

    private CreditMemo transform(final InvoiceTypeDTO dto,final Map<String,String> errorMessageCodes) {
        CreditMemo creditMemo = new CreditMemo();
        String claimNumber = dto.getHeader().getUserArea().getClaimNumber().trim();
        int idx = claimNumber.indexOf("_");
        if(idx==-1){
        	errorMessageCodes.put(CreditInterfaceErrorConstants.CN007, creditInterfaceErrorConstants.getPropertyMessageFromErrorCode(CreditInterfaceErrorConstants.CN007));
        	throw new RuntimeException(creditInterfaceErrorConstants.getPropertyMessageFromErrorCode(CreditInterfaceErrorConstants.CN007));
        }
        String claimNo = claimNumber.substring(0, idx);
        creditMemo.setClaimNumber(claimNo);
       
        /**
         * @ToDo Web methods would sent date time in GMT, we should store them as per system calendar
         * please verify
         */
        creditMemo.setCreditMemoDate(CalendarUtil.convertToCalendarDate(dto.getHeader().getDocumentDateTime()));
        String creditMemoNumber = dto.getHeader().getDocumentIds().getDocumentId().getId();
		creditMemo.setCreditMemoNumber(creditMemoNumber);
		Currency currency = Currency.getInstance(dto.getHeader().getCurrency().toString());
		Money tax = Money.valueOf(dto.getLine().getTax().getTaxAmount(), currency).abs();
        Money actualAllocatedAmt = Money.valueOf(dto.getHeader().getTotalAmount(), currency);
        actualAllocatedAmt = actualAllocatedAmt.minus(Money.valueOf(dto.getLine().getTax().getTaxAmount(), currency));
		if(actualAllocatedAmt.isNegative()){
        	creditMemo.setCrDrFlag(IntegrationConstants.CR);
        }else{
        	creditMemo.setCrDrFlag(IntegrationConstants.DR);
        }
         	 Claim claim = claimService.findClaimByNumber(claimNo);
         	 if(claim==null){
         		throw new RuntimeException(creditInterfaceErrorConstants.getPropertyMessageFromErrorCode((CreditInterfaceErrorConstants.CN001))); 
         	 }
        	 String erpCurrency = claimCurrencyConversionAdvice.getCurrencyForERPInteractions(claim);
        	 String claimCurrency = claim.getCurrencyForCalculation().getCurrencyCode();
        	 if (!erpCurrency.equalsIgnoreCase(actualAllocatedAmt.breachEncapsulationOfCurrency().getCurrencyCode())) {
      			throw new RuntimeException(creditInterfaceErrorConstants.getPropertyMessage((CreditInterfaceErrorConstants.CN006),new String[] { erpCurrency, actualAllocatedAmt.breachEncapsulationOfCurrency().getCurrencyCode()}));
      		}else{
      			Money amountInERPCurrency = getClaimedAmount(claim,erpCurrency);
      			      			
      			if (claimCurrency.equalsIgnoreCase(IntegrationConstants.USD) && !(amountInERPCurrency.abs()).equals(actualAllocatedAmt.abs())) {
     				throw new RuntimeException(creditInterfaceErrorConstants.getPropertyMessageFromErrorCode(CreditInterfaceErrorConstants.CN002));
     			}
     			else if (amountInERPCurrency.breachEncapsulationOfAmount().abs().intValue()!=actualAllocatedAmt.breachEncapsulationOfAmount().abs().intValue()){
     				throw new RuntimeException(creditInterfaceErrorConstants.getPropertyMessageFromErrorCode(CreditInterfaceErrorConstants.CN003));
     			}
      			
      		}
        	 if(tax.isZero()){
        		 creditMemo.setPaidAmount(getClaimedAmount(claim,claimCurrency));
        	 }else{
        		 creditMemo.setPaidAmount(claimCurrencyConversionAdvice.convertMoneyUsingAppropriateConFactor(actualAllocatedAmt,claim.getRepairDate(),Currency.getInstance(claimCurrency)));
        	 }
        	creditMemo.setTaxAmountErpCurrency(tax);
        	creditMemo.setPaidAmountErpCurrency(actualAllocatedAmt);
        	creditMemo.setTaxAmount(claimCurrencyConversionAdvice.convertMoneyUsingAppropriateConFactor(tax, claim.getRepairDate(),Currency.getInstance(claimCurrency)));
        	     
        return creditMemo;
    }

	private Money getClaimedAmount(Claim claim, String currency) {
		Currency resultCurrency = Currency.getInstance(currency);
		if (claim == null) {
			throw new RuntimeException(
					creditInterfaceErrorConstants
							.getPropertyMessageFromErrorCode(CreditInterfaceErrorConstants.CN001));
		}
		Payment payment = claim.getPayment();
		List<LineItemGroup> lineItemGroups = payment.getLineItemGroups();
		Money totalClaimedAmt = Money.valueOf(0, resultCurrency);
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if (!lineItemGroup.getName().equalsIgnoreCase(
					IntegrationConstants.CLAIM_AMOUNT)
					&& !lineItemGroup.getName()
							.equalsIgnoreCase(Section.TRAVEL)
					&& !lineItemGroup.getName()
							.equalsIgnoreCase(Section.OTHERS)) {
				/*if (lineItemGroup.getName().equalsIgnoreCase(Section.LATE_FEE)) {
					totalClaimedAmt = totalClaimedAmt
							.minus(claimCurrencyConversionAdvice
									.convertMoneyUsingAppropriateConFactor(
											lineItemGroup
													.getTotalCreditAmount()
													.abs(), claim
													.getRepairDate(),
											resultCurrency).negated());*/
				 if(lineItemGroup.getName().equalsIgnoreCase(Section.DEDUCTIBLE)){
					totalClaimedAmt = totalClaimedAmt
					.minus(claimCurrencyConversionAdvice
							.convertMoneyUsingAppropriateConFactor(
									lineItemGroup.getTotalCreditAmount(),
									claim.getRepairDate(), resultCurrency));
				}
				else{
				totalClaimedAmt = totalClaimedAmt
						.plus(claimCurrencyConversionAdvice
								.convertMoneyUsingAppropriateConFactor(
										lineItemGroup.getTotalCreditAmount(),
										claim.getRepairDate(), resultCurrency));
				}
			}
		}
		return totalClaimedAmt.negated();
	}
	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public CreditInterfaceErrorConstants getCreditInterfaceErrorConstants() {
		return creditInterfaceErrorConstants;
	}

	public void setCreditInterfaceErrorConstants(
			CreditInterfaceErrorConstants creditInterfaceErrorConstants) {
		this.creditInterfaceErrorConstants = creditInterfaceErrorConstants;
	}

	public void setClaimCurrencyConversionAdvice(ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}
}
