/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

package tavant.twms.integration.layer.component.global;

import java.util.ArrayList;

import java.util.Currency;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import com.domainlanguage.money.Money;

import tavant.globalsync.supplierdebitnotification.DocumentIdsTypeDTO;
import tavant.globalsync.supplierdebitnotification.InvoiceTypeDTO;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.external.PaymentAsyncService;

import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.util.CalendarUtil;
/**
 * 
 * 
 * 
 */

public class ProcessGlobalSupplierDebitNotification {
	private static Logger logger = Logger.getLogger(ProcessGlobalSupplierDebitNotification.class.getName());
	
	private TransactionTemplate transactionTemplate;
	private RecoveryClaimService recoveryClaimService;
	private PaymentAsyncService paymentAsyncService;
	
	
	public List<SyncResponse> sync(final List<InvoiceTypeDTO> invoiceDtoList) {
		 if(logger.isDebugEnabled()){
	            logger.debug("Received " + invoiceDtoList.size()
	                    + " credit Notification(s).");
	        }		
		List<SyncResponse> responses = new ArrayList<SyncResponse>();
		for(final InvoiceTypeDTO invoiceTypeDTO : invoiceDtoList){
			SyncResponse response = new SyncResponse();
			DocumentIdsTypeDTO documentIds= invoiceTypeDTO.getHeader().getDocumentIds();
			response.setBusinessId(String.valueOf(documentIds.getDocumentId().getId()));
			response.setUniqueIdName("WarrantyClaimNumber");
			response.setUniqueIdValue(invoiceTypeDTO.getHeader().getUserArea().getWarrantyClaimNumber());
			try{
				validate(invoiceTypeDTO, response);
				transactionTemplate.execute(new TransactionCallbackWithoutResult(){
				
				protected void doInTransactionWithoutResult(TransactionStatus ts){
					CreditMemo creditMemo = transform(invoiceTypeDTO);
					paymentAsyncService.syncCreditMemo(creditMemo);
					}				
			});
			response.setSuccessful(true);
		}catch(RuntimeException e){
			logger.error(e, e);
			response = buildErrorResponse(response, e.getMessage(),documentIds.getDocumentId().getId());						
		}finally {
           responses.add(response);
        }		
	}
		return responses;
	}
	private CreditMemo transform(final InvoiceTypeDTO dto) {
		CreditMemo creditMemo = new CreditMemo();
		String claimNumber = dto.getHeader().getUserArea().getWarrantyClaimNumber();
		String claimNum = claimNumber;
		int indx = claimNumber.indexOf("_");
		if(indx>0){
			claimNum = claimNumber.substring(0, indx);
		}
		RecoveryClaim recoveryClaim = recoveryClaimService.findActiveRecoveryClaimForClaim(claimNum);
		creditMemo.setRecoveryClaim(recoveryClaim);
		creditMemo.setClaimNumber(claimNum);
		creditMemo.setCreditMemoDate(CalendarUtil.convertToCalendarDate(dto.getDocumentDateTime()));
		creditMemo.setCreditMemoNumber(dto.getHeader().getDocumentIds().getDocumentId().getId());
		
		Currency currency = Currency.getInstance(dto.getLine().getTotalAmount().getCurrency());
		
		Money tax= Money.valueOf(dto.getLine().getTax().getTaxAmount(), currency);
		creditMemo.setTaxAmount(tax);
		
		Money paidAmount = Money.valueOf(dto.getHeader().getTotalAmount(), currency);
		creditMemo.setPaidAmount(paidAmount);
		if(paidAmount.isNegative()){
			creditMemo.setCrDrFlag("CR");
		}else{
			creditMemo.setCrDrFlag("DR");
		}
		return creditMemo;
	}

	private void validate(
			final InvoiceTypeDTO invoiceTypeDTO, SyncResponse response) {
		StringBuilder errorMessage = new StringBuilder();
		if(!(StringUtils.hasText(invoiceTypeDTO.getHeader().getUserArea().getWarrantyClaimNumber().trim()))){					
			appendErrorMessage(errorMessage, " Warranty Claim Number can't be Null");					
		} 
		if(!(StringUtils.hasText(invoiceTypeDTO.getDocumentDateTime().trim()))){					
			appendErrorMessage(errorMessage, " Document Date Time can't be Null");					
		} 
		if(!(StringUtils.hasText(invoiceTypeDTO.getLine().getTotalAmount().getCurrency().trim()))){					
			appendErrorMessage(errorMessage, " Currency can't be Null");					
		} 
		if(!(StringUtils.hasText(invoiceTypeDTO.getLine().getTax().getTaxAmount()+"".trim()))){					
			appendErrorMessage(errorMessage, " Tax Amount can't be Null");					
		} 
		if(!(StringUtils.hasText(invoiceTypeDTO.getHeader().getTotalAmount()+"".trim()))){					
			appendErrorMessage(errorMessage, " Total Amount can't be Null");					
		} 
		if (StringUtils.hasText(errorMessage.toString())) {
			response.setErrorType(errorMessage.toString());
			throw new RuntimeException(errorMessage.toString());
		}
	}
	
	private void appendErrorMessage(StringBuilder errorMessage, String appendMessage) {
		if (StringUtils.hasText(errorMessage.toString())) {
			errorMessage.append(", ");
		}
		errorMessage.append(appendMessage);
	}
	
	private SyncResponse buildErrorResponse(SyncResponse response, String message, String documentId) {
		response.setSuccessful(false);
		response.setException(new StringBuilder().append(
				" Error syncing of Global Supplier Debit Submission with ").append(
						documentId).append("\n").append(
				" The Reason for the Error is : ").append(message).append("\n")
				.append("\n").toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		return response;
	}
	
	public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}
	
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	public void setPaymentAsyncService(PaymentAsyncService paymentAsyncService) {
		this.paymentAsyncService = paymentAsyncService;
	}
}