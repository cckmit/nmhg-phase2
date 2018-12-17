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
 */
package tavant.twms.integration.layer.customer.sync;

import java.util.Map;

import tavant.globalsync.customersync.CustomerTypeDTO;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.integration.layer.TransformException;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UploadSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.customer.CustomerTransformer;

/**
 * @author prasad.r
 */
public class CustomerSyncTransformer extends CustomerTransformer {

	private UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants;
    public Object transform(Object customer, final Map<String,String> errorMessageCodes) throws TransformException {
        CustomerTypeDTO customerTypeDTO = (CustomerTypeDTO) customer;
        String companyType = null;
        if (customerTypeDTO.getCompanyType() != null) {
            companyType = customerTypeDTO.getCompanyType().toString();
        }
        Object primaryShipToAddressDTO = null;
        if (companyType != null && companyType.equalsIgnoreCase(IntegrationConstants.VENDOR)) {
            primaryShipToAddressDTO = customerTypeDTO.getPrimaryShipToAddress();
        } else {
            primaryShipToAddressDTO = customerTypeDTO.getBillToAddress();
        }
        Address billToAddress = new Address();
        this.mergeAddress(primaryShipToAddressDTO, billToAddress);
        Organization organization = createNewCustomer(companyType);
        this.mergeOrganization(customer, organization);
        billToAddress.setBelongsTo(organization);
        if (companyType == null) {
        	errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0056, uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0056));
            throw new TransformException(uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0056));
        } else if (companyType.equalsIgnoreCase(IntegrationConstants.DEALER)) {
            ServiceProvider serviceProvider = (ServiceProvider) organization;
            this.mergeServiceProvider(customer, serviceProvider);
            Dealership dealership = (Dealership) serviceProvider;
            this.mergeDealership(customer, dealership);
            if (customerTypeDTO.getBrandInfo() != null && customerTypeDTO.getBrandInfo().getBrandCodeArray() != null && customerTypeDTO.getBrandInfo().getBrandCodeArray().length == 1) {
                dealership.setBrand(customerTypeDTO.getBrandInfo().getBrandCodeArray(0).toString());
            }
            if (customerTypeDTO.getMarketingGroupCode() != null) {
                dealership.setMarketingGroup(customerTypeDTO.getMarketingGroupCode());
            }
            dealership.setSiteNumber(customerTypeDTO.getBillToAddress().getSiteNumber());
            dealership.setServiceProviderDescription(customerTypeDTO.getServiceProviderDesc());
            return dealership;
        } else if (companyType.equalsIgnoreCase(IntegrationConstants.VENDOR)) {
        	Supplier supplier = (Supplier) organization;
        	supplier.setAddress(billToAddress);
            this.mergeSupplier(customer, supplier);
            return supplier;
        } else {
        	errorMessageCodes.put(UploadSyncInterfaceErrorConstants.CU0057, uploadSyncInterfaceErrorConstants.getPropertyMessage("CU0057",new String[]{companyType}));
            throw new TransformException(uploadSyncInterfaceErrorConstants.getPropertyMessage("CU0057",new String[]{companyType}));
        }
    }
    
	public UploadSyncInterfaceErrorConstants getUploadSyncInterfaceErrorConstants() {
		return uploadSyncInterfaceErrorConstants;
	}
	public void setUploadSyncInterfaceErrorConstants(
			UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants) {
		this.uploadSyncInterfaceErrorConstants = uploadSyncInterfaceErrorConstants;
	}

}