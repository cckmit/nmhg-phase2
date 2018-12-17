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
package tavant.twms.web.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.ecs.xhtml.map;
import org.springframework.util.StringUtils;


import tavant.twms.domain.claim.MatchReadInfo;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBook;
import tavant.twms.domain.orgmodel.AddressBookAddressMapping;
import tavant.twms.domain.orgmodel.AddressBookService;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.CountyCodeMapping;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.OrganizationRepository;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class ManageCustomer extends I18nActionSupport implements Preparable{

	protected CustomerService customerService;

	protected Customer customer;

	private final Map<String, String> addressTypesIndividual = new HashMap<String, String>();

	private final Map<String, String> addressTypesCompany = new HashMap<String, String>();

	private final Map<String, String> customerTypes = new HashMap<String, String>();

	private boolean hideSelect;

	protected boolean pushInfoPressed;

	private boolean pushCustomerDetails;

	protected Long selectedAddressId;

	private String password;

	private boolean transfer;

	private boolean company;

	protected Warranty warranty;
	
	protected ServiceProvider serviceProvider;
	
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public OrganizationAddress getOrgAddresses() {
		return orgAddresses;
	}

	public void setOrgAddresses(OrganizationAddress orgAddresses) {
		this.orgAddresses = orgAddresses;
	}

	protected OrganizationAddress orgAddresses;

	private final Map<String, String> addressBookTypesForDealer = new HashMap<String, String>();
	private final Map<String, String> addressBookTypesForCompany = new HashMap<String, String>();
	protected List<AddressBookAddressMapping> addressBookAddressMappings = new ArrayList<AddressBookAddressMapping>();
	protected AddressBookService addressBookService;
	protected Long customerId;
	protected boolean matchRead;
	private MatchReadInfo matchReadInfo;
	protected String customerStartsWith;
	protected String customerType = "Company";
	protected List<Customer> matchingCustomerList = new ArrayList<Customer>();
	protected List<ServiceProvider> matchingNAList = new ArrayList<ServiceProvider>();
	protected Organization dealerOrganization;
	protected MSAService msaService;
	protected final SortedHashMap <String, String> countryList = new SortedHashMap<String, String>();
	private String countryCode;
	private String stateCode;
	private String cityCode;
	private String zipsCode;
	protected List<String> states = new ArrayList<String>();
	protected List<String> cities = new ArrayList<String>();
	protected List<String> zips = new ArrayList<String>();
	protected List<String> counties = new ArrayList<String>();
	protected List<String> countriesFromMSA = new ArrayList<String>();
    private Integer pageNo=new Integer(0);
    protected ListCriteria listCriteria;
    protected List<Integer> pageNoList = new ArrayList<Integer>();
    private String dealerName;
    private Long dealerId;
    protected DealershipRepository dealershipRepository;
    protected String addressBookType;
    protected String addressBookTypeForOperator;
    protected OrganizationRepository organizationRepository;
    private WarrantyService warrantyService;
    private Long warrantyId;
	String selectedBusinessUnit;
	private String customerTypeSelected;


	public String getCustomerTypeSelected() {
		return customerTypeSelected;
	}

	public void setCustomerTypeSelected(String customerTypeSelected) {
		this.customerTypeSelected = customerTypeSelected;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public boolean showCustomerCompanyWebSite() {
		return getConfigParamService().getBooleanValue(
				ConfigName.SHOW_CUSTOMER_COMPANY_WEBSITEON_DR.getName());
	}


	public ManageCustomer() {
		super();
		this.addressTypesIndividual
				.put("HOME", getText("label.manageCustomer.home"));
		this.addressTypesIndividual
				.put("WORK", getText("label.manageCustomer.work"));
		this.addressTypesCompany.put("BILLING",
				getText("label.manageCustomer.billing"));
		this.addressTypesCompany.put("SHIPPING",
				getText("label.manageCustomer.shipping"));
		this.customerTypes.put(Boolean.TRUE.toString(),
				getText("label.manageCustomer.individual"));
		this.customerTypes.put(Boolean.FALSE.toString(),
				getText("label.manageCustomer.company"));
		if(customerTypeSelected!=null){
		this.addressBookTypesForCompany
				.put("NATIONALACCOUNT",getText("label.manageCustomer.addressBookType.nationalAccount"));
		this.addressBookTypesForCompany
				.put("GOVERNMENTACCOUNT",getText("label.manageCustomer.addressBookType.governmentAccount"));
		}
		this.addressBookTypesForDealer.put("ENDCUSTOMER",getText("label.manageCustomer.addressBookType.endCustomer"));
	}

	public String create() {
		prepareCustomer();
		this.customerId = (Long) this.customerService
				.createCustomerAndReturnKey(this.customer);
		addActionMessage("message.manageCustomer.created");

		if (this.selectedAddressId == null) {
			this.selectedAddressId = this.customer.getAddresses().get(
					this.customer.getAddresses().size() - 1).getId();
		}

		if (this.pushInfoPressed) {
			setPushCustomerDetails(true);
			if (this.matchRead) {
				prepareMatchReadInfo();
			}
		}
		prepareAddressBookAddressMapping();
		//Customer tempCustomer= this.customerService.findCustomerById(customerId);
		//customer.setCustomerId(tempCustomer.getCustomerId());
		customer.setCustomerId("TAV"+customer.getId());		
		return SUCCESS;
	}
	
	public String updateNationalAccount(){
		orgAddresses.setSiteNumber("SN"+serviceProvider.getId()+serviceProvider.getOrgAddresses().size()+1);
		setLocation(orgAddresses);
		this.serviceProvider.getOrgAddresses().add(orgAddresses);
		orgService.updateDealership(this.serviceProvider);
		addActionMessage("message.managecustomer.updated");
		return SUCCESS;
	}
	
	private void setLocation(OrganizationAddress address){
	 StringBuffer location = new StringBuffer();  
     if (address.getAddressLine1() != null) {
         location.append(address.getAddressLine1());
         location.append("-");
     }
     if (address.getAddressLine2() != null) {
         location.append(address.getAddressLine2());
         location.append("-");
     }
     if (address.getCity() != null) {
         location.append(address.getCity());
         location.append("-");
     }
     if (address.getState() != null) {
         location.append(address.getState());
         location.append("-");
     }
     if (address.getZipCode() != null) {
         location.append(address.getZipCode());
         location.append("-");
     }
     if (address.getCountry() != null) {
         location.append(address.getCountry());
         location.append("-");
     }
     address.setLocation(location.toString());
	}

	public String update() {		
		prepareCustomer();
		this.customerId = this.customer.getId();		
		this.customerService.updateCustomer(this.customer);
		addActionMessage("message.managecustomer.updated");

		if (this.selectedAddressId == null) {
			this.selectedAddressId = this.customer.getAddresses().get(
					this.customer.getAddresses().size() - 1).getId();
		}

		if (this.pushInfoPressed) {
			setPushCustomerDetails(true);
			if (this.matchRead) {
				prepareMatchReadInfo();
			}
		}
		prepareAddressBookAddressMapping();
		return SUCCESS;
	}

	protected void prepareCustomer() {
		
		int primaryAddressIndex = 0;
		
		for (AddressBookAddressMapping addressBookAddressMapping : this.addressBookAddressMappings) {
			if (addressBookAddressMapping.getPrimary() != null
					&& addressBookAddressMapping.getPrimary().equals(
							Boolean.TRUE)) {
				this.customer.setAddress(this.customer.getAddresses().get(
						primaryAddressIndex));
				break;
			}
			primaryAddressIndex++;
		}
		if (this.customer.getId() == null) {
			this.customer.setLocale(ActionContext.getContext().getLocale());
		}
		
		if(this.customer != null && !StringUtils.hasText(this.customer.getName()))
		{
			if(StringUtils.hasText(this.customer.getCompanyName()))
			{
				this.customer.setName(this.customer.getCompanyName());
			}
			else if(StringUtils.hasText(this.customer.getCorporateName()))
			{
				this.customer.setName(this.customer.getCorporateName());
			}			
		}
		prepareAddress();
	}
	public boolean isPageReadOnly() {
		return false;
	}
	
	public boolean isPageReadOnlyAdditional() {
		boolean isReadOnlyDealer = false;
		Set<Role> roles = getLoggedInUser().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
				isReadOnlyDealer = true;
				break;
			}
		}
		return isReadOnlyDealer;
	}

	private void prepareAddress() {
		int addressIndex = 0;
		Iterator<Address> addressIterator = this.customer.getAddresses().iterator();
		while (addressIterator.hasNext()) {
			Address address = addressIterator.next();
			if (address.getId() == null) {
				address.setBelongsTo(this.customer);
			}
			if (!this.countriesFromMSA.contains(address.getCountry())) {
				address.setState(this.states.get(addressIndex));
				address.setCity(this.cities.get(addressIndex));
				address.setZipCode(this.zips.get(addressIndex));
				if(isDisplayCountyOnCustomerPage()){
					address.setCounty(this.counties.get(addressIndex));
				}
				else{
					address.setCounty(null);
				}
			} else {
				this.states.set(addressIndex, null);
				this.cities.set(addressIndex, null);
				this.zips.set(addressIndex, null);
			}
			addressIndex++;
		}
	}

	protected void prepareAddressBookAddressMapping() {
		for (AddressBookAddressMapping addressBookAddressMapping : this.addressBookAddressMappings) {
			if (addressBookAddressMapping.getId() == null) {
				Address address = null;
				Organization organization ;
				if(getDealerId() != null && getDealerId() > 0){
					organization=dealershipRepository.findByDealerId(getDealerId());
				}else if (StringUtils.hasText(getDealerName())){
                    organization=dealershipRepository.findByDealerName(getDealerName());
                } else {
                    organization = getOrganization();
				}
				address = getNewAddress();
				AddressBookType addressBookType = addressBookAddressMapping
						.getAddressBook().getType();
                if(isNAorGAorInterCompanyorDirectCustomer(addressBookType.getType())){
                    organization=getOrganization();
                }
                AddressBook addressBook = this.addressBookService
						.getAddressBookByOrganizationAndType(organization,
								addressBookType);
				if (addressBook == null) {
					createAddressBook(addressBookType);
					addressBook = this.addressBookService
							.getAddressBookByOrganizationAndType(organization,
									addressBookType);
				}
				addressBookAddressMapping.setAddressBook(addressBook);
				addressBookAddressMapping.setAddress(address);
				this.addressBookService
						.createAddressBookAddressMapping(addressBookAddressMapping);
			} else {
				this.addressBookService
						.updateAddressBookAddressMapping(addressBookAddressMapping);
			}
		}

	}

	public String newCustomer() {
		this.customer = new Customer();
		AddressBookAddressMapping addressBookAddressMapping = new AddressBookAddressMapping();
		this.addressBookAddressMappings.removeAll(Collections.singleton(null));
		if(this.addressBookAddressMappings.isEmpty() || isLoggedInUserADealer() || (!this.addressBookAddressMappings.isEmpty() && this.addressBookAddressMappings.get(0).getAddressBook()!=null && this.addressBookAddressMappings.get(0).getAddressBook().getType() == null)){
		this.addressBookAddressMappings = new ArrayList<AddressBookAddressMapping>(0);
		}else{
		this.addressBookAddressMappings.add(addressBookAddressMapping);
		}
        Organization organizationForSearch = null;
		if(warrantyId!=null){
            warranty = warrantyService.findById(Long.valueOf(warrantyId));
            if (this.warranty != null){
                organizationForSearch = new HibernateCast<ServiceProvider>().cast(this.warranty.getForTransaction().getOwnerShip());
                setDealerName(organizationForSearch.getName());
            }
		} else if (this.dealerId != null && this.dealerId > 0) {
            organizationForSearch = dealershipRepository.findByDealerId(this.dealerId);
            if (organizationForSearch != null) {
                setDealerName(organizationForSearch.getName());
            }
        }
		return SUCCESS;
		
	}

	public String showCustomerDetails() {
		Long customerId = getLoggedInUser().getId();
		this.customer = this.customerService.findCustomerById(customerId); 
		return SUCCESS;
	}

	public Organization getDealerOrganization() {
		return this.dealerOrganization;
	}

	public void setDealerOrganization(Organization dealerOrganization) {
		this.dealerOrganization = dealerOrganization;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Map<String, String> getAddressTypesCompany() {
		return this.addressTypesCompany;
	}

	public Map<String, String> getAddressTypesIndividual() {
		return this.addressTypesIndividual;
	}

	public boolean isPushCustomerDetails() {
		return this.pushCustomerDetails;
	}

	public void setPushCustomerDetails(boolean pushCustomerDetails) {
		this.pushCustomerDetails = pushCustomerDetails;
	}

	public boolean isPushInfoPressed() {
		return this.pushInfoPressed;
	}

	public void setPushInfoPressed(boolean pushInfoPressed) {
		this.pushInfoPressed = pushInfoPressed;
	}

	public boolean isHideSelect() {
		return this.hideSelect;
	}

	public void setHideSelect(boolean hideSelect) {
		this.hideSelect = hideSelect;
	}

	public Long getSelectedAddressId() {
		return this.selectedAddressId;
	}

	public void setSelectedAddressId(Long selectedAddressId) {
		this.selectedAddressId = selectedAddressId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isTransfer() {
		return this.transfer;
	}

	public void setTransfer(boolean transfer) {
		this.transfer = transfer;
	}

	public boolean isCompany() {
		return this.company;
	}

	public void setCompany(boolean company) {
		this.company = company;
	}

	public Map<String, String> getCustomerTypes() {
		return this.customerTypes;
	}

	public Map<String, String> getAddressBookTypesForDealer() {
		return this.addressBookTypesForDealer;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getAddressBookTypesForCompany() {
	
		if(StringUtils.hasText(getDealerName()) || !isLoggedInUserADealer()){
			this.addressBookTypesForCompany.put("DEMO",getText("label.manageCustomer.addressBookType.demo"));
			this.addressBookTypesForCompany.put("ENDCUSTOMER",getText("label.manageCustomer.addressBookType.endCustomer"));
			this.addressBookTypesForCompany.put("DIRECTCUSTOMER",getText("label.manageCustomer.addressBookType.directCustomer"));
			this.addressBookTypesForCompany.put("DEALERRENTAL",getText("label.manageCustomer.addressBookType.dealerRental"));
			this.addressBookTypesForCompany.put("GOVERNMENTACCOUNT",getText("label.manageCustomer.addressBookType.governmentAccount"));
			this.addressBookTypesForCompany.put("NATIONALACCOUNT",getText("label.manageCustomer.addressBookType.nationalAccount"));
		}
	  
	 
        return this.addressBookTypesForCompany;
	  
	}
	 
	@Override
	public void validate() {
		boolean validCompName = true;
		boolean validCorpName = true;
		if(this.customer != null)
		{
			if((StringUtils.hasText(this.customer.getCompanyName()) &&  (this.customer.getCompanyName().indexOf("'") > 0
					|| this.customer.getCompanyName().indexOf("\"") > 0))||(this.customer.getCompanyName().equals("")))
			{
				validCompName = false;
			}
			if(StringUtils.hasText(this.customer.getCorporateName()) && (this.customer.getCorporateName().indexOf("'") > 0
					|| this.customer.getCorporateName().indexOf("\"") > 0))
			{
				validCorpName = false;
			}

		}			
		
		if(addressBookType!=null && addressBookType.equals("NATIONALACCOUNT")){
			if(serviceProvider == null || serviceProvider.getName()==null){
				validCompName = false;
			}
			List<Address> addresses = new ArrayList<Address>();
			addresses.add((Address)orgAddresses);
			validateAddress(addresses);
		}
		else{
			int primaryAddressCount = 0;
			boolean validAddressBookType = true;
			for (AddressBookAddressMapping addressBookAddressMapping : this.addressBookAddressMappings) {
				if (addressBookAddressMapping.getPrimary() != null
						&& addressBookAddressMapping.getPrimary().equals(
								Boolean.TRUE)) {
					primaryAddressCount++;
				}
				if(addressBookAddressMapping.getAddressBook() == null ||
						addressBookAddressMapping.getAddressBook().getType() == null )
					validAddressBookType = false;
			}
			if (primaryAddressCount == 0) {
				addActionError("error.manageCustomer.noPrimaryAddress");
			} else if (primaryAddressCount > 1) {
				addActionError("error.manageCustomer.moreThanOnePrimaryAddress");
			}
			if(!validAddressBookType){
				addActionError("error.manageCustomer.noAddressBookType");
			}
		if(this.customer.getAddresses() !=null && this.customer.getAddresses().size() >0 ){
			validateAddress(this.customer.getAddresses());
		} else{
			addActionError("error.manageCustomer.addressMandatory");
		}
		}
		if(!validCompName)
		{
			addActionError("error.companyName.notWellFormed");
		}
		if(!validCorpName)
		{
			addActionError("error.corpName.notWellFormed");
		}

	}
	
	private void validateAddress(List<Address> addresses){
		int addressIndex = 0;
		boolean validStateList = true;
		boolean validCityList = true;
		boolean validZipList = true;
		boolean validCountyList = true;
		boolean validAddressCombination = true;
		boolean validPhone=true;
		boolean validEmail = true;
		boolean validContactName = true;
		boolean validCustomerTitle = true;


		for (Address address : addresses) {
			if (!this.countriesFromMSA.contains(address.getCountry())) {
				if (!this.states.isEmpty()&&(this.states.get(addressIndex) == null
						|| "".equals(this.states.get(addressIndex).trim()) || this.states.get(addressIndex).indexOf("'") > 0
						|| this.states.get(addressIndex).indexOf("\"") > 0)) {
					validStateList = false;
				}
				if (!this.cities.isEmpty()&&(this.cities.get(addressIndex) == null
						|| "".equals(this.cities.get(addressIndex).trim()) || this.cities.get(addressIndex).indexOf("'") > 0
						|| this.cities.get(addressIndex).indexOf("\"") > 0)) {
					validCityList = false;
				}
				if (!this.zips.isEmpty()&&(this.zips.get(addressIndex) == null 
						|| "".equals(this.zips.get(addressIndex).trim()) || (this.zips.get(addressIndex).indexOf("'") > 0
						|| this.zips.get(addressIndex).indexOf("\"") > 0))) {
					validZipList = false;
				}
			
				if(address.getPhone()== null || "".equals(address.getPhone().trim()))
				{
					validPhone=false;
				}
				if(address.getEmail() == null || "".equals(address.getEmail().trim())){
					validEmail = false;
				}
				if(address.getContactPersonName()== null || "".equals(address.getContactPersonName().trim())){
					validContactName = false;
				}
				
				if(address.getCustomerContactTitle() == null || "".equals(address.getCustomerContactTitle().trim())){
					validCustomerTitle = false;
				}
				
			} else {
				if (address.getState() == null
						|| "".equals(address.getState().trim())) {
					validStateList = false;
				}
				if (address.getCity() == null
						|| "".equals(address.getCity().trim())) {
					validCityList = false;
				}
				if (address.getZipCode() == null
						|| "".equals(address.getZipCode().trim())) {
					validZipList = false;
				}
				if (!validateAddressCombination(address)) {
					validAddressCombination = false;
				}
				if (address.getCounty() == null
						|| "".equals(address.getCounty().trim())) {
					validCountyList = false;
				}
				if(address.getPhone()== null || "".equals(address.getPhone().trim()))
				{
					validPhone=false;
				}
				if(address.getEmail() == null || "".equals(address.getEmail().trim())){
					validEmail = false;
				}
				if(address.getContactPersonName() == null || "".equals(address.getContactPersonName().trim())){
					validContactName = false;
				}
				if(address.getCustomerContactTitle() == null || "".equals(address.getCustomerContactTitle().trim())){
					validCustomerTitle = false;
				}
				
			}
			addressIndex++;
		}
		//Fixed for  NMHGSLMS-33(State should not be mandatory)
		
		if (isBuConfigAMER() && !validStateList) {
			addActionError("error.manageCustomer.requiredState");
		}
		if (!validCityList) {
			addActionError("error.manageCustomer.requiredCity");
		}
		if (!validZipList) {
			addActionError("error.manageCustomer.requiredZipCode");
		}
		if (!validPhone) {
			addActionError("error.manageCustomer.requiredPhone");
		}
		if (!validCountyList && isDisplayCountyOnCustomerPage()){
			addActionError("error.manageCustomer.requiredCounty");			
		}
		if (validStateList && validCityList && validZipList 
				&& !validAddressCombination) {
			addActionError("error.manageCustomer.invalidAddressCombination");
		}
		if(checkForValidatableCountry(countryCode) == true){
			addActionError("error.manageCustomer.requiredCountry");
		}
		
		if(isBuConfigAMER() && !validEmail){
			addActionError("error.manageCustomer.requiredEmail");
		}
		if(!validContactName){
			addActionError("error.manageCustomer.requiredContactPersonName");
		}
		if(!validCustomerTitle){
			addActionError("error.manageCustomer.requiredCustomerContactTitle");
		}
	}

	private void createAddressBook(AddressBookType addressBookType) {
		AddressBook addressBook = new AddressBook();
		Organization organization ;
		if(StringUtils.hasText(getDealerName())){
			organization=dealershipRepository.findByDealerName(getDealerName());
		}else{
			organization = getOrganization();
		}
        if(isNAorGAorInterCompanyorDirectCustomer(addressBookType.getType()) ){
            organization = getOrganization();
        }
        addressBook.setBelongsTo(organization);
		addressBook.setType(addressBookType);
		this.addressBookService.createAddressBook(addressBook);
	}

	public String getAddressBookTypeForDealerLabel(String addressBookType) {
		return this.addressBookTypesForDealer.get(addressBookType.toUpperCase());
	}

	public String getAddressBookTypeForCompanyLabel(String addressBookType) {
		return this.addressBookTypesForCompany.get(addressBookType.toUpperCase());
	}

	public String getAddressTypeIndividualLabel(String addressType) {
		return this.addressTypesIndividual.get(addressType.toUpperCase());
	}

	public String getAddressTypeCompanyLabel(String addressType) {
		return this.addressTypesCompany.get(addressType.toUpperCase());
	}

	public void setAddressBookService(AddressBookService addressBookService) {
		this.addressBookService = addressBookService;
	}

	public List<AddressBookAddressMapping> getAddressBookAddressMappings() {
		return this.addressBookAddressMappings;
	}

	public void setAddressBookAddressMappings(
			List<AddressBookAddressMapping> addressBookAddressMappings) {
		this.addressBookAddressMappings = addressBookAddressMappings;
	}

	private Address getNewAddress() {
		Customer customerFromDB = this.customerService.findCustomerById(this.customerId);
		List<AddressBookAddressMapping> addressBookAddressMappings = this.addressBookService
				.getAddressBookAddressMappingByOrganizationAndListOfAddresses(
						customerFromDB.getAddresses(), getOrganization());
		List<Address> addressesFromDB = customerFromDB.getAddresses();

		for (Address address : addressesFromDB) {
			boolean isNewAddress = true;
			for (AddressBookAddressMapping addressBookAddressMapping : addressBookAddressMappings) {
				if (address.getId().equals(
						addressBookAddressMapping.getAddress().getId())) {
					isNewAddress = false;
				}
			}
			if (isNewAddress) {
				return address;
			}
		}
		return null;
	}

	public String prepareCustomerForEdit() throws Exception {
		Organization organization;
		if(StringUtils.hasText(getDealerName())){
			organization=dealershipRepository.findByDealerName(getDealerName());
		}else{
			organization = getOrganization();
		}
		int addressIndex = 0;
		for (Address address : this.customer.getAddresses()) {
            boolean isAuthorized=false;
            AddressBookAddressMapping addressBookAddressMapping = this.addressBookService
					.getAddressBookAddressMappingByOrganizationAndAddress(
							address, organization);
            if(addressBookAddressMapping==null){
            	addressBookAddressMapping = this.addressBookService.
            			getAddressBookAddressMappingByOrganizationAndAddress(address, getOrganization());
            	if(addressBookAddressMapping!=null){
                      isAuthorized=true;
            	}
            	else{
            		if(!StringUtils.hasText(getDealerName())&& this.customer!=null  ){
            			addressBookAddressMapping = this.addressBookService.getAddressBookAddressMappingByAddress(address);
            		}
            		if(addressBookAddressMapping!=null){
            			isAuthorized=true;
            		}
            	}
            }else{
                isAuthorized=true;
            }
            if(!isAuthorized){
                addressBookAddressMapping=addressBookService.getAddressBookAddressMappingByAddress(address);
                addActionError("error.edit.customer");
            }
             this.addressBookAddressMappings.add(addressBookAddressMapping);
                if (!this.countriesFromMSA.contains(address.getCountry())) {
                    this.states.add(addressIndex, address.getState());
                    this.cities.add(addressIndex, address.getCity());
                    this.zips.add(addressIndex, address.getZipCode());
                    if(isDisplayCountyOnCustomerPage()){
                    	this.counties.add(addressIndex, address.getCounty());
                    }
                } else {
                    this.states.add(addressIndex, null);
                    this.cities.add(addressIndex, null);
                    this.zips.add(addressIndex, null);
                    this.counties.add(addressIndex,null);
                }
                addressIndex++;

        }
        if(hasActionErrors() || hasFieldErrors()){
            return INPUT;
        }
		return SUCCESS;
	}

	public boolean isMatchRead() {
		return this.matchRead;
	}

	public void setMatchRead(boolean matchRead) {
		this.matchRead = matchRead;
	}

	public MatchReadInfo getMatchReadInfo() {
		return this.matchReadInfo;
	}

	public void setMatchReadInfo(MatchReadInfo matchReadInfo) {
		this.matchReadInfo = matchReadInfo;
	}

	public Organization getOrganization() {
		return getLoggedInUser().getBelongsToOrganization();
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	protected void prepareMatchReadInfo() {
		List<Address> addresses = this.customer.getAddresses();
		this.matchReadInfo = new MatchReadInfo();
		for (Address address : addresses) {
			if (address.getId() != null
					&& address.getId().equals(this.selectedAddressId)) {
				if (this.customer.isIndividual()) {
					this.matchReadInfo.setOwnerName(this.customer.getName());
				} else {
					this.matchReadInfo.setOwnerName(this.customer.getCompanyName());
				}
				this.matchReadInfo.setOwnerCity(address.getCity());
				this.matchReadInfo.setOwnerState(address.getState());
				this.matchReadInfo.setOwnerZipcode(address.getZipCode());
				this.matchReadInfo.setOwnerCountry(address.getCountry());
				break;
			}
		}
	}

	public List<Customer> getMatchingCustomerList() {
		return this.matchingCustomerList;
	}

	public void setMatchingCustomerList(List<Customer> matchingCustomerList) {
		this.matchingCustomerList = matchingCustomerList;
	}

	public String getCustomerType() {
		return this.customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getCustomerStartsWith() {
		return this.customerStartsWith;
	}

	public void setCustomerStartsWith(String customerStartsWith) {
		this.customerStartsWith = customerStartsWith;
	}

	public void prepare() {
		List<Country> countries = this.msaService.getCountryList();
		for (Country country : countries) {
			this.countryList.put(country.getCode(), country.getName());
		}
		this.countriesFromMSA = this.msaService.getCountriesFromMSA();

	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateCode() {
		return this.stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCityCode() {
		return this.cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public SortedHashMap<String, String> getCountryList() {
		return this.countryList;
	}

	public List<String> getStates() {
		return this.states;
	}

	public void setStates(List<String> states) {
		this.states = states;
	}

	public List<String> getCities() {
		return this.cities;
	}

	public void setCities(List<String> cities) {
		this.cities = cities;
	}

	public List<String> getZips() {
		return this.zips;
	}

	public void setZips(List<String> zips) {
		this.zips = zips;
	}

	public String getStatesByCountry() throws IOException {
		List<String> statesFromDB = this.msaService.getStatesByCountry(this.countryCode);
		return generateAndWriteComboboxJson(statesFromDB);
	}

	public String getCitiesByCountryAndState() throws IOException {
		List<String> citiesFromDB = this.msaService.getCitiesByCountryAndState(
				this.countryCode, this.stateCode);
		return generateAndWriteComboboxJson(citiesFromDB);
	}

	public String getZipsByCountryStateAndCity() throws IOException {
		List<String> zipsFromDB = this.msaService.getZipsByCountryStateAndCity(
				this.countryCode, this.stateCode, this.cityCode);
		return generateAndWriteComboboxJson(zipsFromDB);
	}
	
	public String getCountiesByCountryStateAndZip() throws IOException {
		List<CountyCodeMapping> countiesFromDB = this.msaService.getCountiesByCountryStateAndZip(this.countryCode, this.stateCode, this.zipsCode);
		return generateAndWriteComboboxJson(countiesFromDB,"codeWithName","codeWithName");
	}

	public boolean checkForValidatableCountry(String country) {
		if (this.countriesFromMSA.contains(country)) {
			return true;
		}
		return false;
	}

	private boolean validateAddressCombination(Address address) {
		return this.msaService.isValidAddressCombination(address.getCountry(),
				address.getState(), address.getCity(), address.getZipCode());
	}

    public Warranty getWarranty() {
        return this.warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public ListCriteria getListCriteria() {
		return listCriteria;
	}

	public void setListCriteria() {
		ListCriteria criteria = new ListCriteria();
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageNumber(this.pageNo.intValue());
		pageSpecification.setPageSize(10);
		criteria.setPageSpecification(pageSpecification);
		this.listCriteria=criteria;
	}

	public List<Integer> getPageNoList() {
		return pageNoList;
	}

	public void setPageNoList(List<Integer> pageNoList) {
		this.pageNoList = pageNoList;
	}

	public String searchCustomers() {
	
		
		this.addressBookType = this.addressBookType==null?this.addressBookTypeForOperator:this.addressBookType;
		setListCriteria();
	    PageResult<Customer> pageResult =null;
	    PageResult<ServiceProvider> pageResultForNA =null;
		Organization organizationForSearch = null;

		
		if (this.dealerOrganization != null) {
			organizationForSearch = this.dealerOrganization;
		}		
		else if (this.warranty != null){
		    organizationForSearch = new HibernateCast<ServiceProvider>().cast(this.warranty.getForTransaction().getOwnerShip());
		}
		else {
			if(isLoggedInUserADealer() && !isLoggedInUserAParentDealer()){
				organizationForSearch = getOrganization();
			}else{
                if (StringUtils.hasText(getDealerName())) {
                    organizationForSearch = dealershipRepository.findByDealerName(getDealerName());
                }
                if (organizationForSearch == null && getDealerId() != null && getDealerId() > 0) {
                    organizationForSearch = dealershipRepository.findByDealerId(getDealerId());
                } else if (organizationForSearch == null) {
                    organizationForSearch = getOrganization();
                }
			}
		}

		if (this.customerType.equals("Individual")) {
			pageResult = this.customerService
					.findCustomersForNameLikeFromAddressBook(
							this.customerStartsWith, organizationForSearch, listCriteria);
		} else if (this.customerType.equals("Company")) {
			if (this.addressBookType != null
					&& isNAorGAorInterCompanyorDirectCustomer(this.addressBookType)) {

				Organization oemOrganization = organizationRepository
						.findByName("OEM");

				pageResult = this.customerService
				.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
						this.customerStartsWith, oemOrganization,
						getAddressBookType(addressBookType), listCriteria);
			}  else if(this.addressBookType != null && isNACustomer(this.addressBookType)){
				pageResultForNA = orgService
				.findAllNationalAccounts(
						this.customerStartsWith, listCriteria);
	       }else if(this.addressBookType != null && !isLoggedInUserAnAdmin()){
	    	   if(this.addressBookType.equals(AddressBookType.DEALERRENTAL.getType()) || this.addressBookType.equals(AddressBookType.DEMO.getType())){
	    		   pageResult = this.customerService
	    					.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
	    							this.customerStartsWith, organizationForSearch,
	    							getAddressBookType(AddressBookType.ENDCUSTOMER.getType()), listCriteria); //the customers for dealer rental are the end customers while filing DR NMHGSLMS-162,SLMSPROD-1174, demo should work in the same way as that of End customer
	    	   }
	    	   else{
	    		   pageResult = this.customerService
	    					.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
	    							this.customerStartsWith, organizationForSearch,
	    							getAddressBookType(addressBookType), listCriteria);
	    	   }
				
	      }
	       else if(isLoggedInUserAnAdmin()){
	    	   if(this.addressBookType != null && this.addressBookType.equals(AddressBookType.DEALERRENTAL.getType()) || this.addressBookType.equals(AddressBookType.DEMO.getType())){
	    		   pageResult = this.customerService
	    					.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
	    							this.customerStartsWith, organizationForSearch,
	    							getAddressBookType(AddressBookType.ENDCUSTOMER.getType()), listCriteria);
	    	   }
	    	   else{
	    		   if(this.addressBookType != null){
	    		   pageResult = this.customerService
	   	   				.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
	   							this.customerStartsWith, organizationForSearch,
	   							getAddressBookType(addressBookType), listCriteria);
	    		   }else{
	    			   pageResult = this.customerService
	   	   	   				.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
	   	   							this.customerStartsWith, organizationForSearch,
	   	   							getAddressBookType(AddressBookType.ENDCUSTOMER.getType()), listCriteria);
	    		   }// List of End Customers for the dealer will be returned in case the address book type is null
	    	   }
	    	   
	       }
			else if(this.getSelectedBusinessUnit()==null) {
				pageResult = this.customerService
				.findCustomersForCompanyNameLikeFromAddressBook(
						this.customerStartsWith, organizationForSearch,
						listCriteria);
			}
			else {
				pageResult = this.customerService
				.findCustomersForCompanyNameLikeFromAddressBook(
						this.customerStartsWith, organizationForSearch,this.getSelectedBusinessUnit(),
						listCriteria);
			}
		}
		if(this.addressBookType != null && isNACustomer(this.addressBookType)){
			if(pageResultForNA != null){
			this.setMatchingNAList(pageResultForNA.getResult());
			for (int i=0;i<pageResultForNA.getNumberOfPagesAvailable();i++){
	        	this.pageNoList.add(new Integer(i+1));
			}
			}
		}
		else{
		this.setMatchingCustomerList(pageResult.getResult());
		for (int i=0;i<pageResult.getNumberOfPagesAvailable();i++){
	        	this.pageNoList.add(new Integer(i+1));
	    }
		}
		return SUCCESS;
	}
	
	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public void setDealershipRepository(DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

    protected boolean isNAorGAorInterCompanyorDirectCustomer(String addressBookType){
        if(AddressBookType.GOVERNMENTACCOUNT.getType().equalsIgnoreCase(addressBookType)
                || AddressBookType.INTERCOMPANY.getType().equalsIgnoreCase(addressBookType)
                || AddressBookType.DIRECTCUSTOMER.getType().equalsIgnoreCase(addressBookType)
                ){
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean isNACustomer(String addressBookType){
    if(AddressBookType.NATIONALACCOUNT.getType().equalsIgnoreCase(addressBookType))
    	return true;
    else 
    	return false;
    	}

    protected AddressBookType getAddressBookType(String addressBookType){
    	if(AddressBookType.ENDCUSTOMER.getType().equalsIgnoreCase(addressBookType)){
    		return AddressBookType.ENDCUSTOMER;
    	}else if(AddressBookType.NATIONALACCOUNT.getType().equalsIgnoreCase(addressBookType)){
        	return AddressBookType.NATIONALACCOUNT;
        }else if(AddressBookType.GOVERNMENTACCOUNT.getType().equalsIgnoreCase(addressBookType)){
        	return AddressBookType.GOVERNMENTACCOUNT;
        }else if(AddressBookType.INTERCOMPANY.getType().equalsIgnoreCase(addressBookType)){
        	return AddressBookType.INTERCOMPANY;
        }else if(AddressBookType.DIRECTCUSTOMER.getType().equalsIgnoreCase(addressBookType)){
            return AddressBookType.DIRECTCUSTOMER;
        }else if(AddressBookType.DEALER.getType().equalsIgnoreCase(addressBookType)){
            return AddressBookType.DEALER; //note: SELF type is returned
        }else if(AddressBookType.DEALERRENTAL.getType().equalsIgnoreCase(addressBookType)){
        	return AddressBookType.DEALERRENTAL; //note: SELF type is returned
        }else if(AddressBookType.DEALERUSER.getType().equalsIgnoreCase(addressBookType)){
        	return AddressBookType.SELF; //note: SELF type is returned
        }else if(AddressBookType.SELF.getType().equalsIgnoreCase(addressBookType)){
        	return AddressBookType.SELF;
       }else if (AddressBookType.FEDERAL_GOVERNMENT.getType().equalsIgnoreCase(addressBookType)){
    	   return AddressBookType.FEDERAL_GOVERNMENT;
    	}else if (AddressBookType.STATE_GOVERNMENT.getType().equalsIgnoreCase(addressBookType)){
     	   return AddressBookType.STATE_GOVERNMENT;
     	}else if (AddressBookType.COUNTY_GOVERNMENT.getType().equalsIgnoreCase(addressBookType)){
     	   return AddressBookType.COUNTY_GOVERNMENT;
     	}else if (AddressBookType.CTV_GOVERNMENT.getType().equalsIgnoreCase(addressBookType)){
     	   return AddressBookType.CTV_GOVERNMENT;
     	}else if (AddressBookType.HOMEOWNERS.getType().equalsIgnoreCase(addressBookType)){
     	   return AddressBookType.HOMEOWNERS;
     	}else if (AddressBookType.BUSINESS.getType().equalsIgnoreCase(addressBookType)){
     	   return AddressBookType.BUSINESS;
     	}else if (AddressBookType.REGIONAL_ACCOUNT.getType().equalsIgnoreCase(addressBookType)){
      	   return AddressBookType.REGIONAL_ACCOUNT;
      	}else {
        	return null;
        }

    }
    
	public boolean isDisplayCountyOnCustomerPage() {
		return getConfigParamService().getBooleanValue(
				ConfigName.DISPLAY_COUNTY_ON_CUSTOMER_PAGE.getName());
	}

	public String getAddressBookType() {
		return addressBookType;
	}

	public void setAddressBookType(String addressBookType) {
		this.addressBookType = addressBookType;
	}

	public OrganizationRepository getOrganizationRepository() {
		return organizationRepository;
	}

	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}
	
	public WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public Long getWarrantyId() {
		return warrantyId;
	}

	public void setWarrantyId(Long warrantyId) {
		this.warrantyId = warrantyId;
	}
    public Long getDealerId() {
        return dealerId;
    }

    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

	public String getAddressBookTypeForOperator() {
		return addressBookTypeForOperator;
	}

	public void setAddressBookTypeForOperator(String addressBookTypeForOperator) {
		this.addressBookTypeForOperator = addressBookTypeForOperator;
	}

	public List<ServiceProvider> getMatchingNAList() {
		return matchingNAList;
	}

	public void setMatchingNAList(List<ServiceProvider> matchingNAList) {
		this.matchingNAList = matchingNAList;
	}
    
	public String getNationalAccount(){
		return SUCCESS;
	}

	public String getZipsCode() {
		return zipsCode;
	}

	public void setZipsCode(String zipsCode) {
		this.zipsCode = zipsCode;
	}
	
	public List<String> getCounties() {
		return counties;
	}

	public void setCounties(List<String> counties) {
		this.counties = counties;
	}
    
}
