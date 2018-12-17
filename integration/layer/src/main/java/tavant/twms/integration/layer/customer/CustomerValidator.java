/*
 *   Copyright (c)2007 Tavant Technologies
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
 *
 */package tavant.twms.integration.layer.customer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.globalsync.customersync.AddressTypeDTO;
import tavant.globalsync.customersync.CustomerTypeDTO;
import tavant.globalsync.customersync.ShipToLocationTypeDTO;
import tavant.globalsync.customersync.ShipToLocationsTypeDTO;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.integration.layer.Validator;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UploadSyncInterfaceErrorConstants;

/**
 *
 * @author prasad.r
 */
public class CustomerValidator implements Validator {

	private UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants;

    private BusinessUnitService businessUnitService;
    
    private CountryStateService countryStateService;

    private OrgService orgService;

	public void validate(Object o,Object dtoObject, final Map<String,String> errorMessageCodes) {
        CustomerTypeDTO customerTypeDTO=(CustomerTypeDTO)dtoObject;
        if (o instanceof Organization) {
            Organization organization = (Organization) o;
            validateOrganization(organization,customerTypeDTO,errorMessageCodes);
            if (o instanceof ServiceProvider) {
                ServiceProvider serviceProvider = (ServiceProvider) o;
                validateServiceProvider(serviceProvider,errorMessageCodes);
                if (serviceProvider instanceof Dealership) {
                    validateDealerShip((Dealership) serviceProvider,customerTypeDTO,errorMessageCodes);
                    validateAddress(customerTypeDTO ,errorMessageCodes);
                    validateShipToAddress(customerTypeDTO.getShipToLocations(),customerTypeDTO,errorMessageCodes);
                }
            } else if (o instanceof Supplier) {
                validateSupplier((Supplier) o,errorMessageCodes, customerTypeDTO);
                validatePrimaryShipToAddress(customerTypeDTO,errorMessageCodes);
            }
        }
        if(!errorMessageCodes.isEmpty()){
             throw new RuntimeException(StringUtils.collectionToCommaDelimitedString(errorMessageCodes.values()));
        }
    }

    private void validateAddress(CustomerTypeDTO customerTypeDTO, final Map<String,String> errorMessageCodes) {
        if (customerTypeDTO.getShipToChanged().toString().equals("Y")) {
            return;
        }
        if (!StringUtils.hasText(customerTypeDTO.getBillToAddress().getAddressline1())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0029,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0029));
        }

        if (!StringUtils.hasText(customerTypeDTO.getBillToAddress().getCity())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU007,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU007));

        }
        
        /*if(!StringUtils.hasText(customerTypeDTO.getBillToAddress().getState())){
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0050,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0050));
        }*/
        
        if (!StringUtils.hasText(customerTypeDTO.getBillToAddress().getCountry())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU008,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU008));

        }
        
        if(StringUtils.hasText(customerTypeDTO.getBillToAddress().getStatus()))
        	if(!StringUtils.hasText(customerTypeDTO.getBillToAddress().getStatus().toString()) ||!(customerTypeDTO.getBillToAddress().getStatus().equals("I")||customerTypeDTO.getBillToAddress().getStatus().equals("A")))
    		errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0059,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0059));
    }
    
    private void validatePrimaryShipToAddress(CustomerTypeDTO customerTypeDTO, final Map<String,String> errorMessageCodes){
    	if(customerTypeDTO.getPrimaryShipToAddress()!=null){
	    	if (!StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getAddressline1())) {
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0030,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0030));
	        }
	        if (!StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getSiteNumber())) {
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0031,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0031));
	        }
	        if (!StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getCity())) {
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU007,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU007));
	        }
	
	       /* if(!StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getState())){
	            errorMessageCodes.put("",uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0050));
	        }*/
	        if (!StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getCountry())) {
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU008,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU008));
	        }
	        if(!StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getZipcode()))
	    		errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0062,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0062));
	        
	        if(StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getStatus())){
	        	if(!StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getStatus().toString()) || !(customerTypeDTO.getPrimaryShipToAddress().getStatus().equals("I")||customerTypeDTO.getPrimaryShipToAddress().getStatus().equals("A")))
	        		errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0059,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0059));
	        }
    	}
    }

    private void validateOrganization(Organization organization, CustomerTypeDTO customerTypeDTO, final Map<String,String> errorMessageCodes) {
        if (!StringUtils.hasText(organization.getName())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU004,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU004));
        }

        if (organization.getPreferredCurrency()!=null && organization.getPreferredCurrency().getCurrencyCode()!=null 
        		&& !StringUtils.hasText(organization.getPreferredCurrency().getCurrencyCode())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU018,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU018));
        }
        
        if(customerTypeDTO.getBusinessUnits()==null || customerTypeDTO.getBusinessUnits().getBUNameArray()==null ||
                customerTypeDTO.getBusinessUnits().getBUNameArray().length==0){
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU001,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU001));
        }else{
            String[] businessUnitsStrings = customerTypeDTO.getBusinessUnits().getBUNameArray();
            List<String> businessUnitList= Arrays.asList(businessUnitsStrings);
            for(String businessUnit:businessUnitList){
            	if(!StringUtils.hasText(businessUnit)){
            		errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU001,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU001));
            	}else{
	        		if (businessUnit.equalsIgnoreCase(IntegrationConstants.US)) {
	        			businessUnit = IntegrationConstants.NMHG_US;
	        		} else if (businessUnit.equalsIgnoreCase(IntegrationConstants.EMEA)) {
	        			businessUnit = IntegrationConstants.NMHG_EMEA;
	        		}
	                BusinessUnit bu=businessUnitService.findBusinessUnit(businessUnit.trim());
	                if(bu==null){
	                    errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0051,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0051)+businessUnit);
	                }
            	}
            }
        }

    }
    private void validateServiceProvider(ServiceProvider serviceProvider, final Map<String,String> errorMessageCodes) {
        if (!StringUtils.hasText(serviceProvider.getServiceProviderNumber())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU003,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU003));
        }
    }

    private void validateDealerShip(Dealership dealership, CustomerTypeDTO customerTypeDTO, final Map<String,String> errorMessageCodes) {
        if (!StringUtils.hasText(dealership.getDealerNumber())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU003,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU003));
        }
        if (dealership.getPreferredCurrency()==null ||dealership.getPreferredCurrency().getCurrencyCode()==null) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU018,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU018));
        }
        if(!StringUtils.hasText(dealership.getMarketingGroup())){
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0036,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0036));
        }

        if(customerTypeDTO.getStatus()==null || !StringUtils.hasText(customerTypeDTO.getStatus().toString()) || !(customerTypeDTO.getStatus().toString().equals("I")||customerTypeDTO.getStatus().toString().equals("A")))
    		errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0058,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0058));
        
        if(!StringUtils.hasText(dealership.getBrand())){
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0037,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0037));
        }
        
        if(customerTypeDTO.getShipToChanged()!=null){
        	if(customerTypeDTO.getShipToChanged().equals("Y")||customerTypeDTO.getShipToChanged().equals("N")){
        		errorMessageCodes.put("","ShipToChanged value Should be 'Y' or'N'");
        	}
        }else{
        	errorMessageCodes.put("","ShipToChanged cannot be null");
        }

        if(StringUtils.hasText(customerTypeDTO.getDualBrandDealer())){
            ServiceProvider serviceProvider=orgService.findServiceProviderByNumberWithOutBU(customerTypeDTO.getDualBrandDealer());
            if(serviceProvider==null){
                errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0042,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0042));
            }
        }

        if(StringUtils.hasText(customerTypeDTO.getDealerFamilyCode()) && StringUtils.hasText(customerTypeDTO.getCustomerNumber())){
            if(!customerTypeDTO.getDealerFamilyCode().equalsIgnoreCase(customerTypeDTO.getCustomerNumber())){
                ServiceProvider serviceProvider=orgService.findServiceProviderByNumberWithOutBU(customerTypeDTO.getDealerFamilyCode());
                if(serviceProvider==null){
                    errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0043,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0043));
                }
            }
        }
        
        if(customerTypeDTO.getShipToChanged()==null || !(customerTypeDTO.getShipToChanged().toString().equals("Y")||customerTypeDTO.getShipToChanged().toString().equals("N")))
    		errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0060,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0060));
        else if(customerTypeDTO.getShipToChanged().toString().equals("N")){
        	
	    	if (!StringUtils.hasText(customerTypeDTO.getBillToAddress().getSiteNumber())) {
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0031,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0031));
	        }
        	 
	        if(!StringUtils.hasText(dealership.getSellingLocation())){
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0039,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0039));
	        }
	        
	        if(!StringUtils.hasText(dealership.getServiceProviderType())){
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0040,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0040));
	        }
	        
	        if(!StringUtils.hasText(dealership.getServiceProviderDescription())){
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0041,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0041));
	        }
	        
	        if(!StringUtils.hasText(dealership.getNetwork())){
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0045,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0045));
	        }
	        
	        if(!StringUtils.hasText(dealership.getLanguage())){
	            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0046,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0046));
	        }
        }

    }

    private void validateSupplier(Supplier supplier, final Map<String,String> errorMessageCodes, CustomerTypeDTO customerTypeDTO) {
        if (!StringUtils.hasText(supplier.getSupplierNumber())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU003,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU003));
        }
        if (!StringUtils.hasText(supplier.getStatus())) {
            errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU019,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU019));
        }
    }

    private void validateShipToAddress(ShipToLocationsTypeDTO sihpToLocationsDTO,CustomerTypeDTO customerTypeDTO, final Map<String,String> errorMessageCodes) {
    	if(customerTypeDTO.getShipToChanged().toString().equals("Y")) {
    		if (sihpToLocationsDTO != null){
    			ShipToLocationTypeDTO[] shipToLocations = sihpToLocationsDTO.getShipToLocationArray();
    			if (shipToLocations.length > 0) {
    				for (ShipToLocationTypeDTO shipToLocation : shipToLocations) {
    					AddressTypeDTO addressDTO = shipToLocation.getAddress();
    					if (!StringUtils.hasText(addressDTO.getAddressline1())) {
    						errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0030,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0030));
    					}
    					if (!StringUtils.hasText(addressDTO.getSiteNumber())) {
    						errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0031,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0031));
    					}
    					if (!StringUtils.hasText(addressDTO.getCity())) {
    						errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU007,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU007));
    					}

    					/*if(!StringUtils.hasText(addressDTO.getState())){
    						errorMessageCodes.put("",uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0050));
    					}*/
    					if (!StringUtils.hasText(addressDTO.getCountry())) {
    						errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU008,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU008));
    					}
    					if(!StringUtils.hasText(addressDTO.getStatus()) || !StringUtils.hasText(addressDTO.getStatus().toString()) || !(addressDTO.getStatus().equals("I")||addressDTO.getStatus().equals("A")))
    						errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0059,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0059));

    					if(addressDTO.getIsPrimary()!=null){
    						if(!(addressDTO.getIsPrimary().toString().equals("Y")||addressDTO.getIsPrimary().toString().equals("N"))){
    							errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0065,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0065));
    						}
    					}
    					
    					if(addressDTO.getIsEndCustomer()==null || !(addressDTO.getIsEndCustomer().toString().equals("Y")||addressDTO.getIsEndCustomer().toString().equals("N")))
    						errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0063,uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0063));
    				}
    			}
    		}
    	}
    }

    public UploadSyncInterfaceErrorConstants getUploadSyncInterfaceErrorConstants() {
		return uploadSyncInterfaceErrorConstants;
	}

	public void setUploadSyncInterfaceErrorConstants(UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants) {
		this.uploadSyncInterfaceErrorConstants = uploadSyncInterfaceErrorConstants;
	}

    public void setBusinessUnitService(BusinessUnitService businessUnitService) {
        this.businessUnitService = businessUnitService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
}