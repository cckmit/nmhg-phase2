package tavant.twms.domain.policy;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface CustomerService {

	public List<Customer> findCustomersForNameLike(String name, int pageSize);

	public List<Customer> findCustomersForCompanyNameLike(String companyName,
			int pageSize);

	public Customer findCustomerByCustomerId(String customerId);
	
	public List<Customer> findCustomerByIds(Collection<Long> customerIds);

	public Customer findCustomerById(Long customerId);

	@Transactional(readOnly = false)
	public void createCustomer(Customer cust);

	@Transactional(readOnly = false)
	public void updateCustomer(Customer cust);

	@Transactional(readOnly = false)
	public Serializable createCustomerAndReturnKey(Customer cust);
	
	public PageResult<Customer> findCustomersForNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria);
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria);
	
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name, Organization organization,String businessUnit,
			ListCriteria listCriteria);
	
	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeFromAddressBook(
			String name, Organization organization, AddressBookType type,
			ListCriteria listCriteria);
	
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name,ListCriteria listCriteria);
	
	
	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeforNAorGAFromAddressBook(
			String name, List<Organization> organization, AddressBookType type,
			ListCriteria listCriteria);
	
	public List<Address> findAddressForNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria);
	
	public Customer findCustomerByCustomerIdAndDealer(String customerId ,Organization organization,AddressBookType type);

	public List<Address> findAddressForCustomerIdFromAddressBook(
			String customerId, Organization organizationForSearch,
			ListCriteria listCriteria);
	
	public Customer findCustomerByCustomerIdAndDealer(final String customerId,
			final Organization organization);


}