package tavant.twms.domain.inventory;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.Country;
import tavant.twms.infra.GenericRepositoryImpl;

public class DieselTierWaiverRepositoryImpl extends
		GenericRepositoryImpl<DieselTierWaiver, Long> implements
		DieselTierWaiverRepository {

	public DieselTierWaiver findByCountryAndDieselTier(String country,String dieselTier){
		final String query = "from DieselTierWaiver dieselTierWaiver where dieselTierWaiver.destinationCountry like '" + country + "' and dieselTierWaiver.countryEmissionRating like '"+dieselTier.toUpperCase()+"'";
		return (DieselTierWaiver) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).setMaxResults(1).uniqueResult();
			}
		});
	}
	
	public DieselTierWaiver findLatestDieselTierWaiver(InventoryItem inventoryItem){
		final String query = "from DieselTierWaiver dieselTierWaiver where dieselTierWaiver.inventoryItem = " + inventoryItem.getId() + " order by dieselTierWaiver.id desc";
		return (DieselTierWaiver) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).setMaxResults(1).uniqueResult();
			}
		});
	}
}