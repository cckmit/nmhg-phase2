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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import tavant.globalsync.exchangeratesync.ExchangeRateDocumentDTO;
import tavant.globalsync.exchangeratesync.ExchangeRateDocumentDTO.ExchangeRate;
import tavant.globalsync.exchangeratesync.ExchangeRateDocumentDTO.ExchangeRate.ApplicationArea;
import tavant.globalsync.exchangeratesync.ExchangeRateDocumentDTO.ExchangeRate.ApplicationArea.Sender;
import tavant.globalsync.exchangeratesync.ExchangeRateDocumentDTO.ExchangeRate.DataArea.CurrencyDetails;
import tavant.globalsync.exchangeratesync.ExchangeRateDocumentDTO.ExchangeRate.DataArea.CurrencyDetails.CurrencyConversion;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyConversionFactorService;
import tavant.twms.domain.common.CurrencyExchangeRate;
import tavant.twms.domain.common.CurrencyExchangeRateService;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.constants.ExchangeRateErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.CalendarUtil;

public class ProcessGlobalExchangeRate extends IntegrationConstants {

	private static Logger logger = Logger.getLogger(ProcessGlobalExchangeRate.class.getName());
	private TransactionTemplate transactionTemplate;
	private CurrencyExchangeRateService currencyExchangeRateService;
	private CurrencyConversionFactorService currencyConversionFactorService;
	private ExchangeRateErrorConstants exchangeRateErrorConstants;

	public List<SyncResponse> sync(final ExchangeRateDocumentDTO exchangeRateDocumentDTO) {
		if (logger.isDebugEnabled()) {
			logger.debug("Received " + exchangeRateDocumentDTO + " Exchange Rate(s).");
		}

		List<SyncResponse> responses = new ArrayList<SyncResponse>();
		SyncResponse syncResponse = new SyncResponse();
		final Map<String,String> errorMessageCodes = new HashMap<String,String>();

		if (exchangeRateDocumentDTO.getExchangeRate().getDataArea() != null
				&& exchangeRateDocumentDTO.getExchangeRate().getApplicationArea()!= null) {
			setApplicationArea(syncResponse, exchangeRateDocumentDTO.getExchangeRate());
		}
	
			
			validateBasicData(exchangeRateDocumentDTO, errorMessageCodes);
			if(!errorMessageCodes.isEmpty()){
				syncResponse = buildErrorResponse(syncResponse,null,errorMessageCodes);
			}
			try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(
						TransactionStatus status) {
				
						syncExchangeRate(exchangeRateDocumentDTO.getExchangeRate(),errorMessageCodes);
					
				}
			});
			if(!errorMessageCodes.isEmpty()){
				 syncResponse = buildErrorResponse(syncResponse, null,errorMessageCodes);
           }else{
        	   syncResponse.setSuccessful(true);
           }
		} catch(IllegalArgumentException  ex){
			String error = ex.getMessage();
			if(error != null){
				errorMessageCodes.put(exchangeRateErrorConstants.EX008, "Xml Validation Error:"+error);
			}
			syncResponse = buildErrorResponse(syncResponse, null,errorMessageCodes);
		}catch (RuntimeException e) {
			logger.error("Exception Occurred in exchangerates!!", e);
			
			if(!errorMessageCodes.isEmpty()){
     		   syncResponse.setErrorMessages(errorMessageCodes);
     	   }else{
     		   if(e.getMessage()!=null){
     			   String errorMessage = e.getMessage();
     			   if(errorMessage!=null){
     				   String[] message = errorMessage.split(":");
     				   if(message.length>1){
     					  String messageKey=exchangeRateErrorConstants.getErrorMessage(message[0]);
     					  if(messageKey!=null){
     					   errorMessageCodes.put(message[0],message[1]);
     					  }
     				   }
     			   }
     		   }
     		   syncResponse.setErrorMessages(errorMessageCodes);
     	   }
     	   syncResponse = buildErrorResponse(syncResponse,null,errorMessageCodes);
     	   syncResponse.setErrorMessages(errorMessageCodes);
        } finally {
			responses.add(syncResponse);
		}

		return responses;
	}

	private void syncExchangeRate(final ExchangeRate exchangeRateDocumentDTO,final Map<String,String> errorMessageCodes)
			 {	
		

		CalendarDuration calendarDuration = new CalendarDuration(CalendarUtil
              	.convertToCalendarDate(exchangeRateDocumentDTO.getDataArea().getDateRange().getStartDate()), CalendarUtil
				.convertToCalendarDate(exchangeRateDocumentDTO.getDataArea().getDateRange().getEndDate()));

		CurrencyDetails[] currencyDetails = exchangeRateDocumentDTO.getDataArea().getCurrencyDetailsArray();
		Currency fromCurrency =null;
		try{
			fromCurrency	=  Currency.getInstance(exchangeRateDocumentDTO.getDataArea().getConCur());		
		}catch(IllegalArgumentException iae){
			iae.printStackTrace();
			errorMessageCodes.put(ExchangeRateErrorConstants.EX003, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX003));

		}
		

		for (CurrencyDetails currencyDetail : currencyDetails) {
			CurrencyConversion[] conversionTypeDTOs = currencyDetail.getCurrencyConversionArray();
			
					for (CurrencyConversion conversiondto : conversionTypeDTOs) {
						
						Currency toCurrency = null;
						// Validate the currency.
						/*try{
						}catch(IllegalArgumentException iae){
							throw new RuntimeException("The from currency is not a valid currency code");
						}*/
						
						try{
							toCurrency = Currency.getInstance(conversiondto.getConCode().trim());
						}catch(IllegalArgumentException iae){
							iae.printStackTrace();
							errorMessageCodes.put(ExchangeRateErrorConstants.EX004, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX004));
						}
						
						// Check if the currency conversion already exists in the system.
						CurrencyExchangeRate currencyExchangeRate = currencyExchangeRateService
								.findCurrencyExchangeRateForSync(fromCurrency, toCurrency);
						
						// If not, create one.
						if(currencyExchangeRate == null){
							currencyExchangeRate = createCurrencyExchangeRate(fromCurrency, toCurrency);
							// If this was never supported there would be no existing factor. So create.
							createCurrencyConversionFactor(calendarDuration, conversiondto.getRate(), currencyExchangeRate);
						}else{
							// Check if there is an overlap
							CurrencyConversionFactor overlapFactor = currencyExchangeRateService
									.findConversionFactorForSync(fromCurrency, toCurrency, calendarDuration.getFromDate());
							if(overlapFactor != null){
								errorMessageCodes.put(ExchangeRateErrorConstants.EX009, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX009));
								return;
							}
							
							// Check if there is discontinuity
							CurrencyConversionFactor factorContinuity = currencyExchangeRateService
									.findConversionFactorForSync(fromCurrency, toCurrency, calendarDuration.getFromDate().plusDays(-1));
							if(factorContinuity == null){
								errorMessageCodes.put(ExchangeRateErrorConstants.EX0010, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX0010));
								return;
							}
							
							// If not, create a new one.
							createCurrencyConversionFactor(calendarDuration, conversiondto.getRate(), currencyExchangeRate);
						}
					}				
		}
	}
	
	
	public void setApplicationArea(SyncResponse response,
			ExchangeRate exchangeRate ) {
		ApplicationArea applicationArea =   exchangeRate.getApplicationArea();
		Sender sender = applicationArea.getSender();
		response.setUniqueIdName(IntegrationConstants.TECHNICIAN_SYNC_UNIQUE_ID);
		response.setCreationDateTime(Calendar.getInstance());
		if (applicationArea.getBODId() != null) {
			response.setBodId(exchangeRate.getApplicationArea().getBODId());
		}
		if (applicationArea.getInterfaceNumber() != null) {
			response.setInterfaceNumber(applicationArea.getInterfaceNumber());
		}
		if (sender != null) {
			response.setLogicalId(applicationArea.getSender().getLogicalId());
			response.setTask(applicationArea.getSender().getTask());
			response.setReferenceId(applicationArea.getSender()
					.getReferenceId());
			response.setUniqueIdValue(applicationArea.getSender()
					.getReferenceId());
		}
	}
	private CurrencyExchangeRate createCurrencyExchangeRate(Currency fromCurrency, Currency toCurrency)  {
		CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
		currencyExchangeRate.setFromCurrency(fromCurrency);
		currencyExchangeRate.setToCurrency(toCurrency);
		currencyExchangeRateService.save(currencyExchangeRate);
		return currencyExchangeRate;
	}

	private void createCurrencyConversionFactor(
			CalendarDuration calendarDuration, BigDecimal factor, CurrencyExchangeRate currencyExchangeRate) {
		CurrencyConversionFactor conversionFactor = new CurrencyConversionFactor();
		try{
			conversionFactor.setDuration(calendarDuration);
			conversionFactor.setParent(currencyExchangeRate);
			conversionFactor.setValue(factor);
			currencyConversionFactorService.save(conversionFactor);
		}catch (Exception e) {
			logger.error(e, e);
			throw new RuntimeException(e);
		} 
	}

	private void validateBasicData(ExchangeRateDocumentDTO exchangeRateDocumentDTO, final Map<String,String> errorMessageCodes) {
			if(exchangeRateDocumentDTO.getExchangeRate().getDataArea().getDateRange().getStartDate() == null){
				errorMessageCodes.put(ExchangeRateErrorConstants.EX001, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX001));

		}
		if(exchangeRateDocumentDTO.getExchangeRate().getDataArea().getDateRange().getStartDate() == null){
			errorMessageCodes.put(ExchangeRateErrorConstants.EX002, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX002));
		}
		if(exchangeRateDocumentDTO.getExchangeRate().getDataArea().getConCur() == null){
			errorMessageCodes.put(ExchangeRateErrorConstants.EX003, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX003));
		}
		
		CurrencyDetails[] currencyDetails = exchangeRateDocumentDTO.getExchangeRate().getDataArea().getCurrencyDetailsArray();
		for (CurrencyDetails currencyDetail : currencyDetails) {
			CurrencyConversion[] currencyConversions = currencyDetail.getCurrencyConversionArray();
			for (CurrencyConversion currencyConversion : currencyConversions) {
				if (!(StringUtils.hasText(currencyConversion.getConCode().trim()))) {
					errorMessageCodes.put(ExchangeRateErrorConstants.EX004, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX004));
				}
				
				if (currencyConversion.getRate()==null) {
					errorMessageCodes.put(ExchangeRateErrorConstants.EX005, exchangeRateErrorConstants.getPropertyMessageFromErrorCode(ExchangeRateErrorConstants.EX005));
				}				
			}
		}
		
	}

	private SyncResponse buildErrorResponse(SyncResponse response,
			String message,Map<String,String> errorMessageCodes) {
		response.setSuccessful(false);
		response.setException(new StringBuilder().append(
				" Error syncing Exchange Rate Date Range ")
				.append("\n").append(" The Reason for the Error is : ").append(
						message).append("\n").append("\n").toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorMessages(errorMessageCodes);

		return response;
	}

	private void appendErrorMessage(StringBuilder errorMessage,
			String appendMessage) {
		if (StringUtils.hasText(errorMessage.toString())) {
			errorMessage.append(", ");
		}
		errorMessage.append(appendMessage);
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void setCurrencyExchangeRateService(
			CurrencyExchangeRateService currencyExchangeRateService) {
		this.currencyExchangeRateService = currencyExchangeRateService;
	}

	public void setCurrencyConversionFactorService(
			CurrencyConversionFactorService currencyConversionFactorService) {
		this.currencyConversionFactorService = currencyConversionFactorService;
	}

	public ExchangeRateErrorConstants getExchangeRateErrorConstants() {
		return exchangeRateErrorConstants;
	}

	public void setExchangeRateErrorConstants(
			ExchangeRateErrorConstants exchangeRateErrorConstants) {
		this.exchangeRateErrorConstants = exchangeRateErrorConstants;
	}

}
