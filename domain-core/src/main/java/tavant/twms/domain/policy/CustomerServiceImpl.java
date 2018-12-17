package tavant.twms.domain.policy;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.QueryParameters;
import tavant.twms.infra.Repository;

public class CustomerServiceImpl implements CustomerService {

	private CustomerRepository customerRepository;

	private RoleRepository roleRepository;
	
	private Repository repository;

	public void setCustomerRepository(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public List<Customer> findCustomersForNameLike(String name, int pageSize) {
		return customerRepository.findCustomersForNameLike(name, pageSize);
	}

	public Customer findCustomerByCustomerId(String customerId) {
		return customerRepository.findCustomerByCustomerId(customerId);
	}
	
	public List<Customer> findCustomerByIds(Collection<Long> customerIds) {
		return customerRepository.findCustomerByIds(customerIds);
	}

	public Customer findCustomerById(Long customerId) {
		return customerRepository.findCustomerById(customerId);
	}

	public void createCustomer(Customer cust) {
		// Add a default role "Customer"
		cust.getRoles().add(roleRepository.findRoleByName(Role.CUSTOMER));
		customerRepository.createCustomer(cust);
	}

	public void updateCustomer(Customer cust) {
		customerRepository.updateCustomer(cust);
	}

	public List<Customer> findCustomersForCompanyNameLike(String companyName,
			int pageSize) {
		return customerRepository.findCustomersForCompanyNameLike(companyName,
				pageSize);
	}

	public RoleRepository getRoleRepository() {
		return roleRepository;
	}

	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public Serializable createCustomerAndReturnKey(Customer cust) {
		return customerRepository.createCustomerAndReturnKey(cust);
	}
	
	public PageResult<Customer> findCustomersForNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria){
		return customerRepository. findCustomersForNameLikeFromAddressBook(name, organization, listCriteria);
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria){
		return customerRepository.findCustomersForCompanyNameLikeFromAddressBook(name, organization, listCriteria);
	}
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name, Organization organization,String businessUnit,
			ListCriteria listCriteria){
		return customerRepository.findCustomersForCompanyNameLikeFromAddressBook(name, organization,businessUnit, listCriteria);
	}
	
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name,
			ListCriteria listCriteria){
				return customerRepository.findCustomersForCompanyNameLikeFromAddressBook(name, listCriteria);
			}
	
	
	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeFromAddressBook(
			String name, Organization organization, AddressBookType type,
			ListCriteria listCriteria){
		return customerRepository.findCustomersForCompanyNameLikeAndTypeFromAddressBook(
				name, organization, type, listCriteria);
	}
	
	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeforNAorGAFromAddressBook(
			String name, List<Organization> organization, AddressBookType type,
			ListCriteria listCriteria){
		return customerRepository.findCustomersForCompanyNameLikeAndTypeforNAorGAFromAddressBook(
				name, organization, type, listCriteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<Address> findAddressForNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria){
		String AddressCustomerSearchQuery = "select address from Customer customer join customer.addresses address "
			+ " where upper(customer.companyName) like upper(:companyName) and customer.individual = false and "
			+ " address  in (select abam.address from AddressBookAddressMapping abam join abam.addressBook addressBook "
			+ " where (addressBook.belongsTo = :organization  and addressBook.d.active = 1 ) " 
			+ " and (abam.address = address and abam.d.active = 1))";

		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("companyName", name);
		params.put("organization", organization);
		return (List<Address>) this.repository.findUsingQuery(AddressCustomerSearchQuery, params);
	}
	
	@SuppressWarnings("unchecked")
	public List<Address> findAddressForCustomerIdFromAddressBook(
			String customerId, Organization organization,
			ListCriteria listCriteria) {
		String companyCustomerSearchQuery = "select address from Customer customer join customer.addresses address "
			+ " where upper(customer.customerId)= upper(:customerId) and customer.individual = false and "
			+ " address  in (select abam.address from AddressBookAddressMapping abam join abam.addressBook addressBook "
			+ " where (addressBook.belongsTo = :organization  and addressBook.d.active = 1 ) " 
			+ " and (abam.address = address and abam.d.active = 1))";
								
		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("customerId", customerId);
		params.put("organization", organization);
		return   (List<Address>) this.repository.findUsingQuery(companyCustomerSearchQuery, params);
	}

	public Customer findCustomerByCustomerIdAndDealer(String customerId,
			Organization organization, AddressBookType type) {
		return customerRepository.findCustomerByCustomerIdAndDealer(
				customerId,organization,type);
	}
	public Customer findCustomerByCustomerIdAndDealer(final String customerId,
			final Organization organization) {
		return customerRepository.findCustomerByCustomerIdAndDealer(
				customerId,organization);
	}
		
}