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
 */
package tavant.twms.domain.policy;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author radhakrishnan.j
 * 
 */
public interface CustomerRepository extends GenericRepository<Customer, Long> {

	public List<Customer> findCustomersForNameLike(String name, int pageSize);

	public List<Customer> findCustomersForCompanyNameLike(String companyName,
			int pageSize);

	public Customer findCustomerByCustomerId(String customerId);
	
	public List<Customer> findCustomerByIds(Collection<Long> customerIds);

	public Customer findCustomerById(Long customerId);

	public void createCustomer(Customer cust);

	public void updateCustomer(Customer cust);

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
	
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(String name,ListCriteria listCriteria);
		
	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeFromAddressBook(
			String name, Organization organization,AddressBookType type,
			ListCriteria listCriteria);

	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeforNAorGAFromAddressBook(
			String name, List<Organization> organization,AddressBookType type,
			ListCriteria listCriteria);

	
	public Customer findCustomerByCustomerIdAndDealer(String customerId,
			Organization organization, AddressBookType type);
	
	public Customer findCustomerByCustomerIdAndDealer(final String customerId,
			final Organization organization);
	
}
