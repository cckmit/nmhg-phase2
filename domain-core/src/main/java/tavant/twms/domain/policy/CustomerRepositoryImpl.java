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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.QueryParameters;

/**
 * @author radhakrishnan.j
 * 
 */
public class CustomerRepositoryImpl extends
		GenericRepositoryImpl<Customer, Long> implements CustomerRepository {

	@SuppressWarnings("unchecked")
	public List<Customer> findCustomersForNameLike(final String name,
			final int pageSize) {

		return (List<Customer>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Customer c "
												+ "where c.name like :name and c.individual = true "
												+ "order by c.name")
								.setParameter("name", name + "%")
								.setFirstResult(0).setMaxResults(pageSize)
								.list();
					}

				});
	}

	@SuppressWarnings("unchecked")
	public List<Customer> findCustomersForCompanyNameLike(
			final String companyName, final int pageSize) {

		return (List<Customer>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Customer c "
												+ "where c.companyName like :companyName and c.individual = false "
												+ "order by c.companyName")
								.setParameter("companyName", companyName + "%")
								.setFirstResult(0).setMaxResults(pageSize)
								.list();
					}

				});
	}

	public Customer findCustomerByCustomerId(final String customerId) {
		return (Customer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Customer c where c.customerId =:custIdParam")
								.setString("custIdParam", customerId)
								.uniqueResult();
					}
				});
	}
	
	public Customer findCustomerByCustomerIdAndDealer(final String customerId,
			final Organization organization, final AddressBookType type) {
		return (Customer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Customer customer where customer.customerId =:customerId"
												+ " and exists(select abam from AddressBookAddressMapping abam where abam.addressBook.belongsTo = :organization"
												+ " and abam.addressBook.type = :type and abam.address=customer.address)")
								.setParameter("customerId", customerId)
								.setParameter("organization", organization)
								.setParameter("type", type).uniqueResult();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<Customer> findCustomerByIds(final Collection<Long> customerIds) {
		return findByIds(customerIds);
	}

	public Customer findCustomerById(Long customerId) {
		return (Customer) getHibernateTemplate()
				.get(Customer.class, customerId);
	}

	public void createCustomer(Customer cust) {
		getHibernateTemplate().save(cust);
	}

	public void updateCustomer(Customer cust) {
		getHibernateTemplate().update(cust);
	}

	public Serializable createCustomerAndReturnKey(Customer cust) {
		return getHibernateTemplate().save(cust);
	}

	public PageResult<Customer> findCustomersForNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria) {
		String individualCustomerSearchQuery = "from Customer customer join customer.addresses address "
												+ " where upper(customer.name) like upper(:name) and customer.individual = true and "
												+ " address in (select abam.address from AddressBookAddressMapping abam join abam.addressBook addressBook "
												+ " where (addressBook.belongsTo = :organization and addressBook.d.active = 1) and (abam.address = address and abam.d.active = 1))"
												;
		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("name", name + "%");
		params.put("organization", organization);
		
		return findPageUsingQuery(individualCustomerSearchQuery, "customer.name asc", "select distinct(customer)", listCriteria.getPageSpecification(), new QueryParameters(params));	 

	}
	
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name, Organization organization,
			ListCriteria listCriteria) {
		String companyCustomerSearchQuery = "from Customer customer join customer.addresses address "
												+ " where upper(customer.companyName) like upper(:companyName) and customer.individual = false and "
												+ " address in  (select abam.address from AddressBookAddressMapping abam join abam.addressBook addressBook join addressBook.belongsTo belongsTo "
												+ " where (belongsTo = :organization or addressBook.type ='GOVERNMENTACCOUNT' or addressBook.type ='NATIONALACCOUNT'))"
		                                        ;
		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("companyName", name + "%");
		params.put("organization", organization);
		return   findPageUsingQueryForDistinctItems(companyCustomerSearchQuery, "customer.companyName asc", 
    			"select distinct(customer)", listCriteria.getPageSpecification(), new QueryParameters(params),"distinct customer");
	}
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(String name,ListCriteria listCriteria){
		String companyCustomerSearchQuery = "from Customer customer join customer.addresses address "
			+ " where upper(customer.companyName) like upper(:companyName) and customer.individual = false and "
			+ " address in  (select abam.address from AddressBookAddressMapping abam join abam.addressBook addressBook)";
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("companyName", name + "%");
			return   findPageUsingQueryForDistinctItems(companyCustomerSearchQuery, "customer.companyName asc", 
			"select distinct(customer)", listCriteria.getPageSpecification(), new QueryParameters(params),"distinct customer");
	}
	
	
	public PageResult<Customer> findCustomersForCompanyNameLikeFromAddressBook(
			String name, Organization organization,String businessUnit,
			ListCriteria listCriteria) {
		String companyCustomerSearchQuery = "from Customer customer, Address address, AddressBook addressBook, AddressBookAddressMapping mapping"
				+ " where upper(customer.companyName) like upper(:companyName) and customer.individual = false and"
				+ " customer.address = address and mapping.address = address and mapping.addressBook = addressBook"
				+ " and (addressBook.belongsTo = :organization or addressBook.type ='GOVERNMENTACCOUNT' or addressBook.type ='NATIONALACCOUNT')"
				+ " and (:businessUnit) in elements(addressBook.belongsTo.businessUnits)"
				+ " and addressBook.d.active = 1 and mapping.d.active = 1";
		
		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("companyName", name + "%");
		params.put("organization", organization);
		params.put("businessUnit", businessUnit);
		return   findPageUsingQueryForDistinctItems(companyCustomerSearchQuery, "customer.companyName asc, customer.customerId asc", 
    			"select distinct(customer)", listCriteria.getPageSpecification(), new QueryParameters(params),"distinct customer");
	}
	
	// This would be used to fetch customers based on a organization and Customer Type
	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeFromAddressBook(
			String name, Organization organization,AddressBookType type,
			ListCriteria listCriteria){
		
		String companyCustomerSearchQuery = "from Customer customer, Address address, AddressBook addressBook, AddressBookAddressMapping mapping"
				+ " where customer.individual = false and upper(customer.companyName) like upper(:companyName)"
				+ " and mapping.address = address and mapping.addressBook = addressBook"
				+ " and addressBook.d.active = 1 and mapping.d.active = 1"
				+ " and addressBook.belongsTo in (:organization) and addressBook.type = :type"
				+ " and customer.address = address ";

		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("companyName", name + "%");
		params.put("organization", organization);
		params.put("type", type);
		
		return   findPageUsingQueryForDistinctItems(companyCustomerSearchQuery, "customer.companyName asc, customer.customerId asc", 
    			"select distinct(customer)", listCriteria.getPageSpecification(), new QueryParameters(params),"distinct customer");
	}
	
	public PageResult<Customer> findCustomersForCompanyNameLikeAndTypeforNAorGAFromAddressBook(
			String name, List<Organization> organization,AddressBookType type,
			ListCriteria listCriteria){
		
		String companyCustomerSearchQuery = "from Customer customer join customer.addresses address "
			+ " where upper(customer.companyName) like upper(:companyName) and customer.individual = false and "
			+ " address  in (select abam.address from AddressBookAddressMapping abam join abam.addressBook addressBook "
			+ " where (addressBook.belongsTo in(:organization) and addressBook.type = :type and addressBook.d.active = 1 ) " 
			+ " and (abam.address = address and abam.d.active = 1))";

		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("companyName", name + "%");
		params.put("organization", organization);
		params.put("type", type);
		
		return   findPageUsingQueryForDistinctItems(companyCustomerSearchQuery, "customer.companyName asc", 
    			"select distinct(customer)", listCriteria.getPageSpecification(), new QueryParameters(params),"distinct customer");
	}
	
	
	public Customer findCustomerByCustomerIdAndDealer(final String customerId,
			final Organization organization) {
		return (Customer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select customer from Customer customer join customer.addresses address "
										+ " where customer.customerId =:customerId and customer.individual = false and "
										+ " address in  (select abam.address from AddressBookAddressMapping abam join abam.addressBook addressBook "
										+ " where (addressBook.belongsTo = :organization or addressBook.type ='GOVERNMENTACCOUNT' or addressBook.type ='NATIONALACCOUNT') and abam.address = address)")
								.setParameter("customerId", customerId)
								.setParameter("organization", organization).uniqueResult();
					}
				});
	}
}