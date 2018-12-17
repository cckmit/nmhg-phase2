package tavant.twms.integration.layer.component.global.dealerinterfaces.unitservicehistory;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitservicehistory.UnitServiceHistoryTransformer;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryrequest.UnitServiceHistoryRequestDocumentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.EachErrorCodeDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.ErrorCodesTypeDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.UnitServiceHistoryResponseDocumentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.UnitServiceHistoryResponseDocumentDTO.UnitServiceHistoryResponse;

/**
 * The class handles the requests for the Unit Service History web service.
 * @author TWMSUSER
 */
public class UnitServiceHistoryHandler {

	private static final Logger logger = Logger.getLogger("dealerAPILogger");

    private InventoryService inventoryService;

    private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

    private UnitServiceHistoryTransformer unitServiceTransformer;
    
    private ApplicationSettingsHolder applicationSettings;

    /**
     * The method validates the request and returns the response.
     * @param unitServiceHistoryRequest
     * @return UnitServiceHistoryResponseDocumentDTO
     */
    public UnitServiceHistoryResponseDocumentDTO getUnitServiceHistory(
            UnitServiceHistoryRequestDocumentDTO unitServiceHistoryRequest) {
        String serialNumber = unitServiceHistoryRequest.getUnitServiceHistoryRequest()
                .getSerialNumber();
        String itemNumber = unitServiceHistoryRequest.getUnitServiceHistoryRequest()
                .getItemNumber();
        UnitServiceHistoryResponseDocumentDTO unitServiceHistoryResponseDocDTO = UnitServiceHistoryResponseDocumentDTO.Factory
                .newInstance();
        
        
        InventoryItem inventoryItem = null;
        try {
            inventoryItem = this.inventoryService.findItemBySerialNumberAndItemNumber(serialNumber,
                    itemNumber);            
            dealerInterfaceErrorConstants.getI18nDomainTextReader().setLoggedInUserLocale(inventoryItem.getBusinessUnitInfo().getName());
            boolean canViewInventory = true;
            /**
             * If inventoryItem in retail any user can view the detail
             * If inventoryItem in stock only owing dealer/internal users/configured service
             * providers
             */
            if(inventoryItem.isInStock()){
                if(!unitServiceTransformer.canViewStockInventoryItem(inventoryItem)){
                    setErrorResponseDTO(unitServiceHistoryResponseDocDTO, null,
                            DealerInterfaceErrorConstants.USH02);
                    canViewInventory = false;
                }                
            }
            if (canViewInventory) {
                unitServiceHistoryResponseDocDTO = unitServiceTransformer
                        .convertBeanToResponseDTO(inventoryItem);
                unitServiceHistoryResponseDocDTO.getUnitServiceHistoryResponse().setStatus(
                        dealerInterfaceErrorConstants
                                .getPropertyMessage(DealerInterfaceErrorConstants.SUCESS));
                unitServiceHistoryResponseDocDTO.getUnitServiceHistoryResponse().setTWMSURL(getApplicationSettings().getExternalUrl());
            }
       } catch (ItemNotFoundException e) {
            setErrorResponseDTO(unitServiceHistoryResponseDocDTO, null,
                    DealerInterfaceErrorConstants.USH01);
        } catch (Exception e) {
            logger.error("inside getUnitServiceHistory :" + e);
            setErrorResponseDTO(unitServiceHistoryResponseDocDTO, null,
                    DealerInterfaceErrorConstants.DAPI02);
        }
        return unitServiceHistoryResponseDocDTO;
    }
    
    public void setErrorResponseDTO(
            UnitServiceHistoryResponseDocumentDTO unitServiceHistoryResponseDocDTO, Map<String, String[]> errorCodesMap, String errorCode) {
        UnitServiceHistoryResponse unitServiceResponseDTO = UnitServiceHistoryResponse.Factory
                .newInstance();
        
        ErrorCodesTypeDTO errorCodesTypeDTO = ErrorCodesTypeDTO.Factory.newInstance();
        EachErrorCodeDTO[] eachErrorCodeDTO = null;
        if (errorCodesMap != null) {
        	eachErrorCodeDTO = new EachErrorCodeDTO[2];            
            eachErrorCodeDTO[0] = EachErrorCodeDTO.Factory.newInstance();
            eachErrorCodeDTO[0].setErrorCode(errorCode);
            eachErrorCodeDTO[0].setErrorMessage(dealerInterfaceErrorConstants
                    .getErrorMessage(errorCode));
            
            eachErrorCodeDTO[1] = EachErrorCodeDTO.Factory.newInstance();
            eachErrorCodeDTO[1].setErrorCode(errorCode);
            eachErrorCodeDTO[1].setErrorMessage(errorCodesMap.keySet().iterator().next());
                      
        } else {
        	eachErrorCodeDTO =  new EachErrorCodeDTO[1];        	        
	   	    eachErrorCodeDTO[0] = EachErrorCodeDTO.Factory.newInstance();
	   	    eachErrorCodeDTO[0].setErrorCode(errorCode);
	   	    eachErrorCodeDTO[0].setErrorMessage(dealerInterfaceErrorConstants
	   	                .getErrorMessage(errorCode));
        }
        errorCodesTypeDTO.setEachErrorCodeArray(eachErrorCodeDTO);
        unitServiceResponseDTO.setErrorCodes(errorCodesTypeDTO);
        unitServiceResponseDTO.setStatus(dealerInterfaceErrorConstants
                .getPropertyMessage(DealerInterfaceErrorConstants.FAILURE));
        unitServiceResponseDTO.setTWMSURL(getApplicationSettings().getExternalUrl());
        unitServiceHistoryResponseDocDTO.setUnitServiceHistoryResponse(unitServiceResponseDTO);

    }    

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setUnitServiceHistoryTransformer(
            UnitServiceHistoryTransformer unitServiceTransformer) {
        this.unitServiceTransformer = unitServiceTransformer;
    }

    public void setDealerInterfaceErrorConstants(
            DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
        this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
    }

    public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	public void setApplicationSettings(
			ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}
    
    

}
