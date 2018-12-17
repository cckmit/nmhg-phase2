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

import net.sf.dozer.util.mapping.MapperIF;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.integration.layer.TransformException;
import tavant.twms.integration.layer.Transformer;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UploadSyncInterfaceErrorConstants;

/**
 *
 * @author prasad.r
 */
public abstract class CustomerTransformer implements Transformer {

    private MapperIF mapper;
    
    private UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants;

    public MapperIF getMapper() {
        return mapper;
    }

    public void setMapper(MapperIF mapper) {
        this.mapper = mapper;
    }

    public void mergeAddress(final Object addressDTO, Address address) {
        mapper.map(addressDTO, address);
    }

    public void mergeOrganization(final Object customer, Organization organization) {
        mapper.map(customer, organization);
    }

    public void mergeServiceProvider(final Object customer, ServiceProvider serviceProvider) {
        mapper.map(customer, serviceProvider);
    }

    public void mergeDealership(final Object customer, Dealership dealership) {
        mapper.map(customer, dealership);
    }

    public void mergeDirectCustomer(final Object customer, DirectCustomer directCustomer) {
        mapper.map(customer, directCustomer);
    }

    public void mergeOEM(final Object customer, OriginalEquipManufacturer oem) {
        mapper.map(customer, oem);
    }

    public void mergeInterCompany(final Object customer, InterCompany interCompany) {
        mapper.map(customer, interCompany);
    }

    public void mergeNationalAccount(final Object customer, NationalAccount nationalAccount) {
        mapper.map(customer, nationalAccount);
    }

    public void mergeSupplier(final Object customer, Supplier supplier) {
        mapper.map(customer, supplier);
    }

    protected Organization createNewCustomer(String companyType) throws TransformException {

        if (companyType.equalsIgnoreCase(IntegrationConstants.DIRECT_CUSTOMER)) {
            return new DirectCustomer();
        } else if (companyType.equalsIgnoreCase(IntegrationConstants.INTERCOMPANY)) {
            return new InterCompany();
        } else if (companyType.equalsIgnoreCase(IntegrationConstants.NATIONAL_ACCOUNT)) {
            return new NationalAccount();
        } else if (companyType.equalsIgnoreCase(IntegrationConstants.OEM)) {
            return new OriginalEquipManufacturer();
        } else if (companyType.equalsIgnoreCase(IntegrationConstants.DEALER) || "DEALERSHIP".equalsIgnoreCase(companyType)) {
            return new Dealership();
        } else if (companyType.equalsIgnoreCase(IntegrationConstants.VENDOR)) {
            return new Supplier();
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
