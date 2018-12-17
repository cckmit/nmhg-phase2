package tavant.twms.web.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookAddressMapping;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.policy.Customer;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.PageResult;

@SuppressWarnings("serial")
public class EndCustomerMgmt extends ManageCustomer {

	private int size;
	private String selectedItemsIds;
	private List<Customer> mergedCustomerList = new ArrayList<Customer>();
	private String mergedCustomer;
	private String selectedMergedCustomerIds;

	public boolean isPageReadOnly() {
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
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSelectedItemsIds() {
		return selectedItemsIds;
	}

	public void setSelectedItemsIds(String selectedItemsIds) {
		this.selectedItemsIds = selectedItemsIds;
	}

	public List<Customer> getMergedCustomerList() {
		return mergedCustomerList;
	}

	public void setMergedCustomerList(List<Customer> mergedCustomerList) {
		this.mergedCustomerList = mergedCustomerList;
	}

	public String getMergedCustomer() {
		return mergedCustomer;
	}

	public void setMergedCustomer(String mergedCustomer) {
		this.mergedCustomer = mergedCustomer;
	}

	public String getSelectedMergedCustomerIds() {
		return selectedMergedCustomerIds;
	}

	public void setSelectedMergedCustomerIds(String selectedMergedCustomerIds) {
		this.selectedMergedCustomerIds = selectedMergedCustomerIds;
	}

	public EndCustomerMgmt() {
		super();
	}
	
	public void prepare() {
		List<Country> countries = this.msaService.getCountryList();
		for (Country country : countries) {
			this.countryList.put(country.getCode(), country.getName());
		}
		this.countriesFromMSA = this.msaService.getCountriesFromMSA();
		
		if (this.selectedMergedCustomerIds != null
				&& StringUtils.hasText(this.selectedMergedCustomerIds)) {
			this.mergedCustomerList = getSelectedCustomers();
		}
	}

	@Override
	public void validate() {
		if (this.selectedMergedCustomerIds != null
				&& StringUtils.hasText(this.selectedMergedCustomerIds)) {
			this.selectedMergedCustomerIds = this.selectedMergedCustomerIds
			.replaceAll(" ", "");
			String[] customerIds = this.selectedMergedCustomerIds.split(",");
			if (customerIds.length < 2) {
				addActionError("error.customerMerge.lessThanTwoCustomerSelected");
			}
		} else {
			addActionError("error.customerMerge.lessThanTwoCustomerSelected");
		}

	}

	public String searchEndCustomers() {
		setListCriteria();
		PageResult<Customer> pageResult = null;
		pageResult = getCustomers(pageResult);
		this.setMatchingCustomerList(pageResult.getResult());
		this.setSize(pageResult.getResult().size());
		for (int i = 0; i < pageResult.getNumberOfPagesAvailable(); i++) {
			this.pageNoList.add(new Integer(i + 1));
		}
		return SUCCESS;
	}

	private PageResult<Customer> getCustomers(PageResult<Customer> pageResult) {
		Organization organizationForSearch;
		if (this.dealerOrganization != null) {
			organizationForSearch = this.dealerOrganization;
		} else if (this.warranty != null) {
			organizationForSearch = new HibernateCast<Dealership>()
			.cast(this.warranty.getForTransaction().getOwnerShip());
		} else {
			if (isLoggedInUserADealer()) {
				organizationForSearch = getOrganization();
			} else {
				if (StringUtils.hasText(getDealerName())) {
					organizationForSearch = dealershipRepository
					.findByDealerName(getDealerName());
				} else {
					organizationForSearch = getOrganization();
				}
			}
		}

		if (this.customerType.equals("Individual")) {
			pageResult = this.customerService
			.findCustomersForNameLikeFromAddressBook(
					getCustomerName(this.customerStartsWith),
					organizationForSearch, listCriteria);
		} else if (this.customerType.equals("Company")) {
			pageResult = this.customerService
			.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
					getCustomerName(this.customerStartsWith),
					organizationForSearch, AddressBookType.ENDCUSTOMER,
					listCriteria);
		}
		return pageResult;
	}

	private String getCustomerName(String name) {
		String customerName = "";
		if (name != null && StringUtils.hasText(name)) {
			return name;
		}
		return customerName;
	}

	public List<Customer> getSelectedCustomers() {
		this.selectedMergedCustomerIds = this.selectedMergedCustomerIds
		.replaceAll(" ", "");
		String[] customerIds = this.selectedMergedCustomerIds.split(",");
		Set<Long> ids = new HashSet<Long>();
		for (int i = 0; i < customerIds.length; i++) {
			ids.add(new Long(customerIds[i]));
		}
		List<Customer> customers = this.customerService.findCustomerByIds(ids);
		return customers;
	}

	public String selectedEndCustomersForMerge() {
		setListCriteria();
		PageResult<Customer> pageResult = null;
		pageResult = getCustomers(pageResult);
		this.setMatchingCustomerList(pageResult.getResult());
		removeSelectedCustomersandAddForMerge(this.matchingCustomerList);
		this.setSize(pageResult.getResult().size());
		for (int i = 0; i < pageResult.getNumberOfPagesAvailable(); i++) {
			this.pageNoList.add(new Integer(i + 1));
		}
		return SUCCESS;
	}

	private void removeSelectedCustomersandAddForMerge(
			List<Customer> customerList) {
		if (customerList != null && !customerList.isEmpty()) {
			List<Customer> reducedMatchingCustomerList = new ArrayList<Customer>();
			reducedMatchingCustomerList.addAll(customerList);
			for (Iterator<Customer> customerIterator = customerList.iterator(); customerIterator
			.hasNext();) {
				Customer customer = customerIterator.next();
				if (this.selectedItemsIds != null
						&& StringUtils.hasText(this.selectedItemsIds)) {
					if (this.selectedItemsIds.contains(customer.getId()
							.toString())) {
						this.mergedCustomerList.add(customer);
						reducedMatchingCustomerList.remove(customer);
					}
				}
			}
			this.setMatchingCustomerList(reducedMatchingCustomerList);
		}
	}

	public String perviewMerge() throws Exception {
		this.customer = this.customerService.findCustomerById(new Long(
				this.mergedCustomer));
		Organization organization;
		if (StringUtils.hasText(getDealerName())) {
			organization = dealershipRepository
			.findByDealerName(getDealerName());
		} else {
			organization = getOrganization();
		}
		if (this.selectedAddressId == null) {
			this.selectedAddressId = this.customer.getAddresses().get(
					this.customer.getAddresses().size() - 1).getId();
		}
		int addressIndex = 0;
		for (Address address : this.customer.getAddresses()) {
			boolean isAuthorized = false;
			AddressBookAddressMapping addressBookAddressMapping = this.addressBookService
			.getAddressBookAddressMappingByOrganizationAndAddress(
					address, organization);
			if (addressBookAddressMapping == null) {
				addressBookAddressMapping = this.addressBookService
				.getAddressBookAddressMappingByOrganizationAndAddress(
						address, getOrganization());
				if (addressBookAddressMapping != null) {
					isAuthorized = true;
				}
			} else {
				isAuthorized = true;
			}
			if (!isAuthorized) {
				addressBookAddressMapping = addressBookService
				.getAddressBookAddressMappingByAddress(address);
				addActionError("error.edit.customer");
			}
			this.addressBookAddressMappings.add(addressBookAddressMapping);
			if (!this.countriesFromMSA.contains(address.getCountry())) {
				this.states.add(addressIndex, address.getState());
				this.cities.add(addressIndex, address.getCity());
				this.zips.add(addressIndex, address.getZipCode());
			} else {
				this.states.add(addressIndex, null);
				this.cities.add(addressIndex, null);
				this.zips.add(addressIndex, null);
			}
			addressIndex++;

		}
		if (hasActionErrors() || hasFieldErrors()) {
			return INPUT;
		}
		addActionWarning("message.managecustomer.preview");
		return SUCCESS;
	}

	public String confirmMerge() throws Exception {
		prepareCustomer();
		this.customerId = this.customer.getId();
		this.customerService.updateCustomer(this.customer);

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
		
		List<Customer> deactiveCustomers = new ArrayList<Customer>();
		for (Iterator<Customer> customerIterator = this.mergedCustomerList
				.iterator(); customerIterator.hasNext();) {

			Customer customer = customerIterator.next();
			if (customer != null
					&& !customer.getId().equals(this.customer.getId())) {
				AddressBookAddressMapping addressBookAddressMapping = null;
				Organization organization;
				if (StringUtils.hasText(getDealerName())) {
					organization = dealershipRepository
					.findByDealerName(getDealerName());
				} else {
					organization = getOrganization();
				}
				List<Address> addresses = new ArrayList<Address>();
				for (Address address : customer.getAddresses()) {
					boolean isAuthorized = false;
					addressBookAddressMapping = this.addressBookService
					.getAddressBookAddressMappingByOrganizationAndAddress(
							address, organization);
					if (addressBookAddressMapping == null) {
						addressBookAddressMapping = this.addressBookService
						.getAddressBookAddressMappingByOrganizationAndAddress(
								address, getOrganization());
						if (addressBookAddressMapping != null) {
							isAuthorized = true;
						}
					} else {
						isAuthorized = true;
					}
					if (!isAuthorized) {
						addressBookAddressMapping = addressBookService
						.getAddressBookAddressMappingByAddress(address);
					}
					if(addressBookAddressMapping != null){
						this.addressBookService.delete(addressBookAddressMapping);
					}

					populateAddress(address, this.customer.getAddresses().get(
							this.customer.getAddresses().size() - 1));
					addresses.add(address);
				}
				customer.setAddresses(addresses);
				deactiveCustomers.add(customer);
			}
		}

		for (Customer customer : deactiveCustomers) {
			this.customerService.updateCustomer(customer);
		}
		
		addActionMessage("message.managecustomer.merged");
		return SUCCESS;
	}
	
	private void populateAddress(Address toAddress, Address fromAddress){
		toAddress.setAddressIdOnRemoteSystem(fromAddress.getAddressIdOnRemoteSystem());
		toAddress.setAddressLine1(fromAddress.getAddressLine1());
		toAddress.setAddressLine2(fromAddress.getAddressLine2());
		toAddress.setAddressLine3(fromAddress.getAddressLine3());
		toAddress.setAddressLine4(fromAddress.getAddressLine4());
		toAddress.setBelongsTo(fromAddress.getBelongsTo());
		toAddress.setCity(fromAddress.getCity());
		toAddress.setContactPersonName(fromAddress.getContactPersonName());
		toAddress.setCountry(fromAddress.getCountry());
		toAddress.setEmail(fromAddress.getEmail());
		toAddress.setPhone(fromAddress.getPhone());
		toAddress.setSecondaryEmail(fromAddress.getSecondaryEmail());
		toAddress.setSecondaryPhone(fromAddress.getSecondaryPhone());
		toAddress.setState(fromAddress.getState());
		toAddress.setZipCode(fromAddress.getZipCode());
	}
		
}
