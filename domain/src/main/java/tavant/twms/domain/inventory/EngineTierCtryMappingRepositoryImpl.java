package tavant.twms.domain.inventory;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.Country;
import tavant.twms.infra.GenericRepositoryImpl;

public class EngineTierCtryMappingRepositoryImpl extends
		GenericRepositoryImpl<EngineTierCtryMapping, Long> implements EngineTierCtryMappingRepository{
	public DieselTier findDieselTier(final String id){
		final String query = "from DieselTier tier where tier.id = " + id;
		return (DieselTier) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).uniqueResult();
			}
		});
	}
	
	public DieselTier findDieselTierByTier(final String tier){
		final String query = "from DieselTier tier where tier.tier = '" + tier+"'";
		return (DieselTier) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).uniqueResult();
			}
		});
	}
	
	public Country findCountry(final String id){
		final String query = "from Country country where country.id = " + id;
		return (Country) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).uniqueResult();
			}
		});
	}
	
	public Country findCountryByName(final String name){
		final String query = "from Country country where country.code like '" + name + "' or country.name like '"+name.toUpperCase()+"'";
		return (Country) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).uniqueResult();
			}
		});
	}
	
	public EngineTierCtryMapping findDieselTierByCountry(Country country){
		final String query = "from EngineTierCtryMapping tierCountry where tierCountry.country= " + country.getId();
		return (EngineTierCtryMapping) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).uniqueResult();
			}
		});
	}
	
	public EngineTierCtryMapping findByDieselTierAndByCountry(String dieselTierName,String countryName){
		final String query = "select tierCountry from EngineTierCtryMapping tierCountry,Country country,DieselTier dieselTier where " +
							"tierCountry.country= country.id and tierCountry.dieselTier= dieselTier.id and (country.code like '" +
							countryName +"' or country.name like '"+countryName.toUpperCase()+"') and dieselTier.tier like '"+dieselTierName+"'";
		return (EngineTierCtryMapping) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).uniqueResult();
			}
		});
	}

	public TierTierMapping findTierTierMappingByInventoryTierAndCustomerTier(String inventoryTier, EngineTierCtryMapping customerTier){
		final String query = "select tierTierMapping from TierTierMapping tierTierMapping where " +
				"tierTierMapping.inventoryTier= '" + inventoryTier + "' and tierTierMapping.customerTier = " + customerTier.getId() ;
		return (TierTierMapping) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).uniqueResult();
			}
		});
	}
}
