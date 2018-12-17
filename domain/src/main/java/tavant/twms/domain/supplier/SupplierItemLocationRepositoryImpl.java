package tavant.twms.domain.supplier;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

public class SupplierItemLocationRepositoryImpl extends GenericRepositoryImpl<SupplierItemLocation, Long> implements
SupplierItemLocationRepository {

	public PageResult<SupplierItemLocation> findAllSupplierLocationItemsForSupplier(
			Supplier supplier, ListCriteria listCriteria) {
		
		PageSpecification ps = listCriteria.getPageSpecification();
		Map<String, Object> parameterMap = new HashMap<String, Object>(2);
		parameterMap.put("supplier", supplier);
		StringBuilder sb = new StringBuilder();
		sb.append("from SupplierItemLocation supplierItemLocation  join supplierItemLocation.itemMapping as im join im.fromItem as oemitem join im.toItem as supplieritem where ");
		String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
		if (StringUtils.isNotBlank(paramterizedFilterCriteria)) {
			sb.append(paramterizedFilterCriteria).append(" and ");
			parameterMap.putAll(listCriteria.getParameterMap());
		}
		sb.append(" supplieritem.ownedBy = :supplier");
		return findPageUsingQuery(sb.toString(), listCriteria.getSortCriteriaString(), "select supplierItemLocation ", ps, new QueryParameters(parameterMap));
		
	}

	
	public List<SupplierItemLocation> findSupplierItems(final Supplier supplier) {
		return (List<SupplierItemLocation>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
					return session.createQuery(
							" Select supplierItemLocation from SupplierItemLocation supplierItemLocation join supplierItemLocation.itemMapping as im join im.fromItem as oemitem join im.toItem as supplieritem where " +
							" supplieritem.ownedBy = :supplier"
							).setParameter("supplier",supplier).list();
				}
		});
	}


	@SuppressWarnings("unchecked")
	public Long findLocationsByMapping(final ItemMapping itemMapping,final CalendarDate fromDate,final
			CalendarDate toDate,final String locationCode) {
		return (Long) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
					return session.createQuery(" Select count(supplierItemLocation.id) from SupplierItemLocation as supplierItemLocation where  supplierItemLocation.itemMapping=:itemMapping and supplierItemLocation.fromDate=:fromDate and supplierItemLocation.toDate=:toDate ")
							.setParameter("itemMapping",itemMapping)
							.setParameter("fromDate",fromDate)
							.setParameter("toDate",toDate).uniqueResult();
				}
		});
	}
	

}
