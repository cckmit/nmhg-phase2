    package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class OrganizationRepositoryImpl extends
		GenericRepositoryImpl<Party, Long> implements OrganizationRepository {

	public Organization findByName(final String name) {
		return (Organization) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria nameCriteria = session
								.createCriteria(Organization.class);
						nameCriteria.add(Restrictions.eq("name", name));
						return nameCriteria.uniqueResult();
					}
				});
	}
	
	public Organization findOrgById(final Long id) {
		return (Organization) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria nameCriteria = session
								.createCriteria(Organization.class);
						nameCriteria.add(Restrictions.eq("id", id));
						return nameCriteria.uniqueResult();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public PageResult<Party> findAllSuppliers(final ListCriteria listCriteria) {
		return findPage("from Supplier supplier", listCriteria);
	}

	public void updateOrganization(Organization organization) {
		getHibernateTemplate().update(organization);
	}
	
	public void updateOrganizationAddress(OrganizationAddress orgAddress) {
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactiveAddress");
		getHibernateTemplate().update(orgAddress);
		getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("excludeInactiveAddress");
	}
	
	public void updateAddress(Address address) {
		getHibernateTemplate().update(address);
	}
	
	@SuppressWarnings("unchecked")
	public List<OrganizationAddress> getAddressesForOrganization(
			final Organization organization) {
		return (List<OrganizationAddress>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select orgAddress from Organization organization "
										  + " join organization.orgAddresses orgAddress where " 
                                          + " organization =:organization and orgAddress.addressActive = true and exists (select abam.address from AddressBookAddressMapping abam "
                                          + " join abam.addressBook addressBook where addressBook.belongsTo = :organization " 
                                          + " and abam.address = orgAddress and addressBook.type = :addressBookType and abam.address.d.active = true)")
								.setParameter("organization", organization)
								.setParameter("addressBookType", AddressBookType.SELF)
								.list();
					}
				});
	}
	
	
	@SuppressWarnings("unchecked")
	public OrganizationAddress getPrimaryOrganizationAddressForOrganization(
			final Organization organization) {
		return (OrganizationAddress) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select orgAddress from Organization organization "
										  + " join organization.orgAddresses orgAddress where " 
                                          + " organization =:organization and exists (select abam.address from AddressBookAddressMapping abam "
                                          + " join abam.addressBook addressBook where addressBook.belongsTo = :organization " 
                                          + " and abam.address = orgAddress and addressBook.type = :addressBookType and abam.primary=true)")
								.setParameter("organization", organization)
								.setParameter("addressBookType", AddressBookType.SELF).setMaxResults(1)
								.uniqueResult();
					}
				});
	}
	
	
	@SuppressWarnings("unchecked")
	public OrganizationAddress getAddressesForOrganizationBySiteNumber(
			final Organization organization, final String siteNumber) {
		return (OrganizationAddress) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						session.disableFilter("excludeInactiveAddress");
						OrganizationAddress organizationAddress = (OrganizationAddress) session
								.createQuery(
										"select orgAddress from Organization organization "
												+ " join organization.orgAddresses orgAddress where "
												+ " organization =:organization and exists (select abam.address from AddressBookAddressMapping abam "
												+ " join abam.addressBook addressBook where addressBook.belongsTo = :organization "
												+ " and abam.address = orgAddress and addressBook.type = :addressBookType and abam.address.d.active = true)"
												+ " and orgAddress.siteNumber = :siteNumber ")
								.setParameter("organization", organization)
								.setParameter("addressBookType",
										AddressBookType.SELF).setParameter(
										"siteNumber", siteNumber)
								.uniqueResult();
						session.enableFilter("excludeInactiveAddress");
						return organizationAddress;
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public OrganizationAddress getOrganizationAddressBySiteNumber(final String siteNumber) {
		return (OrganizationAddress) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						
						OrganizationAddress addresssite= 
						 (OrganizationAddress) session
								.createQuery(
										"select orgAddress from Organization organization "
										+ " join organization.orgAddresses orgAddress where " 
                                        + " orgAddress.siteNumber = :siteNumber ")
                                       
//								.setParameter("organization", organization)
//								.setParameter("addressBookType", AddressBookType.SELF)
								.setParameter("siteNumber", siteNumber)
								
								.uniqueResult();
						
						
						return addresssite;
					}
				});
	}
	
   public OrganizationAddress getOrgAddressBySiteNumberForUpload(final String siteNumber, final Long orgId){
	        return (OrganizationAddress) getHibernateTemplate().execute(
	   				new HibernateCallback() {
	   					public Object doInHibernate(Session session)
	   							throws HibernateException, SQLException {
	                           String sqlQuery = "select oa.id as id, oa.location as location, oa.site_number as siteNumber, oa.name as name, oa.address_active as addressActive" +
	                           		" from ORGANIZATION o, organization_org_addresses ooa, ORGANIZATION_ADDRESS oa where"
	                                   + " o.ID = ooa.ORGANIZATION and ooa.ORG_ADDRESSES = oa.id and substr(oa.SITE_NUMBER,10,4) = :siteNumber and"
	                                   + " o.ID = :orgId and oa.address_active=1";
	   
	                           return session.createSQLQuery(sqlQuery).addScalar("id",Hibernate.LONG).
	   								addScalar("location",Hibernate.STRING).addScalar("siteNumber",Hibernate.STRING).
	   								addScalar("name",Hibernate.STRING).addScalar("addressActive", Hibernate.BOOLEAN)
	   								.setLong("orgId", orgId).setString("siteNumber", siteNumber).
	   								setResultTransformer(Transformers.aliasToBean(OrganizationAddress.class)).uniqueResult();
	   					}
	   				});
	       }


	@SuppressWarnings("unchecked")
	public PageResult<OrganizationAddress> getAddressesForOrganization(final ListCriteria criteria, final Organization organization) {
		return (PageResult<OrganizationAddress>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// Count Query
				Long countOfOrgAddresses = (Long) session.createQuery(
						"select count(orgAddress) from Organization organization "
						  + " join organization.orgAddresses orgAddress where " 
                          + " organization =:organization and exists (select abam.address from AddressBookAddressMapping abam "
                          + " join abam.addressBook addressBook where addressBook.belongsTo = :organization " 
                          + " and abam.address = orgAddress and addressBook.type = :addressBookType)")
				.setParameter("organization", organization)
				.setParameter("addressBookType", AddressBookType.SELF)
				.uniqueResult();
				// List of Organization Addresses
				List<OrganizationAddress> orgAddresses = session.createQuery(
						"select orgAddress from Organization organization "
						  + " join organization.orgAddresses orgAddress where " 
                          + " organization =:organization and exists (select abam.address from AddressBookAddressMapping abam "
                          + " join abam.addressBook addressBook where addressBook.belongsTo = :organization " 
                          + " and abam.address = orgAddress and addressBook.type = :addressBookType)")
				.setParameter("organization", organization)
				.setParameter("addressBookType", AddressBookType.SELF)
				.setFirstResult(criteria.getPageSpecification().offSet())
				.setMaxResults(criteria.getPageSpecification().getPageSize())
				.list();
				return new PageResult<OrganizationAddress>(
						orgAddresses, criteria.getPageSpecification(), 
						criteria.getPageSpecification().convertRowsToPages(countOfOrgAddresses.longValue()));
			}});
	}

	public Organization findByOrganizationName(final String organizationName) {
		return (Organization) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Organization org where upper(org.name)=:orgName")
								.setParameter("orgName", organizationName.toUpperCase())
								.uniqueResult();
					};
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<Currency> listUniqueCurrencies()
	{
        return (List<Currency>) getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	Criteria currencyCriteria = session.createCriteria(Organization.class);
            	currencyCriteria.setProjection(Projections.distinct(Projections.property("preferredCurrency")));
            	currencyCriteria.add(Restrictions.isNotNull("preferredCurrency"));
            	currencyCriteria.addOrder(Order.asc("preferredCurrency"));
                return currencyCriteria.list();
            }
        });
	}

	public void removeAddressesForOrganization(final String siteNumber) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String queryString = "delete from OrganizationAddress organizationAddress where organizationAddress.siteNumber =:number";
				session.createQuery(queryString)
						.setParameter("number", siteNumber).executeUpdate();
				return null;
			}

		});
	}

    public List<Long> getParentOrganizationIds(final Long orgId){
        return (List<Long>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    /**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     */
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session.createSQLQuery("select org.PARENT_ORG as id from org_owner_association org where org.CHILD_ORG=:orgId").addScalar("id", Hibernate.LONG)
                                .setParameter("orgId", orgId).setCacheable(true).list();

                    };
                });
    }

   /* public List<Organization> getChildOrganizations(final Long orgId){
        return (List<Organization>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    *//**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     *//*
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        //return session.createSQLQuery("select o.id as id from org_owner_association org, organization o where org.PARENT_ORG=:orgId and org.child_org=o.id").addScalar("id",Hibernate.LONG)
                        return session.createSQLQuery("select org.child_org as id from org_owner_association org where org.PARENT_ORG=:orgId").addScalar("id",Hibernate.LONG)
                        .setResultTransformer(Transformers.aliasToBean(Organization.class)).setParameter("orgId", orgId).setCacheable(true).list();

                    };
                });
    }

    public List<Long> getChildOrganizationIds(final Long orgId){
        return (List<Long>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    *//**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     *//*
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session.createSQLQuery("select org.child_org as id from org_owner_association org where org.PARENT_ORG=:orgId").addScalar("id",Hibernate.LONG)
                                .setParameter("orgId", orgId).setCacheable(true).list();

                    };
                });
    }

    public List<Organization> getParentOrganizations(final Long orgId){
        return (List<Organization>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    *//**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     *//*
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session.createSQLQuery("select org.PARENT_ORG as id from org_owner_association org where upper(org.CHILD_ORG)=:orgId").addScalar("id",Hibernate.LONG)
                                .setParameter("orgId", orgId).setCacheable(true).list();

                    };
                });
    }*/

    public Address findAddressWithMandatoryFields(final String addressLine1,final String country, final String city, final String state, final String zipCode){
        return (Address) getHibernateTemplate().execute(
                new HibernateCallback() {

                    /**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     */
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        List<Address> addresses = session
                        .createQuery(
                                "from Address address where upper(address.addressLine1)=:addressLine1 and upper(address.country) = :country and upper(address.city) = :city and upper(address.state) = :state and upper(address.zipCode) = :zipCode")
                        .setParameter("addressLine1", addressLine1.toUpperCase())
                        .setParameter("country", country.toUpperCase())
                        .setParameter("city", city.toUpperCase())
                        .setParameter("state", state.toUpperCase())
                        .setParameter("zipCode", zipCode.toUpperCase())
                        .list();
                        if(!addresses.isEmpty()){
                            return addresses.get(0);
                        }else{
                            return null;
                        }
                    };
                });
    }
    
    
    public List<User> getDealersFromDealerShip(final List<Long> dealerShips) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public List<User> doInHibernate(Session session) {
                return session
                        .createQuery(
                                "SELECT user FROM User user join user.belongsToOrganizations organization  where organization.id in (:dealerShips)")
                        .setParameterList("dealerShips", dealerShips).list();
            }
        });

    }

    public List<User> getCustomers(final List<Long> customerIds) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public List<User> doInHibernate(Session session) {
                return session
                        .createQuery(
                                "SELECT user FROM User user join user.belongsToOrganizations organization  where organization.id in (:customerIds) or organization.id in ( select id from CustomerLocation where customer in(:customerIds)) ")
                        .setParameterList("customerIds", customerIds).list();
            }
        });
    }

    public Long checkLoggedInDealerForDualBrand(final Long id){
        return (Long) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        Long dealerId=
                                (Long) session
                                        .createQuery(
                                                "select dualDealer.id from Dealership where id =:id")
                                        .setParameter("id", id)
                                        .uniqueResult();
                        return dealerId;
                    }
                });
    }
    
	public PageResult<Party> findDealersByOrganizations(final ListCriteria listCriteria, final List<Organization> organizations) {
		  Map<String, Object> params = new HashMap<String, Object>();
	        final StringBuilder baseQuery = new StringBuilder(" from Organization org where ");
	        if (CollectionUtils.isNotEmpty(organizations))
			{
				int fromIndex = 0;
				int endIndex = 0;
				int chunk = 500;
				int size = organizations.size();
				if (size < 1000)
				{
					baseQuery.append(" org IN (:organizations) ");
					params.put("organizations", organizations);
				} 
				else
				{
					baseQuery.append(" ( ");
					for (int loop = 1; endIndex < size; loop++)
					{
						fromIndex = endIndex;
						endIndex = chunk * loop;
						if (endIndex > size)
						{
							endIndex = size;
						}
						baseQuery.append(" org IN (:organizations" + loop + ") ");
						if (endIndex < size)
						{
							baseQuery.append(" or ");
						}
						params.put("organizations" + loop, organizations.subList(fromIndex, endIndex));
					}
					baseQuery.append(" ) ");
				}
			}
	        params.putAll(listCriteria.getParameterMap());
	        if (listCriteria.isFilterCriteriaSpecified()) {
	            baseQuery.append(" and ").append(listCriteria.getParamterizedFilterCriteria());
	        }
	        return  findPageUsingQuery(baseQuery.toString(), listCriteria.getSortCriteriaString(), listCriteria.getPageSpecification(), params);
	}

}
