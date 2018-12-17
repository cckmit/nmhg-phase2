package tavant.twms.domain.uom;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.infra.GenericRepositoryImpl;

public class UomMappingsRepositoryImpl extends GenericRepositoryImpl<UomMappings,Long> implements
		UomMappingsRepository {
	
	
	public UomMappings findUomMappingByBaseUom(String baseUom){		
		String query = "select uom from UomMappings uom where upper(baseUom) = :baseUom";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("baseUom",ItemUOMTypes.valueOf(baseUom.toUpperCase()));
		return (UomMappings)findUniqueUsingQuery(query,params);				
	}

	public List<String> findUnMappedUoms() {
		 final String queryString = "select baseUom from UomMappings uom where mappedUom is null or mappingFraction is null";
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(queryString);
						return query.list();

					}
				});
	}

	public List<UomMappings> findMappedUoms() {
		 final String queryString = "select uom from UomMappings uom where mappedUom is not null and mappingFraction is not null";
			return (List<UomMappings>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Query query = session.createQuery(queryString);
							return query.list();

						}
					});	}

	public List<String> getListOfMappedUoms() {
		 final String queryString = "select upper(uom.baseUom) from UomMappings uom where mappedUom is not null and mappingFraction is not null";
			return (List<String>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Query query = session.createQuery(queryString);
							return query.list();

						}
					});	}	

	
}
