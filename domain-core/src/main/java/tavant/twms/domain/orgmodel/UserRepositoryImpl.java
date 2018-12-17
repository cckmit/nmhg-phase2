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
package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.annotations.common.DisableDeActivation;
import tavant.twms.annotations.common.DisableSpecificBuSelection;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.common.Constants;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SecurityHelper;

public class UserRepositoryImpl extends GenericRepositoryImpl<User, Long> implements UserRepository {
	
	@SuppressWarnings("unchecked")
    public void init() {
		systemUser = (User) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						User systemUser = (User)session
								.createQuery("select u from User u where lower(u.name)= 'system' ").uniqueResult();
						return systemUser;
					}
				});        
        
    }	
	
	private User systemUser;
	
	private CriteriaHelper criteriaHelper;
	
	private SecurityHelper securityHelper;  
	 
    public void save(User user) {
        getHibernateTemplate().save(user);
    }

    public void update(User user) {
        getHibernateTemplate().update(user);
    }
     public void updateAll(List<User> users) {
        getHibernateTemplate().saveOrUpdateAll(users);
    }
    public User findById(Long id) {
        return (User) getHibernateTemplate().get(User.class, id);
    }

    public User findByUserId(final String userId) {
        return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from User u where u.userId =:userIdParam").setString(
                        "userIdParam", userId).uniqueResult();
            }
        });
    }

    @DisableDeActivation
    @DisableSpecificBuSelection
    public User findByName(final String userName) {
        return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                 return session.createQuery("from User u where lower(u.name)=:nameParam").setString(
                         "nameParam", userName.toLowerCase()).uniqueResult();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Set<User> findUsersBelongingToRole(String roleName) {
        List<User> userList = getHibernateTemplate().find(
                "select u from User u, Role r where r in elements(u.roles) and r.name=?", roleName);
        Set<User> userSet = new HashSet<User>();
        userSet.addAll(userList);
        return userSet;
    }
    
    @SuppressWarnings("unchecked")
    public List<User> findAvailableUsersBelongingToRole(String roleName) {
        List<User> userList = getHibernateTemplate().find(
                "select u from User u, Role r ,UserBUAvailability uba where uba.orgUser = u and uba.available = true " +
                "and uba.role = r and r in elements(u.roles) and r.name=?", roleName);
        Set<User> userSet = new HashSet<User>();
        userSet.addAll(userList);
        return new ArrayList<User>(userSet);
    }

    @SuppressWarnings("unchecked")
    public List<Supplier> findAllSuppliers() {
        return getHibernateTemplate().find("from Supplier");
    }

    // FIXME : Method added for supplier search screen:Reports
    @SuppressWarnings("unchecked")
    public List<Supplier> findAllSuppliers(final String supplierName) {
        return (List<Supplier>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "from Supplier supplier where supplier.name like :supplierName ")
                        .setParameter("supplierName", supplierName + "%").list();
            };
        });

    }

    public Set<User> findAllDealers() {
        return findUsersBelongingToRole("dealer");
    }

    @SuppressWarnings("unchecked")
    public Set<User> findAllDealersLike(String name) {
        List<User> userList = getHibernateTemplate().find(
                "select u from User u, Role r where u.name like '" + name
                        + "%' and r in elements(u.roles) " + "and r.name='dealer'");
        Set<User> userSet = new HashSet<User>();
        userSet.addAll(userList);
        return userSet;
    }

    public Set<User> findAllProcessors() {
        return findUsersBelongingToRole("processor");
    }
    public Set<User> findAllAssignToUsers(){
    	Set<User> findAllAssignToUsers=new HashSet<User>();
    	findAllAssignToUsers.addAll(findUsersBelongingToRole("processor"));    	
    	return findAllAssignToUsers;     	
    }
    
    public List<User> findAllAvailableProcessors() {
        return findAvailableUsersBelongingToRole("processor");
    }
    
    public List<User> findAllAvailableRecoveryProcessors() {
        return findAvailableUsersBelongingToRole("recoveryProcessor");
    }

    @SuppressWarnings("unchecked")
    public Set<User> findAllUsers() {
        List<User> userList = getHibernateTemplate().find("select u from User u");
        Set<User> userSet = new HashSet<User>();
        userSet.addAll(userList);
        return userSet;
    }

    @SuppressWarnings("unchecked")
    public List<String> findUsersWithNameLikeOfType(final String name, final String userType) {
        return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "select user.name from User user where upper(user.name) like :userName and upper(user.userType) = :userTypeInput order by user.name")
                        .setParameter("userName", name.toUpperCase() + "%").setParameter("userTypeInput", userType.toUpperCase()).list();
            };
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<String> findUsersWithNameLike(final String name, final int pageNumber,
            final int pageSize, final String currentBusinessUnit) {
        return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
								.createQuery(
										"select user.name from User user, BusinessUnit bu where lower(user.name) like :userName and bu in elements(user.businessUnits) and bu.name=:currentBusinessUnit")
								.setParameter("userName", name.toLowerCase() + "%")
								.setParameter("currentBusinessUnit",
										currentBusinessUnit).setFirstResult(
										pageSize * pageNumber).setMaxResults(
										pageSize).list();
            };
        });
    }


    @SuppressWarnings("unchecked")
    public List<Supplier> findSuppliersWithNameLike(final String name, final int pageNumber,
            final int pageSize) {
        return (List<Supplier>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from Supplier where upper(name) like :name").setParameter(
                        "name", name.toUpperCase() + "%").setFirstResult(pageSize * pageNumber).setMaxResults(
                        pageSize).list();
            };
        });
    }

    public boolean isInternalUser(final User user) {
        final List<Organization> organizations = user.getBelongsToOrganizations();
        for(Organization organization:organizations){
        	if(organization.getId().longValue() == securityHelper.getOEMOrganization().getId().longValue())
        		return true;
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
	public Map<Long, String> findTechnicianForDealer(final Long dealerId,final String businessUnit) {
		return (Map<Long, String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String query = "select u.id as id,u.last_name as lastName,u.first_name as firstName,u.login as name " +
								" from org_user u, user_roles ur, role r,org_user_belongs_to_orgs buusr,bu_org_mapping buorg " +
								" where u.d_active = 1 and u.id = buusr.org_user and buusr.belongs_to_organizations = :dealerIdParam  " +
								" and buorg.org = :dealerIdParam and buorg.bu = :buParam" +
								" and u.id = ur.org_user and ur.roles = r.id and r.name = 'technician' ";
						List<User> users = getSession().createSQLQuery(query).addScalar("id",Hibernate.LONG).
						addScalar("lastName",Hibernate.STRING).addScalar("firstName",Hibernate.STRING).addScalar("name",Hibernate.STRING).
						setParameter("dealerIdParam", dealerId).setParameter("buParam", businessUnit).
						setResultTransformer(Transformers.aliasToBean(User.class)).list();
						Map<Long, String> toReturn = new HashMap<Long, String>();
						for (User usr: users) {
							toReturn.put(usr.getId(),usr.getCompleteNameAndLogin());
						}
						return toReturn;
					};
				});
	}
    
    @SuppressWarnings("unchecked")
    public List<User> findSalesPersonForDealer(final Long dealerId){
    	return (List<User>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria salesPersonCriteria = session.createCriteria(User.class);
						List addedAliases = new ArrayList<String>(5);
						salesPersonCriteria.createAlias("belongsToOrganizations", "belongsToOrgs");
						salesPersonCriteria.add(Restrictions.eq("belongsToOrgs.id",dealerId));
						UserRepositoryImpl.this.criteriaHelper.addAliasIfRequired(salesPersonCriteria, "roles", "roles", addedAliases);
						salesPersonCriteria.add(Restrictions.eq("roles.name","salesPerson"));
						salesPersonCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return salesPersonCriteria.list();
					};
				});
    }

    //associatedUserType can be technician, salesPerson
    @SuppressWarnings("unchecked")
    public List<User> findAssociatedUsersForDealers(final Set<Long> dealerIds, final  String associatedUserType){
    	return (List<User>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria salesPersonCriteria = session.createCriteria(User.class);
						List addedAliases = new ArrayList<String>(5);
						salesPersonCriteria.createAlias("belongsToOrganizations", "belongsToOrgs");
						salesPersonCriteria.add(Restrictions.in("belongsToOrgs.id",dealerIds));
						UserRepositoryImpl.this.criteriaHelper.addAliasIfRequired(salesPersonCriteria, "roles", "roles", addedAliases);
						salesPersonCriteria.add(Restrictions.eq("roles.name",associatedUserType));
						salesPersonCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return salesPersonCriteria.list();
					};
				});
    }

	public CriteriaHelper getCriteriaHelper() {
		return this.criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	@SuppressWarnings("unchecked")
	public PageResult<User> findAllProcessors(final ListCriteria processorCriteria, final String role) {
		super.setCriteriaHelper(this.getCriteriaHelper());
		return (PageResult<User>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria userCriteria = session.createCriteria(User.class);
				List addedAliases = new ArrayList<String>(5);
				userCriteria.add(Restrictions.eq(
						criteriaHelper.processNestedAssociations(userCriteria, 
								"roles.name", addedAliases), role));

//				if (processorCriteria.getSortCriteriaString().indexOf("userAvailablity.available") > 0) {
/*					userCriteria.add(Restrictions.eq(
							criteriaHelper.processNestedAssociations(userCriteria, 
									"userAvailablity.role.name", addedAliases), role));*/
//				}
				/*session.disableFilter("bu_name");*/
				
                addFilterRestrictions(userCriteria, processorCriteria, addedAliases);
                userCriteria.setProjection(Projections.countDistinct("id"));
                long numResults = (Long) userCriteria.uniqueResult();
                PageSpecification pageSpecification = processorCriteria.getPageSpecification();
                List<User> properties = Collections.EMPTY_LIST;
                if(numResults > 0 && numResults > pageSpecification.offSet()){
                	userCriteria.setProjection(null);
                	userCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                    addSortRestrictions(userCriteria, processorCriteria, addedAliases);
                    properties = userCriteria.setFirstResult(pageSpecification.offSet()).list();
                }
                return new PageResult<User>(properties,
								pageSpecification, pageSpecification
										.convertRowsToPages(numResults));
			}
		});
	}
	
	private void addFilterRestrictions(Criteria hCriteria, ListCriteria searchCriteria, List<String> addedAliases){
        Map<String, String> filterCriteria = searchCriteria.getFilterCriteria();
        for (Map.Entry<String, String> entry : filterCriteria.entrySet()) {
            String propertyName = entry.getKey();
            String value = entry.getValue();
            if(StringUtils.hasText(value)){
                String processedProperty = getCriteriaHelper().processNestedAssociations(hCriteria, propertyName, addedAliases);
                getCriteriaHelper().ilikeIfNotNull(hCriteria, processedProperty, value, MatchMode.START); 
            }
        }
    }
	
	private void addSortRestrictions(Criteria hCriteria, ListCriteria searchCriteria, List<String> addedAliases) {
        int joinType = CriteriaSpecification.LEFT_JOIN;
        Map<String, String> sortCriteria = searchCriteria.getSortCriteria();
        for (String propertyName : sortCriteria.keySet()) {
            String processedProperty = getCriteriaHelper().processNestedAssociations(hCriteria, propertyName, joinType, addedAliases);
            String sortDirection = sortCriteria.get(propertyName);
			final Order sortOrder = "asc".equalsIgnoreCase(sortDirection) ? Order.asc(processedProperty) : Order.desc(processedProperty);
            hCriteria.addOrder(sortOrder);
        }
    }
	
	public User findDefaultUserBelongingToRole(final String userRole) {
		return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	return (User) getHibernateTemplate().execute(
        				new HibernateCallback() {
        					@SuppressWarnings("unchecked")
							public Object doInHibernate(Session session)
        							throws HibernateException, SQLException {
        						Criteria userCriteria = session.createCriteria(User.class);
        						List<String> addedAliases = new ArrayList<String>(2);
        						userCriteria.add(Restrictions.eq(criteriaHelper.processNestedAssociations
								(userCriteria, "userAvailablity.role.name", addedAliases), userRole));
        						userCriteria.add(Restrictions.eq("userAvailablity.defaultToRole", Boolean.TRUE));
        						List<User> users = (List<User>) userCriteria.list();
        						if (users!=null && !users.isEmpty())
        							return users.get(0);
        						else 
        							return null;
        					};
        				});                
            };
        });	
	}
	
	@SuppressWarnings("unchecked")
	public User findDefaultUserBelongingToRoleForSelectedBU(final String businessUnit, final String userRole) {
		return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	return (User) getHibernateTemplate().execute(
        				new HibernateCallback() {
        					@SuppressWarnings("unchecked")
							public Object doInHibernate(Session session)
        							throws HibernateException, SQLException {
        						Criteria userCriteria = session.createCriteria(User.class);
        						List<String> addedAliases = new ArrayList<String>(2);
        						userCriteria.add(Restrictions.eq(criteriaHelper.processNestedAssociations
								(userCriteria, "userAvailablity.role.name", addedAliases), userRole));
        						userCriteria.add(Restrictions.eq(criteriaHelper.processNestedAssociations
        								(userCriteria, "userAvailablity.businessUnitInfo", addedAliases), businessUnit));
        						userCriteria.add(Restrictions.eq("userAvailablity.defaultToRole", Boolean.TRUE));
        						List<User> users = (List<User>) userCriteria.list();
        						if (users!=null && !users.isEmpty())
        							return users.get(0);
        						else 
        							return null;
        					};
        				});                
            };
        });	
	}

	@SuppressWarnings("unchecked")
	public boolean isUserHasRole(final User loggedInUser, final String roleName) {
		User partInvUser = (User) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						Criteria userCriteria = session.createCriteria(User.class);
						userCriteria.add(Restrictions.eq("id", loggedInUser.getId()));
						List<String> addedAliases = new ArrayList<String>(2);
						userCriteria.add(Restrictions.eq(
								criteriaHelper.processNestedAssociations
								(userCriteria, "roles.name", addedAliases), roleName));
						return userCriteria.uniqueResult();
					}
					
				}
		);
		return partInvUser!=null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isThirdPartyDealerWithLogin(final Long id) {
		List<User> partInvUser = (List<User>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						Criteria userCriteria = session.createCriteria(User.class);
						userCriteria.createAlias("belongsToOrganizations", "belongsToOrgs");
						userCriteria.add(Restrictions.eq("belongsToOrgs.id", id));
						List<String> addedAliases = new ArrayList<String>(2);
						userCriteria.add(Restrictions.eq(
								criteriaHelper.processNestedAssociations
								(userCriteria, "roles.name", addedAliases), "dealer"));
						return userCriteria.list();
					}
					
				}
		);
		return ( partInvUser.size() > 0 ? true : false );
	}

    @SuppressWarnings("unchecked")
    public List<User> findInternalUsersWithNameLike(final String name) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user where user.userType = :userType and lower(user.name) like :userName order by user.name")
                        .setParameter("userType", Constants.USER_TYPE_INTERNAL)
                        .setParameter("userName", name.toLowerCase() + "%")
                        .list();
            }

            ;
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<User> findInternalUsersWithNameLike(final String name, final int pageNumber, final int pageSize) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user where user.userType = :userType and lower(user.name) like :userName order by user.name")
                        .setParameter("userType", Constants.USER_TYPE_INTERNAL)
                        .setParameter("userName", name.toLowerCase() + "%")
                        .setFirstResult(pageSize * pageNumber)
                        .setMaxResults(pageSize).list();
            }

            ;
        });
    }
    
    @SuppressWarnings("unchecked")
     public List<BusinessUnit> findAllBusinessUnitsForUser(final User name) {
        return (List<BusinessUnit>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	session.disableFilter("bu_name");
				List<BusinessUnit> bus = session
						.createQuery("select bu from User u join u.businessUnits bu where u = :usr ")
                        .setParameter("usr", name)
						.list();
				session.enableFilter("bu_name");
				return bus;
			}
		});
}
    
    @SuppressWarnings("unchecked")
    public List<User> findProcessorUsersWithNameLike(final String roleName,final String name) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user JOIN  user.roles roles JOIN user.userAvailablity userAvailablity where roles.name = :roleTitle and userAvailablity.available =:available  " +
                                "and userAvailablity.role.name = :roleTitle " +
                                "and lower(user.name) like :userName")
                                .setParameter("roleTitle", roleName) 
                                .setParameter("available", true)
                                .setParameter("userName", name.toLowerCase() + "%").list();
            }
            ;
        });
    }
    
    @SuppressWarnings("unchecked")
    public User findFOCUserInServiceProviderOrganization(final String serviceProviderNo){
        List<User> users = (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user JOIN user.belongsToOrganizations belongsToOrgs " +
                                " where user.viewFocClaims = :permission  and "
                                + " belongsToOrgs.serviceProviderNumber=:serviceProviderNo" )
                        .setParameter("permission", true)               
                        .setParameter("serviceProviderNo", serviceProviderNo).list();
            }

            ;
        });
        
        return (users!=null && users.size() >0 ) ? users.get(0) : null;
        }
    
    @SuppressWarnings("unchecked")
    public List<User> findUsersWithRoleInServiceProviderOrganization(final String serviceProviderNo,final String role){
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user JOIN user.belongsToOrganizations belongsToOrgs JOIN" +
                                "  user.roles roles" +
                                " where roles.name = :roleTitle  and "
                                + " belongsToOrgs.serviceProviderNumber=:serviceProviderNo" )
                        .setParameter("roleTitle", role)               
                        .setParameter("serviceProviderNo", serviceProviderNo).list();
            }

            ;
        });
        
        
      }
    
    @DisableDeActivation
    @SuppressWarnings("unchecked")
    public List<User> findDealerUsers(final ServiceProvider serviceProvider, final List<String> listOfRoles, final String partialLoginId) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria userCriteria = session.createCriteria(User.class);
                userCriteria.add(Restrictions.like("name",partialLoginId+"%").ignoreCase());                
                if(serviceProvider != null) {
                	userCriteria.createAlias("belongsToOrganizations", "belongsToOrgs");
                	userCriteria.add(Restrictions.eq("belongsToOrgs.id", serviceProvider.getId()));
                }
                List<String> addedAliases = new ArrayList<String>(2);
                userCriteria.add(Restrictions.in(
                        criteriaHelper.processNestedAssociations
                                (userCriteria, "roles.name", addedAliases), listOfRoles));
                userCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                return userCriteria.list();
            }
        });
    }

	@SuppressWarnings("unchecked")
	public List<User> findAllUsersByRole(final List<String> listOfRoles) {
		return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria userCriteria = session.createCriteria(User.class);
				List<String> addedAliases = new ArrayList<String>(2);
				userCriteria.add(Restrictions.in(criteriaHelper.processNestedAssociations(userCriteria, "roles.name",
						addedAliases), listOfRoles));
				userCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
				return userCriteria.list();
			}
		});

	}
	
	
	@SuppressWarnings("unchecked")
	

	public List<User> findAllParticipantsForEquipmentTransfer (){
		return (List<User>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						  String query = "select u from org_user u join user_roles  ur on ur.org_user=u.id join role r on (r.id=ur.roles and r.name='fleetCoordinator') join role_permission_mapping rpm on rpm.role_def_id = r.id join  mst_admin_fnc_area fa on rpm.functional_area = fa.id join mst_admin_subject_area sa on (sa.id=rpm.subject_area and sa.name='emailNotification')";
						
							List<User> users = getSession().createSQLQuery(query).addScalar("id",Hibernate.LONG).
									addScalar("lastName",Hibernate.STRING).addScalar("firstName",Hibernate.STRING).addScalar("name",Hibernate.STRING).
									setResultTransformer(Transformers.aliasToBean(User.class)).list();
									Map<Long, String> toReturn = new HashMap<Long, String>();
									for (User usr: users) {
										toReturn.put(usr.getId(),usr.getCompleteNameAndLogin());
									}
									return toReturn;
								};
				});
	}

	@SuppressWarnings("unchecked")
	public Address fetchLoggedInUserAddress(final User user) 
	{
		return (Address) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Address address = (Address) session
						.createQuery("select  address from User u join u.address address  where u = :user and address.status='ACTIVE'")
						.setParameter("user", user)
						.list().get(0);
						return address;
						
					}
					});
	}
    
    @SuppressWarnings("unchecked")
	public Set<Role> getRolesForUser(final User user) 
	{
		return (Set<Role>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						List<Role> userRoles = session
						.createQuery("select distinct role from User u join u.roles role where lower(u.name) = :login")
						.setParameter("login", user.getName().toLowerCase())
						.list();
						if (userRoles==null || userRoles.isEmpty())
							return new HashSet<Role>();
						return new HashSet<Role>(userRoles);
					}
					});
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	
	@SuppressWarnings("unchecked")
	public User findTechnicianForDealerByLoginName(final Long dealerId, final String loginName) {
		return (User) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria technicianCriteria = session.createCriteria(User.class);
						List addedAliases = new ArrayList<String>(5);
						technicianCriteria.createAlias("belongsToOrganizations", "belongsToOrgs");
						technicianCriteria.add(Restrictions.eq("belongsToOrgs.id",dealerId));
						UserRepositoryImpl.this.criteriaHelper.addAliasIfRequired(technicianCriteria, "roles", "roles", addedAliases);
						technicianCriteria.add(Restrictions.eq("roles.name","technician"));
						technicianCriteria.add(Restrictions.eq("name",loginName));
						return technicianCriteria.uniqueResult();
					};
				});
	}
	
    @DisableDeActivation
    public User findInternalUser(final String loginId,final String userType){
    	return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria userCriteria = session.createCriteria(User.class);
           
                userCriteria.add(Restrictions.eq("name", loginId));
                userCriteria.add(Restrictions.eq("userType", userType));
                return userCriteria.uniqueResult();
            }
        });
    
    }

	public Set<Role> getRolesByType(final RoleType roleType) {
		return (Set<Role>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						List<Role> userRoles = session
						.createQuery("select r from Role r where r.roleType ='"+roleType.name()+"'")						
						.list();
						if (userRoles==null || userRoles.isEmpty())
							return new HashSet<Role>();
						return new HashSet<Role>(userRoles);
					}
					});
	}

	public User findSystemUserByName(){
		return this.systemUser;
	}

	public List<User> findUsersWithLoginIds(List<String> loginIds) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("loginIds", loginIds);
		return findUsingQuery("from User where name in (:loginIds)",params);
	}

    /**
     * This is a performance enhanced API which returns exclusively first name,last name,id of user only for display purpose
     *
     * @param roleName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<User> findUsersBelongingToRoleAndOrgForDisplay(final Organization organization, final String roleName) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = null;
                if (InstanceOfUtil.isInstanceOfClass(Dealership.class, organization)) {
                    query = session
                            .createSQLQuery
                                    ("select u.first_name as firstName,u.last_name as lastName,u.id as id from org_user u,user_roles ur,org_user_belongs_to_orgs userorg, " +
                                            "role r where u.d_active = 1 and r.d_active = 1 and u.id = ur.org_user and ur.roles = r.id and r.name = '" + roleName + "'" +
                                            "and u.id = userorg.org_user and userorg.belongs_to_organizations = '" + organization.getId() + "' order by u.last_name");
                }
                return query.addScalar("firstName").
                        addScalar("lastName").
                        addScalar("id", Hibernate.LONG).
                        setResultTransformer(Transformers.aliasToBean(User.class)).list();
            }

        });
    }

    /**
     * This is a performance enhanced API which returns exclusively first name,last name,id.login of user only for display purpose
     *
     * @param roleName
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<User> findUsersBelongingToRoleForDisplay(String roleName) {
        Set<User> userSet = new HashSet<User>();
        userSet.addAll(findUsersListBelongingToRoleForDisplay(roleName));
        return userSet;
    }

    @SuppressWarnings("unchecked")
    public List<User> findUsersListBelongingToRoleForDisplay(String roleName) {
        List<User> userList = getSession().createSQLQuery(" select u.first_name as firstName,u.last_name as lastName,u.id as id,u.login as name " +
                " from org_user u " +
                " where u.d_active = 1 and u.id in (select ur.org_user from user_roles ur,role r where r.d_active = 1 " +
                " and ur.roles = r.id and r.name = '" + roleName + "' ) order by u.last_name").
                addScalar("firstName").
                addScalar("lastName").
                addScalar("id", Hibernate.LONG).addScalar("name").
                setResultTransformer(Transformers.aliasToBean(User.class)).list();

        return userList;
    }

    @SuppressWarnings("unchecked")
    public List<User> findUsersWithFullNameLike(final String fullNamePrefix, final int pageNumber,
                                                final int pageSize) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "from User where upper(fullName) like :fullName")
                        .setParameter("fullName", fullNamePrefix.toUpperCase() + "%")
                        .setFirstResult(pageSize * pageNumber)
                        .setMaxResults(pageSize).list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<User> findUsersLikeBelongingToRoles(final String name, final List<RoleType> roleTypes) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<Long> ids = session
                        .createQuery("select distinct user.id from User user JOIN user.roles roles " +
                                " where lower(user.name) like :name and roles.roleType in (:roleTypes) ")
                        .setParameter("name", name.toLowerCase() + "%")
                        .setParameterList("roleTypes", roleTypes).list();
                return findByIds(ids);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<User> findUsersWithNameLike(final String name, final String type) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user join  user.belongsToOrganizations orgs where lower(user.name) " +
                                        "like :userName and orgs  = :oemOrg")
                        .setParameter("userName", name.toLowerCase() + "%")
                     .setParameter("oemOrg", securityHelper.getOEMOrganization()).list();
            }

            ;
        });
    }

    @SuppressWarnings("unchecked")
    public List<User> findUsersWithNameLike(final String name) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user where lower(user.name) like :userName")
                        .setParameter("userName", name.toLowerCase() + "%").list();
            }

            ;
        });
    }

    @SuppressWarnings("unchecked")
    public List<User> findUsersForDealerWithNameLike(final Long dealerId,
                                                     final String name) {
        return (List<User>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "select user from User user  join user.belongsToOrganizations as org " +
                                                " where lower(user.name) like :userName and org.id = :dealerId and user.userType = :userType")
                                .setParameter("userName", name.toLowerCase() + "%")
                                .setParameter("dealerId", dealerId)
                                .setParameter("userType", UserType.DEALER).list();
                    }
                });
    }
    
    
    @SuppressWarnings("unchecked")
    public List<User> findFleetDealerUsersWithNameLike(final Long dealerId,
                                                     final String name,final String userType) {
        return (List<User>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "select user from User user  join user.belongsToOrganizations as org " +
                                                " where lower(user.name) like :userName and org.id = :dealerId and user.userType = :userType")
                                .setParameter("userName", name.toLowerCase() + "%")
                                .setParameter("dealerId", dealerId)
                                .setParameter("userType", userType).list();
                    }
                });
    }
    
    
    @SuppressWarnings("unchecked")
    public List<User> findUsersForDealerOwnedWithNameLike(final Long orgId,
                                                     final String name) {
        return (List<User>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {                 
                    List<User> users=session.createQuery("select distinct(user) from User user join user.belongsToOrganizations org  where lower(user.name) like :userName and (org.id=:orgId or org.id in( select fc.id from FleetCustomer fc where fc.belongTo.id=:orgId) or org.id in(select cl.id from CustomerLocation cl  where cl.belongTo.id=:orgId))")
                                .setParameter("userName", name.toLowerCase() + "%")
                                .setParameter("orgId", orgId).list();
                 return users;
                    }
                });
    }

	public void saveOrUpdate(User user) {
		getHibernateTemplate().saveOrUpdate(user);
	}
    
    public Set<User> findAllInternalUsers(){
            Set<User> findAllAssignToUsers=new HashSet<User>();
            findAllAssignToUsers.addAll(findAvailableUsersBelongingToRole("operationalManager"));
            findAllAssignToUsers.addAll(findAvailableUsersBelongingToRole("fleetServiceSpecialist"));
            findAllAssignToUsers.addAll(findAvailableUsersBelongingToRole("fleetCoordinator"));
            findAllAssignToUsers.addAll(findAvailableUsersBelongingToRole("fleetProcessor"));
            findAllAssignToUsers.addAll(findAvailableUsersBelongingToRole("fleetManager"));
            
          return findAllAssignToUsers; 
            
    }
    
    public List<User> findAllAvailableFleetProcessors() {
        return findAvailableUsersBelongingToRole("fleetProcessor");
    }
    
    @SuppressWarnings("unchecked")
	public List<Dealership> findAllDealersFromDealerCode(final String dealerCode) {
		return (List<Dealership>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select dealer from Dealership dealer where dealer.dealerNumber like :dealerCode")
								.setParameter("dealerCode", "%"+dealerCode)
								.list();
					}
				});
	}
    
    @SuppressWarnings("unchecked")
	public String getLoggedInUsersCountry(final Long id){
		   String countryName = (String) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session
	                        .createQuery(
	                                "select user.address.country from User user " +
	                                " where user.id = :id ")	                                
	                        .setParameter("id", id).list().get(0);               	                        
	            }

	            ;
	        });
	        return countryName;
	}

    @DisableDeActivation
	@SuppressWarnings("unchecked")
	public List<User> findFleetCustomerUsers(final Organization organization,
		final List<Role> listOfRoles,final String partialLoginId) {
		return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
				       StringBuilder queryName = new StringBuilder("select distinct user from User user join user.belongsToOrganizations organization join user.roles role where role in (:roles)  and upper(user.name) like :login ");
				       if(organization != null){
				    	   queryName  = queryName.append("and organization.id = :id ");
				       }	   
	                   Query query = session.createQuery(queryName.toString());
	                   query.setParameterList("roles", listOfRoles).setParameter("login",partialLoginId.toUpperCase()+"%");
				       if(organization != null){
				    	   query.setParameter("id", organization.getId());
				       }	   
				       return query.list();
	            }
        });
	}
    
    @SuppressWarnings("unchecked")
    public List<User> findAvailableUsersByRoleAndPermission(final String roleName, final Long orgId, final String permissionString) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select distinct u from User u, Role r ,UserBUAvailability uba join r.permissions permission join u.belongsToOrganizations organization where uba.orgUser = u and uba.available = true "
                                        + "and uba.role = r and r in elements(u.roles) and r.name = :roleName"
                                        + " and permission.permissionString = :permissionString " + " and organization.id = :ownedById")
                        .setParameter("roleName", roleName).setParameter("permissionString", permissionString).setParameter("ownedById", orgId).list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<User> findUsersByRoleAndPermission(final String roleName, final Long orgId, final String permissionString) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select distinct u from User u, Role r join r.permissions permission join u.belongsToOrganizations organization where r in elements(u.roles) and r.name = :roleName"
                                        + " and permission.permissionString = :permissionString" + " and organization.id = :ownedById")
                        .setParameter("roleName", roleName).setParameter("permissionString", permissionString).setParameter("ownedById", orgId).list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<User> findAllUsersBelongingToServiceProvider(final String serviceProviderNo){
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select user from User user JOIN user.belongsToOrganizations belongsToOrgs where belongsToOrgs.serviceProviderNumber=:serviceProviderNo" )          
                        .setParameter("serviceProviderNo", serviceProviderNo).list();
            }

            ;
        });
        
        
      }
    
	public Set<Role> getRolesByTypeAndCategory(final RoleType roleType, final RoleCategory roleCategory) {
		return (Set<Role>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<Role> userRoles = session.createQuery(
				        "select r from Role r where r.roleType ='" + roleType.name() + "'" + " and r.roleCategory ='" + roleCategory.name() + "'").list();
				if (userRoles == null || userRoles.isEmpty())
					return new HashSet<Role>();
				return new HashSet<Role>(userRoles);
			}
		});
	}
	
    @DisableDeActivation
	@SuppressWarnings("unchecked")
	public List<User> findCustomerUsers(final List<Organization> orgs,final String partialLoginId) {
		return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
				       StringBuilder queryName = new StringBuilder("select distinct user from User user join user.belongsToOrganizations organization  where user.userType ='CUSTOMER' and upper(user.name) like :login and organization in(:orgs)");	   
	                   Query query = session.createQuery(queryName.toString());
	                   query.setParameter("login",partialLoginId.toUpperCase()+"%").setParameterList("orgs", orgs);	   
				       return query.list();
	            }
        });
	}

    @SuppressWarnings("unchecked")
    public List<User> findfleetCustomerUsersBasedOnOrganization(final Organization organization,final String partialLoginName) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                      StringBuilder queryName = new StringBuilder("select distinct user from User user join user.belongsToOrganizations org  where lower(user.name) like :login and  user.userType ='CUSTOMER' and (org.id in( select fc.id from FleetCustomer fc where fc.belongTo.id=:orgId) or org.id in(select cl.id from CustomerLocation cl  where cl.belongTo.id=:orgId))");      
                      Query query = session.createQuery(queryName.toString());
                      query.setParameter("login", partialLoginName.toLowerCase() + "%").setParameter("orgId", organization.getId());    
                      return query.list();
               }
       });
    }
    
    @SuppressWarnings("unchecked")
    public List<User> findDealerUsersBasedOnOrganizations(final List<Long> orgIds,final String eventName) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                      StringBuilder queryName = new StringBuilder("select distinct user from User user join user.belongsToOrganizations belongToOrg join user.eventState eventState where belongToOrg.id in(:orgIds) and eventState.name=:eventName");      
                      Query query = session.createQuery(queryName.toString());
                      query .setParameterList("orgIds", orgIds).setParameter("eventName", eventName);
                      return query.list();
               }
       });
    }
    
    
    public Set<Role> getFleetRolesByTypeAndCategory(final List<RoleType> roleTypes, final RoleCategory roleCategory) {
        return (Set<Role>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<Role> userRoles = session.createQuery(
                        "select r from Role r where r.roleType in (:roleTypes) and r.roleCategory ='" + roleCategory.name() + "'").setParameterList("roleTypes", roleTypes).list();
                if (userRoles == null || userRoles.isEmpty())
                    return new HashSet<Role>();
                return new HashSet<Role>(userRoles);
            }
        });
    }

    public List<User> findAllUsersSubscribedForEvent(final Set<User> actorList, final String eventName) {
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select distinct user from User user join user.eventState eventState where eventState.name=:eventState and user in(:actorList)")
                       .setParameter("eventState", eventName)
                       .setParameterList("actorList" , actorList). list();
            }
        });
    }
}