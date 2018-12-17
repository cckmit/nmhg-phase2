package tavant.twms.integration.layer.component.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Code;

import tavant.globalsync.customersync.ApplicationAreaTypeDTO;
import tavant.globalsync.customersync.CustomerTypeDTO;
import tavant.globalsync.customersync.SenderTypeDTO;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.integration.layer.TransformException;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.constants.InstallBaseSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UploadSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.customer.CustomerSyncService;
import tavant.twms.integration.layer.customer.CustomerValidator;
import tavant.twms.integration.layer.customer.sync.CustomerSyncTransformer;

public class GlobalCustomerSync {
    private static Logger logger = Logger.getLogger(GlobalCustomerSync.class
            .getName());

        
    private UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants;
    private TransactionTemplate transactionTemplate;
    private CustomerSyncTransformer customerSyncTransformer;
    private CustomerValidator customerValidator;
    private CustomerSyncService customerSyncService;
    public static String dt;
  
   public List<SyncResponse> sync(final Collection<CustomerTypeDTO> customers,ApplicationAreaTypeDTO applicationArea) {
       if (logger.isDebugEnabled()) {
           logger.debug("Received " + customers.size()
                   + " customers for synchronising.");
       }
       List<SyncResponse> responses = new ArrayList<SyncResponse>();
       for (final CustomerTypeDTO customerDTO : customers) {
    	   final Map<String,String> errorMessageCodes = new HashMap<String,String>();
    	   SyncResponse response = new SyncResponse();
    	   try{
    		   customerDTO.setCustomerName(customerDTO.getCustomerName().trim());
    	   }
    	   catch(Exception e){
    		   errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU004, uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU004));
    	   }
           response.setBusinessId(customerDTO.getCustomerName());
           response.setUniqueIdName(IntegrationConstants.CUSTOMER_NAME);
           response.setUniqueIdValue(customerDTO.getCustomerName());
           if(customerDTO.getBusinessUnits()!=null && customerDTO.getBusinessUnits().getBUNameArray()!=null){
        	   Set<String> businessUnits =  new HashSet<String>(Arrays.asList(customerDTO.getBusinessUnits().getBUNameArray()));
        	   if(CollectionUtils.isNotEmpty(businessUnits)){
        	    response.setBusinessUnits(businessUnits);
        	   }
           }
           if(applicationArea !=null){
        	   SenderTypeDTO sender = applicationArea.getSender();
        	   if(sender!=null){
        		   response.setLogicalId(sender.getLogicalId());
        		   response.setTask(sender.getTask());
        		   response.setReferenceId(sender.getReferenceId());
        	   }
        	   response.setCreationDateTime(Calendar.getInstance());
        	   response.setBodId(applicationArea.getBODId());
        	   response.setInterfaceNumber(applicationArea.getInterfaceNumber());
           }
           try {
               this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                   protected void doInTransactionWithoutResult(
                           TransactionStatus transactionStatus) {
                	   if(errorMessageCodes.isEmpty()){
                       sync(customerDTO, errorMessageCodes);
                	   }
                   }
               });
               if(!errorMessageCodes.isEmpty()){
		           response.setErrorMessages(errorMessageCodes);
		           response.setSuccessful(false);
               }else{
            	   response.setErrorMessages(errorMessageCodes);
            	   response.setSuccessful(true);
               }
           }catch(IllegalArgumentException  ex){
				String error = ex.getMessage();
				if(error != null){
					errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0068,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0068));
				}
				response = buildErrorResponse(response, ex.getMessage(),
	        			   customerDTO.getCustomerName(),errorMessageCodes);
			} catch (RuntimeException e) {
        	   logger.error(e, e);
        	   if(!errorMessageCodes.isEmpty()){
        		   response.setErrorMessages(errorMessageCodes);
        	   }else{
        		   if(e.getMessage()!=null){
        			   String errorMessage = e.getMessage();
        			   if(errorMessage!=null){
        				   if(!errorMessage.contains("##")){
        					   if(uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(e.getMessage())!=null){
        						   errorMessageCodes.put(e.getMessage(),uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(e.getMessage()));
        			            }else{
        					   errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0068,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0068));
        			            }
        				   }
        				   else if(errorMessage.contains("##")){
	        				   String[] message = errorMessage.split("##");
	        				   if(message.length>0){
	        					   errorMessageCodes.put(message[0],message[1]);
	        				   }
        				   }else{
        					   errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0068,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0068));
        				   }
        			   }
        		   }
        	   }
        	   response = buildErrorResponse(response, e.getMessage(),
        			   customerDTO.getCustomerName(),errorMessageCodes);
        	   response.setErrorMessages(errorMessageCodes);
           }
           responses.add(response);
       }
       return responses;
   }

  
	
	public void sync(final CustomerTypeDTO customerDTO , final Map<String,String> errorMessageCodes) {
        try{
        	Organization organization = null;
        	if(customerDTO.getCompanyType()!=null){
	        	if(customerDTO.getCompanyType().toString().equals("DEALER"))
	        		organization = (ServiceProvider) customerSyncTransformer.transform(customerDTO, errorMessageCodes);
	        	else
	        		organization = (Supplier) customerSyncTransformer.transform(customerDTO, errorMessageCodes);
	            customerValidator.validate(organization,customerDTO, errorMessageCodes);
	            customerSyncService.createOrUpdate(organization, customerDTO);
        	}else{
        		 throw new TransformException(uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0056));
        	}
        }catch(IllegalArgumentException  ex){
        	throw new IllegalArgumentException(ex.getMessage(), ex);
		}catch(Exception e){
            logger.error("Error while syncing customer !!!", e);
            throw new RuntimeException(e.getMessage(), e);
        }

	}

	private SyncResponse buildErrorResponse(SyncResponse response,
			String message, String customerName,Map<String,String> errorMessageCodes) {
		response.setSuccessful(false);
		response.setException(new StringBuilder()
				.append(uploadSyncInterfaceErrorConstants
						.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0053))
				.append(customerName)
				.append("\n")
				.append(message).append("\n").append("\n").toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		if (errorMessageCodes.isEmpty()) {
			errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0068,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0068));
		}
		response.setErrorMessages(errorMessageCodes);
		return response;
	}
	
	public UploadSyncInterfaceErrorConstants getUploadSyncInterfaceErrorConstants() {
		return uploadSyncInterfaceErrorConstants;
	}

	public void setUploadSyncInterfaceErrorConstants(
			UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants) {
		this.uploadSyncInterfaceErrorConstants = uploadSyncInterfaceErrorConstants;
	}

	public CustomerSyncService getCustomerSyncService() {
		return customerSyncService;
	}

	public void setCustomerSyncService(CustomerSyncService customerSyncService) {
		this.customerSyncService = customerSyncService;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

   public CustomerSyncTransformer getCustomerSyncTransformer() {
		return customerSyncTransformer;
	}

	public void setCustomerSyncTransformer(CustomerSyncTransformer customerSyncTransformer) {
		this.customerSyncTransformer = customerSyncTransformer;
	}

	public CustomerValidator getCustomerValidator() {
		return customerValidator;
	}

	public void setCustomerValidator(CustomerValidator customerValidator) {
		this.customerValidator = customerValidator;
	}
	
}
