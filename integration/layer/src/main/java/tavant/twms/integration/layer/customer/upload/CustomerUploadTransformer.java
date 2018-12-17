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
package tavant.twms.integration.layer.customer.upload;

import java.util.Map;

import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.upload.CustomerStaging;
import tavant.twms.integration.layer.TransformException;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UploadSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.customer.CustomerTransformer;

/**
 *
 * @author prasad.r
 */
public class CustomerUploadTransformer extends CustomerTransformer{

	private UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants;
    public Object transform(Object customer, final Map<String,String> errorMessageCodes) throws TransformException {
        customer = (CustomerStaging) customer;
        CustomerStaging customerStaging = (CustomerStaging) customer;
        Object primaryShipToAddressDTO = customer;
		Address primaryShipToaddress = new Address();
        String companyType = customerStaging.getCustomerType();
		this.mergeAddress(primaryShipToAddressDTO, primaryShipToaddress);
		Organization organization = createNewCustomer(companyType);
		organization.setAddress(primaryShipToaddress);
		this.mergeOrganization(customer, organization);
		primaryShipToaddress.setBelongsTo(organization);
		if (companyType != null
				&& (companyType.equalsIgnoreCase(IntegrationConstants.DEALER) || (IntegrationConstants.DEALERSHIP).equalsIgnoreCase(companyType))) {
			ServiceProvider serviceProvider = (ServiceProvider)organization;
			this.mergeServiceProvider(customer, serviceProvider);
		} else if (companyType != null
				&& companyType.equalsIgnoreCase(IntegrationConstants.SUPPLIER)) {
			Supplier supplier = (Supplier)organization;
			this.mergeSupplier(customer, supplier);
			return supplier;
		} 
        throw new TransformException(uploadSyncInterfaceErrorConstants.getPropertyMessage("CU0057",new String[]{companyType}));
    }
	public UploadSyncInterfaceErrorConstants getUploadSyncInterfaceErrorConstants() {
		return uploadSyncInterfaceErrorConstants;
	}
	public void setUploadSyncInterfaceErrorConstants(
			UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants) {
		this.uploadSyncInterfaceErrorConstants = uploadSyncInterfaceErrorConstants;
	}

}
