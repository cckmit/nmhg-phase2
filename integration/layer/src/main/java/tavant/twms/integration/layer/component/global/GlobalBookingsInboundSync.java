package tavant.twms.integration.layer.component.global;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.component.global.InstallBase.BookingsValidator;
import tavant.twms.integration.layer.constants.BookingsInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.security.SecurityHelper;

import com.tavant.globalsync.bookingsync.BookingsDocument;
import com.tavant.globalsync.bookingsync.BookingsDocument.Bookings.ApplicationArea;
import com.tavant.globalsync.bookingsync.BookingsDocument.Bookings.ApplicationArea.Sender;

public class GlobalBookingsInboundSync {

	private static final Logger logger = Logger
			.getLogger(GlobalBookingsInboundSync.class.getName());

	private InventoryService inventoryService;

	private TransactionTemplate transactionTemplate;

	private BookingsValidator bookingsValidator;

	private BookingsInterfaceErrorConstants bookingsInterfaceErrorConstants;

	private SecurityHelper securityHelper;
	
	private InventoryTransactionService invTransactionService;


	public List<SyncResponse> syncUnitBooking(
			final BookingsDocument bookingsDocument ) {
		List<SyncResponse> responses = new ArrayList<SyncResponse>();
		final Map<String,String> errorMessageCodes = new HashMap<String,String>();
		SyncResponse response = new SyncResponse();
		if (bookingsDocument.getBookings().getDataArea() != null
				&& bookingsDocument.getBookings().getApplicationArea()!= null) {
			setApplicationArea(response, bookingsDocument);
		}
		String itemNumber=null;
		bookingsValidator.validateCommonFields(bookingsDocument.getBookings(),
				errorMessageCodes);
		if(!errorMessageCodes.isEmpty()){
	         response = buildErrorResponse(response,null,response.getBusinessId(),
	     			  itemNumber,errorMessageCodes);
	         responses.add(response);
	 		return responses;
		}
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(
						TransactionStatus transactionStatus) {
					syncInventoryBooking( bookingsDocument,errorMessageCodes);
				}
			});
			if(!errorMessageCodes.isEmpty()){
				 response = buildErrorResponse(response, null,response.getBusinessId(),
		     			  itemNumber,errorMessageCodes);
            }else{
         	   response.setSuccessful(true);
            }
		} catch(IllegalArgumentException  ex){
			String error = ex.getMessage();
			if(error != null){
				errorMessageCodes.put(bookingsInterfaceErrorConstants.B006, "Xml Validation Error:"+error);
			}
			response = buildErrorResponse(response, ex.getMessage(),response.getBusinessId(),
	     			  itemNumber,errorMessageCodes);
		}catch (RuntimeException e) {
			logger.error("Exception Occurred in GlobalBookingsSync!!", e);
			
			if(!errorMessageCodes.isEmpty()){
     		   response.setErrorMessages(errorMessageCodes);
     	   }else{
     		   if(e.getMessage()!=null){
     			   String errorMessage = e.getMessage();
     			   if(errorMessage!=null){
     				   String[] message = errorMessage.split(":");
     				   if(message.length>1){
     					  String messageKey=bookingsInterfaceErrorConstants.getErrorMessage(message[0]);
     					  if(messageKey!=null){
     					   errorMessageCodes.put(message[0],message[1]);
     					  }
     				   }
     			   }
     		   }
     		   response.setErrorMessages(errorMessageCodes);
     	   }
     	   response = buildErrorResponse(response, e.getMessage(),response.getBusinessId(),
     			  itemNumber,errorMessageCodes);
     	   response.setErrorMessages(errorMessageCodes);
        }
		responses.add(response);
		return responses;
	}

	public void setApplicationArea(SyncResponse response,
			BookingsDocument bookingsDocument) {
		ApplicationArea applicationArea =   bookingsDocument.getBookings().getApplicationArea();
		Sender sender = applicationArea.getSender();
		response.setUniqueIdName(IntegrationConstants.TECHNICIAN_SYNC_UNIQUE_ID);
		response.setCreationDateTime(Calendar.getInstance());
		if (applicationArea.getBODId() != null) {
			response.setBodId(bookingsDocument.getBookings().getApplicationArea()
					.getBODId());
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
	private SyncResponse buildErrorResponse(SyncResponse response,
			String message, String serialNumber, String itemNumber,Map<String,String> errorMessageCodes) {
		response.setSuccessful(false);
		response.setException(new StringBuilder()
				.append(" Error Syncing Bookings, with Serial Number: ")
				.append(" The Reason for the Error is : ").append(message)
				.append("\n").append("\n").toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		if (errorMessageCodes.isEmpty()) {
			errorMessageCodes
					.put(BookingsInterfaceErrorConstants.B007,
							bookingsInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B007));
		}
		response.setErrorMessages(errorMessageCodes);
		return response;
	}

	@SuppressWarnings("unused")
	private void syncInventoryBooking(BookingsDocument bookingsDocument,final Map<String,String> errorMessageCodes) {
		InventoryItem inventoryItem=null;
		Item item = null;
				String serialNumber = String.valueOf((bookingsDocument.getBookings().getDataArea().getUnitSerialNumber().trim()));
		try {
			inventoryItem = inventoryService.findInventoryItemBySerialNumber(serialNumber);
			if(inventoryItem.getType().getType().equalsIgnoreCase(IntegrationConstants.RETAIL)){
				errorMessageCodes.put(BookingsInterfaceErrorConstants.B005, bookingsInterfaceErrorConstants.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B005));
				return;
			}
		} catch (ItemNotFoundException e) {
			errorMessageCodes.put(BookingsInterfaceErrorConstants.B008, bookingsInterfaceErrorConstants.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B008));
		   return;
			}		
			
			if(bookingsDocument.getBookings().getDataArea().getTransactionType().equalsIgnoreCase("B")){
				inventoryItem.setPreOrderBooking(true);
			}else {	
				inventoryItem.setPreOrderBooking(false);
			}
			inventoryService.updateInventoryItem(inventoryItem);
			
	}


	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public BookingsValidator getBookingsValidator() {
		return bookingsValidator;
	}

	public void setBookingsValidator(BookingsValidator bookingsValidator) {
		this.bookingsValidator = bookingsValidator;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public static Logger getLogger() {
		return logger;
	}


	public BookingsInterfaceErrorConstants getBookingsInterfaceErrorConstants() {
		return bookingsInterfaceErrorConstants;
	}

	public void setBookingsInterfaceErrorConstants(
			BookingsInterfaceErrorConstants bookingsInterfaceErrorConstants) {
		this.bookingsInterfaceErrorConstants = bookingsInterfaceErrorConstants;
	}



}
