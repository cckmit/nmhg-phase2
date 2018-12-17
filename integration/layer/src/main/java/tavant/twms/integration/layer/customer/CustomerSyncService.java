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
package tavant.twms.integration.layer.customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.globalsync.customersync.AddressTypeDTO;
import tavant.globalsync.customersync.BrandInfoTypeDTO;
import tavant.globalsync.customersync.CustomerTypeDTO;
import tavant.globalsync.customersync.ShipToLocationTypeDTO;
import tavant.globalsync.customersync.ShipToLocationsTypeDTO;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBook;
import tavant.twms.domain.orgmodel.AddressBookAddressMapping;
import tavant.twms.domain.orgmodel.AddressBookService;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.AddressType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DirectCustomer;
import tavant.twms.domain.orgmodel.InterCompany;
import tavant.twms.domain.orgmodel.NationalAccount;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.OriginalEquipManufacturer;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.integration.layer.Service;
import tavant.twms.integration.layer.ServiceException;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UploadSyncInterfaceErrorConstants;

/**
 * @author prasad.r
 */
public class CustomerSyncService implements Service {

    private Logger logger = Logger.getLogger(CustomerSyncService.class);
    private SupplierService supplierService;
    private OrgService orgService;
    private ClaimService claimService;
    private InventoryService inventoryService;
    private CustomerService customerService;
    private AddressBookService addressBookService;
    private BusinessUnitService businessUnitService;
    private String emeaMasterSupplier;
    private String amerMasterSupplier;
    private UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants;

    public void createOrUpdate(Object entity, Object input) throws ServiceException {
        if (!(entity instanceof Organization)) {
            throw new ServiceException("Entity is not of type Organization !!!");
        }
        if (!(input instanceof CustomerTypeDTO)) {
            throw new ServiceException("Entity not a type of CustomerDTO");
        }
        CustomerTypeDTO customerTypeDTO = (CustomerTypeDTO) input;
        if (entity instanceof ServiceProvider) {
            ServiceProvider newServiceProvider = (ServiceProvider) entity;
            String serviceProviderNumber = newServiceProvider.getDealerNumber();
            ServiceProvider oldServiceProvider = getServiceProviderByNumber(serviceProviderNumber);
            createOrUpdateServiceProvider(oldServiceProvider, newServiceProvider, customerTypeDTO);
        } else if (entity instanceof Supplier) {
            Supplier newSupplier = (Supplier) entity;
            Supplier oldSupplier = supplierService.findSupplierByNumberWithOutActiveInactiveFilter(newSupplier.getSupplierNumber());
             createOrUpdateSupplier(oldSupplier, newSupplier, customerTypeDTO);
        }
    }

	private ServiceProvider getServiceProviderByNumber(
			String serviceProviderNumber) {
		ServiceProvider serviceProvider = orgService
				.findServiceProviderByNumberWithOutBU(serviceProviderNumber);
		
		return serviceProvider;
	}

    private boolean createOrUpdateServiceProvider(ServiceProvider oldServiceProvider, ServiceProvider newServiceProvider, String newCustomerType) throws ServiceException {
        if (oldServiceProvider != null) {
            if (oldServiceProvider.getPreferredCurrency() != null && oldServiceProvider.getPreferredCurrency().
                    getCurrencyCode() != null && !oldServiceProvider.getPreferredCurrency().
                    getCurrencyCode().equalsIgnoreCase(newServiceProvider.getPreferredCurrency().getCurrencyCode())) {
                if (claimService.areOpenClaimsPresentForServiceProvider(oldServiceProvider.getDealerNumber())) {
                    throw new ServiceException(uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0070));
                }
            }
            if (hasServiceProviderTypeChanged(oldServiceProvider, newCustomerType)) {
                if (claimService.areOpenClaimsPresentForServiceProvider(oldServiceProvider.getDealerNumber())) {
                    throw new ServiceException(uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0071));
                } else if (inventoryService.areInventoriesPresentforThisServiceProvider(oldServiceProvider.getDealerNumber())) {
                    throw new ServiceException(uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0072));
                } else {
                    deactivateRelevantServiceProvider(oldServiceProvider);
                    return true;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean hasServiceProviderTypeChanged(ServiceProvider serviceProvider, String newCustomerType) {
        return !((serviceProvider.isOriginalEquipManufacturer() && IntegrationConstants.OEM.equals(newCustomerType))
                || (serviceProvider.isNationalAccount() && IntegrationConstants.NATIONAL_ACCOUNT.equals(newCustomerType))
                || (serviceProvider.isInterCompany() && IntegrationConstants.INTERCOMPANY.equals(newCustomerType))
                || (serviceProvider.isDirectCustomer() && IntegrationConstants.DIRECT_CUSTOMER.equals(newCustomerType))
                || (serviceProvider.isDealer() && IntegrationConstants.DEALER.equals(newCustomerType)));
    }

    private void deactivateRelevantServiceProvider(ServiceProvider serviceProvider) {
    	String deactivatedCustomerNumber = "";
    	if(serviceProvider.getId()!=null){
    		deactivatedCustomerNumber = serviceProvider.getDealerNumber() + "_" + serviceProvider.getId() + "_DEACTIVATED";
    	}else{
    		deactivatedCustomerNumber = serviceProvider.getDealerNumber() + "_DEACTIVATED";
    	}
        serviceProvider.setServiceProviderNumber(deactivatedCustomerNumber);
        serviceProvider.setStatus(getStatus("I"));
        serviceProvider.getD().setActive(false);
        if (serviceProvider.isDirectCustomer()) {
            ((DirectCustomer) serviceProvider).setDirectCustomerNumber(deactivatedCustomerNumber);
        } else if (serviceProvider.isInterCompany()) {
            ((InterCompany) serviceProvider).setInterCompanyNumber(deactivatedCustomerNumber);
        } else if (serviceProvider.isNationalAccount()) {
            ((NationalAccount) serviceProvider).setNationalAccountNumber(deactivatedCustomerNumber);
        } else if (serviceProvider.isOriginalEquipManufacturer()) {
            ((OriginalEquipManufacturer) serviceProvider).setOrgEquipManufNumber(deactivatedCustomerNumber);
        } else if (serviceProvider.isDealer()) {
            ((Dealership) serviceProvider).setDealerNumber(deactivatedCustomerNumber);
        }
    }
    
    private void deactivateRelevantSupplier(Supplier supplier) {
    	String deactivatedCustomerNumber = supplier.getSupplierNumber();
        supplier.getD().setActive(false);
        supplier.setSupplierNumber(deactivatedCustomerNumber);
    }
    
    

    public AddressBookService getAddressBookService() {
        return addressBookService;
    }

    public void setAddressBookService(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public OrgService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public SupplierService getSupplierService() {
        return supplierService;
    }

    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    public UploadSyncInterfaceErrorConstants getUploadSyncInterfaceErrorConstants() {
        return uploadSyncInterfaceErrorConstants;
    }

    public void setUploadSyncInterfaceErrorConstants(UploadSyncInterfaceErrorConstants uploadSyncInterfaceErrorConstants) {
        this.uploadSyncInterfaceErrorConstants = uploadSyncInterfaceErrorConstants;
    }

    private void createSupplier(Supplier newSupplier, Object entity) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void updateSupplier(Supplier oldSupplier, Supplier newSupplier, Object input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createOrUpdateServiceProvider(ServiceProvider oldServiceProvider, ServiceProvider newServiceProvider, CustomerTypeDTO customerTypeDTO) throws ServiceException {
        String customerType = getCustomerType(newServiceProvider);
        boolean create = createOrUpdateServiceProvider(oldServiceProvider, newServiceProvider, customerType);
        if(create==true&&customerTypeDTO.getShipToChanged().toString().equals("Y")&&IntegrationConstants.DEALER.equals(customerType)){
        	throw new RuntimeException(uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0066));
        }
        ServiceProvider provider;
        AddressTypeDTO billToAddressDTO = null;
        ShipToLocationsTypeDTO shipToLocationsDTO = null;
        if (create) {
            provider = createServiceProviderForCustomerType(customerType);
            provider = newServiceProvider;
        } else {
            provider = oldServiceProvider;
        }
        String dealerCode = null;
        String brand = populateBrands(customerTypeDTO);
        ServiceProvider dealerFamilyCode = null;
        String dealerFamilyCodeText = customerTypeDTO.getDealerFamilyCode().trim();
        if (StringUtils.hasText(dealerFamilyCodeText) && !dealerFamilyCodeText.equalsIgnoreCase(provider.getDealerNumber())) {
            dealerFamilyCode = getServiceProviderByNumber(dealerFamilyCodeText);
        }
        String sellingLocation = customerTypeDTO.getSellingLocation() != null ? customerTypeDTO.getSellingLocation().trim() : null;
        String businessArea = customerTypeDTO.getBusinessArea() != null ? customerTypeDTO.getBusinessArea().trim() : null;
        String fleetBusinessArea = customerTypeDTO.getFleetBusinessArea() != null ? customerTypeDTO.getFleetBusinessArea().trim() : null;
        String serviceProviderType = customerTypeDTO.getServiceProviderType() != null ? customerTypeDTO.getServiceProviderType().trim() : null;
        String serviceProviderDescription = customerTypeDTO.getServiceProviderType() != null ? customerTypeDTO.getServiceProviderDesc().trim() : null;
        String salesTerritoryName = customerTypeDTO.getSalesTerritoryName() != null ? customerTypeDTO.getSalesTerritoryName() : null;
        Dealership dualBrandDealership = null;
        if (customerTypeDTO.getDualBrandDealer() != null&&StringUtils.hasText(customerTypeDTO.getDualBrandDealer())) {
            ServiceProvider dualBrandServiceProvider = getServiceProviderByNumber(customerTypeDTO.getDualBrandDealer().trim());
            if (dualBrandServiceProvider instanceof Dealership) {
                dualBrandDealership = (Dealership) dualBrandServiceProvider;
            }
        }
        shipToLocationsDTO = customerTypeDTO.getShipToLocations();
        if (StringUtils.hasText(customerTypeDTO.getCustomerNumber())) {
            dealerCode = customerTypeDTO.getCustomerNumber().trim();
        }
        //updateCustomerAddress(provider, newServiceProvider.getAddress());
        //OrganizationAddress primaryAddress = createOrUpdateAddress(provider, newServiceProvider.getAddress());
        if (create) {
            if (newServiceProvider instanceof Dealership) {
                Dealership dealer = (Dealership) provider;
                if(customerTypeDTO.getStatus()!=null){
                	dealer.setStatus(getStatus(customerTypeDTO.getStatus().toString()));
	                if (dealer.getStatus()!=null && customerTypeDTO.getStatus().toString().equalsIgnoreCase("I")) {
	                    deactivateRelevantServiceProvider((ServiceProvider) dealer);
	                    if(dealer.getId()!=null){
	                    	orgService.getPartyById(dealer.getId()).getD().setActive(false);
	                    }
	                }
                }
                billToAddressDTO = customerTypeDTO.getBillToAddress();
                dealer.setMarketingGroup(customerTypeDTO.getMarketingGroupCode());
                dealer.setBrand(brand);
                dealer.setSalesTerritoryName(salesTerritoryName);
                dealer.setSellingLocation(sellingLocation);
                dealer.setBusinessArea(businessArea);
                dealer.setFleetBusinessArea(fleetBusinessArea);
                dealer.setServiceProviderType(serviceProviderType);
                dealer.setServiceProviderDescription(serviceProviderDescription);
                dealer.setEnterpriseDealer(Boolean.FALSE);
                if (customerTypeDTO != null) {
                    addBusinessUnits(customerTypeDTO, dealer);
                }
                if (dealerFamilyCode != null) {
                    dealer.setPartOf(dealerFamilyCode);
                }
                if (dualBrandDealership != null) {
                    dealer.setDualDealer(dualBrandDealership);
                }
                if(customerTypeDTO.getShipToChanged().toString().equals("N")){
                dealer.setName(customerTypeDTO.getCustomerName()+"-"+dealer.getDealerNumber());
                }
                orgService.createDealership(dealer);
                if (StringUtils.hasText(dealer.getDealerNumber())) {
                    dealer = (Dealership) getServiceProviderByNumber(dealer.getDealerNumber().trim());
                }
                if(customerTypeDTO.getShipToChanged().toString().equals("N")){
                	createOrUpdateBillToAddress(dealer, billToAddressDTO);
                }
            }

        } else {
            if (oldServiceProvider instanceof Dealership && newServiceProvider instanceof Dealership) {
                mergeServiceProvider(oldServiceProvider, newServiceProvider, customerTypeDTO);
                Dealership dealer = (Dealership) oldServiceProvider;
                billToAddressDTO = customerTypeDTO.getBillToAddress();
                dealer.setEnterpriseDealer(Boolean.FALSE);
                if(customerTypeDTO.getShipToChanged().toString().equals("N")){
                dealer.setMarketingGroup(customerTypeDTO.getMarketingGroupCode());
                dealer.setBrand(brand);
                if (dualBrandDealership != null) {
                    dealer.setDualDealer(dualBrandDealership);
                }
                if (customerTypeDTO != null) {
                    addBusinessUnits(customerTypeDTO, dealer);
                }
                if (dealerFamilyCode != null) {
                    dealer.setPartOf(dealerFamilyCode);
                }
                dealer.setSalesTerritoryName(salesTerritoryName);
                dealer.setSellingLocation(sellingLocation);
                dealer.setBusinessArea(businessArea);
                dealer.setFleetBusinessArea(fleetBusinessArea);
                dealer.setServiceProviderType(serviceProviderType);
                dealer.setServiceProviderDescription(serviceProviderDescription);
                dealer.setName(customerTypeDTO.getCustomerName()+"-"+dealer.getDealerNumber());
                createOrUpdateBillToAddress(dealer, billToAddressDTO);
                }
                else{
                	createOrUpdateShipToAdderess(shipToLocationsDTO, dealer,customerTypeDTO.getCustomerName());
                }
                orgService.updateDealership(dealer);
                newServiceProvider=(ServiceProvider)dealer;
            }

        }
        /*if (provider.getD().isActive()) {
            createOrUpdateAddrBookMappingForPrimaryAddr(newServiceProvider, primaryAddress);
        }*/

    }

    private void updateCustomerAddress(ServiceProvider serviceProvider,
                                       Address primaryShipToaddress) {
        if (serviceProvider.getAddress() == null) {
            serviceProvider.setAddress(primaryShipToaddress);
        } else {
            serviceProvider.getAddress().setContactPersonName(primaryShipToaddress.getContactPersonName());
            serviceProvider.getAddress().setAddressLine1(primaryShipToaddress.getAddressLine1());
            serviceProvider.getAddress().setAddressLine2(primaryShipToaddress.getAddressLine2());
            serviceProvider.getAddress().setAddressLine3(primaryShipToaddress.getAddressLine3());
            serviceProvider.getAddress().setAddressLine4(primaryShipToaddress.getAddressLine4());
            serviceProvider.getAddress().setAddressIdOnRemoteSystem(primaryShipToaddress.getAddressIdOnRemoteSystem());
            serviceProvider.getAddress().setCity(primaryShipToaddress.getCity());
            serviceProvider.getAddress().setState(primaryShipToaddress.getState());
            serviceProvider.getAddress().setCountry(primaryShipToaddress.getCountry());
            serviceProvider.getAddress().setZipCode(primaryShipToaddress.getZipCode());
            serviceProvider.getAddress().setZipcodeExtension(primaryShipToaddress.getZipcodeExtension());
            serviceProvider.getAddress().setEmail(primaryShipToaddress.getEmail());
            serviceProvider.getAddress().setPhone(primaryShipToaddress.getPhone());
            serviceProvider.getAddress().setPhoneExt(primaryShipToaddress.getPhoneExt());
            serviceProvider.getAddress().setSicCode(primaryShipToaddress.getSicCode());
            serviceProvider.getAddress().setSecondaryPhone(primaryShipToaddress.getSecondaryPhone());
            serviceProvider.getAddress().setSecondaryPhoneExt(primaryShipToaddress.getSecondaryPhoneExt());
            serviceProvider.getAddress().setFax(primaryShipToaddress.getFax());
            serviceProvider.getAddress().setStatus(getStatus(primaryShipToaddress.getStatus(),serviceProvider.getAddress()));
            if (primaryShipToaddress.getStatus()!=null && primaryShipToaddress.getStatus().equalsIgnoreCase("I")) {
                serviceProvider.getAddress().getD().setActive(false);
            }
        }
    }

    private String getCustomerType(ServiceProvider newServiceProvider) throws ServiceException {
        if (newServiceProvider instanceof Dealership) {
            return IntegrationConstants.DEALER;
        } else if (newServiceProvider instanceof DirectCustomer) {
            return IntegrationConstants.DIRECT_CUSTOMER;
        } else if (newServiceProvider instanceof InterCompany) {
            return IntegrationConstants.INTERCOMPANY;
        } else if (newServiceProvider instanceof NationalAccount) {
            return IntegrationConstants.NATIONAL_ACCOUNT;
        } else if (newServiceProvider instanceof OriginalEquipManufacturer) {
            return IntegrationConstants.OEM;
        }
        throw new ServiceException("Invalid Instance, Unable to finde Customer Type !!!");
    }

    private ServiceProvider createServiceProviderForCustomerType(String customerType) {
        if (IntegrationConstants.DEALER.equals(customerType)) {
            return new Dealership();
        } else if (IntegrationConstants.DIRECT_CUSTOMER.equals(customerType)) {
            return new DirectCustomer();
        } else if (IntegrationConstants.INTERCOMPANY.equals(customerType)) {
            return new InterCompany();
        } else if (IntegrationConstants.NATIONAL_ACCOUNT.equals(customerType)) {
            return new NationalAccount();
        } else {
            return new OriginalEquipManufacturer();
        }
    }

    private void mergeServiceProvider(ServiceProvider oldServiceProvider, ServiceProvider newServiceProvider, CustomerTypeDTO customerTypeDTO) {
        final Dealership oldDealership = (Dealership) oldServiceProvider;
        final Dealership newDealership = (Dealership) newServiceProvider;
        if(customerTypeDTO.getShipToChanged().toString().equals("N")){
        	oldDealership.setName(newDealership.getName()+"-"+newDealership.getDealerNumber());
        
        oldDealership.setDealerNumber(newDealership.getDealerNumber());
        oldDealership.setRegionCode(newDealership.getRegionCode());
        oldDealership.setServiceProviderNumber(newDealership.getServiceProviderNumber());
        oldDealership.setPrimaryContactpersonFstName(newDealership.getPrimaryContactpersonFstName());
        oldDealership.setPrimaryContactpersonLstName(newDealership.getPrimaryContactpersonLstName());
        oldDealership.setStatus(getStatus(newDealership.getStatus()));
        if (newDealership.getStatus()!=null && newDealership.getStatus().equalsIgnoreCase("I")) {
            deactivateRelevantServiceProvider((ServiceProvider) oldDealership);
            orgService.getPartyById(oldDealership.getId()).getD().setActive(false);
        }
        oldDealership.setCompanyType(IntegrationConstants.DEALER);
        oldDealership.setXmlacknowledgementFooter(newDealership.getXmlacknowledgementFooter());
        oldDealership.setNetwork(newDealership.getNetwork());
        oldDealership.setLanguage(newDealership.getLanguage());
        if (newServiceProvider.getPreferredCurrency() != null) {
            oldDealership.setPreferredCurrency(newDealership.getPreferredCurrency());
        }
        }
    }


    private void createOrUpdateBillToAddress(Dealership dealer, AddressTypeDTO billToAddressDTO) {
    	Address billToAddress = null;
        if (billToAddressDTO != null && dealer != null) {
            StringBuffer name = new StringBuffer();
            final StringBuffer location = new StringBuffer();
            AddressBookAddressMapping addressBookAddressMapping = addressBookService.getAddressBookAddressMappingByOrganizationAndType(dealer, AddressType.BILLING );
            if(addressBookAddressMapping != null){
            	billToAddress = addressBookAddressMapping.getAddress();
            }
            if (billToAddress == null) {
                billToAddress = new Address();
                //billToAddress.setSiteNumber(siteNumber);
                populateBillToAddress(billToAddressDTO, billToAddress, name,
                        location);
                billToAddress.setBelongsTo(dealer);
                dealer.setAddress(billToAddress);
            } else {
                populateBillToAddress(billToAddressDTO, billToAddress, name, location);
                billToAddress.setBelongsTo(dealer);
                dealer.setAddress(billToAddress);
            }
        }
        createOrUpdateAddrBookMappingForBillToAddr(dealer, billToAddress);
    }


    private void populateBillToAddress(
            AddressTypeDTO billToAddressDTO,
            Address billToAddress, StringBuffer name,
            StringBuffer location) {
        String firstName;
        String middleName;
        String lastName;
        if (StringUtils.hasText(billToAddressDTO.getFirstName())) {
            firstName = billToAddressDTO.getFirstName().trim();
            name.append(firstName).append(" ");
        }
        if (StringUtils.hasText(billToAddressDTO.getMiddleName())) {
            middleName = billToAddressDTO.getMiddleName().trim();
            name.append(middleName).append(" ");
        }
        if (StringUtils.hasText(billToAddressDTO.getLastName())) {
            lastName = billToAddressDTO.getLastName().trim();
            name.append(lastName);
        }
        if (StringUtils.hasText(name)) {
            billToAddress.setContactPersonName(name.toString());
        }
        if (StringUtils.hasText(billToAddressDTO.getAddressline1())) {
            billToAddress.setAddressLine1(billToAddressDTO.getAddressline1().trim());
            location.append(billToAddressDTO.getAddressline1().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getAddressline2())) {
            billToAddress.setAddressLine2(billToAddressDTO.getAddressline2().trim());
            location.append(billToAddressDTO.getAddressline2().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getAddressline3())) {
            billToAddress.setAddressLine3(billToAddressDTO.getAddressline3().trim());
            location.append(billToAddressDTO.getAddressline3().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getAddressline4())) {
            billToAddress.setAddressLine4(billToAddressDTO.getAddressline4().trim());
            location.append(billToAddressDTO.getAddressline4().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getCity())) {
            billToAddress.setCity(billToAddressDTO.getCity().trim());
            location.append(billToAddressDTO.getCity().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getState())) {
            billToAddress.setState(billToAddressDTO.getState().trim());
            location.append(billToAddressDTO.getState().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getCountyCode())) {
            billToAddress.setCounty(billToAddressDTO.getCountyCode().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getCountry())) {
            billToAddress.setCountry(billToAddressDTO.getCountry().trim());
            location.append(billToAddressDTO.getCountry().trim());
            location.append("-");
        }
        if (StringUtils.hasText(billToAddressDTO.getFax())) {
            billToAddress.setFax(billToAddressDTO.getFax().trim());
        }
        if (StringUtils.hasText(billToAddressDTO.getEmail())) {
            billToAddress.setEmail(billToAddressDTO.getEmail().trim());
        }
        if (StringUtils.hasText(billToAddressDTO.getPhone())) {
            billToAddress.setPhone(billToAddressDTO.getPhone());
        }
        if (StringUtils.hasText(billToAddressDTO.getPhoneExt())) {
            billToAddress.setPhoneExt(billToAddressDTO.getPhoneExt().trim());
        }
        if (StringUtils.hasText(billToAddressDTO.getSecondaryPhone())) {
            billToAddress.setSecondaryPhone(billToAddressDTO.getSecondaryPhone().trim());
        }
        if (StringUtils.hasText(billToAddressDTO.getSicCode())) {
            billToAddress.setSicCode(billToAddressDTO.getSicCode().trim());
        }
        if (StringUtils.hasText(billToAddressDTO.getSecondaryPhoneExt())) {
            billToAddress.setSecondaryPhoneExt(billToAddressDTO.getSecondaryPhoneExt().trim());
        }
        if (StringUtils.hasText(billToAddressDTO.getStatus())) {
            billToAddress.setStatus(getStatus(billToAddressDTO.getStatus(),billToAddress));
        }else{
        	billToAddress.setStatus(IntegrationConstants.ACTIVE);
        }
        if (StringUtils.hasText(billToAddressDTO.getZipcode())) {
            billToAddress.setZipCode(billToAddressDTO.getZipcode().trim());
            location.append(billToAddressDTO.getZipcode().trim());
        }
        if (StringUtils.hasText(billToAddressDTO.getZipExtension())) {
            billToAddress.setZipcodeExtension(billToAddressDTO.getZipExtension().trim());
        }
        if (location != null) {
            billToAddress.setLocation(location.toString());
        }

    }

    private void createOrUpdateAddressBookMappingForShipToLocation(ServiceProvider serviceProvider, Address address, boolean isPrimary) {
        AddressBook addressBook = addressBookService.getAddressBookByOrganizationAndType(serviceProvider,
    			AddressBookType.SELF);
    	if (addressBook == null) {
    		List<AddressBookAddressMapping> mappings = new ArrayList<AddressBookAddressMapping>();
    		AddressBookAddressMapping addressBookAddressMapping = createShipToAddressBookAddressMappingForDealer(
    				addressBook, isPrimary, address,AddressBookType.SELF,serviceProvider);
    		addressBookAddressMapping.setEndCustomer(Boolean.FALSE);
    		mappings.add(addressBookAddressMapping);
    	} else {
    		AddressBookAddressMapping addressBookAddressMapping = null;
    		addressBookAddressMapping = addressBookService.getAddressBookAddressMappingByOrganizationAndAddressAndBookType(
    				address, serviceProvider, AddressBookType.SELF);
    		if (addressBookAddressMapping == null) {
    			addressBookAddressMapping = createShipToAddressBookAddressMappingForDealer(addressBook, isPrimary, address,AddressBookType.SELF,serviceProvider);
    			addressBookAddressMapping.setEndCustomer(false);
    			addressBookService.createAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
    		} else{
    			setPrimaryValue(addressBookAddressMapping,isPrimary,serviceProvider, AddressBookType.SELF,addressBook);
    			addressBookAddressMapping.setEndCustomer(false);
    			addressBookService.updateAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
    		}
    	}
    }

    private void createOrUpdateAddressBookMappingForEndCustomer(ServiceProvider serviceProvider, Address address, boolean isPrimary) {
        AddressBook addressBook = addressBookService.getAddressBookByOrganizationAndType(serviceProvider,
                AddressBookType.ENDCUSTOMER);
        if (addressBook == null) {
            addressBook = new AddressBook();
            addressBook.setBelongsTo(serviceProvider);
            addressBook.setType(AddressBookType.ENDCUSTOMER);
            List<AddressBookAddressMapping> mappings = new ArrayList<AddressBookAddressMapping>();
            AddressBookAddressMapping addressBookAddressMapping = createShipToAddressBookAddressMappingForDealer(
                    addressBook, isPrimary, address, AddressBookType.ENDCUSTOMER, serviceProvider);
            addressBookAddressMapping.setEndCustomer(Boolean.TRUE);
            mappings.add(addressBookAddressMapping);
            addressBook.setAddressBookAddressMapping(mappings);
            addressBookService.createAddressBook(addressBook);
        } else {
            AddressBookAddressMapping addressBookAddressMapping = addressBookService.getAddressBookAddressMappingByOrganizationAndAddressAndBookType(
                    address, serviceProvider, AddressBookType.ENDCUSTOMER);
            if (addressBookAddressMapping == null) {
                addressBookAddressMapping = createShipToAddressBookAddressMappingForDealer(
                        addressBook, isPrimary, address, AddressBookType.ENDCUSTOMER, serviceProvider);
                addressBookAddressMapping.setEndCustomer(true);
                addressBookService.createAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
            } else {
                setPrimaryValue(addressBookAddressMapping, isPrimary, serviceProvider, AddressBookType.ENDCUSTOMER, addressBook);
                addressBookAddressMapping.setEndCustomer(true);
                addressBookService.updateAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
            }
        }
    }

    private void createOrUpdateShipToAdderess(ShipToLocationsTypeDTO shipToLocationsDTO,
                                                                   Dealership dealer,String customerName) {
    	boolean isEndCustomer = Boolean.FALSE;
    	boolean isPrimary = Boolean.FALSE;
        if (shipToLocationsDTO != null) {
            ShipToLocationTypeDTO[] shipToLocations = shipToLocationsDTO.getShipToLocationArray();
            if (shipToLocations.length > 0) {
                for (ShipToLocationTypeDTO shipToLocation : shipToLocations) {
                	if(shipToLocation.getAddress().getIsEndCustomer()!=null){
                		if(shipToLocation.getAddress().getIsEndCustomer().equals("Y")){
                			isEndCustomer = Boolean.TRUE;
                		}else if(shipToLocation.getAddress().getIsEndCustomer().equals("N")){
                			isEndCustomer = Boolean.FALSE;
                		}
                	}else{
                		throw new RuntimeException(UploadSyncInterfaceErrorConstants.CU0063+"$#"+uploadSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(UploadSyncInterfaceErrorConstants.CU0063));
                	}
                	if(shipToLocation.getAddress().getIsPrimary()!=null){
            	    	if(shipToLocation.getAddress().getIsPrimary().equals("Y")){
            	        	isPrimary = Boolean.TRUE;
            	    	}else if(shipToLocation.getAddress().getIsPrimary().equals("N")){
            	    		isPrimary = Boolean.FALSE;
            	    	}
                    }
                    if (!isEndCustomer) {
                        String siteNumber = null;
                        OrganizationAddress shipToAddress;
                        if (StringUtils.hasText(shipToLocation.getAddress().getSiteNumber())) {
                            siteNumber = shipToLocation.getAddress().getSiteNumber().trim();
                        }
                        shipToAddress = orgService.getAddressesForOrganizationBySiteNumber(dealer, siteNumber);
                        if (shipToAddress == null) {
                            shipToAddress = new OrganizationAddress();
                            shipToAddress.setBelongsTo(dealer);
                            shipToAddress.setName(customerName);
                            populateAddress(shipToLocation, shipToAddress);
                            orgService.createOrgAddressForDealer(shipToAddress, dealer);
                        } else {
                            shipToAddress.setBelongsTo(dealer);
                            shipToAddress.setName(customerName);
                            populateAddress(shipToLocation, shipToAddress);
                            orgService.updateOrganizationAddress(shipToAddress);
                        }
                        createOrUpdateAddressBookMappingForShipToLocation(dealer, shipToAddress, isPrimary);
                    } else {
                        String customerId = dealer.getDealerNumber();
                        Customer customer;
                        Address address;
                        if (StringUtils.hasText(shipToLocation.getAddress().getSiteNumber())) {
                            customerId = customerId + shipToLocation.getAddress().getSiteNumber().trim();
                        }
                        customer = customerService.findCustomerByCustomerIdAndDealer(customerId, dealer);
                        if (customer == null) {
                            customer = new Customer();
                            customer.setCustomerId(customerId);
                            customer.setCompanyName(customerName);
                            customer.setCorporateName(customerName);
                            customer.setIndividual(Boolean.FALSE);
                            customer.setSiCode(shipToLocation.getAddress().getSicCode());

                            address = new Address();
                            populateAddress(shipToLocation.getAddress(), address);
                            customer.setAddress(address);
                            List<Address> addresses = new ArrayList<Address>();
                            addresses.add(address);
                            customer.setAddresses(addresses);

                            customerService.createCustomer(customer);
                        } else {
                            customer.setCustomerId(customerId);
                            customer.setCompanyName(customerName);
                            customer.setCorporateName(customerName);
                            customer.setSiCode(shipToLocation.getAddress().getSicCode());

                            address = customer.getAddress();
                            populateAddress(shipToLocation.getAddress(), address);

                            customerService.updateCustomer(customer);
                        }
                        createOrUpdateAddressBookMappingForEndCustomer(dealer, address, isPrimary);
                    }
                }
            }
        }
    }

    private void populateAddress(ShipToLocationTypeDTO shipToLocation, final OrganizationAddress orgAddress) {
        AddressTypeDTO addressDTO = shipToLocation.getAddress();
        if (StringUtils.hasText(shipToLocation.getAddress().getSiteNumber())) {
            orgAddress.setSiteNumber(shipToLocation.getAddress().getSiteNumber().trim());
        }
        StringBuffer location = populateAddress(addressDTO, orgAddress);
		if (StringUtils.hasText(addressDTO.getStatus())) {
			if ("I".equalsIgnoreCase(addressDTO.getStatus())) {
				orgAddress.setAddressActive(Boolean.FALSE);
			} else if ("A".equalsIgnoreCase(addressDTO.getStatus())) {
				orgAddress.setAddressActive(Boolean.TRUE);
			}
		}
        orgAddress.setLocation(location.toString());
    }

    private StringBuffer populateAddress(AddressTypeDTO addressDTO, Address orgAddress) {
        StringBuffer name = new StringBuffer();
        if (StringUtils.hasText(addressDTO.getFirstName())) {
            name.append(addressDTO.getFirstName().trim()).append(" ");
        }
        if (StringUtils.hasText(addressDTO.getMiddleName())) {
            name.append(addressDTO.getMiddleName().trim()).append(" ");
        }
        if (StringUtils.hasText(addressDTO.getLastName())) {
            name.append(addressDTO.getLastName().trim());
        }
        if (StringUtils.hasText(name)) {
            orgAddress.setContactPersonName(name.toString());
        }
        StringBuffer location = new StringBuffer();
        if (StringUtils.hasText(addressDTO.getAddressline1())) {
            orgAddress.setAddressLine1(addressDTO.getAddressline1().trim());
            location.append(addressDTO.getAddressline1().trim());
            location.append("-");
        }
        if (StringUtils.hasText(addressDTO.getAddressline2())) {
            orgAddress.setAddressLine2(addressDTO.getAddressline2().trim());
            location.append(addressDTO.getAddressline2().trim());
            location.append("-");
        }
        if (StringUtils.hasText(addressDTO.getAddressline3())) {
            orgAddress.setAddressLine3(addressDTO.getAddressline3().trim());
            location.append(addressDTO.getAddressline3().trim());
            location.append("-");
        }
        if (StringUtils.hasText(addressDTO.getAddressline4())) {
            orgAddress.setAddressLine4(addressDTO.getAddressline4().trim());
            location.append(addressDTO.getAddressline4().trim());
            location.append("-");
        }
        if (StringUtils.hasText(addressDTO.getCity())) {
            orgAddress.setCity(addressDTO.getCity().trim());
            location.append(addressDTO.getCity().trim());
            location.append("-");
        }
        if (StringUtils.hasText(addressDTO.getState())) {
            orgAddress.setState(addressDTO.getState().trim());
            location.append(addressDTO.getState().trim());
            location.append("-");
        }
        if (StringUtils.hasText(addressDTO.getCountyCode())) {
            orgAddress.setCounty(addressDTO.getCountyCode().trim());
        }
        if (StringUtils.hasText(addressDTO.getCountry())) {
            orgAddress.setCountry(addressDTO.getCountry().trim());
            location.append(addressDTO.getCountry().trim());
            location.append("-");
        }
        if (StringUtils.hasText(addressDTO.getFax())) {
            orgAddress.setFax(addressDTO.getFax().trim());
        }
        if (StringUtils.hasText(addressDTO.getEmail())) {
            orgAddress.setEmail(addressDTO.getEmail().trim());
        }
        if (StringUtils.hasText(addressDTO.getPhone())) {
            orgAddress.setPhone(addressDTO.getPhone());
        }
        if (StringUtils.hasText(addressDTO.getPhoneExt())) {
            orgAddress.setPhoneExt(addressDTO.getPhoneExt().trim());
        }
        if (StringUtils.hasText(addressDTO.getSecondaryPhone())) {
            orgAddress.setSecondaryPhone(addressDTO.getSecondaryPhone().trim());
        }
        if (StringUtils.hasText(addressDTO.getSecondaryPhoneExt())) {
            orgAddress.setSecondaryPhoneExt(addressDTO.getSecondaryPhoneExt().trim());
        }
        if (StringUtils.hasText(addressDTO.getStatus())) {
        	orgAddress.setStatus(getStatus(addressDTO.getStatus().trim(),orgAddress));
        }
        if (StringUtils.hasText(addressDTO.getSicCode())) {
            orgAddress.setSicCode(addressDTO.getSicCode().trim());
        }
        if (StringUtils.hasText(addressDTO.getZipcode())) {
            orgAddress.setZipCode(addressDTO.getZipcode().trim());
            location.append(addressDTO.getZipcode().trim());
        }
        if (StringUtils.hasText(addressDTO.getZipExtension())) {
            orgAddress.setZipcodeExtension(addressDTO.getZipExtension().trim());
        }
        return location;
    }


    private OrganizationAddress createOrUpdateAddress(ServiceProvider serviceProvider, Address address) {
        OrganizationAddress organizationAddress = null;
        if (null != address && StringUtils.hasText(address.getAddressLine1())) {
            if ("I".equalsIgnoreCase(address.getStatus()) && organizationAddress == null) {
//	              Got an new address with inactive status no need to create this !!.
                return organizationAddress;
            }
            if (organizationAddress == null) { // new Address with active status create new org address
                organizationAddress = new OrganizationAddress();
                List<OrganizationAddress> orgAddresses = serviceProvider.getOrgAddresses();
                if (orgAddresses == null) {
                    orgAddresses = new ArrayList<OrganizationAddress>();
                    serviceProvider.setOrgAddresses(orgAddresses);
                }
                orgAddresses.add(organizationAddress);
            }
            StringBuffer location = new StringBuffer();
            /*
             * if (address.getFirstName() != null && address.getLastName() !=
             * null) { String contactPersonName = address.getFirstName() + " " +
             * address.getLastName();
             * organizationAddress.setContactPersonName(contactPersonName); }
             * This needs to be moved to GlobalCustomerSync
             */
            if (address.getAddressLine1() != null) {
                organizationAddress.setAddressLine1(address.getAddressLine1());
                location.append(address.getAddressLine1());
                location.append("-");
            }
            if (address.getAddressLine2() != null) {
                organizationAddress.setAddressLine2(address.getAddressLine2());
                location.append(address.getAddressLine2());
                location.append("-");
            }
            organizationAddress.setAddressLine3(address.getAddressLine3());
            organizationAddress.setAddressLine4(address.getAddressLine4());
            organizationAddress.setAddressIdOnRemoteSystem(serviceProvider.getSiteNumber());
            if (address.getCity() != null) {
                organizationAddress.setCity(address.getCity());
                location.append(address.getCity());
                location.append("-");
            }
            if (address.getState() != null) {
                organizationAddress.setState(address.getState());
                location.append(address.getState());
                location.append("-");
            }
            if (address.getZipCode() != null) {
                organizationAddress.setZipCode(address.getZipCode());
                location.append(address.getZipCode());
                location.append("-");
            }
            if (address.getCountry() != null) {
                organizationAddress.setCountry(address.getCountry());
                location.append(address.getCountry());
                location.append("-");
            }

            if (address.getStatus().equalsIgnoreCase("I")) {
                organizationAddress.getD().setActive(false);
            }
            if (location != null) {
                organizationAddress.setLocation(location.toString());
            }
            organizationAddress.setSiteNumber(serviceProvider.getSiteNumber());
        }
        return organizationAddress;

    }

    private void createOrUpdateAddrBookMappingForBillToAddr(
            ServiceProvider serviceProvider, Address billToAddress) {
        AddressBook addressBook = addressBookService.getAddressBookByOrganizationAndType(serviceProvider,
                AddressBookType.SELF);
        if (addressBook == null) {
            addressBook = new AddressBook();
            addressBook.setBelongsTo(serviceProvider);
            addressBook.setType(AddressBookType.SELF);
            List<AddressBookAddressMapping> mappings = new ArrayList<AddressBookAddressMapping>();
            AddressBookAddressMapping addressBookAddressMapping = null;
            if (billToAddress != null) {
                addressBookAddressMapping = createBillToAddressBookAddressMappingForDealer(
                        addressBook, billToAddress);
                addressBookAddressMapping.setEndCustomer(Boolean.FALSE);
                mappings.add(addressBookAddressMapping);
            }
            addressBook.setAddressBookAddressMapping(mappings);
            addressBookService.createAddressBook(addressBook);
        } else {
            /*
             * Reset the primary field if the primary address has changed 3.Make
             * the latest Organization Address as Primary site and set this
             * field to false for the old primary site.--DONE 4.Carefully update
             * the existing address_book_mapping and create a new mapping for
             * the new Organization address --DONE
             */

            AddressBookAddressMapping addressBookAddressMapping = null;
            if (billToAddress != null) {
                addressBookAddressMapping = addressBookService.getAddressBookAddressMappingByOrganizationAndAddressAndBookType(
                        billToAddress, serviceProvider, AddressBookType.SELF);
                if (addressBookAddressMapping == null) {
                    addressBookAddressMapping = createBillToAddressBookAddressMappingForDealer(addressBook, billToAddress);
                    addressBookAddressMapping.setEndCustomer(Boolean.FALSE);
                    addressBookService.createAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
                } else {
                    addressBookService.updateAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
                }
            }
        }
    }

    private AddressBookAddressMapping createShipToAddressBookAddressMappingForDealer(
            AddressBook addressBook, boolean isPrimary,
            Address shipToAddress, AddressBookType type, ServiceProvider serviceProvider) {
        AddressBookAddressMapping addressBookAddressMapping = new AddressBookAddressMapping();
        // TODO Get the address from Organization Addresses
        addressBookAddressMapping.setAddress(shipToAddress);
        addressBookAddressMapping.setAddressBook(addressBook);
        setPrimaryValue(addressBookAddressMapping, isPrimary, serviceProvider, type, addressBook);
        addressBookAddressMapping.setType(AddressType.SHIPPING);
        return addressBookAddressMapping;
    }

    private AddressBookAddressMapping createBillToAddressBookAddressMappingForDealer(
            AddressBook addressBook, Address address) {
        AddressBookAddressMapping addressBookAddressMapping = new AddressBookAddressMapping();
        // TODO Get the address from Organization Addresses
        addressBookAddressMapping.setAddress(address);
        addressBookAddressMapping.setPrimary(Boolean.FALSE);
        addressBookAddressMapping.setAddressBook(addressBook);
        addressBookAddressMapping.setType(AddressType.BILLING);
        return addressBookAddressMapping;
    }


    private AddressBookAddressMapping createAddressBookAddressMappingForServiceProvider(
            AddressBook addressBook, boolean isPrimary, OrganizationAddress orgAddress) {
        AddressBookAddressMapping addressBookAddressMapping = new AddressBookAddressMapping();
        // TODO Get the address from Organization Addresses
        addressBookAddressMapping.setAddress(orgAddress);
        addressBookAddressMapping.setAddressBook(addressBook);
        addressBookAddressMapping.setPrimary(isPrimary);
        addressBookAddressMapping.setType(AddressType.HOME);
        return addressBookAddressMapping;
    }

    private void createOrUpdateSupplier(Supplier existingSupplier, Supplier newSupplier, CustomerTypeDTO customerTypeDTO) throws ServiceException {
    	boolean create = false;
    	if (existingSupplier == null) {
    		if (!newSupplier.getD().isActive()) {
    			throw new ServiceException("No Supplier found with supplierNumber "
    					+ newSupplier.getSupplierNumber() + " and supplierName " + newSupplier.getName() + "with Status InActive");
    		} else {

    			if (logger.isDebugEnabled()) {
    				logger.debug("No Supplier found with supplierNumber "
    						+ newSupplier.getSupplierNumber() + " and supplierName " + newSupplier.getName());
    			}
    			// Create new Supplier
    			create = true;
    		}
    	}
    	Supplier supplier = null;
    	if (create) {
    		supplier = newSupplier;
    		//set orgaddress
    		createSupplierLocations(newSupplier.getAddress(),supplier,customerTypeDTO);
    		if (customerTypeDTO != null) {
                addBusinessUnits(customerTypeDTO, supplier);
            }
    		if (supplier.getStatus()!=null && supplier.getStatus().equalsIgnoreCase("I")) {
    			deactivateRelevantSupplier((Supplier) supplier);
            }
    		
			if(supplier.getId()!=null){
				orgService.getPartyById(supplier.getId()).getD().setActive(true);
			}
    		supplier.setName(supplier.getName()+"-"+supplier.getSupplierNumber());
    		supplier.setStatus(getStatus(supplier.getStatus()));	
    		supplierService.save(supplier);
    		assignSupplierToMasterSupplier(supplier);
    	}else{
    		if (existingSupplier.getSupplierLocations().isEmpty()) {
             	existingSupplier.setAddress(newSupplier.getAddress());
                 createSupplierLocations(newSupplier.getAddress(), existingSupplier,customerTypeDTO);
             } else {
                List<Location> existingLocations = existingSupplier.getLocations();
                 upDateSupplierLocations(existingLocations, existingSupplier, newSupplier.getAddress(), true, customerTypeDTO);
             }
    		existingSupplier.setName(customerTypeDTO.getCustomerName()+"-"+customerTypeDTO.getCustomerNumber());
    		if (customerTypeDTO != null) {
                addBusinessUnits(customerTypeDTO, existingSupplier);
            }
    		if (newSupplier.getStatus()!=null && newSupplier.getStatus().equalsIgnoreCase("I")) {
                deactivateRelevantSupplier((Supplier) existingSupplier);
            }
		    orgService.getPartyById(existingSupplier.getId()).getD().setActive(true);
    		existingSupplier.setPreferredCurrency(newSupplier.getPreferredCurrency());
    		existingSupplier.setStatus(getStatus(newSupplier.getStatus()));
    		supplierService.update(existingSupplier);
    	}
    }

	private void assignSupplierToMasterSupplier(Supplier supplier) {
		for (BusinessUnit businessUnit : supplier.getBusinessUnits()) {
			User user = null;
			if (businessUnit.getName() != null
					&& StringUtils.hasText(businessUnit.getName())) {
				if (IntegrationConstants.NMHG_EMEA
						.equalsIgnoreCase(businessUnit.getName())
						&& this.emeaMasterSupplier != null
						&& StringUtils.hasText(this.emeaMasterSupplier)) {
					user = orgService.findInternalUser(this.emeaMasterSupplier
							.trim(), IntegrationConstants.SUPPLIER_USER);
				} else if (IntegrationConstants.NMHG_US
						.equalsIgnoreCase(businessUnit.getName())
						&& this.amerMasterSupplier != null
						&& StringUtils.hasText(this.amerMasterSupplier)) {
					user = orgService.findInternalUser(this.amerMasterSupplier
							.trim(), IntegrationConstants.SUPPLIER_USER);
				}
			}
			if (user != null && user.getBelongsToOrganizations() != null) {
				user.getBelongsToOrganizations().add(supplier);
			} else {
				List<Organization> orgList = new ArrayList<Organization>();
				orgList.add(supplier);
				user.setBelongsToOrganizations(orgList);
			}
		}
	}

	private void createSupplierLocations(Address primaryshipToaddress,
    		Supplier supplier,CustomerTypeDTO customerTypeDTO) {
    	primaryshipToaddress.setBelongsTo(supplier);
    	primaryshipToaddress.setZipcodeExtension(customerTypeDTO.getPrimaryShipToAddress().getZipExtension());
    	primaryshipToaddress.setCounty(customerTypeDTO.getPrimaryShipToAddress().getCountyCode());
    	if(StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getStatus())){
    		primaryshipToaddress.setStatus(getStatus(customerTypeDTO.getPrimaryShipToAddress().getStatus(),primaryshipToaddress));
    	}else{
    		primaryshipToaddress.setStatus(IntegrationConstants.ACTIVE);
    	}
    	String firstName;
        String middleName;
        String lastName;
        StringBuffer name = new StringBuffer();
    	if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getFirstName())) {
            firstName = customerTypeDTO.getPrimaryShipToAddress().getFirstName().trim();
            name.append(firstName).append(" ");
        }
        if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getMiddleName())) {
            middleName = customerTypeDTO.getPrimaryShipToAddress().getMiddleName().trim();
            name.append(middleName).append(" ");
        }
        if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getLastName())) {
            lastName = customerTypeDTO.getPrimaryShipToAddress().getLastName().trim();
            name.append(lastName);
        }
        if (StringUtils.hasText(name)) {
        	primaryshipToaddress.setContactPersonName(name.toString());
        }
    	String locationCode = customerTypeDTO.getPrimaryShipToAddress().getSiteNumber();
    	Location location = new Location();
    	location.setAddress(primaryshipToaddress);
    	if(primaryshipToaddress.getCity()!=null)
    		locationCode += "-"+primaryshipToaddress.getCity();
    	location.setCode(locationCode);
    	supplier.addSupplierLocation("BUSINESS", location);
    	supplier.setPreferredLocationType("BUSINESS");
    	if (primaryshipToaddress.getStatus() != null && primaryshipToaddress.getStatus().equalsIgnoreCase("I")) {
    		location.getD().setActive(false);
    	}
    	/*if(shipToAddresses!=null && !shipToAddresses.isEmpty()){
			for (Address shipToAddress : shipToAddresses) {
			shipToAddress.setBelongsTo(supplier);
			Location shipToAddresslocation = new Location();
			shipToAddresslocation.setAddress(shipToAddress);
			shipToAddresslocation.setCode(shipToAddress.getAddressIdOnRemoteSystem());
			supplier.addSupplierLocation("BUSINESS", shipToAddresslocation);
			if (shipToAddress.getStatus().equalsIgnoreCase("I")) {
			shipToAddresslocation.getD().setActive(false);
			}
			}
		}*/
    }

    private void upDateSupplierLocations(List<Location> existingLocations,
                                         Supplier supplier, Address address, boolean isPrimaryShipToAddress, CustomerTypeDTO customerTypeDTO) {
        boolean update = false;
        Address existingAddress = new Address();
        for (Location existinglocation : existingLocations) {
        	String sitenumber = existinglocation.getCode();
        	if(sitenumber!= null && sitenumber.contains("-"))
        	{
        		sitenumber = sitenumber.split("-")[0];
        		if (customerTypeDTO.getPrimaryShipToAddress().getSiteNumber() != null && sitenumber.equals(customerTypeDTO.getPrimaryShipToAddress().getSiteNumber())) {
                    update = true;
                   // existingLocations.remove(existinglocation);
                    //existinglocation.setAddress(address);
                   //existingAddress = existinglocation.getAddress();
                    if (address.getStatus()!=null&&address.getStatus().equalsIgnoreCase("I")) {
                        existinglocation.getD().setActive(false);
                    }
                    break;
                }
        	}
        }
        String firstName;
        String middleName;
        String lastName;
        StringBuffer name = new StringBuffer();
        if (update) {
            if (address != null) {
            	if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getFirstName())) {
                    firstName = customerTypeDTO.getPrimaryShipToAddress().getFirstName().trim();
                    name.append(firstName).append(" ");
                }
                if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getMiddleName())) {
                    middleName = customerTypeDTO.getPrimaryShipToAddress().getMiddleName().trim();
                    name.append(middleName).append(" ");
                }
                if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getLastName())) {
                    lastName = customerTypeDTO.getPrimaryShipToAddress().getLastName().trim();
                    name.append(lastName);
                }
                if (StringUtils.hasText(name)) {
                	existingAddress.setContactPersonName(name.toString());
                }
                existingAddress.setAddressLine1(address.getAddressLine1());
                existingAddress.setAddressLine2(address.getAddressLine2());
                existingAddress.setAddressLine3(address.getAddressLine3());
                existingAddress.setAddressLine4(address.getAddressLine4());
                existingAddress.setAddressIdOnRemoteSystem(address.getAddressIdOnRemoteSystem());
                existingAddress.setCity(address.getCity());
                existingAddress.setState(address.getState());
                existingAddress.setCountry(address.getCountry());
                existingAddress.setZipCode(address.getZipCode());
                existingAddress.setZipcodeExtension(address.getZipcodeExtension());
                existingAddress.setEmail(address.getEmail());
                existingAddress.setPhone(address.getPhone());
                existingAddress.setPhoneExt(address.getPhoneExt());
                existingAddress.setSecondaryPhone(address.getSecondaryPhone());
                existingAddress.setSecondaryPhoneExt(address.getSecondaryPhoneExt());
                existingAddress.setFax(address.getFax());
                existingAddress.setStatus(getStatus(address.getStatus(),existingAddress));
                if (address.getStatus()!=null&&address.getStatus().equalsIgnoreCase("I")) {
                    existingAddress.getD().setActive(false);
                }
            }
            if (isPrimaryShipToAddress) {
                supplier.setAddress(existingAddress);
            }
        } else {
        	String locationCode = customerTypeDTO.getPrimaryShipToAddress().getSiteNumber();
            Location location = new Location();
            if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getFirstName())) {
                firstName = customerTypeDTO.getPrimaryShipToAddress().getFirstName().trim();
                name.append(firstName).append(" ");
            }
            if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getMiddleName())) {
                middleName = customerTypeDTO.getPrimaryShipToAddress().getMiddleName().trim();
                name.append(middleName).append(" ");
            }
            if (StringUtils.hasText(customerTypeDTO.getPrimaryShipToAddress().getLastName())) {
                lastName = customerTypeDTO.getPrimaryShipToAddress().getLastName().trim();
                name.append(lastName);
            }
            if (StringUtils.hasText(name)) {
            	address.setContactPersonName(name.toString());
            }
            location.setAddress(address);
            if(address.getCity()!=null)
        		locationCode += "-"+address.getCity();
            location.setCode(locationCode);
            if (address.getStatus()!=null&&address.getStatus().equalsIgnoreCase("I")) {
                location.getD().setActive(false);
            }
            supplier.addSupplierLocation("BUSINESS", location);
            supplier.setPreferredLocationType("BUSINESS");
            address.setBelongsTo(supplier);
            if (isPrimaryShipToAddress) {
                supplier.setAddress(address);
            }
        }
    }


    private String populateBrands(final CustomerTypeDTO dealerDTO) {
        BrandInfoTypeDTO.BrandCode.Enum brandCodes[]=dealerDTO.getBrandInfo().getBrandCodeArray();
        if (brandCodes != null && brandCodes.length != 0) {
            return brandCodes[0].toString();
        }
        return null;
    }
    

    private void createOrUpdateAddrBookMappingForPrimaryAddr(
            ServiceProvider serviceProvider, OrganizationAddress primaryAddress) {
        AddressBook addressBook = addressBookService.getAddressBookByOrganizationAndType(serviceProvider,
                AddressBookType.SELF);
        if (addressBook == null) {
            addressBook = new AddressBook();
            addressBook.setBelongsTo(serviceProvider);
            addressBook.setType(AddressBookType.SELF);
            List<AddressBookAddressMapping> mappings = new ArrayList<AddressBookAddressMapping>();
            AddressBookAddressMapping addressBookAddressMapping = null;
            if (primaryAddress != null) {
                addressBookAddressMapping = createAddressBookAddressMappingForServiceProvider(
                        addressBook, true, primaryAddress);
                mappings.add(addressBookAddressMapping);
            }
            addressBook.setAddressBookAddressMapping(mappings);
            addressBookService.createAddressBook(addressBook);
        } else {
            /*
             * Reset the primary field if the primary address has changed 3.Make
             * the latest Organization Address as Primary site and set this
             * field to false for the old primary site.--DONE 4.Carefully update
             * the existing address_book_mapping and create a new mapping for
             * the new Organization address --DONE
             */

            AddressBookAddressMapping addressBookAddressMapping = null;
            AddressBookAddressMapping existingPrimaryABAddressMapping = null;

            if (primaryAddress != null) {
                addressBookAddressMapping = addressBookService.getAddressBookAddressMappingByOrganizationAndAddress(
                        primaryAddress, serviceProvider);
                if (addressBookAddressMapping == null) {
                    existingPrimaryABAddressMapping = addressBookService.getAddressBookAddressMappingByOrganizationAndAddress(
                            orgService.getPrimaryOrganizationAddressForOrganization(serviceProvider),
                            serviceProvider);
                    if (existingPrimaryABAddressMapping != null) {
                        existingPrimaryABAddressMapping.setPrimary(Boolean.FALSE);
                        addressBookService.updateAddressBookAddressMapping(existingPrimaryABAddressMapping);
                    }
                    addressBookAddressMapping = createAddressBookAddressMappingForServiceProvider(addressBook, true, primaryAddress);
                    addressBookService.createAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
                } else if (Boolean.FALSE.equals(addressBookAddressMapping.getPrimary())) {
                    addressBookAddressMapping.setPrimary(Boolean.TRUE);
                    addressBookService.updateAddressBookAddressMappingWithOutActiveFilter(addressBookAddressMapping);
                }
            }
        }
    }

    private void addBusinessUnits(final CustomerTypeDTO customerTypeDTO, final Organization organization) {
        String[] businessUnits = null;
        if (customerTypeDTO.getBusinessUnits() != null) {
            businessUnits = customerTypeDTO.getBusinessUnits().getBUNameArray();
        }
        List<String> businessUnitList = null;
        final TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
        if (businessUnits != null && businessUnits.length > 0) {
            businessUnitList = Arrays.asList(businessUnits);
        }
        if (!CollectionUtils.isEmpty(businessUnitList)) {
            for (String buName : businessUnitList) {
                BusinessUnit bu = null;
                if (StringUtils.hasText(buName)) {
                    bu = businessUnitService.findBusinessUnit(getBuName(buName.trim()));
                }
                businessUnitMapping.add(bu);
            }
            organization.setBusinessUnits(businessUnitMapping);
        }
    }
    private String getBuName(String divivsionCode) {
		if (divivsionCode.equalsIgnoreCase(IntegrationConstants.US)) {
			return IntegrationConstants.NMHG_US;
		} else if (divivsionCode.equalsIgnoreCase(IntegrationConstants.EMEA)) {
			return IntegrationConstants.NMHG_EMEA;
		}
		return null;
	}

    public void setBusinessUnitService(BusinessUnitService businessUnitService) {
        this.businessUnitService = businessUnitService;
    }
    
    void setPrimaryValue(AddressBookAddressMapping addressBookAddressMapping, Boolean isPrimary, ServiceProvider serviceProvider, AddressBookType type, AddressBook addressBook){
    	AddressBookAddressMapping abam = null;
    	if(isPrimary){
        	Address address = orgService.getPrimaryOrganizationAddressForOrganization(serviceProvider);
        	if(address!=null){
        		if(addressBook.getId()!=null){
        			abam = addressBookService.getAddressBookAddressMappingByOrganizationAndAddressBookAndBookType(address,addressBook, serviceProvider, type);
        		}
        		if(abam!=null){
	    			abam.setPrimary(Boolean.FALSE);
	    			addressBookService.updateAddressBookAddressMapping(abam);
        		}
    			addressBookAddressMapping.setPrimary(Boolean.TRUE);
        	}
        	else
        		addressBookAddressMapping.setPrimary(Boolean.TRUE);
        }else{
        	addressBookAddressMapping.setPrimary(isPrimary);
        }
    }
    
   private String getStatus(String status){
    	if(status!=null){
	    	if(status.equals("I")){
	    		return "INACTIVE";
	    	}else if(status.equals("A")){
	    		return "ACTIVE";
	    	}
    	}
    	return null;
    }
    
    private String getStatus(String status,Address address){
    	if(status!=null){
	    	if(status.equals("I")){
	    		address.getD().setActive(Boolean.FALSE);
	    		return "INACTIVE";
	    	}else if(status.equals("A")){
	    		address.getD().setActive(Boolean.TRUE);
	    		return "ACTIVE";
	    	}
    	}
    	return null;
    }

	public String getEmeaMasterSupplier() {
		return emeaMasterSupplier;
	}

	public void setEmeaMasterSupplier(String emeaMasterSupplier) {
		this.emeaMasterSupplier = emeaMasterSupplier;
	}

	public String getAmerMasterSupplier() {
		return amerMasterSupplier;
	}

	public void setAmerMasterSupplier(String amerMasterSupplier) {
		this.amerMasterSupplier = amerMasterSupplier;
	}
    
}
