package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class SupplierRepositoryImpl extends GenericRepositoryImpl<Supplier, Long> implements
        SupplierRepository {

    public static final String QUERY_FOR_LOCATIONS_OF_SUPPLIER = "select location.locations from Supplier supplier " +
							" join supplier.locations location " +
							"where supplier.id = :supplierId";
    
	public static final String COUNT_NUMBER_OF_LOCATIONS = "select count(location.locations) from Supplier supplier " +
							" join supplier.locations location " +
							" where supplier.id = :supplierId";

	@SuppressWarnings("unchecked")
    public Supplier findSupplierByNumber(String supplierNumber) {
        List<Supplier> supplierList = getHibernateTemplate().find(
                "from Supplier supplier where supplier.supplierNumber like ?", supplierNumber);
        return supplierList == null || supplierList.size() == 0 ? null : supplierList.get(0);
    }
	

	@SuppressWarnings("unchecked")
    public Supplier findSupplierByNumberWithOutActiveOrInactiveStaus(String supplierNumber) {
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactive");
        List<Supplier> supplierList = getHibernateTemplate().find(
                "from Supplier supplier where supplier.supplierNumber like ?", supplierNumber);
        getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("excludeInactive");
        return supplierList == null || supplierList.size() == 0 ? null : supplierList.get(0);
    }
	
	@SuppressWarnings("unchecked")
	 public Supplier findSupplierByNumberWithOutBU(final String supplierNumber) {
		return (Supplier) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						session.disableFilter("party_bu_name");
						session.disableFilter("excludeInactive");
						List<Supplier> supplierList = session
								.createQuery(
										"from Supplier supplier where upper(supplier.supplierNumber) = :supNumber")
								.setParameter("supNumber",
										supplierNumber.toUpperCase()).list();
						session.enableFilter("party_bu_name");
						session.enableFilter("excludeInactive");
						return supplierList == null || supplierList.size() == 0 ? null
								: supplierList.get(0);
					};
				});
	  }

    @SuppressWarnings("unchecked")
    public List<Supplier> findSupplierWithNameLike(String userEntry) {
        List<Supplier> supplierList = getHibernateTemplate().find(
                "from Supplier supplier where supplier.name like ?", userEntry);
        return supplierList;
    }
    
	@SuppressWarnings("unchecked")
	public List<Supplier> findSuppliersWithNameLike(final String name,
			final int pageNumber, final int pageSize) {
		return (List<Supplier>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery("from Supplier where upper(name) like :name")
								.setParameter("name", name.toUpperCase() + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}

    @SuppressWarnings("unchecked")
    public Supplier findSupplierByName(String supplierName) {
        List<Supplier> supplierList = getHibernateTemplate().find(
                "from Supplier supplier where supplier.name like ?", supplierName);
        return supplierList == null || supplierList.size() == 0 ? null : supplierList.get(0);
    }
    
    @SuppressWarnings("unchecked")
    public Supplier findSupplierById(final Long id) {
    	List<Supplier> supplierList = getHibernateTemplate().find(
                "from Supplier supplier where supplier.id = ?", id);
        return supplierList == null || supplierList.size() == 0 ? null : supplierList.get(0);
    }
    
    @SuppressWarnings("unchecked")
    public Supplier findSupplierByIdWithOutActivateInactivate(final Long id) {
    	getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactive");
    	getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("party_bu_name");
    	List<Supplier> supplierList = getHibernateTemplate().find(
                "from Supplier supplier where supplier.id = ?", id);
    	getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("excludeInactive");
    	getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("party_bu_name");
        return supplierList == null || supplierList.size() == 0 ? null : supplierList.get(0);
    }
    
    @SuppressWarnings("unchecked")
   	public Supplier findSupplierByNameAndNumber(final String supplierName,
			final String supplierNumber) {
		return (Supplier) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						session.disableFilter("party_bu_name");
						session.disableFilter("excludeInactive");
						List<Supplier> supplierList = session
								.createQuery(
										"from Supplier supplier where upper(supplier.name) like :supName "
												+ " and upper(supplier.supplierNumber) = :supNumber")
								.setParameter("supName", supplierName.toUpperCase()+ "%")
								.setParameter("supNumber", supplierNumber.toUpperCase())
								.list();
						session.enableFilter("party_bu_name");
						session.enableFilter("excludeInactive");
						return supplierList == null || supplierList.size() == 0 ? null
								: supplierList.get(0);
					};
				});
	}
    
    public List<Supplier> findSuppliersForLabel(Label label) {
		String query="select s from Supplier s join s.labels label where label=:label";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", label);
		return findUsingQuery(query, params);
	}

	@SuppressWarnings("unchecked")
	public PageResult<Location> findLocationsForSupplier(final ListCriteria supplierCriteria, 
			final Supplier loggedInUserAsSupplier) {
		return (PageResult<Location>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// Count Query
				String countQuery = addFilterRestrictionOnQuery(supplierCriteria, Boolean.FALSE);				
				Long countOfSupplierLocations = (Long) session.createQuery(
						countQuery)
						.setLong("supplierId", loggedInUserAsSupplier.getId())
						.uniqueResult();
				// List of Supplier Locations Query
				String listQuery = addFilterRestrictionOnQuery(supplierCriteria, Boolean.TRUE);
				addSortRestrictionOnQuery(supplierCriteria, listQuery);
				List<Location> supplierLocations = (List<Location>) session.createQuery(
						listQuery)
				.setLong("supplierId", loggedInUserAsSupplier.getId())
				.setFirstResult(supplierCriteria.getPageSpecification().offSet())
				.setMaxResults(supplierCriteria.getPageSpecification().getPageSize())
				.list();
				return new PageResult<Location>(
						supplierLocations, 
								supplierCriteria.getPageSpecification(),
								supplierCriteria.getPageSpecification()
										.convertRowsToPages(countOfSupplierLocations.longValue()));
			
		}

			private void addSortRestrictionOnQuery(ListCriteria supplierCriteria, String listQuery) {
				StringBuffer listLocationsQuery = new StringBuffer(listQuery);
		        Map<String, String> sortCriteria = supplierCriteria.getSortCriteria();
		        if (sortCriteria!=null && !sortCriteria.isEmpty())
		        {
	        		listLocationsQuery.append(" order by ");
	        		int iter = 0;
		        	for (String propertyName : sortCriteria.keySet()) {
		        		String sortDirection = sortCriteria.get(propertyName);
		        		if (iter>0) listLocationsQuery.append(", ");
		        		
						if ("code".equalsIgnoreCase(propertyName))
							listLocationsQuery.append(" location.locations.code ");
						else if ("location".equalsIgnoreCase(propertyName) || 
								"supplierLocationAddress".equalsIgnoreCase(propertyName))
							listLocationsQuery.append(" location.locations.address.addressLine1 ");;
						String sortingDirection = "asc".equalsIgnoreCase(sortDirection)? " asc":" desc";
						listLocationsQuery.append(sortingDirection);
		        		iter++;
		        	}				
		        }
			}

			private String addFilterRestrictionOnQuery(ListCriteria supplierCriteria, Boolean listQuery) {
				StringBuffer hqlQuery = new StringBuffer(COUNT_NUMBER_OF_LOCATIONS);
				if (listQuery)
					hqlQuery = new StringBuffer(QUERY_FOR_LOCATIONS_OF_SUPPLIER);
				Map<String, String> filterCriteria = supplierCriteria.getFilterCriteria();
				for (String propertyName : filterCriteria.keySet()) {
					String filterData = filterCriteria.get(propertyName);
					if ("code".equalsIgnoreCase(propertyName))
						hqlQuery.append(" and lower(location.locations.code) like '" + filterData.toLowerCase() + "%'");
					else if ("location".equalsIgnoreCase(propertyName) || 
							"supplierLocationAddress".equalsIgnoreCase(propertyName))
						hqlQuery.append(" and lower(location.locations.address.addressLine1) like '" + filterData.toLowerCase() + "%'");;
						
				}
				return hqlQuery.toString();
			}});
	}

	@SuppressWarnings("unchecked")
	public List<Location> findLocationsForSupplier(final Supplier supplier) {
		return (List<Location>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return (List<Location>) session.createQuery(
						QUERY_FOR_LOCATIONS_OF_SUPPLIER)
				.setLong("supplierId", supplier.getId())
				.list();
			}});
	}
	
	public PageResult<Supplier> findAllSuppliers(ListCriteria listCriteria) {
		return super.findPage("from Supplier supplier", listCriteria);
	}

}
